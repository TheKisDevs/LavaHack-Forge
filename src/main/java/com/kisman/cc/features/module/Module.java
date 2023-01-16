package com.kisman.cc.features.module;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.hud.modules.arraylist.IArrayListElement;
import com.kisman.cc.features.module.client.Config;
import com.kisman.cc.features.subsystem.subsystems.Target;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.SettingsManager;
import com.kisman.cc.settings.types.SettingArray;
import com.kisman.cc.settings.types.SettingEnum;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.settings.util.MultiThreaddableModulePattern;
import com.kisman.cc.settings.util.RenderingRewritePattern;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import com.kisman.cc.util.settings.SettingLoader;
import me.zero.alpine.listener.Listenable;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class Module implements IBindable, Listenable, IArrayListElement {
	protected static Minecraft mc = Minecraft.getMinecraft();
	protected static SettingsManager setmgr;

	private String name, description, displayInfo;
	private int key;
	public int mouse = -1;
	public BindType bindType = BindType.Keyboard;
	private final Category category;
	public boolean toggled;
	public boolean toggleable = true;
	public boolean subscribes = true;
	public boolean visible = true;
	public boolean hold = false;
	public boolean block = false;
	private Supplier<String> displayInfoSupplier = null;
	public Supplier<EntityPlayer> enemySupplier = null;

	public boolean sendToggleMessages = true;

	public ArrayList<RenderingRewritePattern> renderPatterns = new ArrayList<>();

	public float xCoeff = 1;
	public float xCoeffPrev = 1;

	public Module(String name, Category category) {this(name, "", category, 0, true);}
	public Module(String name, Category category, boolean subscribes) {this(name, "", category, 0, subscribes);}
	public Module(String name, String description, Category category) {this(name, description, category, 0, true);}

	public Module(String name, String description, Category category, int key, boolean subscribes) {
		this.name = name;
		this.description = description;
		this.displayInfo = "";
		this.key = key;
		this.category = getClass().isAnnotationPresent(WorkInProgress.class) ? Category.WIP : category;
		this.toggled = false;
		this.subscribes = subscribes;

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
			Kisman.LOGGER.error("Cant create enemy supplier of " + name + " module! The module will be ignored in emeny manager!");
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
		if (sendToggleMessages && Kisman.instance.init && Config.instance.notification.getValBoolean()) ChatUtility.message().printClientMessage(TextFormatting.GRAY + "Module " + (isToggled() ? TextFormatting.GREEN : TextFormatting.RED) + getName() + TextFormatting.GRAY + " has been " + (isToggled() ? "enabled" : "disabled") + "!");
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
		setmgr.rSetting(group);
		return group;
	}

	public <T extends Enum<?>> SettingEnum<T> register(SettingEnum<T> setting) {
		return (SettingEnum<T>) register((Setting) setting);
	}

	public <T> SettingArray<T> register(SettingArray<T> setting) {
		return (SettingArray<T>) register((Setting) setting);
	}

	private boolean isBeta0(){
		return getClass().getAnnotation(Beta.class) != null;
	}

	private boolean isAddon0() {
		return getClass().getAnnotation(Addon.class) != null;
	}

	private boolean isPingBypassModule0() {
		return getClass().getAnnotation(PingBypassModule.class) != null;
	}

	public boolean isPingBypassModule() {
		return isPingBypassModule0();
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

	public void onEnable() {}

	public void onDisable() {}

	public String getName() {return this.name;}
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
	public boolean isBeta() {return isBeta0();}
	public boolean isAddon() {return isAddon0();}
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

	protected void dontSendToggleMessages() {
		sendToggleMessages = false;
	}

	@Override
	public float getXCoeff() {
		return xCoeff;
	}

	@Override
	public void setXCoeff(float v) {
		this.xCoeff = v;
	}

	@Override
	public float getXCoeffPrev() {
		return xCoeffPrev;
	}

	@Override
	public void setXCoeffPrev(float v) {
		this.xCoeffPrev = v;
	}
}
