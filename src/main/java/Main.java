import com.kisman.cc.Kisman;
import com.kisman.cc.file.SaveConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

@Mod(modid = Kisman.MODID, name = Kisman.NAME, version = Kisman.VERSION)
public class Main {

    @EventHandler
    public void init(FMLInitializationEvent event) {
    	Kisman.instance = new Kisman();
    	Kisman.instance.init();

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void disconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        SaveConfig.init();
    }
}
