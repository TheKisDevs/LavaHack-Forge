 package com.kisman.cc.mixin.mixins;

 import com.kisman.cc.Kisman;
 import com.kisman.cc.file.SaveConfig;
 import com.kisman.cc.mixin.mixins.accessor.AccessorPlayerControllerMP;
 import com.kisman.cc.mixin.mixins.accessor.IEntityPlayerSP;
 import com.kisman.cc.module.client.SandBox;
 import com.kisman.cc.module.exploit.MultiTask;
 import net.minecraft.client.entity.EntityPlayerSP;
 import net.minecraft.client.multiplayer.PlayerControllerMP;
 import net.minecraft.client.settings.GameSettings;
 import net.minecraft.crash.CrashReport;
 import org.spongepowered.asm.mixin.Mixin;

 import net.minecraft.client.Minecraft;
 import org.spongepowered.asm.mixin.Shadow;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

 @Mixin(Minecraft.class)
 public class MixinMinecraft {
  @Shadow
  public GameSettings gameSettings;

  @Shadow
  public EntityPlayerSP player;

  @Shadow
  public PlayerControllerMP playerController;

  @Shadow
  protected void clickMouse() {};

  private boolean mt_handActive = false;
  private boolean mt_isHittingBlock = false;
     /*@Inject(method = "crashed", at = @At("HEAD"))
     private void crashed(CrashReport crash, CallbackInfo ci) {
         SaveConfig.init();
     }

     @Inject(method = "shutdown", at = @At("HEAD"))
     private void shutdown(CallbackInfo ci) {
         SaveConfig.init();
     }*/

     /*@Inject(method = "getLimitFramerate", at = @At("HEAD"), cancellable = true)
     private void getLimitFramerate(CallbackInfoReturnable<Integer> cir) {
         if((Kisman.instance.shaders.currentshader != null && Minecraft.getMinecraft().player == null) || (SandBox.instance.isToggled() && Kisman.instance.sandBoxShaders.currentshader != null && Minecraft.getMinecraft().player == null)) {
             cir.setReturnValue(60);
         }
     }*/

  // pasted from lambda who cares
  @Inject( method = "processKeyBinds", at = @At( value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;isKeyDown()Z", shift = At.Shift.BEFORE, ordinal = 2 ) )
  public void mt_processKeyBinds( CallbackInfo info ) {
   if(MultiTask.instance.isToggled()) {
    while( gameSettings.keyBindAttack.isPressed( ) )
     clickMouse( );
   }
  }

  @Inject( method = "rightClickMouse", at = @At( "HEAD" ) )
  public void mt_rightClickMouse( CallbackInfo info ) {
   if(MultiTask.instance.isToggled()) {
    mt_isHittingBlock = playerController.getIsHittingBlock();
    ((AccessorPlayerControllerMP) playerController).mm_setIsHittingBlock(false);
   }
  }

  @Inject(method = "rightClickMouse", at = @At("RETURN"))
  public void mt_rightClickMousePost(CallbackInfo ci) {
   if (MultiTask.instance.isToggled() && !playerController.getIsHittingBlock()) {
    ((AccessorPlayerControllerMP) playerController).mm_setIsHittingBlock(mt_isHittingBlock);
   }
  }

  @Inject(method = "sendClickBlockToController", at = @At("HEAD"))
  public void mt_sendClickBlockToControllerPre(boolean leftClick, CallbackInfo ci) {
   if (MultiTask.instance.isToggled()) {
    mt_handActive = player.isHandActive();
    ((IEntityPlayerSP) player).mm_setHandActive(false);
   }
  }

  @Inject(method = "sendClickBlockToController", at = @At("RETURN"))
  public void mt_sendClickBlockToControllerPost(boolean leftClick, CallbackInfo ci) {
   if (MultiTask.instance.isToggled() && !player.isHandActive()) {
    ((IEntityPlayerSP) player).mm_setHandActive(mt_handActive);
   }
  }
 }
