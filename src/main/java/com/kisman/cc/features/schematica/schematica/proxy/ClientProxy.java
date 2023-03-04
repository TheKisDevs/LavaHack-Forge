package com.kisman.cc.features.schematica.schematica.proxy;

import com.kisman.cc.features.schematica.core.util.math.MBlockPos;
import com.kisman.cc.features.schematica.core.util.vector.Vector3d;
import com.kisman.cc.features.schematica.schematica.Schematica;
import com.kisman.cc.features.schematica.schematica.api.ISchematic;
import com.kisman.cc.features.schematica.schematica.client.printer.SchematicPrinter;
import com.kisman.cc.features.schematica.schematica.client.renderer.RenderSchematic;
import com.kisman.cc.features.schematica.schematica.client.world.SchematicWorld;
import com.kisman.cc.features.schematica.schematica.command.client.CommandSchematicaReplace;
import com.kisman.cc.features.schematica.schematica.handler.ConfigurationHandler;
import com.kisman.cc.features.schematica.schematica.handler.client.*;
import com.kisman.cc.features.schematica.schematica.reference.Reference;
import com.kisman.cc.features.schematica.schematica.world.schematic.SchematicFormat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.io.File;

public class ClientProxy extends CommonProxy {
    public static boolean isRenderingGuide = false;
    public static boolean isPendingReset = false;

    public static final Vector3d playerPosition = new Vector3d();
    public static EnumFacing orientation = null;
    public static int rotationRender = 0;

    public static SchematicWorld schematic = null;

    public static final MBlockPos pointA = new MBlockPos();
    public static final MBlockPos pointB = new MBlockPos();
    public static final MBlockPos pointMin = new MBlockPos();
    public static final MBlockPos pointMax = new MBlockPos();

    public static EnumFacing axisFlip = EnumFacing.UP;
    public static EnumFacing axisRotation = EnumFacing.UP;

    public static RayTraceResult objectMouseOver = null;

    public static void setPlayerData(EntityPlayer player, float partialTicks) {
        playerPosition.x = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        playerPosition.y = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        playerPosition.z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        orientation = getOrientation(player);

        rotationRender = MathHelper.floor(player.rotationYaw / 90) & 3;
    }

    private static EnumFacing getOrientation(EntityPlayer player) {
        if (player.rotationPitch > 45) {
            return EnumFacing.DOWN;
        } else if (player.rotationPitch < -45) {
            return EnumFacing.UP;
        } else {
            switch (MathHelper.floor(player.rotationYaw / 90.0 + 0.5) & 3) {
            case 0:
                return EnumFacing.SOUTH;
            case 1:
                return EnumFacing.WEST;
            case 2:
                return EnumFacing.NORTH;
            case 3:
                return EnumFacing.EAST;
            }
        }

        return null;
    }

    public static void updatePoints() {
        pointMin.x = Math.min(pointA.x, pointB.x);
        pointMin.y = Math.min(pointA.y, pointB.y);
        pointMin.z = Math.min(pointA.z, pointB.z);

        pointMax.x = Math.max(pointA.x, pointB.x);
        pointMax.y = Math.max(pointA.y, pointB.y);
        pointMax.z = Math.max(pointA.z, pointB.z);
    }

    public static void movePointToPlayer(MBlockPos point) {
        point.x = (int) Math.floor(playerPosition.x);
        point.y = (int) Math.floor(playerPosition.y);
        point.z = (int) Math.floor(playerPosition.z);

        switch (rotationRender) {
        case 0:
            point.x -= 1;
            point.z += 1;
            break;
        case 1:
            point.x -= 1;
            point.z -= 1;
            break;
        case 2:
            point.x += 1;
            point.z -= 1;
            break;
        case 3:
            point.x += 1;
            point.z += 1;
            break;
        }
    }

    public static void moveSchematicToPlayer(SchematicWorld schematic) {
        if (schematic != null) {
            MBlockPos position = schematic.position;
            position.x = (int) Math.floor(playerPosition.x);
            position.y = (int) Math.floor(playerPosition.y);
            position.z = (int) Math.floor(playerPosition.z);

            switch (rotationRender) {
            case 0:
                position.x -= schematic.getWidth();
                position.z += 1;
                break;
            case 1:
                position.x -= schematic.getWidth();
                position.z -= schematic.getLength();
                break;
            case 2:
                position.x += 1;
                position.z -= schematic.getLength();
                break;
            case 3:
                position.x += 1;
                position.z += 1;
                break;
            }
        }
    }

    public void preInit() {
        for (KeyBinding keyBinding : InputHandler.KEY_BINDINGS) {
            ClientRegistry.registerKeyBinding(keyBinding);
        }
    }

    public void init() {
        MinecraftForge.EVENT_BUS.register(InputHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(TickHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(RenderTickHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(RenderSchematic.INSTANCE);
        MinecraftForge.EVENT_BUS.register(GuiHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(new OverlayHandler());
        MinecraftForge.EVENT_BUS.register(new WorldHandler());

        ClientCommandHandler.instance.registerCommand(new CommandSchematicaReplace());
    }

    public void postInit() {
        resetSettings();
    }

    @Override
    public File getDataDirectory() {
        return Schematica.CONFIG_FOLDER;
    }

    @Override
    public void resetSettings() {
        super.resetSettings();

        SchematicPrinter.INSTANCE.setEnabled(true);
        unloadSchematic();

        isRenderingGuide = false;

        playerPosition.set(0, 0, 0);
        orientation = null;
        rotationRender = 0;

        pointA.set(0, 0, 0);
        pointB.set(0, 0, 0);
        updatePoints();
    }

    @Override
    public void unloadSchematic() {
        schematic = null;
        RenderSchematic.INSTANCE.setWorldAndLoadRenderers(null);
        SchematicPrinter.INSTANCE.setSchematic(null);
    }

    @Override
    public boolean loadSchematic(EntityPlayer player, File directory, String filename) {
        ISchematic schematic = SchematicFormat.readFromFile(directory, filename);
        if (schematic == null) {
            return false;
        }

        final SchematicWorld world = new SchematicWorld(schematic);

        Reference.logger.debug("Loaded {} [w:{},h:{},l:{}]", filename, world.getWidth(), world.getHeight(), world.getLength());

        ClientProxy.schematic = world;
        RenderSchematic.INSTANCE.setWorldAndLoadRenderers(world);
        SchematicPrinter.INSTANCE.setSchematic(world);
        world.isRendering = true;

        return true;
    }

    @Override
    public boolean isPlayerQuotaExceeded(EntityPlayer player) {
        return false;
    }

    @Override
    public File getPlayerSchematicDirectory(EntityPlayer player, final boolean privateDirectory) {
        return ConfigurationHandler.schematicDirectory;
    }
}
