package com.kisman.cc.mixin.mixins;

import com.kisman.cc.features.module.player.FreeCamRewrite;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

@Mixin(ItemEndCrystal.class)
public class MixinItemEnderCrystal {
    @Redirect(
            method = "onItemUse",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;" +
                            "getEntitiesWithinAABBExcludingEntity(" +
                            "Lnet/minecraft/entity/Entity;" +
                            "Lnet/minecraft/util/math/AxisAlignedBB;)" +
                            "Ljava/util/List;",
                    remap = false))
    private List<Entity> getEntitiesWithinAABBExcludingEntityHook(World world,
                                                                  Entity entityIn,
                                                                  AxisAlignedBB bb)
    {
        List<Entity> entities =
                world.getEntitiesWithinAABBExcludingEntity(entityIn, bb);

        if ( FreeCamRewrite.instance.isToggled()) {
            Entity player = Minecraft.getMinecraft().player;
            if (player == null) return entities;

            for (Entity entity : entities) {
                if (player.equals(entity)) continue;
                return entities;
            }

            return new ArrayList<>(0);
        }

        return entities;
    }

}