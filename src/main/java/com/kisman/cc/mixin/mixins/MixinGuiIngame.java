package com.kisman.cc.mixin.mixins;

import com.google.common.base.Predicate;
import com.google.common.collect.*;
import com.kisman.cc.Kisman;
import com.kisman.cc.module.render.*;
import com.kisman.cc.util.*;
import com.kisman.cc.util.customfont.CustomFontUtil;
import com.kisman.cc.util.Render2DUtil;
import com.kisman.cc.util.render.objects.AbstractGradient;
import com.kisman.cc.util.render.objects.Vec4d;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.*;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;

import static net.minecraft.client.gui.GuiIngame.WIDGETS_TEX_PATH;

@Mixin(value = GuiIngame.class, priority = 10000)
public class MixinGuiIngame extends Gui {
    @Shadow @Final public Minecraft mc;
    @Shadow protected void renderHotbarItem(int p_184044_1_, int p_184044_2_, float p_184044_3_, EntityPlayer player, ItemStack stack) {}
    @Shadow public FontRenderer getFontRenderer() {return null;}

    @Inject(method = "renderPumpkinOverlay", at = @At("HEAD"), cancellable = true)
    protected void renderPumpkinOverlayHook(ScaledResolution scaledRes, CallbackInfo callbackInfo) {
        if (NoRender.instance.isToggled() && NoRender.instance.overlay.getValBoolean()) callbackInfo.cancel();
    }

    @Inject(method = "renderPotionEffects", at = @At("HEAD"), cancellable = true)
    protected void renderPotionEffectsHook(ScaledResolution scaledRes, CallbackInfo callbackInfo) {
        if (NoRender.instance.isToggled() && NoRender.instance.overlay.getValBoolean()) callbackInfo.cancel();
    }

    /**
     * @author _kisman_
     * @credits wild
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
                Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[] {selectedCoords[0], selectedCoords[1]}, new double[] {selectedCoords[0] + selectedCoords[2], selectedCoords[1]}, new double[] {selectedCoords[0] + selectedCoords[2], selectedCoords[1] + selectedCoords[3]}, new double[] {selectedCoords[0], selectedCoords[1] + selectedCoords[3]}), ColorUtils.injectAlpha(backgroundColor, 1), new Color(255, 255, 255, 152), true));
                if (!itemstack.isEmpty()) {
                    if (enumhandside == EnumHandSide.LEFT) {
                        if(!HotbarModifier.instance.offhand.getValBoolean()) this.drawTexturedModalRect(i - 91 - 29, sr.getScaledHeight() - 23, 24, 22, 29, 24);
                        else {
                            Render2DUtil.drawRectWH(i - 91 - 29, sr.getScaledHeight() - 22, 22, 22, backgroundColor.getRGB());
                            double[] selectedCoordsOffhand = new double[] {i - 91 - 29, sr.getScaledHeight() - 22, 22, 22};
                            if(HotbarModifier.instance.offhandGradient.getValBoolean()) Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[] {selectedCoordsOffhand[0], selectedCoordsOffhand[1]}, new double[] {selectedCoordsOffhand[0] + selectedCoordsOffhand[2], selectedCoordsOffhand[1]}, new double[] {selectedCoordsOffhand[0] + selectedCoordsOffhand[2], selectedCoordsOffhand[1] + selectedCoordsOffhand[3]}, new double[] {selectedCoordsOffhand[0], selectedCoordsOffhand[1] + selectedCoordsOffhand[3]}), ColorUtils.injectAlpha(backgroundColor, 1), new Color(255, 255, 255, 152), true));
                        }
                    } else {
                        if(!HotbarModifier.instance.offhand.getValBoolean()) this.drawTexturedModalRect(i + 91, sr.getScaledHeight() - 23, 53, 22, 29, 24);
                        else {
                            Render2DUtil.drawRectWH(i + 91 + 7, sr.getScaledHeight() - 22, 22, 22, backgroundColor.getRGB());
                            double[] selectedCoordsOffhand = new double[] {i + 91 + 7, sr.getScaledHeight() - 22, 22, 22};
                            if(HotbarModifier.instance.offhandGradient.getValBoolean()) Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[] {selectedCoordsOffhand[0], selectedCoordsOffhand[1]}, new double[] {selectedCoordsOffhand[0] + selectedCoordsOffhand[2], selectedCoordsOffhand[1]}, new double[] {selectedCoordsOffhand[0] + selectedCoordsOffhand[2], selectedCoordsOffhand[1] + selectedCoordsOffhand[3]}, new double[] {selectedCoordsOffhand[0], selectedCoordsOffhand[1] + selectedCoordsOffhand[3]}), ColorUtils.injectAlpha(backgroundColor, 1), new Color(255, 255, 255, 152), true));
                        }
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

    /**
     * @author _kisman_
     */
//    @Overwrite
    /*protected void renderScoreboard1(ScoreObjective objective, ScaledResolution scaledRes) {
        if(ScoreboardModifier.instance.isToggled()) {
            final Scoreboard scoreboard = objective.getScoreboard();
            Collection<Score> collection = scoreboard.getSortedScores(objective);
            final List<Score> list = (List<Score>)Lists.newArrayList(Iterables.filter((Iterable)collection, (Predicate)new Predicate<Score>() {
                public boolean apply(@Nullable final Score p_apply_1_) {
                    return p_apply_1_.getPlayerName() != null && !p_apply_1_.getPlayerName().startsWith("#");
                }
            }));
            if (list.size() > 15) collection = (Collection<Score>)Lists.newArrayList(Iterables.skip((Iterable)list, collection.size() - 15));
            else collection = list;
            int i = this.getFontRenderer().getStringWidth(objective.getDisplayName());
            for (final Score score : collection) {
                final ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(score.getPlayerName());
                final String s = ScorePlayerTeam.formatPlayerName(scoreplayerteam, score.getPlayerName()) + ": " + TextFormatting.RED + score.getScorePoints();
                i = Math.max(i, this.getFontRenderer().getStringWidth(s));
            }
            this.i2 = (int) AnimationUtils.animate(collection.size() * this.getFontRenderer().FONT_HEIGHT + ScoreboardModifier.instance.yPos.getValInt(), this.i2, 0.05f);
            final int j1 = scaledRes.getScaledHeight() / 2 + i2 / 2;
            final int l1 = scaledRes.getScaledWidth() - i - 3;
            int m = 0;
            for (final Score score2 : collection) {
                ++m;
                final ScorePlayerTeam scoreplayerteam2 = scoreboard.getPlayersTeam(score2.getPlayerName());
                final String s2 = ScorePlayerTeam.formatPlayerName(scoreplayerteam2, score2.getPlayerName());
                final String s3 = TextFormatting.RED + "" + score2.getScorePoints();
                final int k2 = j1 - m * this.getFontRenderer().FONT_HEIGHT;
                final int l2 = scaledRes.getScaledWidth() - 3 + 2;
                Gui.drawRect(l1 - 2, k2, l2, k2 + this.getFontRenderer().FONT_HEIGHT, 1342177280);
                this.getFontRenderer().drawString(s2, l1, k2, 553648127);
                this.getFontRenderer().drawString(s3, l2 - this.getFontRenderer().getStringWidth(s3), k2, 553648127);
                if (m == collection.size()) {
                    final String s4 = objective.getDisplayName();
                    Render2DUtil.drawRect(l1 - 2, k2 - CustomFontUtil.getFontHeight() - 14, l2, k2 - 22, ColorUtils.astolfoColors(100, 100));
                    Gui.drawRect(l1 - 2, k2 - this.getFontRenderer().FONT_HEIGHT - 13, l2, k2 - 10, 1610612736);
                    CustomFontUtil.drawStringWithShadow(Kisman.getName(), l1 + i / 2 - CustomFontUtil.getStringWidth(Kisman.getName()) / 2, k2 - CustomFontUtil.getFontHeight() - 10, 553648127);
                    Gui.drawRect(l1 - 2, k2 - this.getFontRenderer().FONT_HEIGHT - 1, l2, k2 - 1, 1610612736);
                    Gui.drawRect(l1 - 2, k2 - 1, l2, k2, 1342177280);
                    this.getFontRenderer().drawString(s4, l1 + i / 2 - this.getFontRenderer().getStringWidth(s4) / 2, k2 - this.getFontRenderer().FONT_HEIGHT, 553648127);
                }
            }
        } else {
            Scoreboard scoreboard = objective.getScoreboard();
            List<Score> list = Lists.newArrayList(Iterables.filter(scoreboard.getSortedScores(objective), new Predicate<Score>() {
                public boolean apply(@Nullable Score p_apply_1_) {
                    return p_apply_1_.getPlayerName() != null && !p_apply_1_.getPlayerName().startsWith("#");
                }
            }));
            ArrayList collection;
            if (list.size() > 15) collection = Lists.newArrayList(Iterables.skip(list, (scoreboard.getSortedScores(objective).size() - 15)));
            else collection = new ArrayList(Collections.singletonList(list));

            int i = this.getFontRenderer().getStringWidth(objective.getDisplayName());

            String s;
            for(Iterator var7 = collection.iterator(); var7.hasNext(); i = Math.max(i, this.getFontRenderer().getStringWidth(s))) {
                Score score = (Score)var7.next();
                ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(score.getPlayerName());
                s = ScorePlayerTeam.formatPlayerName(scoreplayerteam, score.getPlayerName()) + ": " + TextFormatting.RED + score.getScorePoints();
            }

            int i1 = collection.size() * this.getFontRenderer().FONT_HEIGHT;
            int j1 = scaledRes.getScaledHeight() / 2 + i1 / 3;
            int l1 = scaledRes.getScaledWidth() - i - 3;
            int j = 0;
            Iterator var12 = collection.iterator();

            while(var12.hasNext()) {
                Score score1 = (Score)var12.next();
                ++j;
                ScorePlayerTeam scoreplayerteam1 = scoreboard.getPlayersTeam(score1.getPlayerName());
                String s1 = ScorePlayerTeam.formatPlayerName(scoreplayerteam1, score1.getPlayerName());
                String s2 = TextFormatting.RED + "" + score1.getScorePoints();
                int k = j1 - j * this.getFontRenderer().FONT_HEIGHT;
                int l = scaledRes.getScaledWidth() - 3 + 2;
                drawRect(l1 - 2, k, l, k + this.getFontRenderer().FONT_HEIGHT, 1342177280);
                this.getFontRenderer().drawString(s1, l1, k, 553648127);
                this.getFontRenderer().drawString(s2, l - this.getFontRenderer().getStringWidth(s2), k, 553648127);
                if (j == collection.size()) {
                    String s3 = objective.getDisplayName();
                    drawRect(l1 - 2, k - this.getFontRenderer().FONT_HEIGHT - 1, l, k - 1, 1610612736);
                    drawRect(l1 - 2, k - 1, l, k, 1342177280);
                    this.getFontRenderer().drawString(s3, l1 + i / 2 - this.getFontRenderer().getStringWidth(s3) / 2, k - this.getFontRenderer().FONT_HEIGHT, 553648127);
                }
            }
        }
    }*/
}
