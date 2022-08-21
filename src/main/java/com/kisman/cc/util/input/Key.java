package com.kisman.cc.util.input;

/**
 * This class represents. This class has a {@link KeyState} to
 * determine whether or not this key is pressed or not.
 * @author Cubic
 * @since 17.08.2022
 */
public abstract class Key {

    protected final int key;

    /**
     * @param key the key {@link org.lwjgl.input.Keyboard}
     */
    public Key(int key){
        this.key = key;
    }

    /**
     * @return the key {@link org.lwjgl.input.Keyboard}
     */
    public final int key(){
        return this.key;
    }

    /**
     * @return the key state of this key {@link KeyState}
     */
    public abstract KeyState keyState();
}
