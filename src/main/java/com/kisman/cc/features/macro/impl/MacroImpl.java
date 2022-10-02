package com.kisman.cc.features.macro.impl;

/**
 * @author Cubic
 * @since 02.10.2022
 */
public abstract class MacroImpl {

    protected final String name;

    protected final String arguments;

    public MacroImpl(String name, String arguments){
        this.name = name;
        this.arguments = arguments;
    }

    public String getName() {
        return name;
    }

    public String getArguments() {
        return arguments;
    }

    public synchronized void execute(){
        synchronized(this){
            this.exec();
        }
    }

    protected abstract void exec();
}
