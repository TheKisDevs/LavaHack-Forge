package com.kisman.cc.util.render.cubic;

import com.sun.javafx.geom.Vec2d;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Deprecated
public class BufBuilder {

    public static int POS_XYZ = 0;
    public static int POS_VEC = 1;
    public static int POS_2D_XYZ = 2;
    public static int POS_2D_VEC = 3;

    private static final Invokable[] operations;

    private static final WorldVertexBufferUploader vertexUploader;

    private final BufferBuilder bufferBuilder;

    private final Map<Integer, Object[]> ops;

    private double x;
    private double z;
    private double y;

    private Color color;

    private BufBuilder(BufferBuilder bufferBuilder){
        Tessellator.getInstance().draw();
        this.bufferBuilder = bufferBuilder;
        this.ops = new ConcurrentHashMap<>(16);
    }

    public static BufBuilder build(BufferBuilder bufferBuilder){
        return new BufBuilder(bufferBuilder);
    }

    public static BufBuilder build(){
        return new BufBuilder(Tessellator.getInstance().getBuffer());
    }

    public BufBuilder pos(double x, double y, double z){
        add(POS_XYZ, x, y, z);
        return this;
    }

    public BufBuilder pos(Vec3d vec){
        add(POS_VEC, vec);
        return this;
    }

    public BufBuilder pos2D(double x, double y){
        add(POS_2D_XYZ, x, y);
        return this;
    }

    public BufBuilder pos2D(Vec2d vec){
        add(POS_2D_VEC, vec);
        return this;
    }

    public void endVertex(){
        for(Map.Entry<Integer, Object[]> entry : ops.entrySet())
            operations[entry.getKey()].invoke(this, bufferBuilder, entry.getValue());
        ops.clear();
        x = 0;
        y = 0;
        z = 0;
        color = null;
    }

    public void draw(){
        bufferBuilder.finishDrawing();
        vertexUploader.draw(bufferBuilder);
    }

    private void add(int op, Object... args){
        ops.put(op, args);
    }

    private interface Invokable {

        void invoke(BufBuilder instance, BufferBuilder bufferBuilder, Object... args);
    }

    static {
        try {
            Field f = Tessellator.class.getDeclaredField("vboUploader");
            f.setAccessible(true);
            vertexUploader = (WorldVertexBufferUploader) f.get(Tessellator.getInstance());
        } catch (Exception ignored){
            throw new IllegalStateException();
        }
        operations = new Invokable[32];
        operations[0] = (instance, buf, args) -> buf.pos((Double) args[0], (Double) args[1], (Double) args[2]);
        operations[1] = (instance, buf, args) -> {
            Vec3d vec = (Vec3d) args[0];
            buf.pos(vec.x, vec.y, vec.z);
        };
        operations[2] = (instance, buf, args) -> buf.pos((Double) args[0], (Double) args[1], 0.0);
        operations[3] = (instance, buf, args) -> {
            Vec2d vec = (Vec2d) args[0];
            buf.pos(vec.x, vec.y, 0.0);
        };
    }
}
