package com.kisman.cc.util.input;

import java.util.Arrays;

public class Keyboard {

    public static Key key(int key){
        return new KeyImpl(key);
    }

    public static KeyState keyState(int key){
        return new KeyImpl(key).keyState();
    }

    public static KeyCombo keyCombo(int... keys){
        return new KeyComboImpl(keys);
    }

    public static KeyCombo keyCombo(Key... keys){
        return new KeyComboImpl(mapKeys(keys));
    }

    public static boolean keyDown(int key){
        return isKeyDown(key);
    }

    private static class KeyComboImpl extends KeyCombo {

        private KeyState[] states;

        public KeyComboImpl(int... keys){
            super(keys);
        }

        @Override
        public KeyState[] keyState() {
            pollStates();
            return Arrays.copyOf(states, states.length);
        }

        @Override
        public KeyState keyState(int idx) {
            pollStates();
            return states[idx];
        }

        @Override
        public KeyState anyKey() {
            pollStates();
            for(KeyState state : states)
                if(state == KeyState.Pressed)
                    return KeyState.Pressed;
            return KeyState.Released;
        }

        @Override
        public KeyState allKeys() {
            pollStates();
            for(KeyState state : states)
                if(state == KeyState.Released)
                    return KeyState.Released;
            return KeyState.Pressed;
        }

        private void pollStates(){
            KeyState[] states = new KeyState[keys.length];
            for(int i = 0; i < keys.length; i++)
                states[i] = KeyState.of(isKeyDown(keys[i]));
            this.states = states;
        }
    }

    private static final class KeyImpl extends Key {

        public KeyImpl(int key){
            super(key);
        }

        @Override
        public KeyState keyState() {
            return KeyState.of(isKeyDown(key));
        }
    }

    private static boolean isKeyDown(int key){
        return org.lwjgl.input.Keyboard.isKeyDown(key);
    }

    private static int[] mapKeys(Key... keys){
        int[] actualKeys = new int[keys.length];
        for(int i = 0; i < keys.length; i++)
            actualKeys[i] = keys[i].key();
        return actualKeys;
    }
}
