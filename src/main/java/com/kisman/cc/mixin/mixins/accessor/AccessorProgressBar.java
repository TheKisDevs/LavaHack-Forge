package com.kisman.cc.mixin.mixins.accessor;

import net.minecraftforge.fml.common.ProgressManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author _kisman_
 * @since 23:54 of 23.12.2022
 */
@Mixin(ProgressManager.ProgressBar.class)
public interface AccessorProgressBar {
    @Accessor("step") void step(int step);
}
