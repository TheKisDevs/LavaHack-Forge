package com.kisman.cc.oldclickgui.component.components;

import java.awt.Color;
import java.util.ArrayList;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.clickguiEvents.keyTyped.KeyTypedPreEvent;
import com.kisman.cc.event.events.clickguiEvents.mouseClicked.MouseClickedPreEvent;
import com.kisman.cc.event.events.clickguiEvents.mouseReleased.MouseReleasedPreEvent;
import com.kisman.cc.oldclickgui.ClickGui;
import com.kisman.cc.oldclickgui.component.Component;
import com.kisman.cc.oldclickgui.component.Frame;
import com.kisman.cc.oldclickgui.component.components.sub.*;
import com.kisman.cc.hud.hudgui.component.components.sub.DrawHudButton;
import com.kisman.cc.hud.hudmodule.*;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.ColorUtil;
import com.kisman.cc.util.LineMode;
import com.kisman.cc.util.customfont.CustomFontUtil;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.gui.Gui;

public class Button extends Component {
	public int x;
	public int y;

	int color;
	public Module mod;
	public HudModule hudMod;
	public boolean hud;
	public Frame parent;
	public ColorUtil colorUtil = new ColorUtil();
	public int offset;
	public int opY;
	private boolean isHovered;
	public ArrayList<Component> subcomponents;
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
		this.subcomponents = new ArrayList<>();
		this.open = false;
		height = 12;
		opY = offset + 12;
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
				if(s.isColorPicker()) {
					this.subcomponents.add(new ColorPickerButton(s, this, opY));
					opY += 12;
				}
				if(s.isColorPickerSimple()) {
					this.subcomponents.add(new ColorPickerSimpleButton(s, this, opY));
					opY += 12;
				}
				if(s.isString()) {
					this.subcomponents.add(new StringButton(s, this, opY));
					opY += 12;
				}
				if(s.isCategory()) {
					this.subcomponents.add(new Category(this, s, opY));
					opY += 12;
				}
				if(s.isBind()) {
					subcomponents.add(new Keybind(this, s, opY));
					opY += 12;
				}
				if(s.isPreview()) {
					subcomponents.add(new PreviewButton(s, this, opY));
					opY += 12;
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
	}

	@Override
	public int getOff() {
		return offset;
	}
	
	@Override
	public void setOff(int newOff) {
		offset = newOff;
		int opY = offset + 12;
		for(Component comp : this.subcomponents) {
			comp.setOff(opY);
			opY += 12;
		}
	}

	public void setOff(int newOff, Component comp) {
		boolean finded = false;
		int opY = 12;
		for(Component comp1 : subcomponents) {
			if(comp1 == comp) {
				finded = true;
				opY	+= 12;
				continue;
			}

			if(finded) {
				comp1.setOff(opY + newOff);
			}

			opY += 12;
		}
	}
	
	@Override
	public void renderComponent() {
		Gui.drawRect(
			this.parent.getX(), 
			this.parent.getY() + this.offset,
				this.parent.getX() + this.parent.getWidth(),
			this.parent.getY() + 12 + this.offset,
			this.hud ?  
			this.isHovered ? (this.hudMod.isToggled() ? num1 : num2) : (this.hudMod.isToggled() ? num3 : num4) : 
			this.isHovered ? (this.mod.isToggled() ? num1 : num2) : (this.mod.isToggled() ? num3 : num4)
		);
		if(ClickGui.getSetLineMode() == LineMode.SETTINGALL) {
			Gui.drawRect(
					this.parent.getX(),
					this.parent.getY() + this.offset,
					this.parent.getX() + 1,
					this.parent.getY() + this.offset + 12,
					new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB()
			);
			Gui.drawRect(
					this.parent.getX() + parent.getWidth() - 1,
					this.parent.getY() + offset,
					this.parent.getX() + parent.getWidth(),
					parent.getY() + this.offset + 12,
					new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB()
			);

			if(parent.components.size() == parent.components.indexOf(this)) {
				Gui.drawRect(
						parent.getX(),
						parent.getY() + this.offset - 1,
						parent.getX() + parent.getWidth(),
						parent.getY() + this.offset,
						new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB()
				);
			}
		}



		GL11.glPushMatrix();
		GL11.glScalef(0.5f,0.5f, 0.5f);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(
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
		if(this.open && (parent.components.indexOf(this) == parent.components.size()) && ClickGui.getSetLineMode() == LineMode.SETTINGALL) {
			Gui.drawRect(
					parent.getX(),
					parent.getY() + offset + 11,
					parent.getX() + parent.getWidth(),
					parent.getY() + this.offset + 12,
					new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB()
			);
		}

		if(this.open) {
			if(!this.subcomponents.isEmpty()) {
				int compCount = 0;
//
//				int height = 0;
//				int lastHeight = 0;

				for(Component comp : this.subcomponents) {
					comp.renderComponent();

//					lastHeight = height;

//					if(comp instanceof PreviewButton) {
//						if(((PreviewButton) comp).open) {
//							lastHeight += height + 100;
//							height += 112;
//						} else {
//							lastHeight += height;
//							height += 12;
//						}
//					} else {
//						lastHeight += height;
//						height += 12;
//					}

//					Gui.drawRect(
//							comp.x + 2,
//							comp.y + comp.offset,
//							comp.x + 3,
//							comp.y + comp.offset + 12,
//							new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB()
//					);

//					Gui.drawRect(parent.getX() + 2, parent.getY() + offset + 12 + lastHeight, parent.getX() + 3, parent.getY() + offset + 12 + 12, new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB());


					if(ClickGui.getSetLineMode() == LineMode.SETTINGONLYSET || ClickGui.getSetLineMode() == LineMode.SETTINGALL) {
						Gui.drawRect(
								parent.getX() + parent.getWidth() - 3,
								parent.getY() + offset + 12 + (12 * compCount),
								parent.getX() + parent.getWidth() - 2,
								parent.getY() + offset + 24 + (12 * compCount),
								new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB()
						);
					}

					compCount++;
				}

//				Gui.drawRect(parent.getX() + 2, parent.getY() + this.offset + 12, parent.getX() + 3, parent.getY() + this.offset + ((this.subcomponents.size() + 1) * 12), new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB()); // ClickGui.isRainbowLine() ? new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB() :
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
			int subHeigth = 0;

			for(Component comp : subcomponents) {
				subHeigth += comp.getHeight();
			}

			return (12 * (this.subcomponents.size() + 1)) + subHeigth;
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
		if(this.isHovered) {
			ClickGui.setRenderDesc(true);
			ClickGui.setDescStr(this.hud ? hudMod.getDescription() : mod.getDescription());
		} else {
			ClickGui.setRenderDesc(false);
		}
	}
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		MouseClickedPreEvent event = new MouseClickedPreEvent();
		for(Component comp : this.subcomponents) {
			comp.mouseClickedPre(mouseX, mouseY, button, event);
		}

		if(event.isCancelled()) {
			return;
		}

		if(isMouseOnButton(mouseX, mouseY) && button == 0) {
			if(this.hud) {
				this.hudMod.toggle();
			} else {
				this.mod.toggle();
			}
		}
		if(isMouseOnButton(mouseX, mouseY) && button == 1) {
			this.open = !this.open;
//			this.parent.refresh();
			parent.refreshPosition();
		}
		for(Component comp : this.subcomponents) {
			comp.mouseClicked(mouseX, mouseY, button);
		}
	}
	
	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		MouseReleasedPreEvent event = new MouseReleasedPreEvent();
		for(Component comp : this.subcomponents) {
			comp.mouseReleasedPre(mouseX, mouseY, mouseButton, event);
		}

		if(event.isCancelled()) {
			return;
		}

		for(Component comp : this.subcomponents) {
			comp.mouseReleased(mouseX, mouseY, mouseButton);
		}
	}
	
	@Override
	public void keyTyped(char typedChar, int key) {
		KeyTypedPreEvent event = new KeyTypedPreEvent();
		for(Component comp : this.subcomponents) {
			comp.keyTypedPre(typedChar, key, event);
		}

		if(event.isCancelled()) {
			return;
		}

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
