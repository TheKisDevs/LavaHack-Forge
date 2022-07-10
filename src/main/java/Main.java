import com.kisman.cc.Kisman;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;

import java.io.IOException;

@Mod(modid = Kisman.MODID, name = Kisman.NAME, version = Kisman.VERSION)
public class Main {
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) throws IOException, NoSuchFieldException, IllegalAccessException {
        Kisman.instance.init();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) throws IOException, NoSuchFieldException, IllegalAccessException {
        Kisman.instance.preInit();
    }
}
