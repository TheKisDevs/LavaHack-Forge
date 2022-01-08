package com.kisman.cc.module;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

import com.kisman.cc.module.chat.*;
import com.kisman.cc.module.client.*;
import com.kisman.cc.module.combat.*;
import com.kisman.cc.module.exploit.*;
import com.kisman.cc.module.misc.*;
import com.kisman.cc.module.movement.*;
import com.kisman.cc.module.player.*;
import com.kisman.cc.module.render.*;
import com.kisman.cc.util.customfont.CustomFontUtil;
import net.minecraft.client.Minecraft;
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
		modules.add(new AntiBot());
		modules.add(new AntiBow());
		modules.add(new AntiTrap());
		modules.add(new AutoArmor());
		modules.add(new AutoBowExploit());
		modules.add(new AutoClicker());
		modules.add(new AutoCrystal());
		modules.add(new AutoCrystalRewrite());
		modules.add(new AutoFirework());
		modules.add(new AutoPot());
		modules.add(new AutoRer());
		modules.add(new AutoTotem());
		modules.add(new AutoTrap());
		modules.add(new BowAimBot());
		modules.add(new BowSpam());
		modules.add(new Burrow());
		modules.add(new BurrowBypass());
		modules.add(new Criticals());
		modules.add(new CrystalFiller());
		modules.add(new HoleFiller());
		modules.add(new KillAura());
		modules.add(new OffHand());
		modules.add(new SilentXp());
		modules.add(new Surround());
		//client
		modules.add(new Cape());
		modules.add(new ClickGUI());
		modules.add(new ClientFont());
		modules.add(new ColorModule());
		modules.add(new Config());
		modules.add(new Console());
		modules.add(new CSGOGui());
		modules.add(new CustomFont());
		modules.add(new DiscordRPC());
		modules.add(new Dumper());
		modules.add(new HUD());
		modules.add(new NewGui());
		modules.add(new NotEnoughCoinsModule());
		modules.add(new ExampleModule());
		modules.add(new ParticleGui());
		modules.add(new SandBox());
		modules.add(new Test());
		//chat
		modules.add(new AntiSpamBypass());
		modules.add(new AutoEZ());
		modules.add(new AutoGlobal());
		modules.add(new ChatAnimation());
		modules.add(new ChatSuffix());
		modules.add(new Notification());
		modules.add(new Spammer());
		modules.add(new TimeStamps());
		modules.add(new TotemPopCounter());
		modules.add(new TraceTeleport());
		//render
		modules.add(new Ambience());
		modules.add(new Animation());
		modules.add(new BlockOutline());
		modules.add(new Breadcrumbs());
		modules.add(new Charms());
		modules.add(new ChinaHat());
		modules.add(new CityESP());
		modules.add(new CrystalModifier());
		modules.add(new CustomFog());
		modules.add(new CustomFov());
//		modules.add(new EntityESP());
		modules.add(new FramebufferTest());
		modules.add(new FullBright());
//		modules.add(new HandCharms());
		modules.add(new HoleESP());
//		modules.add(new ItemCharms());
		modules.add(new KismanESP());
		modules.add(new NameTags());
//		modules.add(new NoPitchLimit());
		modules.add(new NoRender());
		modules.add(new Particle());
//		modules.add(new PenisESP());
//		modules.add(new PlayerModifier());
		modules.add(new PopCharms());
		modules.add(new PortalESP());
		modules.add(new RangeVisualisator());
		modules.add(new Reverse());
		modules.add(new ShaderCharms());
		modules.add(new SkyColor());
//		modules.add(new SpawnsESP());
		modules.add(new Spin());
		modules.add(new StorageESP());
		modules.add(new SwingAnimation());
		modules.add(new TargetESP());
//		modules.add(new Tracers());
//		modules.add(new Trails());
		modules.add(new Trajectories());
		modules.add(new TrajectoriesRewrite());
		modules.add(new ViewModel());
		modules.add(new XRay());
		//movement
		modules.add(new AirJump());
		modules.add(new Anchor());
		modules.add(new AutoJump());
		modules.add(new AutoWalk());
		modules.add(new ElytraFly());
		modules.add(new FastSwim());
		modules.add(new Fly());
		modules.add(new HoleTP());
		modules.add(new IceSpeed());
		modules.add(new Jesus());
		modules.add(new NoJumpDelay());
		modules.add(new NoRotate());
		modules.add(new NoSlow());
		modules.add(new NoSlowBypass());
		modules.add(new NoSlowSneak());
		modules.add(new NoWeb());
		modules.add(new Parkour());
		modules.add(new ReverseStep());
//		modules.add(new SafeWalk());
		modules.add(new Scaffold());
		modules.add(new Speed());
		modules.add(new Spider());
		modules.add(new Sprint());
		modules.add(new Step());
//		modules.add(new Zoom());
		//player
		modules.add(new AntiKnokBack());
		modules.add(new FastBreak());
		modules.add(new FastPlace());
		modules.add(new NoInteract());
		modules.add(new PacketCancel());
		modules.add(new Refill());
		modules.add(new Swing());
		modules.add(new TeleportBack());
		modules.add(new Velocity());
		//exploit
		modules.add(new AntiLogger());
		modules.add(new AutoKick());
//		modules.add(new BookFormatModule());
		modules.add(new BowExploit());
		modules.add(new BowExploitRewrite());
		modules.add(new CactusLeave());
		modules.add(new Disabler());
		modules.add(new Ghost());
		modules.add(new KismansDupe());
		modules.add(new MiddleClick());
		modules.add(new MultiTask());
		modules.add(new NoMiningTrace());
		modules.add(new NoSwing());
		modules.add(new PacketFly());
		modules.add(new PacketMine());
		modules.add(new Rubberband());
		modules.add(new SilentClose());
		modules.add(new SoundCoordLogger());
//		modules.add(new StrongholdFinder());
		modules.add(new TickShift());
		modules.add(new Timer());
		modules.add(new WaterLeave());
		modules.add(new WebLeave());
		//misc
		modules.add(new AutoLog());
		modules.add(new BurrowCounter());
		modules.add(new FakePlayer());
//		modules.add(new ItemScroller());
		modules.add(new MurderFinder());
		modules.add(new NameProtect());
		modules.add(new PigPOV());
		modules.add(new SelfDamage());
//		modules.add(new TeamRusherLag());
//		modules.add(new Tracker());
		modules.add(new VisualRange());
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

	public ArrayList<Module> getEnabledModules() {
		ArrayList<Module> enabled = new ArrayList<>();
		modules.stream().forEach(module -> {
			if(module.isToggled()) {
				enabled.add(module);
			}
		});

		return enabled;
	}

	public ArrayList<Module> getSortModuleList(boolean reverse) {
		ArrayList<Module> sorted = new ArrayList<>();
		getEnabledModules().stream().filter(module -> module.visible)
				.sorted(Comparator.comparing(module -> CustomFontUtil.getStringWidth(module.getName() + " " + module.getDisplayInfo()) * (reverse ? -1 : 1)))
				.collect(Collectors.toList());

		return sorted;
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

	public void key(char typedChar, int key, Module mod) {
		if(mod.isToggled()) {
			mod.key();
			mod.key(key);
			mod.key(typedChar, key);
		}
	}
}
