package com.kisman.cc.features.module.player.rotatemodifier;

import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.ModuleInfo;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingEnum;
import com.kisman.cc.util.math.MathKt;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

@ModuleInfo(
        name = "YawStep",
        submodule = true
)
public class YawStep extends Module {

    private final SettingEnum<Mode> mode = new SettingEnum<>("Mode", this, Mode.Section).register();
    private final Setting section = register(new Setting("Section", this, 4, 1, 16, true).setVisible(mode.getValEnum() == Mode.Section));
    private final Setting angle = register(new Setting("Angle", this, 90, 1, 360, true).setVisible(mode.getValEnum() == Mode.Angle));
    private final Setting entities = register(new Setting("Entities", this, false));
    private final Setting settingIgnoreTicks = register(new Setting("IgnoreTicks", this, 4, 0, 20, true));
    private final Setting interpolate = register(new Setting("Interpolate", this, true));
    private final Setting speed = register(new Setting("Speed", this, 0.1, 0, 5, false).setVisible(interpolate::getValBoolean));

    private int ignoreTicks = 0;

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event){
        if(mc.player == null || mc.world == null)
            return;

        if(mc.mouseHelper.deltaX != 0 || mc.mouseHelper.deltaY != 0 || isAnyMouseButtonDown()){
            ignoreTicks = settingIgnoreTicks.getValInt();
            return;
        }

        if(ignoreTicks > 0){
            ignoreTicks -= 1;
            return;
        }

        float diff;
        if(mode.getValEnum() == Mode.Section)
            diff = 360.0f / section.getValInt();
        else
            diff = angle.getValFloat();
        float yaw = mc.player.rotationYaw + 180.0f;
        float t = yaw;
        yaw = Math.round((yaw / diff)) * diff;
        yaw += (t - yaw);
        yaw -= 180.0f;
        mc.player.prevRotationYaw = mc.player.rotationYaw;
        mc.player.rotationYaw = interpolate.getValBoolean() ? MathKt.interpolateTo(mc.player.rotationYaw, yaw, mc.getRenderPartialTicks(), speed.getValFloat()) : yaw;

        Entity entity = mc.player.getRidingEntity();
        if(entities.getValBoolean() && entity != null){
            entity.prevRotationYaw = entity.rotationYaw;
            entity.rotationYaw = mc.player.rotationYaw;
        }
    }

    private static boolean isAnyMouseButtonDown(){
        for(int i = 0; i < Mouse.getButtonCount(); i++)
            if(Mouse.isButtonDown(i))
                return true;
        return false;
    }

    private enum Mode {
        Section,
        Angle
    }
}
