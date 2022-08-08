package com.kisman.cc.features.module.player;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.render.Rendering;
import com.kisman.cc.util.render.objects.world.Box;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Work in progress
 * @author Cubic
 */
@SuppressWarnings("rawtypes")
public class ChorusDelay extends Module {
    private final Setting teleportKey = register(new Setting("Teleport", this, Keyboard.KEY_LSHIFT));

    private final Queue<Packet<?>> packets = new LinkedList<>();
    private SPacketPlayerPosLook posLook = null;
    private BlockPos pos = null;
    private boolean chorusCheck = false, posTP = false;

    public ChorusDelay(){
        super("ChorusDelay", Category.PLAYER);
    }

    @SubscribeEvent
    public void onFinishEat(LivingEntityUseItemEvent.Finish event) {
        if(event.getEntity() == mc.player && event.getItem().getItem() == Items.CHORUS_FRUIT) {
            chorusCheck = true;
            pos = mc.player.getPosition();
            posTP = false;
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if(posLook != null && chorusCheck) {
            Box box = Box.Companion.byAABB(mc.player.getEntityBoundingBox());
            box.setPos(new Vec3d(posLook.x, posLook.y, posLook.z));

            Rendering.draw(
                    Rendering.correct(box.toAABB()),
                    2f,
                    new Colour(255, 255, 255, 120),
                    Rendering.DUMMY_COLOR,
                    Rendering.Mode.BOTH
            );
        }
    }

    @EventHandler
    private final Listener<PacketEvent.Send> send = new Listener<>(event -> {
        if((event.getPacket() instanceof CPacketConfirmTeleport || event.getPacket() instanceof CPacketPlayer) && chorusCheck) {
            packets.add(event.getPacket());
            event.cancel();
        }
    });

    @EventHandler
    private final Listener<PacketEvent.Receive> receive = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketPlayerPosLook/* && chorusCheck*/) {
            posLook = (SPacketPlayerPosLook) event.getPacket();
        }
    });

    @Override
    public void onEnable(){
        super.onEnable();
        Kisman.EVENT_BUS.subscribe(send);
        Kisman.EVENT_BUS.subscribe(receive);
        reset();
    }

    @Override
    public void onDisable(){
        super.onDisable();
        Kisman.EVENT_BUS.unsubscribe(receive);
        Kisman.EVENT_BUS.unsubscribe(send);
        reset();
    }

    public void update() {
        if(mc.player == null || mc.world == null) return;

        if(chorusCheck) {
            if(!mc.player.getPosition().equals(pos) && !posTP) {
                if (mc.player.getDistance(pos.getX(),pos.getY(),pos.getZ()) > 1) {
                    mc.player.setPosition(pos.getX()  + 0.5F,pos.getY(),pos.getZ() + 0.5F);
                    posTP = true;
                }
            }

            if(Companion.isPressed(teleportKey)) doTeleport();
        }
    }

    private void reset() {
        chorusCheck = false;
        posLook = null;
        posTP = false;
        pos = null;
    }

    private void doTeleport() {
        reset();
        while(!packets.isEmpty()) {
            mc.player.connection.sendPacket(packets.poll());
        }
    }
}
