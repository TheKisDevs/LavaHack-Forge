package com.kisman.cc.event.events;

import com.kisman.cc.event.Event;

public class MouseEvent extends Event {
    private final int button;
    private final boolean state;

    public MouseEvent(int button, boolean state) {
        this.button = button;
        this.state = state;
    }

    public boolean getState() {
        return state;
    }

    public int getButton() {
        return button;
    }

}
