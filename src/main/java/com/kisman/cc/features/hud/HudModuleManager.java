package com.kisman.cc.features.hud;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.client.loadingscreen.progressbar.EventProgressBar;
import com.kisman.cc.features.hud.modules.*;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;

public class HudModuleManager {
    public ArrayList<HudModule> modules = new ArrayList<>();
	
	public HudModuleManager() {
		MinecraftForge.EVENT_BUS.register(this);
		Kisman.instance.progressBar.steps++;
	}

	public void init() {
		Kisman.EVENT_BUS.post(new EventProgressBar("HUD Module Manager"));

		modules.add(new TwoBeeTwoTeeQueue());
		modules.add(new ArmorHUD());
		modules.add(ArrayListModule.instance);
		modules.add(new BindList());
		modules.add(new Coords());
		modules.add(new CrystalPerSecond());
		modules.add(new CurrentConfig());
		modules.add(new Fps());
		modules.add(new Indicators());
		modules.add(new InventoryHud());
		modules.add(new Logo());
		modules.add(new PearlCooldown());
		modules.add(new Ping());
		modules.add(new PotionHud());
		modules.add(new PvpInfo());
		modules.add(new PvpResources());
		modules.add(new Radar());
		modules.add(new ServerIp());
		modules.add(new Speed());
		modules.add(new TargetHUD());
		modules.add(new TextRadar());
		modules.add(new Tps());
		modules.add(new Welcomer());
	}
	
	public HudModule getModule(String name) {
		for (HudModule m : this.modules) if (m.getName().equalsIgnoreCase(name)) return m;
		return null;
	}
}
