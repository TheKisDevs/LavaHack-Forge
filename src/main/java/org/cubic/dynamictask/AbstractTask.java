package org.cubic.dynamictask;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class AbstractTask<R> {

    private final Class<?>[] types;

    public AbstractTask(Class<?>... types){
        this.types = types;
    }

    protected abstract R call(ArgumentFetcher arg);

    public final R doTask(Object... args){
        ArgumentFetcher fetcher = new TaskArgumentFetcher(args, types);
        return call(fetcher);
    }

    public static <T> DelegateAbstractTask<T> types(Class<T> ret, Class<?>... cls){
        return new DelegateAbstractTask<>(cls);
    }

    public static <T> DelegateAbstractTask<T> typesResolve(Class<?>... cls){
        return new DelegateAbstractTask<>(cls);
    }

    public static <T> AbstractTask<T> directTask(Class<T> ret, T retValue){
        return new AbstractTask<T>() {
            @Override
            protected T call(ArgumentFetcher arg) {
                return retValue;
            }
        };
    }

    public static <T> AbstractTask<T> noArgsTask(Class<T> ret, Callable<T> task) {
        return new AbstractTask<T>() {
            @Override
            protected T call(ArgumentFetcher arg) {
                return task.call();
            }
        };
    }

    public static class DelegateAbstractTask<R> {

        private final Class<?>[] types;

        DelegateAbstractTask(Class<?>[] types){
            this.types = types;
        }

        public AbstractTask<R> task(IAbstractTask<R> task){
            return new AbstractTask<R>(types) {
                @Override
                protected R call(ArgumentFetcher arg) {
                    return task.call(arg);
                }
            };
        }
    }

    private interface Callable<R> {

        R call();
    }
}
