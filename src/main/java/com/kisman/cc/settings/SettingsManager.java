package com.kisman.cc.settings;

import java.util.ArrayList;
import java.util.List;

import com.kisman.cc.module.Module;

import com.kisman.cc.hud.hudmodule.*;


/**
 *  Made by HeroCode
 *  it's free to use
 *  but you have to credit him
 *
 *  @author HeroCode
 */
public class SettingsManager {
	
	private ArrayList<Setting> settings;

	private ArrayList<Setting> subsetting;
	
	public SettingsManager(){
		this.settings = new ArrayList<>();
		this.subsetting = new ArrayList<>();
	}

	public void rSubSetting(Setting in) { this.subsetting.add(in); }

	public void rSetting(Setting in){
		this.settings.add(in);
	}

	public void rSettings(Setting... in) {
		for(Setting set : in) {
			settings.add(set);
		}
	}
	
	public ArrayList<Setting> getSettings(){
		return this.settings;
	}

	public ArrayList<Setting> getSubSettingsByMod(Module mod, Setting set) {
		ArrayList<Setting> out = new ArrayList<>();
		for(Setting s : this.subsetting) {
			if(s.getParentMod() == mod && s.getSetparent() == set) {
				out.add(s);
			}
		}
		if(out.isEmpty()) return null;

		return out;
	}
	
	public ArrayList<Setting> getSettingsByMod(Module mod) {
		ArrayList<Setting> out = new ArrayList<>();
		for(Setting s : getSettings()){
			if(s.getParentMod() == mod){
				out.add(s);
			}
		}
		if(out.isEmpty()){
			return null;
		}
		return out;
	}

	public ArrayList<Setting> getSettingsByHudMod(HudModule mod) {
		ArrayList<Setting> out = new ArrayList<Setting>();
		for(Setting s : getSettings()) {
			if(s.isHud()) {
				if(s.getParentHudModule().equals(mod)) {
					out.add(s);
				}
			}
		}
		if(out.isEmpty()) {
			return null;
		}
		return out;
	}

	public Setting getSubSettingByName(Module mod, Setting set, String name) {
		for(Setting s : this.subsetting) {
			if(set.isHud()) return null;

			if(set.getName().equalsIgnoreCase(name) && set.getParentMod() == mod && set.getSetparent() == set) return set;
		}
		System.out.println("[kisman.cc] Error Sub Setting NOT found: '" + name +"'!");
		return null;
	}
	
	public Setting getSettingByName(Module mod, String name){
		for(Setting set : getSettings()){
			if(set.isHud()) {
				return null;
			}
			if(set.getName().equalsIgnoreCase(name) && set.getParentMod() == mod){
				return set;
			}
		}
		System.err.println("[kisman.cc] Error Setting NOT found: '" + name +"'!");
		return null;
	}

	public Setting getHudSettingByName(HudModule mod, String name) {
		try {
			for(Setting set : getSettings()) {
				if(!set.isHud()) {
					return null;
				}

				if(set.getName().equalsIgnoreCase(name) && set.getParentHudModule() == mod) {
					return set;
				}
			}
			System.err.println("[kisman.cc] Error HUD Setting NOT found: '" + name +"'!");
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public Setting getSettingByIndex(int index) {
		try {
			for(Setting set : getSettings()) {
				if(set.getIndex() == index) {
					return set;
				}
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}
}