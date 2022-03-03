package com.kisman.cc.event;

import me.zero.alpine.event.type.Cancellable;

public class Event extends Cancellable {
    private Era era;
    public Event() {}
    public Event(Era era) {this.era = era;}
    public Era getEra() {return era;}
    public void setEra(Era era) {this.era = era;}

    public enum Era {
        PRE,
        POST,
        PERI
    }
}
