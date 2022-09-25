package com.kisman.cc.features.module.render;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.settings.util.BoxRendererPattern;
import com.kisman.cc.settings.util.MultiThreaddableModulePattern;
import com.kisman.cc.util.ColourUtilKt;
import net.minecraft.tileentity.*;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

public class StorageESP extends Module{
    private final Setting distance = register(new Setting("Distance(Squared)", this, 4000, 10, 4000, true));
    private final Setting colorAlpha = register(new Setting("Color Alpha", this, 255, 0, 255, true));
    private final SettingGroup blocks = register(new SettingGroup(new Setting("Blocks", this)));
    private final Setting chest = register(blocks.add(new Setting("Chest", this, false)));
    private final Setting echest = register(blocks.add(new Setting("Ender Chest", this, false)));
    private final Setting shulker = register(blocks.add(new Setting("Shulker Box", this, false)));
    private final Setting dispenser = register(blocks.add(new Setting("Dispenser", this, false)));
    private final Setting furnace = register(blocks.add(new Setting("Furnace", this, false)));
    private final Setting hopper = register(blocks.add(new Setting("Hopper", this, false)));
    private final Setting dropper = register(blocks.add(new Setting("Dropper", this, false)));
    private final BoxRendererPattern renderer = new BoxRendererPattern(this).init();
    private final MultiThreaddableModulePattern multiThread = threads();

    private ArrayList<TileEntity> list = new ArrayList<>();

    public StorageESP() {
        super("StorageESP", "ESP for storages", Category.RENDER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        multiThread.reset();
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        multiThread.update(this::doStorageESPLogic);
        doStorageESP(event.getPartialTicks());
    }

    private void doStorageESPLogic() {
        ArrayList<TileEntity> list = new ArrayList<>();
        mc.world.loadedTileEntityList.stream()
                .filter(tileEntity -> tileEntity.getDistanceSq(mc.player.posX, mc.player.posY, mc.player.posZ) <= distance.getValDouble())
                .forEach(tileEntity -> {
                            if(tileEntity instanceof TileEntityChest && chest.getValBoolean()) list.add(tileEntity);
                            if(tileEntity instanceof TileEntityEnderChest && echest.getValBoolean()) list.add(tileEntity);
                            if(tileEntity instanceof TileEntityShulkerBox && shulker.getValBoolean()) list.add(tileEntity);
                            if(tileEntity instanceof TileEntityDispenser && dispenser.getValBoolean()) list.add(tileEntity);
                            if(tileEntity instanceof TileEntityFurnace && furnace.getValBoolean()) list.add(tileEntity);
                            if(tileEntity instanceof TileEntityHopper && hopper.getValBoolean()) list.add(tileEntity);
                            if(tileEntity instanceof TileEntityDropper && dropper.getValBoolean()) list.add(tileEntity);
                        }
                );

        mc.addScheduledTask(() -> this.list = list);
    }

    private void doStorageESP(float ticks) {
        list.forEach(tileEntity -> {
            if(tileEntity instanceof TileEntityChest && chest.getValBoolean()) renderer.draw(ticks, ColourUtilKt.BlockColors.Companion.getChestColor(), tileEntity.getPos(), colorAlpha.getValInt());
            if(tileEntity instanceof TileEntityEnderChest && echest.getValBoolean()) renderer.draw(ticks, ColourUtilKt.BlockColors.Companion.getEnderChestColor(), tileEntity.getPos(), colorAlpha.getValInt());
            if(tileEntity instanceof TileEntityShulkerBox && shulker.getValBoolean()) renderer.draw(ticks, ColourUtilKt.BlockColors.Companion.getShulkerBoxColor(), tileEntity.getPos(), colorAlpha.getValInt());
            if(tileEntity instanceof TileEntityDispenser && dispenser.getValBoolean()) renderer.draw(ticks, ColourUtilKt.BlockColors.Companion.getDispenserColor(), tileEntity.getPos(), colorAlpha.getValInt());
            if(tileEntity instanceof TileEntityFurnace && furnace.getValBoolean()) renderer.draw(ticks, ColourUtilKt.BlockColors.Companion.getFurnaceColor(), tileEntity.getPos(), colorAlpha.getValInt());
            if(tileEntity instanceof TileEntityHopper && hopper.getValBoolean()) renderer.draw(ticks, ColourUtilKt.BlockColors.Companion.getHopperColor(), tileEntity.getPos(), colorAlpha.getValInt());
            if(tileEntity instanceof TileEntityDropper && dropper.getValBoolean()) renderer.draw(ticks, ColourUtilKt.BlockColors.Companion.getDropperColor(), tileEntity.getPos(), colorAlpha.getValInt());
        });
    }
}
