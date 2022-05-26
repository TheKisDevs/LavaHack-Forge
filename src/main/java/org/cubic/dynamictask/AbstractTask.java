package org.cubic.dynamictask;

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
}
