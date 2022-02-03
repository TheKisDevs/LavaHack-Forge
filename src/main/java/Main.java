import com.kisman.cc.Kisman;
import com.kisman.cc.util.protect.AntiDump;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;

import java.io.IOException;

@Mod(modid = Kisman.MODID, name = Kisman.NAME, version = Kisman.VERSION)
public class Main {
    private final Kisman k = new Kisman();

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) throws IOException, NoSuchFieldException, IllegalAccessException {
        AntiDump.check();
        k.init();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) throws IOException, NoSuchFieldException, IllegalAccessException {
        AntiDump.check();
        k.preInit();
    }
}
