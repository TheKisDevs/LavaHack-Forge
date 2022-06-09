package com.kisman.cc.features.module.Debug;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import com.kisman.cc.util.math.Trigonometric;
import com.kisman.cc.util.render.Rendering;
import com.kisman.cc.util.world.BlockUtil;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.util.math.*;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Triangulation extends Module {

    private final Setting distance = register(new Setting("Distance", this, 4, 1, 10, false));

    public Triangulation(){
        super("Triangulation", Category.DEBUG);
    }

    @Override
    public void onEnable(){
        if(mc.player == null || mc.world == null)
            return;

        ChatUtility.message().printClientModuleMessage("yaw: " + mc.player.rotationYaw + " pitch: " + mc.player.rotationPitch);


        //pitch + 90
        //MathHelper.normalizeAngle(40, 360);

        Vec3d vec = BlockUtil.getEyesPos();
        Vec3d vec1 = Trigonometric.position(mc.player.rotationPitch, mc.player.rotationYaw, distance.getValDouble());
        RayTraceResult raytrace = mc.world.rayTraceBlocks(vec, vec.add(vec1));
        if(raytrace == null)
            return;
        BlockPos objectMouseOver = raytrace.getBlockPos();
        ChatUtility.message().printClientModuleMessage("x: " + objectMouseOver.getX() + " y: " + objectMouseOver.getY() + " z: " + objectMouseOver.getZ());

        toggle();
    }

    private RayTraceResult doProperly(double pitch, double yaw, double radius){
        double sRadians = yaw / 180.0 * Math.PI;
        double tRadians = pitch / 180.0 * Math.PI;
        double x = Math.cos(sRadians) * Math.sin(tRadians) * radius;
        double y = Math.cos(tRadians) * radius;
        double z = Math.sin(sRadians) * Math.sin(tRadians) * radius;
        Vec3d vec = new Vec3d(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ);
        Vec3d vec1 = vec.addVector(x, y, z);
        return mc.world.rayTraceBlocks(vec, vec1);
    }

    //@SubscribeEvent
    public void onRender(RenderWorldLastEvent event){
        if(mc.player == null || mc.world == null)
            return;
        if(!this.isToggled())
            return;
        /*
        double radius = distance.getValDouble();
        double pitch = mc.player.rotationPitch;
        double yaw = mc.player.rotationYaw;
        double pitchRadians = (pitch + 90.0) / 180.0 * Math.PI;
        double yawRadians = (MathHelper.normalizeAngle((int) yaw, 360)) / 180.0 * Math.PI;
        double sin = Math.sin(pitchRadians) * radius;
        double cos = Math.sin(pitchRadians + 1.5707963267948966) * radius;
        double sin2 = Math.sin(yawRadians) * radius;
        Vec3d eyePos = mc.player.getPositionEyes(event.getPartialTicks());
        RayTraceResult raytrace = mc.world.rayTraceBlocks(eyePos, new Vec3d(eyePos.x + sin, eyePos.y + cos, eyePos.z + sin2));
        if(raytrace == null)
            return;
        BlockPos objectMouseOver = raytrace.getBlockPos();
        ChatUtility.message().printClientModuleMessage("x: " + objectMouseOver.getX() + " y: " + objectMouseOver.getY() + " z: " + objectMouseOver.getZ());
         */
        //BlockPos objectMouseOver = new BlockPos(eyePos.x + sin, eyePos.y + cos, eyePos.z + sin2);
        Vec3d vec = mc.player.getPositionEyes(event.getPartialTicks());
        Vec3d vec1 = Trigonometric.position(mc.player.rotationPitch, mc.player.rotationYaw, distance.getValDouble());
        RayTraceResult raytrace = mc.world.rayTraceBlocks(vec, vec.add(vec1));
        if(raytrace == null)
            return;
        BlockPos objectMouseOver = raytrace.getBlockPos();
        Rendering.draw(Rendering.correct(new AxisAlignedBB(objectMouseOver)), 2.0f, new Colour(255, 255, 255, 120), Rendering.DUMMY_COLOR, Rendering.Mode.BOTH);
    }
}
