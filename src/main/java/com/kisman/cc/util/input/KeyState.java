package com.kisman.cc.util.input;

public enum KeyState {
    Pressed,
    Released;

    public static KeyState of(boolean isDown){
        return isDown ? Pressed : Released;
    }
}
