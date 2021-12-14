package com.kisman.cc.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.console.GuiConsole;
import com.kisman.cc.event.events.EventPlayerUpdateMoveState;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.oldclickgui.ClickGui;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.PlayerUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemShield;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;

public class NoSlow extends Module {
    private Setting invMove = new Setting("InvMove", this, true);
    private Setting items = new Setting("Items", this, true);
    private Setting ncpStrict = new Setting("NCPStrict", this, true);


    private Setting invLine = new Setting("InvLine", this, "InvMode");

    private Setting ignoreChat = new Setting("IgnoreChat", this, true);
    private Setting ignoreConsole = new Setting("IgnoreConsole", this, true);
    private Setting ignoreClickGui = new Setting("IgnoreClickGui", this, false);

    public NoSlow() {
        super("NoSlow", "NoSlow", Category.MOVEMENT);

        setmgr.rSetting(invMove);
        setmgr.rSetting(items);
        setmgr.rSetting(ncpStrict);

        setmgr.rSetting(invLine);
        setmgr.rSetting(ignoreChat);
        setmgr.rSetting(ignoreConsole);
        setmgr.rSetting(ignoreClickGui);
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener);
        Kisman.EVENT_BUS.subscribe(listener1);
        Kisman.EVENT_BUS.subscribe(listener2);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);
        Kisman.EVENT_BUS.unsubscribe(listener1);
        Kisman.EVENT_BUS.unsubscribe(listener2);
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;

        if (mc.player.isHandActive()) {
            if (mc.player.getHeldItem(mc.player.getActiveHand()).getItem() instanceof ItemShield) {
                if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0 && mc.player.getItemInUseMaxCount() >= 8) {
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
                }
            }
        }
    }

    @EventHandler
    private final Listener<EventPlayerUpdateMoveState> listener = new Listener<>(event -> {
        if (invMove.getValBoolean() && mc.currentScreen != null) {
            if(mc.currentScreen instanceof GuiChat && ignoreChat.getValBoolean()) return;
            if(mc.currentScreen instanceof GuiConsole && ignoreConsole.getValBoolean()) return;
            if(mc.currentScreen instanceof ClickGui && ignoreClickGui.getValBoolean()) return;

            mc.player.movementInput.moveStrafe = 0.0F;
            mc.player.movementInput.moveForward = 0.0F;

            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()));

            if (Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())) {
                ++mc.player.movementInput.moveForward;
                mc.player.movementInput.forwardKeyDown = true;
            } else {
                mc.player.movementInput.forwardKeyDown = false;
            }

            KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode()));
            if (Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())) {
                --mc.player.movementInput.moveForward;
                mc.player.movementInput.backKeyDown = true;
            } else {
                mc.player.movementInput.backKeyDown = false;
            }

            KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode()));
            if (Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode())) {
                ++mc.player.movementInput.moveStrafe;
                mc.player.movementInput.leftKeyDown = true;
            } else {
                mc.player.movementInput.leftKeyDown = false;
            }

            KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode()));
            if (Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode())) {
                --mc.player.movementInput.moveStrafe;
                mc.player.movementInput.rightKeyDown = true;
            } else {
                mc.player.movementInput.rightKeyDown = false;
            }

            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()));
            mc.player.movementInput.jump = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
        }
    });

    @EventHandler
    private final Listener<EventPlayerUpdateMoveState> listener1 = new Listener<>(event -> {
        if(items.getValBoolean() && mc.player.isHandActive() && !mc.player.isRiding()) {
            mc.player.movementInput.moveForward /= 0.2;
            mc.player.movementInput.moveStrafe /= 0.2;
        }
    });

    @EventHandler
    private final Listener<PacketEvent.PostSend> listener2 = new Listener<>(event -> {
        if(event.getPacket() instanceof CPacketPlayer) {
            if(ncpStrict.getValBoolean()) {
                if(items.getValBoolean() && mc.player.isHandActive() && !mc.player.isRiding()) {
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, PlayerUtil.GetLocalPlayerPosFloored(), EnumFacing.DOWN));
                }
            }
        }
    });
}
