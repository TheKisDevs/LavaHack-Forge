package com.kisman.cc.util.thread;

import java.util.concurrent.Callable;

public class Promise<T> implements Runnable {

    private final transient CallableThread<T> thread;

    private transient boolean running;

    private transient boolean paused;

    private Promise(CallableThread<T> thread) {
        this.thread = thread;
    }

    public void run(){
        if(running)
            throw new IllegalStateException("Already running");
        this.running = true;
        thread.thread.start();
    }

    public Promise<T> start(){
        if(running)
            throw new IllegalStateException("Already running");
        this.running = true;
        thread.thread.start();
        return this;
    }

    public PromiseResult<T> stop(){
        if(!running || paused)
            return new PromiseResult<>(null, true);
        if(!thread.finished){
            thread.thread.stop();
            this.running = false;
            return new PromiseResult<>(null, true);
        }
        this.running = false;
        return new PromiseResult<>(thread.result, false);
    }

    public void pause(){
        if(paused)
            throw new IllegalStateException("Already paused");
        this.paused = true;
        thread.thread.suspend();
    }

    public void resume(){
        if(!paused)
            throw new IllegalStateException("Not paused");
        this.paused = false;
        thread.thread.resume();
    }

    public T join(){
        if(!running || paused)
            throw new IllegalStateException("No running");
        tryDo(thread.thread::join);
        this.running = false;
        return thread.result;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public T await(){
        if(!running || paused)
            throw new IllegalStateException("No running");
        while(!thread.finished);
        this.running = false;
        return thread.result;
    }

    public boolean isFinished(){
        return thread.finished;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isPaused() {
        return paused;
    }

    public static <T> Promise<T> task(Callable<T> callable){
        return new Promise<>(new CallableThread<>(callable));
    }



    private static <T> T tryCall(Callable<T> callable){
        try {
            callable.call();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static void tryDo(Executable executable){
        try {
            executable.exec();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private interface Executable {
        void exec() throws Exception;
    }
}
