package utils;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

public class RxUtils {

    private RxUtils() { }

    public static <T> Observable.Transformer<T, T> retrofitTransformer() {
        return observable -> observable
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnError(throwable -> System.err.println(throwable.toString()));
    }

    public static void unsubscribe(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}
