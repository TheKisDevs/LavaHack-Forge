package com.kisman.cc.oldclickgui.component;

import java.awt.*;
import java.util.ArrayList;

import com.kisman.cc.Kisman;
import com.kisman.cc.hud.hudmodule.*;
import com.kisman.cc.oldclickgui.ClickGui;
import com.kisman.cc.oldclickgui.component.components.Button;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.oldclickgui.component.components.sub.ItemsButton;
import com.kisman.cc.oldclickgui.component.components.sub.ColorPickerSimpleButton;
import com.kisman.cc.oldclickgui.component.components.sub.PreviewButton;
import com.kisman.cc.util.ColorUtil;
import com.kisman.cc.util.LineMode;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

public class Frame {
	public ArrayList<Component> components;
	public Category category;
	public HudCategory hudCategory;
	public boolean hud;
	public ColorUtil colorUtil = new ColorUtil();
	private boolean open;
	private int width;
	private int y;
	private int x;
	private int barHeight;
	private boolean isDragging;
	public int dragX;
	public int dragY;
	public int offset;
	
	public Frame(Category cat) {
		this.components = new ArrayList<Component>();
		this.category = cat;
		this.width = 88;
		this.x = 5;
		this.y = 5;
		this.barHeight = 13;
		this.dragX = 0;
		this.open = true;
		this.isDragging = false;
		this.hud = false;
		int tY = this.barHeight;
		
		for(Module mod : Kisman.instance.moduleManager.getModulesInCategory(category)) {
			Button modButton = new Button(mod, this, tY);
			this.components.add(modButton);
			tY += 12;
		}
	}

	public Frame(HudCategory cat) {
		this.components = new ArrayList<Component>();
		this.hudCategory = cat;
		this.width = 88;
		this.x = 5;
		this.y = 5;
		this.barHeight = 13;
		this.dragX = 0;
		this.open = true;
		this.isDragging = false;
		this.hud = true;
		int tY = this.barHeight;
		
		for(HudModule mod : Kisman.instance.hudModuleManager.getModulesInCategory(hudCategory)) {
			Button modButton = new Button(mod, this, tY);
			this.components.add(modButton);
			tY += 12;
		}
	}
	
	public ArrayList<Component> getComponents() {
		return components;
	}
	
	public void setX(int newX) {
		this.x = newX;
	}
	
	public void setY(int newY) {
		this.y = newY;
	}
	
	public void setDrag(boolean drag) {
		this.isDragging = drag;
	}
	
	public boolean isOpen() {
		return open;
	}
	
	public void setOpen(boolean open) {
		this.open = open;
	}
	
	public void renderFrame(FontRenderer fontRenderer) {
		Gui.drawRect(this.x, this.y, this.x + this.width, this.y + this.barHeight, ClickGui.isRainbowBackground() ? colorUtil.getColor() : new Color(ClickGui.getRBackground(), ClickGui.getGBackground(), ClickGui.getBBackground(), ClickGui.getABackground()).getRGB());
		if(ClickGui.isLine()) {
			if(ClickGui.getLineMode() == LineMode.LEFT) {
				Gui.drawRect(this.x, this.y, this.x + 1, this.y + this.barHeight, new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB());
			} else if(ClickGui.getLineMode() == LineMode.LEFTONTOP) {
				Gui.drawRect(this.x, this.y, this.x + 1, this.y + this.barHeight, new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB());
				Gui.drawRect(this.x, this.y, this.x + this.width, this.y + 1, new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB());
			} else {
				Gui.drawRect(this.x, this.y, this.x + 1, this.y + this.barHeight, new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB());
				Gui.drawRect(this.x, this.y, this.x + this.width, this.y + 1, new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB());
				Gui.drawRect(this.x + this.width - 1, this.y, this.x + this.width, this.y + this.barHeight, new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB());
				Gui.drawRect(this.x, this.y + this.barHeight - 1, this.x + this.width, this.y + this.barHeight, new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB());
			}
		}
		GL11.glPushMatrix();
		GL11.glScalef(0.5f,0.5f, 0.5f);
		fontRenderer.drawStringWithShadow(this.hud ? this.hudCategory.name() : this.category.name(), (this.x + 2) * 2 + 5, (this.y + 2.5f) * 2 + 5, new Color(ClickGui.getRText(), ClickGui.getGText(), ClickGui.getBText(), ClickGui.getAText()).getRGB());
		fontRenderer.drawStringWithShadow(this.open ? "-" : "+", (this.x + this.width - 10) * 2 + 5, (this.y + 2.5f) * 2 + 5, new Color(ClickGui.getRText(), ClickGui.getGText(), ClickGui.getBText(), ClickGui.getAText()).getRGB());

		GL11.glPopMatrix();
		if(this.open) {
			if(!this.components.isEmpty()) {
				for(Component component : components) {
					component.renderComponent();
				}
			}
		}
	}
	
	public void refresh() {
		int off = this.barHeight;
		for(Component comp : components) {
			comp.setOff(off);
			off += comp.getHeight();
			this.offset = off;
		}
	}

	public void refreshPosition() {
		int off = barHeight;

		for(Component comp : components) {
			Button button = (Button) comp;

			comp.setOff(off);
			off += 12;

			if(!(comp instanceof Button) || !button.open) {
				continue;
			}

			for (Component set : button.subcomponents) {
				set.setOff(off);

				if(set instanceof ColorPickerSimpleButton) {
					if(((ColorPickerSimpleButton) set).open) {
						off += ((ColorPickerSimpleButton) set).PICKER_HEIGHT;
						continue;
					}

					off += 12;
					continue;
				}

				if(set instanceof com.kisman.cc.oldclickgui.component.components.sub.Category) {
					com.kisman.cc.oldclickgui.component.components.sub.Category cat1 = (com.kisman.cc.oldclickgui.component.components.sub.Category) set;

					if(cat1.open) {
						off += cat1.opY;
						continue;
					}

					off += 12;
					continue;
				}

				if(set instanceof PreviewButton) {
					if(((PreviewButton) set).open) {
						off += 100;
						continue;
					}

					off += 12;
					continue;
				}

				if(set instanceof ItemsButton) {
					if(((ItemsButton) set).open) {
						off += ((ItemsButton) set).blocksOffset;
						continue;
					}

					off += 12;
					continue;
				}

				off += 12;
			}
		}
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getWidth() {
		return width;
	}
	
	public void updatePosition(int mouseX, int mouseY) {
		if(this.isDragging) {
			this.setX(mouseX - dragX);
			this.setY(mouseY - dragY);
		}
	}
	
	public boolean isWithinHeader(int x, int y) {
		if(x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.barHeight) {
			return true;
		}
		return false;
	}
	
}
