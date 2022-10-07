package com.kisman.cc.settings;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.client.settings.EventSettingChange;
import com.kisman.cc.features.Binder;
import com.kisman.cc.features.catlua.lua.settings.LuaSetting;
import com.kisman.cc.features.module.BindType;
import com.kisman.cc.features.module.IBindable;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.types.number.NumberType;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.ColourUtilKt;
import com.kisman.cc.util.UtilityKt;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;

import java.util.*;
import java.util.function.Supplier;

public class Setting implements IBindable {
	public Supplier<Boolean> visibleSupplier = () -> true;

	public boolean haveDisplayInfo = false;
	public Supplier<String> displayInfoSupplier = () -> "";

	private Colour colour;

	private Entity entity;

	private int index = 0;
	private int key = Keyboard.KEY_NONE;
	public int mouse = -1;
	public BindType bindType = BindType.Keyboard;
	public boolean hold = false;
	
	private String name;
	public Module parent;
	public Setting parent_ = null;
	public String mode = "";

	private String title;

	private String sval;
	private String dString;
	private ArrayList<String> options;
	private Enum optionEnum;
	
	private boolean bval;
	private boolean rainbow;
	private boolean onlyOneWord;
	private boolean onlyNumbers;
	private boolean enumCombo = false;

	private double dval;
	private double min;
	private double max;

	private ItemStack[] items;

	private int x1, y1, x2, y2;

	private float red, green, blue, alpha;

	private boolean onlyint = false;

	private NumberType numberType = NumberType.DECIMAL;

	public final HashMap<String, Binder> binders = new HashMap<>();

	public Setting(String type) {mode = type;}

	public Setting(String name, Module parent, int key) {
		this.name = name;
		this.parent = parent;
		this.title = name;
		this.key = key;
		this.mode = "Bind";
	}

	public Setting(String name, Module parent, String sval, String dString, boolean opening) {
		this.name = name;
		this.parent = parent;
		this.title = name;
		this.sval = sval;
		this.dString = dString;
		this.onlyOneWord = false;
		this.onlyNumbers = false;
		this.mode = "String";
	}

	public Setting(String name, Module parent, String sval, ArrayList<String> options){
		this.name = name;
		this.parent = parent;
		this.title = name;
		this.sval = sval;
		this.options = options;
		this.optionEnum = null;
		this.mode = "Combo";
		setupBinders(options);
	}

	public Setting(String name, Module parent, String sval, List<String> options){
		this.name = name;
		this.parent = parent;
		this.title = name;
		this.sval = sval;
		this.options = new ArrayList<>(options);
		this.optionEnum = null;
		this.mode = "Combo";
		setupBinders(options);
	}

	public Setting(String name, Module parent, Enum<?> options){
		this.name = name;
		this.parent = parent;
		this.title = name;
		this.sval = options.name();
		this.options = null;
		this.optionEnum = options;
		this.enumCombo = true;
		this.mode = "Combo";
		setupBinders(
				Arrays.asList(Arrays.stream(options.getClass().getEnumConstants()).map(Enum::toString).toArray(String[]::new))
		);
	}
	
	public Setting(String name, Module parent, boolean bval){
		this.name = name;
		this.parent = parent;
		this.title = name;
		this.bval = bval;
		this.mode = "Check";
	}

	public Setting(String name, Module parent, double dval, double min, double max, NumberType numberType){
		this.name = name;
		this.parent = parent;
		this.title = name;
		this.dval = dval;
		this.min = min;
		this.max = max;
		this.onlyint = numberType.equals(NumberType.INTEGER);
		this.mode = "Slider";
		this.numberType = numberType;
	}

	public Setting(String name, Module parent, double dval, double min, double max, boolean onlyint){
		this.name = name;
		this.parent = parent;
		this.title = name;
		this.dval = dval;
		this.min = min;
		this.max = max;
		this.onlyint = onlyint;
		this.mode = "Slider";
		this.numberType = onlyint ? NumberType.INTEGER : NumberType.DECIMAL;
	}

	public Setting(String name, Module parent, String title, Colour colour) {
		this.name = name;
		this.parent = parent;
		this.title = title;
		this.colour = colour;
		this.mode = "ColorPicker";
	}

	public Setting(String name, Module parent, Colour colour) {
		this(name, parent, name, colour);
	}

	public Setting(String name, Module parent, String title, Entity entity) {
		this.name = name;
		this.parent = parent;
		this.title = title;
		this.entity = entity;
		this.mode = "Preview";
	}

	public Setting(String name, Module parent, String title, ItemStack[] items) {
		this.name = name;
		this.parent = parent;
		this.title = title;
		this.items = items;
		this.mode = "Items";
	}

	public Setting(String name, Module parent) {
		this(name, parent, name);
	}

	public Setting(String name, Module parent, String title) {
		this.name = name;
		this.parent = parent;
		this.title = title;
	}

	public Setting setDisplayInfo(Supplier<String> displayInfoSupplier) {
		this.displayInfoSupplier = displayInfoSupplier;
		this.haveDisplayInfo = true;
		return this;
	}

	private void setupBinders(List<String> options) {
		for(String option : options) {
			binders.put(
					option,
					new Binder(
							option,
							BindType.Keyboard,
							-1,
							-1,
							false
					)
			);
		}
	}

	public Setting setDisplayInfo(String displayInfo) {
		return setDisplayInfo(() -> displayInfo);
	}

	public String getDisplayInfo() {
		return haveDisplayInfo ? displayInfoSupplier.get() : "";
	}

	public Enum<?> getEnumByName() {
		if(optionEnum == null) return null;
		Enum<?> enumVal = optionEnum;
		String[] values = Arrays.stream(enumVal.getClass().getEnumConstants()).map(Enum::toString).toArray(String[]::new);
		return Enum.valueOf(enumVal.getClass(), values[index]);
	}

	@Override
	public boolean equals(Object obj) {
		if(isCombo()) return sval.equals(obj);
		return false;
	}

	@Override
	public String toString() {
		if(isCombo()) return getValString();
		if(isCheck()) return String.valueOf(getValBoolean());
		if(isSlider()) return String.valueOf(onlyint ? getValInt() : getValDouble());
		if(isString()) return getValString();
		if(isColorPicker()) return ColourUtilKt.Companion.toConfig(colour);
		return super.toString();
	}

	public boolean isNoneKey() {return key == Keyboard.KEY_NONE;}

	public boolean checkValString(String str) {
		return sval.equalsIgnoreCase(str);
	}

	public boolean checkValString(Enum<?> enm) {
		return checkValString(enm.toString());
	}

	public boolean isVisible() {
		return visibleSupplier.get();
	}
	
	public Setting setVisible(Setting setting) {
		visibleSupplier = setting::getValBoolean;

		return this;
	}

	public Setting setVisible(Supplier<Boolean> suppliner) {
		visibleSupplier = suppliner;

		return this;
	}

	public Setting setVisible(boolean visible) {
		visibleSupplier = () -> visible;
		return this;
	}

	public String[] getStringValues() {
		if(!enumCombo) return options.toArray(new String[options.size()]);
		else return Arrays.stream(optionEnum.getClass().getEnumConstants()).map(Enum::toString).toArray(String[]::new);
	}

	public ArrayList<String> getStringArray() {
		return new ArrayList<>(Arrays.asList(getStringValues()));
	}

	public String getStringFromIndex(int index) {
		if(index != -1) return getStringValues()[index];
		else return "";
	}

	public int getSelectedIndex() {
		String[] modes = getStringValues();
		int object = 0;

		for(int i = 0; i < modes.length; i++) {
			String mode = modes[i];

			if(mode.equalsIgnoreCase(sval)) object = i;
		}

		return object;
	}

	public boolean isOnlyint() {
		return onlyint;
	}


	public NumberType getNumberType() {
		return numberType;
	}

	public void setNumberType(NumberType numberType) {
		this.numberType = numberType;
	}

	public float getRed() {
		return red;
	}

	public void setRed(float red) {
		this.red = red;
	}

	public float getGreen() {
		return green;
	}

	public void setGreen(float green) {
		this.green = green;
	}

	public float getBlue() {
		return blue;
	}

	public void setBlue(float blue) {
		this.blue = blue;
	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public boolean isOnlyNumbers() {
		return onlyNumbers;
	}

	public Setting setOnlyNumbers(boolean onlyNumbers) {
		this.onlyNumbers = onlyNumbers;
		return this;
	}

	public ItemStack[] getItems() {
		return items;
	}

	public void setItems(ItemStack[] items) {
		this.items = items;
	}

	public Entity getEntity() {
		return entity;
	}

	public int getValInt() {
		return (int) this.dval;
	}

	public boolean isOnlyOneWord() {
		return onlyOneWord;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public Colour getColour() {
		return colour;
	}

	public void setColour(Colour colour) {
		this.colour = colour;
		EventSettingChange.Any event = new EventSettingChange.Any(this);
		Kisman.EVENT_BUS.post(event);
	}

	public Enum<?> getValEnum() {
		try {
			return optionEnum.valueOf(optionEnum.getClass(), sval);
		} catch(Exception ignored) {
			return optionEnum;
		}
	}

	public void setValEnum(Enum<?> enum_) {
		sval = enum_.name();
	}

	public String getdString() {
		return dString;
	}

	public int getX1() {
		return this.x1;
	}
	
	public int getY1() {
		return this.y1;
	}
	
	public int getX2() {
		return this.x2;
	}
	
	public int getY2() {
		return this.y2;
	}

	public void setX1(int num) {
		this.x1 = num;
	}

	public void setY1(int num) {
		this.y1 = num;
	}

	public void setX2(int num) {
		this.x2 = num;
	}

	public void setY2(int num) {
		this.y2 = num;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getTitle() {
		return title;
	}

	public Setting setTitle(String title) {
		this.title = title;
		return this;
	}

	@Override
	public @NotNull String getButtonName(){
		return isBind() ? name : "Bind";
	}

	public String getName(){
		return name;
	}

	public Setting setName(String name) {
		this.name = name;
		return this;
	}
	
	public Module getParentMod(){
		return parent;
	}
	
	public String getValString(){
		return this.sval;
	}
	
	public Setting setValString(String in){
		this.sval = in;
		EventSettingChange.Any event = new EventSettingChange.Any(this);
		Kisman.EVENT_BUS.post(event);
		return this;
	}
	
	public ArrayList<String> getOptions(){
		return this.options;
	}

	public Setting setOptions(String... options) {
		this.options = new ArrayList<>(Arrays.asList(options));
		return this;
	}

	public Setting setOptions(List<String> options) {
		this.options = new ArrayList<>(options);
		return this;
	}

	//#Lua
	//TODO: доделать
	public Setting build(Module module) {
		Kisman.instance.settingsManager.rSetting(this);
		return this;
	}
	
	public boolean getValBoolean(){
		return this.bval;
	}
	
	public Setting setValBoolean(boolean in){
		this.bval = in;
		EventSettingChange.Any event = new EventSettingChange.Any(this);
		Kisman.EVENT_BUS.post(event);
		return this;
	}
	
	public double getValDouble(){
		if(this.onlyint){
			this.dval = (int) dval;
		}
		return this.dval;
	}

	public float getValFloat() {
		if(onlyint) {
			dval = (int) dval;
		}

		return (float) dval;
	}

	public long getValLong() {
		if(onlyint) {
			dval = (int) dval;
		}

		return (long) dval;
	}

	public Setting setValDouble(double in){
		this.dval = in;
		EventSettingChange.Any event = new EventSettingChange.Any(this);
		Kisman.EVENT_BUS.post(event);
		return this;
	}
	
	public double getMin(){
		return this.min;
	}
	
	public double getMax(){
		return this.max;
	}

	public Setting setMin(double min) {
		this.min = min;
		return this;
	}

	public Setting setMax(double max) {
		this.max = max;
		return this;
	}

	public Setting setType(String type) {
		numberType = LuaSetting.getNumberTypeByName(type);
		return this;
	}

	public boolean isRainbow() {
		return this.rainbow;
	}

	public void setRainbow(boolean rainbow) {
		this.rainbow = rainbow;
	}

	public boolean isItems() { return mode.equalsIgnoreCase("Items"); }

	public boolean isPreview() { return mode.equalsIgnoreCase("Preview"); }

	public boolean isBind() { return mode.equalsIgnoreCase("Bind"); }

	public boolean isCategory() { return this.mode.equalsIgnoreCase("Category"); }

	public boolean isGroup() { return this.mode.equalsIgnoreCase("Group"); }

	public boolean isString() { return this.mode.equalsIgnoreCase("String"); }

	public boolean isVoid() { return this.mode.equalsIgnoreCase("Void"); }
	
	public boolean isCombo(){
		return this.mode.equalsIgnoreCase("Combo");
	}
	
	public boolean isCheck(){
		return this.mode.equalsIgnoreCase("Check");
	}

	public boolean isSlider(){
		return this.mode.equalsIgnoreCase("Slider");
	}

	public boolean isLine() {
		return this.mode.equalsIgnoreCase("Line");
	}

	public boolean isColorPicker() {
		return this.mode.equalsIgnoreCase("ColorPicker");
	}

	public boolean onlyInt(){
		return this.onlyint;
	}

	@NotNull @Override public BindType getType() {return bindType;}
	@Override public void setType(@NotNull BindType type) {this.bindType = type;}
	@Override public boolean isHold() {return hold;}
	@Override public void setHold(boolean hold) {this.hold = hold;}
	@Override public int getKeyboardKey() {return key;}
	@Override public void setKeyboardKey(int key) {this.key = key;}
	@Override public int getMouseButton() {return mouse;}
	@Override public void setMouseButton(int button) {this.mouse = button;}

	public Supplier<String> getSupplierString() {return () -> sval;}
	public Supplier<Integer> getSupplierInt() {return this::getValInt;}
	public Supplier<Double> getSupplierDouble() {return () -> dval;}
	public Supplier<Float> getSupplierFloat() {return this::getValFloat;}
	public Supplier<Long> getSupplierLong() {return this::getValLong;}
	public Supplier<Enum<?>> getSupplierEnum() {return this::getValEnum;}
	public Supplier<Boolean> getSupplierBoolean() {return () -> bval;}

	private ArrayList<String> doIterationDisplayString(Setting setting) {
		ArrayList<String> result = new ArrayList<>();

		result.add("->" + setting.getName());

		if(setting.parent_ != null) {
			result.addAll((doIterationDisplayString(setting.parent_)));
		}

		return result;
	}

	public String toDisplayString() {
		String message = getParentMod().getName();

		if(parent_ != null) {
			ArrayList<String> elements = doIterationDisplayString(parent_);

			Collections.reverse(elements);

			message += UtilityKt.toString(elements);
		}

		message += "->" + getName();

		return message;
	}
}