package com.kisman.cc.mixin.mixins;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.event.events.ChatEvent;
import baritone.api.event.events.PlayerUpdateEvent;
import baritone.api.event.events.SprintStateEvent;
import baritone.api.event.events.type.EventState;
import baritone.behavior.LookBehavior;
import com.kisman.cc.Kisman;
import com.kisman.cc.event.Event;
import com.kisman.cc.event.events.EventPlayerMotionUpdate;
import com.kisman.cc.event.events.EventPlayerMove;
import com.kisman.cc.event.events.EventPlayerPushOutOfBlocks;
import com.kisman.cc.event.events.EventPlayerUpdate;
import com.kisman.cc.features.module.movement.MoveModifier;
import com.kisman.cc.mixin.mixins.accessor.IEntityPlayerSP;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.MobEffects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovementInput;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.kisman.cc.util.Globals.mc;

@SuppressWarnings("unused")
@Mixin(value = EntityPlayerSP.class, priority = 10000)
public class MixinEntityPlayerSP extends MixinAbstractClientPlayer implements IEntityPlayerSP {
    @Shadow public MovementInput movementInput;

    public MixinEntityPlayerSP(World worldIn, GameProfile gameProfileIn) {super(worldIn, gameProfileIn);}

    @Shadow protected boolean isCurrentViewEntity() {return true;}

    @Shadow private void onUpdateWalkingPlayer() {};

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void move(MoverType type, double x, double y, double z, CallbackInfo ci) {
        EventPlayerMove event = new EventPlayerMove(type, x, y, z);
        Kisman.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            move(type, event.x, event.y, event.z);
            ci.cancel();
        }
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"), cancellable = true)
    public void onPreUpdateWalkingPlayer(CallbackInfo ci) {
        EventPlayerMotionUpdate event = new EventPlayerMotionUpdate(Event.Era.PRE, rotationYaw, rotationPitch, this.posX, this.getEntityBoundingBox().minY, this.posZ, this.onGround);
        Kisman.EVENT_BUS.post(event);
        this.rotationYaw = event.getYaw();
        this.rotationPitch = event.getPitch();
        if(event.isCancelled()) ci.cancel();
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("RETURN"), cancellable = true)
    public void onPostUpdateWalkingPlayer(CallbackInfo ci) {
        EventPlayerMotionUpdate event = new EventPlayerMotionUpdate(Event.Era.POST, rotationYaw, rotationPitch, this.posX, this.getEntityBoundingBox().minY, this.posZ, this.onGround);
        Kisman.EVENT_BUS.post(event);
        if(event.isCancelled()) ci.cancel();
    }

    @Inject(method = "onUpdate", at = @At("HEAD"), cancellable = true)
    public void onUpdate(CallbackInfo ci) {
        EventPlayerUpdate event = new EventPlayerUpdate();
        Kisman.EVENT_BUS.post(event);
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(method = "pushOutOfBlocks(DDD)Z", at = @At("HEAD"), cancellable = true)
    public void pushOutOfBlocks(double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        EventPlayerPushOutOfBlocks event = new EventPlayerPushOutOfBlocks(x, y, z);
        Kisman.EVENT_BUS.post(event);
        if (event.isCancelled()) cir.setReturnValue(false);
    }

    @Override
    public void attackTargetEntityWithCurrentItem(@NotNull Entity targetEntity) {
        super.attackTargetEntityWithCurrentItem(targetEntity);

        if (((MoveModifier) Kisman.instance.moduleManager.getModule("MoveModifier")).getKeepSprint().getValBoolean() && targetEntity.canBeAttackedWithItem() && !targetEntity.hitByEntity(this)) {
            float f1;
            if (targetEntity instanceof EntityLivingBase) f1 = EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), ((EntityLivingBase) targetEntity).getCreatureAttribute());
            else f1 = EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), EnumCreatureAttribute.UNDEFINED);

            float f2 = this.getCooledAttackStrength(0.5F);
            float f = (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() * (0.2F + f2 * f2 * 0.8F);
            f1 = f1 * f2;

            if (f > 0.0F || f1 > 0.0F) {
                boolean flag2 = !isSprinting() && f2 > 0.9f && this.fallDistance > 0.0F && !this.onGround && !this.isOnLadder() && !this.isInWater() && !this.isPotionActive(MobEffects.BLINDNESS) && !this.isRiding() && targetEntity instanceof EntityLivingBase;
                CriticalHitEvent hitResult = ForgeHooks.getCriticalHit(this, targetEntity, flag2, flag2 ? 1.5F : 1.0F);
                if (targetEntity.attackEntityFrom(DamageSource.causePlayerDamage(this), (hitResult != null ? f * hitResult.getDamageModifier() : f) + f1) && EnchantmentHelper.getKnockbackModifier(this) + (isSprinting() && EnchantmentHelper.getKnockbackModifier(this) > 0.9 ? 1 : 0) > 0) {
                    mc.player.setSprinting(true);
                    motionX = (10 * motionX) / 6;
                    motionY = (10 * motionY) / 6;
                }
            }
        }
    }

    @Inject(
            method = "sendChatMessage",
            at = @At("HEAD"),
            cancellable = true
    )
    private void sendChatMessage(String msg, CallbackInfo ci) {
        ChatEvent event = new ChatEvent(msg);
        IBaritone baritone = BaritoneAPI.getProvider().getBaritoneForPlayer((EntityPlayerSP) (Object) this);
        if (baritone == null) {
            return;
        }
        baritone.getGameEventHandler().onSendChatMessage(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(
            method = "onUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/client/entity/EntityPlayerSP.isRiding()Z",
                    shift = At.Shift.BY,
                    by = -3
            )
    )
    private void onPreUpdate(CallbackInfo ci) {
        IBaritone baritone = BaritoneAPI.getProvider().getBaritoneForPlayer((EntityPlayerSP) (Object) this);
        if (baritone != null) {
            baritone.getGameEventHandler().onPlayerUpdate(new PlayerUpdateEvent(EventState.PRE));
        }
    }

    @Inject(
            method = "onUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/client/entity/EntityPlayerSP.onUpdateWalkingPlayer()V",
                    shift = At.Shift.BY,
                    by = 2
            )
    )
    private void onPostUpdate(CallbackInfo ci) {
        IBaritone baritone = BaritoneAPI.getProvider().getBaritoneForPlayer((EntityPlayerSP) (Object) this);
        if (baritone != null) {
            baritone.getGameEventHandler().onPlayerUpdate(new PlayerUpdateEvent(EventState.POST));
        }
    }

    @Redirect(
            method = "onLivingUpdate",
            at = @At(
                    value = "FIELD",
                    target = "net/minecraft/entity/player/PlayerCapabilities.allowFlying:Z"
            )
    )
    private boolean isAllowFlying(PlayerCapabilities capabilities) {
        IBaritone baritone = BaritoneAPI.getProvider().getBaritoneForPlayer((EntityPlayerSP) (Object) this);
        if (baritone == null) {
            return capabilities.allowFlying;
        }
        return !baritone.getPathingBehavior().isPathing() && capabilities.allowFlying;
    }

    @Redirect(
            method = "onLivingUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/client/settings/KeyBinding.isKeyDown()Z"
            )
    )

    private boolean isKeyDown(KeyBinding keyBinding) {
        IBaritone baritone = BaritoneAPI.getProvider().getBaritoneForPlayer((EntityPlayerSP) (Object) this);
        if (baritone == null) {
            return keyBinding.isKeyDown();
        }
        SprintStateEvent event = new SprintStateEvent();
        baritone.getGameEventHandler().onPlayerSprintState(event);
        if (event.getState() != null) {
            return event.getState();
        }
        if (baritone != BaritoneAPI.getProvider().getPrimaryBaritone()) {
            // hitting control shouldn't make all bots sprint
            return false;
        }
        return keyBinding.isKeyDown();
    }

    @Inject(
            method = "updateRidden",
            at = @At(
                    value = "HEAD"
            )
    )
    private void updateRidden(CallbackInfo cb) {
        IBaritone baritone = BaritoneAPI.getProvider().getBaritoneForPlayer((EntityPlayerSP) (Object) this);
        if (baritone != null) {
            ((LookBehavior) baritone.getLookBehavior()).pig();
        }
    }

    @Override
    public void invokeUpdateWalkingPlayer() {
        onUpdateWalkingPlayer();
    }
}
