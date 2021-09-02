package com.kisman.cc.oldclickgui.component.components;

import java.awt.Color;
import java.util.ArrayList;

import com.kisman.cc.Kisman;
import com.kisman.cc.oldclickgui.ClickGui;
import com.kisman.cc.oldclickgui.component.Component;
import com.kisman.cc.oldclickgui.component.Frame;
import com.kisman.cc.oldclickgui.component.components.sub.*;
import com.kisman.cc.hud.hudgui.component.components.sub.DrawHudButton;
import com.kisman.cc.hud.hudmodule.*;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.ColorUtil;
import com.kisman.cc.util.customfont.CustomFontUtil;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.gui.Gui;

public class Button extends Component {

	int i = 0;

	int color;
	public Module mod;
	public HudModule hudMod;
	public boolean hud;
	public Frame parent;
	public ColorUtil colorUtil = new ColorUtil();
	public int offset;
	private boolean isHovered;
	private ArrayList<Component> subcomponents;
	private ArrayList<Component> drawBoxHud;
	public boolean open;
	private int height;
	private int num1 = new Color(ClickGui.getRHoveredModule(),ClickGui.getGHoveredModule(), ClickGui.getBHoveredModule(), ClickGui.getAHoveredModule()).darker().getRGB();
	private int num2 = new Color(ClickGui.getRHoveredModule(),ClickGui.getGHoveredModule(), ClickGui.getBHoveredModule(), ClickGui.getAHoveredModule()).getRGB();
	private int num3 = new Color(ClickGui.getRNoHoveredModule(),ClickGui.getGNoHoveredModule(), ClickGui.getBNoHoveredModule(), ClickGui.getANoHoveredModule()).darker().getRGB();
	private int num4 = new Color(ClickGui.getRNoHoveredModule(),ClickGui.getGNoHoveredModule(), ClickGui.getBNoHoveredModule(), ClickGui.getANoHoveredModule()).getRGB();

	public Button(Module mod, Frame parent, int offset) {
		this.hud = false;
		this.mod = mod;
		this.parent = parent;
		this.offset = offset;
		this.subcomponents = new ArrayList<Component>();
		this.open = false;
		height = 12;
		int opY = offset + 12;
		if(Kisman.instance.settingsManager.getSettingsByMod(mod) != null) {
			for(Setting s : Kisman.instance.settingsManager.getSettingsByMod(mod)){
				if(s.isCombo()){
					this.subcomponents.add(new ModeButton(s, this, mod, opY));
					opY += 12;
				}
				if(s.isSlider()){
					this.subcomponents.add(new Slider(s, this, opY));
					opY += 12;
				}
				if(s.isCheck()){
					this.subcomponents.add(new Checkbox(s, this, opY));
					opY += 12;
				}
				if(s.isLine()) {
					this.subcomponents.add(new Line(s, this, opY));
					opY += 12;
				}
				if(s.isCategory()) {
					this.subcomponents.add(new CategoryButton(s, this, opY, s.getIndex()));
					opY += 12;
				}
				if(s.isColorPicker()) {
					this.subcomponents.add(new ColorPickerButton(s, this, opY));
					opY += 12;
				}
				if(s.isColorPickerSimple()) {
					this.subcomponents.add(new ColorPickerSimpleButton(s, this, opY));
					opY += 90;
				}
			}
		}
		this.subcomponents.add(new Keybind(this, opY));
		this.subcomponents.add(new VisibleButton(this, mod, opY));
	}

	public Button(HudModule mod, Frame parent, int offset) {
		this.hud = true;
		this.hudMod = mod;
		this.parent = parent;
		this.offset = offset;
		this.subcomponents = new ArrayList<Component>();
		this.drawBoxHud = new ArrayList<Component>();
		this.open = false;
		height = 12;
		int opY = offset + 12;
		if(Kisman.instance.settingsManager.getSettingsByHudMod(hudMod) != null) {
			for(Setting s : Kisman.instance.settingsManager.getSettingsByHudMod(hudMod)){
				if(s.isCheckHud()) {
					this.subcomponents.add(new Checkbox(s, this, opY));
					opY += 12;
				}
				if(s.isColorPickerHud()) {
					this.subcomponents.add(new ColorPickerButton(s, this, opY));
					opY += 12;
				}
				if(s.isDrawHud()) {
					this.drawBoxHud.add(new DrawHudButton(s, this));
				}
			}
		}
		//this.subcomponents.add(new Keybind(this, opY));
		// this.subcomponents.add(new VisibleButton(this, mod, opY));
	}
	
	@Override
	public void setOff(int newOff) {
		offset = newOff;
		int opY = offset + 12;
		for(Component comp : this.subcomponents) {
			if(comp.isI()) {
				comp.setOff(opY);
				opY += 90;
				i++;
			} else {
				comp.setOff(opY);
				opY += 12;
			}
		}
	}
	
	@Override
	public void renderComponent() {
		Gui.drawRect(
			this.parent.getX(), 
			this.parent.getY() + this.offset, this.parent.getX() + this.parent.getWidth(), 
			this.parent.getY() + 12 + this.offset,
			this.hud ?  
			this.isHovered ? (this.hudMod.isToggled() ? num1 : num2) : (this.hudMod.isToggled() ? num3 : num4) : 
			this.isHovered ? (this.mod.isToggled() ? num1 : num2) : (this.mod.isToggled() ? num3 : num4)
		);
		GL11.glPushMatrix();
		GL11.glScalef(0.5f,0.5f, 0.5f);
		CustomFontUtil.drawString(
			this.hud ? this.hudMod.getName() : this.mod.getName(), 
			(parent.getX() + 2) * 2, 
			(parent.getY() + offset + 2) * 2 + 4, 
			this.hud ? 
			this.hudMod.isToggled() ? 
			new Color(ClickGui.getAActiveText(), ClickGui.getGActiveText(), ClickGui.getBActiveText(), ClickGui.getAActiveText()).getRGB() : 
			new Color(ClickGui.getRText(), ClickGui.getGText(), ClickGui.getBText(), ClickGui.getAText()).getRGB() :
			this.mod.isToggled() ? 
			new Color(ClickGui.getAActiveText(), ClickGui.getGActiveText(), ClickGui.getBActiveText(), ClickGui.getAActiveText()).getRGB() : 
			new Color(ClickGui.getRText(), ClickGui.getGText(), ClickGui.getBText(), ClickGui.getAText()).getRGB()
		);
		if(this.subcomponents.size() > 2)
			CustomFontUtil.drawString(this.open ? "-" : "+", (parent.getX() + parent.getWidth() - 10) * 2, (parent.getY() + offset + 2) * 2 + 4, new Color(ClickGui.getRText(), ClickGui.getGText(), ClickGui.getBText(), ClickGui.getAText()).getRGB());
		GL11.glPopMatrix();
		if(this.open) {
			if(!this.subcomponents.isEmpty()) {
				for(Component comp : this.subcomponents) {
					comp.renderComponent();
				}
				Gui.drawRect(parent.getX() + 2, parent.getY() + this.offset + 12, parent.getX() + 3, parent.getY() + this.offset + ((this.subcomponents.size() + 1) * 12) + (90 * i), new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB()); // ClickGui.isRainbowLine() ? new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB() : 
				//Gui.drawRect(parent.getX(), parent.getY() + this.offset + 12, parent.getX() + 1, parent.getY() + this.offset + ((this.subcomponents.size() + 1) * 12), this.color);
			}
		}
		if(hud) {
			if(hudMod.isToggled()) {
				if(!drawBoxHud.isEmpty()) {
					for(Component comp : this.drawBoxHud) {
						comp.renderComponent();
					}
				}
			}
		}
	}
	
	@Override
	public int getHeight() {
		if(this.open) {
			return (12 * (this.subcomponents.size() + 1));
		}
		return 12;
	}
	
	@Override
	public void updateComponent(int mouseX, int mouseY) {
		this.isHovered = isMouseOnButton(mouseX, mouseY);
		if(!this.subcomponents.isEmpty()) {
			for(Component comp : this.subcomponents) {
				comp.updateComponent(mouseX, mouseY);
			}
		}
	}
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		if(isMouseOnButton(mouseX, mouseY) && button == 0) {
			if(this.hud) {
				this.hudMod.toggle();
			} else {
				this.mod.toggle();
			}
		}
		if(isMouseOnButton(mouseX, mouseY) && button == 1) {
			this.open = !this.open;
			this.parent.refresh();
		}
		for(Component comp : this.subcomponents) {
			comp.mouseClicked(mouseX, mouseY, button);
		}
	}
	
	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		for(Component comp : this.subcomponents) {
			comp.mouseReleased(mouseX, mouseY, mouseButton);
		}
	}
	
	@Override
	public void keyTyped(char typedChar, int key) {
		for(Component comp : this.subcomponents) {
			comp.keyTyped(typedChar, key);
		}
	}
	
	public boolean isMouseOnButton(int x, int y) {
		if(x > parent.getX() && x < parent.getX() + parent.getWidth() && y > this.parent.getY() + this.offset && y < this.parent.getY() + 12 + this.offset) {
			return true;
		}
		return false;
	}

	private void setColor(int color) {
		this.color = color;
	}
}
