package com.kisman.cc.features.module.combat;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.ModuleInfo;
import com.kisman.cc.features.module.combat.blocker.BlockerModule;
import com.kisman.cc.features.module.combat.blocker.modules.CrystalPushBlocker;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.util.world.BlockUtil2;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@ModuleInfo(
        name = "Blocker",
        category = Category.COMBAT,
        wip = true
)
public class Blocker extends Module {

    private final SettingGroup crystalPushBlocker = register(new SettingGroup(new Setting("CrystalPushBlocker", this)));

    private final List<BlockerModule> blockers;

    private Map<BlockerModule, Boolean> blockerStates = new ConcurrentHashMap<>();

    private void loadAllBlockers(){
        BlockerModule crystalPushBlocker = new CrystalPushBlocker(this.crystalPushBlocker);
        blockers.add(crystalPushBlocker);
    }

    public Blocker(){
        this.blockers = new Vector<>();
        BlockerModule.blockerSupplier = () -> this;
        this.loadAllBlockers();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.blockerStates.clear();
    }

    private List<BlockPos> getDynamicBlocks(){
        List<BlockPos> list = Arrays.asList(
                new BlockPos(mc.player.posX + 0.3, mc.player.posY, mc.player.posZ + 0.3),
                new BlockPos(mc.player.posX + 0.3, mc.player.posY, mc.player.posZ - 0.3),
                new BlockPos(mc.player.posX - 0.3, mc.player.posY, mc.player.posZ - 0.3),
                new BlockPos(mc.player.posX - 0.3, mc.player.posY, mc.player.posZ + 0.3)
        );
        final List<BlockPos> dynamicBlocks = new ArrayList<>(list);
        list.forEach(pos -> dynamicBlocks.addAll(Arrays.stream(EnumFacing.HORIZONTALS).map(pos::offset).collect(Collectors.toList())));
        dynamicBlocks.removeAll(list);
        return dynamicBlocks.stream()
                .distinct()
                .filter(pos -> mc.world.getBlockState(pos).getMaterial().isReplaceable())
                .filter(pos -> !BlockUtil2.sides(pos).isEmpty())
                .collect(Collectors.toList());
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
