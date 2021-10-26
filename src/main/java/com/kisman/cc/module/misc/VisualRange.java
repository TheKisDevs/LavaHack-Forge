package com.kisman.cc.module.misc;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.module.client.HUD;
import com.kisman.cc.oldclickgui.notification.Notification;
import com.kisman.cc.oldclickgui.notification.NotificationManager;
import com.kisman.cc.oldclickgui.notification.NotificationType;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;

public class VisualRange extends Module {
    private ArrayList<String> names;
    private ArrayList<String> newnames;

    public VisualRange() {
        super("VisualRange", "", Category.MISC);

        this.names = new ArrayList<>();
        this.newnames = new ArrayList<>();
    }

    public void update() {
        this.newnames.clear();
        try {
            for (final Entity entity : mc.world.loadedEntityList) {
                if (entity instanceof EntityPlayer && !entity.getName().equalsIgnoreCase(mc.player.getName())) {
                    this.newnames.add(entity.getName());
                }
            }
            if (!this.names.equals(this.newnames)) {
                for (final String name : this.newnames) {
                    if (!this.names.contains(name)) {
                        String msg = name + " entered visual range!";

                        if(HUD.instance.visualRange.getValBoolean()) {
                            NotificationManager.show(new Notification(NotificationType.WARNING, "VisualRange", msg, 10));
                        }

                        ChatUtils.warning(msg);
                    }
                }
                for (final String name : this.names) {
                    if (!this.newnames.contains(name)) {
                        String msg = name + " left visual range!";

                        if(HUD.instance.visualRange.getValBoolean()) {
                            NotificationManager.show(new Notification(NotificationType.INFO, "VisualRange", msg, 600));
                        }

                        ChatUtils.message(msg);
                    }
                }
                this.names.clear();
                for (final String name : this.newnames) {
                    this.names.add(name);
                }
            }
        }
        catch (Exception ex) {}
    }
}
