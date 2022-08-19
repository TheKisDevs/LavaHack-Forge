package com.kisman.cc.util.thread;

public class PromiseResult<T> {

    private final T result;

    private final boolean error;

    PromiseResult(T result, boolean error){
        this.result = result;
        this.error = error;
    }

    public T result(){
        if(error)
            throw new IllegalStateException();
        return result;
    }

    public T expect(T expect){
        if(error)
            return expect;
        return result;
    }

    public T expectNull(){
        if(error)
            return null;
        return result;
    }
}
