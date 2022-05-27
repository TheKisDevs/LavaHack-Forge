package org.cubic.dynamictask;

class TaskArgumentFetcher implements ArgumentFetcher {

    private final Object[] args;

    private final Class<?>[] types;

    public TaskArgumentFetcher(Object[] args, Class<?>[] types){
        this.args = args;
        this.types = types;
    }

    @Override
    public <T> T fetch(int i) {
        Class<?> type = types[i];
        Object o = args[i];
        Class<?> oClass = o.getClass();
        if(type != oClass)
            throw new IllegalArgumentException("Argument types don't match");
        return (T) type.cast(o);
    }
}
