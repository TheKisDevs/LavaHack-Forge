package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.Event;
import com.kisman.cc.event.events.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerControllerMP.class, priority = 10000)
public class MixinPlayerControllerMP {
    @Shadow public GameType currentGameType;
    @Shadow @Final public Minecraft mc;

    @Shadow public int currentPlayerItem;

    @Shadow @Final public NetHandlerPlayClient connection;

    /**
     * @author _kisman_
     * @reason no way ong
     */
    @Overwrite
    public void syncCurrentPlayItem() {
        try {
            int i = mc.player.inventory.currentItem;
            if (i != currentPlayerItem) {
                currentPlayerItem = i;
                connection.sendPacket(new CPacketHeldItemChange(currentPlayerItem));
            }
        } catch(Exception ignored) { }
    }

    @Inject(method = "getBlockReachDistance", at = @At("HEAD"), cancellable = true)
    public void getBlockReachDistance(CallbackInfoReturnable<Float> callback) {
        float attrib = (float)mc.player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
        EventBlockReachDistance event = new EventBlockReachDistance(currentGameType.isCreative() ? attrib : attrib - 0.5F);
        Kisman.EVENT_BUS.post(event);
        callback.setReturnValue(event.getDistance());
    }

    @Inject(method = "clickBlock", at = @At("HEAD"), cancellable = true)
    private void clickBlock(BlockPos posBlock, EnumFacing directionFacing, CallbackInfoReturnable<Boolean> cir) {
        EventDamageBlock event = new EventDamageBlock(posBlock, directionFacing);
        Kisman.EVENT_BUS.post( event);
        if (event.isCancelled()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Dynamic
    @Inject(
        method = "onPlayerDestroyBlock",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/block/Block.removedByPlayer" +
                    "(Lnet/minecraft/block/state/IBlockState;" +
                     "Lnet/minecraft/world/World;" +
                     "Lnet/minecraft/util/math/BlockPos;" +
                     "Lnet/minecraft/entity/player/EntityPlayer;Z)Z",
            remap = false),
        cancellable = true)
    private void onPlayerDestroyBlockHook(BlockPos pos,
                                          CallbackInfoReturnable<Boolean> info)
    {
        DestroyBlockEvent event = new DestroyBlockEvent(Event.Era.PRE, pos);
        Kisman.EVENT_BUS.post(event);
        if (event.isCancelled())
        {
            info.setReturnValue(false);
        }
    }

    @Inject(method = "onPlayerDamageBlock", at = @At("HEAD"), cancellable = true)
    private void onPlayerDamageBlockPre(BlockPos pos, EnumFacing facing, CallbackInfoReturnable<Boolean> cir){
        EventPlayerDamageBlock.Pre event = new EventPlayerDamageBlock.Pre(pos, facing);
        Kisman.EVENT_BUS.post(event);
        if(event.isCancelled()){
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Inject(method = "onPlayerDamageBlock", at = @At("RETURN"))
    private void onPlayerDamageBlockPost(BlockPos posBlock, EnumFacing directionFacing, CallbackInfoReturnable<Boolean> cir){
        EventPlayerDamageBlock.Post event = new EventPlayerDamageBlock.Post(posBlock, directionFacing);
        Kisman.EVENT_BUS.post(event);
    }

    @Inject(method = "onPlayerDamageBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;syncCurrentPlayItem()V", shift = At.Shift.AFTER), cancellable = true)
    private void onPlayerDamageBlockAfter(BlockPos posBlock, EnumFacing directionFacing, CallbackInfoReturnable<Boolean> cir){
        EventPlayerDamageBlock.After event = new EventPlayerDamageBlock.After(posBlock, directionFacing);
        Kisman.EVENT_BUS.post(event);
        if(event.isCancelled()){
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Inject(method = "onStoppedUsingItem", at = @At("HEAD"), cancellable = true)
    private void onStoppedUsingItem(EntityPlayer player, CallbackInfo ci){
        EventStopUsingItem event = new EventStopUsingItem(player);
        Kisman.EVENT_BUS.post(event);
        if(event.isCancelled())
            ci.cancel();
    }
}
