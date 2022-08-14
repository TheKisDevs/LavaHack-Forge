package com.kisman.cc.mixin.mixins.accessor;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.template.Template;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

/**
 * @author _kisman_
 * @since 13:22 of 09.07.2022
 */
@Mixin(Template.class)
public interface ITemplate {
    @Accessor("blocks") List<Template.BlockInfo> blocks();
    @Accessor("size") void size(BlockPos pos);
    @Accessor("entities") List<Template.EntityInfo> entities();
}
