package com.kisman.cc.mixin.mixins;

import com.kisman.cc.gui.other.container.ItemESP;
import com.kisman.cc.features.module.render.ContainerModifier;
import com.kisman.cc.util.render.Render2DUtil;
import com.kisman.cc.util.render.ColorUtils;
import com.kisman.cc.util.render.objects.screen.AbstractGradient;
import com.kisman.cc.util.render.objects.screen.Vec4d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.*;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.Set;

@SuppressWarnings({"unused", "IntegerDivisionInFloatingPointContext"})
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
                itemESP.getGuiTextField().drawTextBox();
            }
        }
    }

    @Inject(method = "drawScreen", at = @At("HEAD"))
    private void doDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (itemESP != null) {
            itemESP.update(guiLeft, guiTop, xSize, ySize);
            itemESP.getItemStacks().clear();
            if(!itemESP.getGuiTextField().getText().isEmpty()) for(Slot slot : inventorySlots.inventorySlots) if(slot.getHasStack() && slot.getStack().getDisplayName().toLowerCase().contains(itemESP.getGuiTextField().getText().toLowerCase()))  itemESP.getItemStacks().add(slot.getStack());
        }
    }

    /**
     * @author _kisman_
     * @reason nya~ uwa~ owa~
     */
    @Overwrite
    protected void keyTyped(char typedChar, int keyCode) {
        if(ContainerModifier.instance.itemESP.getValBoolean()) itemESP.getGuiTextField().textboxKeyTyped(typedChar, keyCode);
        if (keyCode == 1 || (this.mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode) && !itemESP.getGuiTextField().isFocused())) mc.player.closeScreen();
        checkHotbarKeys(keyCode);
        if (hoveredSlot != null && hoveredSlot.getHasStack()) {
            if (mc.gameSettings.keyBindPickBlock.isActiveAndMatches(keyCode)) handleMouseClick(hoveredSlot, hoveredSlot.slotNumber, 0, ClickType.CLONE);
            else if (mc.gameSettings.keyBindDrop.isActiveAndMatches(keyCode)) handleMouseClick(hoveredSlot, hoveredSlot.slotNumber, isCtrlKeyDown() ? 1 : 0, ClickType.THROW);
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void doMouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        if(ContainerModifier.instance.isToggled() && ContainerModifier.instance.itemESP.getValBoolean() && itemESP != null) {
            itemESP.getGuiTextField().mouseClicked(mouseX, mouseY, mouseButton);
            if(itemESP.getGuiTextField().isFocused()) ci.cancel();
        }
    }

    private boolean flag = true;

    @Inject(method = "drawSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;enableDepth()V"))
    private void drawSlotHook(Slot slot, CallbackInfo ci) {
        try {
            if (ContainerModifier.instance.isToggled() && ContainerModifier.instance.itemESP.getValBoolean() && !itemESP.getItemStacks().isEmpty() && itemESP.getItemStacks().contains(slot.getStack())) Render2DUtil.drawRect(slot.xPos, slot.yPos, slot.xPos + 16, slot.yPos + 16, ColorUtils.astolfoColors(100, 100));
        } catch(Exception e) {
            if(flag) {
                e.printStackTrace();
                flag = false;
            }
        }
    }
}
