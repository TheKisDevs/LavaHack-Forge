package com.kisman.cc.features.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventEnchantGlintColor;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.render.ColorUtils;
import me.zero.alpine.listener.Listener;

import java.awt.*;

public class EnchantGlint extends Module {

    private final Setting noGlint = register(new Setting("NoGlint", this, false));
    private final Setting items = register(new Setting("Items", this, true));
    private final Setting armor = register(new Setting("Armor", this, true));

    private final SettingGroup render = register(new SettingGroup(new Setting("Render", this)));
    private final Setting rainbow = register(render.add(new Setting("Rainbow", this, false)));
    private final Setting rainbowSpeed = register(render.add(new Setting("Speed", this, 1, 0.25, 5, false).setVisible(rainbow::getValBoolean)));
    private final Setting rainbowSat = register(render.add(new Setting("Saturation", this, 100, 0, 100, true).setVisible(rainbow::getValBoolean)));
    private final Setting rainbowBright = register(render.add(new Setting("Brightness", this, 50, 0, 100, true).setVisible(rainbow::getValBoolean)));

    private final Setting color = register(render.add(new Setting("Color", this, new Colour(255, 255, 255, 150))));

    public EnchantGlint(){
        super("EnchantGlint", Category.RENDER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if(mc.player == null || mc.world == null){
            toggle();
            return;
        }
        Kisman.EVENT_BUS.subscribe(listener);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Kisman.EVENT_BUS.unsubscribe(listener);
    }

    private final Listener<EventEnchantGlintColor> listener = new Listener<>(event -> {
        if(items.getValBoolean() && event.getStage() != EventEnchantGlintColor.Stage.Item)
            return;
        if(armor.getValBoolean() && event.getStage() != EventEnchantGlintColor.Stage.Armor)
            return;
        event.cancel();
        if(noGlint.getValBoolean()){
            event.setColor(new Color(255, 255, 255, 0));
            return;
        }
        Color color = this.color.getColour().getColor();
        if(rainbow.getValBoolean())
            color = ColorUtils.rainbow2(0, rainbowSat.getValInt(), rainbowBright.getValInt(), color.getAlpha(), rainbowSpeed.getValDouble()).getColor();
        event.setColor(color);
    });
}
