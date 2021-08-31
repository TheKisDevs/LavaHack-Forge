// package com.kisman.cc.mixin.mixins;

// import com.kisman.cc.Kisman;

// import org.spongepowered.asm.mixin.Mixin;
// import org.spongepowered.asm.mixin.Shadow;
// import org.spongepowered.asm.mixin.injection.At;
// import org.spongepowered.asm.mixin.injection.Inject;
// import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// import net.minecraft.client.Minecraft;
// import net.minecraft.client.entity.EntityPlayerSP;
// import net.minecraft.client.multiplayer.PlayerControllerMP;

// @Mixin(Minecraft.class)
// public class MixinMinecraft {
//     @Shadow
//     public EntityPlayerSP player;
//     @Shadow
//     public PlayerControllerMP playerController;

//     private boolean isHittingBlock = false;

//     // Sponsored by KAMI Blue
//     // https://github.com/kami-blue/client/blob/97a62ce0a3e165f445e46bc6ea0823020d1b14ae/src/main/java/org/kamiblue/client/mixin/client/MixinMinecraft.java#L84
//     @Inject(method = "rightClickMouse", at = @At("HEAD"))
//     public void rightClickMousePre(CallbackInfo ci) {
//         if(Kisman.instance.moduleManager.getModule("MultiTask").isToggled()) {
//             isHittingBlock = playerController.getIsHittingBlock();
//             playerController.isHittingBlock = false;
//         }
//     }

//     @Inject(method = "rightClickMouse", at = @At("RETURN"))
//     public void rightClickMousePost(CallbackInfo ci) {
//         if (Kisman.instance.moduleManager.getModule("MultiTask").isToggled() && !playerController.getIsHittingBlock()) {
//             playerController.isHittingBlock = isHittingBlock;
//         }
//     }
// }
