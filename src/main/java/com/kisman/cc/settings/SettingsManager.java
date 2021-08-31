package com.kisman.cc.settings;

import java.util.ArrayList;

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
	
	public SettingsManager(){
		this.settings = new ArrayList<Setting>();
	}
	
	public void rSetting(Setting in){
		this.settings.add(in);
	}
	
	public ArrayList<Setting> getSettings(){
		return this.settings;
	}
	
	public ArrayList<Setting> getSettingsByMod(Module mod){
		ArrayList<Setting> out = new ArrayList<Setting>();
		for(Setting s : getSettings()){
			if(!s.isHud()) {
				if(s.getParentMod().equals(mod)){
					out.add(s);
				}
			}
			// try {

			// } catch(Exception e) {
			// 	//e.printStackTrace();
			// }
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
			// try {

			// } catch(Exception e) {}
		}
		if(out.isEmpty()) {
			return null;
		}
		return out;
	}

	public ArrayList<Setting> getSettingsByMod(HudModule mod){
		ArrayList<Setting> out = new ArrayList<Setting>();
		for(Setting s : getSettings()){
			if(s.getParentMod().equals(mod)){
				out.add(s);
			}
		}
		if(out.isEmpty()){
			return null;
		}
		return out;
	}
	
	public Setting getSettingByName(Module mod, String name){
		for(Setting set : getSettings()){
			if(set.getName().equalsIgnoreCase(name) && set.getParentMod() == mod){
				return set;
			}
		}
		System.err.println("[kisman.cc] Error Setting NOT found: '" + name +"'!");
		return null;
	}

	public Setting getSettingByName(HudModule mod, String name) {
		for(Setting set : getSettings()) {
			if(set.getName().equalsIgnoreCase(name) && set.getParentHudModule() == mod) {
				return set;
			}
		}
		System.err.println("[kisman.cc] Error HUD Setting NOT found: '" + name +"'!");
		return null;
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