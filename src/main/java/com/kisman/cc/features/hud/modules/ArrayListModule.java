package com.kisman.cc.features.hud.modules;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.hud.HudModule;
import com.kisman.cc.features.hud.modules.arraylist.ArrayListElement;
import com.kisman.cc.features.hud.modules.arraylist.ElementTypes;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.settings.types.number.NumberType;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.render.Render2DUtil;
import com.kisman.cc.util.render.customfont.CustomFontUtil;
import com.kisman.cc.util.enums.GradientModes;
import com.kisman.cc.util.enums.Orientations;
import com.kisman.cc.util.render.ColorUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public class ArrayListModule extends HudModule {
    public static ArrayListModule instance = new ArrayListModule();

    private final SettingGroup types = register(new SettingGroup(new Setting("Types", this)));

    private final Setting modules = register(types.add(new Setting("Modules", this, true)));
    private final Setting hudModules = register(types.add(new Setting("Hud Modules", this, false)));
    private final Setting checkBoxes = register(types.add(new Setting("Check Boxes", this, false)));
    
    private final Setting showDisplayInfo = register(new Setting("Show Display Info", this, true));

    private final Setting yCoord = register(new Setting("Y Coord", this, 3, 0, 500, true));
    private final Setting orientation = register(new Setting("Orientation", this, Orientations.Right));
    private final Setting offsets = register(new Setting("Offsets", this, 2, 0, 10, true));
    private final Setting astolfoColor = register(new Setting("Astolfo", this, true));
    private final Setting color = register(new Setting("Color", this, "Color", new Colour(-1)));
    private final Setting gradient = register(new Setting("Gradient", this, GradientModes.None));
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

    public ArrayListModule() {
        super("ArrayList", "Displays your enables modules!");
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        ArrayList<ArrayListElement> elements = new ArrayList<>();
        ScaledResolution sr = new ScaledResolution(mc);

        if(modules.getValBoolean()) for(Module mod : Kisman.instance.moduleManager.modules) if(mod != null && mod.isToggled() && mod.visible) elements.add(new ArrayListElement((mod.getName() + (mod.getDisplayInfo().isEmpty() || !showDisplayInfo.getValBoolean() ? "" : " " + TextFormatting.GRAY + mod.getDisplayInfo())), (mod.getName() + (mod.getDisplayInfo().equalsIgnoreCase("") ? "" : " " + mod.getDisplayInfo())), ElementTypes.Module));
        if(hudModules.getValBoolean()) for(HudModule mod : Kisman.instance.hudModuleManager.modules) if(mod != null && mod.isToggled() && mod.visible) elements.add(new ArrayListElement((mod.getName() + (mod.getDisplayInfo().isEmpty() || !showDisplayInfo.getValBoolean() ? "" : " " + TextFormatting.GRAY + mod.getDisplayInfo())), (mod.getName() + (mod.getDisplayInfo().equalsIgnoreCase("") ? "" : " " + mod.getDisplayInfo())), ElementTypes.HudModule));
        if(checkBoxes.getValBoolean()) {
            for(Setting set : Kisman.instance.settingsManager.getSettings()) {
                if(set.isCheck() && set.getKey() != Keyboard.KEY_NONE && set.getValBoolean()) elements.add(new ArrayListElement(set.toDisplayString() + (set.getDisplayInfo().isEmpty() || !showDisplayInfo.getValBoolean() ? "" : " " + TextFormatting.GRAY + set.getDisplayInfo()), ElementTypes.CheckBox));
            }
        }

        Comparator<ArrayListElement> comparator = (first, second) -> {
            float dif = CustomFontUtil.getStringWidth(second.getName()) - CustomFontUtil.getStringWidth(first.getName());
            return (dif != 0) ? ((int) dif) : second.getRaw().compareTo(first.getRaw());
        };

        elements.sort(comparator);

        int count = 0;
        int color = astolfoColor.getValBoolean() ? ColorUtils.astolfoColors(100, 100) : this.color.getColour().getRGB();
        double heigth = CustomFontUtil.getFontHeight() + offsets.getValDouble() + 1;
        float[] hsb = Color.RGBtoHSB(ColorUtils.getRed(color), ColorUtils.getGreen(color), ColorUtils.getBlue(color), null);

        for(int j = 0; j < elements.size(); j++) {
            ArrayListElement element = elements.get(j);
            if(element != null) {
                switch (gradient.getValString()) {
                    case "None": {
                        if(background.getValBoolean()) {
                            double offset = offsets.getValDouble() / 2 + 1;
                            drawBackground((orientation.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(element.getName()) - 1) - offset, yCoord.getValDouble() + (heigth * count) - offset, (orientation.getValString().equalsIgnoreCase("LEFT") ? 1 + CustomFontUtil.getStringWidth(element.getName()) : sr.getScaledWidth()) + offset, yCoord.getValDouble() + (heigth * count) + CustomFontUtil.getFontHeight() + offset - 1);
                        }

                        if(glowBackground.getValBoolean()) {
                            double offset1 = offsets.getValDouble() / 2 + 1;
                            Render2DUtil.drawRoundedRect((orientation.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(element.getName()) - 1) - offset1 - glowRadius.getValInt(), yCoord.getValDouble() + (heigth * count) - offset1 - glowRadius.getValInt(), (orientation.getValString().equalsIgnoreCase("LEFT") ? 1 + CustomFontUtil.getStringWidth(element.getName()) : sr.getScaledWidth()) + offset1 + glowRadius.getValInt(), yCoord.getValDouble() + (heigth * count) + CustomFontUtil.getFontHeight() + offset1 - 1 + glowRadius.getValInt(), ColorUtils.injectAlpha(Color.BLACK, backgroundAlpha.getValInt()).getRGB(), glowOffset.getValInt());
                        }

                        CustomFontUtil.drawStringWithShadow(element.getName(), (orientation.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(element.getName()) - 1), yCoord.getValDouble() + (heigth * count), color);

                        if(glow.getValBoolean()) {
                            double offset = glowOffset.getValDouble() + offsets.getValDouble() / 2;
                            if(glowV2.getValBoolean()) {
                                double offset1 = offsets.getValDouble() / 2 + 1;
                                Render2DUtil.drawRoundedRect((orientation.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(element.getName()) - 1) - offset1 - glowRadius.getValInt(), yCoord.getValDouble() + (heigth * count) - offset1 - glowRadius.getValInt(), (orientation.getValString().equalsIgnoreCase("LEFT") ? 1 + CustomFontUtil.getStringWidth(element.getName()) : sr.getScaledWidth()) + offset1 + glowRadius.getValInt(), yCoord.getValDouble() + (heigth * count) + CustomFontUtil.getFontHeight() + offset1 - 1 + glowRadius.getValInt(), ColorUtils.injectAlpha(color, glowAlpha.getValInt()).getRGB(), glowOffset.getValInt());
                            } else Render2DUtil.drawGlow((orientation.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(element.getName())), yCoord.getValDouble() + (heigth * count) - offset, ((orientation.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(element.getName())) + CustomFontUtil.getStringWidth(element.getName())), offset + yCoord.getValDouble() + (heigth * count) + CustomFontUtil.getFontHeight(), ColorUtils.injectAlpha(color, glowAlpha.getValInt()).getRGB());
                        }
                        break;
                    }
                    case "Rainbow": {
                        if(background.getValBoolean()) {
                            double offset = offsets.getValDouble() / 2 + 1;
                            drawBackground((orientation.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(element.getName()) - 1) - offset, yCoord.getValDouble() + (heigth * count) - offset, (orientation.getValString().equalsIgnoreCase("LEFT") ? 1 + CustomFontUtil.getStringWidth(element.getName()) : sr.getScaledWidth()) + offset, yCoord.getValDouble() + (heigth * count) + CustomFontUtil.getFontHeight() + offset - 1);
                        }

                        if(glowBackground.getValBoolean()) {
                            double offset1 = offsets.getValDouble() / 2 + 1;
                            Render2DUtil.drawRoundedRect((orientation.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(element.getName()) - 1) - offset1 - glowRadius.getValInt(), yCoord.getValDouble() + (heigth * count) - offset1 - glowRadius.getValInt(), (orientation.getValString().equalsIgnoreCase("LEFT") ? 1 + CustomFontUtil.getStringWidth(element.getName()) : sr.getScaledWidth()) + offset1 + glowRadius.getValInt(), yCoord.getValDouble() + (heigth * count) + CustomFontUtil.getFontHeight() + offset1 - 1 + glowRadius.getValInt(), ColorUtils.injectAlpha(Color.BLACK, backgroundAlpha.getValInt()).getRGB(), glowOffset.getValInt());
                        }

                        CustomFontUtil.drawStringWithShadow(element.getName(), (orientation.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(element.getName()) - 1), yCoord.getValDouble() + (heigth * count), ColorUtils.injectAlpha(ColorUtils.rainbow(count * diff.getValInt(), hsb[1], 1f), 255).getRGB());

                        if(glow.getValBoolean()) {
                            double offset = glowOffset.getValDouble() + offsets.getValDouble() / 2;
                            if(glowV2.getValBoolean()) {
                                double offset1 = offsets.getValDouble() / 2 + 1;
                                Render2DUtil.drawRoundedRect((orientation.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(element.getName()) - 1) - offset1 - glowRadius.getValInt(), yCoord.getValDouble() + (heigth * count) - offset1 - glowRadius.getValInt(), (orientation.getValString().equalsIgnoreCase("LEFT") ? 1 + CustomFontUtil.getStringWidth(element.getName()) : sr.getScaledWidth()) + offset1 + glowRadius.getValInt(), yCoord.getValDouble() + (heigth * count) + CustomFontUtil.getFontHeight() + offset1 - 1 + glowRadius.getValInt(), ColorUtils.injectAlpha(ColorUtils.rainbow(count * diff.getValInt(), hsb[1], 1f), glowAlpha.getValInt()).getRGB(), glowOffset.getValInt());
                            } else Render2DUtil.drawGlow((orientation.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(element.getName())), yCoord.getValDouble() + (heigth * count) - offset, ((orientation.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(element.getName())) + CustomFontUtil.getStringWidth(element.getName())), offset + yCoord.getValDouble() + (heigth * count) + CustomFontUtil.getFontHeight(), ColorUtils.injectAlpha(ColorUtils.rainbow(count * diff.getValInt(), hsb[1], 1f), glowAlpha.getValInt()).getRGB());
                        }
                        break;
                    }
                    case "Astolfo": {
                        if(background.getValBoolean()) {
                            double offset = offsets.getValDouble() / 2 + 1;
                            drawBackground((orientation.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(element.getName()) - 1) - offset, yCoord.getValDouble() + (heigth * count) - offset, (orientation.getValString().equalsIgnoreCase("LEFT") ? 1 + CustomFontUtil.getStringWidth(element.getName()) : sr.getScaledWidth()) + offset, yCoord.getValDouble() + (heigth * count) + CustomFontUtil.getFontHeight() + offset - 1);
                        }

                        if(glowBackground.getValBoolean()) {
                            double offset1 = offsets.getValDouble() / 2 + 1;
                            Render2DUtil.drawRoundedRect((orientation.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(element.getName()) - 1) - offset1 - glowRadius.getValInt(), yCoord.getValDouble() + (heigth * count) - offset1 - glowRadius.getValInt(), (orientation.getValString().equalsIgnoreCase("LEFT") ? 1 + CustomFontUtil.getStringWidth(element.getName()) : sr.getScaledWidth()) + offset1 + glowRadius.getValInt(), yCoord.getValDouble() + (heigth * count) + CustomFontUtil.getFontHeight() + offset1 - 1 + glowRadius.getValInt(), ColorUtils.injectAlpha(Color.BLACK, backgroundAlpha.getValInt()).getRGB(), glowOffset.getValInt());
                        }

                        CustomFontUtil.drawStringWithShadow(element.getName(), (orientation.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(element.getName()) - 1), yCoord.getValDouble() + (heigth * count), ColorUtils.injectAlpha(ColorUtils.getAstolfoRainbow(count * diff.getValInt()), 255).getRGB());

                        if(glow.getValBoolean()) {
                            double offset = glowOffset.getValDouble() + offsets.getValDouble() / 2;
                            if(glowV2.getValBoolean()) {
                                double offset1 = offsets.getValDouble() / 2 + 1;
                                Render2DUtil.drawRoundedRect((orientation.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(element.getName()) - 1) - offset1 - glowRadius.getValInt(), yCoord.getValDouble() + (heigth * count) - offset1 - glowRadius.getValInt(), (orientation.getValString().equalsIgnoreCase("LEFT") ? 1 + CustomFontUtil.getStringWidth(element.getName()) : sr.getScaledWidth()) + offset1 + glowRadius.getValInt(), yCoord.getValDouble() + (heigth * count) + CustomFontUtil.getFontHeight() + offset1 - 1 + glowRadius.getValInt(), ColorUtils.injectAlpha(ColorUtils.getAstolfoRainbow(count * diff.getValInt()), glowAlpha.getValInt()).getRGB(), glowOffset.getValInt());
                            } else Render2DUtil.drawGlow((orientation.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(element.getName())), yCoord.getValDouble() + (heigth * count) - offset, ((orientation.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(element.getName())) + CustomFontUtil.getStringWidth(element.getName())), offset + yCoord.getValDouble() + (heigth * count) + CustomFontUtil.getFontHeight(), ColorUtils.injectAlpha(ColorUtils.getAstolfoRainbow(count * diff.getValInt()), glowAlpha.getValInt()).getRGB());
                        }
                        break;
                    }
                    case "Pulsive": {
                        if(background.getValBoolean()) {
                            double offset = offsets.getValDouble() / 2 + 1;
                            drawBackground((orientation.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(element.getName()) - 1) - offset, yCoord.getValDouble() + (heigth * count) - offset, (orientation.getValString().equalsIgnoreCase("LEFT") ? 1 + CustomFontUtil.getStringWidth(element.getName()) : sr.getScaledWidth()) + offset, yCoord.getValDouble() + (heigth * count) + CustomFontUtil.getFontHeight() + offset - 1);
                        }

                        if(glowBackground.getValBoolean()) {
                            double offset1 = offsets.getValDouble() / 2 + 1;
                            Render2DUtil.drawRoundedRect((orientation.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(element.getName()) - 1) - offset1 - glowRadius.getValInt(), yCoord.getValDouble() + (heigth * count) - offset1 - glowRadius.getValInt(), (orientation.getValString().equalsIgnoreCase("LEFT") ? 1 + CustomFontUtil.getStringWidth(element.getName()) : sr.getScaledWidth()) + offset1 + glowRadius.getValInt(), yCoord.getValDouble() + (heigth * count) + CustomFontUtil.getFontHeight() + offset1 - 1 + glowRadius.getValInt(), ColorUtils.injectAlpha(Color.BLACK, backgroundAlpha.getValInt()).getRGB(), glowOffset.getValInt());
                        }

                        CustomFontUtil.drawStringWithShadow(element.getName(), (orientation.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(element.getName()) - 1), yCoord.getValDouble() + (heigth * count), ColorUtils.injectAlpha(ColorUtils.twoColorEffect(this.color.getColour(), this.color.getColour().setBrightness(0.25f), Math.abs(System.currentTimeMillis() / 10L) / 100.0 + 6.0 * (count * diff.getValFloat()) / 60.0).getColor(), 255).getRGB());

                        if(glow.getValBoolean()) {
                            double offset = glowOffset.getValDouble() + offsets.getValDouble() / 2;
                            if(glowV2.getValBoolean()) {
                                double offset1 = offsets.getValDouble() / 2 + 1;
                                Render2DUtil.drawRoundedRect((orientation.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(element.getName()) - 1) - offset1 - glowRadius.getValInt(), yCoord.getValDouble() + (heigth * count) - offset1 - glowRadius.getValInt(), (orientation.getValString().equalsIgnoreCase("LEFT") ? 1 + CustomFontUtil.getStringWidth(element.getName()) : sr.getScaledWidth()) + offset1 + glowRadius.getValInt(), yCoord.getValDouble() + (heigth * count) + CustomFontUtil.getFontHeight() + offset1 - 1 + glowRadius.getValInt(), ColorUtils.injectAlpha(ColorUtils.twoColorEffect(this.color.getColour(), this.color.getColour().setBrightness(0.25f), Math.abs(System.currentTimeMillis() / 10L) / 100.0 + 6.0 * (count * diff.getValFloat()) / 60.0).getColor(), glowAlpha.getValInt()).getRGB(), glowOffset.getValInt());
                            } else Render2DUtil.drawGlow((orientation.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(element.getName())), yCoord.getValDouble() + (heigth * count) - offset, ((orientation.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(element.getName())) + CustomFontUtil.getStringWidth(element.getName())), offset + yCoord.getValDouble() + (heigth * count) + CustomFontUtil.getFontHeight(), ColorUtils.injectAlpha(ColorUtils.twoColorEffect(this.color.getColour(), this.color.getColour().setBrightness(0.25f), Math.abs(System.currentTimeMillis() / 10L) / 100.0 + 6.0 * (count * diff.getValFloat()) / 60.0).getColor(), glowAlpha.getValInt()).getRGB());
                        }
                        break;
                    }
                }

                count++;
            }
        }
    }

    private void drawBackground(double x, double y, double x1, double y1) {Render2DUtil.drawRect(x, y, x1, y1, ColorUtils.injectAlpha(Color.BLACK, backgroundAlpha.getValInt()).getRGB());}
}
