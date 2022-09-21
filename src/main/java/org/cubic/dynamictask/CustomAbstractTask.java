package org.cubic.dynamictask;

public abstract class CustomAbstractTask<R, A extends ArgumentFetcher> {

    private final Class<?>[] types;

    private ArgumentFetcherFactory<A> argumentFetcherFactory;

    public CustomAbstractTask(Class<?>... types){
        this.types = types;
    }

    public ArgumentFetcherFactory<A> getArgumentFetcher() {
        return argumentFetcherFactory;
    }

    public void setArgumentFetcher(ArgumentFetcherFactory<A> argumentFetcher) {
        this.argumentFetcherFactory = argumentFetcher;
    }

    protected abstract R call(A arg);

    public final R doTask(Object... args){
        A fetcher = argumentFetcherFactory.newArgumentFetcher(args, types);
        return call(fetcher);
    }

    public static <T, A extends ArgumentFetcher> CustomDelegateAbstractTask<T, A> types(Class<T> ret, Class<?>... cls){
        return new CustomDelegateAbstractTask<>(cls);
    }

    public static <T, A extends ArgumentFetcher> CustomDelegateAbstractTask<T, A> typesResolve(Class<?>... cls){
        return new CustomDelegateAbstractTask<>(cls);
    }

    public static <T, A extends ArgumentFetcher> CustomAbstractTask<T, A> directTask(Class<T> ret, T retValue){
        return new CustomAbstractTask<T, A>() {
            @Override
            protected T call(ArgumentFetcher arg) {
                return retValue;
            }
        };
    }

    public static <T, A extends ArgumentFetcher> CustomAbstractTask<T, A> noArgsTask(Class<T> ret, Callable<T> task) {
        return new CustomAbstractTask<T, A>() {
            @Override
            protected T call(ArgumentFetcher arg) {
                return task.call();
            }
        };
    }

    public static class CustomDelegateAbstractTask<R, A extends ArgumentFetcher> {

        private final Class<?>[] types;

        CustomDelegateAbstractTask(Class<?>[] types){
            this.types = types;
        }

        public CustomAbstractTask<R, A> task(ICustomAbstractTask<R, A> task){
            return new CustomAbstractTask<R, A>(types) {
                @Override
                protected R call(A arg) {
                    return task.call(arg);
                }
            };
        }
    }

    private interface Callable<R> {

        R call();
    }
}
