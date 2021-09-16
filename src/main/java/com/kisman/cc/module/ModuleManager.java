package com.kisman.cc.module;

import java.util.ArrayList;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.chat.*;
import com.kisman.cc.module.client.*;
import com.kisman.cc.module.combat.*;
import com.kisman.cc.module.exploit.NoDamage;
import com.kisman.cc.module.exploit.PacketMine;
import com.kisman.cc.module.exploit.WaterLeave;
import com.kisman.cc.module.exploit.WebLeave;
import com.kisman.cc.module.misc.FakePlayer;
import com.kisman.cc.module.movement.*;
import com.kisman.cc.module.player.*;
import com.kisman.cc.module.render.*;
import i.gishreloaded.gishcode.utils.system.Connection;
import i.gishreloaded.gishcode.wrappers.Wrapper;
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
		//combat
		modules.add(new AutoArmor());
		modules.add(new AutoTotem());
		modules.add(new OffHand());
		//client
		modules.add(new ClickGUI());
		modules.add(new Color());
		modules.add(new Console());
		modules.add(new CustomFont());
		modules.add(new HUD());
		modules.add(new ExampleModule());
		//chat
		modules.add(new AutoEZ());
		modules.add(new Notification());
		modules.add(new Spammer());
		//render
		modules.add(new BlockOutline());
		modules.add(new CustomFov());
		modules.add(new Charms());
		modules.add(new EntityESP());
		modules.add(new FullBright());
		modules.add(new KismanESP());
		modules.add(new NoRender());
		modules.add(new StorageESP());
		modules.add(new SwingAnimation());
		modules.add(new ViemModel());
		//movement
		modules.add(new AutoJump());
		modules.add(new AutoWalk());
		modules.add(new Fly());
		modules.add(new NoSlow());
		modules.add(new ReverseStep());
		modules.add(new Speed());
		modules.add(new Spider());
		modules.add(new Sprint());
		modules.add(new Step());
		//player
		modules.add(new AntiKnokBack());
		modules.add(new FastBreak());
		modules.add(new Velocity());
		//exploit
		modules.add(new NoDamage());
		modules.add(new PacketMine());
		modules.add(new WaterLeave());
		modules.add(new WebLeave());
		//misc
		modules.add(new FakePlayer());
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
	public void onKey(InputEvent.KeyInputEvent event) {}

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
