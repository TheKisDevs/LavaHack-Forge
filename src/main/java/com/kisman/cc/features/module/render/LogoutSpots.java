package com.kisman.cc.features.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import com.kisman.cc.util.manager.friend.FriendManager;
import com.kisman.cc.util.render.Rendering;
import com.mojang.authlib.GameProfile;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class LogoutSpots extends Module {
    private final Setting color = new Setting("Color", this, "Color", new Colour(Color.RED));
    private final Setting chatNotify = new Setting("Chat Notify", this, false);

    private final Setting mode = new Setting("Mode", this, Mode.Box2);

    private final Setting scale = new Setting("Scale", this, 0.003f, 0.001f, 0.01f, false);

    private final ArrayList<LogoutSpot> spots = new ArrayList<>();

    public LogoutSpots() {
        super("LogoutSpots", Category.RENDER);

        setmgr.rSetting(color);
        setmgr.rSetting(chatNotify);
        setmgr.rSetting(mode);
        setmgr.rSetting(scale);
    }

    public boolean isBeta() {return true;}

    public void onEnable() {
        super.onEnable();
        spots.clear();
        Kisman.EVENT_BUS.subscribe(packetevent);
    }

    public void onDisable() {
        super.onDisable();
        Kisman.EVENT_BUS.unsubscribe(packetevent);
        spots.clear();
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if(!spots.isEmpty()) spots.forEach(spot -> {
            spot.render(event.getPartialTicks());
        });
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> packetevent = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketPlayerListItem) {
            SPacketPlayerListItem packet = (SPacketPlayerListItem) event.getPacket();
            if(mc.world == null || packet.getAction() != SPacketPlayerListItem.Action.ADD_PLAYER || packet.getAction() != SPacketPlayerListItem.Action.REMOVE_PLAYER) return;

            packet.getEntries().stream().filter(Objects::nonNull).filter(data -> data.getProfile().getName() != null && !data.getProfile().getName().isEmpty() || data.getProfile().getId() != null).forEach(data -> {
                switch(packet.getAction()) {
                    case ADD_PLAYER:
                        if(get(data.getProfile().getId()) != null) {
                            LogoutSpot spot = get(data.getProfile().getId());
                            if(chatNotify.getValBoolean()) ChatUtility.warning().printClientModuleMessage(spot.name + " is back at " + spot.pos.getX() + " " + spot.pos.getY() + " " + spot.pos.getZ());
                            spots.remove(spot);
                        }
                        break;
                    case REMOVE_PLAYER:
                        System.out.println("1");
                        EntityPlayer player = mc.world.getPlayerEntityByUUID(data.getProfile().getId());
                        if(player == null) return;
                        if(chatNotify.getValBoolean()) ChatUtility.warning().printClientModuleMessage(player.getName() + " just logout at " + player.getPosition().getX() + " " + player.getPosition().getY() + " " + player.getPosition().getZ());
                        spots.add(new LogoutSpot(player, color.getColour()));
                        break;
                }
            });
        }
    });

    public LogoutSpot get(UUID uuid) {
        for(LogoutSpot spot : spots) if(spot.uuid.equals(uuid)) return spot;
        return null;
    }

    public enum Mode {Box1, Box2, Model}

    public class LogoutSpot {
        public Vec3d vec;
        public BlockPos pos;
        public String name;
        public int pops;
        public Colour color;
        public EntityOtherPlayerMP player;
        public UUID uuid;

        public LogoutSpot(EntityPlayer player, Colour colour) {
            this.vec = player.getPositionVector();
            this.pos = player.getPosition();
            this.name = player.getName();
            this.pops = -1;
            this.color = FriendManager.instance.isFriend(name) ? new Colour(Color.CYAN) : colour;
            this.player = new EntityOtherPlayerMP(mc.world, new GameProfile(player.getUniqueID(), ""));
            this.player.copyLocationAndAnglesFrom(player);
            this.player.rotationYaw = player.rotationYaw;
            this.player.rotationYawHead = player.rotationYawHead;
            this.player.rotationPitch = player.rotationPitch;
            this.player.prevRotationPitch = player.prevRotationPitch;
            this.player.prevRotationYaw = player.prevRotationYaw;
            this.player.renderYawOffset = player.renderYawOffset;
            this.uuid = player.getUniqueID();
        }

        public void render(float particalTicks) {
            switch (mode.getValString()) {
                case "Box1":
                    Rendering.drawBoxESP(player, color.r1, color.g1, color.b1, 1, particalTicks);
                    break;
                case "Box2":
                    //TODO: rendering
//                    RenderUtil.drawBoxESP(player.getEntityBoundingBox(), color.getColor(), 1, true, true, 100, 255);
                    break;
                case "Model":
                    GL11.glPushMatrix();
                    mc.renderManager.renderEntityStatic(player, particalTicks, false);
                    GL11.glPopMatrix();
                    break;
            }
//            RenderUtil2.drawNametag(player.getName() + " just logout at " + player.getPosition().getX() + " " + player.getPosition().getY() + " " + player.getPosition().getZ(), player.getEntityBoundingBox(), scale.getValDouble(), color.getRGB(),  false);
            /*
            * public static void drawNametag(String text, AxisAlignedBB interpolated, double scale, int color, boolean rectangle) {
        double x = (interpolated.minX + interpolated.maxX) / 2.0;
        double y = (interpolated.minY + interpolated.maxY) / 2.0;
        double z = (interpolated.minZ + interpolated.maxZ) / 2.0;

        drawNametag(text, x, y, z, scale, color, rectangle);
    }

    public static Entity getEntity() {
        return mc.getRenderViewEntity() == null ? mc.player : mc.getRenderViewEntity();
    }

    public static void drawNametag(String text, double x, double y, double z, double scale, int color, boolean rectangle) {
        //double dist = MathHelper.sqrt(x * x + y * y + z * z);
        double dist = getEntity().getDistance(x + mc.getRenderManager().viewerPosX, y + mc.getRenderManager().viewerPosY, z + mc.getRenderManager().viewerPosZ);

        int textWidth = CustomFontUtil.getStringWidth(text) / 2;
        double scaling = 0.0018 + scale * dist;

        if (dist <= 8.0) scaling = 0.0245;

        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0F, -1500000.0f);
        GlStateManager.disableLighting();
        GlStateManager.translate(x, y + 0.4f, z);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
        float xRot = mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f;
        GlStateManager.rotate(mc.getRenderManager().playerViewX, xRot, 0.0f, 0.0f);
        GlStateManager.scale(-scaling, -scaling, scaling);
        GlStateManager.disableDepth();

        if (rectangle)
        {
            GlStateManager.enableBlend();
            prepare( (float)( -textWidth - 1), (float) -CustomFontUtil.getFontHeight(), (float) (textWidth + 2), 1.0F, 1.8F, 0x55000400, 0x33000000);
            GlStateManager.disableBlend();
        }

        GlStateManager.enableBlend();
        CustomFontUtil.drawStringWithShadow(text, -textWidth, -(mc.fontRenderer.FONT_HEIGHT - 1), color);
        GlStateManager.disableBlend();

        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0F, 1500000.0f);
        GlStateManager.popMatrix();
    }*/
        }
    }
}
