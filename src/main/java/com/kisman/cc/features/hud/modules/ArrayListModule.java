package com.kisman.cc.features.hud.modules;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.hud.HudModule;
import com.kisman.cc.features.hud.ShaderableHudModule;
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
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

public class ArrayListModule extends ShaderableHudModule {
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
    private final Setting shaderedBackground = register(new Setting("Shadered Background", this, false));

    private HashSet<IArrayListElement> prevElements = new HashSet<>();
    private HashSet<IArrayListElement> prevElements2 = new HashSet<>();

    public ArrayListModule() {
        super("ArrayList", "Displays your enables modules!", false, true, false);
    }

    public void handleRender() {
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
        HashSet<IArrayListElement> prevElementsNew2 = new HashSet<>();

        for(ArrayListElement element : elements) {
            prevElementsNew.add(element.getElement());

            if(element.getElement().getXCoeff() == 1) {
                prevElementsNew2.add(element.getElement());
            }

            int finalColor = gradient.getValEnum().getGetter().get(gradient.getValEnum().equals(Gradients.None) ? staticColor : (count * diff.getValInt()), hsb[1]);
            int finalCount = count;

            if(background.getValBoolean()) {
                double offset = offsets.getValDouble() / 2 + 1;
                Runnable backgroundRunnable = () -> drawBackground((orientation.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(element.getName()) - 1) - offset, yCoord.getValDouble() + (heigth * finalCount) - offset, (orientation.getValString().equalsIgnoreCase("LEFT") ? 1 + CustomFontUtil.getStringWidth(element.getName()) : sr.getScaledWidth()) + offset, yCoord.getValDouble() + (heigth * finalCount) + CustomFontUtil.getFontHeight() + offset - 1);

                if(shaderedBackground.getValBoolean()) addShader(backgroundRunnable);
                else addPreNormal(backgroundRunnable);
            }

            boolean flag = prevElements.contains(element.getElement());
            boolean flag3 = prevElements2.contains(element.getElement());
            float xCoeffPrev = element.getElement().getXCoeff();
            boolean flag2 = !flag;
            if(test2.getValBoolean()) {
                flag2 = element.active();
            }

            if(!flag) {
                element.done = false;
            }

            float xCoeff = (float) AnimationUtils.animate(element.active() && flag2 ? 1 : 0, element.getElement().getXCoeff(), speed.getValDouble());//element.getElement().getXCoeff();

            if(element.active() && flag && (test.getValBoolean() || flag3)) {
                xCoeff = 1;
            }

            element.getElement().setXCoeff(xCoeff);
            element.getElement().setXCoeffPrev(xCoeffPrev);

//            System.out.println(element.getElement().getXCoeff() == 1);
//            System.out.print(xCoeff);
            if(xCoeff != 1) xCoeff = easings.mutateProgress(xCoeff);
//            System.out.print(xCoeff);
//            System.out.print((test.getValBoolean() || element.getElement().getXCoeff() == 1));
//            System.out.print(flag);

            float finalXCoeff = xCoeff;

            addShader(() -> drawStringWithShadow(element.getName(), (orientation.getValString().equalsIgnoreCase("LEFT") ? (1 - (CustomFontUtil.getStringWidth(element.getName()) * (1 - finalXCoeff))) : (sr.getScaledWidth() - (CustomFontUtil.getStringWidth(element.getName()) * finalXCoeff) - 1)), yCoord.getValDouble() + (heigth * finalCount), finalColor));

            count++;
        }

        prevElements = prevElementsNew;
        prevElements2 = prevElementsNew2;
    }

    private void drawBackground(double x, double y, double x1, double y1) {Render2DUtil.drawRect(x, y, x1, y1, ColorUtils.injectAlpha(Color.BLACK, backgroundAlpha.getValInt()).getRGB());}
}
