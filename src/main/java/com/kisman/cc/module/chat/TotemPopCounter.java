package com.kisman.cc.module.chat;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.subscribe.TotemPopEvent;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.module.chat.totempopcounter.Totem;
import com.kisman.cc.settings.Setting;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

public class TotemPopCounter extends Module {
    private Setting target = new Setting("Target", this, TargetMode.Both);
//    public ArrayList<Totem> pops = new ArrayList<>();
    public TotemPopCounter() {
        super("TotemPopCounter", "totem pops count!", Category.CHAT);
    }

    @SubscribeEvent
    public void onTotemPop(TotemPopEvent event) {
        if(event.getPopEntity() instanceof EntityPlayer) {
            boolean isFriend = Kisman.instance.friendManager.isFriend((EntityPlayer) event.getPopEntity());
            if(isFriend && target.getValEnum().equals(TargetMode.OtherPLayers)) {
                return;
            }

            if(!isFriend && target.getValEnum().equals(TargetMode.Friend)) {
                return;
            }
//            final Totem player = new Totem((EntityPlayer) event.getPopEntity());
            ChatUtils.warning((isFriend ? TextFormatting.AQUA : TextFormatting.GRAY) + event.getPopEntity().getName() + TextFormatting.GRAY + " was popped totem!");
            /*if(pops.contains(player)) {
                pops.get(pops.indexOf(player)).addPop();
            } else {
                player.addPop();
                pops.add(player);
            }*/
        }
    }

    public enum TargetMode {
        Friend("Only Friends"),
        OtherPLayers("Only Other Players"),
        Both("Both");

        public String name;

        TargetMode(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
