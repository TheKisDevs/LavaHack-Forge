package com.kisman.cc.util.hwid;


public class NoStackTraceThrowable extends RuntimeException {

    public NoStackTraceThrowable(final String msg) {
        super(msg);
        this.setStackTrace(new StackTraceElement[0]);
    }

    @Override
    public String toString() {
        return "NoHWIDException!";
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
