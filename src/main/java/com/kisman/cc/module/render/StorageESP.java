package com.kisman.cc.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.*;
import com.kisman.cc.util.RenderUtil;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.tileentity.TileEntityShulkerBox;

public class StorageESP extends Module{
    int distance;

    boolean chest = true;
    boolean eChest = true;
    boolean shulkerBox = true;
    boolean dispenser = true;
    boolean furnace = true;
    boolean hopper = true;
    boolean dropper = true;

    public StorageESP() {
        super("StorageESP", "sosat", Category.RENDER);

        Kisman.instance.settingsManager.rSetting(new Setting("Distance", this, 100, 10, 100, true));

        Kisman.instance.settingsManager.rSetting(new Setting("Chest", this, true));
        Kisman.instance.settingsManager.rSetting(new Setting("EChest", this, true));
        Kisman.instance.settingsManager.rSetting(new Setting("ShulkerBox", this, true));
        Kisman.instance.settingsManager.rSetting(new Setting("Dispenser", this, true));
        Kisman.instance.settingsManager.rSetting(new Setting("Furnace", this, true));
        Kisman.instance.settingsManager.rSetting(new Setting("Hopper", this, true));
        Kisman.instance.settingsManager.rSetting(new Setting("Dropper", this, true));
    }

    public void update() {
        distance = (int) Kisman.instance.settingsManager.getSettingByName(this, "Distance").getValDouble();

        chest = Kisman.instance.settingsManager.getSettingByName(this, "Chest").getValBoolean();
        eChest = Kisman.instance.settingsManager.getSettingByName(this, "EChest").getValBoolean();
        shulkerBox = Kisman.instance.settingsManager.getSettingByName(this, "ShulkerBox").getValBoolean();
        dispenser = Kisman.instance.settingsManager.getSettingByName(this, "Dispenser").getValBoolean();
        furnace = Kisman.instance.settingsManager.getSettingByName(this, "Furnace").getValBoolean();
        hopper = Kisman.instance.settingsManager.getSettingByName(this, "Hopper").getValBoolean();
        dropper = Kisman.instance.settingsManager.getSettingByName(this, "Dropper").getValBoolean();
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        mc.world.loadedTileEntityList.stream()
            .filter(tileEntity -> tileEntity.getDistanceSq(mc.player.posX, mc.player.posY, mc.player.posZ) <= distance)
            .forEach(tileEntity -> {
                if(tileEntity instanceof TileEntityChest && chest) {
                    RenderUtil.drawBlockESP(tileEntity.getPos(), 0.94f, 0.60f, 0.11f);
                }
                if(tileEntity instanceof TileEntityEnderChest && eChest) {
                    RenderUtil.drawBlockESP(tileEntity.getPos(), 0.53f, 0.11f, 0.94f);
                }
                if(tileEntity instanceof TileEntityShulkerBox && shulkerBox) {
                    RenderUtil.drawBlockESP(tileEntity.getPos(), 0.8f, 0.08f, 0.93f);
                }
                if(tileEntity instanceof TileEntityDispenser && dispenser) {
                    RenderUtil.drawBlockESP(tileEntity.getPos(), 0.34f, 0.32f, 0.34f);
                }
                if(tileEntity instanceof TileEntityFurnace && furnace) {
                    RenderUtil.drawBlockESP(tileEntity.getPos(), 0.34f, 0.32f, 0.34f);
                }
                if(tileEntity instanceof TileEntityHopper && hopper) {
                    RenderUtil.drawBlockESP(tileEntity.getPos(), 0.34f, 0.32f, 0.34f);
                }
                if(tileEntity instanceof TileEntityDropper && dropper) {
                    RenderUtil.drawBlockESP(tileEntity.getPos(), 0.34f, 0.32f, 0.34f);
                }
            }
        );
    }
}
