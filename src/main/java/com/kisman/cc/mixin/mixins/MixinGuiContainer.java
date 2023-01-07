package com.kisman.cc.mixin.mixins;

import com.kisman.cc.features.module.client.DevelopmentHelper;
import com.kisman.cc.features.module.render.ContainerModifier;
import com.kisman.cc.gui.other.container.ItemESP;
import com.kisman.cc.util.enums.DevelopmentHelperSlotTypes;
import com.kisman.cc.util.render.ColorUtils;
import com.kisman.cc.util.render.Render2DUtil;
import com.kisman.cc.util.render.customfont.CustomFontUtil;
import com.kisman.cc.util.render.objects.screen.AbstractGradient;
import com.kisman.cc.util.render.objects.screen.Vec4d;
import com.kisman.cc.websockets.WebSocketsManagerKt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.Set;

@SuppressWarnings({"unused", "IntegerDivisionInFloatingPointContext", "ConstantConditions"})
@Mixin(value = GuiContainer.class, priority = 10000)
public class MixinGuiContainer extends GuiScreen {
    @Shadow protected int guiLeft, guiTop, xSize, ySize;
    @Shadow public Container inventorySlots;
    @Shadow private ItemStack draggedStack;
    @Shadow private Slot clickedSlot;
    @Shadow private boolean isRightMouseClick;
    @Shadow protected boolean dragSplitting;
    @Shadow @Final protected Set<Slot> dragSplittingSlots;
    @Shadow private void updateDragSplitting() {}
    @Shadow private int dragSplittingLimit;
    @Shadow protected boolean checkHotbarKeys(int keyCode) {return false;}
    @Shadow private Slot hoveredSlot;
    @Shadow protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {}

    public ItemESP itemESP = new ItemESP();

    private boolean flag2 = false;

    @Inject(method = "drawScreen", at = @At("TAIL"))
    public void drawee(int mouseX, int mouseY, float particalTicks, CallbackInfo ci) {
        if(ContainerModifier.instance.isToggled()) {
            if(ContainerModifier.instance.containerShadow.getValBoolean()) {
                {
                    double x = 0, y = (guiTop + xSize / 2) - guiLeft / 2, y2 = (guiTop + xSize / 2) + guiLeft / 2;
                    double x2 = guiLeft, y3 = guiTop, y4 = guiTop + ySize;

                    Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[]{x, y}, new double[]{x2, y3}, new double[]{x2, y4}, new double[]{x, y2}), Color.BLACK, new Color(0, 0, 0, 0), false));
                }

                {
                    double x = new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth_double(), y = (guiTop + xSize / 2) - guiLeft / 2, y2 = (guiTop + xSize / 2) + guiLeft / 2;

                    Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[]{guiLeft + xSize, guiTop}, new double[]{x, y}, new double[]{x, y2}, new double[]{guiLeft + xSize, guiTop + ySize}), new Color(0, 0, 0, 0), Color.BLACK, false));
                }
            }

            if(ContainerModifier.instance.itemESP.getValBoolean()) {
                try {
                    itemESP.getGuiTextField().drawTextBox();
                } catch(Exception e) {
                    if(flag2) {
                        WebSocketsManagerKt.reportIssue("Got exception in drawScreen tail inject hook by ItemESP, stack trace: " + e);
                        flag2 = false;
                    }
                }
            }
        }
    }

    private boolean flag3 = false;

    @Inject(method = "drawScreen", at = @At("HEAD"))
    private void doDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (itemESP != null) {
            try {
                itemESP.update(guiLeft, guiTop, xSize, ySize);
                itemESP.getItemStacks().clear();
                if(!itemESP.getGuiTextField().getText().isEmpty()) for(Slot slot : inventorySlots.inventorySlots) if(slot.getHasStack() && slot.getStack().getDisplayName().toLowerCase().contains(itemESP.getGuiTextField().getText().toLowerCase()))  itemESP.getItemStacks().add(slot.getStack());
            } catch(Exception e) {
                if(flag3) {
                    WebSocketsManagerKt.reportIssue("Got exception in drawScreen head inject hook by ItemESP, stack trace: " + e);
                    flag3 = false;
                }
            }
        }
    }

    private boolean flag4 = true;

    @Inject(method = "keyTyped", at = @At("HEAD"))
    private void keyTypedHook(char typedChar, int keyCode, CallbackInfo ci) {
        try {
            if(ContainerModifier.instance.itemESP.getValBoolean()) itemESP.getGuiTextField().textboxKeyTyped(typedChar, keyCode);
        } catch(Exception e) {
            if(flag4) {
                WebSocketsManagerKt.reportIssue("Got exception in keyTyped head inject hook by ItemESP, stack trace: " + e);
                flag4 = false;
            }
        }
    }

    private boolean flag5 = true;

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void doMouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        if(ContainerModifier.instance.isToggled() && ContainerModifier.instance.itemESP.getValBoolean() && itemESP != null) {
            try {
                itemESP.getGuiTextField().mouseClicked(mouseX, mouseY, mouseButton);
                if(itemESP.getGuiTextField().isFocused()) ci.cancel();
            } catch(Exception e) {
                if(flag5) {
                    WebSocketsManagerKt.reportIssue("Got exception in mouseClicked head inject hook by ItemESP, stack trace: " + e);
                    flag5 = false;
                }
            }
        }
    }

    private boolean flag = true;

    @Inject(method = "drawSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;enableDepth()V"))
    private void drawSlotHook(Slot slot, CallbackInfo ci) {
        try {
            if (ContainerModifier.instance.isToggled() && ContainerModifier.instance.itemESP.getValBoolean() && !itemESP.getItemStacks().isEmpty() && itemESP.getItemStacks().contains(slot.getStack())) Render2DUtil.drawRect(slot.xPos, slot.yPos, slot.xPos + 16, slot.yPos + 16, ColorUtils.astolfoColors(100, 100));
            if(DevelopmentHelper.getInstance().isToggled() && DevelopmentHelper.getInstance().getDisplaySlots().getValBoolean()) {
                Render2DUtil.drawRect(slot.xPos, slot.yPos, slot.xPos + 16, slot.yPos + 16, Color.BLACK.getRGB());
                CustomFontUtil.drawString(String.valueOf(DevelopmentHelper.getInstance().getSlotType().getValEnum() == DevelopmentHelperSlotTypes.Index ? slot.getSlotIndex() : slot.slotNumber), slot.xPos, slot.yPos, -1);
            }
        } catch(Exception e) {
            if(flag) {
                WebSocketsManagerKt.reportIssue("Got exception in drawSlot invoke inject hook by ItemESP, stack trace: " + e);
                flag = false;
            }
        }
    }
}
