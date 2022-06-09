package com.kisman.cc.util.enums.dynamic;

import net.minecraft.block.Block;
import org.cubic.dynamictask.AbstractTask;

public class BlockEnum {

    private static final AbstractTask.DelegateAbstractTask<Block> task = AbstractTask.types(Block.class);

    public enum Blocks {
        Obsidian(task.task(arg -> net.minecraft.init.Blocks.OBSIDIAN)),
        EnderChest(task.task(arg -> net.minecraft.init.Blocks.ENDER_CHEST));

        private final AbstractTask<Block> abstractTask;

        Blocks(AbstractTask<Block> task){
            this.abstractTask = task;
        }

        public AbstractTask<Block> getTask(){
            return abstractTask;
        }
    }
}
