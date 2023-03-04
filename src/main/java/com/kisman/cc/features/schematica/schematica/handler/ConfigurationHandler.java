package com.kisman.cc.features.schematica.schematica.handler;

import com.kisman.cc.features.schematica.schematica.Schematica;
import net.minecraft.block.Block;

import java.io.File;
import java.util.*;

public class ConfigurationHandler {
    public static final boolean DUMP_BLOCK_LIST_DEFAULT = false;
    public static final boolean SHOW_DEBUG_INFO_DEFAULT = true;
    public static final boolean ENABLE_ALPHA_DEFAULT = false;
    public static final double ALPHA_DEFAULT = 1.0;
    public static final boolean HIGHLIGHT_DEFAULT = true;
    public static final boolean HIGHLIGHT_AIR_DEFAULT = true;
    public static final double BLOCK_DELTA_DEFAULT = 0.005;
    public static final int RENDER_DISTANCE_DEFAULT = 8;
    public static final int PLACE_DELAY_DEFAULT = 1;
    public static final int TIMEOUT_DEFAULT = 10;
    public static final int PLACE_DISTANCE_DEFAULT = 5;
    public static final boolean PLACE_INSTANTLY_DEFAULT = false;
    public static final boolean DESTROY_BLOCKS_DEFAULT = false;
    public static final boolean DESTROY_INSTANTLY_DEFAULT = false;
    public static final boolean PLACE_ADJACENT_DEFAULT = true;
    public static final boolean[] SWAP_SLOTS_DEFAULT = new boolean[] {
            false, false, false, false, false, true, true, true, true
    };
    public static final String SORT_TYPE_DEFAULT = "";
    public static final boolean PRINTER_ENABLED_DEFAULT = true;
    public static final boolean SAVE_ENABLED_DEFAULT = true;
    public static final boolean LOAD_ENABLED_DEFAULT = true;

    public static boolean dumpBlockList = DUMP_BLOCK_LIST_DEFAULT;
    public static boolean showDebugInfo = SHOW_DEBUG_INFO_DEFAULT;
    public static boolean enableAlpha = ENABLE_ALPHA_DEFAULT;
    public static float alpha = (float) ALPHA_DEFAULT;
    public static boolean highlight = HIGHLIGHT_DEFAULT;
    public static boolean highlightAir = HIGHLIGHT_AIR_DEFAULT;
    public static double blockDelta = BLOCK_DELTA_DEFAULT;
    public static int renderDistance = RENDER_DISTANCE_DEFAULT;
    public static int placeDelay = PLACE_DELAY_DEFAULT;
    public static int timeout = TIMEOUT_DEFAULT;
    public static int placeDistance = PLACE_DISTANCE_DEFAULT;
    public static boolean placeInstantly = PLACE_INSTANTLY_DEFAULT;
    public static boolean destroyBlocks = DESTROY_BLOCKS_DEFAULT;
    public static boolean destroyInstantly = DESTROY_INSTANTLY_DEFAULT;
    public static boolean placeAdjacent = PLACE_ADJACENT_DEFAULT;
    public static boolean[] swapSlots = Arrays.copyOf(SWAP_SLOTS_DEFAULT, SWAP_SLOTS_DEFAULT.length);
    public static Queue<Integer> swapSlotsQueue = new ArrayDeque<>();
    public static File schematicDirectory = Schematica.SCHEMATICS_FOLDER;
    public static String sortType = SORT_TYPE_DEFAULT;
    public static boolean printerEnabled = PRINTER_ENABLED_DEFAULT;
    public static boolean saveEnabled = SAVE_ENABLED_DEFAULT;
    public static boolean loadEnabled = LOAD_ENABLED_DEFAULT;

    private static final Set<Block> extraAirBlockList = new HashSet<>();

    public static boolean isExtraAirBlock(Block block) {
        return extraAirBlockList.contains(block);
    }
}