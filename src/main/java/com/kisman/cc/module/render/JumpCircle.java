package com.kisman.cc.module.render;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.RenderUtil;
import i.gishreloaded.gishcode.utils.TimerUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.*;
import java.util.List;

public class JumpCircle extends Module {
    private Setting maxRadius = new Setting("Max Radius", this, 0.95, .55, 3, false);
    private Setting red = new Setting("Red", this, 255, 0, 255, true);
    private Setting green = new Setting("Green", this, 255, 0, 255, true);
    private Setting blue = new Setting("Blue", this, 255, 0, 255, true);

    private List<Circle> circles = new ArrayList<>();
    private boolean jumped;

    public JumpCircle() {
        super("JumpCircle", Category.RENDER);

        setmgr.rSetting(maxRadius);
        setmgr.rSetting(red);
        setmgr.rSetting(green);
        setmgr.rSetting(blue);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (!mc.player.collidedVertically) jumped = true;
        if (jumped && mc.player.onGround && mc.player.collidedVertically) {
            Circle circle = new Circle(mc.player.getPosition(), maxRadius.getValDouble());
            if (!circles.contains(circle)) circles.add(circle);
            jumped = false;
        }

        if (!circles.isEmpty())
            circles.forEach(circle -> {
                circle.render();
                if (circle.renderRadius >= maxRadius.getValDouble() && circle.timer.hasReached(300)) circles.remove(circle);
            });
    }

    public class Circle {
        private TimerUtils timer = new TimerUtils();
        private BlockPos pos;
        private double maxRadius;
        private double renderRadius;

        public Circle(BlockPos pos, double maxRadius) {
            this.pos = pos;
            this.maxRadius = maxRadius;
            renderRadius = 0;
            timer.reset();
        }

        public void render() {
            if (renderRadius < maxRadius) timer.reset();
            renderRadius = Math.min(maxRadius, renderRadius + 0.06);

            RenderUtil.drawCircle(pos, renderRadius, new Color(red.getValInt(), green.getValInt(), blue.getValInt()));
        }
    }
}
