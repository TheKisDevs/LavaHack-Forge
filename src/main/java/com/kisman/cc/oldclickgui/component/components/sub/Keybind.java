package com.kisman.cc.oldclickgui.component.components.sub;

import com.kisman.cc.oldclickgui.ClickGui;
import com.kisman.cc.oldclickgui.component.components.Button;
import com.kisman.cc.oldclickgui.component.Component;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.ColorUtil;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.awt.*;

public class Keybind extends Component {

	private boolean hovered;
	private boolean binding;
	public Button button;
	private Setting set;
	private ColorUtil colorUtil = new ColorUtil();
	public int offset;
	public int x, y;
	private int x1;
	private int y1;
	
	public Keybind(Button button, int offset) {
		this.button = button;
		this.set = null;
		this.x = button.parent.getX();
		this.y = button.parent.getY();
		this.x1 = button.parent.getX() + button.parent.getWidth();
		this.y1 = button.parent.getY() + button.offset;
		this.offset = offset;
	}

	public Keybind(Button button, Setting set, int offset) {
		this.button = button;
		this.set = set;
		this.x1 = button.parent.getX() + button.parent.getWidth();
		this.y1 = button.parent.getY() + button.offset;
		this.offset = offset;
	}
	
	@Override
	public void setOff(int newOff) {
		offset = newOff;
	}
	
	@Override
	public void renderComponent() {
		Gui.drawRect(button.parent.getX() + 2, button.parent.getY() + offset, button.parent.getX() + (button.parent.getWidth() * 1) - 3, button.parent.getY() + offset + 12, this.hovered ? new Color(ClickGui.getRHoveredModule(), ClickGui.getGHoveredModule(), ClickGui.getBHoveredModule(), ClickGui.getAHoveredModule()).getRGB() : new Color(ClickGui.getRNoHoveredModule(), ClickGui.getGNoHoveredModule(), ClickGui.getBNoHoveredModule(), ClickGui.getANoHoveredModule()).getRGB());
		//Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + 2, parent.parent.getY() + offset + 12, new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB());
		GL11.glPushMatrix();
		GL11.glScalef(0.5f,0.5f, 0.5f);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(binding ? "Press a key..." : ("Key: " + Keyboard.getKeyName(this.button.mod.getKey())), (button.parent.getX() + 7) * 2, (button.parent.getY() + offset + 2) * 2 + 5, new Color(ClickGui.getRText(), ClickGui.getGText(), ClickGui.getBText(), ClickGui.getAText()).getRGB());
		GL11.glPopMatrix();

		Gui.drawRect(button.parent.getX() + 2, button.parent.getY() + offset, button.parent.getX() + 3, button.parent.getY() + offset + 12, new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB());
	}
	
	@Override
	public void updateComponent(int mouseX, int mouseY) {
		this.hovered = isMouseOnButton(mouseX, mouseY);
		this.y1 = button.parent.getY() + offset;
		this.x1 = button.parent.getX();
	}
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		if(isMouseOnButton(mouseX, mouseY) && button == 0 && this.button.open) {
			this.binding = !this.binding;
		}
	}
	
	@Override
	public void keyTyped(char typedChar, int key) {
		System.out.println("5");
		if(this.binding) {
			if(set == null) {
				this.button.mod.setKey(key);
				this.binding = false;
			} else {
				set.setKey(key);
				binding = false;
			}
		}
	}
	
	public boolean isMouseOnButton(int x, int y) {
		if(x > this.x1 && x < this.x1 + 88 && y > this.y1 && y < this.y1 + 12) {
			return true;
		}
		return false;
	}
}
