package com.kisman.cc.features.module.misc.announcer;

import com.kisman.cc.features.module.*;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;

@ModuleInfo(
        name = "VisualRange",
        display = "Visual Range",
        submodule = true
)
public class  VisualRange extends Module {
    private final ArrayList<String> names = new ArrayList<>();
    private final ArrayList<String> newnames = new ArrayList<>();

    public void update() {
        this.newnames.clear();
        try {
            for (Entity entity : mc.world.loadedEntityList) if (entity instanceof EntityPlayer && !entity.getName().equalsIgnoreCase(mc.player.getName())) this.newnames.add(entity.getName());
            if (!this.names.equals(this.newnames)) {
                for (final String name : this.newnames) if (!this.names.contains(name)) ChatUtility.warning().printClientModuleMessage(name + " entered in visual range!");
                for (final String name : this.names) if (!this.newnames.contains(name)) ChatUtility.message().printClientModuleMessage(name + " left from visual range!");
                this.names.clear();
                this.names.addAll(this.newnames);
            }
        } catch (Exception ignored) {}
    }
}
