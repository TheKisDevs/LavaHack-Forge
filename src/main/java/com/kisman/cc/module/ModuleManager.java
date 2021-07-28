package com.kisman.cc.module;

import java.util.ArrayList;

import com.kisman.cc.module.client.ClickGUI;
import com.kisman.cc.module.client.HUD;
import com.kisman.cc.module.dl.DLGui;
import com.kisman.cc.module.movement.*;
import com.kisman.cc.module.player.AntiKnokBack;
import com.kisman.cc.module.render.*;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ModuleManager {

	public ArrayList<Module> modules;
	
	public ModuleManager() {
		modules = new ArrayList<Module>();
		MinecraftForge.EVENT_BUS.register(this);
		init();
	}

	public void init() {
		modules.add(new AntiKnokBack());
		modules.add(new AutoJump());
		modules.add(new CustomFov());
		//modules.add(new Criticals());
		modules.add(new ClickGUI());
		modules.add(new DLGui());
		modules.add(new Sprint());
		modules.add(new Step());
		modules.add(new HUD());
		//this.modules.add(new AutoClicker());
		modules.add(new Fly());
		modules.add(new FullBright());
		//this.modules.add(new AntiBot());
		//this.modules.add(new NoFall())
	}
	
	public Module getModule(String name) {
		for (Module m : this.modules) {
			if (m.getName().equalsIgnoreCase(name)) {
				return m;
			}
		}
		return null;
	}
	
	public ArrayList<Module> getModuleList() {
		return modules;
	}
	
	public ArrayList<Module> getModulesInCategory(Category c) {
		ArrayList<Module> mods = new ArrayList<Module>();
		for (Module m : this.modules) {
			if (m.getCategory() == c) {
				mods.add(m);
			}
		}
		return mods;
	}

	@SubscribeEvent
	public void onKey(InputEvent.KeyInputEvent event) {
//		if(Keyboard.getEventKeyState()) {
//			for(Module m : modules) {
//				if(m.getKey() == Keyboard.getEventKey()) {
//					m.toggle();
//				}
//			}
//		}
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		for(Module m : modules) {
			if(m.isToggled()) {
				m.update();
			}
		}
	}

	@SubscribeEvent
	public void onRender(RenderGameOverlayEvent event) {
		for(Module m : modules) {
			if(m.isToggled()) {
				m.render();
			}
		}
	}
}
