package com.kisman.cc.features.schematica.core;

import com.kisman.cc.features.schematica.core.reference.Reference;
import org.apache.logging.log4j.LogManager;

public class LunatriusCore {
    public static LunatriusCore instance = new LunatriusCore();

    public void init() {
        Reference.logger = LogManager.getLogger("LavaHack Lunatrius Core");
    }
}
