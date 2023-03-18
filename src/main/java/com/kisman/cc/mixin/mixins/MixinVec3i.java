package com.kisman.cc.mixin.mixins;

import net.minecraft.util.math.Vec3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author _kisman_
 * @since 11:59 of 18.03.2023
 */
@Mixin(Vec3i.class)
public class MixinVec3i {
    @Shadow public int getX() { return 0; };
    @Shadow public int getY() { return 0; };
    @Shadow public int getZ() { return 0; };
}
