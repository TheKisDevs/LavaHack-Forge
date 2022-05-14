package com.kisman.cc.util.thread;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.BooleanSupplier;

public class TaskQueue implements Runnable {

    private final Queue<Runnable> queue;

    private BooleanSupplier done;

    public TaskQueue(){
        queue = new LinkedList<>();
        done = () -> false;
    }

    public void add(Runnable runnable){
        queue.offer(runnable);
    }

    public void add(Func<?> func, Object... args){
        queue.offer(() -> func.call(args));
    }

    public void runNext(){
        queue.poll();
        Runnable runnable = queue.peek();
        if(runnable == null) return;
        runnable.run();
    }

    public void runCur(){
        Runnable runnable = queue.peek();
        if(runnable == null) return;
        runnable.run();
    }

    public void runNextIfDone(){
        if(!done.getAsBoolean()) return;
        runNext();
    }

    @Override
    public void run(){
        if(done.getAsBoolean()){
            runNext();
            return;
        }
        runCur();
    }

    public void runRemaining(){
        while(hasMoreTasks()){
            runNext();
        }
    }

    public void runRemainingIfDone(){
        if(!done.getAsBoolean()) return;
        runRemaining();
    }

    public boolean hasMoreTasks(){
        return !queue.isEmpty();
    }

    public int remainingTasks(){
        return queue.size();
    }

    public void setDoneCheck(BooleanSupplier supplier){
        done = supplier;
    }

    public Runnable[] getRemainingTasks(){
        return queue.toArray(new Runnable[0]);
    }
}
