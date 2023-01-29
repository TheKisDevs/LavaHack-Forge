package com.kisman.cc.util.client.collections;

import java.util.Objects;

public class MutBind<A, B> {

    private A first;

    private B second;

    public MutBind(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public A getFirst() {
        return first;
    }

    public void setFirst(A first) {
        this.first = first;
    }

    public B getSecond() {
        return second;
    }

    public void setSecond(B second) {
        this.second = second;
    }

    @Override
    public boolean equals(Object other){
        if(!(other instanceof MutBind))
            return false;
        MutBind<?, ?> bind = (MutBind<?, ?>) other;
        return Objects.equals(first, bind.first) && Objects.equals(second, bind.second);
    }
}
