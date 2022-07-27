package com.kisman.cc.mixin.mixins.accessor;

import net.minecraft.client.model.ModelEnderCrystal;
import net.minecraft.client.model.ModelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author _kisman_
 * @since 13:17 of 27.07.2022
 */
@Mixin(ModelEnderCrystal.class)
public interface AccessorModelEnderCrystal {
    @Accessor("base") ModelRenderer base();
    @Accessor("cube") ModelRenderer cube();
    @Accessor("glass") ModelRenderer glass();
}
