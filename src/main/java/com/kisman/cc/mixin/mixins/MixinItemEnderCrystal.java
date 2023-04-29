package com.kisman.cc.mixin.mixins;

import net.minecraft.item.ItemEndCrystal;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemEndCrystal.class)
public class MixinItemEnderCrystal {
    /*@Redirect(
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
    }*/

}