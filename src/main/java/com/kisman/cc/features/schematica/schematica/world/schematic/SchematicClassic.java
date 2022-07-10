package com.kisman.cc.features.schematica.schematica.world.schematic;

import com.kisman.cc.features.schematica.schematica.api.ISchematic;
import com.kisman.cc.features.schematica.schematica.reference.Names;

import net.minecraft.nbt.NBTTagCompound;

// TODO: http://minecraft.gamepedia.com/Data_values_%28Classic%29
public class SchematicClassic extends SchematicFormat {
    @Override
    public ISchematic readFromNBT(final NBTTagCompound tagCompound) {
        // TODO
        return null;
    }

    @Override
    public boolean writeToNBT(final NBTTagCompound tagCompound, final ISchematic schematic) {
        // TODO
        return false;
    }

    @Override
    public String getName() {
        return Names.Formats.CLASSIC;
    }

    @Override
    public String getExtension() {
        return Names.Extensions.SCHEMATIC;
    }
}
