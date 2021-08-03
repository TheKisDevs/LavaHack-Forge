package com.kisman.cc.oldclickgui;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

import com.kisman.cc.oldclickgui.component.Component;
import com.kisman.cc.oldclickgui.component.Frame;
import com.kisman.cc.module.Category;
import com.kisman.cc.util.HoveredMode;
import com.kisman.cc.util.LineMode;
import com.kisman.cc.util.TextMode;
import net.minecraft.client.gui.GuiScreen;

public class ClickGui extends GuiScreen {
	public static boolean line = false;

	public static LineMode lineMode = LineMode.LEFT;
	public static TextMode textMode = TextMode.DEFAULT;
	public static HoveredMode hoveredMode = HoveredMode.HOVERED;

	public static int RLine = 255;
	public static int GLine = 0;
	public static int BLine = 0;
	public static int ALine = 150;

	public static int RBackground = 80;
	public static int GBackground = 75;
	public static int BBackground = 75;
	public static int ABackground = 150;

	public static int RHoveredModule = 95;
	public static int GHoveredModule = 95;
	public static int BHoveredModule = 87;
	public static int AHoveredModule = 	150;

	public static int RNoHoveredModule = 14;
	public static int GNoHoveredModule = 14;
	public static int BNoHoveredModule = 14;
	public static int ANoHoveredModule = 255;


	public static int RText = 166;
	public static int GText = 161;
	public static int BText = 160;
	public static int AText = 255;

	public static int RActiveText = 255;
	public static int GActiveText = 255;
	public static int BActiveText = 255;
	public static int AActiveText = 255;

	public static ArrayList<Frame> frames;
	//public static int color = -1;

	public ClickGui() {
		this.frames = new ArrayList<Frame>();
		int frameX = 5;
		for(Category category : Category.values()) {
			Frame frame = new Frame(category);
			frame.setX(frameX);
			frames.add(frame);
			frameX += frame.getWidth() + 1;
		}
	}

	@Override
	public void initGui() {
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		for(Frame frame : frames) {
			frame.renderFrame(this.fontRenderer);
			frame.updatePosition(mouseX, mouseY);
			for(Component comp : frame.getComponents()) {
				comp.updateComponent(mouseX, mouseY);
			}
		}
	}

	@Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
		for(Frame frame : frames) {
			if(frame.isWithinHeader(mouseX, mouseY) && mouseButton == 0) {
				frame.setDrag(true);
				frame.dragX = mouseX - frame.getX();
				frame.dragY = mouseY - frame.getY();
			}
			if(frame.isWithinHeader(mouseX, mouseY) && mouseButton == 1) {
				frame.setOpen(!frame.isOpen());
			}
			if(frame.isOpen()) {
				if(!frame.getComponents().isEmpty()) {
					for(Component component : frame.getComponents()) {
						component.mouseClicked(mouseX, mouseY, mouseButton);
					}
				}
			}
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) {
		for(Frame frame : frames) {
			if(frame.isOpen() && keyCode != 1) {
				if(!frame.getComponents().isEmpty()) {
					for(Component component : frame.getComponents()) {
						component.keyTyped(typedChar, keyCode);
					}
				}
			}
		}
		if (keyCode == 1) {
            this.mc.displayGuiScreen(null);
        }
	}


	@Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
		for(Frame frame : frames) {
			frame.setDrag(false);
		}
		for(Frame frame : frames) {
			if(frame.isOpen()) {
				if(!frame.getComponents().isEmpty()) {
					for(Component component : frame.getComponents()) {
						component.mouseReleased(mouseX, mouseY, state);
					}
				}
			}
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}

	public static HoveredMode getHoveredMode() {
		return hoveredMode;
	}

	public static void setHoveredMode(HoveredMode hoveredMode) {
		ClickGui.hoveredMode = hoveredMode;
	}

	public static int getRHoveredModule() {
		return RHoveredModule;
	}

	public static void setRHoveredModule(int RHoveredModule) {
		ClickGui.RHoveredModule = RHoveredModule;
	}

	public static int getGHoveredModule() {
		return GHoveredModule;
	}

	public static void setGHoveredModule(int GHoveredModule) {
		ClickGui.GHoveredModule = GHoveredModule;
	}

	public static int getBHoveredModule() {
		return BHoveredModule;
	}

	public static void setBHoveredModule(int BHoveredModule) {
		ClickGui.BHoveredModule = BHoveredModule;
	}

	public static int getAHoveredModule() {
		return AHoveredModule;
	}

	public static void setAHoveredModule(int AHoveredModule) {
		ClickGui.AHoveredModule = AHoveredModule;
	}

	public static int getRNoHoveredModule() {
		return RNoHoveredModule;
	}

	public static void setRNoHoveredModule(int RNoHoveredModule) {
		ClickGui.RNoHoveredModule = RNoHoveredModule;
	}

	public static int getGNoHoveredModule() {
		return GNoHoveredModule;
	}

	public static void setGNoHoveredModule(int GNoHoveredModule) {
		ClickGui.GNoHoveredModule = GNoHoveredModule;
	}

	public static int getBNoHoveredModule() {
		return BNoHoveredModule;
	}

	public static void setBNoHoveredModule(int BNoHoveredModule) {
		ClickGui.BNoHoveredModule = BNoHoveredModule;
	}

	public static int getANoHoveredModule() {
		return ANoHoveredModule;
	}

	public static void setANoHoveredModule(int ANoHoveredModule) {
		ClickGui.ANoHoveredModule = ANoHoveredModule;
	}

	public static int getRActiveText() {
		return RActiveText;
	}

	public static void setRActiveText(int RActiveText) {
		ClickGui.RActiveText = RActiveText;
	}

	public static int getGActiveText() {
		return GActiveText;
	}

	public static void setGActiveText(int GActiveText) {
		ClickGui.GActiveText = GActiveText;
	}

	public static int getBActiveText() {
		return BActiveText;
	}

	public static void setBActiveText(int BActiveText) {
		ClickGui.BActiveText = BActiveText;
	}

	public static int getAActiveText() {
		return AActiveText;
	}

	public static void setAActiveText(int AActiveText) {
		ClickGui.AActiveText = AActiveText;
	}

	public static TextMode getTextMode() {
		return textMode;
	}

	public static void setTextMode(TextMode textMode) {
		ClickGui.textMode = textMode;
	}

	public static int getRText() {
		return RText;
	}

	public static void setRText(int RText) {
		ClickGui.RText = RText;
	}

	public static int getGText() {
		return GText;
	}

	public static void setGText(int GText) {
		ClickGui.GText = GText;
	}

	public static int getBText() {
		return BText;
	}

	public static void setBText(int BText) {
		ClickGui.BText = BText;
	}

	public static int getAText() {
		return AText;
	}

	public static void setAText(int AText) {
		ClickGui.AText = AText;
	}

	public static LineMode getLineMode() {
		return lineMode;
	}

	public static void setLineMode(LineMode lineMode) {
		ClickGui.lineMode = lineMode;
	}

	public static int getALine() {
		return ALine;
	}

	public static void setALine(int ALine) {
		ClickGui.ALine = ALine;
	}

	public static int getABackground() {
		return ABackground;
	}

	public static void setABackground(int ABackground) {
		ClickGui.ABackground = ABackground;
	}

	public static boolean isLine() {
		return line;
	}

	public static void setLine(boolean line) {
		ClickGui.line = line;
	}

	public static int getRLine() {
		return RLine;
	}

	public static void setRLine(int RLine) {
		ClickGui.RLine = RLine;
	}

	public static int getGLine() {
		return GLine;
	}

	public static void setGLine(int GLine) {
		ClickGui.GLine = GLine;
	}

	public static int getBLine() {
		return BLine;
	}

	public static void setBLine(int BLine) {
		ClickGui.BLine = BLine;
	}

	public static int getRBackground() {
		return RBackground;
	}

	public static void setRBackground(int RBackground) {
		ClickGui.RBackground = RBackground;
	}

	public static int getGBackground() {
		return GBackground;
	}

	public static void setGBackground(int GBackground) {
		ClickGui.GBackground = GBackground;
	}

	public static int getBBackground() {
		return BBackground;
	}

	public static void setBBackground(int BBackground) {
		ClickGui.BBackground = BBackground;
	}
}
