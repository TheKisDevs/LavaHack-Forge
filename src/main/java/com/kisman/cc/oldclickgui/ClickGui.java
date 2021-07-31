package com.kisman.cc.oldclickgui;

import java.io.IOException;
import java.util.ArrayList;

import com.kisman.cc.oldclickgui.component.Component;
import com.kisman.cc.oldclickgui.component.Frame;
import com.kisman.cc.module.Category;
import net.minecraft.client.gui.GuiScreen;

public class ClickGui extends GuiScreen {
	public static int RLine = 255;
	public static int GLine = 0;
	public static int BLine = 0;

	public static int RBackground = 80;
	public static int GBackground = 75;
	public static int BBackground = 75;

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
