package com.kisman.cc.oldclickgui.component.components.sub;

import com.kisman.cc.oldclickgui.ClickGui;
import com.kisman.cc.oldclickgui.component.Component;
import com.kisman.cc.oldclickgui.component.components.Button;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.LineMode;
import com.kisman.cc.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class PreviewButton extends Component {
    public Setting set;
    public EntityEnderCrystal entityEnderCrystal;
    public EntityPlayer entityPlayer;
    public boolean open = false;

    public int offset;
    public Button button;
    private boolean hover;
    public int x, y;
    private int mouseX, mouseY;

    private Minecraft mc = Minecraft.getMinecraft();

    public PreviewButton(Setting set, Button parent, int offset) {
        this.set = set;
        this.button = parent;
        this.offset = offset;
        this.x = button.parent.getX();
        this.y = button.parent.getY();
    }

    public void renderComponent() {
        Entity entity = set.getEntity();

        Gui.drawRect(button.parent.getX() + 2, button.parent.getY() + offset, button.parent.getX() + (button.parent.getWidth() * 1) - 3, button.parent.getY() + offset + 12, hover ? new Color(ClickGui.getRHoveredModule(), ClickGui.getGHoveredModule(), ClickGui.getBHoveredModule(), ClickGui.getAHoveredModule()).getRGB() : new Color(ClickGui.getRNoHoveredModule(), ClickGui.getGNoHoveredModule(), ClickGui.getBNoHoveredModule(), ClickGui.getANoHoveredModule()).getRGB());
        Gui.drawRect(button.parent.getX() + 3, (button.parent.getY() + offset + mc.fontRenderer.FONT_HEIGHT - 5) + 5, (button.parent.getX() + 7 + button.parent.getWidth() - 7) - 3,(button.parent.getY() + offset + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT - 4) + 5, new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB());

        if (open) {
            Gui.drawRect(button.parent.getX() + 5, button.parent.getY() + offset + 12, button.parent.getX() + 88 - 5, button.parent.getY() + offset + 112, new Color(ClickGui.getRNoHoveredModule(), ClickGui.getGNoHoveredModule(), ClickGui.getBNoHoveredModule(), ClickGui.getANoHoveredModule()).getRGB());
            Gui.drawRect(button.parent.getX() + 5, button.parent.getY() + offset + 12, button.parent.getX() + 6, button.parent.getY() + offset + 112, new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB());

            if(ClickGui.getSetLineMode() == LineMode.SETTINGONLYSET || ClickGui.getSetLineMode() == LineMode.SETTINGALL) {
                Gui.drawRect(
                        button.parent.getX() + 88 - 6,
                        button.parent.getY() + offset + 12,
                        button.parent.getX() + 88 - 5,
                        button.parent.getY() + offset + 112,
                        new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB()
                );
            }

            if (entity instanceof EntityEnderCrystal) {
                EntityEnderCrystal ent;
                entityEnderCrystal = ent = new EntityEnderCrystal(
                        mc.world, Double.longBitsToDouble(Double.doubleToLongBits(9.310613315809524E306) ^ 0x7FAA847B55B02A7FL),
                        Double.longBitsToDouble(Double.doubleToLongBits(1.7125394916952668E308) ^ 0x7FEE7BF580E967CDL), Double.longBitsToDouble(Double.doubleToLongBits(1.351057559302745E308) ^ 0x7FE80CB4154FF45AL));
                ent.setShowBottom(false);
                ent.rotationYaw = Float.intBitsToFloat(Float.floatToIntBits(1.1630837E38f) ^ 0x7EAF005B);
                ent.rotationPitch = Float.intBitsToFloat(Float.floatToIntBits(2.1111544E38f) ^ 0x7F1ED35B);
                ent.innerRotation = 0;
                ent.prevRotationYaw = Float.intBitsToFloat(Float.floatToIntBits(3.176926E38f) ^ 0x7F6F015F);
                ent.prevRotationPitch = Float.intBitsToFloat(Float.floatToIntBits(2.4984888E38f) ^ 0x7F3BF725);
                if (ent != null) {
                    GL11.glScalef(Float.intBitsToFloat(Float.floatToIntBits(6.72125f) ^ 0x7F57147B), (float)Float.intBitsToFloat(Float.floatToIntBits(8.222657f) ^ 0x7E839001), (float)Float.intBitsToFloat(Float.floatToIntBits(7.82415f) ^ 0x7F7A5F70));
                    drawEntityOnScreen(button.parent.getX() + 50, button.parent.getY() + offset + 102, 40, 0, 0, ent);
                }
            } else {
                drawEntityOnScreen(button.parent.getX() + 44, button.parent.getY() + offset + 98, 40, 0, 0, (Entity) mc.player);
            }
        }

        GL11.glPushMatrix();
        GL11.glScalef(0.5f,0.5f, 0.5f);
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(set.getTitle(), (button.parent.getX() + 4) * 2, (button.parent.getY() + offset) * 2 + 5, new Color(ClickGui.getRText(), ClickGui.getGText(), ClickGui.getBText(), ClickGui.getAText()).getRGB());
        GL11.glPopMatrix();

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

    public void updateComponent(int mouseX, int mouseY) {
        if(isMouseOnButton(mouseX, mouseY)) {
            hover = true;
        } else {
            hover = false;
        }

        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 1) {
            open = !open;

            this.button.parent.refreshPosition();
        }
    }

    public void setOff(int newOff) {
        offset = newOff;
    }

    private boolean isMouseOnButton(int x, int y) {
        if(x > button.parent.getX() && x < button.parent.getX() + 88 && y > button.parent.getY() + offset && y < button.parent.getY() + offset + 12) return true;

        return false;
    }

    public void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, Entity ent) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GL11.glColor4f(1, 1, 1, 1);
        GlStateManager.translate((float)posX, (float)posY, 50.0F);
        GlStateManager.scale((float)(-scale), (float)scale, (float)scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
//        float f = ent.getrenderyaw;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
//        float f3 = ent.prevRotationYawHead;
//        float f4 = ent.rotationYawHead;
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        ent.setRenderYawOffset((float)Math.atan((double)(mouseX / 40.0F)) * 20.0F);
        ent.rotationYaw = (float)Math.atan((double)(mouseX / 40.0F)) * 40.0F;
        ent.rotationPitch = -((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F;
        ent.setRotationYawHead(ent.rotationYaw);
        ent.prevRotationYaw = ent.rotationYaw;
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        rendermanager.setRenderShadow(true);
//        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
//        ent.prevRotationYaw = f3;
//        ent.prevRotationPitch = f4;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
}
