//package com.kisman.cc.mixin.mixins;
//
//import com.kisman.cc.Kisman;
//import com.kisman.cc.module.Module;
//import com.kisman.cc.settings.Setting;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.renderer.GlStateManager;
//import net.minecraft.client.renderer.RenderItem;
//import net.minecraft.client.renderer.block.model.IBakedModel;
//import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
//import net.minecraft.item.ItemStack;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//@Mixin(RenderItem.class)
//public abstract class MixinItemRenderer {
//    public Module viemModel = Kisman.instance.moduleManager.getModule("ViemModel");
//
//    Minecraft mc = Minecraft.getMinecraft();
//
//    @Inject(method = "renderItemModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderItem;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V", shift = At.Shift.BEFORE))
//    private void test(ItemStack stack, IBakedModel bakedmodel, ItemCameraTransforms.TransformType transform, boolean leftHanded, CallbackInfo ci) {
//        if(Kisman.instance.moduleManager.getModule("ViemModel").isToggled() && Kisman.instance.settingsManager.getSettingByName(Kisman.instance.moduleManager.getModule("ViemModel"), "RenderMode").getValString().equalsIgnoreCase("Strong") && mc.player != null && mc.world != null) {
//            GlStateManager.translate(getSet("LeftX").getValDouble(), getSet("LeftY").getValDouble(), getSet("LeftZ").getValDouble());
//        }
//        if(Kisman.instance.moduleManager.getModule("ViemModel").isToggled()) {
//            GlStateManager.translate(getSet("LeftX").getValDouble(), getSet("LeftY").getValDouble(), getSet("LeftZ").getValDouble());
//        }
//    }
//
//    private Setting getSet(String name) {
//        return Kisman.instance.settingsManager.getSettingByName(Kisman.instance.moduleManager.getModule("ViemModel"), name);
//    }
//}
