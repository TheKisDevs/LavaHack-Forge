package com.kisman.cc.util.settings;

import com.kisman.cc.Kisman;
import com.kisman.cc.settings.Setting;
import java.util.Arrays;

public class SettingLoader {

    public static void load(Object object){
        if(object.getClass().getAnnotation(LoadSettings.class) == null)
            return;
        Arrays.stream(object.getClass().getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(ExcludeSetting.class))
                .filter(field -> Setting.class.isAssignableFrom(field.getType()))
                .forEach(field -> {
                    field.setAccessible(true);
                    try { Kisman.instance.settingsManager.rSetting((Setting) field.get(object)); }
                    catch (IllegalAccessException ignored){ }
                });
    }
}
