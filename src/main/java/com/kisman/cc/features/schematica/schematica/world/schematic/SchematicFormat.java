package com.kisman.cc.features.schematica.schematica.world.schematic;

import com.kisman.cc.features.schematica.schematica.api.ISchematic;
import com.kisman.cc.features.schematica.schematica.api.event.PostSchematicCaptureEvent;
import com.kisman.cc.features.schematica.schematica.reference.Names;
import com.kisman.cc.features.schematica.schematica.reference.Reference;
import com.kisman.cc.mixin.accessors.INBTTagCompound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

@SuppressWarnings({"ConstantConditions"})
public abstract class SchematicFormat {
    // LinkedHashMap to ensure defined iteration order
    public static final Map<String, SchematicFormat> FORMATS = new LinkedHashMap<>();
    public static String FORMAT_DEFAULT = Names.NBT.FORMAT_ALPHA;

    public abstract ISchematic readFromNBT(NBTTagCompound tagCompound);

    public abstract boolean writeToNBT(NBTTagCompound tagCompound, ISchematic schematic);

    /**
     * Gets the translation key used for this format.
     */
    public abstract String getName();

    /**
     * Gets the file extension used for this format, including the leading dot.
     */
    public abstract String getExtension();

    public static ISchematic readFromFile(File file) {
        try {
            NBTTagCompound tagCompound = SchematicUtil.readTagCompoundFromFile(file);
            SchematicFormat schematicFormat;
            if (tagCompound.hasKey(Names.NBT.MATERIALS)) {
                String format = tagCompound.getString(Names.NBT.MATERIALS);
                schematicFormat = FORMATS.get(format);

                if (schematicFormat == null) {
                    throw new UnsupportedFormatException(format);
                }
            } else {
                schematicFormat = FORMATS.get(Names.NBT.FORMAT_STRUCTURE);
            }

            return schematicFormat.readFromNBT(tagCompound);
        } catch (Exception ex) {
            Reference.logger.error("Failed to read schematic!", ex);
        }

        return null;
    }

    public static ISchematic readFromFile(File directory, String filename) {
        return readFromFile(new File(directory, filename));
    }

    /**
     * Writes the given schematic.
     *
     * @param file The file to write to
     * @param format The format to use, or null for {@linkplain #FORMAT_DEFAULT the default}
     * @param schematic The schematic to write
     * @return True if successful
     */
    public static boolean writeToFile(File file, @Nullable String format, ISchematic schematic) {
        try {
            PostSchematicCaptureEvent event = new PostSchematicCaptureEvent(schematic);
            MinecraftForge.EVENT_BUS.post(event);

            NBTTagCompound tagCompound = new NBTTagCompound();

            FORMATS.get(FORMAT_DEFAULT).writeToNBT(tagCompound, schematic);

            DataOutputStream dataOutputStream = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(file)));

            try {
                dataOutputStream.writeByte(tagCompound.getId());

                if (tagCompound.getId() != 0) {
                    dataOutputStream.writeUTF(Names.NBT.ROOT);
                    ((INBTTagCompound) tagCompound).handleWrite(dataOutputStream);
                }
            } finally {
                dataOutputStream.close();
            }

            return true;
        } catch (final Exception ex) {
            Reference.logger.error("Failed to write schematic!", ex);
        }

        return false;
    }

    /**
     * Writes the given schematic.
     *
     * @param directory The directory to write in
     * @param filename The filename (including the extension) to write to
     * @param format The format to use, or null for {@linkplain #FORMAT_DEFAULT the default}
     * @param schematic The schematic to write
     * @return True if successful
     */
    public static boolean writeToFile(final File directory, final String filename, @Nullable final String format, final ISchematic schematic) {
        return writeToFile(new File(directory, filename), format, schematic);
    }

    /**
     * Writes the given schematic, notifying the player when finished.
     *
     * @param file The file to write to
     * @param format The format to use, or null for {@linkplain #FORMAT_DEFAULT the default}
     * @param schematic The schematic to write
     * @param player The player to notify
     */
    public static void writeToFileAndNotify(File file, @Nullable String format, ISchematic schematic, EntityPlayer player) {
        boolean success = writeToFile(file, format, schematic);
        String message = success ? Names.Command.Save.Message.SAVE_SUCCESSFUL : Names.Command.Save.Message.SAVE_FAILED;
        player.sendMessage(new TextComponentTranslation(message, file.getName()));
    }

    /**
     * Gets a schematic format name translation key for the given format ID.
     *
     * If an invalid format is chosen, logs a warning and returns a key stating
     * that it's invalid.
     *
     * @param format The format.
     */
    public static String getFormatName(final String format) {
        if (!FORMATS.containsKey(format)) {
            Reference.logger.warn("No format with id {}; returning invalid for name", format, new UnsupportedFormatException(format).fillInStackTrace());
            return Names.Formats.INVALID;
        }
        return FORMATS.get(format).getName();
    }

    /**
     * Gets the extension used by the given format.
     *
     * If the format is invalid, returns the default format's extension.
     *
     * @param format The format (or null to use {@link #FORMAT_DEFAULT the default}).
     */
    public static String getExtension(@Nullable String format) {
        if (format == null) {
            format = FORMAT_DEFAULT;
        }
        if (!FORMATS.containsKey(format)) {
            Reference.logger.warn("No format with id {}; returning default extension", format, new UnsupportedFormatException(format).fillInStackTrace());
            format = FORMAT_DEFAULT;
        }
        return FORMATS.get(format).getExtension();
    }

    static {
        // TODO?
        // FORMATS.put(Names.NBT.FORMAT_CLASSIC, new SchematicClassic());
        FORMATS.put(Names.NBT.FORMAT_ALPHA, new SchematicAlpha());
    }
}
