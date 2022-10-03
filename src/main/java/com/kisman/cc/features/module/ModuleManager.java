package com.kisman.cc.features.module;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.module.Debug.*;
import com.kisman.cc.features.module.client.*;
import com.kisman.cc.features.module.combat.*;
import com.kisman.cc.features.module.exploit.LogoutSpots;
import com.kisman.cc.features.module.exploit.*;
import com.kisman.cc.features.module.misc.*;
import com.kisman.cc.features.module.movement.*;
import com.kisman.cc.features.module.player.*;
import com.kisman.cc.features.module.render.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
	public List<Module> modules;
	
	public ModuleManager() {
		modules = new ArrayList<>();
		MinecraftForge.EVENT_BUS.register(this);
		Kisman.processAccountData();

//		Loader<Module> loader = new Loader<>();
//		loader.exclude("com.kisman.cc.features.module.Module");
//		loader.filter(Module::isToggled);
//		modules = loader.loadAllFromPackage("com.kisman.cc.features.module");

		//Debug
		modules.add(new BaritoneTest());
		modules.add(new BlockOverlay());
		modules.add(new ChatPrint());
		//modules.add(new ChorusTP());
		modules.add(ClickCooldownReset.INSTANCE);
		modules.add(new EventSystemTest());
		modules.add(new FallbackableFontTest());
		modules.add(new FastFallTest());
		modules.add(new FrostWalk());
		modules.add(new FutureShader());
		modules.add(new GLScissorTest());
		modules.add(new HelloWorld());
//		modules.add(new M2LTest());
		modules.add(new Meow());
		modules.add(new ModuleInstancingJava());
		modules.add(new ModuleInstancingKt());
		modules.add(new NoMove());
		modules.add(new ObjectMouseOver());
		modules.add(new PacketIDGetterTest());
		modules.add(new RectTest());
		modules.add(new ScaffoldTest());
		modules.add(new ScreenShaders());;
		modules.add(new SmoothRenderer());
		modules.add(SwingTest.INSTANCE);
		modules.add(new TextFieldTest());
		modules.add(new TowerTest());
		modules.add(new TracerTest());
		modules.add(new Triangulation());

		//combat
//		modules.add(new AutoCrystal());
		modules.add(new AntiBot());
		modules.add(new AntiBow());
		modules.add(new AntiTrap());
		modules.add(new AutoAnvil());
		modules.add(new AutoArmor());
		modules.add(new AutoClicker());
		modules.add(AutoCrystalPvP.INSTANCE);
		modules.add(new AutoFirework());
		modules.add(new AutoObsidian());
		modules.add(new AutoPot());
		modules.add(new AutoQuiver());
		modules.add(new AutoRer());
		modules.add(new AutoTrap());
		modules.add(new BowAimBot());
		modules.add(new BowSpam());
//		modules.add(new BreakAlert());
		modules.add(new Burrow2());
		modules.add(new CityBoss());
		modules.add(new Criticals());
		modules.add(new CrystalFiller());
		modules.add(new CrystalPvPHelper());
		modules.add(Crystals.INSTANCE);
//		modules.add(new Flatten());
		modules.add(new FlattenRewrite());
		modules.add(new HandRewrite());
		modules.add(new HoleFillerRewrite());
		modules.add(new HoleKicker());
		modules.add(new KillAuraRewrite());
		modules.add(new OffHand());
		modules.add(new SelfTrap());
		modules.add(new SilentXp());
		modules.add(new Surround());
		modules.add(new SurroundRewrite());
//		modules.add(new TrapDoorBurrow());
		//client
		modules.add(new Baritone());
		modules.add(new Cape());
		modules.add(new Changer());
//		modules.add(ClientFixer.INSTANCE);
		modules.add(new Config());
		modules.add(CustomFontModule.instance);
		modules.add(new CustomMainMenuModule());
		modules.add(new DiscordRPCModule());
		modules.add(new GuiModule());
		modules.add(NoSpoof.INSTANCE);
		modules.add(new PauseBaritone());
		modules.add(PingBypass.INSTANCE);
		//render
		modules.add(new BlockESP());
		modules.add(new BlockHighlight());
		//modules.add(new BlockLiner());
		modules.add(new Breadcrumbs());
		modules.add(new CameraClip());
		modules.add(CharmsRewrite.INSTANCE);
		modules.add(new CityESP());
		modules.add(new ContainerModifier());
		modules.add(new CrystalModifier());
		modules.add(new CrystalSafeBlocks());
		modules.add(new DamageESP());
		modules.add(new EntityESPRewrite());
//		modules.add(new HoleESP());
//		modules.add(new HoleESPRewrite());
		modules.add(new HoleESPRewrite2());
		modules.add(new HotbarModifier());
		modules.add(new ItemESPRewrite());
		modules.add(new JumpCircle());
//		modules.add(new LogoutSpots());
		modules.add(new MotionBlur());
		modules.add(new NameTags());
		modules.add(new NoBobbing());
		modules.add(new NoRender());
		modules.add(new Particle());
//		modules.add(new PearlTracer());
		modules.add(new PopCharms());
		modules.add(new ScreenTint());
		modules.add(new SelfCityESP());
		modules.add(new ShaderCharms());
		modules.add(new Shaders());
		modules.add(new SpawnsESP());
		modules.add(new StorageESP());
		modules.add(new SwingAnimation());
		modules.add(new SwingProgress());
		modules.add(new Tracers2());
//		modules.add(new Trails());
//		modules.add(new Trajectories());
//		modules.add(new TrajectoriesRewrite());
		modules.add(new ViewModel());
		modules.add(new Weather());
		modules.add(new XRay());
		//movement
		modules.add(new AirJump());
		modules.add(new Anchor());
		modules.add(new BoatFly());
		modules.add(new ElytraFly());
		modules.add(new Fly());
		modules.add(new Jesus());
		modules.add(new MoveModifier());
		modules.add(new NoFall());
		modules.add(new NoPushOutBlock());
		modules.add(new NoRotate());
		modules.add(new NoSlow());
		modules.add(new NoWeb());
		modules.add(new SafeWalk());
		modules.add(new ScaffoldRewrite());
		modules.add(new SoftScaffold());
		modules.add(new Speed());
//		modules.add(SpeedRewrite.INSTANCE);
		modules.add(new Spider());
		modules.add(new TargetStrafe());
		//player
		modules.add(AntiDesync.INSTANCE);
		modules.add(new AntiHunger());
		modules.add(new AutoMine());
		modules.add(new AutoRespawn());
		modules.add(new ChorusDelay());
		modules.add(new ForgeBypass());
		modules.add(new FreeCamBypass());
		modules.add(new FreeCamRewrite());
		modules.add(Interaction.INSTANCE);
		modules.add(new Octopus());
		//modules.add(new PacketFeatures());
		modules.add(new Refill());
		modules.add(new Replenish());
		modules.add(new RotateModifier());
		modules.add(new Velocity());
		modules.add(new YawLock());
		modules.add(new YawStep());
		//exploit
		modules.add(new AntiLogger());
		modules.add(new AntiVanish());
		modules.add(new BookFormatModule());
		modules.add(new BowExploit());
		modules.add(new ChorusBypass());
		modules.add(new CowDupe());
		modules.add(new DamageLeave());
		modules.add(new FakePearl());
		modules.add(new FastMove());
		modules.add(new Ghost());
		modules.add(new HandMine());
		modules.add(new LogoutSpots());
		modules.add(new MiddleClick());
		modules.add(new NewChunks());
		modules.add(new NoFallExploit());
		modules.add(new NoGlitchBlocks());
		modules.add(new PacketEat());
		modules.add(new PacketFly());
		modules.add(new PacketMine());
		modules.add(new PlayerLook());
		//modules.add(new PreciseStrongholdFinder());
		modules.add(new SilentClose());
		modules.add(new SoundCoordLogger());
		modules.add(new Teleport());
		modules.add(new TickShift());
		modules.add(new TickShiftTeleport());
		modules.add(new TraceTeleport());
		modules.add(new WaterLeave());
		modules.add(new WebLeave());
		//misc
		modules.add(new AntiRegear());
		modules.add(new AntiSpammer());
		modules.add(new AutoEZ());
		modules.add(new AutoLog());
		modules.add(new BetterScreenshot());
		modules.add(new BurrowCounter());
		modules.add(new ChatModifier());
		//modules.add(new DDOSModule());
		modules.add(new FakePlayer());
//		modules.add(new FreeLook());
		modules.add(new Funny());
		modules.add(new HotbarScroller());
		modules.add(new MurderFinder());
		modules.add(new NameProtect());
		modules.add(new Optimizer());
		//modules.add(new PacketDelay());
		modules.add(new PortalsModifier());
		modules.add(new Printer());
		modules.add(new Reverse());
		modules.add(new SelfDamage());
		modules.add(new SkyBlockFeatures());
		modules.add(new SkylightFix());
		modules.add(new SlotMapper());
		modules.add(new Spammer());
		modules.add(new Spin());
		modules.add(new TotemPopCounter());
		modules.add(new Tracker());
		modules.add(new VisualRange());
		modules.add(new WeaknessLog());
		modules.add(new XCarry());

		//modules = modules.stream().filter(module -> {
		//	if(module.getClass().getAnnotation(OnlyDebug.class) == null)
		//		return true;
		//	return Kisman.MODULE_DEBUG;
		//}).collect(Collectors.toList());
	}
	
	public Module getModule(String name) {
		for (Module m : this.modules) if (m.getName().equalsIgnoreCase(name)) return m;
		return null;
	}
	
	public ArrayList<Module> getModulesInCategory(Category c) {
		ArrayList<Module> mods = new ArrayList<>();
		for (Module m : this.modules) if (m.getCategory() == c) mods.add(m);
		return mods;
	}

	public ArrayList<Module> getEnabledModules() {
		ArrayList<Module> enabled = new ArrayList<>();
		modules.stream().filter(Module::isToggled).forEach(enabled::add);
		return enabled;
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		for(Module m : modules) if(m.isToggled()) m.update();
	}

	public void key(char typedChar, int key, Module mod) {
		if(mod.isToggled()) {
			mod.key();
			mod.key(key);
			mod.key(typedChar, key);
		}
	}
}
