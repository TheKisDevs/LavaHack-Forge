package com.kisman.cc.module;

import java.util.ArrayList;

import com.kisman.cc.module.chat.*;
import com.kisman.cc.module.client.*;
import com.kisman.cc.module.combat.*;
import com.kisman.cc.module.exploit.*;
import com.kisman.cc.module.misc.*;
import com.kisman.cc.module.movement.*;
import com.kisman.cc.module.player.*;
import com.kisman.cc.module.render.*;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ModuleManager {
	public ArrayList<Module> modules;
	
	public ModuleManager() {
		modules = new ArrayList<>();
		MinecraftForge.EVENT_BUS.register(this);
		init();
	}

	public void init() {
		//combat
		modules.add(new AimBot());
		modules.add(new AutoArmor());
		modules.add(new AutoClicker());
		modules.add(new AutoCrystal());
		modules.add(new AutoCrystalBypass());
		modules.add(new AutoPot());
		modules.add(new AutoTotem());
		modules.add(new CevBreaker());
		modules.add(new KillAura());
		modules.add(new OffHand());
		modules.add(new Rubberband());
		modules.add(new Surround());
		//client
		modules.add(new Cape());
		modules.add(new ClickGUI());
		modules.add(new ColorModule());
		modules.add(new Console());
		modules.add(new CustomFont());
		modules.add(new DiscordRPC());
		modules.add(new HUD());
		modules.add(new ExampleModule());
		modules.add(new ParticleGui());
		//chat
		modules.add(new AutoEZ());
		modules.add(new Notification());
		modules.add(new Spammer());
		//render
		modules.add(new BlockOutline());
		modules.add(new CustomFov());
		modules.add(new Charms());
		modules.add(new ChinaHat());
		modules.add(new EntityESP());
		modules.add(new FullBright());
		modules.add(new HoleESP());
		modules.add(new KismanESP());
		modules.add(new NameTags());
		modules.add(new NoRender());
		modules.add(new SkyColor());
		modules.add(new Spin());
		modules.add(new StorageESP());
		modules.add(new SwingAnimation());
		modules.add(new ViemModel());
		modules.add(new Particle());
		//movement
		modules.add(new Anchor());
		modules.add(new AutoJump());
		modules.add(new AutoWalk());
		modules.add(new Fly());
		modules.add(new IceSpeed());
		modules.add(new Jesus());
		modules.add(new NoRotate());
		modules.add(new NoSlow());
		modules.add(new Parkour());
		modules.add(new ReverseStep());
		modules.add(new Speed());
		modules.add(new Spider());
		modules.add(new Sprint());
		modules.add(new Step());
		modules.add(new Zoom());
		//player
		modules.add(new AntiKnokBack());
		modules.add(new FastBreak());
		modules.add(new FastPlace());
		modules.add(new NoInteract());
		modules.add(new PacketCancel());
		modules.add(new TeleportBack());
		modules.add(new Velocity());
		//exploit
		modules.add(new BowExploit());
		modules.add(new CactusLeave());
		modules.add(new Ghost());
		modules.add(new KismansDupe());
		modules.add(new MiddleClick());
		modules.add(new NoMiningTrace());
		modules.add(new PacketFly());
		modules.add(new PacketMine());
		modules.add(new WaterLeave());
		modules.add(new WebLeave());
		//misc
		modules.add(new AutoLog());
		modules.add(new BurrowCounter());
		modules.add(new FakePlayer());
		modules.add(new MurderFinder());
		modules.add(new TeamRusherLag());
		modules.add(new WeaknessLog());
		modules.add(new XCarry());
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
		ArrayList<Module> mods = new ArrayList<>();
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
