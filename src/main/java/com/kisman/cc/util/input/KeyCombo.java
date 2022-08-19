package com.kisman.cc.util.input;

/**
 * This class represents a combination of keys.
 * This is especially useful for actions that require
 * multiple keys to be pressed
 * @author Cubic
 * @since 17.08.2022
 */
abstract class KeyCombo {

    protected final int[] keys;

    /**
     * The keys
     * @param keys the keys {@link org.lwjgl.input.Keyboard}
     */
    public KeyCombo(int... keys) {
        this.keys = keys;
    }

    /**
     * @return the keys
     */
    public final int[] keys(){
        return this.keys;
    }

    /**
     * @return the keyState of the {@code keys} in order
     */
    public abstract KeyState[] keyState();

    /**
     * @param idx the index to get the key state of
     * @return the key state of the key at {@param idx}
     */
    public abstract KeyState keyState(int idx);

    /**
     * @return the 'pressed' key state if any
     *         key is pressed. Else it returns
     *         the 'released' key state.
     */
    public abstract KeyState anyKey();

    /**
     * @return the 'pressed' key state if all
     *         all keys are pressed. Else it
     *         return 'released' key state.
     */
    public abstract KeyState allKeys();
}
