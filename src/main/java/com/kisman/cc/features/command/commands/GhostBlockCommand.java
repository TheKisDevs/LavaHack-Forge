package com.kisman.cc.features.command.commands;

import com.kisman.cc.features.command.Command;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class GhostBlockCommand extends Command {

    public GhostBlockCommand(){
        super("gb");
    }

    @Override
    public String getDescription() {
        return "DON'T USE THIS PLEASE";
    }

    @Override
    public String getSyntax() {
        return "kek i said don't use it";
    }

    @Override
    public void runCommand(@NotNull String s, @NotNull String[] args) {
        if(args.length < 1){
            error(getSyntax());
            return;
        }
        Block block = Block.REGISTRY.getObject(new ResourceLocation(args[0]));
        if(block == null){
            error(getSyntax());
            return;
        }
        mc.world.setBlockState(mc.objectMouseOver.getBlockPos().offset(mc.objectMouseOver.sideHit), block.getDefaultState());
    }
}
