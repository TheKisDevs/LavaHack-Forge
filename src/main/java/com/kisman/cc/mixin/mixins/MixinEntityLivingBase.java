package com.kisman.cc.mixin.mixins;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends MixinEntity {
    @Shadow public EnumHand swingingHand;
    @Shadow
    protected ItemStack activeItemStack;
    @Shadow
    public float moveStrafing;
    @Shadow
    public float moveVertical;
    @Shadow
    public float moveForward;

    @Shadow
    public void jump() {
    }

    @Shadow
    public abstract boolean isElytraFlying();
}
