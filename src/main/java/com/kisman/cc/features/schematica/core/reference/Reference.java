package com.kisman.cc.features.schematica.core.reference;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Reference {
    public static final String MODID = "lunatriuscore";
    public static final String NAME = "LunatriusCore";
    public static final String VERSION = "${version_lunatriuscore}";
    public static final String FORGE = "${forgeVersion}";
    public static final String MINECRAFT = "${version_minecraft}";
    public static final String PROXY_SERVER = "com.kisman.cc.features.schematica.core.proxy.ServerProxy";
    public static final String PROXY_CLIENT = "com.kisman.cc.features.schematica.core.proxy.ClientProxy";

    public static Logger logger = LogManager.getLogger(Reference.MODID);
}
