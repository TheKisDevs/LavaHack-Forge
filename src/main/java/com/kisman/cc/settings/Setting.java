package com.kisman.cc.settings;

import java.util.ArrayList;

import com.kisman.cc.module.Module;
import com.kisman.cc.oldclickgui.ColorPicker;

/**
 *  Made by HeroCode
 *  it's free to use
 *  but you havяe to credit him
 *
 *  @author HeroCode
 */
public class Setting {
	private ColorPicker colorPicker;

	private int index = 0;
	private int color;
	
	private String name;
	private Module parent;
	private String mode;

	private String string;
	private String title;

	private String sval;
	private ArrayList<String> options;
	
	private boolean bval;
	private boolean rainbow;
	
	private double dval;
	private double min;
	private double max;

	private int r, g, b, a;

	private boolean onlyint = false;

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
		this.options = options;
		this.mode = "Combo";
	}
	
	public Setting(String name, Module parent, boolean bval){
		this.name = name;
		this.parent = parent;
		this.bval = bval;
		this.mode = "Check";
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

	public Setting(String name, Module parent, int index, String title) {
		this.name = name;
		this.parent = parent;
		this.index = index;
		this.title = title;
		this.mode = "Category";
	}

	public Setting(String name, Module parent, String title, int index) {
		this.name = name;
		this.parent = parent;
		this.index = index;
		this.title = title;
		this.mode = "CategoryLine";
	}

	public Setting(String name, Module parent, int index, String title, boolean bval) {
		this.name = name;
		this.parent = parent;
		this.index = index;
		this.bval = bval;
		this.mode = "CategoryCheck";
	}

	public Setting(String name, Module parent, String title, boolean no) {//, int dColor
		this.name = name;
		this.parent = parent;
		this.title = title;
		this.mode = "ColorPicker";
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

	public void setColor(int color) {
		this.color = color;
	}

	public ColorPicker getColorPicker() {
		return this.colorPicker;
	}

	public void setColorPicker(ColorPicker colorPicker) {
		this.colorPicker = colorPicker;
	}
	
	public boolean isCombo(){
		return this.mode.equalsIgnoreCase("Combo") ? true : false;
	}
	
	public boolean isCheck(){
		return this.mode.equalsIgnoreCase("Check") ? true : false;
	}
	
	public boolean isSlider(){
		return this.mode.equalsIgnoreCase("Slider") ? true : false;
	}

	public boolean isLine() {
		return this.mode.equalsIgnoreCase("Line") ? true : false;
	}

	public boolean isCategory() {
		return this.mode.equalsIgnoreCase("Category") ? true : false;
	}

	public boolean isCategoryLine() {
		return this.mode.equalsIgnoreCase("CategoryLine") ? true : false;
	}

	public boolean isCategoryCheck() {
		return this.mode.equalsIgnoreCase("CategoryCheck") ? true : false;
	}

	public boolean isColorPicker() {
		return this.mode.equalsIgnoreCase("ColorPicker") ? true : false;
	}

	public boolean onlyInt(){
		return this.onlyint;
	}
}
