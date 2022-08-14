package com.kisman.cc.features.module.misc

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.schematica.schematica.client.printer.SchematicPrinter

/**
 * @author _kisman_
 * @since 23:10 of 08.07.2022
 */
class Printer : Module(
    "Printer",
    "Integration of Schematica's printer",
    Category.MISC
) {
    init {
        super.setDisplayInfo { if(SchematicPrinter.INSTANCE.IsStationary()) "Stationary" else "Printing" }
    }

    override fun onEnable() {
        super.onEnable()
        SchematicPrinter.INSTANCE.isPrinting = true
    }

    override fun onDisable() {
        super.onDisable()
        SchematicPrinter.INSTANCE.isPrinting = false
    }
}