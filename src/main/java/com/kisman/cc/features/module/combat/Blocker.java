package com.kisman.cc.features.module.combat;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.WorkInProgress;
import com.kisman.cc.features.module.combat.blocker.BlockerModule;
import com.kisman.cc.features.module.combat.blocker.CrystalPushBlocker;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingGroup;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

@WorkInProgress
public class Blocker extends Module {

    private final SettingGroup crystalPushBlocker = register(new SettingGroup(new Setting("CrystalPushBlocker", this)));

    private final List<BlockerModule> blockers;

    private Map<BlockerModule, Boolean> blockerStates = new ConcurrentHashMap<>();

    private void loadAllBlockers(){
        BlockerModule crystalPushBlocker = new CrystalPushBlocker(this.crystalPushBlocker);
        blockers.add(crystalPushBlocker);
    }

    public Blocker(){
        super("Blocker", Category.COMBAT);
        this.blockers = new Vector<>();
        BlockerModule.blockerSupplier = () -> this;
        this.loadAllBlockers();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.blockerStates.clear();
    }

    @Override
    public void update() {
        Map<BlockerModule, Boolean> map = new ConcurrentHashMap<>();
        for(BlockerModule blocker : blockers){
            Boolean blockerState = blockerStates.get(blocker);
            if(blockerState != null){
                if(blockerState != blocker.isEnabled()){
                    if(blocker.isEnabled())
                        blocker.onEnable();
                    else
                        blocker.onDisable();
                }
            } else {
                if(blocker.isEnabled())
                    blocker.onEnable();
            }
            map.put(blocker, blocker.isEnabled());
            if(blocker.isEnabled())
                blocker.update();
        }
        this.blockerStates = map;
    }
}
