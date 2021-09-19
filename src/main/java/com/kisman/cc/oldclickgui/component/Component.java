package com.kisman.cc.oldclickgui.component;

import java.util.ArrayList;

public class Component {
	public ArrayList<Component> components;

	public boolean category = false;

	public void renderComponent() {
		
	}
	
	public void updateComponent(int mouseX, int mouseY) {
		
	}
	
	public void mouseClicked(int mouseX, int mouseY, int button) {
		
	}
	
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
	}
	
	public int getParentHeight() {
		return 0;
	}
	
	public void keyTyped(char typedChar, int key) {
		
	}
	
	public void setOff(int newOff) {
		
	}
	
	public int getHeight() {
		return 0;
	}

	public boolean isCategory() { return this.category; }

	public ArrayList<Component> getComponents() { return this.category ? this.components : null; }
}
