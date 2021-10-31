package com.kisman.cc.hud.hudmodule;

import com.kisman.cc.hud.hudmodule.combat.*;
import com.kisman.cc.hud.hudmodule.misc.NotificationHud;
import com.kisman.cc.hud.hudmodule.movement.Speed;
import com.kisman.cc.hud.hudmodule.player.Ping;
import com.kisman.cc.hud.hudmodule.player.ServerIp;
import com.kisman.cc.hud.hudmodule.player.Tps;
import com.kisman.cc.hud.hudmodule.render.*;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class HudModuleManager {
    public java.util.ArrayList<HudModule> modules;
	
	public HudModuleManager() {
		modules = new java.util.ArrayList<>();
		MinecraftForge.EVENT_BUS.register(this);
		init();
	}

	public void init() {
		//combat
		modules.add(new ArmorHUD());
		modules.add(new PvpInfo());
		modules.add(new TargetInfo());

		//movement
		modules.add(new Speed());

		//render
        modules.add(new ArrayList());
		modules.add(new Coord());
		modules.add(new Fps());
		modules.add(new Logo());
		modules.add(new Radar());
		modules.add(new Welcomer());

		//player
		modules.add(new Ping());
		modules.add(new ServerIp());
		modules.add(new Tps());

		//misc
		modules.add(new NotificationHud());
	}
	
	public HudModule getModule(String name) {
		for (HudModule m : this.modules) {
			if (m.getName().equalsIgnoreCase(name)) {
				return m;
			}
		}
		return null;
	}
	
	public java.util.ArrayList<HudModule> getModuleList() {
		return modules;
	}
	
	public java.util.ArrayList<HudModule> getModulesInCategory(HudCategory c) {
		java.util.ArrayList<HudModule> mods = new java.util.ArrayList<HudModule>();
		for (HudModule m : this.modules) {
			if (m.getCategory() == c) {
				mods.add(m);
			}
		}
		return mods;
	}

	@SubscribeEvent
	public void onKey(InputEvent.KeyInputEvent event) {}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		for(HudModule m : modules) {
			if(m.isToggled()) {
				m.update();
			}
		}
	}

	@SubscribeEvent
	public void onRender(RenderGameOverlayEvent event) {
		for(HudModule m : modules) {
			if(m.isToggled()) {
				m.render();
			}
		}
	}
}
