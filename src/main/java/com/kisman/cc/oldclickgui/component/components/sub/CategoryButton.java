//package com.kisman.cc.oldclickgui.component.components.sub;
//
//import com.kisman.cc.Kisman;
//import com.kisman.cc.module.Module;
//import com.kisman.cc.oldclickgui.ClickGui;
//import com.kisman.cc.oldclickgui.component.Component;
//import com.kisman.cc.oldclickgui.component.components.sub.sub.CheckBox;
//import com.kisman.cc.oldclickgui.component.components.sub.sub.LineButton;
//import com.kisman.cc.oldclickgui.component.components.sub.sub.SubComponent;
//import com.kisman.cc.oldclickgui.component.components.Button;
//import com.kisman.cc.settings.Setting;
//import com.kisman.cc.util.ColorUtil;
//import com.kisman.cc.util.customfont.CustomFontUtil;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.Gui;
//import net.minecraft.client.gui.ScaledResolution;
//import org.lwjgl.opengl.GL11;
//
//import java.awt.*;
//import java.util.ArrayList;
//
//public class CategoryButton extends Component {
//    private ArrayList<SubComponent> subSubComponent;
//
//    private ColorUtil colorUtil = new ColorUtil();
//    private Minecraft mc = Minecraft.getMinecraft();
//    private ScaledResolution sr = new ScaledResolution(mc);
//    public Button button;
//    public Setting line;
//
//    public int x;
//    public int y;
//    public int x1;
//    public int y1;
//    public int x2;
//    public int y2;
//    public int offset;
//    public int index;
//
//    public boolean open = false;
//
//    public CategoryButton(Setting line, Button button, int offset, int index) {//, int index
//        this.subSubComponent = new ArrayList<>();
//        this.button = button;
//        this.line = line;
//        this.x = sr.getScaledWidth() - 44;
//        this.y = sr.getScaledHeight() - 6;
//        this.x1 = button.parent.getX();
//        this.y1 = button.parent.getY();
//        this.x2 = button.parent.getX() + button.parent.getWidth();
//        this.y2 = button.parent.getY() + button.offset;
//        this.offset = offset;
//        this.index = index;
//
//        int opY = 12;
//        for(Setting set : Kisman.instance.settingsManager.getSettings()) {
//            if(set.getIndex() == this.index) {
//                if(set.isCategoryLine()) {
//                    this.subSubComponent.add(new LineButton());
//                    opY += 12;
//                }
//                if(set.isCategoryCheck()) {
//                    this.subSubComponent.add(new CheckBox(set, button, opY));
//                    opY += 12;
//                }
//            }
//        }
//    }
//
//    @Override
//    public void renderComponent() {
//        Gui.drawRect(button.parent.getX() + 2, button.parent.getY() + offset, button.parent.getX() + (button.parent.getWidth() * 1) - 3, button.parent.getY() + offset + 12, new Color(ClickGui.getRNoHoveredModule(), ClickGui.getGNoHoveredModule(), ClickGui.getBNoHoveredModule(), ClickGui.getANoHoveredModule()).getRGB());
//        Gui.drawRect(button.parent.getX() + 3, (button.parent.getY() + offset + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT - 5) + 5, button.parent.getX() + 7 + button.parent.getWidth() - 7,(button.parent.getY() + offset + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT - 4) + 5, new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB());
//        GL11.glPushMatrix();
//        GL11.glScalef(0.5f,0.5f, 0.5f);
//        CustomFontUtil.drawString(line.getTitle(), (button.parent.getX() + 4) * 2, (button.parent.getY() + offset) * 2 + 5, new Color(ClickGui.getRText(), ClickGui.getGText(), ClickGui.getBText(), ClickGui.getAText()).getRGB());
//        GL11.glPopMatrix();
//        if(open) {
//            Gui.drawRect(sr.getScaledWidth() - 44, sr.getScaledHeight() - 6, sr.getScaledWidth() + 44, sr.getScaledHeight() + 7, new Color(ClickGui.getRNoHoveredModule(), ClickGui.getGNoHoveredModule(), ClickGui.getBNoHoveredModule(), ClickGui.getANoHoveredModule()).getRGB());
//            GL11.glPushMatrix();
//            GL11.glScalef(0.5f, 0.5f, 0.5f);
//            mc.fontRenderer.drawString(line.getTitle(), ((mc.displayWidth / 2) - 42), ((mc.displayHeight / 2) - 5), -1);
//            GL11.glPopMatrix();
//            for(SubComponent subComp : subSubComponent) {
//                subComp.renderComponent();
//            }
//        }
//    }
//
//    @Override
//    public void mouseClicked(int mouseX, int mouseY, int button) {
//        if(button == 0) {
//            if(isMouseOnScreen(mouseX, mouseY)) {
//                if(!isMouseOnFrame(mouseX, mouseY)) {
//                    this.open = false;
//                }
//            }
//        }
//        if(button == 1) {
//            if(isMouseOnButton(mouseX, mouseY)) {
//                this.open = !this.open;
//            }
//            if(isMouseOnScreen(mouseX, mouseY)) {
//                if(!isMouseOnFrame(mouseX, mouseY)) {
//                    this.open = false;
//                }
//            }
//        }
//    }
//
//    public boolean isMouseOnButton(int x, int y) {
//        if(x > button.parent.getX() && x < button.parent.getX() + 88 && y > button.parent.getY() + offset && y < button.parent.getY() + 12 + offset) {
//            return true;
//        }
//        return false;
//    }
//
//    public boolean isMouseOnScreen(int x, int y) {
//        if(!(x < this.x && x > this.x + 88 && y < this.y && y > this.y + 12)) {
//            if(!isMouseOnButton(x, y)) {
//                 return true;
//            }
//            return false;
//        }
//        return false;
//    }
//
//    public boolean isMouseOnFrame(int x, int y) {
//        if(x < this.x && x > this.x + 88 && y < this.y && y > this.y + 12) {
//            return true;
//        }
//        return false;
//    }
//}
