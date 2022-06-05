package com.kisman.cc.util.render.cubic;

import com.kisman.cc.util.Colour;
import com.kisman.cc.util.render.Rendering;
import net.minecraft.util.math.AxisAlignedBB;

public class DelegateRenderBuilder extends RenderBuilder {

    private final DelegateRenderPattern renderer;

    public DelegateRenderBuilder(DelegateRenderPattern renderer){
        super();
        this.renderer = renderer;
    }

    @Override
    public void render(){
        renderer.render(aabb.toAABB(), this);
    }

    public void doRender(){
        if(aabb == null || mode == null)
            return;

        AxisAlignedBB a = Rendering.correct(aabb.toAABB());
        Rendering.draw(a, lineWidth, new Colour(color1), new Colour(color2), mode);
    }
}
