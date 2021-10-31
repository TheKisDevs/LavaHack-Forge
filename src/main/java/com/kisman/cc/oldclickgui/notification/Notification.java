package com.kisman.cc.oldclickgui.notification;

import com.kisman.cc.Kisman;
import com.kisman.cc.util.AnimationUtils;
import com.kisman.cc.util.Render2DUtil;
import com.kisman.cc.util.customfont.CustomFontUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import i.gishreloaded.gishcode.utils.TimerUtils;

import java.awt.Color;

public class Notification {
    public String text;
    public long disableTime;
    public float width;
    public TimerUtils timer = new TimerUtils();
    public AnimationUtils animationUtils;
    public AnimationUtils animationUtils2;
    public AnimationUtils reverse;
    public AnimationUtils reverse2;
    public boolean didThing = false;
    public boolean isReversing = false;
    public boolean didFirstReverse = false;

    public Notification(String text, long disableTime, long inOutTime) {
        this.text = text;
        this.disableTime = disableTime;
        this.width = CustomFontUtil.getStringWidth(text);
        this.animationUtils = new AnimationUtils(inOutTime, Float.intBitsToFloat(Float.floatToIntBits(1.1522732E38f) ^ 0x7EAD5FF3), this.width + Float.intBitsToFloat(Float.floatToIntBits(0.7864751f) ^ 0x7F49566F));
        this.animationUtils2 = new AnimationUtils(inOutTime, Float.intBitsToFloat(Float.floatToIntBits(1.2526955E38f) ^ 0x7EBC7C13), this.width + Float.intBitsToFloat(Float.floatToIntBits(1.8077307f) ^ 0x7F6763B8));
        this.reverse = new AnimationUtils(inOutTime, Float.intBitsToFloat(Float.floatToIntBits(2.4275405E37f) ^ 0x7D921A2F), this.width + Float.intBitsToFloat(Float.floatToIntBits(0.07119077f) ^ 0x7D91CC77));
        this.reverse2 = new AnimationUtils(inOutTime, Float.intBitsToFloat(Float.floatToIntBits(1.9618134E38f) ^ 0x7F139727), this.width + Float.intBitsToFloat(Float.floatToIntBits(1.1008757f) ^ 0x7F0CE97F));
        this.timer.reset();
        this.animationUtils.reset();
        this.animationUtils2.reset();
        this.reverse.reset();
        this.reverse2.reset();
    }

    /*
     * WARNING - void declaration
     */
    public void onDraw(int y) {
        if (this.timer.hasReached(this.disableTime)) {
            Kisman.instance.notificationProcessor.getNotifications().remove(this);
        }
        Render2DUtil.drawRect(
                -(this.width + Float.intBitsToFloat(Float.floatToIntBits(0.3717855f) ^ 0x7E3E5AAB)) + this.animationUtils2.getValue() - (
                        this.isReversing && this.didFirstReverse ?
                                this.reverse2.getValue() :
                                Float.intBitsToFloat(Float.floatToIntBits(3.1289036E38f) ^ 0x7F6B647E)),
                (float)y,
                this.width + Float.intBitsToFloat(Float.floatToIntBits(0.20066918f) ^ 0x7ECD7C39),
                Float.intBitsToFloat(Float.floatToIntBits(0.7693848f) ^ 0x7EE4F667), new Color(70, 70, 70, 255).getRGB());
        if (this.animationUtils2.isDone()) {
            Render2DUtil.drawRect(
                    -(this.width + Float.intBitsToFloat(Float.floatToIntBits(0.64607567f) ^ 0x7F256537)) + this.animationUtils.getValue() - (this.isReversing ? this.reverse.getValue() : Float.intBitsToFloat(Float.floatToIntBits(1.4951425E38f) ^ 0x7EE0F6CB)), (float)y, this.width + Float.intBitsToFloat(Float.floatToIntBits(0.17125504f) ^ 0x7E2F5D7B), Float.intBitsToFloat(Float.floatToIntBits(0.448751f) ^ 0x7F45C2B1), new Color(28, 28, 28).getRGB());
            CustomFontUtil.drawString(ChatFormatting.stripFormatting(this.text),
                    -(this.width + Float.intBitsToFloat(Float.floatToIntBits(0.694125f) ^ 0x7F31B22D)) + this.animationUtils.getValue() - (this.isReversing ? this.reverse.getValue() : Float.intBitsToFloat(Float.floatToIntBits(1.5466472E37f) ^ 0x7D3A2BBF)) + Float.intBitsToFloat(Float.floatToIntBits(0.2626216f) ^ 0x7E867657),
                    (float)(y + 10) - CustomFontUtil.getFontHeight() / Float.intBitsToFloat(Float.floatToIntBits(0.5751044f) ^ 0x7F133A0B) - 10, -1);
        }
    }
}

