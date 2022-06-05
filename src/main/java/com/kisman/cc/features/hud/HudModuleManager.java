package com.kisman.cc.features.hud;

import com.kisman.cc.features.hud.modules.Speed;

import com.kisman.cc.features.hud.modules.*;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;

public class HudModuleManager {
    public ArrayList<HudModule> modules;
	
	public HudModuleManager() {
		modules = new ArrayList<>();
		MinecraftForge.EVENT_BUS.register(this);
		init();
	}

	public void init() {
		modules.add(new ArmorHUD());
		modules.add(ArrayListModule.instance);
		modules.add(new BindList());
		modules.add(new Coord());
		modules.add(new CrystalPerSecond());
		modules.add(new CurrentConfig());
		modules.add(new Fps());
		modules.add(new Indicators());
		modules.add(new Logo());
		modules.add(new PacketChat());
		modules.add(new Ping());
		modules.add(new PvpInfo());
		modules.add(new PvpResources());
		modules.add(new Radar());
		modules.add(new ServerIp());
		modules.add(new Speed());
		modules.add(new TargetHUD());
		modules.add(new Tps());
		modules.add(new Welcomer());
	}
	
	public HudModule getModule(String name) {
		for (HudModule m : this.modules) if (m.getName().equalsIgnoreCase(name)) return m;
		return null;
	}
}
