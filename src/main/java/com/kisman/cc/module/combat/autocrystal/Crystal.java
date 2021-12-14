package com.kisman.cc.module.combat.autocrystal;

import net.minecraft.util.math.BlockPos;

public class Crystal {
    public BlockPos pos;
    public double maxDMG;

    public Crystal(BlockPos pos, double maxDMG) {
        this.pos = pos;
        this.maxDMG = maxDMG;
    }
}
