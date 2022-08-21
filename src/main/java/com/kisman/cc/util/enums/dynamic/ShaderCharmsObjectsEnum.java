package com.kisman.cc.util.enums.dynamic;

import com.kisman.cc.util.enums.ShaderCharmsRewriteObjectTypes;
import com.kisman.cc.util.math.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.Vec3d;
import org.cubic.dynamictask.AbstractTask;
import org.cubic.dynamictask.ArgumentFetcher;

import java.util.ArrayList;

/**
 * @author _kisman_
 * @since 13:31 of 16.08.2022
 */
public class ShaderCharmsObjectsEnum {
    private static final Minecraft mc = Minecraft.getMinecraft();

    private static final AbstractTask.DelegateAbstractTask<Void> task = AbstractTask.types(
            Void.class,
            Float.class//Ticks
    );

    private static final AbstractTask.DelegateAbstractTask<Void> taskWithEntity = AbstractTask.types(
            Void.class,
            Float.class,//Ticks,
            Entity.class//Entity
    );

    private static final AbstractTask.DelegateAbstractTask<Void> taskWithTileEntity = AbstractTask.types(
            Void.class,
            Float.class,//Ticks,
            Entity.class//Tile Entity
    );

    private static void drawEntity(ArgumentFetcher arg) {
        Vec3d vector = MathUtil.getInterpolatedRenderPos(arg.fetch(1), arg.fetch(0));
        mc.getRenderManager().getEntityRenderObject(arg.fetch(1)).doRender(
                arg.fetch(1),
                vector.x,
                vector.y,
                vector.z,
                ((Entity) arg.fetch(1)).rotationYaw,
                arg.fetch(0)
        );
    }

    private static void drawTileEntity(ArgumentFetcher arg) {
        TileEntityRendererDispatcher.instance.render(
                arg.fetch(1),
                ((TileEntity) arg.fetch(1)).getPos().getX() - mc.renderManager.renderPosX,
                ((TileEntity) arg.fetch(1)).getPos().getY() - mc.renderManager.renderPosY,
                ((TileEntity) arg.fetch(1)).getPos().getZ() - mc.renderManager.renderPosZ,
                arg.fetch(0)
        );
    }
    
    public enum ShaderCharmsRewriteObjects {
        Hands(task.task(arg -> {
            mc.entityRenderer.renderHand(arg.fetch(0), 2);
            return null;
        }), ShaderCharmsRewriteObjectTypes.Hand, 0),
        Players(taskWithEntity.task(arg -> {
            if(arg.fetch(1) instanceof EntityPlayer) {
                drawEntity(arg);
            }
            return null;
        }), ShaderCharmsRewriteObjectTypes.Entity, 1),
        Crystals(taskWithEntity.task(arg -> {
            if(arg.fetch(1) instanceof EntityEnderCrystal) {
                drawEntity(arg);
            }
            return null;
        }), ShaderCharmsRewriteObjectTypes.Entity, 2),
        Monsters(taskWithEntity.task(arg -> {
            if(arg.fetch(1) instanceof EntityMob) {
                drawEntity(arg);
            }
            return null;
        }), ShaderCharmsRewriteObjectTypes.Entity, 3),
        Animals(taskWithEntity.task(arg -> {
            if(arg.fetch(1) instanceof EntityAnimal) {
                drawEntity(arg);
            }
            return null;
        }), ShaderCharmsRewriteObjectTypes.Entity, 4),
        Frames(taskWithEntity.task(arg -> {
            if(arg.fetch(1) instanceof EntityItemFrame) {
                drawEntity(arg);
            }
            return null;
        }), ShaderCharmsRewriteObjectTypes.Entity, 5),
        Items(taskWithEntity.task(arg -> {
            if(arg.fetch(1) instanceof EntityItem) {
                drawEntity(arg);
            }
            return null;
        }), ShaderCharmsRewriteObjectTypes.Entity, 6),
        Chests(taskWithTileEntity.task(arg -> {
            if(arg.fetch(1) instanceof TileEntityChest) {
                drawTileEntity(arg);
            }
            return null;
        }), ShaderCharmsRewriteObjectTypes.TileEntity, 7),
        EnderChests(taskWithTileEntity.task(arg -> {
            if(arg.fetch(1) instanceof TileEntityEnderChest) {
                drawTileEntity(arg);
            }
            return null;
        }), ShaderCharmsRewriteObjectTypes.TileEntity, 8),
        Furnaces(taskWithTileEntity.task(arg -> {
            if(arg.fetch(1) instanceof TileEntityFurnace) {
                drawTileEntity(arg);
            }
            return null;
        }), ShaderCharmsRewriteObjectTypes.TileEntity, 9),
        EnchantmentTables(taskWithTileEntity.task(arg -> {
            if(arg.fetch(1) instanceof TileEntityEnchantmentTable) {
                drawTileEntity(arg);
            }
            return null;
        }), ShaderCharmsRewriteObjectTypes.TileEntity, 10),
        Droppers(taskWithTileEntity.task(arg -> {
            if(arg.fetch(1) instanceof TileEntityDropper) {
                drawTileEntity(arg);
            }
            return null;
        }), ShaderCharmsRewriteObjectTypes.TileEntity, 11),
        Dispensers(taskWithTileEntity.task(arg -> {
            if(arg.fetch(1) instanceof TileEntityDispenser) {
                drawTileEntity(arg);
            }
            return null;
        }), ShaderCharmsRewriteObjectTypes.TileEntity, 12),
        Hoppers(taskWithTileEntity.task(arg -> {
            if(arg.fetch(1) instanceof TileEntityHopper) {
                drawTileEntity(arg);
            }
            return null;
        }), ShaderCharmsRewriteObjectTypes.TileEntity, 13),
        Comparators(taskWithTileEntity.task(arg -> {
            if(arg.fetch(1) instanceof TileEntityComparator) {
                drawTileEntity(arg);
            }
            return null;
        }), ShaderCharmsRewriteObjectTypes.TileEntity, 14),
        DaylightDetectors(taskWithTileEntity.task(arg -> {
            if(arg.fetch(1) instanceof TileEntityDaylightDetector) {
                drawTileEntity(arg);
            }
            return null;
        }), ShaderCharmsRewriteObjectTypes.TileEntity, 15),
        EndGateway(taskWithTileEntity.task(arg -> {
            if(arg.fetch(1) instanceof TileEntityEndGateway) {
                drawTileEntity(arg);
            }
            return null;
        }), ShaderCharmsRewriteObjectTypes.TileEntity, 16),
        EndPortals(taskWithTileEntity.task(arg -> {
            if(arg.fetch(1) instanceof TileEntityEndPortal) {
                drawTileEntity(arg);
            }
            return null;
        }), ShaderCharmsRewriteObjectTypes.TileEntity, 17),
        FlowerPots(taskWithTileEntity.task(arg -> {
            if(arg.fetch(1) instanceof TileEntityFlowerPot) {
                drawTileEntity(arg);
            }
            return null;
        }), ShaderCharmsRewriteObjectTypes.TileEntity, 18),
        Spawners(taskWithTileEntity.task(arg -> {
            if(arg.fetch(1) instanceof TileEntityMobSpawner) {
                drawTileEntity(arg);
            }
            return null;
        }), ShaderCharmsRewriteObjectTypes.TileEntity, 19),
        NoteBlocks(taskWithTileEntity.task(arg -> {
            if(arg.fetch(1) instanceof TileEntityNote) {
                drawTileEntity(arg);
            }
            return null;
        }), ShaderCharmsRewriteObjectTypes.TileEntity, 20),
        Pistons(taskWithTileEntity.task(arg -> {
            if(arg.fetch(1) instanceof TileEntityPiston) {
                drawTileEntity(arg);
            }
            return null;
        }), ShaderCharmsRewriteObjectTypes.TileEntity, 21),
        Shulkers(taskWithTileEntity.task(arg -> {
            if(arg.fetch(1) instanceof TileEntityShulkerBox) {
                drawTileEntity(arg);
            }
            return null;
        }), ShaderCharmsRewriteObjectTypes.TileEntity, 22),
        Signs(taskWithTileEntity.task(arg -> {
            if(arg.fetch(1) instanceof TileEntitySign) {
                drawTileEntity(arg);
            }
            return null;
        }), ShaderCharmsRewriteObjectTypes.TileEntity, 23),
        Skull(taskWithTileEntity.task(arg -> {
            if(arg.fetch(1) instanceof TileEntitySkull) {
                drawTileEntity(arg);
            }
            return null;
        }), ShaderCharmsRewriteObjectTypes.TileEntity, 24);
        
        private final AbstractTask<Void> abstractTask;
        private final ShaderCharmsRewriteObjectTypes type;
        private final int index;

        ShaderCharmsRewriteObjects(
                AbstractTask<Void> task,
                ShaderCharmsRewriteObjectTypes type,
                int index
        ) {
            this.abstractTask = task;
            this.type = type;
            this.index = index;
        }

        public AbstractTask<Void> getTask() { return abstractTask; }
        public ShaderCharmsRewriteObjectTypes getType() { return type; }
        public int getIndex() { return index; }

        public static ArrayList<ShaderCharmsRewriteObjects> byType(ShaderCharmsRewriteObjectTypes type) {
            ArrayList<ShaderCharmsRewriteObjects> list = new ArrayList<>();

            for(ShaderCharmsRewriteObjects option : values()) {
                if(option.getType() == type) {
                    list.add(option);
                }
            }

            return list;
        }
    }
}
