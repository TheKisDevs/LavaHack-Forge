package org.cubic.dynamictask;

public abstract class AbstractEnum<C extends AbstractEnum<C, T>, T extends AbstractTask<?>> {

    private final String name;

    private final T task;

    public AbstractEnum(String name, T task){
        this.name = name;
        this.task = task;
    }

    public final String getName(){
        return name;
    }

    public final T getTask(){
        return task;
    }

    public abstract AbstractEnum<C, T>[] values();

    public AbstractEnum<C, T> valueOf(String name){
        for(AbstractEnum<C, T> abstractEnum : values()){
            if(abstractEnum.name.equals(name))
                return abstractEnum;
        }
        return null;
    }
}
