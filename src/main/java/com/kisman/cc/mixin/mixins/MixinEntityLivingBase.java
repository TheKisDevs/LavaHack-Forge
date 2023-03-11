package com.kisman.cc.mixin.mixins;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.event.events.RotationMoveEvent;
import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventArmSwingAnimationEnd;
import com.kisman.cc.features.module.Debug.FrostWalk;
import com.kisman.cc.features.module.movement.NoSlow;
import com.kisman.cc.features.module.render.SwingProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentFrostWalker;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static org.spongepowered.asm.lib.Opcodes.GETFIELD;

@SuppressWarnings("ConstantConditions")
@Mixin(value = EntityLivingBase.class, priority = 10000)
public abstract class MixinEntityLivingBase extends MixinEntity {
    @Shadow public EnumHand swingingHand;
    @Shadow public ItemStack activeItemStack;
    @Shadow public float moveStrafing;
    @Shadow public float moveVertical;
    @Shadow public float moveForward;
    @Shadow protected void jump() {}
    @Shadow public boolean isElytraFlying() {return true;}
    @Shadow public boolean isPotionActive(Potion potionIn) {return false;}
    @Shadow public PotionEffect getActivePotionEffect(Potion potionIn) {return null;}
    @Shadow protected void entityInit() {}
    @Shadow public void readEntityFromNBT(NBTTagCompound nbtTagCompound) {}
    @Shadow public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {}

    @Shadow public float swingProgress;

    @Shadow public int swingProgressInt;

    @Shadow protected float getJumpUpwardsMotion() { return 0f; }

    @Shadow public abstract ItemStack getHeldItemMainhand();

    @Shadow public abstract IAttributeInstance getEntityAttribute(IAttribute attribute);

    @Shadow public abstract boolean isOnLadder();

    @Inject(method = "getArmSwingAnimationEnd", at = @At("HEAD"), cancellable = true)
    private void yesido(CallbackInfoReturnable<Integer> cir) {
        int armSwingAnimationEnd;
        if (this.isPotionActive(MobEffects.HASTE)) armSwingAnimationEnd = 6 - (1 + this.getActivePotionEffect(MobEffects.HASTE).getAmplifier());
        else armSwingAnimationEnd = this.isPotionActive(MobEffects.MINING_FATIGUE) ? 6 + (1 + this.getActivePotionEffect(MobEffects.MINING_FATIGUE).getAmplifier()) * 2 : 6;
        EventArmSwingAnimationEnd event = new EventArmSwingAnimationEnd(armSwingAnimationEnd);
        Kisman.EVENT_BUS.post(event);
        cir.setReturnValue(event.getArmSwingAnimationEnd());
        cir.cancel();
    }

    private RotationMoveEvent jumpRotationEvent;

    @Inject(
            method = "jump",
            at = @At("HEAD"),
            cancellable = true
    )
    private void preMoveRelative(CallbackInfo ci) {
        // noinspection ConstantConditions
        if (EntityPlayerSP.class.isInstance(this)) {
            IBaritone baritone = BaritoneAPI.getProvider().getBaritoneForPlayer((EntityPlayerSP) (Object) this);
            if (baritone != null) {
                this.jumpRotationEvent = new RotationMoveEvent(RotationMoveEvent.Type.JUMP, this.rotationYaw);
                baritone.getGameEventHandler().onPlayerRotationMove(this.jumpRotationEvent);
            }
        }

        if(NoSlow.instance.jump.getValBoolean() && Minecraft.getMinecraft().player.getName().equals(getName())) {
            motionY = getJumpUpwardsMotion() + ((isPotionActive(MobEffects.JUMP_BOOST)) ? (getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1F : 0);
            isAirBorne = true;
            ForgeHooks.onLivingJump((EntityLivingBase) (Object) this);
            ci.cancel();
        }
    }

    @Redirect(
            method = "jump",
            at = @At(
                    value = "FIELD",
                    opcode = GETFIELD,
                    target = "net/minecraft/entity/EntityLivingBase.rotationYaw:F"
            )
    )
    private float overrideYaw(EntityLivingBase self) {
        if (self instanceof EntityPlayerSP && BaritoneAPI.getProvider().getBaritoneForPlayer((EntityPlayerSP) (Object) this) != null) {
            return this.jumpRotationEvent.getYaw();
        }
        return self.rotationYaw;
    }

    @Redirect(
            method = "travel",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/entity/EntityLivingBase.moveRelative(FFFF)V"
            )
    )
    private void travel(EntityLivingBase self, float strafe, float up, float forward, float friction) {
        // noinspection ConstantConditions
        if (!EntityPlayerSP.class.isInstance(this) || BaritoneAPI.getProvider().getBaritoneForPlayer((EntityPlayerSP) (Object) this) == null) {
            moveRelative(strafe, up, forward, friction);
            return;
        }
        RotationMoveEvent motionUpdateRotationEvent = new RotationMoveEvent(RotationMoveEvent.Type.MOTION_UPDATE, this.rotationYaw);
        BaritoneAPI.getProvider().getBaritoneForPlayer((EntityPlayerSP) (Object) this).getGameEventHandler().onPlayerRotationMove(motionUpdateRotationEvent);
        float originalYaw = this.rotationYaw;
        this.rotationYaw = motionUpdateRotationEvent.getYaw();
        this.moveRelative(strafe, up, forward, friction);
        this.rotationYaw = originalYaw;
    }

    @Inject(method = "frostWalk", at = @At("HEAD"), cancellable = true)
    public void frostWalk(BlockPos pos, CallbackInfo ci){
        if(!FrostWalk.INSTANCE.isToggled())
            return;

        EnchantmentFrostWalker.freezeNearby(Minecraft.getMinecraft().player, this.world, pos, FrostWalk.INSTANCE.level.getValInt());

        ci.cancel();
    }

    @Inject(method = "updateArmSwingProgress", at = @At("HEAD"), cancellable = true)
    public void updateArmSwingProgress(CallbackInfo ci){
        if(!SwingProgress.INSTANCE.isToggled())
            return;
        this.swingProgressInt = SwingProgress.INSTANCE.progress.getValInt();
        this.swingProgress = (float) this.swingProgressInt / 6.0f;
        ci.cancel();
    }
}
