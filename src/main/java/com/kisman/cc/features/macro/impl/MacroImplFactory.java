package com.kisman.cc.features.macro.impl;

public interface MacroImplFactory<T extends MacroImpl> {

    T construct(String arguments);
}
