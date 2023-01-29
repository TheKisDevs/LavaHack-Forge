package com.kisman.cc.features.module.render;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.render.storageesp.TileEntityImplementation;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.util.MultiThreaddableModulePattern;
import com.kisman.cc.util.enums.StorageESPTileEntities;
import com.kisman.cc.util.client.interfaces.Drawable;
import com.kisman.cc.util.client.interfaces.ITileEntityImplementation;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class StorageESP extends Module implements Drawable {
    private final Setting distance = register(new Setting("Distance(Squared)", this, 4000, 10, 4000, true));

    private final ArrayList<ITileEntityImplementation> implementations = new ArrayList<>(Arrays.asList(
            new TileEntityImplementation(StorageESPTileEntities.Chest, this),
            new TileEntityImplementation(StorageESPTileEntities.EnderChest, this),
            new TileEntityImplementation(StorageESPTileEntities.Furnace, this),
            new TileEntityImplementation(StorageESPTileEntities.FlowerPot, this),
            new TileEntityImplementation(StorageESPTileEntities.Dispenser, this),
            new TileEntityImplementation(StorageESPTileEntities.Dropper, this),
            new TileEntityImplementation(StorageESPTileEntities.Hopper, this),
            new TileEntityImplementation(StorageESPTileEntities.Shulker, this)
    ));

    private final MultiThreaddableModulePattern threads = threads();

    private ArrayList<TileEntity> entities = new ArrayList<>();

    public StorageESP() {
        super("StorageESP", "ESP for storages", Category.RENDER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        threads.reset();
        entities.clear();
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        threads.update(() -> {
            ArrayList<TileEntity> list = new ArrayList<>();

            for(TileEntity tile : mc.world.loadedTileEntityList) if(mc.player.getDistanceSq(tile.getPos()) < distance.getValInt()) for(ITileEntityImplementation impl : implementations) if(impl.valid(tile, null)) list.add(tile);

            mc.addScheduledTask(() -> entities = list);
        });

        doStorageESP(false);
    }

    @Override
    public void draw() {
        doStorageESP(true);
    }

    private void doStorageESP(boolean callingFromDraw) {
        for(TileEntity tile : entities) for(ITileEntityImplementation impl : implementations) impl.process(tile, callingFromDraw);
    }
}
