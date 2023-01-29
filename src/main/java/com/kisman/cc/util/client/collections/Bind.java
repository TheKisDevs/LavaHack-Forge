package com.kisman.cc.util.client.collections;

import java.util.Objects;

public class Bind<A, B> {

    private final A first;

    private final B second;

    public Bind(A first, B second){
        this.first = first;
        this.second = second;
    }

    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object other){
        if(!(other instanceof Bind))
            return false;
        Bind<?, ?> bind = (Bind<?, ?>) other;
        return Objects.equals(first, bind.first) && Objects.equals(second, bind.second);
    }
}
