package com.kisman.cc.features.module;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.client.loadingscreen.progressbar.EventProgressBar;
import com.kisman.cc.features.catlua.module.ModuleScript;
import com.kisman.cc.features.module.Debug.*;
import com.kisman.cc.features.module.client.*;
import com.kisman.cc.features.module.combat.*;
import com.kisman.cc.features.module.exploit.LogoutSpots;
import com.kisman.cc.features.module.exploit.*;
import com.kisman.cc.features.module.misc.*;
import com.kisman.cc.features.module.movement.*;
import com.kisman.cc.features.module.player.*;
import com.kisman.cc.features.module.render.*;
import com.kisman.cc.features.plugins.ModulePlugin;
import com.kisman.cc.features.subsystem.subsystems.Targetable;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
	public List<Module> modules = new ArrayList<>();
	public List<Module> targetableModules = new ArrayList<>();
	
	public ModuleManager() {
		Kisman.instance.progressBar.steps++;
	}

	public void init() {
		MinecraftForge.EVENT_BUS.register(this);
		Kisman.EVENT_BUS.post(new EventProgressBar("Module Manager"));

//		Loader<Module> loader = new Loader<>();
//		loader.exclude("com.kisman.cc.features.module.Module");
//		loader.filter(Module::isToggled);
//		modules = loader.loadAllFromPackage("com.kisman.cc.features.module");

		//Debug
//		add(new BaritoneTest());
		add(new BlockOverlay());
		add(new BurrowHelper());
		add(new ChatPrint());
		//add(new ChorusTP());
		add(ClickCooldownReset.INSTANCE);
//		add(new EventSystemTest());
//		add(new FallbackableFontTest());
		add(new FastFallTest());
		add(new FrostWalk());
		add(new FutureShader());
		//add(new GhostBlock());
		add(new GLScissorTest());
		add(new HealthCancel());
		add(new HelloWorld());
		add(new HolesTest());
		add(new LavaHackOwns());
//		add(new M2LTest());
		add(new Meow());
		add(new ModuleInstancingJava());
		add(new ModuleInstancingKt());
		add(new MoveInspector());
//		add(new NoMove());
		add(new ObjectMouseOver());
		add(new PacketIDGetterTest());
		add(new PacketMineProviderTest());
		add(new RectTest());
		add(new RotationTest());
//		add(new ScaffoldTest());
//		add(new ScaffoldTest2());
		add(new ScreenShaders());
		add(new SmoothRenderer());
		add(SwingTest.INSTANCE);
//		add(new TextFieldTest());
//		add(new TowerTest());
//		add(new TracerTest());
		add(new Triangulation());

		//combat
//		add(new AutoCrystal());
		add(new AntiBot());
		add(new AntiBow());
//		add(new AntiTrap());
		add(new AutoAnchor());
		add(new AutoAnvil());
		add(new AutoArmor());
		add(AutoCrystalPvP.INSTANCE);
		add(new AutoObsidian());
		add(new AutoQuiver());
		add(new AutoRer());
		add(new AutoTrap());
		add(new Avoid());
		add(new Blocker());
		add(new BowAimBot());
		add(new BowSpam());
//		add(new BreakAlert());
		add(new Burrow2());
		add(new CevBreaker());
		add(new CityBoss());
		add(new Criticals());
		add(new CrystalFiller());
		add(new CrystalPvPHelper());
		add(Crystals.INSTANCE);
		add(new DamageIncreaser());
		add(new FireworkAura());
//		add(new Flatten());
		add(new FlattenRewrite());
		add(new HandRewrite());
		add(new HoleFillerRewrite());
		add(new HoleKicker());
		add(new KillAuraRewrite());
		add(new AutoCrystalRewrite());
		add(new OffHand());
		add(new Prison());
		add(new Robot());
		add(new SelfTrapRewrite());
//		add(new Surround());
		add(new SurroundRewrite());
//		add(new TrapDoorBurrow());
		//client
		add(new Baritone());
		add(new BetterScreenshot());
		add(new Cape());
		add(Changer.INSTANCE);
		add(new ChunkAnimator());
		add(new ClientFixer());
		add(new Config());
		add(CustomFontModule.instance);
		add(new DevelopmentHelper());
		add(new DiscordRPCModule());
		add(FriendHighlight.INSTANCE);
		add(new GuiModule());
		add(new GuiShader());
		add(new MainMenuModule());
		add(NoSpoof.INSTANCE);
		add(new PauseBaritone());
		add(PingBypass.INSTANCE);
		add(new Printer());
		add(new SchematicaModule());
		add(new ViaForgeModule());
		//render
		add(new BlockESP());
		add(new BlockHighlight());
		//add(new BlockLiner());
		add(new Breadcrumbs());
		add(CharmsRewrite.INSTANCE);
		add(new CityESP());
		add(new ContainerModifier());
		add(new CrystalModifier());
		add(new CrystalSafeBlocks());
		add(new DamageESP());
		add(new EnchantGlint());
		add(new EntityESPRewrite());
//		add(new FovModifier());
//		add(new HoleESP());
//		add(new HoleESPRewrite());
		add(new HoleESPRewrite2());
		add(new HotbarModifier());
		add(new InfiniteRender());
		add(new ItemESPRewrite());
		add(new JumpCircle());
//		add(new LogoutSpots());
		add(new MotionBlur());
		add(new NameTags());
//		add(new NoBobbing());
//		add(new NoCrosshair());
		add(new NoRender());
		add(new PearlTracer());
		add(new PlayerLook());
		add(new PopCharms());
		add(new ScreenTint());
		add(new SelfCityESP());
		add(new ShaderCharms());
		add(new Shaders());
		add(new SmallShield());
		add(new SmartCityESP());
		add(new SpawnsESP());
		add(new StorageESP());
		add(new SwingAnimation());
		add(new SwingProgress());
		add(new Tracers2());
//		add(new Trails());
//		add(new Trajectories());
//		add(new TrajectoriesRewrite());
		add(new ViewModel());
		add(new XRay());
		//movement
		add(new AirJump());
		add(new Anchor());
		add(new AutoPacketFly());
		add(new BoatFly());
		add(new CornerClip());
		add(new ElytraFly());
		add(new Fly());
		add(new HoleSnap());
		add(new Jesus());
		add(new MoveModifier());
		add(new NoFall());
		add(new NoPushOutBlock());
		add(new NoRotate());
		add(new NoSlow());
		add(new NoWeb());
		add(new Phase());
		add(new SafeWalk());
		add(new Scaffold());
		add(new SkyBlockFarmer());
		add(new SoftScaffold());
		add(new Speed());
//		add(SpeedRewrite.INSTANCE);
		add(new Spider());
		add(new Strafe());
		add(new TargetStrafe());
		//player
		add(new AirPlace());
		add(AntiDesync.INSTANCE);
		add(new AntiHunger());
		add(new AntiWeakness());
		add(new ArrowBlocker());
		add(new AutoEat());
		add(new AutoMine());
		add(new AutoMount());
		add(new AutoRespawn());
		add(new CameraClip());
		add(new ChorusDelay());
		add(new ChorusPredict());
		add(new ForgeBypass());
		add(new FreeCamBypass());
		add(new FreeCamRewrite());
		add(Interaction.INSTANCE);
		add(new Octopus());
		add(new PacketFeatures());
		add(new PearlBypass());
		add(new Refill());
		add(new Replenish());
		add(new RotateModifier());
		add(new SilentXp());
		add(new Velocity());
		add(new YawLock());
		add(new YawStep());
		//exploit
		add(new AntiLogger());
		add(new AntiVanish());
		add(new BookFormatModule());
		add(new BowExploit());
		add(new ChorusBypass());
		add(new CowDupe());
		add(new FakePearl());
		add(new FastMove());
		add(new Ghost());
		//add(new HandMine()); deprecated
		add(new HandMineRewrite());
		add(new LiquidInteractRewrite());
		add(new LogoutSpots());
		add(new MiddleClick());
		add(new NewChunks());
//		add(new NoFallExploit());
		add(new NoGlitchBlocks());
		add(new PacketEat());
		add(new PacketFly());
		add(new PacketMine());
		add(new PacketMineRewrite3());
		//add(new PreciseStrongholdFinder());
		add(new SilentClose());
		add(new SoundCoordLogger());
		add(new Teleport());
		add(new TickShift());
		add(new TickShiftTeleport());
		add(new TraceTeleport());
		//misc
		add(new AntiRegear());
		add(new AntiSpammer());
		add(new AutoEZ());
		add(new AutoLog());
		add(new BurrowCounter());
		add(new ChatModifier());
		//add(new DDOSModule());
		add(new FakePlayer());
//		add(new FreeLook());
		add(new Funny());
		add(new HotbarScroller());
		add(new ItemRenamer());
		add(new MurderFinder());
		add(new NameProtect());
		add(new Optimizer());
		//add(new PacketDelay());
		add(new PortalsModifier());
		add(new SkyBlockFeatures());
		add(new SlotMapper());
		add(new Spammer());
		add(new TotemPopCounter());
		add(new Translate());
		add(new VisualRange());
		add(new Weather());
		add(new XCarry());

		//modules = modules.stream().filter(module -> {
		//	if(module.getClass().getAnnotation(OnlyDebug.class) == null)
		//		return true;
		//	return Kisman.MODULE_DEBUG;
		//}).collect(Collectors.toList());
	}

	private void add(Module module) {
		modules.add(module);
		Kisman.LOGGER.info("Registering " + module.getName() + " module!");

		if(module.getClass().isAnnotationPresent(Targetable.class)) {
			targetableModules.add(module);
		}
	}
	
	public Module getModule(String name) {
		for (Module m : this.modules) if (m.getName().equalsIgnoreCase(name) || m.displayName.equalsIgnoreCase(name)) return m;
		return null;
	}

	public Module getModule(String name, boolean scripts, boolean plugins) {
		for (Module m : this.modules) if ((plugins || !(m instanceof ModulePlugin)) && (scripts || !(m instanceof ModuleScript)) && (m.getName().equalsIgnoreCase(name) || m.displayName.equalsIgnoreCase(name))) return m;
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

	/*@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		for(Module m : modules) if(m.isToggled()) {
			try {
				m.update();
			} catch(Exception e) {
				if(mc.player != null && mc.world != null) ChatUtility.error().printClientModuleMessage("Received " + e.getClass().getSimpleName() + " from update method. Disabling!", m);
				Kisman.LOGGER.error("Received " + e.getClass().getSimpleName() + " from update method from " + m.getName() + ". Disabling!", e);
				e.printStackTrace();
				m.setToggled(false);
			}
		}
	}*/

	public void key(char typedChar, int key, Module mod) {
		if(mod.isToggled()) {
			mod.key();
			mod.key(key);
			mod.key(typedChar, key);
		}
	}
}
