package com.kisman.cc.features.module;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.client.loadingscreen.progressbar.EventProgressBar;
import com.kisman.cc.features.module.Debug.*;
import com.kisman.cc.features.module.client.*;
import com.kisman.cc.features.module.combat.*;
import com.kisman.cc.features.module.exploit.*;
import com.kisman.cc.features.module.misc.*;
import com.kisman.cc.features.module.misc.announcer.TraceTeleport;
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
		add(new DynamicBlocksTest());
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
		add(new ModuleInfoTest());
		add(new ModuleInstancingKt());
		add(new MoveInspector());
//		add(new NoMove());
		add(new ObjectMouseOver());
		add(new PacketIDGetterTest());
		add(new RectTest());
		add(new RotationTest());
//		add(new ScaffoldTest());
//		add(new ScaffoldTest2());
		add(new ScreenShaders());
		add(new SmoothRenderer());
		add(new SubModuleTest());
		add(SwingTest.INSTANCE);
//		add(new TextFieldTest());
//		add(new TowerTest());
//		add(new TracerTest());
		add(new Triangulation());

		//combat
//		add(new AutoCrystal());
		add(new AntiBot());
//		add(new AntiBow());
		add(new AntiCity());
//		add(new AntiTrap());
		add(new AutoAnchor());
		add(new AutoAnvil());
//		add(new AutoArmor());
		add(AutoCrystalPvP.INSTANCE);
		add(new AutoObsidian());
		add(new AutoQuiver());
		add(new AutoRer());
		add(new AutoTrapRewrite());
		add(new Avoid());
		add(new Blocker());
		add(new BlockerRewrite());
//		add(new BowAimBot());
//		add(new BreakAlert());
		add(new Burrow2());
		add(new CevBreaker());
		add(new CityBoss());
//		add(new Criticals());
		add(new CrystalFiller());
		add(new CrystalPvPHelper2());
		add(new DamageIncreaser());
		add(new FireworkAura());
//		add(new Flatten());
		add(new FlattenRewrite());
		add(new HoleFillerRewrite());
		add(new HoleKicker());
		add(new KillAuraRewrite());
		add(new AutoCrystalRewrite());
//		add(new OffHand());
		add(new PistonAura());
		add(new PistonTest());
		add(new Prison());
		add(new Robot());
		add(new SelfTrapRewrite());
//		add(new Surround());
		add(new SurroundRewrite());
//		add(new TrapDoorBurrow());
		//client
		add(new AntiLogger());
		add(new Baritone());
		add(new BetterScreenshot());
		add(new Cape());
		add(Changer.INSTANCE);
		add(new ChunkAnimator());
		add(new ClientFixer());
		add(new Config());
		add(CustomFontModule.instance);
//		add(new DevelopmentHelper());
		add(new DiscordRPCModule());
		add(new ForgeBypass());
		add(FriendHighlight.INSTANCE);
		add(new GuiModule());
		add(new GuiModifier());
		add(new MainMenuModule());
		add(NoSpoof.INSTANCE);
		add(new Optimizer());
		add(new PauseBaritone());
		add(PingBypass.INSTANCE);
		add(new Printer());
		add(new SchematicaModule());
		add(new ViaForgeModule());
		//render
//		add(new BlockESP());
		add(new BlockHighlight());
		//add(new BlockLiner());
		add(new Breadcrumbs());
		add(new BreakHighlight());
		add(CharmsRewrite.INSTANCE);
//		add(new ContainerModifier());
		add(new CrystalModifier());
		add(new CrystalSafeBlocks());
		add(new DamageESP());
		add(new EnchantGlint());
		add(new ESP());
//		add(new EntityESPRewrite());
//		add(new FovModifier());
//		add(new HoleESP());
//		add(new HoleESPRewrite());
//		add(new HoleESPRewrite2());
		add(new HotbarModifier());
		add(new InfiniteRender());
//		add(new ItemESPRewrite());
		add(new JumpCircle());
//		add(new LogoutSpots());
		add(new MotionBlur());
		add(new NameTags());
//		add(new NoBobbing());
//		add(new NoCrosshair());
		add(new NoRender());
		add(new PearlTracer());
		add(new PlayerLook());
		add(new ScreenTint());
		add(new SelfCityESP());
		add(new ShaderCharms());
		add(new Shaders());
		add(new SmallShield());
		add(new SmartCityESP());
		add(new SpawnsESP());
//		add(new StorageESP());
		add(new SwingAnimation());
		add(new SwingProgress());
		add(new Tracers2());
//		add(new Trails());
//		add(new Trajectories());
//		add(new TrajectoriesRewrite());
		add(new ViewModel());
		//movement
		add(new AirJump());
		add(new Anchor());
		add(new CornerClip());
		add(new Flight());
		add(new HoleSnap());
		add(new Jesus());
		add(new MoveModifier());
		add(new NoChunkUnload());
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
		add(new SpeedRewrite2());
//		add(SpeedRewrite.INSTANCE);
		add(new Spider());
//		add(new Strafe());
		//player
		add(new AirPlace());
		add(AntiDesync.INSTANCE);
		add(new AntiHunger());
		add(new ArrowBlocker());
//		add(new AutoArmor());
		add(new AutoEat());
		add(new AutoEatRewrite());
//		add(new AutoMine());
		add(new AutoMount());
		add(new AutoRespawn());
		add(new CameraClip());
		add(new ChorusDelay());
		add(new ChorusPredict());
		add(new FreeCamRewrite2());
		add(Interaction.INSTANCE);
		add(new InventoryModule());
		add(new Octopus());
		add(new PacketFeatures());
		add(new PearlBypass());
//		add(new Refill());
//		add(new Replenish());
		add(new RotateModifier());
		add(new SilentXp());
		add(new Velocity());
//		add(new YawLock());
//		add(new YawStep());
		//exploit
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
		add(new MiddleClick());
		add(new NewChunks());
//		add(new NoFallExploit());
		add(new NoGlitchBlocks());
		add(new PacketEat());
		add(new PacketMineRewrite3());
		//add(new PreciseStrongholdFinder());
		add(new SilentClose());
		add(new SoundCoordLogger());
		add(new Teleport());
		add(new TickShift());
		add(new TickShiftTeleport());
		add(new TraceTeleport());
		//misc
		add(new Announcer());
		add(new AntiRegear());
		add(new AntiSpammer());
		add(new AutoEZ());
		add(new AutoLog());
//		add(new BurrowCounter());
		add(new ChatModifier());
		//add(new DDOSModule());
		add(new FakePlayer());
//		add(new FreeLook());
		add(new Funny());
		add(new HotbarScroller());
		add(new ItemRenamer());
		add(new MurderFinder());
		add(new NameProtect());
		//add(new PacketDelay());
		add(new PortalsModifier());
		add(new SlotMapper());
		add(new Spammer());
//		add(new TotemPopCounterRewrite());
		add(new Translate());
//		add(new VisualRange());
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

		if(module.getClass().isAnnotationPresent(Targetable.class) || (module.getClass().isAnnotationPresent(ModuleInfo.class) && module.getClass().getAnnotation(ModuleInfo.class).targetable().real())) {
			targetableModules.add(module);
		}

		if(!module.submodules.isEmpty()) {
			for(Module submodule : module.submodules) {
				add(submodule);
			}
		}
	}
	
	public Module getModule(String name) {
		for (Module m : this.modules) if (m.getName().equalsIgnoreCase(name) || m.displayName.equalsIgnoreCase(name)) return m;
		return null;
	}

	public Module getModule(String name, boolean scripts, boolean plugins) {
		for (Module m : this.modules) if ((plugins || !(m instanceof ModulePlugin)) && (m.getName().equalsIgnoreCase(name) || m.displayName.equalsIgnoreCase(name))) return m;
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

	public void key(char typedChar, int key, Module mod) {
		if(mod.isToggled()) {
			mod.key();
			mod.key(key);
			mod.key(typedChar, key);
		}
	}
}
