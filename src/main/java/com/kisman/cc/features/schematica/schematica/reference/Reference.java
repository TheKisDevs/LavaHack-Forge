package com.kisman.cc.features.schematica.schematica.reference;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Reference {
    public static final String MODID = "schematica";
    public static final String NAME = "Schematica";
    public static final String VERSION = "${version_schematica}";
    public static final String FORGE = "${forgeVersion}";
    public static final String MINECRAFT = "${version_minecraft}";
    public static final String PROXY_SERVER = "com.kisman.cc.features.schematica.schematica.proxy.ServerProxy";
    public static final String PROXY_CLIENT = "com.kisman.cc.features.schematica.schematica.proxy.ClientProxy";
    public static final String GUI_FACTORY = "com.kisman.cc.features.schematica.schematica.client.gui.config.GuiFactory";

    public static Logger logger = LogManager.getLogger(Reference.MODID);
}
