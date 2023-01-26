package com.kisman.cc.features.schematica.schematica.handler.client;

import com.kisman.cc.features.schematica.schematica.client.printer.SchematicPrinter;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GuiHandler {
    public static final GuiHandler INSTANCE = new GuiHandler();

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (SchematicPrinter.INSTANCE.isPrinting()) {
            if (event.getGui() instanceof GuiEditSign) {
                event.setGui(null);
            }
        }
    }
}
