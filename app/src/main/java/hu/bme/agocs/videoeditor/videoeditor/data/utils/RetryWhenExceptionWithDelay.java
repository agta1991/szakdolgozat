package hu.bme.agocs.videoeditor.videoeditor.data.utils;

import com.github.hiteshsondhi88.libffmpeg.exceptions.CommandAlreadyRunningException;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by agocs on 2015.12.08..
 */
public class RetryWhenExceptionWithDelay implements
        Func1<Observable<? extends Throwable>, Observable<?>> {

    private final int maxRetries;
    private final int retryDelayMillis;
    private int retryCount;
    private final TimeUnit unit;
    private final Class<? extends Exception> exceptionType;

    public RetryWhenExceptionWithDelay(final int maxRetries, final int retryDelayMillis, TimeUnit unit, Class<? extends Exception> exceptionType) {
        this.maxRetries = maxRetries;
        this.retryDelayMillis = retryDelayMillis;
        this.retryCount = 0;
        this.unit = unit;
        this.exceptionType = exceptionType;
    }

    @Override
    public Observable<?> call(Observable<? extends Throwable> attempts) {
        return attempts
                .flatMap(new Func1<Throwable, Observable<?>>() {
                    @Override
                    public Observable<?> call(Throwable throwable) {
                        if (++retryCount < maxRetries && exceptionType.isInstance(throwable)) {
                            // When this Observable calls onNext, the original
                            // Observable will be retried (i.e. re-subscribed).
                            return Observable.timer(retryDelayMillis,
                                    unit);
                        }

                        // Max retries hit. Just pass the error along.
                        return Observable.error(throwable);
                    }
                });
    }
}