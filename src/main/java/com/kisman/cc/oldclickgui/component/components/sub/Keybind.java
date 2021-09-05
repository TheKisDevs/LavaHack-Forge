package com.kisman.cc.oldclickgui.component.components.sub;

import com.kisman.cc.oldclickgui.ClickGui;
import com.kisman.cc.oldclickgui.component.components.Button;
import com.kisman.cc.oldclickgui.component.Component;
import com.kisman.cc.util.ColorUtil;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.awt.*;

public class Keybind extends Component {

	private boolean hovered;
	private boolean binding;
	private Button parent;
	private ColorUtil colorUtil = new ColorUtil();
	private int offset;
	private int x;
	private int y;
	
	public Keybind(Button button, int offset) {
		this.parent = button;
		this.x = button.parent.getX() + button.parent.getWidth();
		this.y = button.parent.getY() + button.offset;
		this.offset = offset;
	}
	
	@Override
	public void setOff(int newOff) {
		offset = newOff;
	}
	
	@Override
	public void renderComponent() {
		Gui.drawRect(parent.parent.getX() + 2, parent.parent.getY() + offset, parent.parent.getX() + (parent.parent.getWidth() * 1) - 3, parent.parent.getY() + offset + 12, this.hovered ? new Color(ClickGui.getRHoveredModule(), ClickGui.getGHoveredModule(), ClickGui.getBHoveredModule(), ClickGui.getAHoveredModule()).getRGB() : new Color(ClickGui.getRNoHoveredModule(), ClickGui.getGNoHoveredModule(), ClickGui.getBNoHoveredModule(), ClickGui.getANoHoveredModule()).getRGB());
		//Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + 2, parent.parent.getY() + offset + 12, new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB());
		GL11.glPushMatrix();
		GL11.glScalef(0.5f,0.5f, 0.5f);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(binding ? "Press a key..." : ("Key: " + Keyboard.getKeyName(this.parent.mod.getKey())), (parent.parent.getX() + 7) * 2, (parent.parent.getY() + offset + 2) * 2 + 5, new Color(ClickGui.getRText(), ClickGui.getGText(), ClickGui.getBText(), ClickGui.getAText()).getRGB());
		GL11.glPopMatrix();
	}
	
	@Override
	public void updateComponent(int mouseX, int mouseY) {
		this.hovered = isMouseOnButton(mouseX, mouseY);
		this.y = parent.parent.getY() + offset;
		this.x = parent.parent.getX();
	}
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		if(isMouseOnButton(mouseX, mouseY) && button == 0 && this.parent.open) {
			this.binding = !this.binding;
		}
	}
	
	@Override
	public void keyTyped(char typedChar, int key) {
		if(this.binding) {
			this.parent.mod.setKey(key);
			this.binding = false;
		}
	}
	
	public boolean isMouseOnButton(int x, int y) {
		if(x > this.x && x < this.x + 88 && y > this.y && y < this.y + 12) {
			return true;
		}
		return false;
	}
}
