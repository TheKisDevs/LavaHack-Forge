package com.kisman.cc.util.collections;

import java.util.Objects;

public class Pair<T> {

    private final T first;

    private final T second;

    public Pair(T first, T second){
        this.first = first;
        this.second = second;
    }

    public T getFirst(){
        return this.first;
    }

    public T getSecond(){
        return this.second;
    }

    @Override
    public boolean equals(Object other){
        if(!(other instanceof Pair))
            return false;
        Pair<?> pair = (Pair<?>) other;
        return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
    }
}
