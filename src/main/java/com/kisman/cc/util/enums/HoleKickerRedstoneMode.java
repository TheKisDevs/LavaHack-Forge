package com.kisman.cc.util.enums;

import net.minecraft.init.Blocks;

//i get null pointer when im using it :skull:
public enum HoleKickerRedstoneMode {
    Torch {
        @Override
        public HoleKickerRedstoneMode opposite() {
            return Block;
        }

        @Override
        public net.minecraft.block.Block block() {
            return Blocks.REDSTONE_TORCH;
        }
    },
    Block {
        @Override
        public HoleKickerRedstoneMode opposite() {
            return Torch;
        }

        @Override
        public net.minecraft.block.Block block() {
            return Blocks.REDSTONE_BLOCK;
        }
    };

    public HoleKickerRedstoneMode opposite() {return null;};
    public net.minecraft.block.Block block() {return null;};
}