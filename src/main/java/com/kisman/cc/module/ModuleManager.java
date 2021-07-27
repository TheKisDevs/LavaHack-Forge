package com.kisman.cc.module;

import java.util.ArrayList;

import com.kisman.cc.module.client.ClickGUI;
import com.kisman.cc.module.client.HUD;
import com.kisman.cc.module.combat.Criticals;
import com.kisman.cc.module.movement.*;
import com.kisman.cc.module.player.Velocity;
import com.kisman.cc.module.render.*;

public class ModuleManager {

	public ArrayList<Module> modules;
	
	public ModuleManager() {
		(modules = new ArrayList<Module>()).clear();
		this.modules.add(new ClickGUI());
		this.modules.add(new HUD());
		this.modules.add(new Sprint());
		//this.modules.add(new AutoClicker());
		this.modules.add(new Velocity());
		this.modules.add(new FullBright());
		this.modules.add(new AutoJump());
		this.modules.add(new CustomFov());
		this.modules.add(new Criticals());
		this.modules.add(new Step());
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
		return this.modules;
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
}
