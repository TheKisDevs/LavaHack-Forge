 package com.kisman.cc.mixin.mixins;

 import com.kisman.cc.Kisman;
 import com.kisman.cc.file.SaveConfig;
 import com.kisman.cc.module.client.SandBox;
 import net.minecraft.crash.CrashReport;
 import org.spongepowered.asm.mixin.Mixin;

 import net.minecraft.client.Minecraft;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

 @Mixin(Minecraft.class)
 public class MixinMinecraft {
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
 }
