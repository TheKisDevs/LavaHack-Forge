package com.kisman.cc.mixin.mixins.baritone;

import baritone.api.utils.accessor.IItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class MixinItemStack implements IItemStack {
    @Shadow @Final public Item item;
    @Shadow public int itemDamage;
    @Unique private int baritoneHash;

    private void recalculateHash() {
        baritoneHash = item == null ? -1 : item.hashCode() + itemDamage;
    }

    @Inject(
            method = "<init>*",
            at = @At("RETURN")
    )
    private void onInit(CallbackInfo ci) {
        recalculateHash();
    }

    @Inject(
            method = "setItemDamage",
            at = @At("TAIL")
    )
    private void onItemDamageSet(CallbackInfo ci) {
        recalculateHash();
    }

    @Override
    public int getBaritoneHash() {
        return baritoneHash;
    }
}
