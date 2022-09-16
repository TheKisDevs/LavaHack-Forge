package com.kisman.cc.event.events;

import com.kisman.cc.event.Event;

public class EventSendClickBlockToController extends Event {

    private final boolean leftClick;

    public EventSendClickBlockToController(Era era, boolean leftClick){
        super(era);
        this.leftClick = leftClick;
    }

    public boolean isLeftClick() {
        return leftClick;
    }
}
