package com.kisman.cc.oldclickgui.component.components.sub;

import com.kisman.cc.oldclickgui.component.Component;
import com.kisman.cc.oldclickgui.component.components.Button;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.ColorUtil;
import com.kisman.cc.util.customfont.CustomFontUtil;
import com.kisman.cc.Kisman;
import com.kisman.cc.oldclickgui.ClickGui;
import com.kisman.cc.oldclickgui.ColorPicker;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.util.function.*;
import java.awt.Color;

public class ColorPickerButton extends Component{
  private Button button;
  private Setting set;
  private ColorUtil colorUtil = new ColorUtil();
  ColorPicker colorPicker = new ColorPicker();
  float alpha = colorPicker.getColor(3);
  boolean rainbow = colorPicker.isRainbowState();

  public boolean open = false;

  private int x;
  private int y;
  private int x1;
  private int y1;
  private int offset;

  private int r, g, b;

  public ColorPickerButton(Setting set, Button button, int offset) {
      this.button = button;
      this.set = set;
      this.x = button.parent.getX();
      this.y = button.parent.getY();
      this.x1 = button.parent.getX() + button.parent.getWidth();
      this.y1 = button.parent.getY() + button.offset;
      this.offset = offset;
  }

  public void setOff(int offset) {
      this.offset = offset;
  }

  @Override
  public void renderComponent() {
      Gui.drawRect(button.parent.getX() + 3, button.parent.getY() + offset, button.parent.getX() + (button.parent.getWidth() * 1), button.parent.getY() + offset + 12, alpha(new Color(Color.HSBtoRGB(colorPicker.getColor(0), colorPicker.getColor(1), colorPicker.getColor(2))), colorPicker.getColor(3)));
      //Gui.drawRect(button.parent.getX() + 3, (button.parent.getY() + offset + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT - 5) + 5, button.parent.getX() + 7 + button.parent.getWidth() - 7,(button.parent.getY() + offset + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT - 4) + 5, ClickGui.isRainbowLine() ? colorUtil.getColor() : new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB());
      GL11.glPushMatrix();
      GL11.glScalef(0.5f,0.5f, 0.5f);
      CustomFontUtil.drawString(set.getTitle(), (button.parent.getX() + 4) * 2, (button.parent.getY() + offset + 2) * 2 + 4, new Color(ClickGui.getRText(), ClickGui.getGText(), ClickGui.getBText(), ClickGui.getAText()).getRGB());
      CustomFontUtil.drawString(this.open ? "+" : "-", (button.parent.getX() + button.parent.getWidth() - 10) * 2, (button.parent.getY() + offset + 2) * 2 + 4, new Color(ClickGui.getRText(), ClickGui.getGText(), ClickGui.getBText(), ClickGui.getAText()).getRGB());
      GL11.glPopMatrix();
      if(this.open) {
        Minecraft.getMinecraft().displayGuiScreen(colorPicker);
        this.open = false;
      }
  }

  @Override
  public void updateComponent(int mouseX, int mouseY) {
    set.setColor(alpha(new Color(Color.HSBtoRGB(colorPicker.getColor(0), colorPicker.getColor(1), colorPicker.getColor(2))), colorPicker.getColor(3)));
    set.setR(
      colour(
        new Color(
          Color.HSBtoRGB(
            colorPicker.getColor(0), 
            colorPicker.getColor(1), 
            colorPicker.getColor(2)
          )
        ), 
        colorPicker.getColor(3),
        1
      )
    );
    set.setG(
      colour(
        new Color(
          Color.HSBtoRGB(
            colorPicker.getColor(0), 
            colorPicker.getColor(1), 
            colorPicker.getColor(2)
          )
        ), 
        colorPicker.getColor(3),
        2
      )
    );
    set.setB(
      colour(
        new Color(
          Color.HSBtoRGB(
            colorPicker.getColor(0), 
            colorPicker.getColor(1), 
            colorPicker.getColor(2)
          )
        ), 
        colorPicker.getColor(3),
        3
      )
    );
    set.setA(
      colour(
        new Color(
          Color.HSBtoRGB(
            colorPicker.getColor(0), 
            colorPicker.getColor(1), 
            colorPicker.getColor(2)
          )
        ), 
        colorPicker.getColor(3),
        4
      )
    );
    set.setRainbow(colorPicker.isRainbowState());

    //colorUtil.getColorPickerRainBow(colorPicker);
    Kisman.instance.colorUtil.getColorPickerRainBow(colorPicker);
  }

  @Override
  public void mouseClicked(int mouseX, int mouseY, int button) {
    if(isMouseOnButton(mouseX, mouseY) && button == 1) {
      this.open = true;
    }
  }

  public boolean isMouseOnButton(int x, int y) {
    return 
      x > button.parent.getX() + 2 && 
      x < button.parent.getX() + button.parent.getWidth() && 
      y > button.parent.getY() + offset && 
      y < button.parent.getY() + 12 + offset;
  }

  public int getR(Color color, float alpha) {
    return color.getRed();
  } 

  public int getG(Color color, float alpha) {
    return color.getGreen();
  }

  public int getB(Color color, float alpha) {
    return color.getBlue();
  }
//        this.selectedColorFinal = alpha(new Color(Color.HSBtoRGB(this.color[0], this.color[1], this.color[2])), this.color[3]);
  // alpha(new Color(Color.HSBtoRGB(colorPicker.getColor(0), colorPicker.getColor(1), colorPicker.getColor(2))), colorPicker.getColor(3));
  final int alpha(Color color, float alpha) {
    final float red = (float) color.getRed() / 255;
    final float green = (float) color.getGreen() / 255;
    final float blue = (float) color.getBlue() / 255;
    return new Color(red, green, blue, alpha).getRGB();
  }

  final int colour(Color color, float alpha, int index) {
    final float red = (float) color.getRed() / 255;
    final float green = (float) color.getGreen() / 255;
    final float blue = (float) color.getBlue() / 255;

    if(index == 1) {  
      return new Color(red, green, blue, alpha).getRed();
    } else if(index == 2) {
      return new Color(red, green, blue, alpha).getGreen();
    } else if(index == 3) {
      return new Color(red, green, blue, alpha).getBlue();
    } else if(index == 4) {
      return new Color(red, green, blue, alpha).getAlpha();
    } else {
      return 5;
    }
  }
}
