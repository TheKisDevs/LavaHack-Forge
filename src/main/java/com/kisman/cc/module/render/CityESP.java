package com.kisman.cc.module.render;

import com.kisman.cc.module.*;
import com.kisman.cc.module.combat.Surround;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.RenderUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.*;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

public class CityESP extends Module {
    private Setting range = new Setting("Range", this, 50, 0, 100, true);
    private Setting safe = new Setting("Bebrock", this, false);
    private Setting unSafe = new Setting("Obby", this, true);
    private Setting onlyIfCanPlaceCrystal = new Setting("OnlyIfCanPlaceCrystal", this, true);
    private Setting ignoreOwn = new Setting("IgnoreOwn", this, false);

    private ArrayList<Hole> holeBlocks = new ArrayList<>();

    public CityESP() {
        super("CityESP", "CityESP", Category.RENDER);

        setmgr.rSetting(range);
        setmgr.rSetting(safe);
        setmgr.rSetting(unSafe);
        setmgr.rSetting(onlyIfCanPlaceCrystal);
        setmgr.rSetting(ignoreOwn);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        for(EntityPlayer player : mc.world.playerEntities) {
            if(mc.player == player && ignoreOwn.getValBoolean()) continue;
            getBlock(player);
        }

        if(holeBlocks.isEmpty()) return;

        for(Hole hole : holeBlocks) {
            hole.render();
        }
    }

    private void getBlock(EntityPlayer player) {
        if(Surround.isInHole(player)) {
            boolean safe = true;

            if(Surround.isObsidianHole(player.getPosition()) && unSafe.getValBoolean()) safe = false;

            BlockPos holePos = new BlockPos(Math.round(player.posX), Math.round(player.posY), Math.round(player.posZ));
            holeBlocks.add(new Hole(Vectors.X_PLUS, holePos, safe));
            holeBlocks.add(new Hole(Vectors.X_MINUS, holePos, safe));
            holeBlocks.add(new Hole(Vectors.Z_PLUS, holePos, safe));
            holeBlocks.add(new Hole(Vectors.Z_MINUS, holePos, safe));
        }
    }

    public class Hole {
        public Vectors vec;
        public BlockPos parentHolePos;
        public boolean safe;

        public Hole(Vectors vec, BlockPos parentHolePos, boolean safe) {
            this.vec = vec;
            this.parentHolePos = parentHolePos;
            this.safe = safe;
        }

        public BlockPos getPos() {
            return parentHolePos.add(vec.vec);
        }

        public void render() {
            RenderUtil.drawBlockESP(getPos(), !safe ? 1 : 0, safe ? 1 : 0, 0);
        }
    }

    public enum Vectors {
        X_PLUS(new Vec3i(1, 0, 0)),
        X_MINUS(new Vec3i(-1, 0, 0)),
        Z_PLUS(new Vec3i(0, 0, 1)),
        Z_MINUS(new Vec3i(0, 0, -1));

        public final Vec3i vec;

        Vectors(Vec3i vec) {
            this.vec = vec;
        }
    }
}
