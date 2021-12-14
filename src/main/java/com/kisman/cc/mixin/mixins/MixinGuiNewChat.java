package com.kisman.cc.mixin.mixins;

import com.kisman.cc.module.chat.ChatAnimation;
import com.kisman.cc.util.ColorUtil;
import com.kisman.cc.util.MathUtil;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = GuiNewChat.class, priority = 9999)
public abstract class MixinGuiNewChat {
    @Shadow
    private boolean isScrolled;
    private float percentComplete;
    private int newLines;
    private long prevMillis;
    private boolean configuring;
    private float animationPercent;
    private int lineBeingDrawn;

    public MixinGuiNewChat() {
        this.prevMillis = System.currentTimeMillis();
    }

    @Shadow
    public abstract float getChatScale();

    private void updatePercentage(final long diff) {
        if (percentComplete < 1.0f) {
            percentComplete += 0.004f * diff;
        }

        percentComplete = (float) MathUtil.clamp(percentComplete, 0.0, 1.0);
    }

    @Inject(method = "drawChat", at = @At("HEAD"), cancellable = true)
    private void modifyChatRendering(CallbackInfo ci) {
        if (ChatAnimation.instance != null) {
            if(ChatAnimation.instance.isToggled()) {
                if (this.configuring) {
                    ci.cancel();
                    return;
                }
                final long current = System.currentTimeMillis();
                final long diff = current - this.prevMillis;
                this.prevMillis = current;
                this.updatePercentage(diff);
                float t = this.percentComplete;
                this.animationPercent = (float) MathUtil.clamp(1.0f - --t * t * t * t, 0.0, 1.0);
            }
        }
    }

    @Inject(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;pushMatrix()V", ordinal = 0, shift = At.Shift.AFTER))
    private void translate(CallbackInfo ci) {
        if (ChatAnimation.instance.isToggled()) {
            float y = 1.0f;
            if (!this.isScrolled) {
                y += (9.0f - 9.0f * this.animationPercent) * this.getChatScale();
            }
            GlStateManager.translate(0.0f, y, 0.0f);
        }
    }

    @ModifyArg(method = "drawChat" , at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;", ordinal = 0, remap = false), index = 0)
    private int getLineBeingDrawn(int line) {
        return this.lineBeingDrawn = line;
    }

    @ModifyArg(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"), index = 3)
    private int modifyTextOpacity(int original) {
        if (this.lineBeingDrawn <= this.newLines) {
            int opacity = original >> 24 & 0xFF;
            opacity *= (int)this.animationPercent;
            return (original & 0xFFFFFF) | opacity << 24;
        }
        return original;
    }

    @Inject(method = "printChatMessageWithOptionalDeletion", at = @At("HEAD"))
    private void resetPercentage(final CallbackInfo ci) {
        this.percentComplete = 0.0f;
    }

    @ModifyVariable(method = "setChatLine", at = @At("STORE"), ordinal = 0)
    private List<ITextComponent> setNewLines(final List<ITextComponent> original) {
        this.newLines = original.size() - 1;
        return original;
    }

    @ModifyVariable(method = "getChatComponent", at = @At(value = "STORE", ordinal = 0), ordinal = 3)
    private int modifyX(int original) {
        return original - 0;
    }

    @ModifyVariable(method = "getChatComponent", at = @At(value = "STORE", ordinal = 0), ordinal = 4)
    private int modifyY(int original) {
        return original + 1;
    }

    private int draw(FontRenderer fontRenderer, String text, float x, float y, int color) {
        int width = 0;
        boolean custom = true;
        boolean shouldContinue = false;
        String niggers = "";
        for (int j = 0; j < text.length(); ++j) {
            final char currentChar = text.charAt(j);
            final char nextChar = text.charAt((int)MathUtil.clamp(j + 1, 0.0, text.length() - 1));
            if ((String.valueOf(currentChar) + nextChar).matches("§[a-zA-Z1-9]")) {
                custom = false;
                niggers = "§" + nextChar;
            }
            else if ((String.valueOf(currentChar) + nextChar).equals("§$")) {
                custom = true;
            }
            if (shouldContinue) {
                shouldContinue = false;
            }
            else {
//                fontRenderer.drawStringWithShadow(String.valueOf(currentChar).equals("§") ? "" : (niggers + currentChar), x + width, y, custom ? ColorUtil.injectAlpha((Color)((Global)Main.getModuleManager().get((Class)Global.class)).colorSetting.getValue(), color >> 24 & 0xFF).getRGB() : color);
                if (String.valueOf(currentChar).equals("§")) {
                    shouldContinue = true;
                }
                width += fontRenderer.getStringWidth(String.valueOf(currentChar));
            }
        }
        return 0;
    }
}
