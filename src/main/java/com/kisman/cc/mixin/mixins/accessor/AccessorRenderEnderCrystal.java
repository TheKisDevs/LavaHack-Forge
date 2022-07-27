package com.kisman.cc.mixin.mixins.accessor;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author _kisman_
 * @since 18:12 of 27.07.2022
 */
@Mixin(RenderEnderCrystal.class)
public interface AccessorRenderEnderCrystal {
    @Accessor("modelEnderCrystal") void modelEnderCrystal(ModelBase base);
    @Accessor("modelEnderCrystalNoBase") void modelEnderCrystalNoBase(ModelBase base);
}
