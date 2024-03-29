package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventEntityFreeCam;
import com.kisman.cc.event.events.EventIngameOverlay;
import com.kisman.cc.event.events.EventRenderAttackIndicator;
import com.kisman.cc.features.module.render.*;
import com.kisman.cc.util.render.Render2DUtil;
import com.kisman.cc.util.render.ColorUtils;
import com.kisman.cc.util.render.objects.screen.AbstractGradient;
import com.kisman.cc.util.render.objects.screen.Vec4d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

import static net.minecraft.client.gui.GuiIngame.WIDGETS_TEX_PATH;

@Mixin(value = GuiIngame.class, priority = 10000)
public class MixinGuiIngame extends Gui {
    @Shadow @Final public Minecraft mc;
    @Shadow protected void renderHotbarItem(int p_184044_1_, int p_184044_2_, float p_184044_3_, EntityPlayer player, ItemStack stack) {}
    @Shadow public FontRenderer getFontRenderer() {return null;}

    @Inject(method = "renderPortal", at = @At("HEAD"), cancellable = true)
    protected void antiPortal(float timeInPortal, ScaledResolution scaledRes, CallbackInfo ci) {
        EventIngameOverlay.Portal event = new EventIngameOverlay.Portal();
        Kisman.EVENT_BUS.post(event);
        if(event.isCancelled()) ci.cancel();
    }

    @Inject(method = "renderPumpkinOverlay", at = @At("HEAD"), cancellable = true)
    protected void renderPumpkinOverlayHook(ScaledResolution scaledRes, CallbackInfo ci) {
        EventIngameOverlay.Pumpkin event = new EventIngameOverlay.Pumpkin();
        Kisman.EVENT_BUS.post(event);
        if(event.isCancelled()) ci.cancel();
    }

    @Inject(method = "renderPotionEffects", at = @At("HEAD"), cancellable = true)
    protected void renderPotionEffectsHook(ScaledResolution scaledRes, CallbackInfo ci) {
        EventIngameOverlay.Overlay event = new EventIngameOverlay.Overlay();
        Kisman.EVENT_BUS.post(event);
        if(event.isCancelled()) ci.cancel();
    }

    /**
     * Thanks to Wild client for this idea
     *
     * @author _kisman_
     * @reason no reason no problems.
     */
    @Overwrite
    protected void renderHotbar(ScaledResolution sr, float partialTicks) {
        if(HotbarModifier.instance.isToggled()) {
            Color backgroundColor = new Color(31, 31, 31, 152);
            if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.mc.getTextureManager().bindTexture(WIDGETS_TEX_PATH);
                EntityPlayer entityplayer = (EntityPlayer)this.mc.getRenderViewEntity();
                ItemStack itemstack = entityplayer.getHeldItemOffhand();
                EnumHandSide enumhandside = entityplayer.getPrimaryHand().opposite();
                int i = sr.getScaledWidth() / 2;
                float f = this.zLevel;
                this.zLevel = -90.0F;
                Render2DUtil.drawRectWH(i - 91, sr.getScaledHeight() - 22, 182, 22, backgroundColor.getRGB());
                double[] selectedCoords = new double[] {i - 91 + entityplayer.inventory.currentItem * 20, sr.getScaledHeight() - 22, 22, 22};
                Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[] {selectedCoords[0], selectedCoords[1]}, new double[] {selectedCoords[0] + selectedCoords[2], selectedCoords[1]}, new double[] {selectedCoords[0] + selectedCoords[2], selectedCoords[1] + selectedCoords[3]}, new double[] {selectedCoords[0], selectedCoords[1] + selectedCoords[3]}), ColorUtils.injectAlpha(backgroundColor, 1), HotbarModifier.getPrimaryColor(), true));
                if (!itemstack.isEmpty()) {
                    if(!HotbarModifier.instance.offhand.getValBoolean()) this.drawTexturedModalRect(i - 91 - 29, sr.getScaledHeight() - 23, 24, 22, 29, 24);
                    else {
                        double[] selectedCoordsOffhand;

                        if (enumhandside == EnumHandSide.LEFT) selectedCoordsOffhand = new double[]{i - 91 - 29, sr.getScaledHeight() - 22, 22, 22};
                        else selectedCoordsOffhand = new double[]{i + 91 + 7, sr.getScaledHeight() - 22, 22, 22};

                        Render2DUtil.drawRectWH(selectedCoordsOffhand[0], selectedCoordsOffhand[1], selectedCoordsOffhand[2], selectedCoordsOffhand[3], backgroundColor.getRGB());
                        if (HotbarModifier.instance.offhandGradient.getValBoolean()) Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[]{selectedCoordsOffhand[0], selectedCoordsOffhand[1]}, new double[]{selectedCoordsOffhand[0] + selectedCoordsOffhand[2], selectedCoordsOffhand[1]}, new double[]{selectedCoordsOffhand[0] + selectedCoordsOffhand[2], selectedCoordsOffhand[1] + selectedCoordsOffhand[3]}, new double[]{selectedCoordsOffhand[0], selectedCoordsOffhand[1] + selectedCoordsOffhand[3]}), ColorUtils.injectAlpha(backgroundColor, 1), HotbarModifier.getPrimaryColor(), true));
                    }
                }

                this.zLevel = f;
                GlStateManager.enableRescaleNormal();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                RenderHelper.enableGUIStandardItemLighting();

                int l1;
                int i2;
                int j2;
                for(l1 = 0; l1 < 9; ++l1) {
                    i2 = i - 90 + l1 * 20 + 2;
                    j2 = sr.getScaledHeight() - 16 - 3;
                    this.renderHotbarItem(i2, j2, partialTicks, entityplayer, entityplayer.inventory.mainInventory.get(l1));
                }

                if (!itemstack.isEmpty()) {
                    l1 = sr.getScaledHeight() - 16 - 3;
                    if (enumhandside == EnumHandSide.LEFT) this.renderHotbarItem(i - 91 - 26, l1, partialTicks, entityplayer, itemstack);
                    else this.renderHotbarItem(i + 91 + 10, l1, partialTicks, entityplayer, itemstack);
                }

                if (this.mc.gameSettings.attackIndicator == 2) {
                    float f1 = this.mc.player.getCooledAttackStrength(0.0F);
                    if (f1 < 1.0F) {
                        i2 = sr.getScaledHeight() - 20;
                        j2 = i + 91 + 6;
                        if (enumhandside == EnumHandSide.RIGHT) j2 = i - 91 - 22;
                        this.mc.getTextureManager().bindTexture(Gui.ICONS);
                        int k1 = (int)(f1 * 19.0F);
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                        this.drawTexturedModalRect(j2, i2, 0, 94, 18, 18);
                        this.drawTexturedModalRect(j2, i2 + 18 - k1, 18, 112 - k1, 18, k1);
                    }
                }

                RenderHelper.disableStandardItemLighting();
                GlStateManager.disableRescaleNormal();
                GlStateManager.disableBlend();
            }
        } else {
            if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.mc.getTextureManager().bindTexture(WIDGETS_TEX_PATH);
                EntityPlayer entityplayer = (EntityPlayer)this.mc.getRenderViewEntity();
                ItemStack itemstack = entityplayer.getHeldItemOffhand();
                EnumHandSide enumhandside = entityplayer.getPrimaryHand().opposite();
                int i = sr.getScaledWidth() / 2;
                float f = this.zLevel;
                this.zLevel = -90.0F;
                this.drawTexturedModalRect(i - 91, sr.getScaledHeight() - 22, 0, 0, 182, 22);
                this.drawTexturedModalRect(i - 91 - 1 + entityplayer.inventory.currentItem * 20, sr.getScaledHeight() - 22 - 1, 0, 22, 24, 22);
                if (!itemstack.isEmpty()) {
                    if (enumhandside == EnumHandSide.LEFT) this.drawTexturedModalRect(i - 91 - 29, sr.getScaledHeight() - 23, 24, 22, 29, 24);
                    else this.drawTexturedModalRect(i + 91, sr.getScaledHeight() - 23, 53, 22, 29, 24);
                }

                this.zLevel = f;
                GlStateManager.enableRescaleNormal();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                RenderHelper.enableGUIStandardItemLighting();

                int l1;
                int i2;
                int j2;
                for(l1 = 0; l1 < 9; ++l1) {
                    i2 = i - 90 + l1 * 20 + 2;
                    j2 = sr.getScaledHeight() - 16 - 3;
                    this.renderHotbarItem(i2, j2, partialTicks, entityplayer, entityplayer.inventory.mainInventory.get(l1));
                }

                if (!itemstack.isEmpty()) {
                    l1 = sr.getScaledHeight() - 16 - 3;
                    if (enumhandside == EnumHandSide.LEFT) this.renderHotbarItem(i - 91 - 26, l1, partialTicks, entityplayer, itemstack);
                    else this.renderHotbarItem(i + 91 + 10, l1, partialTicks, entityplayer, itemstack);
                }

                if (this.mc.gameSettings.attackIndicator == 2) {
                    float f1 = this.mc.player.getCooledAttackStrength(0.0F);
                    if (f1 < 1.0F) {
                        i2 = sr.getScaledHeight() - 20;
                        j2 = i + 91 + 6;
                        if (enumhandside == EnumHandSide.RIGHT) j2 = i - 91 - 22;
                        this.mc.getTextureManager().bindTexture(Gui.ICONS);
                        int k1 = (int)(f1 * 19.0F);
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                        this.drawTexturedModalRect(j2, i2, 0, 94, 18, 18);
                        this.drawTexturedModalRect(j2, i2 + 18 - k1, 18, 112 - k1, 18, k1);
                    }
                }

                RenderHelper.disableStandardItemLighting();
                GlStateManager.disableRescaleNormal();
                GlStateManager.disableBlend();
            }
        }
    }

    @Redirect(method = "renderGameOverlay", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;player:Lnet/minecraft/client/entity/EntityPlayerSP;"))
    private EntityPlayerSP redirectOverlayPlayer(Minecraft mc) {
        EventEntityFreeCam event = new EventEntityFreeCam();
        event.entity = mc.player;
        Kisman.EVENT_BUS.post(event);
        return event.entity;
    }

    @Redirect(method = "renderPotionEffects", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;player:Lnet/minecraft/client/entity/EntityPlayerSP;"))
    private EntityPlayerSP redirectPotionPlayer(Minecraft mc) {
        EventEntityFreeCam event = new EventEntityFreeCam();
        event.entity = mc.player;
        Kisman.EVENT_BUS.post(event);
        return event.entity;
    }

    @Inject(method = "renderAttackIndicator", at = @At("HEAD"), cancellable = true)
    private void onRenderAttackIndicator(float partialTicks, ScaledResolution scaledResolution, CallbackInfo ci){
        EventRenderAttackIndicator event = new EventRenderAttackIndicator(partialTicks, scaledResolution);
        Kisman.EVENT_BUS.post(event);
        if(event.isCancelled())
            ci.cancel();
    }
}
