package com.kisman.cc.util.world;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Rewrite of dynamic block implementation
 * @author Cubic
 * @since 11.11.2022
 */
public class DynamicBlocks {

    public static List<BlockPos> getDynamicBlocksOuter(Entity entity, double yOffset){
        List<BlockPos> dynamicBlocks = getDynamicBlocks(entity, yOffset);
        List<BlockPos> list = new ArrayList<>();
        dynamicBlocks.forEach(
                pos -> list.addAll(
                        Stream.of(EnumFacing.HORIZONTALS).map(pos::offset).collect(Collectors.toList())
                )
        );
        list.removeAll(dynamicBlocks);
        return list;
    }

    public static List<BlockPos> getDynamicBlocks(Entity entity, double yOffset){
        List<BlockPos> list = new ArrayList<>();
        double xD = (entity.boundingBox.maxX - entity.boundingBox.minX) / 2.0;
        double zD = (entity.boundingBox.maxZ - entity.boundingBox.minZ) / 2.0;
        for(double x = Math.floor(entity.posX - xD); x <= Math.floor(entity.posX + xD); x++)
            for(double z = Math.floor(entity.posZ - zD); z <= Math.floor(entity.posZ + zD); x++)
                list.add(new BlockPos(x, entity.posY + yOffset, z));
        return list;
    }
}
