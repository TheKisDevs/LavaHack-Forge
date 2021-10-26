package com.kisman.cc.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.Event;
import com.kisman.cc.event.events.EventPlayerMotionUpdate;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.entity.EntityPlayerSP;

public class SafeWalk extends Module {
    public SafeWalk() {
        super("SafeWalk", "SafeWalk", Category.MOVEMENT);
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);
    }

    @EventHandler
    private final Listener<EventPlayerMotionUpdate> listener = new Listener<>(event -> {
        if(event.getEra() == Event.Era.PRE) {
            double x = event.getX();
            double y = event.getY();
            double z = event.getZ();
            if (SafeWalk.mc.player.onGround) {
                double increment = 0.05;
                while (x != 0.0 && this.isOffsetBBEmpty(x, -1.0, 0.0)) {
                    if (x < increment && x >= -increment) {
                        x = 0.0;
                        continue;
                    }
                    if (x > 0.0) {
                        x -= increment;
                        continue;
                    }
                    x += increment;
                }
                while (z != 0.0 && this.isOffsetBBEmpty(0.0, -1.0, z)) {
                    if (z < increment && z >= -increment) {
                        z = 0.0;
                        continue;
                    }
                    if (z > 0.0) {
                        z -= increment;
                        continue;
                    }
                    z += increment;
                }
                while (x != 0.0 && z != 0.0 && this.isOffsetBBEmpty(x, -1.0, z)) {
                    x = x < increment && x >= -increment ? 0.0 : (x > 0.0 ? (x -= increment) : (x += increment));
                    if (z < increment && z >= -increment) {
                        z = 0.0;
                        continue;
                    }
                    if (z > 0.0) {
                        z -= increment;
                        continue;
                    }
                    z += increment;
                }
            }
            event.setX(x);
            event.setY(y);
            event.setZ(z);

            event.cancel();
        }
    });

    public boolean isOffsetBBEmpty(double offsetX, double offsetY, double offsetZ) {
        EntityPlayerSP playerSP = SafeWalk.mc.player;
        return SafeWalk.mc.world.getCollisionBoxes(playerSP, playerSP.getEntityBoundingBox().offset(offsetX, offsetY, offsetZ)).isEmpty();
    }
}
