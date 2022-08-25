package com.kisman.cc.features.module.misc;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraft.network.play.client.CPacketHeldItemChange;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SlotMapper extends Module {

    private final Setting slot1 = register(new Setting("Slot 1", this, 0, 0, 9, true));
    private final Setting slot2 = register(new Setting("Slot 2", this, 0, 0, 9, true));
    private final Setting slot3 = register(new Setting("Slot 3", this, 0, 0, 9, true));
    private final Setting slot4 = register(new Setting("Slot 4", this, 0, 0, 9, true));
    private final Setting slot5 = register(new Setting("Slot 5", this, 0, 0, 9, true));
    private final Setting slot6 = register(new Setting("Slot 6", this, 0, 0, 9, true));
    private final Setting slot7 = register(new Setting("Slot 7", this, 0, 0, 9, true));
    private final Setting slot8 = register(new Setting("Slot 8", this, 0, 0, 9, true));
    private final Setting slot9 = register(new Setting("Slot 9", this, 0, 0, 9, true));

    private final Map<Integer, Boolean> map;

    public SlotMapper(){
        super("SlotMapper", Category.MISC);
        this.map = new ConcurrentHashMap<>();
        this.map.put(1, false);
        this.map.put(2, false);
        this.map.put(3, false);
        this.map.put(4, false);
        this.map.put(5, false);
        this.map.put(6, false);
        this.map.put(7, false);
        this.map.put(8, false);
        this.map.put(9, false);
    }

    @Override
    public void update(){
        if(mc.player == null || mc.world == null)
            return;

        int slot = mc.player.inventory.currentItem;

        if(mc.player.isHandActive()){
            map.put(slot, false);
            return;
        }

        if(map.get(slot))
            return;

        Setting setting = settingForSlot(slot);

        if(setting == null)
            return;

        int switchSlot = setting.getValInt();

        if(switchSlot == 0)
            return;

        mc.player.connection.sendPacket(new CPacketHeldItemChange(switchSlot));
        mc.player.inventory.currentItem = switchSlot;

        map.put(switchSlot, true);
    }

    private Setting settingForSlot(int slot){
        switch(slot){
            case 1: return slot1;
            case 2: return slot2;
            case 3: return slot3;
            case 4: return slot4;
            case 5: return slot5;
            case 6: return slot6;
            case 7: return slot7;
            case 8: return slot8;
            case 9: return slot9;
            default: return null;
        }
    }
}
