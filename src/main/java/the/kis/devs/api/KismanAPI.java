package the.kis.devs.api;

import com.kisman.cc.Kisman;
import me.zero.alpine.bus.EventBus;

/**
 * @author _kisman_
 * @since 17:04 of 08.06.2022
 */
public class KismanAPI {
    public static EventBus getEventBus() {
        return Kisman.EVENT_BUS;
    }
}
