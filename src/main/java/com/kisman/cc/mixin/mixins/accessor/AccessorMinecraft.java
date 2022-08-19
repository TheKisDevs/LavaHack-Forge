package com.kisman.cc.mixin.mixins.accessor;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Minecraft.class)
public interface AccessorMinecraft {

    @Invoker(value = "clickMouse")
    void clickMouse();

    @Invoker(value = "rightClickMouse")
    void rightClickMouse();

    @Invoker(value = "middleClickMouse")
    void middleClickMouse();
}
