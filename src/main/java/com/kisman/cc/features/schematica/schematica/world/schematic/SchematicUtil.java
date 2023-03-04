package com.kisman.cc.features.schematica.schematica.world.schematic;

import com.kisman.cc.features.schematica.schematica.reference.Names;
import com.kisman.cc.features.schematica.schematica.reference.Reference;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public final class SchematicUtil {
    public static final ItemStack DEFAULT_ICON = new ItemStack(Blocks.GRASS);

    public static NBTTagCompound readTagCompoundFromFile(File file) throws IOException {
        try {
            return CompressedStreamTools.readCompressed(new FileInputStream(file));
        } catch (Exception ex) {
            Reference.logger.warn("Failed compressed read, trying normal read...", ex);
            return CompressedStreamTools.read(file);
        }
    }

    public static ItemStack getIconFromName(String iconName) {
        //TODO: fix it lol
        /*ResourceLocation rl = null;
        int damage = 0;

        final String[] parts = iconName.split(",");
        if (parts.length >= 1) {
            rl = new ResourceLocation(parts[0]);
            if (parts.length >= 2) {
                try {
                    damage = Integer.parseInt(parts[1]);
                } catch (final NumberFormatException ignored) {
                }
            }
        }

        if (rl == null) {
            return DEFAULT_ICON.copy();
        }

        final ItemStack block = new ItemStack(Block.REGISTRY.getObject(rl), 1, damage);
        if (!block.isEmpty()) {
            return block;
        }

        final ItemStack item = new ItemStack(Item.REGISTRY.getObject(rl), 1, damage);
        if (!item.isEmpty()) {
            return item;
        }*/

        return DEFAULT_ICON.copy();
    }

    public static ItemStack getIconFromNBT(final NBTTagCompound tagCompound) {
        ItemStack icon = DEFAULT_ICON.copy();

        if (tagCompound != null && tagCompound.hasKey(Names.NBT.ICON)) {
            icon.deserializeNBT(tagCompound.getCompoundTag(Names.NBT.ICON));

            if (icon.isEmpty()) {
                icon = DEFAULT_ICON.copy();
            }
        }

        return icon;
    }

    public static ItemStack getIconFromFile() {
        return DEFAULT_ICON.copy();
    }
}
