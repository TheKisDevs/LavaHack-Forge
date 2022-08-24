package com.kisman.cc.features.module.player;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import net.minecraft.potion.Potion;

import java.util.Objects;

public class AntiWeakness extends Module {

    public AntiWeakness(){
        super("AntiWeakness", Category.PLAYER);
    }

    @Override
    public void update(){
        if(mc.player == null || mc.world == null)
            return;

        if(mc.player.isPotionActive(Objects.requireNonNull(Potion.getPotionFromResourceLocation("weakness"))))
            mc.player.removeActivePotionEffect(Potion.getPotionFromResourceLocation("weakness"));
    }
}
