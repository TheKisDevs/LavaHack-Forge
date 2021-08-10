package com.kisman.cc.settings;

import java.util.ArrayList;

import com.kisman.cc.module.Module;

/**
 *  Made by HeroCode
 *  it's free to use
 *  but you havяe to credit him
 *
 *  @author HeroCode
 */
public class Setting {
	private int index = 0;
	
	private String name;
	private Module parent;
	private Setting setParent;
	private String mode;

	private String string;
	private String title;

	private String sval;
	private ArrayList<String> options;
	
	private boolean bval;
	
	private double dval;
	private double min;
	private double max;

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

	public Setting(String name, Module modParent, Setting setParent, int index, String title) {
		this.name = name;
		this.parent = modParent;
		this.setParent = setParent;
		this.index = index;
		this.title = title;
		this.mode = "CategoryLine";
	}

	public Setting getSetParent() {
		return setParent;
	}

	public void setSetParent(Setting setParent) {
		this.setParent = setParent;
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

	public boolean onlyInt(){
		return this.onlyint;
	}
}
