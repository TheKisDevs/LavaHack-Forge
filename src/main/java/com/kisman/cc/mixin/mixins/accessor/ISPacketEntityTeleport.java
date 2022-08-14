package com.kisman.cc.mixin.mixins.accessor;

import net.minecraft.network.play.server.SPacketEntityTeleport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SPacketEntityTeleport.class)
public interface ISPacketEntityTeleport {

    @Accessor(value = "posX")
    public void setPosX(double x);

    @Accessor(value = "posY")
    public void setPosY(double y);

    @Accessor(value = "posZ")
    public void setPosZ(double z);
}
