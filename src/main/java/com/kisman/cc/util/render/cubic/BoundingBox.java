package com.kisman.cc.util.render.cubic;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class BoundingBox {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public double minX;

    public double minY;

    public double minZ;

    public double maxX;

    public double maxY;

    public double maxZ;

    boolean corrected = false;

    public BoundingBox(AxisAlignedBB aabb){
        minX = aabb.minX;
        minY = aabb.minY;
        minZ = aabb.minZ;
        maxX = aabb.maxX;
        maxY = aabb.maxY;
        maxZ = aabb.maxZ;
    }

    public BoundingBox(BlockPos pos){
        this(new AxisAlignedBB(pos));
    }

    private BoundingBox(double x1, double y1, double z1, double x2, double y2, double z2){
        minX = x1;
        minY = y1;
        minZ = z1;
        maxX = x2;
        maxY = y2;
        maxZ = z2;
    }


    public BoundingBox grow(double amount){
        minX -= amount;
        minY -= amount;
        minZ -= amount;
        maxX += amount;
        maxY += amount;
        maxZ += amount;
        return this;
    }

    public BoundingBox grow(double x, double y, double z){
        minX -= x;
        minY -= y;
        minZ -= z;
        maxX += x;
        maxY += y;
        maxZ += z;
        return this;
    }

    public BoundingBox growX(double amount){
        minX -= amount;
        maxX += amount;
        return this;
    }

    public BoundingBox growY(double amount){
        minY -= amount;
        maxY += amount;
        return this;
    }

    public BoundingBox growZ(double amount){
        minZ -= amount;
        maxZ -= amount;
        return this;
    }

    public BoundingBox centerTo(double x, double y, double z){
        double difX = (maxX - minX) * 0.5;
        double difY = (maxY - minY) * 0.5;
        double difZ = (maxZ - minZ) * 0.5;
        minX = x - difX;
        minY = y - difY;
        minZ = z - difZ;
        maxX = x + difX;
        maxY = y + difY;
        maxZ = z + difZ;
        return this;
    }

    public BoundingBox centerTo(Vec3d vec3d){
        centerTo(vec3d.x, vec3d.y, vec3d.z);
        return this;
    }

    public BoundingBox centerTo(BoundingBox bb){
        centerTo(bb.getCenter());
        return this;
    }

    public BoundingBox centerTo(AxisAlignedBB aabb){
        centerTo(new BoundingBox(aabb).getCenter());
        return this;
    }

    public BoundingBox centerTo(BlockPos pos){
        centerTo(new BoundingBox(pos).getCenter());
        return this;
    }

    public Vec3d getCenter(){
        double difX = (maxX - minX) * 0.5;
        double difY = (maxY - minY) * 0.5;
        double difZ = (maxZ - minZ) * 0.5;
        return new Vec3d(minX + difX, minY + difY, minZ + difZ);
    }

    public BoundingBox offset(double x, double y, double z){
        minX += x;
        minY += y;
        minZ += z;
        maxX += x;
        maxY += y;
        maxZ += z;
        return this;
    }

    public BoundingBox offset(Vec3d vec3d){
        offset(vec3d.x, vec3d.y, vec3d.z);
        return this;
    }

    public BoundingBox scale(double x, double y, double z){
        Vec3d center = getCenter();
        double cx = center.x;
        double cy = center.y;
        double cz = center.z;
        minX = cx;
        minY = cy;
        minZ = cz;
        maxX = cx;
        maxY = cy;
        maxZ = cz;
        grow(x * 0.5, y * 0.5, z * 0.5);
        return this;
    }

    public BoundingBox scaleNew(double x, double y, double z){
        Vec3d center = getCenter();
        double cx = center.x;
        double cy = center.y;
        double cz = center.z;
        BoundingBox boundingBox = new BoundingBox(cx, cy, cz, cx, cy, cz);
        boundingBox.grow(x * 0.5, y * 0.5, z * 0.5);
        return boundingBox;
    }

    public boolean intersects(BoundingBox boundingBox){
        return toAABB().intersects(boundingBox.toAABB());
    }

    public boolean intersects(AxisAlignedBB axisAlignedBB){
        return toAABB().intersects(axisAlignedBB);
    }

    public boolean contains(double x, double y, double z){
        return toAABB().contains(new Vec3d(x, y, z));
    }

    public boolean contains(Vec3d vec3d){
        return toAABB().contains(vec3d);
    }

    public boolean contains(BoundingBox bb){
        AxisAlignedBB aabb = toAABB();
        Vec3d vec1 = new Vec3d(bb.minX, bb.minY, bb.minZ);
        Vec3d vec2 = new Vec3d(bb.maxX, bb.maxY, bb.maxZ);
        return aabb.contains(vec1) && aabb.contains(vec2);
    }

    public boolean contains(AxisAlignedBB bb){
        AxisAlignedBB aabb = toAABB();
        Vec3d vec1 = new Vec3d(bb.minX, bb.minY, bb.minZ);
        Vec3d vec2 = new Vec3d(bb.maxX, bb.maxY, bb.maxZ);
        return aabb.contains(vec1) && aabb.contains(vec2);
    }

    public BoundingBox correct(){
        minX = minX - mc.renderManager.viewerPosX;
        minY = minY - mc.renderManager.viewerPosY;
        minZ = minZ - mc.renderManager.viewerPosZ;
        maxX = maxX - mc.renderManager.viewerPosX;
        maxY = maxY - mc.renderManager.viewerPosY;
        maxZ = maxZ - mc.renderManager.viewerPosZ;
        corrected = true;
        return this;
    }

    public AxisAlignedBB toAABB(){
        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public boolean equals(Object other){
        if(other == null)
            return false;
        if(!(other instanceof BoundingBox))
            return false;
        BoundingBox boundingBox = (BoundingBox) other;
        boolean b1 = minX == boundingBox.minX;
        boolean b2 = minY == boundingBox.minY;
        boolean b3 = minZ == boundingBox.minZ;
        boolean b4 = maxX == boundingBox.maxX;
        boolean b5 = maxY == boundingBox.maxY;
        boolean b6 = maxZ == boundingBox.maxZ;
        return b1 && b2 && b3 && b4 && b5 && b6;
    }
}
