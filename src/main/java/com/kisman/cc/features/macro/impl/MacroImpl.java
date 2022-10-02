package com.kisman.cc.features.macro.impl;

/**
 * @author Cubic
 * @since 02.10.2022
 */
public abstract class MacroImpl {

    protected final String arguments;

    public MacroImpl(String arguments){
        this.arguments = arguments;
    }

    public synchronized void execute(){
        synchronized(this){
            this.exec();
        }
    }

    protected abstract void exec();
}
