package com.kisman.cc.oldclickgui.component;

import com.kisman.cc.event.events.clickguiEvents.keyTyped.*;
import com.kisman.cc.event.events.clickguiEvents.mouseClicked.*;
import com.kisman.cc.event.events.clickguiEvents.mouseReleased.*;

import java.util.ArrayList;

public class Component {
	public void renderComponent() {}

	public void renderComponentPost() {}
	
	public void updateComponent(int mouseX, int mouseY) {}

	public void mouseClickedPre(int mouseX, int mouseY, int button, MouseClickedPreEvent event) {}
	
	public void mouseClicked(int mouseX, int mouseY, int button) {}

	public void mouseReleasedPre(int mouseX, int mouseY, int mouseButton, MouseReleasedPreEvent event) {}
	
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {}
	
	public int getParentHeight() {
		return 0;
	}

	public void keyTypedPre(char typedChar, int key, KeyTypedPreEvent event) {}
	
	public void keyTyped(char typedChar, int key) {}
	
	public void setOff(int newOff) {}
	
	public int getHeight() {
		return 0;
	}
}
