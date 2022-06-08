package the.kis.devs.api.util.render.customfont;

import com.kisman.cc.util.render.customfont.CustomFontUtil;

/**
 * @author _kisman_
 * @since 21:47 of 08.06.2022
 */
public class CustomFontUtilAPI {
    public static int getStringWidth(String text) {return CustomFontUtil.getStringWidth(text);}
    public static int getStringWidth(String text, boolean gui) {return CustomFontUtil.getStringWidth(text, gui);}
    public static void drawString(String text, double x, double y, int color, boolean gui) {CustomFontUtil.drawString(text, x, y, color, gui);}
    public static int drawString(String text, double x, double y, int color) {return CustomFontUtil.drawString(text, x, y, color);}
    public static int drawStringWithShadow(String text, double x, double y, int color) {return CustomFontUtil.drawStringWithShadow(text, x, y, color);}
    public static void drawCenteredStringWithShadow(String text, double x, double y, int color) {CustomFontUtil.drawCenteredStringWithShadow(text, x, y, color);}
    public static int getFontHeight(boolean gui) {return CustomFontUtil.getFontHeight(gui);}
    public static int getFontHeight() {return CustomFontUtil.getFontHeight();}
}
