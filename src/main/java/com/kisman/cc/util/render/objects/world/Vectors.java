package com.kisman.cc.util.render.objects.world;

import com.kisman.cc.util.Colour;
import com.kisman.cc.util.math.vectors.xyz.Vec3dColored;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

/**
 * @author _kisman_
 * @since 19:35 of 07.07.2022
 */
public class Vectors {
    public Vec3dColored[] vectors;

    public Vectors(Vec3dColored... vectors) {
        this.vectors = vectors;
    }

    public Vectors transform(ITransformer transformer, Vectors vectors2) {
        if(vectors.length != vectors2.vectors.length) {
            throw new IllegalArgumentException("Length of vec1 != length of vec2");
        }

        Vec3dColored[] vectors = new Vec3dColored[this.vectors.length];

        for(int i = 0; i < this.vectors.length; i++) {
            vectors[i] = transformer.transform(this.vectors[i], vectors2.vectors[i]);
        }

        this.vectors = vectors;

        return this;
    }

    public static Vectors byAABB(AxisAlignedBB bb, Colour color) {
        return new Vectors(
                new Vec3dColored(new Vec3d(bb.minX, bb.minY, bb.minZ), color),
                new Vec3dColored(new Vec3d(bb.minX, bb.minY, bb.maxZ), color),
                new Vec3dColored(new Vec3d(bb.maxX, bb.minY, bb.maxZ), color),
                new Vec3dColored(new Vec3d(bb.maxX, bb.minY, bb.minZ), color),
                new Vec3dColored(new Vec3d(bb.minX, bb.minY, bb.minZ), color),
                new Vec3dColored(new Vec3d(bb.minX, bb.maxY, bb.minZ), color),
                new Vec3dColored(new Vec3d(bb.minX, bb.maxY, bb.maxZ), color),
                new Vec3dColored(new Vec3d(bb.minX, bb.minY, bb.maxZ), color),
                new Vec3dColored(new Vec3d(bb.maxX, bb.minY, bb.maxZ), color),
                new Vec3dColored(new Vec3d(bb.maxX, bb.maxY, bb.maxZ), color),
                new Vec3dColored(new Vec3d(bb.minX, bb.maxY, bb.maxZ), color),
                new Vec3dColored(new Vec3d(bb.maxX, bb.maxY, bb.maxZ), color),
                new Vec3dColored(new Vec3d(bb.maxX, bb.maxY, bb.minZ), color),
                new Vec3dColored(new Vec3d(bb.maxX, bb.minY, bb.minZ), color),
                new Vec3dColored(new Vec3d(bb.maxX, bb.maxY, bb.minZ), color),
                new Vec3dColored(new Vec3d(bb.minX, bb.maxY, bb.minZ), color)
        );
    }

    public interface ITransformer {
        Vec3dColored transform(Vec3dColored vec1, Vec3dColored vec2);
    }
}
