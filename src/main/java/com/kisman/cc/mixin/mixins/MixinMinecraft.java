 package com.kisman.cc.mixin.mixins;

 import com.kisman.cc.RPC;
 import com.kisman.cc.file.SaveConfig;
 import com.kisman.cc.module.client.DiscordRPC;
 import net.minecraft.crash.CrashReport;
 import org.spongepowered.asm.mixin.Mixin;

 import net.minecraft.client.Minecraft;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

 @Mixin(Minecraft.class)
 public class MixinMinecraft {

     @Inject(method = "crashed", at = @At("HEAD"))
     public void crashed(CrashReport crash, CallbackInfo callbackInfo) {
         SaveConfig.init();

/*         if(DiscordRPC.instance.isToggled()) {
             RPC.stopRPC();
         }*/
     }

     @Inject(method = "shutdown", at = @At("HEAD"))
     public void shutdown(CallbackInfo callbackInfo) {
         SaveConfig.init();


/*         if(DiscordRPC.instance.isToggled()) {
             RPC.stopRPC();
         }*/
     }
 }
