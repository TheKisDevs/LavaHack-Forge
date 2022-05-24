package com.kisman.cc.util.thread;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

@Deprecated
public final class QueuedThreadExecutor {

    private final Queue<Func<?>> executionQueue;

    private final Queue<Runnable> pendingExecutions;

    private ExecutorService executor;

    public QueuedThreadExecutor(){
        this.executionQueue = new LinkedList<>();
        this.pendingExecutions = new LinkedList<>();
        this.executor = new ScheduledThreadPoolExecutor(1);
    }

    public void submit(Func<?> func){
        executionQueue.offer(func);
    }

    public void runNext(Object... args){
        if(executionQueue.isEmpty()) return;
        Func<?> func = executionQueue.poll();
        pendingExecutions.offer(() -> func.call(args));
        if(!executor.isShutdown() || !executor.isTerminated()) return;
        executor.submit(() -> {
            while(!pendingExecutions.isEmpty()){
                pendingExecutions.poll().run();
            }
        });
    }
}
