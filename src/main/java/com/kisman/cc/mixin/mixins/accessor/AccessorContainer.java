package com.kisman.cc.mixin.mixins.accessor;

import net.minecraft.inventory.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author _kisman_
 * @since 23:57 of 19.08.2022
 */
@Mixin(Container.class)
public interface AccessorContainer {
    @Accessor("transactionID") short transactionID();
    @Accessor("transactionID") void transactionID(short transactionID);
}
