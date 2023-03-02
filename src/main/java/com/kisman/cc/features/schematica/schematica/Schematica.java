package com.kisman.cc.features.schematica.schematica;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.schematica.schematica.handler.ConfigurationHandler;
import com.kisman.cc.features.schematica.schematica.handler.DownloadHandler;
import com.kisman.cc.features.schematica.schematica.handler.QueueTickHandler;
import com.kisman.cc.features.schematica.schematica.network.PacketHandler;
import com.kisman.cc.features.schematica.schematica.proxy.ClientProxy;
import com.kisman.cc.features.schematica.schematica.reference.Reference;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Schematica {
    public static File CONFIG_FOLDER = new File(Kisman.fileName + "schematica/");
    public static File CONFIG_FILE = new File(Kisman.fileName + "schematica/schematica.kis");

    public static Schematica instance = new Schematica();

    public static ClientProxy proxy = new ClientProxy();

    public static HashMap<String, String> properties = new HashMap<>();

    public void init() {
        Kisman.processAccountData();

        if(!CONFIG_FILE.exists()) {
            try {
                CONFIG_FILE.createNewFile();
            } catch (IOException e) {
                Reference.logger.error("Cannot create the config file!");
            }
        }

        ConfigurationHandler.init(CONFIG_FILE);

        proxy.preInit();


        PacketHandler.init();

        MinecraftForge.EVENT_BUS.register(QueueTickHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(DownloadHandler.INSTANCE);


        proxy.init();


        proxy.postInit();

        properties.put("schematica.gui.x", "X:");
        properties.put("schematica.gui.y", "Y:");
        properties.put("schematica.gui.z", "Z:");
        properties.put("schematica.gui.on", "ON");
        properties.put("schematica.gui.off", "OFF");
        properties.put("schematica.gui.done", "Done");
        properties.put("schematica.gui.down", "Down");
        properties.put("schematica.gui.up", "Up");
        properties.put("schematica.gui.north", "North");
        properties.put("schematica.gui.south", "South");
        properties.put("schematica.gui.west", "West");
        properties.put("schematica.gui.east", "East");
        properties.put("schematica.gui.title", "Select schematic file");
        properties.put("schematica.gui.folderInfo", "(Place schematic files here)");
        properties.put("schematica.gui.openFolder", "Open schematic folder");
        properties.put("schematica.gui.noschematic", "-- No schematic --");
        properties.put("schematica.gui.point.red", "Red point");
        properties.put("schematica.gui.point.blue", "Blue point");
        properties.put("schematica.gui.save", "Save");
        properties.put("schematica.gui.saveselection", "Save the selection as a schematic");
        properties.put("schematica.gui.format", "Format: %s");
        properties.put("schematica.gui.moveschematic", "Move schematic");
        properties.put("schematica.gui.materials", "Materials");
        properties.put("schematica.gui.printer", "Printer");
        properties.put("schematica.gui.operations", "Operations");
        properties.put("schematica.gui.unload", "Unload");
        properties.put("schematica.gui.all", "ALL");
        properties.put("schematica.gui.layers", "Layers");
        properties.put("schematica.gui.below", "All below");
        properties.put("schematica.gui.hide", "Hide");
        properties.put("schematica.gui.show", "Show");
        properties.put("schematica.gui.movehere", "Move here");
        properties.put("schematica.gui.flip", "Flip");
        properties.put("schematica.gui.rotate", "Rotate");
        properties.put("schematica.gui.materialname", "Material");
        properties.put("schematica.gui.materialamount", "Amount");
        properties.put("schematica.gui.materialavailable", "Available");
        properties.put("schematica.gui.materialmissing", "Missing");
        properties.put("schematica.gui.materialdump", "Save to file");
        properties.put("schematica.config.category.debug", "Debug");
        properties.put("schematica.config.category.debug.tooltip", "Debug/developer settings.\nDon't touch unless you know what you're doing.");
        properties.put("schematica.config.category.render", "Rendering");
        properties.put("schematica.config.category.render.tooltip", "Render related settings.");
        properties.put("schematica.config.category.printer", "Printer");
        properties.put("schematica.config.category.printer.tooltip", "Printer related settings.");
        properties.put("schematica.config.category.printer.swapslots", "Hotbar Slots");
        properties.put("schematica.config.category.printer.swapslots.tooltip", "Allow the printer to use non empty hotbar slots.");
        properties.put("schematica.config.category.general", "General");
        properties.put("schematica.config.category.general.tooltip", "General settings.");
        properties.put("schematica.config.category.server", "Server");
        properties.put("schematica.config.category.server.tooltip", "Server-side settings.");
        properties.put("schematica.config.dumpBlockList", "Dump Block List");
        properties.put("schematica.config.dumpBlockList.tooltip", "Dump all block states on startup.");
        properties.put("schematica.config.showDebugInfo", "Show Debug Info");
        properties.put("schematica.config.showDebugInfo.tooltip", "Display extra information on the debug screen (F3).");
        properties.put("schematica.config.alphaEnabled", "Alpha Enabled");
        properties.put("schematica.config.alphaEnabled.tooltip", "Enable transparent textures.");
        properties.put("schematica.config.alpha", "Alpha");
        properties.put("schematica.config.alpha.tooltip", "Alpha value used when rendering the schematic (1.0 = opaque, 0.5 = half transparent, 0.0 = transparent).");
        properties.put("schematica.config.highlight", "Highlight Blocks");
        properties.put("schematica.config.highlight.tooltip", "Highlight invalid placed blocks and to be placed blocks.");
        properties.put("schematica.config.highlightAir", "Highlight Air");
        properties.put("schematica.config.highlightAir.tooltip", "Highlight blocks that should be air.");
        properties.put("schematica.config.blockDelta", "Block Delta");
        properties.put("schematica.config.blockDelta.tooltip", "Delta value used for highlighting (if you experience z-fighting increase this).");
        properties.put("schematica.config.renderDistance", "Render Distance");
        properties.put("schematica.config.renderDistance.tooltip", "Schematic render distance.");
        properties.put("schematica.config.placeDelay", "Placement Delay");
        properties.put("schematica.config.placeDelay.tooltip", "Delay between placement attempts (in ticks).");
        properties.put("schematica.config.timeout", "Timeout");
        properties.put("schematica.config.timeout.tooltip", "Timeout before re-trying failed blocks.");
        properties.put("schematica.config.placeDistance", "Placement Distance");
        properties.put("schematica.config.placeDistance.tooltip", "Maximum placement distance.");
        properties.put("schematica.config.placeInstantly", "Place Instantly");
        properties.put("schematica.config.placeInstantly.tooltip", "Place all blocks that can be placed in one tick.");
        properties.put("schematica.config.destroyBlocks", "Destroy Blocks");
        properties.put("schematica.config.destroyBlocks.tooltip", "The printer will destroy blocks (creative mode only).");
        properties.put("schematica.config.destroyInstantly", "Destroy Instantly");
        properties.put("schematica.config.destroyInstantly.tooltip", "Destroy all blocks that can be destroyed in one tick.");
        properties.put("schematica.config.placeAdjacent", "Place Only Adjacent");
        properties.put("schematica.config.placeAdjacent.tooltip", "Place blocks only if there is an adjacent block next to them.");
        properties.put("schematica.config.swapSlot0", "Allow Slot 1");
        properties.put("schematica.config.swapSlot0.tooltip", "Allow the printer to use the hotbar slot 1.");
        properties.put("schematica.config.swapSlot1", "Allow Slot 2");
        properties.put("schematica.config.swapSlot1.tooltip", "Allow the printer to use the hotbar slot 2.");
        properties.put("schematica.config.swapSlot2", "Allow Slot 3");
        properties.put("schematica.config.swapSlot2.tooltip", "Allow the printer to use the hotbar slot 3.");
        properties.put("schematica.config.swapSlot3", "Allow Slot 4");
        properties.put("schematica.config.swapSlot3.tooltip", "Allow the printer to use the hotbar slot 4.");
        properties.put("schematica.config.swapSlot4", "Allow Slot 5");
        properties.put("schematica.config.swapSlot4.tooltip", "Allow the printer to use the hotbar slot 5.");
        properties.put("schematica.config.swapSlot5", "Allow Slot 6");
        properties.put("schematica.config.swapSlot5.tooltip", "Allow the printer to use the hotbar slot 6.");
        properties.put("schematica.config.swapSlot6", "Allow Slot 7");
        properties.put("schematica.config.swapSlot6.tooltip", "Allow the printer to use the hotbar slot 7.");
        properties.put("schematica.config.swapSlot7", "Allow Slot 8");
        properties.put("schematica.config.swapSlot7.tooltip", "Allow the printer to use the hotbar slot 8.");
        properties.put("schematica.config.swapSlot8", "Allow Slot 9");
        properties.put("schematica.config.swapSlot8.tooltip", "Allow the printer to use the hotbar slot 9.");
        properties.put("schematica.config.schematicDirectory", "Schematic Directory");
        properties.put("schematica.config.schematicDirectory.tooltip", "Schematic directory.");
        properties.put("schematica.config.extraAirBlocks", "Extra Air Blocks");
        properties.put("schematica.config.extraAirBlocks.tooltip", "Extra blocks to consider as air for the schematic renderer.");
        properties.put("schematica.config.printerEnabled", "Allow Printer");
        properties.put("schematica.config.printerEnabled.tooltip", "Allow players to use the printer.");
        properties.put("schematica.config.saveEnabled", "Allow Saving");
        properties.put("schematica.config.saveEnabled.tooltip", "Allow players to save schematics.");
        properties.put("schematica.config.loadEnabled", "Allow Loading");
        properties.put("schematica.config.loadEnabled.tooltip", "Allow players to load schematics.");
        properties.put("schematica.key.category", "Schematica");
        properties.put("schematica.key.load", "Load schematic");
        properties.put("schematica.key.save", "Save schematic");
        properties.put("schematica.key.control", "Manipulate schematic");
        properties.put("schematica.key.layerInc", "Next Layer");
        properties.put("schematica.key.layerDec", "Previous Layer");
        properties.put("schematica.key.layerToggle", "Toggle All/Layer Mode");
        properties.put("schematica.key.renderToggle", "Toggle Rendering");
        properties.put("schematica.key.printerToggle", "Toggle Printer");
        properties.put("schematica.key.moveHere", "Move here");
        properties.put("schematica.key.pickBlock", "Pick Block");
        properties.put("schematica.command.save.usage", "/schematicaSave <startX> <startY> <startZ> <endX> <endY> <endZ> <name> [format]");
        properties.put("schematica.command.save.playersOnly", "This command can only be used by players.");
        properties.put("schematica.command.save.started", "Started saving %d chunks into %s.");
        properties.put("schematica.command.save.saveSucceeded", "Successfully saved %s.");
        properties.put("schematica.command.save.saveFailed", "There was a problem saving the schematic %s.");
        properties.put("schematica.command.save.quotaExceeded", "You have exceeded your quota on the server. Please use /schematicaList and /schematicaRemove to remove some old schematics.");
        properties.put("schematica.command.save.playerSchematicDirUnavailable", "There was a problem on the server and your schematic was not saved. Please contact the server administrator.");
        properties.put("schematica.command.save.unknownFormat", "Unknown format \"%s\".");
        properties.put("schematica.command.list.usage", "/schematicaList [page]");
        properties.put("schematica.command.list.notAvailable", "There was a problem retrieving your list of schematics.");
        properties.put("schematica.command.list.remove", "Remove");
        properties.put("schematica.command.list.download", "Download");
        properties.put("schematica.command.list.header", "--- Showing schematics page %d of %d ---");
        properties.put("schematica.command.list.noSuchPage", "No such page");
        properties.put("schematica.command.list.noSchematics", "You have no schematics available.");
        properties.put("schematica.command.remove.usage", "/schematicaRemove <name>");
        properties.put("schematica.command.remove.schematicRemoved", "Schematic \"%s\" removed.");
        properties.put("schematica.command.remove.schematicNotFound", "Schematic \"%s\" could not be found.");
        properties.put("schematica.command.remove.areYouSure", "Are you sure?");
        properties.put("schematica.command.download.usage", "/schematicaDownload <name>");
        properties.put("schematica.command.download.started", "Started downloading %s...");
        properties.put("schematica.command.download.downloadSucceeded", "Successfully downloaded %s.");
        properties.put("schematica.command.download.downloadFail", "Download failed.");
        properties.put("schematica.command.replace.usage", "/schematicaReplace <original> <replacement>");
        properties.put("schematica.command.replace.noSchematic", "You don't have a schematic loaded.");
        properties.put("schematica.command.replace.success", "Replaced %s blocks.");
        properties.put("schematica.message.togglePrinter", "Printer: %s");
        properties.put("schematica.message.invalidBlock", "\"%s\" does not exist.");
        properties.put("schematica.message.invalidProperty", "\"%s\" is not a valid property.");
        properties.put("schematica.message.invalidPropertyForBlock", "\"%s\" is not a valid property for \"%s\".");
        properties.put("schematica.format.classic", "Classic");
        properties.put("schematica.format.alpha", "Alpha (standard)");
        properties.put("schematica.format.structure", "Structure block");
        properties.put("schematica.format.invalid", "Invalid");
    }
}
