package com.kisman.cc.hud.hudmodule;

import com.kisman.cc.hud.hudmodule.combat.*;
import com.kisman.cc.hud.hudmodule.movement.Speed;
import com.kisman.cc.hud.hudmodule.player.*;
import com.kisman.cc.hud.hudmodule.render.*;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.*;

import java.util.ArrayList;

public class HudModuleManager {
    public ArrayList<HudModule> modules;
	
	public HudModuleManager() {
		modules = new ArrayList<>();
		MinecraftForge.EVENT_BUS.register(this);
		init();
	}

	public void init() {
		//combat
		modules.add(new ArmorHUD());
		modules.add(new CrystalPerSecond());
		modules.add(new PvpInfo());
		modules.add(new PvpResources());
		modules.add(new TargetHUD());

		//movement
		modules.add(new Speed());

		//render
        modules.add(ArrayListModule.instance);
		modules.add(new BindList());
		modules.add(new Coord());
		modules.add(new Fps());
		modules.add(new Logo());
		modules.add(new NotificationsModule());
		modules.add(new Radar());
		modules.add(new Welcomer());
		modules.add(new PacketChat());

		//player
		modules.add(new Indicators());
		modules.add(new Ping());
		modules.add(new ServerIp());
		modules.add(new Tps());
	}
	
	public HudModule getModule(String name) {
		for (HudModule m : this.modules) if (m.getName().equalsIgnoreCase(name)) return m;
		return null;
	}

	public ArrayList<HudModule> getModulesInCategory(HudCategory c) {
		ArrayList<HudModule> mods = new ArrayList<>();
		for (HudModule m : this.modules) if (m.getCategoryHud() == c) mods.add(m);
		return mods;
	}
}
