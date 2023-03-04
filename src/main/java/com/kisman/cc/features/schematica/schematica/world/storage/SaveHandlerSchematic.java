package com.kisman.cc.features.schematica.schematica.world.storage;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SaveHandlerSchematic implements ISaveHandler {
    @Override
    public WorldInfo loadWorldInfo() {
        return null;
    }

    @Override
    public void checkSessionLock() throws MinecraftException {}

    @Override
    public IChunkLoader getChunkLoader(WorldProvider provider) {
        return null;
    }

    @Override
    public void saveWorldInfoWithPlayer(WorldInfo info, NBTTagCompound compound) {}

    @Override
    public void saveWorldInfo(WorldInfo info) {}

    @Override
    public IPlayerFileData getPlayerNBTManager() {
        return null;
    }

    @Override
    public void flush() {}

    @Override
    public File getWorldDirectory() {
        return null;
    }

    @Override
    public File getMapFileFromName(String name) {
        return null;
    }

    @Override
    public TemplateManager getStructureTemplateManager() {
        return null;
    }
}
