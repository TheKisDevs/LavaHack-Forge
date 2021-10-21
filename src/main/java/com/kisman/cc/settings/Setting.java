package com.kisman.cc.settings;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import com.kisman.cc.hud.hudmodule.HudModule;
import com.kisman.cc.module.Module;
import com.kisman.cc.oldclickgui.ColorPicker;
import com.kisman.cc.util.Colour;
import org.lwjgl.input.Keyboard;

import javax.management.OperationsException;

/**
 *  Made by HeroCode
 *  it's free to use
 *  but you have to credit him
 *
 *  @author HeroCode
 */
public class Setting {
	private ColorPicker colorPicker;
	private Colour colour;

	private int index = 0;
	private int color;
	private int key = Keyboard.KEY_NONE;
	
	private String name;
	private Module parent;
	private Setting setparent;
	private HudModule hudParent;
	private String mode;

	private String string;
	private String title;

	private String sval;
	private String dString;
	private ArrayList<String> options;
	private Enum optionEnum;
	private Enum svalEnum;
	
	private boolean bval;
	private boolean rainbow;
	private boolean syns;
	private boolean hud = false;
	private boolean opening;
	private boolean onlyOneWord;
	
	private double dval;
	private double min;
	private double max;

	private float[] colorHSB;

	private int r, g, b, a;

	private int x1, y1, x2, y2;

	private boolean onlyint = false;

	public Setting(String name, Module parent, int key) {
		this.name = name;
		this.parent = parent;
		this.key = key;
		this.mode = "Bind";
	}

	public Setting(String name, Module parent, Setting setparent, String title) {
		this.name = name;
		this.parent = parent;
		this.setparent = setparent;
		this.title = title;
		this.mode = "CategoryLine";
	}

	public Setting(String name, Module parent, String title, boolean open) {
		this.name = name;
		this.parent = parent;
		this.title = title;
		this.opening = open;
		this.mode = "Category";
	}

	public Setting(String name, Module parent, String sval, String dString, boolean opening) {
		this.name = name;
		this.parent = parent;
		this.sval = sval;
		this.dString = dString;
		this.opening = opening;
		this.onlyOneWord = false;
		this.mode = "String";
	}

	public Setting(String name, Module parent, String sval, String dString, boolean opening, boolean onlyOneWord) {
		this.name = name;
		this.parent = parent;
		this.sval = sval;
		this.dString = dString;
		this.opening = opening;
		this.onlyOneWord = onlyOneWord;
		this.mode = "String";
	}

	public Setting(String name, Module parent, String gays, String lgbtq) {
		this.name = name;
		this.parent = parent;
		this.title = gays;
		this.sval = lgbtq;
		this.mode = "yep";
	}

	public Setting(String name, Module parent, String title) {
		this.name = name;
		this.title = title;
		this.parent = parent;
		this.mode = "Line";
	}

	public Setting(String name, Module parent, String sval, ArrayList<String> options){
		this.name = name;
		this.parent = parent;
		this.sval = sval;
		this.svalEnum = null;
		this.options = options;
		this.optionEnum = null;
		this.mode = "Combo";
	}

	public Setting(String name, Module parent, Enum options){
		this.name = name;
		this.parent = parent;
		this.sval = options.name();
		this.svalEnum = options;
		this.options = null;
		this.optionEnum = options;
		this.mode = "Combo";
	}
	
	public Setting(String name, Module parent, boolean bval){
		this.name = name;
		this.parent = parent;
		this.bval = bval;
		this.mode = "Check";
	}

	public Setting(String name, HudModule parent, boolean bval) {
		this.name = name;
		this.hudParent = parent;
		this.bval = bval;
		this.mode = "CheckHud";
		this.hud = true;
	}
	
	public Setting(String name, Module parent, double dval, double min, double max, boolean onlyint){
		this.name = name;
		this.parent = parent;
		this.dval = dval;
		this.min = min;
		this.max = max;
		this.onlyint = onlyint;
		this.mode = "Slider";
	}

	public Setting(String name, Module parent, String title, float[] colorHSB, boolean simpleMode) {//, int dColor
		this.name = name;
		this.parent = parent;
		this.title = title;
		this.colorHSB = colorHSB;
		this.mode = simpleMode ? "ColorPickerSimple" : "ColorPicker";
	}

	public Setting(String name, Module parent, String title, float[] colorHSB) {//, int dColor
		this.name = name;
		this.parent = parent;
		this.title = title;
		this.colorHSB = colorHSB;
		this.mode = "ColorPicker";
	}

	public Setting(String name, HudModule parent, String title, float[] colorHSB) {//, int dColor
		this.name = name;
		this.hudParent = parent;
		this.title = title;
		this.colorHSB = colorHSB;
		this.mode = "ColorPickerHud";
		this.hud = true;
	}

	public Setting(String name, HudModule parent, int x1, int y1, int x2, int y2) {
		this.name = name;
		this.hudParent = parent;
		this.mode = "DrawHud";
		this.hud = true;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	public Enum getNextModeEnum() {
		if(optionEnum != null) {
			Enum enumVal = optionEnum;
			String[] values = Arrays.stream(enumVal.getClass().getEnumConstants()).map(Enum::name).toArray(String[]::new);
			index = index + 1 > values.length - 1 ? 0 : index + 1;
			return Enum.valueOf(enumVal.getClass(), values[index]);
		} else {
			return null;
		}
	}

	public boolean isOnlyOneWord() {
		return onlyOneWord;
	}

	public void setOnlyOneWord(boolean onlyOneWord) {
		this.onlyOneWord = onlyOneWord;
	}

	public boolean isSyns() {
		return syns;
	}

	public void setSyns(boolean syns) {
		this.syns = syns;
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
	}

	public Enum getValEnum() {
		return svalEnum;
	}

	public void setValEnum(Enum svalEnum) {
		this.svalEnum = svalEnum;
	}

	public Enum getOptionEnum() {
		return optionEnum;
	}

	public void setOptionEnum(Enum optionEnum) {
		this.optionEnum = optionEnum;
	}

	public Setting getSetparent() {
		return setparent;
	}

	public void setSetparent(Setting setparent) {
		this.setparent = setparent;
	}

	public String getdString() {
		return dString;
	}

	public void setdString(String dString) {
		this.dString = dString;
	}

	public boolean isOpening() {
		return opening;
	}

	public void setOpening(boolean opening) {
		this.opening = opening;
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

	public HudModule getParentHudModule() {
		return this.hudParent;
	}

	public boolean isHud() {
		return this.hud;
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

	public void setTitle(String title) {
		this.title = title;
	}

	public String getName(){
		return name;
	}
	
	public Module getParentMod(){
		return parent;
	}
	
	public String getValString(){
		return this.sval;
	}
	
	public void setValString(String in){
		this.sval = in;
	}
	
	public ArrayList<String> getOptions(){
		return this.options;
	}
	
	public boolean getValBoolean(){
		return this.bval;
	}
	
	public void setValBoolean(boolean in){
		this.bval = in;
	}
	
	public double getValDouble(){
		if(this.onlyint){
			this.dval = (int)dval;
		}
		return this.dval;
	}

	public void setValDouble(double in){
		this.dval = in;
	}
	
	public double getMin(){
		return this.min;
	}
	
	public double getMax(){
		return this.max;
	}

	public int getR() {
		return r;
	}

	public int getG() {
		return g;
	}

	public int getB() {
		return b;
	}

	public int getA() {
		return a;
	}

	public void setR(int r) {
		this.r = r;
	}

	public void setG(int g) {
		this.g = g;
	}

	public void setB(int b) {
		this.b = b;
	}

	public void setA(int a) {
		this.a = a;
	}

	public boolean isRainbow() {
		return this.rainbow;
	}

	public void setRainbow(boolean rainbow) {
		this.rainbow = rainbow;
	}

	public int getColor() {
		return this.color;
	}

	public Color getColor(boolean colorPicker) {
		return new Color(
				getR(),
				getG(),
				getB(),
				getA()
		);
	}

	public void setColor(int color) {
		this.color = color;
	}

	public ColorPicker getColorPicker() {
		return this.colorPicker;
	}

	public float getColor(int index) {
		return this.colorHSB[index];
	}

	public float[] getColorHSB() {
		return this.colorHSB;
	}

	public void setColor(float color, int index) {
		this.colorHSB[index] = color;
	}

	public void setColor(float[] color) {
		this.colorHSB = color;
	}

	public void setColorPicker(ColorPicker colorPicker) {
		this.colorPicker = colorPicker;
	}

	public boolean isBind() { return mode.equalsIgnoreCase("Bind") ? true : false; }

	public boolean isCategory() { return this.mode.equalsIgnoreCase("Category") ? true : false; }

	public boolean isString() { return this.mode.equalsIgnoreCase("String") ? true : false; }

	public boolean isVoid() { return this.mode.equalsIgnoreCase("Void") ? true : false; }
	
	public boolean isCombo(){
		return this.mode.equalsIgnoreCase("Combo") ? true : false;
	}
	
	public boolean isCheck(){
		return this.mode.equalsIgnoreCase("Check") ? true : false;
	}

	public boolean isCheckHud() { return this.mode.equalsIgnoreCase("CheckHud") ? true : false; }
	
	public boolean isSlider(){
		return this.mode.equalsIgnoreCase("Slider") ? true : false;
	}

	public boolean isLine() {
		return this.mode.equalsIgnoreCase("Line") ? true : false;
	}

//	public boolean isCategory() {
//		return this.mode.equalsIgnoreCase("Category") ? true : false;
//	}
//
	public boolean isCategoryLine() {
		return this.mode.equalsIgnoreCase("CategoryLine") ? true : false;
	}
//
//	public boolean isCategoryCheck() {
//		return this.mode.equalsIgnoreCase("CategoryCheck") ? true : false;
//	}

	public boolean isColorPicker() {
		return this.mode.equalsIgnoreCase("ColorPicker") ? true : false;
	}

	public boolean isColorPickerSimple() {
		return this.mode.equalsIgnoreCase("ColorPickerSimple") ? true : false;
	}

	public boolean isColorPickerHud() {
		return this.mode.equalsIgnoreCase("ColorPickerHud") ? true : false;
	}

	public boolean isDrawHud() {
		return this.mode.equalsIgnoreCase("DrawHud") ? true : false;
	}

	public boolean onlyInt(){
		return this.onlyint;
	}
}
