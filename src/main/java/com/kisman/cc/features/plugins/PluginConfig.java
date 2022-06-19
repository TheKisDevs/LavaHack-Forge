package com.kisman.cc.features.plugins;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public final class PluginConfig {
    @SerializedName("name") private String name;
    @SerializedName("displayName") private String displayName;
    @SerializedName("version") private String version;
    @SerializedName("prefix") private String prefix;
    @SerializedName("mainClass") private String mainClass;
    @SerializedName("mixinConfig") private String mixinConfig;

    public String getName() {return name;}
    public String getDisplayName() {return displayName.replaceAll("&", "§");}
    public String getVersion() {return version;}
    public String getPrefix() {return prefix;}
    public String getMainClass() {return mainClass;}
    public String getMixinConfig() {return mixinConfig;}

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        else if (o instanceof PluginConfig) return this.name != null && this.name.equals(((PluginConfig) o).name);
        return false;
    }

    @Override
    public int hashCode()
    {
        return this.name.hashCode();
    }
}