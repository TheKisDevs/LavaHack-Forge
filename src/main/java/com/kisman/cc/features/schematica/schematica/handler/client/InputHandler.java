package com.kisman.cc.features.schematica.schematica.handler.client;

import com.kisman.cc.features.schematica.schematica.client.gui.control.GuiSchematicControl;
import com.kisman.cc.features.schematica.schematica.client.gui.load.GuiSchematicLoad;
import com.kisman.cc.features.schematica.schematica.client.gui.save.GuiSchematicSave;
import com.kisman.cc.features.schematica.schematica.client.renderer.RenderSchematic;
import com.kisman.cc.features.schematica.schematica.client.world.SchematicWorld;
import com.kisman.cc.features.schematica.schematica.client.world.SchematicWorld.LayerMode;
import com.kisman.cc.features.schematica.schematica.proxy.ClientProxy;
import com.kisman.cc.features.schematica.schematica.reference.Names;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class InputHandler {
    public static final InputHandler INSTANCE = new InputHandler();

    private static final KeyBinding KEY_BINDING_LOAD = new KeyBinding(Names.Keys.LOAD, Keyboard.KEY_DIVIDE, Names.Keys.CATEGORY);
    private static final KeyBinding KEY_BINDING_SAVE = new KeyBinding(Names.Keys.SAVE, Keyboard.KEY_MULTIPLY, Names.Keys.CATEGORY);
    private static final KeyBinding KEY_BINDING_CONTROL = new KeyBinding(Names.Keys.CONTROL, Keyboard.KEY_SUBTRACT, Names.Keys.CATEGORY);
    private static final KeyBinding KEY_BINDING_LAYER_INC = new KeyBinding(Names.Keys.LAYER_INC, Keyboard.KEY_NONE, Names.Keys.CATEGORY);
    private static final KeyBinding KEY_BINDING_LAYER_DEC = new KeyBinding(Names.Keys.LAYER_DEC, Keyboard.KEY_NONE, Names.Keys.CATEGORY);
    private static final KeyBinding KEY_BINDING_LAYER_TOGGLE = new KeyBinding(Names.Keys.LAYER_TOGGLE, Keyboard.KEY_NONE, Names.Keys.CATEGORY);
    private static final KeyBinding KEY_BINDING_RENDER_TOGGLE = new KeyBinding(Names.Keys.RENDER_TOGGLE, Keyboard.KEY_NONE, Names.Keys.CATEGORY);
   // private static final KeyBinding KEY_BINDING_PRINTER_TOGGLE = new KeyBinding(Names.Keys.PRINTER_TOGGLE, Keyboard.KEY_NONE, Names.Keys.CATEGORY);
    private static final KeyBinding KEY_BINDING_MOVE_HERE = new KeyBinding(Names.Keys.MOVE_HERE, Keyboard.KEY_NONE, Names.Keys.CATEGORY);
    private static final KeyBinding KEY_BINDING_PICK_BLOCK = new KeyBinding(Names.Keys.PICK_BLOCK, -98, Names.Keys.CATEGORY);

    public static final KeyBinding[] KEY_BINDINGS = new KeyBinding[] {
            KEY_BINDING_LOAD,
            KEY_BINDING_SAVE,
            KEY_BINDING_CONTROL,
            KEY_BINDING_LAYER_INC,
            KEY_BINDING_LAYER_DEC,
            KEY_BINDING_LAYER_TOGGLE,
            KEY_BINDING_RENDER_TOGGLE,
        //    KEY_BINDING_PRINTER_TOGGLE,
            KEY_BINDING_MOVE_HERE,
            KEY_BINDING_PICK_BLOCK
    };

    private final Minecraft minecraft = Minecraft.getMinecraft();

    private InputHandler() {}

    @SubscribeEvent
    public void onKeyInput(InputEvent event) {
        if (minecraft.currentScreen == null) {
            if (KEY_BINDING_LOAD.isPressed()) {
                minecraft.displayGuiScreen(new GuiSchematicLoad(this.minecraft.currentScreen));
            }

            if (KEY_BINDING_SAVE.isPressed()) {
                minecraft.displayGuiScreen(new GuiSchematicSave(this.minecraft.currentScreen));
            }

            if (KEY_BINDING_CONTROL.isPressed()) {
                minecraft.displayGuiScreen(new GuiSchematicControl(this.minecraft.currentScreen));
            }

            if (KEY_BINDING_LAYER_INC.isPressed()) {
                final SchematicWorld schematic = ClientProxy.schematic;
                if (schematic != null && schematic.layerMode != LayerMode.ALL) {
                    schematic.renderingLayer = MathHelper.clamp(schematic.renderingLayer + 1, 0, schematic.getHeight() - 1);
                    RenderSchematic.INSTANCE.refresh();
                }
            }

            if (KEY_BINDING_LAYER_DEC.isPressed()) {
                final SchematicWorld schematic = ClientProxy.schematic;
                if (schematic != null && schematic.layerMode != LayerMode.ALL) {
                    schematic.renderingLayer = MathHelper.clamp(schematic.renderingLayer - 1, 0, schematic.getHeight() - 1);
                    RenderSchematic.INSTANCE.refresh();
                }
            }

            if (KEY_BINDING_LAYER_TOGGLE.isPressed()) {
                final SchematicWorld schematic = ClientProxy.schematic;
                if (schematic != null) {
                    schematic.layerMode = LayerMode.next(schematic.layerMode);
                    RenderSchematic.INSTANCE.refresh();
                }
            }

            if (KEY_BINDING_RENDER_TOGGLE.isPressed()) {
                final SchematicWorld schematic = ClientProxy.schematic;
                if (schematic != null) {
                    schematic.isRendering = !schematic.isRendering;
                    RenderSchematic.INSTANCE.refresh();
                }
            }

            /*if (KEY_BINDING_PRINTER_TOGGLE.isPressed()) {
                if (ClientProxy.schematic != null) {
                    final boolean printing = SchematicPrinter.INSTANCE.togglePrinting();
                    this.minecraft.player.sendMessage(new TextComponentTranslation(Names.Messages.TOGGLE_PRINTER, I18n.format(printing ? Names.Gui.ON : Names.Gui.OFF)));
                }
            }*/

            if (KEY_BINDING_MOVE_HERE.isPressed()) {
                final SchematicWorld schematic = ClientProxy.schematic;
                if (schematic != null) {
                    ClientProxy.moveSchematicToPlayer(schematic);
                    RenderSchematic.INSTANCE.refresh();
                }
            }

            if (KEY_BINDING_PICK_BLOCK.isPressed()) {
                SchematicWorld schematic = ClientProxy.schematic;
                if (schematic != null && schematic.isRendering) {
                    pickBlock(schematic, ClientProxy.objectMouseOver);
                }
            }
        }
    }

    private boolean pickBlock(SchematicWorld schematic, RayTraceResult objectMouseOver) {
        // Minecraft.func_147112_ai
        if (objectMouseOver == null) {
            return false;
        }

        if (objectMouseOver.typeOfHit == RayTraceResult.Type.MISS) {
            return false;
        }

        EntityPlayerSP player = this.minecraft.player;
        if (!ForgeHooks.onPickBlock(objectMouseOver, player, schematic)) {
            return true;
        }

        if (player.capabilities.isCreativeMode) {
            int slot = player.inventoryContainer.inventorySlots.size() - 10 + player.inventory.currentItem;
            minecraft.playerController.sendSlotPacket(player.inventory.getStackInSlot(player.inventory.currentItem), slot);
            return true;
        }

        return false;
    }
}
