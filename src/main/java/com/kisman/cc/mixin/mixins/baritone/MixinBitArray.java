package com.kisman.cc.mixin.mixins.baritone;

import baritone.utils.accessor.IBitArray;
import net.minecraft.util.BitArray;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BitArray.class)
public class MixinBitArray implements IBitArray {
    @Shadow @Final private long[] longArray;
    @Shadow @Final private int bitsPerEntry;
    @Shadow @Final private long maxEntryValue;
    @Shadow @Final private int arraySize;

    @Override
    @Unique
    public int[] toArray() {
        int[] out = new int[arraySize];

        for (int idx = 0, kl = bitsPerEntry - 1; idx < arraySize; idx++, kl += bitsPerEntry) {
            final int i = idx * bitsPerEntry;
            final int j = i >> 6;
            final int l = i & 63;
            final int k = kl >> 6;
            final long jl = longArray[j] >>> l;

            if (j == k) {
                out[idx] = (int) (jl & maxEntryValue);
            } else {
                out[idx] = (int) ((jl | longArray[k] << (64 - l)) & maxEntryValue);
            }
        }

        return out;
    }
}
