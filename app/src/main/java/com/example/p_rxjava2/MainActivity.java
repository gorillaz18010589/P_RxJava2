package com.example.p_rxjava2;
//1.加入APi

/*
被觀察者（Observable/Flowable） 產生事件（Flowable支持背壓）
觀察者（Observer/Subscriber） 響應事件並做出處理
事件（event） 被觀察者和觀察者的消息載體
訂閱（subscribe） 關聯被觀察者和觀察者
訂閱控制（Disposable/Subscription） 用於取消被觀察者與觀察者的關係，Subscription支持背壓拉取消息*/

/*
onSubscribe 觀察者訂閱被觀察者時，觸發該事件，同時返回一個訂閱控制對象（Disposable/Subscription）
onNext 被觀察者通過onNext可以發送多個事件，觀察者可以通過onNext接收多個事件
onError 被觀察者發送onError事件後，其他事件被終止發送，觀察者收到onError事件後會終止接受其他事件
onComplete 被觀察者發送onComplete事件後，其他事件被終止發送，觀察者收到onComplete事件後會終止接受其他事件*/

/*2.1 创建操作符

名称	功能介绍
create	public static Observable create(ObservableOnSubscribe source)
创建一个被观察者，同时定义并发送事件，手动维护事件的发送和结束
just	public static Observable just(T item1, … T item10)
创建一个被观察者，并发送事件，发送的事件不可以超过10个
fromArray	public static Observable fromArray(T… items)
创建一个被观察者，并发送事件，参数接收一个事件数组
fromIterable	public static Observable fromIterable(Iterable source)
创建一个被观察者，并发送事件，参数接收一个事件集合，如List
fromCallable	public static Observable fromCallable(Callable supplier)
参数Callable 是 java.util.concurrent 中的 Callable，Callable 和 Runnable 的用法基本一致，只是它会返回一个结果值，这个结果值就是发给观察者的
fromFuture	public static Observable fromFuture(Future future)
参数中的 Future 是 java.util.concurrent 中的 Future，Future 的作用是增加了 cancel() 等方法操作 Callable，可以通过 get() 方法来获取 Callable 返回的值
defer	public static Observable defer(Callable> supplier)
只有观察者订阅的时候才会创建新的被观察者，所以每订阅一次就会通知一次观察者
timer	public static Observable timer(long delay, TimeUnit unit)
延时发送，当到指定时间后就会发送一个 0L 的值给观察者
interval	public static Observable interval(long period, TimeUnit unit)
public static Observable interval(long period, TimeUnit unit, Scheduler scheduler)
定时发送，每隔一段时间就会发送一个事件，这个事件从0开始递增
intervalRange	public static Observable intervalRange(long start, long count, long initialDelay, long period, TimeUnit unit)
可以指定发送事件的开始值和发送数量，其他与 interval() 的功能一样
range/rangeLong	public static Observable range(final int start, final int count)
public static Observable rangeLong(long start, long count)
发送一定范围的事件序列*/

/*
* 2.2 线程相关操作符

名称	功能介绍
subscribeOn	public final Single subscribeOn(final Scheduler scheduler)
指定被观察者事件发送的线程，如果多次调用此方法，只有第一次有效
observeOn	public final Observable observeOn(Scheduler scheduler)
指定观察者处理事件的线程，每指定一次就会生效一次
注：Scheduler类型

类型	使用方式	含义	使用场景
IoScheduler	Schedulers.io()	io操作线程	读写SD卡文件、查询数据库、访问网络等IO密集型等操作
NewThreadScheduler	Schedulers.newThread()	创建新线程	耗时操作等
SingleScheduler	Schedulers.single()	单例线程	只需一个单例线程时
ComputationScheduler	Schedulers.computation()	CPU计算操作线程	图片压缩取样、xml、json解析等CPU密集型计算
TrampolineScheduler	Schedulers.trampoline()	当前线程	需要在当前线程立即执行任务时
HandlerScheduler	AndroidSchedulers.mainThread()	Android主线程	更新U等I
ExecutorScheduler	Schedulers.from(Executor executor)	自定义线程	自定义任务等
2.3 事件监听操作符

名称	功能介绍
doOnNext	public final Observable doOnNext(Consumer onNext)
每次发送onNext之前回调
doOnEach	public final Observable doOnEach(final Observer observer)
每次发送事件之前回调
doAfterNext	public final Observable doAfterNext(Consumer onAfterNext)
每次发送onNext事件之后回调
doOnError	public final Observable doOnError(Consumer onError)
发送 onError() 之前回调
doOnComplete	public final Observable doOnComplete(Action onComplete)
发送 onComplete() 之前回调
doOnSubscribe	public final Observable doOnSubscribe(Consumer onSubscribe)
发送 onSubscribe() 之前回调
doOnDispose	public final Observable doOnDispose(Action onDispose)
调用 Disposable 的 dispose() 之后回调
doOnTerminate
doAfterTerminate
doOnTerminate 是在 onError 或者 onComplete 发送之前回调，而 doAfterTerminate 则是 onError 或者 onComplete 发送之后回调（取消订阅，方法失效）
doFinally	public final Observable doFinally(Action onFinally)
无论是否取消订阅，在所有事件发送完毕之后回调该
2.4 过滤操作符

名称	功能介绍
filter	public final Flowable filter(Predicate predicate)
通过一定的逻辑来过滤被观察者发送的事件，返回 true 则发送事件，否则不发送
ofType	public final Observable ofType(final Class clazz)
事件类型过滤
distinct	public final Observable distinct()
去重操作符，去掉重复的事件
distinctUntilChanged	public final Observable distinctUntilChanged()
过滤掉连续重复的事件
skip	public final Observable skip(long count)
跳过世界集合中的某些事件，count 代表跳过事件的数量
debounce	public final Observable debounce(long timeout, TimeUnit unit)
如果两件事件发送的时间间隔小于设定的时间间隔timeout，则前一件事件就不会发送
take	public final Observable take(long count)
取指定数量的事件
firstElement / lastElement	public final Maybe firstElement()
firstElement() 获取事件序列的第1个元素，lastElement() 获取事件序列的最后1个元素
elementAt / elementAtOrError	public final Maybe elementAt(long index)
elementAt()可以从事件序列中取值指定index的事件，如果不存在，则无响应。
如果想要在获取不到事件的时候发出响应使用elementAtOrError()
2.5 其他操作符

名称	功能介绍
map	public final Observable map(Function mapper)
遍历被观察者发送的事件，可以对事件进行二次处理
flatMap	public static Observable fromIterable(Iterable source)
可以将事件序列中的元素进行整合加工，返回一个新的被观察者，flatMap 并不能保证事件的顺序
concatMap	public final Observable concatMap(Function> mapper)
功能与flatMap，但是concatMap可以保证事件的顺序
buffer	public final Observable> buffer(int count, int skip)
从需要发送的事件中获取一定数量的事件，并将这些事件放到缓冲区中一次性发出
groupBy	public final Observable> groupBy(Function keySelector)
将发送的数据进行分组，每个分组都会返回一个被观察者
scan	public final Observable scan(BiFunction accumulator)
将时间以一定的逻辑聚合起来
reduce	public final Maybe reduce(BiFunction reducer)
操作符的作用也是将发送数据以一定逻辑聚合起来， 区别在于 scan() 每处理一次数据就会将事件发送给观察者，而 reduce() 会将所有数据聚合在一起才会发送事件给观察者
window	public final Observable> window(long count)
将事件按照count指定的数量分组，一次性发送一组事件
concat/concatArray	public static Observable concat(ObservableSource source1, …, ObservableSource N4)
public static Observable concatArray(ObservableSource… sources)
可以将多个观察者组合在一起，然后按照之前发送顺序发送事件。功能与concat相同，只是concatArray参数接收数组。
需要注意的是，concat() 最多只可以发送4个事件。如果其中有一个被观察者发送了一个 onError 事件，那么就会停止发送事件
merge/mergeArray	public static Observable merge(Iterable> sources)
…
功能与concat相同，只是merge可以并发，不是按照被观察者的顺序发送事件
concatArrayDelayError
mergeArrayDelayError	public static Observable concatArrayDelayError(ObservableSource… sources)
如果事件发送过成中出现了onError事件，该方法可以延迟到所有被观察者都发送完事件后再执行onError
zip	public static Observable zip(Iterable> sources, Function zipper)
会将多个被观察者合并，根据各个被观察者发送事件的顺序一个个结合起来，最终发送的事件数量会与源 Observable 中最少事件的数量一样
collect	public final Single collect(Callable initialValueSupplier, BiConsumer collector)
将要发送的事件收集到定义的数据结构中
startWith/startWithArray	public final Observable startWith(T item)
public final Observable startWithArray(T… items)
在发送事件之前追加事件，startWith() 追加一个事件，startWithArray() 可以追加多个事件。追加的事件会先发出
count	public final Single count()
返回被观察者发送事件的数量
delay	public final Observable delay(long delay, TimeUnit unit)
延时发送事件*/

//<T> Observable<T> create(ObservableOnSubscribe<T> source):創建Observable
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.p_rxjava2.databinding.ActivityMainBinding;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding activityMainBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(MainActivity.this,R.layout.activity_main);
        init();
    }

    private void init() {
        activityMainBinding.btnObservableCrate.setOnClickListener(onClickListener);
        activityMainBinding.btnObservableJust.setOnClickListener(onClickListener);
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnObservableCrate:

                    break;
                case R.id.btnObservableJust:
                    observableJustToObserver();
                    break;
            }
        }
    };

    //簡寫可以傳最多10個物件進去
    private void observableJustToObserver() {
        Observable.just("事件1","事件2","事件3","事件4","事件5","事件6","事件7","事件8","事件9","事件10").subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.v("hank","Consumer -> accept:" + s);
                activityMainBinding.tv1.setText(s);
                activityMainBinding.tv2.setText(s);
                activityMainBinding.tv3.setText(s);
                activityMainBinding.tv4.setText(s);
                activityMainBinding.tv5.setText(s);
                activityMainBinding.tv6.setText(s);
                activityMainBinding.tv7.setText(s);
                activityMainBinding.tv8.setText(s);
                activityMainBinding.tv9.setText(s);
                activityMainBinding.tv10.setText(s);
            }
        });
    }


    //Observable(被觀察者)用Crate方式Observer(觀察者)
    private void observableCrateToObserver() {
        //Observable<T>:被觀察者<要被觀察的資料>
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {//create:创建一个被观察者，同时定义并发送事件，手动维护事件的发送和结束
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                //emitter發射器
                emitter.onNext("第一則事件"); //被觀察者通過onNext可以發送多個事件，觀察者可以通過onNext接收多個事件
                emitter.onNext("第二則事件");
                emitter.onComplete();
                Log.v("hank","Observable ->");
            }
        });

        //Observer<T>->觀察者
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                //訂閱成功會呼叫,Disposable:回傳控制對象
                Log.v("hank","onSubscribe()->Disposable:" + d);
            }

            @Override
            public void onNext(String s) {
                //接收被觀察者onNext的事件(回傳被觀察的物件)
                Log.v("hank","onSubscribe()->Disposable:" + s);
            }

            @Override
            public void onError(Throwable e) {
                //接收被觀察者onError的事件
                Log.v("hank","onSubscribe()->onError:" + e);
            }

            @Override
            public void onComplete() {
                //接收被觀察者onComplete的事件
                Log.v("hank","onSubscribe()->onComplete:");
            }
        };

        observable.subscribe(observer);//訂閱這個觀察者
    }
}