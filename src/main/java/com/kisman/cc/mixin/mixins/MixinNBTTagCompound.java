package com.kisman.cc.mixin.mixins;

import com.kisman.cc.mixin.accessors.INBTTagCompound;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.DataOutput;

/**
 * @author _kisman_
 * @since 22:33 of 02.03.2023
 */
@Mixin(NBTTagCompound.class)
public abstract class MixinNBTTagCompound implements INBTTagCompound {
    @Shadow void write(DataOutput output) { }

    @Override
    public void handleWrite(DataOutput output) {
        write(output);
    }
}
