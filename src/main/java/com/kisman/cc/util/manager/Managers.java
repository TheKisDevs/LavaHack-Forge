package com.kisman.cc.util.manager;

import com.kisman.cc.Kisman;
import com.kisman.cc.util.render.PulseManager;

public class Managers {
    public static Managers instance;

    public FPSManager fpsManager;
    public PulseManager pulseManager;

    public Managers() {
        instance = this;
    }

    public String getRainbowCommandMessage() {
        StringBuilder stringBuilder = new StringBuilder("[" + Kisman.NAME + "]");
        stringBuilder.insert(0, "\u00a7+");
        stringBuilder.append("\u00a7r");
        return stringBuilder.toString();
    }

    public void init() {
        fpsManager = new FPSManager();
        pulseManager = new PulseManager();
    }
}
