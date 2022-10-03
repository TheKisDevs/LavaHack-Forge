package com.kisman.cc.features.module.Debug;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.ModuleInstance;
import com.kisman.cc.util.chat.cubic.ChatUtility;

/**
 * @author _kisman_
 * @since 18:01 of 03.10.2022
 */
public class ModuleInstancingJava extends Module {
    @ModuleInstance public static ModuleInstancingJava instance;

    public ModuleInstancingJava() {
        super("ModuleInstancingJava", "Tests @ModuleInstance annotation in java", Category.DEBUG);
    }

    @Override
    public void update() {
        if(mc.player == null || mc.world == null) return;

        ChatUtility.message().printClientModuleMessage("Instance of module " + getName() + " is " + (instance == null ? "NULL" : "NOT NULL"));
    }
}
