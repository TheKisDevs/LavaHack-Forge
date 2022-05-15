package com.kisman.cc.api.util.exception;

public class URLReaderException extends RuntimeException {
    public URLReaderException(final String msg) {
        super(msg);
        this.setStackTrace(new StackTraceElement[0]);
    }

    @Override public String toString() {return "URLReaderException error! Please create ticket in TheDiscord about the crash!";}
    @Override public synchronized Throwable fillInStackTrace() {return this;}
}
