package com.kisman.cc.util.render.cubic;

import com.kisman.cc.util.Colour;
import com.kisman.cc.util.Rendering;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

public class RenderBuilder {

    protected BoundingBox aabb;

    protected Color color1;

    protected Color color2;

    protected float lineWidth;

    protected Rendering.Mode mode;

    protected RenderBuilder(){
        this.aabb = null;
        this.color1 = Rendering.DUMMY_COLOR.getColor();
        this.color2 = Rendering.DUMMY_COLOR.getColor();
        this.lineWidth = 1.0f;
        this.mode = null;
    }


    public static RenderBuilder build(){
        return new RenderBuilder();
    }


    public RenderBuilder pos(BoundingBox boundingBox){
        aabb = boundingBox;
        return this;
    }

    public RenderBuilder pos(AxisAlignedBB aabb){
        this.aabb = new BoundingBox(aabb);
        return this;
    }

    public RenderBuilder pos(BlockPos pos){
        aabb = new BoundingBox(pos);
        return this;
    }


    public RenderBuilder color(Color color){
        color1 = color;
        return this;
    }

    public RenderBuilder color(Colour colour){
        color1 = colour.getColor();
        return this;
    }

    public RenderBuilder color(Color color1, Color color2){
        this.color1 = color1;
        this.color2 = color2;
        return this;
    }

    public RenderBuilder color(Colour colour1, Colour colour2){
        this.color1 = colour1.getColor();
        this.color2 = colour2.getColor();
        return this;
    }


    public RenderBuilder lineWidth(float lineWidth){
        this.lineWidth = lineWidth;
        return this;
    }

    public RenderBuilder mode(Rendering.Mode mode){
        this.mode = mode;
        return this;
    }

    public void render(){
        if(aabb == null || mode == null)
            return;

        AxisAlignedBB a = Rendering.correct(aabb.toAABB());
        Rendering.draw(a, lineWidth, new Colour(color1), new Colour(color2), mode);
    }
}
