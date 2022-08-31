package com.kisman.cc.util.thread;

import java.util.concurrent.Callable;

class CallableThread<T> {

    final Thread thread;

    volatile T result;

    volatile boolean finished;

    CallableThread(Callable<T> callable){
        this.result = null;
        this.finished = false;
        thread = new Thread(() -> {
            try {
                this.result = callable.call();
                this.finished = true;
            } catch (Exception e){
                e.printStackTrace();
                this.finished = true;
            }
        });
    }
}
