package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.module.client.Changer;
import com.kisman.cc.features.module.client.FriendHighlight;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

/**
 * @author _kisman_
 * @since 15:01 of 27.06.2022
 */
@Mixin(FontRenderer.class)
public class MixinFontRenderer {
    private final String ASCII_RUS = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ�������������� !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~ёÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐АБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдежзийклмнопрстуфхцчшщъыьэюя";
    private final String ASCII_PATH_RUS = "textures/font/ascii_fat.png";

    @Shadow protected void enableAlpha() {}
    @Shadow private void resetStyles() {}
    @Shadow private int renderString(String text, float x, float y, int color, boolean dropShadow) {return 0;}

    @Shadow private boolean unicodeFlag;

    @Shadow @Final protected int[] charWidth;

    @Shadow @Final protected byte[] glyphWidth;

    @Shadow @Final private int[] colorCode;

    @Shadow protected float renderDefaultChar(int ch, boolean italic) { return 0f; }

    @Shadow protected float renderUnicodeChar(char ch, boolean italic) { return 0f; }

    @Shadow private int textColor;

    @Shadow protected void setColor(float r, float g, float b, float a) {}

    @Shadow private boolean randomStyle;

    @Shadow private boolean boldStyle;

    @Shadow private boolean strikethroughStyle;

    @Shadow private boolean underlineStyle;

    @Shadow private boolean italicStyle;

    @Shadow private float red;

    @Shadow private float green;

    @Shadow private float blue;

    @Shadow private float alpha;

    @Shadow public int getCharWidth(char character) { return 0; }

    @Shadow public Random fontRandom;

    @Shadow protected float posX;

    @Shadow protected float posY;

    @Shadow private float renderChar(char ch, boolean italic) { return 0f; }

    @Shadow protected void doDraw(float f) {}



    /**
     * @author _kisman_
     * @reason nya~
     */
    @Inject(method = "drawString(Ljava/lang/String;FFIZ)I", at = @At("HEAD"), cancellable = true)
    private void drawStringHook(String text, float x, float y, int color, boolean dropShadow, CallbackInfoReturnable<Integer> cir) {
        if(Kisman.instance.moduleManager != null) {
            String text0 = FriendHighlight.INSTANCE.modifyLine(text);
            enableAlpha();
            resetStyles();

            if (dropShadow) {
                cir.setReturnValue(Math.max(
                        renderString(text0, x + (float) Changer.fontShadowX, y + (float) Changer.fontShadowY, color, true),
                        renderString(text0, x, y, color, false)
                ));
                cir.cancel();
            }
        }
    }

    /*@Inject(
            method = "renderChar",
            at = @At("HEAD"),
            cancellable = true
    )
    private void renderCharHook(
            char ch,
            boolean italic,
            CallbackInfoReturnable<Float> cir
    ) {
        if(ClientFixer.INSTANCE.isToggled() && Minecraft.getMinecraft().gameSettings.language.equalsIgnoreCase("ru_ru")) {
            if (ch == 160 || ch == ' ') {
                cir.setReturnValue(4f);
                cir.cancel();
            } else {
                int i = ASCII_RUS.indexOf(ch);
                cir.setReturnValue(i != -1 *//*&& !unicodeFlag*//* ? renderDefaultChar(i, italic) : renderUnicodeChar(ch, italic));
                cir.cancel();
            }
        }
    }

    @Inject(
            method = "renderStringAtPos",
            at = @At("HEAD"),
            cancellable = true
    )
    private void renderStringAtPosHook(
            String text,
            boolean shadow,
            CallbackInfo ci
    ) {
        if(ClientFixer.INSTANCE.isToggled() && Minecraft.getMinecraft().gameSettings.language.equalsIgnoreCase("ru_ru")) {
            for(int i = 0; i < text.length(); ++i) {
                char c0 = text.charAt(i);
                int i1;
                int j1;
                if (c0 == 167 && i + 1 < text.length()) {
                    i1 = "0123456789abcdefklmnor".indexOf(String.valueOf(text.charAt(i + 1)).toLowerCase(Locale.ROOT).charAt(0));
                    if (i1 < 16) {
                        this.randomStyle = false;
                        this.boldStyle = false;
                        this.strikethroughStyle = false;
                        this.underlineStyle = false;
                        this.italicStyle = false;
                        if (i1 < 0 || i1 > 15) {
                            i1 = 15;
                        }

                        if (shadow) {
                            i1 += 16;
                        }

                        j1 = colorCode[i1];
                        textColor = j1;
                        setColor((float)(j1 >> 16) / 255.0F, (float)(j1 >> 8 & 255) / 255.0F, (float)(j1 & 255) / 255.0F, this.alpha);
                    } else if (i1 == 16) {
                        randomStyle = true;
                    } else if (i1 == 17) {
                        boldStyle = true;
                    } else if (i1 == 18) {
                        strikethroughStyle = true;
                    } else if (i1 == 19) {
                        underlineStyle = true;
                    } else if (i1 == 20) {
                        italicStyle = true;
                    } else if (i1 == 21) {
                        this.randomStyle = false;
                        this.boldStyle = false;
                        this.strikethroughStyle = false;
                        this.underlineStyle = false;
                        this.italicStyle = false;
                        this.setColor(red, green, blue, alpha);
                    }

                    ++i;
                } else {
                    i1 = ASCII_RUS.indexOf(c0);
                    if (this.randomStyle && i1 != -1) {
                        j1 = getCharWidth(c0);

                        char c1;
                        do {
                            i1 = fontRandom.nextInt(ASCII_RUS.length());
                            c1 = ASCII_RUS.charAt(i1);
                        } while(j1 != this.getCharWidth(c1));

                        c0 = c1;
                    }

                    float f1 = i1 != -1 *//*&& !this.unicodeFlag*//* ? 1.0F : 0.5F;
                    boolean flag = (c0 == 0 || i1 == -1*//* || this.unicodeFlag*//*) && shadow;
                    if (flag) {
                        posX -= f1;
                        posY -= f1;
                    }

                    float f = renderChar(c0, this.italicStyle);
                    if (flag) {
                        this.posX += f1;
                        this.posY += f1;
                    }

                    if (this.boldStyle) {
                        this.posX += f1;
                        if (flag) {
                            this.posX -= f1;
                            this.posY -= f1;
                        }

                        this.renderChar(c0, this.italicStyle);
                        this.posX -= f1;
                        if (flag) {
                            this.posX += f1;
                            this.posY += f1;
                        }

                        ++f;
                    }

                    this.doDraw(f);
                }
            }

            ci.cancel();
        }
    }

    @Inject(
            method = "getCharWidth",
            at = @At("HEAD"),
            cancellable = true
    )
    private void getCharWidthHook(
            char character,
            CallbackInfoReturnable<Integer> cir
    ) {
        if(ClientFixer.INSTANCE.isToggled() && Minecraft.getMinecraft().gameSettings.language.equalsIgnoreCase("ru_ru")) {
            if (character == 160 || character == ' ') {
                cir.setReturnValue(4);
                cir.cancel();
            } else if (character == 167) {
                cir.setReturnValue(-1);
                cir.cancel();
            } else {
                int i = ASCII_RUS.indexOf(character);
                if (character > 0 && i != -1*//* && !unicodeFlag*//*) {
                    cir.setReturnValue(charWidth[i]);
                    cir.cancel();
                } else if (glyphWidth[character] != 0) {
                    int j = glyphWidth[character] & 255;
                    int k = j >>> 4;
                    int l = j & 15;
                    ++l;
                    cir.setReturnValue((l - k) / 2 + 1);
                    cir.cancel();
                } else {
                    cir.setReturnValue(0);
                    cir.cancel();
                }
            }
        }
    }*/
}
