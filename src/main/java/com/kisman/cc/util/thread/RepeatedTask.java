package com.kisman.cc.util.thread;

import com.kisman.cc.Kisman;

/**
 * @author Cubic
 * @since 29.10.2022
 */
public class RepeatedTask {

    private final Thread thread;

    private final WaitMode waitMode;

    private final Mode mode;

    private final long waitMillis;

    private final long runCount;

    public RepeatedTask(Mode mode, WaitMode waitMode, long waitMillis, long runCount, Runnable task){
        this.mode = mode;
        this.waitMode = waitMode;
        this.waitMillis = waitMillis;
        this.runCount = runCount;
        this.thread = new Thread(() -> {
            if(runCount < 0){
                for(;;){
                    if(waitMode == WaitMode.Before || waitMode == WaitMode.Both)
                        sleep(waitMillis);
                    if(mode == Mode.Blocking)
                        task.run();
                    else
                        new Thread(task).start();
                    if(waitMode == WaitMode.After || waitMode == WaitMode.Both)
                        sleep(waitMillis);
                }
            } else {
                for(long i = 0; i < runCount; i++){
                    if(waitMode == WaitMode.Before || waitMode == WaitMode.Both)
                        sleep(waitMillis);
                    if(mode == Mode.Blocking)
                        task.run();
                    else
                        new Thread(task).start();
                    if(waitMode == WaitMode.After || waitMode == WaitMode.Both)
                        sleep(waitMillis);
                }
            }
        });
    }

    public Thread getThread() {
        return thread;
    }

    public WaitMode getWaitMode() {
        return waitMode;
    }

    public Mode getMode() {
        return mode;
    }

    public long getWaitMillis() {
        return waitMillis;
    }

    public long getRunCount() {
        return runCount;
    }

    public enum WaitMode {
        Before,
        After,
        Both
    }

    public enum Mode {
        Blocking,
        Concurrent
    }

    private static void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e){
            Kisman.LOGGER.error("[RepeatedTask]: Thread interrupted!", e);
        }
    }
}
