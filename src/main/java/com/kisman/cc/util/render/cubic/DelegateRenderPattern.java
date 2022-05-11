package com.kisman.cc.util.render.cubic;

import net.minecraft.util.math.AxisAlignedBB;

public abstract class DelegateRenderPattern extends RenderPattern {

    public DelegateRenderPattern(){
        super();
    }

    public abstract void render(AxisAlignedBB aabb, DelegateRenderBuilder delegate);
}
