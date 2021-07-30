package com.kisman.cc.module.combat;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import net.minecraft.entity.Entity;

public class AntiBot extends Module {
    public AntiBot() {
        super("AntiBot", "no bot targer", Category.COMBAT);
    }

    public void update() {
        for(Object entity : mc.world.loadedEntityList)
            if(((Entity)entity).isInvisible() && entity != mc.player)
                mc.world.removeEntity((Entity)entity);
    }
}
