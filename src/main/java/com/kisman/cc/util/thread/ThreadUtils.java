package com.kisman.cc.util.thread;

public class ThreadUtils {

    public static void parallel(Runnable runnable, long runcount){
        for(int i = 0; i < runcount; i++)
            async(runnable);
    }

    public static Thread async(Runnable runnable, long runcount){
        Thread thread = new Thread(() -> {
            for(int i = 0; i < runcount; i++)
                runnable.run();
        });
        thread.start();
        return thread;
    }

    public static Thread async(Runnable runnable){
        Thread thread = new Thread(runnable);
        thread.start();
        return thread;
    }

    public static void sleep(long millis) throws InterruptedException {
        sleepNanos(millis * 1000000L);
    }

    public static void sleepNanos(long nanos) throws InterruptedException {
        final long end = System.nanoTime() + nanos;
        long timeLeft = nanos;
        do {
            if(timeLeft > 2000000)
                Thread.yield();
            timeLeft = end - System.nanoTime();
            if(Thread.interrupted())
                throw new InterruptedException();
        } while (timeLeft > 0);
    }
}
