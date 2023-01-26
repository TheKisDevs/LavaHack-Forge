package com.kisman.cc.features.schematica.schematica.handler.client;

import com.kisman.cc.features.schematica.schematica.client.world.SchematicWorld;
import com.kisman.cc.features.schematica.schematica.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class RenderTickHandler {
    public static final RenderTickHandler INSTANCE = new RenderTickHandler();

    private final Minecraft minecraft = Minecraft.getMinecraft();

    private RenderTickHandler() {}

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        SchematicWorld schematic = ClientProxy.schematic;

        ClientProxy.objectMouseOver = schematic != null ? rayTrace(schematic, 1.0f) : null;
    }

    private RayTraceResult rayTrace(SchematicWorld schematic, float partialTicks) {
        Entity renderViewEntity = minecraft.getRenderViewEntity();
        if (renderViewEntity == null) {
            return null;
        }

        double blockReachDistance = this.minecraft.playerController.getBlockReachDistance();

        double posX = renderViewEntity.posX;
        double posY = renderViewEntity.posY;
        double posZ = renderViewEntity.posZ;

        renderViewEntity.posX -= schematic.position.x;
        renderViewEntity.posY -= schematic.position.y;
        renderViewEntity.posZ -= schematic.position.z;

        Vec3d vecPosition = renderViewEntity.getPositionEyes(partialTicks);
        Vec3d vecLook = renderViewEntity.getLook(partialTicks);
        Vec3d vecExtendedLook = vecPosition.addVector(vecLook.x * blockReachDistance, vecLook.y * blockReachDistance, vecLook.z * blockReachDistance);

        renderViewEntity.posX = posX;
        renderViewEntity.posY = posY;
        renderViewEntity.posZ = posZ;

        return schematic.rayTraceBlocks(vecPosition, vecExtendedLook, false, false, true);
    }
}
