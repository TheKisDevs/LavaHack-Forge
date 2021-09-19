package com.kisman.cc.module.render;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import net.minecraft.util.EnumParticleTypes;

public class Particle extends Module {
    public Particle() {
        super("Particle", "Particle", Category.RENDER);
    }

    public void update() {
        mc.world.spawnParticle(EnumParticleTypes.HEART, mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.motionX, mc.player.motionY, mc.player.motionZ, 1);
    }
}
