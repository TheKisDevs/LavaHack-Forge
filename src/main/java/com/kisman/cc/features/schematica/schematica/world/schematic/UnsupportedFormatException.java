package com.kisman.cc.features.schematica.schematica.world.schematic;

public class UnsupportedFormatException extends Exception {
    public UnsupportedFormatException(final String format) {
        super(String.format("Unsupported format: %s", format));
    }
}
