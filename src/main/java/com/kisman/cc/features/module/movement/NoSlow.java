package com.kisman.cc.features.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventPlayerMotionUpdate;
import com.kisman.cc.event.events.EventPlayerUpdateMoveState;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.gui.console.ConsoleGui;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.mixin.mixins.accessor.AccessorEntityPlayer;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingEnum;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.util.entity.EntityUtil;
import com.kisman.cc.util.movement.MovementUtil;
import com.kisman.cc.util.world.WorldUtilKt;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemShield;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class NoSlow extends Module {
    private final Setting mode = register(new Setting("Mode", this, Mode.None));

    private final Setting items = register(new Setting("Items", this, true));
    private final Setting itemsTest = register(new Setting("Items Test", this, false));
    private final Setting ncpStrict = register(new Setting("NCPStrict", this, false));
    private final Setting strict = register(new Setting("Strict", this, false));
    private final Setting slimeBlocks = register(new Setting("SlimeBlocks", this, true));
    private final Setting web = register(new Setting("Web", this, false));
    private final SettingEnum<WebStrict> webStrict = new SettingEnum<>("WebStrict", this, WebStrict.None).register();

    private final Setting sneak = register(new Setting("Sneak", this, false));
    public final Setting jump = register(new Setting("Jump", this, false));

    private final SettingGroup invMoveGroup = register(new SettingGroup(new Setting("Inv Move", this)));

    private final Setting invMove = invMoveGroup.add(register(new Setting("InvMove", this, true)));
    private final Setting ignoreChat = invMoveGroup.add(register(new Setting("IgnoreChat", this, true).setVisible(invMove::getValBoolean)));
    private final Setting ignoreConsole = invMoveGroup.add(register(new Setting("IgnoreConsole", this, true).setVisible(invMove::getValBoolean)));
    private final Setting ignoreClickGui = invMoveGroup.add(register(new Setting("IgnoreClickGui", this, false).setVisible(invMove::getValBoolean)));

    public static NoSlow instance;

    private boolean webSwitch = false;

    private int teleportId = 0;

    public NoSlow() {
        super("NoSlow", "NoSlow", Category.MOVEMENT);

        instance = this;
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener);
        Kisman.EVENT_BUS.subscribe(listener2);
        Kisman.EVENT_BUS.subscribe(listener3);
        Kisman.EVENT_BUS.subscribe(listener4);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);
        Kisman.EVENT_BUS.unsubscribe(listener2);
        Kisman.EVENT_BUS.unsubscribe(listener3);
        Kisman.EVENT_BUS.unsubscribe(listener4);
        webSwitch = false;
        teleportId = 0;
    }

    public void update() {
        if(mc.player == null || mc.world == null) return;

        if (mc.player.isHandActive() && items.getValBoolean()) if (mc.player.getHeldItem(mc.player.getActiveHand()).getItem() instanceof ItemShield) if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0 && mc.player.getItemInUseMaxCount() >= 8) mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));

        if(slimeBlocks.getValBoolean()) {
            if(mc.player.getRidingEntity() != null) Blocks.SLIME_BLOCK.setDefaultSlipperiness(0.8f);
            else Blocks.SLIME_BLOCK.setDefaultSlipperiness(0.6f);
        }

        if (mc.player.isHandActive() && !mc.player.isRiding() && mc.player.fallDistance > 0.7 && MovementUtil.isMoving() && mode.getValString().equals("None")) {
            mc.player.motionX *= 0.9;
            mc.player.motionZ *= 0.9;
        }

        if(sneak.getValBoolean()) doSneak();

        if(!web.getValBoolean())
            return;

        if(mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)).getBlock() != Blocks.WEB || !mc.player.isInWeb)
            return;

        if(webSwitch || webStrict.getValEnum() == WebStrict.None){
            double[] unwebbed = EntityUtil.unwebMotion(new double[]{mc.player.motionX, mc.player.motionY, mc.player.motionZ});
            mc.player.motionX = unwebbed[0];
            mc.player.motionY = unwebbed[1];
            mc.player.motionZ = unwebbed[2];
        }

        if(webStrict.getValEnum() == WebStrict.Full){

            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + mc.player.motionX, mc.player.posY + mc.player.motionY, mc.player.posZ + mc.player.motionZ, mc.player.onGround));
            mc.player.connection.sendPacket(new CPacketConfirmTeleport(teleportId));
        }

        webSwitch = !webSwitch;
    }

    private void doSneak() {
        if(mc.player.isSneaking()) {
            if(mc.gameSettings.keyBindForward.isKeyDown()) {
                mc.player.jumpMovementFactor = 0.1f;

                if(mc.player.onGround) {
                    mc.player.motionX *= 5;
                    mc.player.motionZ *= 5;
                    mc.player.motionX /= 3.1495;
                    mc.player.motionZ /= 3.1495;
                    MovementUtil.strafe(0.1245f);

                    if(mc.gameSettings.keyBindBack.isKeyDown()) {
                        mc.player.jumpMovementFactor = 0.08f;

                        if(mc.player.onGround) {
                            mc.player.motionX *= -5;
                            mc.player.motionZ *= -5;
                            mc.player.motionX /= -3.1495;
                            mc.player.motionZ /= -3.1495;
                            MovementUtil.strafe(0.1245f);
                        }
                    }
                }
            }
        } else {
            mc.player.jumpMovementFactor = 0.02f;
            ((AccessorEntityPlayer) mc.player).setSpeedInAir(0.02f);
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingUpdateEvent event) {
        if(mc.player == null || mc.world == null) return;

        if (mc.player.isHandActive() && !mc.player.isRiding() && mode.getValString().equals("Sunrise")) {
            mc.player.movementInput.moveStrafe *= 0.2F;
            mc.player.movementInput.moveForward *= 0.2F;
            mc.player.sprintToggleTimer = 0;
        }
        if (mc.player.isHandActive() && !mc.player.isRiding() && itemsTest.getValBoolean()) {
            if (mc.player.ticksExisted % 2 == 0) {
                if (mc.player.onGround) {
                    if (!mc.player.isSprinting()) MovementUtil.setMotion(MovementUtil.WALK_SPEED - 0.2);
                    else MovementUtil.setMotion(MovementUtil.WALK_SPEED - 0.21);
                } else {
                    mc.player.motionX *= 0.9f;
                    mc.player.motionZ *= 0.9f;
                }
            }
        }
    }

    @EventHandler
    private final Listener<EventPlayerUpdateMoveState> listener = new Listener<>(event -> {
        if (invMove.getValBoolean() && mc.currentScreen != null) {
            if(mc.currentScreen instanceof GuiChat && ignoreChat.getValBoolean()) return;
            if(mc.currentScreen instanceof ConsoleGui && ignoreConsole.getValBoolean()) return;
            if(mc.currentScreen instanceof HalqGui && ignoreClickGui.getValBoolean()) return;

            mc.player.movementInput.moveStrafe = 0.0F;
            mc.player.movementInput.moveForward = 0.0F;

            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()));

            if (Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())) {
                ++mc.player.movementInput.moveForward;
                mc.player.movementInput.forwardKeyDown = true;
            } else mc.player.movementInput.forwardKeyDown = false;

            KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode()));
            if (Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())) {
                --mc.player.movementInput.moveForward;
                mc.player.movementInput.backKeyDown = true;
            } else mc.player.movementInput.backKeyDown = false;

            KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode()));
            if (Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode())) {
                ++mc.player.movementInput.moveStrafe;
                mc.player.movementInput.leftKeyDown = true;
            } else mc.player.movementInput.leftKeyDown = false;

            KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode()));
            if (Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode())) {
                --mc.player.movementInput.moveStrafe;
                mc.player.movementInput.rightKeyDown = true;
            } else mc.player.movementInput.rightKeyDown = false;

            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()));
            mc.player.movementInput.jump = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
        }

        if(items.getValBoolean() && mc.player.isHandActive() && !mc.player.isRiding()) {
            mc.player.movementInput.moveForward /= 0.2;
            mc.player.movementInput.moveStrafe /= 0.2;
        }
    });

    @EventHandler private final Listener<PacketEvent.PostSend> listener2 = new Listener<>(event -> {
        if(
                event.getPacket() instanceof CPacketPlayer
                && ncpStrict.getValBoolean()
                && items.getValBoolean()
                && mc.player.isHandActive()
                && !mc.player.isRiding()
        ) mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, WorldUtilKt.playerPosition(), EnumFacing.DOWN));

        else if(
                strict.getValBoolean()
                &&
                (
                        event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock
                        || event.getPacket() instanceof CPacketPlayerTryUseItem
                )
                &&
                (
                        (
                                (
                                        mc.player.getHeldItemMainhand().getItem() instanceof ItemFood
                                        || mc.player.getHeldItemMainhand().getItem() instanceof ItemBow
                                        || mc.player.getHeldItemMainhand().getItem() instanceof ItemShield
                                )
                                &&
                                getHand(event.getPacket()) == EnumHand.MAIN_HAND
                        )
                        ||
                        (
                                (
                                        mc.player.getHeldItemOffhand().getItem() instanceof ItemFood
                                        || mc.player.getHeldItemOffhand().getItem() instanceof ItemBow
                                        || mc.player.getHeldItemOffhand().getItem() instanceof ItemShield
                                )
                                &&
                                getHand(event.getPacket()) == EnumHand.OFF_HAND
                        )
                )
        ) mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
    });

    private final Listener<EventPlayerMotionUpdate> listener3 = new Listener<>(event -> {
    });

    private final Listener<PacketEvent.Receive> listener4 = new Listener<>(event -> {
        if(!(event.getPacket() instanceof SPacketPlayerPosLook))
            return;
        teleportId = ((SPacketPlayerPosLook) event.getPacket()).getTeleportId();
    });

    private EnumHand getHand(Packet<?> packet) {
        return packet instanceof CPacketPlayerTryUseItem ? ((CPacketPlayerTryUseItem) packet).getHand() : ((CPacketPlayerTryUseItemOnBlock) packet).getHand();
    }

    public enum Mode {None, Sunrise}

    private enum WebStrict {None, Semi, Full}
}
