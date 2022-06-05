package com.kisman.cc.features.module.render

import com.kisman.cc.util.manager.friend.FriendManager
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.combat.autorer.util.ProjectionUtils
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.util.GlowRendererPattern
import com.kisman.cc.util.*
import com.kisman.cc.util.render.customfont.CustomFontUtil
import com.kisman.cc.util.render.Render2DUtil
import com.kisman.cc.util.render.Render2DUtilKt
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.GlStateManager.DestFactor
import net.minecraft.client.renderer.GlStateManager.SourceFactor
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11
import java.util.function.Supplier

class NameTagsRewrite : Module("NameTagsRewrite", "Renders info about players.", Category.RENDER) {
    val scale = Setting("Scale", this, 2.5, 0.1, 5.0, false)
    val ping = Setting("Ping", this, true)
    val pops = Setting("Pops", this, true)
    val health = Setting("Health", this, true)
    val background = Setting("Background", this, false)
    val backgroundAlpha = Setting("BG Alpha", this, 100.0, 0.0, 255.0, true).setVisible {background.valBoolean}

    val glow = Setting("Glow Background", this, false).setVisible {background.valBoolean}

    val glowSetting = GlowRendererPattern(this, Supplier {background.valBoolean && glow.valBoolean})

    init {
        setmgr.rSetting(scale)
        setmgr.rSetting(ping)
//        setmgr.rSetting(pops)
        setmgr.rSetting(health)
        setmgr.rSetting(background)
        setmgr.rSetting(backgroundAlpha)
        setmgr.rSetting(glow)

        glowSetting.init()
    }

    @SubscribeEvent fun onRenderWorld(event : RenderWorldLastEvent) {
        val fontRendererIn = mc.fontRenderer

        for(player in mc.world.playerEntities) {
            if(player == mc.player) continue

            val x = player.positionVector.x
            var y = player.positionVector.y
            val z = player.positionVector.z

            val viewerYaw: Float = mc.renderManager.playerViewY
            val viewerPitch: Float = mc.renderManager.playerViewX
            val isThirdPersonFrontal = mc.renderManager.options.thirdPersonView == 2
            val offset: Float = player.height + 0.5f - if (player.isSneaking) 0.25f else 0.0f
            val verticalShift = if ("deadmau5" == player.name) -10 else 0

            y += offset

            val str = buildStr(player)

            GlStateManager.pushMatrix()
            GlStateManager.translate(x, y, z)
            GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f)
            GlStateManager.rotate(-viewerYaw, 0.0f, 1.0f, 0.0f)
            GlStateManager.rotate((if (isThirdPersonFrontal) -1 else 1).toFloat() * viewerPitch, 1.0f, 0.0f, 0.0f)
            GlStateManager.scale(-0.025f, -0.025f, 0.025f)
            GlStateManager.disableLighting()
            GlStateManager.depthMask(false)
            GlStateManager.disableDepth()

            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO)
            val i1: Int = fontRendererIn.getStringWidth(str) / 2
            GlStateManager.disableTexture2D()
            val tessellator = Tessellator.getInstance()
            val bufferbuilder = tessellator.buffer
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR)
            bufferbuilder.pos((-i1 - 1).toDouble(), (-1 + verticalShift).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, (backgroundAlpha.valInt / 255).coerceIn(0, 1).toFloat()).endVertex()
            bufferbuilder.pos((-i1 - 1).toDouble(), (8 + verticalShift).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, (backgroundAlpha.valInt / 255).coerceIn(0, 1).toFloat()).endVertex()
            bufferbuilder.pos((i1 + 1).toDouble(), (8 + verticalShift).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, (backgroundAlpha.valInt / 255).coerceIn(0, 1).toFloat()).endVertex()
            bufferbuilder.pos((i1 + 1).toDouble(), (-1 + verticalShift).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, (backgroundAlpha.valInt / 255).coerceIn(0, 1).toFloat()).endVertex()
            tessellator.draw()
            GlStateManager.enableTexture2D()
            GlStateManager.enableDepth()

            GlStateManager.depthMask(true)
            fontRendererIn.drawStringWithShadow(str, (-fontRendererIn.getStringWidth(str) / 2).toFloat(), verticalShift.toFloat(), -1)
            GlStateManager.enableLighting()
            GlStateManager.disableBlend()
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
            GlStateManager.popMatrix()
        }
    }

    fun buildStr(player : EntityPlayer) : String {
        var pingVal = -1
        try {
            pingVal = mc.player.connection.getPlayerInfo(player.uniqueID).responseTime
        } catch (e : Exception) {}

        return buildString {
            append(player.displayName)
            append(TextFormatting.RESET)
            if(ping.valBoolean) append("${TextFormatting.GRAY}$pingVal ms")
            append(TextFormatting.RESET)
            append("${ColourUtilKt.healthColor(player)}${String.format("%.1f", player.health)}")
        }
    }

//    @SubscribeEvent
    fun onRender(event: RenderGameOverlayEvent.Text) {
        for(player in mc.world.playerEntities) {
            if(player == mc.player) continue

            val health = player.health + player.getAbsorptionAmount()
            val builder = StringBuilder()
            val yOffset = if(player.isSneaking) 1.75 else 2.25
            val deltas = Render2DUtilKt.getDeltas(event.partialTicks, player)
            val projection = ProjectionUtils.toScaledScreenPos(
                Vec3d(deltas[0], deltas[1], deltas[2]).addVector(0.0, yOffset, 0.0)
            )

            GL11.glPushMatrix()

            GL11.glTranslated(projection.x, projection.y, 0.0)
            GL11.glTranslated(scale.valDouble, scale.valDouble, 0.0)


            if(FriendManager.instance.isFriend(player)) builder.append(TextFormatting.AQUA)
            builder.append("${player.name}${TextFormatting.RESET}")
            var ping = -1
            try {
                ping = mc.player.connection.getPlayerInfo(player.uniqueID).responseTime
            } catch (e : Exception) {}
            if(this.ping.valBoolean) builder.append(" $ping ms")
            //TODO: Pops
            if(this.health.valBoolean) builder.append(" ${ColourUtilKt.healthColor(player)}${MathHelper.ceil(health)}${TextFormatting.RESET}")

            if(glow.valBoolean) {
                glowSetting.draw(
                    event.partialTicks,
                    Colour(12, 12, 12, (backgroundAlpha.valInt)),
                    (-((CustomFontUtil.getStringWidth(builder.toString()) - 2) / 2)),
                    (-(CustomFontUtil.getFontHeight() + 2)),
                    CustomFontUtil.getStringWidth(builder.toString()) + 4,
                    CustomFontUtil.getFontHeight()
                )
            }
            Render2DUtil.drawRect(
                (-((CustomFontUtil.getStringWidth(builder.toString()) - 2) / 2)).toDouble(),
                (-(CustomFontUtil.getFontHeight() + 2)).toDouble(),
                ((CustomFontUtil.getStringWidth(builder.toString()) + 2) / 2).toDouble(),
                1.0,
                Colour(12, 12, 12, (if(glow.valBoolean) 0 else backgroundAlpha.valInt)).rgb
            )

            CustomFontUtil.drawStringWithShadow(
                builder.toString(),
                (-((CustomFontUtil.getStringWidth(builder.toString())) / 2)).toDouble(),
                (-(CustomFontUtil.getFontHeight())).toDouble(),
                -1
            )

            GL11.glPopMatrix()
        }
    }
}