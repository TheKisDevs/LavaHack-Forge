package the.kis.devs.api.event;

import com.kisman.cc.event.Event;

/**
 * @author _kisman_
 * @since 16:13 of 18.12.2022
 */
public class EventAPI extends Event {
    public EventAPI(Object... values) {
        mirrorEvent = null;
    }

    public EventAPI(Era era, Object... values) {
        this(values);
        super.setEra(era);
    }
}
