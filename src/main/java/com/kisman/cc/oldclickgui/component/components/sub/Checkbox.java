package com.kisman.cc.oldclickgui.component.components.sub;

import com.kisman.cc.oldclickgui.ClickGui;
import com.kisman.cc.oldclickgui.component.Component;
import com.kisman.cc.oldclickgui.component.components.Button;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.ColorUtil;
import com.kisman.cc.util.LineMode;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.awt.*;

public class Checkbox extends Component {

	private boolean hovered;
	private Setting op;
	public Button button;
	private ColorUtil colorUtil = new ColorUtil();
	public int offset;
	public int x, y;
	private int x1;
	private int y1;
	
	public Checkbox(Setting option, Button button, int offset) {
		this.op = option;
		this.button = button;
		this.x = button.parent.getX();
		this.y = button.parent.getY();
		this.x1 = button.parent.getX() + button.parent.getWidth();
		this.y1 = button.parent.getY() + button.offset;
		this.offset = offset;
	}

	@Override
	public void renderComponent() {
		Gui.drawRect(button.parent.getX() + 2, button.parent.getY() + offset, button.parent.getX() + (button.parent.getWidth() * 1) - 3, button.parent.getY() + offset + 12, this.hovered ? new Color(ClickGui.getRHoveredModule(), ClickGui.getGHoveredModule(), ClickGui.getBHoveredModule(), ClickGui.getAHoveredModule()).getRGB() : new Color(ClickGui.getRNoHoveredModule(), ClickGui.getGNoHoveredModule(), ClickGui.getBNoHoveredModule(), ClickGui.getANoHoveredModule()).getRGB());
		//Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + 2, parent.parent.getY() + offset + 12, new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB());
		GL11.glPushMatrix();
		GL11.glScalef(0.5f,0.5f, 0.5f);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(this.op.getName(), (button.parent.getX() + 10 + 4) * 2 + 5, (button.parent.getY() + offset + 2) * 2 + 4, -new Color(ClickGui.getRText(), ClickGui.getGText(), ClickGui.getBText(), ClickGui.getAText()).getRGB());
		GL11.glPopMatrix();
		Gui.drawRect(button.parent.getX() + 3 + 4, button.parent.getY() + offset + 3, button.parent.getX() + 9 + 4, button.parent.getY() + offset + 9, new Color(ClickGui.getRBackground(), ClickGui.getGBackground(), ClickGui.getBBackground(), ClickGui.getABackground()).getRGB());
		if(this.op.getValBoolean())
			Gui.drawRect(button.parent.getX() + 4 + 4, button.parent.getY() + offset + 4, button.parent.getX() + 8 + 4, button.parent.getY() + offset + 8, 0xFF666666);

		Gui.drawRect(button.parent.getX() + 2, button.parent.getY() + offset, button.parent.getX() + 3, button.parent.getY() + offset + 12, new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB());

		if(ClickGui.getSetLineMode() == LineMode.SETTINGONLYSET || ClickGui.getSetLineMode() == LineMode.SETTINGALL) {
			Gui.drawRect(
					button.parent.getX() + 88 - 3,
					button.parent.getY() + offset,
					button.parent.getX() + button.parent.getWidth() - 2,
					button.parent.getY() + offset + 12,
					new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB()
			);
		}
	}
	
	@Override
	public void setOff(int newOff) {
		offset = newOff;
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
			this.op.setValBoolean(!op.getValBoolean());
		}
	}
	
	public boolean isMouseOnButton(int x, int y) {
		if(x > this.x1 && x < this.x1 + 88 && y > this.y1 && y < this.y1 + 12) {
			return true;
		}
		return false;
	}
}
