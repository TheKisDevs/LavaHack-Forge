package com.kisman.cc.features.schematica.schematica.reference;

import com.kisman.cc.features.schematica.schematica.Schematica;

@SuppressWarnings("HardCodedStringLiteral")
public final class Names {
    public static final class Config {
        public static final class Category {
            public static final String DEBUG = Schematica.properties.get("debug");
            public static final String RENDER = Schematica.properties.get("render");
            public static final String PRINTER = Schematica.properties.get("printer");
            public static final String PRINTER_SWAPSLOTS = Schematica.properties.get("printer.swapslots");
            public static final String GENERAL = Schematica.properties.get("general");
            public static final String SERVER = Schematica.properties.get("server");
        }

        public static final String DUMP_BLOCK_LIST = Schematica.properties.get("dumpBlockList");
        public static final String DUMP_BLOCK_LIST_DESC = Schematica.properties.get("Dump all block states on startup.");
        public static final String SHOW_DEBUG_INFO = Schematica.properties.get("showDebugInfo");
        public static final String SHOW_DEBUG_INFO_DESC = Schematica.properties.get("Display extra information on the debug screen (F3).");

        public static final String ALPHA_ENABLED = Schematica.properties.get("alphaEnabled");
        public static final String ALPHA_ENABLED_DESC = Schematica.properties.get("Enable transparent textures.");
        public static final String ALPHA = Schematica.properties.get("alpha");
        public static final String ALPHA_DESC = Schematica.properties.get("Alpha value used when rendering the schematic (1.0 = opaque, 0.5 = half transparent, 0.0 = transparent).");
        public static final String HIGHLIGHT = Schematica.properties.get("highlight");
        public static final String HIGHLIGHT_DESC = Schematica.properties.get("Highlight invalid placed blocks and to be placed blocks.");
        public static final String HIGHLIGHT_AIR = Schematica.properties.get("highlightAir");
        public static final String HIGHLIGHT_AIR_DESC = Schematica.properties.get("Highlight blocks that should be air.");
        public static final String BLOCK_DELTA = Schematica.properties.get("blockDelta");
        public static final String BLOCK_DELTA_DESC = Schematica.properties.get("Delta value used for highlighting (if you experience z-fighting increase this).");
        public static final String RENDER_DISTANCE = Schematica.properties.get("renderDistance");
        public static final String RENDER_DISTANCE_DESC = Schematica.properties.get("Schematic render distance.");

        public static final String PLACE_DELAY = Schematica.properties.get("placeDelay");
        public static final String PLACE_DELAY_DESC = Schematica.properties.get("Delay between placement attempts (in ticks).");
        public static final String TIMEOUT = Schematica.properties.get("timeout");
        public static final String TIMEOUT_DESC = Schematica.properties.get("Timeout before re-trying failed blocks.");
        public static final String PLACE_DISTANCE = Schematica.properties.get("placeDistance");
        public static final String PLACE_DISTANCE_DESC = Schematica.properties.get("Maximum placement distance.");
        public static final String PLACE_INSTANTLY = Schematica.properties.get("placeInstantly");
        public static final String PLACE_INSTANTLY_DESC = Schematica.properties.get("Place all blocks that can be placed in one tick.");
        public static final String DESTROY_BLOCKS = Schematica.properties.get("destroyBlocks");
        public static final String DESTROY_BLOCKS_DESC = Schematica.properties.get("The printer will destroy blocks (creative mode only).");
        public static final String DESTROY_INSTANTLY = Schematica.properties.get("destroyInstantly");
        public static final String DESTROY_INSTANTLY_DESC = Schematica.properties.get("Destroy all blocks that can be destroyed in one tick.");
        public static final String PLACE_ADJACENT = Schematica.properties.get("placeAdjacent");
        public static final String PLACE_ADJACENT_DESC = Schematica.properties.get("Place blocks only if there is an adjacent block next to them.");
        public static final String SWAP_SLOT = Schematica.properties.get("swapSlot");
        public static final String SWAP_SLOT_DESC = Schematica.properties.get("Allow the printer to use this hotbar slot.");

        public static final String SCHEMATIC_DIRECTORY = Schematica.properties.get("schematicDirectory");
        public static final String SCHEMATIC_DIRECTORY_DESC = Schematica.properties.get("Schematic directory.");
        public static final String EXTRA_AIR_BLOCKS = Schematica.properties.get("extraAirBlocks");
        public static final String EXTRA_AIR_BLOCKS_DESC = Schematica.properties.get("Extra blocks to consider as air for the schematic renderer.");
        public static final String SORT_TYPE = Schematica.properties.get("sortType");
        public static final String SORT_TYPE_DESC = Schematica.properties.get("Default sort type for the material list.");

        public static final String PRINTER_ENABLED = Schematica.properties.get("printerEnabled");
        public static final String PRINTER_ENABLED_DESC = Schematica.properties.get("Allow players to use the printer.");
        public static final String SAVE_ENABLED = Schematica.properties.get("saveEnabled");
        public static final String SAVE_ENABLED_DESC = Schematica.properties.get("Allow players to save schematics.");
        public static final String LOAD_ENABLED = Schematica.properties.get("loadEnabled");
        public static final String LOAD_ENABLED_DESC = Schematica.properties.get("Allow players to load schematics.");

        public static final String PLAYER_QUOTA_KILOBYTES = Schematica.properties.get("playerQuotaKilobytes");
        public static final String PLAYER_QUOTA_KILOBYTES_DESC = Schematica.properties.get("Amount of storage provided per-player for schematics on the server.");

        public static final String LANG_PREFIX = Schematica.properties.get(Reference.MODID + ".config");
    }

    public static final class Command {
        public static final class Save {
            public static final class Message {
                public static final String USAGE = Schematica.properties.get("schematica.command.save.usage");
                public static final String PLAYERS_ONLY = Schematica.properties.get("schematica.command.save.playersOnly");
                public static final String SAVE_STARTED = Schematica.properties.get("schematica.command.save.started");
                public static final String SAVE_SUCCESSFUL = Schematica.properties.get("schematica.command.save.saveSucceeded");
                public static final String SAVE_FAILED = Schematica.properties.get("schematica.command.save.saveFailed");
                public static final String QUOTA_EXCEEDED = Schematica.properties.get("schematica.command.save.quotaExceeded");
                public static final String PLAYER_SCHEMATIC_DIR_UNAVAILABLE = Schematica.properties.get("schematica.command.save.playerSchematicDirUnavailable");
                public static final String UNKNOWN_FORMAT = Schematica.properties.get("schematica.command.save.unknownFormat");
            }

            public static final String NAME = Schematica.properties.get("schematicaSave");
        }

        public static final class List {
            public static final class Message {
                public static final String USAGE = Schematica.properties.get("schematica.command.list.usage");
                public static final String LIST_NOT_AVAILABLE = Schematica.properties.get("schematica.command.list.notAvailable");
                public static final String REMOVE = Schematica.properties.get("schematica.command.list.remove");
                public static final String DOWNLOAD = Schematica.properties.get("schematica.command.list.download");
                public static final String PAGE_HEADER = Schematica.properties.get("schematica.command.list.header");
                public static final String NO_SUCH_PAGE = Schematica.properties.get("schematica.command.list.noSuchPage");
                public static final String NO_SCHEMATICS = Schematica.properties.get("schematica.command.list.noSchematics");
            }

            public static final String NAME = Schematica.properties.get("schematicaList");
        }

        public static final class Remove {
            public static final class Message {
                public static final String USAGE = Schematica.properties.get("schematica.command.remove.usage");
                public static final String PLAYERS_ONLY = Schematica.properties.get("schematica.command.save.playersOnly");
                public static final String SCHEMATIC_REMOVED = Schematica.properties.get("schematica.command.remove.schematicRemoved");
                public static final String SCHEMATIC_NOT_FOUND = Schematica.properties.get("schematica.command.remove.schematicNotFound");
                public static final String ARE_YOU_SURE_START = Schematica.properties.get("schematica.command.remove.areYouSure");
                public static final String YES = Schematica.properties.get("gui.yes");
            }

            public static final String NAME = Schematica.properties.get("schematicaRemove");
        }

        public static final class Download {
            public static final class Message {
                public static final String USAGE = Schematica.properties.get("schematica.command.download.usage");
                public static final String PLAYERS_ONLY = Schematica.properties.get("schematica.command.save.playersOnly");
                public static final String DOWNLOAD_STARTED = Schematica.properties.get("schematica.command.download.started");
                public static final String DOWNLOAD_SUCCEEDED = Schematica.properties.get("schematica.command.download.downloadSucceeded");
                public static final String DOWNLOAD_FAILED = Schematica.properties.get("schematica.command.download.downloadFail");
            }

            public static final String NAME = Schematica.properties.get("schematicaDownload");
        }

        public static final class Replace {
            public static final class Message {
                public static final String USAGE = Schematica.properties.get("schematica.command.replace.usage");
                public static final String NO_SCHEMATIC = Schematica.properties.get("schematica.command.replace.noSchematic");
                public static final String SUCCESS = Schematica.properties.get("schematica.command.replace.success");
            }

            public static final String NAME = Schematica.properties.get("schematicaReplace");
        }
    }

    public static final class Messages {
        public static final String TOGGLE_PRINTER = Schematica.properties.get("schematica.message.togglePrinter");

        public static final String INVALID_BLOCK = Schematica.properties.get("schematica.message.invalidBlock");
        public static final String INVALID_PROPERTY = Schematica.properties.get("schematica.message.invalidProperty");
        public static final String INVALID_PROPERTY_FOR_BLOCK = Schematica.properties.get("schematica.message.invalidPropertyForBlock");
    }

    public static final class Gui {
        public static final class Load {
            public static final String TITLE = Schematica.properties.get("schematica.gui.title");
            public static final String FOLDER_INFO = Schematica.properties.get("schematica.gui.folderInfo");
            public static final String OPEN_FOLDER = Schematica.properties.get("schematica.gui.openFolder");
            public static final String NO_SCHEMATIC = Schematica.properties.get("schematica.gui.noschematic");
        }

        public static final class Save {
            public static final String POINT_RED = Schematica.properties.get("schematica.gui.point.red");
            public static final String POINT_BLUE = Schematica.properties.get("schematica.gui.point.blue");
            public static final String SAVE = Schematica.properties.get("schematica.gui.save");
            public static final String SAVE_SELECTION = Schematica.properties.get("schematica.gui.saveselection");
            public static final String FORMAT = Schematica.properties.get("schematica.gui.format");
        }

        public static final class Control {
            public static final String MOVE_SCHEMATIC = Schematica.properties.get("schematica.gui.moveschematic");
            public static final String MATERIALS = Schematica.properties.get("schematica.gui.materials");
            public static final String PRINTER = Schematica.properties.get("schematica.gui.printer");
            public static final String OPERATIONS = Schematica.properties.get("schematica.gui.operations");

            public static final String UNLOAD = Schematica.properties.get("schematica.gui.unload");
            public static final String MODE_ALL = Schematica.properties.get("schematica.gui.all");
            public static final String MODE_LAYERS = Schematica.properties.get("schematica.gui.layers");
            public static final String MODE_BELOW = Schematica.properties.get("schematica.gui.below");
            public static final String HIDE = Schematica.properties.get("schematica.gui.hide");
            public static final String SHOW = Schematica.properties.get("schematica.gui.show");
            public static final String MOVE_HERE = Schematica.properties.get("schematica.gui.movehere");
            public static final String FLIP = Schematica.properties.get("schematica.gui.flip");
            public static final String ROTATE = Schematica.properties.get("schematica.gui.rotate");
            public static final String TRANSFORM_PREFIX = Schematica.properties.get("schematica.gui.");

            public static final String MATERIAL_NAME = Schematica.properties.get("schematica.gui.materialname");
            public static final String MATERIAL_AMOUNT = Schematica.properties.get("schematica.gui.materialamount");
            public static final String MATERIAL_AVAILABLE = Schematica.properties.get("schematica.gui.materialavailable");
            public static final String MATERIAL_MISSING = Schematica.properties.get("schematica.gui.materialmissing");

            public static final String SORT_PREFIX = Schematica.properties.get("schematica.gui.material");
            public static final String DUMP = Schematica.properties.get("schematica.gui.materialdump");
        }

        public static final String X = Schematica.properties.get("schematica.gui.x");
        public static final String Y = Schematica.properties.get("schematica.gui.y");
        public static final String Z = Schematica.properties.get("schematica.gui.z");
        public static final String ON = Schematica.properties.get("schematica.gui.on");
        public static final String OFF = Schematica.properties.get("schematica.gui.off");
        public static final String DONE = Schematica.properties.get("schematica.gui.done");
    }

    public static final class ModId {
        public static final String MINECRAFT = "minecraft";
    }

    public static final class Keys {
        public static final String CATEGORY = Schematica.properties.get("schematica.key.category");
        public static final String LOAD = Schematica.properties.get("schematica.key.load");
        public static final String SAVE = Schematica.properties.get("schematica.key.save");
        public static final String CONTROL = Schematica.properties.get("schematica.key.control");
        public static final String LAYER_INC = Schematica.properties.get("schematica.key.layerInc");
        public static final String LAYER_DEC = Schematica.properties.get("schematica.key.layerDec");
        public static final String LAYER_TOGGLE = Schematica.properties.get("schematica.key.layerToggle");
        public static final String RENDER_TOGGLE = Schematica.properties.get("schematica.key.renderToggle");
        public static final String PRINTER_TOGGLE = Schematica.properties.get("schematica.key.printerToggle");
        public static final String MOVE_HERE = Schematica.properties.get("schematica.key.moveHere");
        public static final String PICK_BLOCK = Schematica.properties.get("schematica.key.pickBlock");
    }

    public static final class NBT {
        public static final String ROOT = "Schematic";

        public static final String MATERIALS = "Materials";
        public static final String FORMAT_CLASSIC = "Classic";
        public static final String FORMAT_ALPHA = "Alpha";
        public static final String FORMAT_STRUCTURE = "Structure";

        public static final String ICON = "Icon";
        public static final String BLOCKS = "Blocks";
        public static final String DATA = "Data";
        public static final String ADD_BLOCKS = "AddBlocks";
        public static final String ADD_BLOCKS_SCHEMATICA = "Add";
        public static final String WIDTH = "Width";
        public static final String LENGTH = "Length";
        public static final String HEIGHT = "Height";
        public static final String MAPPING_SCHEMATICA = "SchematicaMapping";
        public static final String TILE_ENTITIES = "TileEntities";
        public static final String ENTITIES = "Entities";
        public static final String EXTENDED_METADATA = "ExtendedMetadata";
    }

    public static final class Formats {
        public static final String CLASSIC = Schematica.properties.get("schematica.format.classic");
        public static final String ALPHA = Schematica.properties.get("schematica.format.alpha");
        public static final String STRUCTURE = Schematica.properties.get("schematica.format.structure");
        public static final String INVALID = Schematica.properties.get("schematica.format.invalid");
    }

    public static final class Extensions {
        public static final String SCHEMATIC = ".schematic";
        public static final String STRUCTURE = ".nbt";
    }
}
