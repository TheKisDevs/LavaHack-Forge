import com.kisman.cc.Kisman;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = Kisman.MODID, name = Kisman.NAME, version = Kisman.VERSION)
public class Main {

    @EventHandler
    public void init(FMLInitializationEvent event) {
    	Kisman.instance = new Kisman();
    	Kisman.instance.init();
    }
}
