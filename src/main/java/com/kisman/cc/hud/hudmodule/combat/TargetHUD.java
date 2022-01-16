package com.kisman.cc.hud.hudmodule.combat;

import com.kisman.cc.hud.hudmodule.*;
import com.kisman.cc.module.combat.*;
import com.kisman.cc.util.Render2DUtil;
import com.kisman.cc.util.customfont.CustomFontRenderer;
import com.kisman.cc.util.customfont.CustomFontUtil;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.client.gui.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class TargetHUD extends HudModule {
    private EntityPlayer target = null;

    public TargetHUD() {
        super("TargetHUD", "TargetInfo", HudCategory.COMBAT);
    }

    public void update() {
        if(AutoRer.currentTarget != null) target = AutoRer.currentTarget;
        else if(target == null) if(KillAura.instance.target != null) target = KillAura.instance.target;
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        if(target == null) return;

        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        float heal = target.getHealth();
        double renderX = 503;
        double renderY = 317;
        double hpPercentage = (heal / target.getMaxHealth());
        hpPercentage = MathHelper.clamp(hpPercentage, 0.00D, 1.00D);
        double hpWidth = 92.0D * hpPercentage;
        float healthPercentage = target.getHealth() / target.getMaxHealth();
        int maxX2 = 40;
        float maxX = Math.max(maxX2, CustomFontUtil.getStringWidth(target.getName()) + 70);
        Render2DUtil.drawRect(renderX - 4, renderY - 3, (renderX + 4 + maxX), (int)renderY + 49,ColorUtils.getColor(55, 55, 63));
        Render2DUtil.drawRect(renderX - 3, renderY - 2, (renderX + 3 + maxX), (int)renderY + 48,ColorUtils.getColor(95, 95, 103));
        Render2DUtil.drawRect(renderX - 2, renderY - 1, (renderX + 2 + maxX), (int)renderY + 47,ColorUtils.getColor(65, 65, 73));
        Render2DUtil.drawRect(renderX - 1, renderY, (renderX + 1 + maxX), (int)renderY + 46,ColorUtils.getColor(25, 25, 33));
        Render2DUtil.drawRect(renderX + 2, renderY + 42, (renderX + maxX), (int)renderY + 45, ColorUtils.getColor(48, 48, 58));
        Render2DUtil.drawRect(renderX + 1, renderY + 2, (renderX + 28), (int)renderY + 29,ColorUtils.rainbow(1,20));
        Render2DUtil.drawRect(renderX + 2, renderY + 3, (int)(renderX + 27), (int)renderY + 28,ColorUtils.getColor(25, 25, 33));
        Gui.drawRect((int)renderX - 1, (int)renderY + 36 + 5, (int)(renderX + 1 + (maxX * healthPercentage)), (int)renderY + 41 + 5, ColorUtils.getColor(10, 10, 20));
        Gui.drawRect((int)renderX, (int)renderY + 37 + 5, (int)(renderX + (maxX * healthPercentage)), (int)renderY + 40 + 5, ColorUtils.rainbow(1,1));
        Gui.drawRect((int)renderX + 1, (int)renderY + 38 + 5, (int)(renderX - 1 + (maxX * healthPercentage)), (int)renderY + 39 + 5, ColorUtils.getColor(0,0,0));

        mc.getTextureManager().bindTexture(mc.getConnection().getPlayerInfo(target.getName()).getLocationSkin());
        GL11.glColor4f(1, 1, 1, 1);
        Gui.drawScaledCustomSizeModalRect(scaledResolution.getScaledWidth() / 2 + 25, scaledResolution.getScaledHeight() / 2 + 50, 8.0F,8, 8, 8, 25, 25, 64.0F, 64.0F);
        Color health = Color.GREEN;
        if(target.getHealth() >= 16){
            health = Color.GREEN;
        }else if(target.getHealth() >= 8 && target.getHealth() <= 16){
            health = Color.YELLOW;
        }else if(target.getHealth() > 0 && target.getHealth() <= 8){
            health = Color.RED;
        }
        CustomFontUtil.drawString("HP: " + (int)target.getHealth() + " | Dist: " + mc.player.getDistance(target), scaledResolution.getScaledWidth() / 2 + 53, scaledResolution.getScaledHeight() / 2 + 65, -1);
        CustomFontUtil.drawString(target.getName(), scaledResolution.getScaledWidth() / 2 + 53, scaledResolution.getScaledHeight() / 2 + 52, -1);
        int posX = scaledResolution.getScaledWidth() / 2 + 53;
        for(final ItemStack item : target.getArmorInventoryList()){
            GL11.glPushMatrix();
            GL11.glTranslated(posX - 27, scaledResolution.getScaledHeight() / 2 + 75, 0);
            GL11.glScaled(0.8, 0.8, 0.8);
            mc.getRenderItem().renderItemIntoGUI(item, 0, 0);
            GL11.glPopMatrix();
            posX += 12;
        }
    }
}
