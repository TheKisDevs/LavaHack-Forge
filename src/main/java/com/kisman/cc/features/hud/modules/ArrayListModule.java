package com.kisman.cc.features.hud.modules;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.hud.HudModule;
import com.kisman.cc.features.hud.ShaderableHudModule;
import com.kisman.cc.features.hud.modules.arraylist.ArrayListElement;
import com.kisman.cc.features.hud.modules.arraylist.ElementTypes;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.ModuleInstance;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingArray;
import com.kisman.cc.settings.types.SettingEnum;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.settings.types.number.NumberType;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.client.collections.Sorter;
import com.kisman.cc.util.client.collections.SorterEntry;
import com.kisman.cc.util.enums.Gradients;
import com.kisman.cc.util.enums.Orientations;
import com.kisman.cc.util.enums.dynamic.EasingEnum;
import com.kisman.cc.util.render.ColorUtils;
import com.kisman.cc.util.render.Render2DUtil;
import com.kisman.cc.util.render.customfont.CustomFontUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;

public class ArrayListModule extends ShaderableHudModule {
    @ModuleInstance
    public static ArrayListModule instance;

    public static EasingEnum.Easing ANIMATION_EASING = EasingEnum.Easing.Linear;
    public static long ANIMATION_LENGTH = 750l;

    private final Sorter<ArrayListElement> sorter = new Sorter<>(ArrayListElement::getRaw);

    private final SettingGroup types = register(new SettingGroup(new Setting("Types", this)));

    private final Setting modules = register(types.add(new Setting("Modules", this, true)));
    private final Setting hudModules = register(types.add(new Setting("Hud Modules", this, false)));
    private final Setting checkBoxes = register(types.add(new Setting("Check Boxes", this, true)));

    private final SettingGroup animationGroup = register(new SettingGroup(new Setting("Animation", this)));
    private final Setting animation = register(animationGroup.add(new Setting("Animation", this, true)));
    public final SettingEnum<EasingEnum.Easing> easing = register(animationGroup.add(new SettingEnum<>("Easing", this, EasingEnum.Easing.Linear).onChange0(setting -> {
        ANIMATION_EASING = setting.getValEnum();

        return null;
    })));
    public final Setting length = register(animationGroup.add(new Setting("Speed", this, 750, 100, 1000, false).onChange(setting -> {
        ANIMATION_LENGTH = setting.getValLong();

        return null;
    })));

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
    private final SettingArray<SorterEntry<ArrayListElement>> sort = register(new SettingArray<>("Sort", this, sorter.length(), sorter.array()));

    public ArrayListModule() {
        super("ArrayList", "Displays your enables modules!", false, true, false);
    }

    public void draw() {
        ArrayList<ArrayListElement> elements = new ArrayList<>();
        ScaledResolution sr = new ScaledResolution(mc);

        if(modules.getValBoolean()) for(Module mod : Kisman.instance.moduleManager.modules) if(mod != null && mod.visible) elements.add(new ArrayListElement(mod, (mod.displayName + (mod.getDisplayInfo().isEmpty() || !showDisplayInfo.getValBoolean() ? "" : " " + TextFormatting.GRAY + mod.getDisplayInfo())), (mod.displayName + (mod.getDisplayInfo().equalsIgnoreCase("") ? "" : " " + mod.getDisplayInfo())), ElementTypes.Module));
        if(hudModules.getValBoolean()) for(HudModule mod : Kisman.instance.hudModuleManager.modules) if(mod != null && mod.visible) elements.add(new ArrayListElement(mod, (mod.displayName + (mod.getDisplayInfo().isEmpty() || !showDisplayInfo.getValBoolean() ? "" : " " + TextFormatting.GRAY + mod.getDisplayInfo())), (mod.displayName + (mod.getDisplayInfo().equalsIgnoreCase("") ? "" : " " + mod.getDisplayInfo())), ElementTypes.HudModule));
        if(checkBoxes.getValBoolean()) for(Setting set : Kisman.instance.settingsManager.getSettings()) if(set.isCheck() && set.getKey() != Keyboard.KEY_NONE) elements.add(new ArrayListElement(set, set.toDisplayString() + (set.getDisplayInfo().isEmpty() || !showDisplayInfo.getValBoolean() ? "" : " " + TextFormatting.GRAY + set.getDisplayInfo()), ElementTypes.CheckBox));

        elements.sort(sort.getValElement().getComparator());

        int count = 0;
        int staticColor = astolfoColor.getValBoolean() ? ColorUtils.astolfoColors(100, 100) : this.color.getColour().getRGB();
        double heigth = CustomFontUtil.getFontHeight() + offsets.getValDouble() + 1;
        float[] hsb = Color.RGBtoHSB(ColorUtils.getRed(staticColor), ColorUtils.getGreen(staticColor), ColorUtils.getBlue(staticColor), null);

        for(ArrayListElement element : elements) {
            double coeff = element.active() ? 1 : 0;

            if(element.active()) {
                if(animation.getValBoolean()) coeff = element.getElement().ENABLE_ANIMATION().getCurrent();
                element.getElement().ENABLE_ANIMATION().update();
                element.getElement().DISABLE_ANIMATION().reset();
            } else {
                if(animation.getValBoolean()) coeff = element.getElement().DISABLE_ANIMATION().getCurrent();
                element.getElement().DISABLE_ANIMATION().update();
                element.getElement().ENABLE_ANIMATION().reset();
            }

            if(coeff != 0) {
                int finalColor = gradient.getValEnum().getGetter().get(gradient.getValEnum().equals(Gradients.None) ? staticColor : (count * diff.getValInt()), hsb[1]);
                int finalCount = count;

                if (background.getValBoolean()) {
                    double offset = offsets.getValDouble() / 2 + 1;
                    Runnable backgroundRunnable = () -> drawBackground((orientation.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(element.getName()) - 1) - offset, yCoord.getValDouble() + (heigth * finalCount) - offset, (orientation.getValString().equalsIgnoreCase("LEFT") ? 1 + CustomFontUtil.getStringWidth(element.getName()) : sr.getScaledWidth()) + offset, yCoord.getValDouble() + (heigth * finalCount) + CustomFontUtil.getFontHeight() + offset - 1);

                    if (shaderedBackground.getValBoolean()) addShader(backgroundRunnable);
                    else addPreNormal(backgroundRunnable);
                }

                double coeff0 = coeff;

                addShader(() -> drawStringWithShadow(element.getName(), (orientation.getValString().equalsIgnoreCase("LEFT") ? (1 - (CustomFontUtil.getStringWidth(element.getName()) * (1 - coeff0))) : (sr.getScaledWidth() - (CustomFontUtil.getStringWidth(element.getName()) * coeff0) - 1)), yCoord.getValDouble() + (heigth * finalCount), finalColor));

                count++;
            }
        }
    }

    private void drawBackground(double x, double y, double x1, double y1) {Render2DUtil.drawRect(x, y, x1, y1, ColorUtils.injectAlpha(Color.BLACK, backgroundAlpha.getValInt()).getRGB());}
}