package com.kisman.cc.features.module.render;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;

import java.util.Arrays;

public class Weather extends Module {

    private final Setting weatherMode = register(new Setting("Type", this, "Mode", Arrays.asList("Mode", "Custom")));
    private final Setting weather = register(new Setting("Mode", this, "Sunny", Arrays.asList("Default", "Sunny", "Rain", "Thunder")).setVisible(() -> weatherMode.getValString().equals("Mode")));
    private final Setting weatherSlider = register(new Setting("Custom", this, 0, 0, 2, false).setVisible(() -> weatherMode.getValString().equals("Custom")));

    public Weather() {
        super("Weather", Category.RENDER);
    }

    @Override
    public void update(){
        if(mc.world == null || mc.player == null)
            return;

        float strength = weather.getIndex() == 0 ? weather.getIndex() - 1 : weatherSlider.getValFloat();

        if(weatherMode.getValString().equals("Custom") || weather.getIndex() > 0){
            mc.world.setRainStrength(strength);
        }
    }
}
