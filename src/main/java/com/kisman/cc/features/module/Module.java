package com.kisman.cc.features.module;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.module.client.Config;
import com.kisman.cc.features.subsystem.subsystems.Target;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.SettingsManager;
import com.kisman.cc.settings.types.SettingArray;
import com.kisman.cc.settings.types.SettingEnum;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.settings.SettingsList;
import com.kisman.cc.settings.util.MultiThreaddableModulePattern;
import com.kisman.cc.settings.util.RenderingRewritePattern;
import com.kisman.cc.util.StringUtils;
import com.kisman.cc.util.TimerUtils;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import com.kisman.cc.util.client.DisplayableFeature;
import com.kisman.cc.util.enums.BindType;
import com.kisman.cc.util.settings.SettingLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;
import the.kis.devs.api.features.module.ModuleAPI;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class Module extends DisplayableFeature {
	protected static Minecraft mc = Minecraft.getMinecraft();
	protected static SettingsManager setmgr;

	public Setting visibleSetting = new Setting("Visible", this, true).onChange(setting -> { visible = setting.getValBoolean(); });
	public Setting bindModeSetting = new Setting("Bind Mode", this, "Release", Arrays.asList("Release", "Hold")).setTitle("Bind").onChange(setting -> { hold = setting.checkValString("Hold"); });

	private String name;
	private String description;
	public String displayName;
	private String displayInfo;
	public int key;
	public int mouse = -1;
	public BindType bindType = BindType.Keyboard;
	public Category category;
	public boolean toggled;
	public boolean toggleable = true;
	public boolean subscribes = true;
	public boolean visible = true;
	public boolean hold = false;
	public boolean block = false;
	private Supplier<String> displayInfoSupplier = null;
	public Supplier<EntityPlayer> enemySupplier = null;

	public boolean sendToggleMessages = true;
	public int moduleId;

	public ArrayList<RenderingRewritePattern> renderPatterns = new ArrayList<>();
	public ArrayList<Module> submodules = new ArrayList<>();

	public boolean beta0 = false;
	public boolean wip0 = false;
	public boolean pb0 = false;
	public boolean debug0 = false;
	public boolean submodule = false;

	public Module() {
		if(!getClass().isAnnotationPresent(ModuleInfo.class)) {
			Kisman.LOGGER.error("Missing ModuleInfo annotation!");

			constructor("MISSING MODULEINFO ANNOTATION", "MISSING MODULEINFO ANNOTATION", Category.CLIENT, Keyboard.KEY_NONE, true);
		} else {
			ModuleInfo info = getClass().getAnnotation(ModuleInfo.class);

			constructor(info.name(), info.desc(), info.category(), info.key(), true);

			String displayName0 = info.display();

			if(!displayName0.isEmpty()) displayName = displayName0;
			if(info.toggled()) enable();

			toggled = info.toggled();
			toggleable = info.toggleable();

			hold = info.hold();

			beta0 = info.beta();
			wip0 = info.wip();
			pb0 = info.pingbypass();
			debug0 = info.debug();

			submodule = info.submodule();

			visible = toggleable;

			if(debug0) category = Category.DEBUG;
			if(wip0) category = Category.WIP;

			if(info.modules().length > 0) {
				for(Class<? extends Module> clazz : info.modules()) {
					try {
						Module submodule = clazz.getConstructor().newInstance();

						submodule.category = category;
						submodules.add(submodule);
					} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
						Kisman.LOGGER.error("Cant create new instance of submodule of " + name + " module!", e, e.getCause());
					}
				}
			}
		}
	}

	public Module(String name, Category category) {this(name, "", category, 0, true);}
	public Module(String name, Category category, boolean subscribes) {this(name, "", category, 0, subscribes);}
	public Module(String name, String description, Category category) {this(name, description, category, 0, true);}

	public Module(String name, String description, Category category, int key, boolean subscribes) {
		constructor(name, description, category, key, subscribes);
	}

	private void constructor(String name, String description, Category category, int key, boolean subscribes) {
		this.name = name;
		this.description = description;
		this.displayName = name;
		this.displayInfo = "";
		this.key = key;
		this.category = category;
		this.toggled = false;
		this.subscribes = subscribes;
		this.moduleId = StringUtils.stringToInt(name);

		setmgr = Kisman.instance.settingsManager;

		SettingLoader.load(this);

		for(Field field : getClass().getDeclaredFields()) {
			if(field.isAnnotationPresent(ModuleInstance.class)) {
				try {
					field.set(null, this);
				} catch (IllegalAccessException ignored) {
					Kisman.LOGGER.error("Cant create instance of " + name + " module! it may will give crash in the future");
				}
			} else if(field.isAnnotationPresent(Target.class)) processTargetField(field);
		}

		if(enemySupplier == null) for(Field field : getClass().getFields()) if(field.isAnnotationPresent(Target.class)) if(processTargetField(field)) break;
		if(enemySupplier == null) enemySupplier = () -> null;
	}

	private boolean processTargetField(Field field) {
		try {
			field.get(this);
		} catch(IllegalAccessException ignored) {
			Kisman.LOGGER.error("Cant create enemy supplier of " + name + " module! The module will be ignored in enemy manager!");
			return true;
		}

		enemySupplier = () -> {
			try {
				return (EntityPlayer) field.get(this);
			} catch (IllegalAccessException ignored) {
				//
			}

			return null;
		};

		return false;
	}

	private void printToggleMessage() {
		if (sendToggleMessages && Kisman.instance.init && Config.instance.notification.getValBoolean())
			ChatUtility.message().printClientMessage(TextFormatting.GRAY
					+ "Module "
					+ (isToggled() ? TextFormatting.GREEN : TextFormatting.RED)
					+ displayName
					+ TextFormatting.GRAY
					+ " has been "
					+ (isToggled() ? "enabled" : "disabled")
					+ "!", moduleId);
	}

	public void setToggled(boolean toggled) {
		if(block) return;
		printToggleMessage();
		if (toggled) enable();
		else disable();
	}

	public void toggle() {
		if(block) return;
		if (!toggled) enable();
		else disable();
		printToggleMessage();
	}

	public Setting register(Setting set) {
		setmgr.rSetting(set);
		return set;
	}

	public SettingGroup register(SettingGroup group) {
		return (SettingGroup) register((Setting) group);
	}

	public <T extends Enum<?>> SettingEnum<T> register(SettingEnum<T> setting) {
		return (SettingEnum<T>) register((Setting) setting);
	}

	public <T> SettingArray<T> register(SettingArray<T> setting) {
		return (SettingArray<T>) register((Setting) setting);
	}

	public SettingsList register(SettingsList list) {
		for(Setting setting : list.settings.values()) register(setting);

		return list;
	}

	public boolean isPingBypassModule() {
		return pb0;
	}

	public String getDescription() {return description;}
	public void setDescription(String description) {this.description = description;}
	public int getKey() {return key;}
	public void setKey(int key) {this.key = key;}
	public boolean isToggled() {return toggled;}

	public final void enable() {
		if(toggled) return;

		toggled = true;

		boolean flag = false;

		try {
			onEnable();
		} catch(Exception e) {
			if(mc.player != null && mc.world != null) ChatUtility.error().printClientModuleMessage("Received " + e.getClass().getSimpleName() + " in enable method. Disabling!");
			Kisman.LOGGER.error("Received " + e.getClass().getSimpleName() + " from enable method from " + getName() + ". Disabling!", e);
			e.printStackTrace();
			flag = true;
		}

		if(subscribes) MinecraftForge.EVENT_BUS.register(this);
		Subscribes subscribes = this.getClass().getAnnotation(Subscribes.class);
		if(subscribes == null) return;
		SubscribeMode.register(subscribes, this);

		if(flag) setToggled(false);
	}

	public final void disable() {
		if(!toggled) return;

		toggled = false;

		try {
			onDisable();
		} catch(Exception e) {
			if(mc.player != null && mc.world != null) ChatUtility.error().printClientModuleMessage("Received " + e.getClass().getSimpleName() + " in disable method.");
			Kisman.LOGGER.error("Received " + e.getClass().getSimpleName() + " from disable method from " + getName() + ". Disabling!", e);
			e.printStackTrace();
		}

		if(subscribes) MinecraftForge.EVENT_BUS.unregister(this);
		Subscribes subscribes = this.getClass().getAnnotation(Subscribes.class);
		if(subscribes == null) return;
		SubscribeMode.unregister(subscribes, this);
	}

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		try {
			update();
		} catch(Exception e) {
			if(mc.player != null && mc.world != null) ChatUtility.error().printClientModuleMessage("Received " + e.getClass().getSimpleName() + " from update method. Disabling!");
			Kisman.LOGGER.error("Received " + e.getClass().getSimpleName() + " from update method from " + getName() + ". Disabling!", e);
			e.printStackTrace();
			setToggled(false);
		}
	}

	public void onEnable() {}

	public void onDisable() {}

	public String getName() {return this.name;}
	public void setName(String name) {this.name = name;}
	public Category getCategory() {return this.category;}
	public String getDisplayInfo() {return displayInfoSupplier == null ? displayInfo : displayInfoSupplier.get();}
	public void setDisplayInfo(String displayInfo) {this.displayInfo = displayInfo;}
	public void setDisplayInfo(Supplier<String> displayInfoSupplier) {this.displayInfoSupplier = displayInfoSupplier;}
	public void update() { }
	public void thread() { }
	public void render() { }
	public void key() {}
	public void key(int key) {}
	public void key(char typedChar, int key) {}
	@Override public String toString() {return getName();}
	public boolean isVisible() {return visible;}
	public boolean isBeta() {return beta0;}
	public boolean isAddon() {return this instanceof ModuleAPI;}
	@Override public @NotNull BindType getType() {return bindType;}
	@Override public void setType(@NotNull BindType type) {this.bindType = type;}
	@Override public boolean isHold() {return hold;}
	@Override public void setHold(boolean hold) {this.hold = hold;}
	@Override public int getKeyboardKey() {return key;}
	@Override public void setKeyboardKey(int key) {this.key = key;}
	@Override public int getMouseButton() {return mouse;}
	@Override public void setMouseButton(int button) {this.mouse = button;}
	@Override public @NotNull String getButtonName() {return "Bind";}

	public MultiThreaddableModulePattern threads() {
		return new MultiThreaddableModulePattern(this).preInit().init();
	}

	public TimerUtils timer() {
		return new TimerUtils();
	}

	protected void dontSendToggleMessages() {
		sendToggleMessages = false;
	}

	@Override
	public void onInputEvent() {
		toggle();
	}
}