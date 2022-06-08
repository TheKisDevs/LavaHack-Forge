package com.kisman.cc.features.plugins.managers;

import com.kisman.cc.features.plugins.Plugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PluginDescription
{
    private static final Map<Plugin, String> DESCRIPTIONS =
            new ConcurrentHashMap<>();

    public static void register(Plugin plugin, String description)
    {
        DESCRIPTIONS.put(plugin, description);
    }

    public static String getDescription(Plugin plugin)
    {
        return DESCRIPTIONS.get(plugin);
    }

}