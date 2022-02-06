package com.kisman.cc.hud.hudmodule.render;

import com.kisman.cc.hud.hudmodule.HudCategory;
import com.kisman.cc.hud.hudmodule.HudModule;
import com.kisman.cc.hud.hudmodule.render.packetchat.Log;
import com.kisman.cc.hud.hudmodule.render.packetchat.Message;
import com.kisman.cc.module.client.Config;
import com.kisman.cc.module.client.CustomFont;
import com.kisman.cc.module.client.HUD;
import com.kisman.cc.oldclickgui.vega.component.Frame;
import com.kisman.cc.util.AnimationUtils;
import com.kisman.cc.util.Render2DUtil;
import com.kisman.cc.util.customfont.CustomFontUtil;
import i.gishreloaded.gishcode.utils.TimerUtils;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

public class PacketChat extends HudModule {

    public static PacketChat Instance;

    private final TimerUtils timer = new TimerUtils();
    private int sliderWidth = 200;
    private int sliderHeight = 20;
    private int width = 200;

    private final String header = "PacketChat";
    private final String stringWithMaxLength = "Cooldown";
    private double borderOffset = 5;

    public Log logs = new Log();

    public PacketChat() {
        super("PacketChat", "", HudCategory.PLAYER);
        logs.ActiveMessages.add(new Message("lol"));
        logs.ActiveMessages.add(new Message("lmao"));
        Instance = this;
    }

    public void update() {
        setX(3);
        setY(HUD.instance.indicY.getValInt() + 8);
        setW(width + 4);
        setH(borderOffset * 7 + CustomFontUtil.getFontHeight() * 5);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        drawRewrite();
    }

    private void drawRewrite() {

        if(logs.ActiveMessages.size()>30) logs.ActiveMessages.clear();
        if(logs.PassiveMessages.size()>30) logs.PassiveMessages.clear();

        scrollWheelCheck();

        double x = getX();
        double y = getY();
        double width = getW();
        double height = getH();
        double offset = CustomFontUtil.getFontHeight() + borderOffset;
        int count = 0;

        double prevX = mc.player.posX - mc.player.prevPosX;
        double prevZ = mc.player.posZ - mc.player.prevPosZ;
        double lastDist = Math.sqrt(prevX * prevX + prevZ * prevZ);
        double currSpeed = lastDist * 15.3571428571D / 4;


        double healthPercentage = mc.player.getHealth() / mc.player.getMaxHealth();


        //draw background
        Render2DUtil.drawRect(x + 3, y + 3, x + width + 3, y + height - 3, (ColorUtils.getColor(33, 33, 42)));
        Render2DUtil.drawRect(x + 3, y, x + width + 3, y + height, (ColorUtils.getColor(33, 33, 42)));
        Render2DUtil.drawRect(x + 2, y + 2, x + width + 2, y + height - 2, (ColorUtils.getColor(45, 45, 55)));
        Render2DUtil.drawRect(x + 2, y, x + width + 2, y + height, (ColorUtils.getColor(45, 45, 55)));
        Render2DUtil.drawRect(x + 1, y + 1, x + width + 1, y + height - 1, (ColorUtils.getColor(60, 60, 70)));
        Render2DUtil.drawRect(x + 1, y, x + width + 1, y + height, (ColorUtils.getColor(60, 60, 70)));
        Render2DUtil.drawRect(x - 3, y - 8, x + width + 3, y + height - 3, (ColorUtils.getColor(33, 33, 42)));
        Render2DUtil.drawRect(x - 3, y, x + width + 3, y + height, (ColorUtils.getColor(33, 33, 42)));
        Render2DUtil.drawRect(x - 2, y - 7, x + width + 2, y + height - 2, (ColorUtils.getColor(45, 45, 55)));
        Render2DUtil.drawRect(x - 2, y, x + width + 2, y + height, (ColorUtils.getColor(45, 45, 55)));
        Render2DUtil.drawRect(x - 1, y - 6, x + width + 1, y + height - 1, (ColorUtils.getColor(60, 60, 70)));
        Render2DUtil.drawRect(x - 1, y, x + width + 1, y + height, (ColorUtils.getColor(60, 60, 70)));
        Render2DUtil.drawRect(x, y - 5, x + width, y + height, (ColorUtils.astolfoColors(100, 100)));
        Render2DUtil.drawRect(x - 3, y - 1, x + width + 3, y + height + 3, (ColorUtils.getColor(33, 33, 42)));
        Render2DUtil.drawRect(x - 2, y - 2, x + width + 2, y + height + 2, (ColorUtils.getColor(45, 45, 55)));
        Render2DUtil.drawRect(x - 1, y - 3, x + width + 1, y + height + 1, (ColorUtils.getColor(60, 60, 70)));
        Render2DUtil.drawRect(x, y - 4, x + width, y + height, (ColorUtils.getColor(34, 34, 40)));

        //draw header
        CustomFontUtil.drawCenteredStringWithShadow(header, x + width / 2, y + borderOffset, ColorUtils.astolfoColors(100, 100));

        int pcount = 0;

        //draws messages
        for(Message message : logs.ActiveMessages)
        {
            pcount++;

            CustomFontUtil.drawStringWithShadow(message.message, x + borderOffset, y + CustomFontUtil.getFontHeight() + (offset * count), ColorUtils.astolfoColors(100, 100));
            count++;

            if(pcount>=height/borderOffset)
                up();
        }


    }

    private void drawStringWithShadow(String text, double x, double y, int color) {
        if(CustomFont.turnOn) CustomFontUtil.consolas15.drawStringWithShadow(text, x, y, color);
        else mc.fontRenderer.drawStringWithShadow(text, (int) x, (int) y, color);
    }

    private int getHeight() {
        if(CustomFont.turnOn) return  CustomFontUtil.consolas15.getStringHeight();
        else return mc.fontRenderer.FONT_HEIGHT;
    }

    private int getStringWidth(String text) {
        if(CustomFont.turnOn) return  CustomFontUtil.consolas15.getStringWidth(text);
        else return mc.fontRenderer.getStringWidth(text);
    }

    public void up()
    {
        if(!logs.ActiveMessages.isEmpty()) {
            logs.PassiveMessages.add(logs.ActiveMessages.get(0));
            logs.ActiveMessages.remove(0);
        }
    }

    public void down()
    {
        if(!logs.PassiveMessages.isEmpty())
        {
            logs.ActiveMessages.add(0, logs.PassiveMessages.get(logs.PassiveMessages.size()-1));
            logs.PassiveMessages.remove(logs.PassiveMessages.size()-1);
        }
    }

    public void scrollWheelCheck() {
        int dWheel = Mouse.getDWheel();
        if(dWheel < 0) {
            up();
        } else if(dWheel > 0) {
            down();
        }
    }
}
