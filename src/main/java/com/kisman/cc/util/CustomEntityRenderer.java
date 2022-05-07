package com.kisman.cc.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Don't use
 */
public class CustomEntityRenderer<T extends Entity> extends Render<T> {

    private final Render<T> renderer;

    private final Function<T, ResourceLocation> entityTextureFunc;

    private boolean useDefault = true;

    private double x, y, z;

    private float entityYaw, partialTicks;

    public CustomEntityRenderer(T... ts){
        super(Minecraft.getMinecraft().getRenderManager());
        this.renderer = (Render<T>) Minecraft.getMinecraft().getRenderManager().entityRenderMap.get(ts.getClass().getComponentType());
        this.entityTextureFunc = entity -> (ResourceLocation) ReflectUtil.invoke(Render.class, renderer, "getEntityTexture", new Class[]{ts.getClass().getComponentType()}, entity);
    }

    public void update(double x, double y, double z, float entityYaw, float partialTicks){
        this.x = x;
        this.y = y;
        this.z = z;
        this.entityYaw = entityYaw;
        this.partialTicks = partialTicks;
        useDefault = false;
    }

    public void useDefault(){
        useDefault = true;
    }

    @Override
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks){
        if(useDefault){
            renderer.doRender(entity, x, y, z, entityYaw, partialTicks);
            return;
        }

        renderer.doRender(entity, this.x, this.y, this.z, this.entityYaw, this.partialTicks);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(T t) {
        return entityTextureFunc.apply(t);
    }
}
