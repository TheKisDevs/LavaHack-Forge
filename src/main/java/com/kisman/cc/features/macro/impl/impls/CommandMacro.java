package com.kisman.cc.features.macro.impl.impls;

import com.kisman.cc.features.macro.Macro;
import com.kisman.cc.features.macro.activator.Activator;
import com.kisman.cc.features.macro.impl.MacroImpl;

import java.util.List;

public class CommandMacro extends Macro {

    public CommandMacro(MacroImpl macro, List<Activator> activators) {
        super("cmd", macro, activators);
    }
}
