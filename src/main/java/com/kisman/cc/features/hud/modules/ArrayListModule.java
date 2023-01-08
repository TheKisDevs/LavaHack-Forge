package com.kisman.cc.features.hud.modules;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.hud.HudModule;
import com.kisman.cc.features.hud.modules.arraylist.ArrayListElement;
import com.kisman.cc.features.hud.modules.arraylist.ElementTypes;
import com.kisman.cc.features.hud.modules.arraylist.IArrayListElement;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingEnum;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.settings.types.number.NumberType;
import com.kisman.cc.settings.util.EasingsPattern;
import com.kisman.cc.util.AnimationUtils;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.enums.Gradients;
import com.kisman.cc.util.enums.Orientations;
import com.kisman.cc.util.render.ColorUtils;
import com.kisman.cc.util.render.Render2DUtil;
import com.kisman.cc.util.render.customfont.CustomFontUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

public class ArrayListModule extends HudModule {
    public static ArrayListModule instance = new ArrayListModule();

    private final SettingGroup types = register(new SettingGroup(new Setting("Types", this)));

    private final Setting modules = register(types.add(new Setting("Modules", this, true)));
    private final Setting hudModules = register(types.add(new Setting("Hud Modules", this, false)));
    private final Setting checkBoxes = register(types.add(new Setting("Check Boxes", this, false)));

    private final SettingGroup animationGroup = register(new SettingGroup(new Setting("Animation", this)));
    private final Setting animation = register(animationGroup.add(new Setting("Animation", this, true)));
    private final EasingsPattern easings = new EasingsPattern(this).group(animationGroup).preInit().init();
    private final Setting speed = register(animationGroup.add(new Setting("Speed", this, 0.05, 0.01, 0.1, false)));
    private final Setting test = register(new Setting("Test", this, false));
    private final Setting test2 = register(new Setting("Test 2", this, false));
    
    private final Setting showDisplayInfo = register(new Setting("Show Display Info", this, true));

    private final Setting yCoord = register(new Setting("Y Coord", this, 3, 0, 500, true));
    private final Setting orientation = register(new Setting("Orientation", this, Orientations.Right));
    private final Setting offsets = register(new Setting("Offsets", this, 2, 0, 10, true));
    private final Setting astolfoColor = register(new Setting("Astolfo", this, true));
    private final Setting color = register(new Setting("Color", this, "Color", new Colour(-1)));
    private final SettingEnum<Gradients> gradient = register(new SettingEnum<>("Gradient", this, Gradients.None));
    private final Setting diff = register(new Setting("Gradient Diff", this, 1, 0, 1000, NumberType.TIME));
    private final Setting background = register(new Setting("Background", this, true));
    private final Setting backgroundAlpha = register(new Setting("Background Alpha", this, 255, 0, 255, true));
    private final SettingGroup glowGroup = register(new SettingGroup(new Setting("Glow", this)));
    private final Setting glow = register(glowGroup.add(new Setting("Glow", this, false)));
    private final Setting glowAlpha = register(new Setting("Glow Alpha", this, 255, 0, 255, true));
    private final Setting glowV2 = register(glowGroup.add(new Setting("Second Glow", this, false).setVisible(glow::getValBoolean)));
    private final Setting glowOffset = register(glowGroup.add(new Setting("Glow Offset", this, 5, 1, 20, true).setVisible(glow::getValBoolean)));
    private final Setting glowRadius = register(glowGroup.add(new Setting("Glow Radius", this, 0, 5, 20, true).setVisible(glow::getValBoolean)));
    private final Setting glowBackground = register(glowGroup.add(new Setting("Glow Background", this, false)));

    private HashSet<IArrayListElement> prevElements = new HashSet<>();

    public ArrayListModule() {
        super("ArrayList", "Displays your enables modules!");
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        ArrayList<ArrayListElement> elements = new ArrayList<>();
        ScaledResolution sr = new ScaledResolution(mc);

        if(modules.getValBoolean()) for(Module mod : Kisman.instance.moduleManager.modules) if(mod != null && (mod.isToggled() || (animation.getValBoolean() && mod.getXCoeff() > 0)) && mod.visible) elements.add(new ArrayListElement(mod, (mod.getName() + (mod.getDisplayInfo().isEmpty() || !showDisplayInfo.getValBoolean() ? "" : " " + TextFormatting.GRAY + mod.getDisplayInfo())), (mod.getName() + (mod.getDisplayInfo().equalsIgnoreCase("") ? "" : " " + mod.getDisplayInfo())), ElementTypes.Module));
        if(hudModules.getValBoolean()) for(HudModule mod : Kisman.instance.hudModuleManager.modules) if(mod != null && (mod.isToggled() || (animation.getValBoolean() && mod.getXCoeff() > 0)) && mod.visible) elements.add(new ArrayListElement(mod, (mod.getName() + (mod.getDisplayInfo().isEmpty() || !showDisplayInfo.getValBoolean() ? "" : " " + TextFormatting.GRAY + mod.getDisplayInfo())), (mod.getName() + (mod.getDisplayInfo().equalsIgnoreCase("") ? "" : " " + mod.getDisplayInfo())), ElementTypes.HudModule));
        if(checkBoxes.getValBoolean()) for(Setting set : Kisman.instance.settingsManager.getSettings()) if(set.isCheck() && set.getKey() != Keyboard.KEY_NONE && (set.getValBoolean() || (animation.getValBoolean() && set.getXCoeff() > 0))) elements.add(new ArrayListElement(set, set.toDisplayString() + (set.getDisplayInfo().isEmpty() || !showDisplayInfo.getValBoolean() ? "" : " " + TextFormatting.GRAY + set.getDisplayInfo()), ElementTypes.CheckBox));

        Comparator<ArrayListElement> comparator = (first, second) -> {
            float dif = CustomFontUtil.getStringWidth(second.getName()) - CustomFontUtil.getStringWidth(first.getName());
            return (dif != 0) ? ((int) dif) : second.getRaw().compareTo(first.getRaw());
        };

        elements.sort(comparator);

        int count = 0;
        int staticColor = astolfoColor.getValBoolean() ? ColorUtils.astolfoColors(100, 100) : this.color.getColour().getRGB();
        double heigth = CustomFontUtil.getFontHeight() + offsets.getValDouble() + 1;
        float[] hsb = Color.RGBtoHSB(ColorUtils.getRed(staticColor), ColorUtils.getGreen(staticColor), ColorUtils.getBlue(staticColor), null);

        HashSet<IArrayListElement> prevElementsNew = new HashSet<>();

        for(ArrayListElement element : elements) {
            prevElementsNew.add(element.getElement());

            int finalColor = gradient.getValEnum().getGetter().get(gradient.getValEnum().equals(Gradients.None) ? staticColor : (count * diff.getValInt()), hsb[1]);

            if(background.getValBoolean()) {
                double offset = offsets.getValDouble() / 2 + 1;
                drawBackground((orientation.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(element.getName()) - 1) - offset, yCoord.getValDouble() + (heigth * count) - offset, (orientation.getValString().equalsIgnoreCase("LEFT") ? 1 + CustomFontUtil.getStringWidth(element.getName()) : sr.getScaledWidth()) + offset, yCoord.getValDouble() + (heigth * count) + CustomFontUtil.getFontHeight() + offset - 1);
            }

            if(glowBackground.getValBoolean()) {
                double offset1 = offsets.getValDouble() / 2 + 1;
                Render2DUtil.drawRoundedRect((orientation.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(element.getName()) - 1) - offset1 - glowRadius.getValInt(), yCoord.getValDouble() + (heigth * count) - offset1 - glowRadius.getValInt(), (orientation.getValString().equalsIgnoreCase("LEFT") ? 1 + CustomFontUtil.getStringWidth(element.getName()) : sr.getScaledWidth()) + offset1 + glowRadius.getValInt(), yCoord.getValDouble() + (heigth * count) + CustomFontUtil.getFontHeight() + offset1 - 1 + glowRadius.getValInt(), ColorUtils.injectAlpha(Color.BLACK, backgroundAlpha.getValInt()).getRGB(), glowOffset.getValInt());
            }

            boolean flag = prevElements.contains(element.getElement());
            float xCoeffPrev = element.getElement().getXCoeff();
            boolean flag2 = !flag;
            if(test2.getValBoolean()) {
                flag2 = element.active();
            }
            float xCoeff = (element.active() && flag && (test.getValBoolean() || xCoeffPrev == element.getElement().getXCoeff())) ? 1 : (float) AnimationUtils.animate(element.active() && flag2 ? 1 : 0, element.getElement().getXCoeff(), speed.getValDouble());//element.getElement().getXCoeff();
            element.getElement().setXCoeff(xCoeff);
            element.getElement().setXCoeffPrev(xCoeffPrev);

            System.out.println(element.getElement().getXCoeff() == 1);
            System.out.print(xCoeff);
            if(xCoeff != 1) xCoeff = easings.mutateProgress(xCoeff);
            System.out.print(xCoeff);
            System.out.print((test.getValBoolean() || element.getElement().getXCoeff() == 1));
            System.out.print(flag);

            CustomFontUtil.drawStringWithShadow(element.getName(), (orientation.getValString().equalsIgnoreCase("LEFT") ? (1 - (CustomFontUtil.getStringWidth(element.getName()) * (1 - xCoeff))) : (sr.getScaledWidth() - (CustomFontUtil.getStringWidth(element.getName()) * xCoeff) - 1)), yCoord.getValDouble() + (heigth * count), finalColor);

            if(glow.getValBoolean()) {
                double offset = glowOffset.getValDouble() + offsets.getValDouble() / 2;
                if(glowV2.getValBoolean()) {
                    double offset1 = offsets.getValDouble() / 2 + 1;
                    Render2DUtil.drawRoundedRect((orientation.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(element.getName()) - 1) - offset1 - glowRadius.getValInt(), yCoord.getValDouble() + (heigth * count) - offset1 - glowRadius.getValInt(), (orientation.getValString().equalsIgnoreCase("LEFT") ? 1 + CustomFontUtil.getStringWidth(element.getName()) : sr.getScaledWidth()) + offset1 + glowRadius.getValInt(), yCoord.getValDouble() + (heigth * count) + CustomFontUtil.getFontHeight() + offset1 - 1 + glowRadius.getValInt(), ColorUtils.injectAlpha(staticColor, glowAlpha.getValInt()).getRGB(), glowOffset.getValInt());
                } else Render2DUtil.drawGlow((orientation.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(element.getName())), yCoord.getValDouble() + (heigth * count) - offset, ((orientation.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(element.getName())) + CustomFontUtil.getStringWidth(element.getName())), offset + yCoord.getValDouble() + (heigth * count) + CustomFontUtil.getFontHeight(), ColorUtils.injectAlpha(staticColor, glowAlpha.getValInt()).getRGB());
            }

            count++;
        }

        prevElements = prevElementsNew;
    }

    private void drawBackground(double x, double y, double x1, double y1) {Render2DUtil.drawRect(x, y, x1, y1, ColorUtils.injectAlpha(Color.BLACK, backgroundAlpha.getValInt()).getRGB());}
}
