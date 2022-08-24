package com.kisman.cc.mixin.mixins;

import com.kisman.cc.features.module.misc.BetterScreenshot;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.*;

import javax.annotation.Nullable;
import java.awt.*;
import java.io.*;

@Mixin(ScreenShotHelper.class)
public class MixinScreenShotHelper {
    @Shadow public static ITextComponent saveScreenshot(File gameDirectory, @Nullable String screenshotName, int width, int height, Framebuffer buffer) {return null;}

    /**
     * @author BloomWareClient
     * @reason uwa??
     */
    @Overwrite
    public static ITextComponent saveScreenshot(File gameDirectory, int width, int height, Framebuffer buffer) {
        ITextComponent screenshot = saveScreenshot(gameDirectory, null, width, height, buffer);
        if(BetterScreenshot.instance != null && BetterScreenshot.instance.isToggled()) {
            try {
                Image image = BetterScreenshot.getLatestScreenshot();
                if(image != null) BetterScreenshot.copyToClipboard(image);
            } catch (IOException ignored) {}
        }
        return screenshot;
    }
}
