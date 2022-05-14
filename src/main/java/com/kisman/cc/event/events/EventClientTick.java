package com.kisman.cc.event.events;

import com.kisman.cc.event.Event;

public class EventClientTick extends Event {

    private EventClientTick(Era era){
        super(era);
    }

    public static class Pre extends EventClientTick {

        public Pre(){
            super(Era.PRE);
        }

    }

    public static class Post extends EventClientTick {

        public Post(){
            super(Era.POST);
        }

    }
}
