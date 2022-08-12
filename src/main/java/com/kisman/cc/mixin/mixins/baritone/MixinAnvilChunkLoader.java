package com.kisman.cc.mixin.mixins.baritone;

import baritone.utils.accessor.IAnvilChunkLoader;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;

/**
 * @author Brady
 * @since 9/4/2018
 */
@Mixin(AnvilChunkLoader.class)
public class MixinAnvilChunkLoader implements IAnvilChunkLoader {
    @Shadow @Final private File chunkSaveLocation;
    @Override public File getChunkSaveLocation() {
        return this.chunkSaveLocation;
    }
}
