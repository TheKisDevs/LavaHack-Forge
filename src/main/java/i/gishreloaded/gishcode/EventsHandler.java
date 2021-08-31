package i.gishreloaded.gishcode;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Module;
import i.gishreloaded.gishcode.utils.system.Connection;
import i.gishreloaded.gishcode.wrappers.Wrapper;

public class EventsHandler {
    public boolean onPacket(Object packet, Connection.Side side) {
        boolean suc = true;
        for (Module mod : Kisman.instance.moduleManager.modules) {
            if (!mod.isToggled() || Wrapper.INSTANCE.world() == null) {
                continue;
            }
            suc &= mod.packet(packet, side);
        }
        return suc;
    }
}
