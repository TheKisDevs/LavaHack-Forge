package com.kisman.cc.util.collections;

import java.util.Objects;

public class MutPair<T> {

    private T first;

    private T second;

    public MutPair(T first, T second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public T getSecond() {
        return second;
    }

    public void setSecond(T second) {
        this.second = second;
    }

    @Override
    public boolean equals(Object other){
        if(!(other instanceof MutPair))
            return false;
        MutPair<?> pair = (MutPair<?>) other;
        return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
    }
}
