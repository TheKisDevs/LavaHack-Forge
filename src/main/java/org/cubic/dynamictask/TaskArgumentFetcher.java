package org.cubic.dynamictask;

public class TaskArgumentFetcher implements ArgumentFetcher {

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
        if(!type.isAssignableFrom(oClass))
            throw new IllegalArgumentException("Argument types don't match: " + type.getName() + " " + oClass.getName());
        return (T) type.cast(o);
    }
}
