package com.kisman.cc.features.module.misc;


import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.util.world.BlockUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class SelectionPlacer extends Module {

    public SelectionPlacer(){
        super("SelectionPlacer", Category.MISC);
    }

    private Pair<Vec3i> pair = new Pair<>();

    @Override
    public void update(){
        if(mc.player == null || mc.world == null)
            return;
        if(!pair.isComplete())
            checkKeys();
        if(!pair.isComplete())
            return;
        Selection selection = new Selection(pair.first, pair.second);
        place(selection);
    }

    private void place(Selection selection){
        if(!selection.update())
            return;
        if(!(mc.player.inventory.getStackInSlot(mc.player.inventory.currentItem).getItem() instanceof ItemBlock))
            return;
        BlockUtil.placeBlock2(selection.curPos(), EnumHand.MAIN_HAND, false, false);
    }

    private void checkKeys(){
        if(!Mouse.isButtonDown(0))
            return;
        pair.setNext(mc.objectMouseOver.getBlockPos());
    }

    private static class Selection {

        int x1;
        int y1;
        int z1;
        int x2;
        int y2;
        int z2;

        int dx;
        int dy;
        int dz;

        int x;
        int y;
        int z;

        int amount;

        Vec3i[] vec;

        int ptr;

        private Selection(Vec3i begin, Vec3i end){
            Vec3i min = begin.compareTo(end) <= 0 ? begin : end;
            Vec3i max = begin.compareTo(end) <= 0 ? end : begin;
            x1 = min.getX();
            y1 = min.getY();
            z1 = min.getZ();
            x2 = max.getX();
            y2 = max.getY();
            z2 = max.getZ();
            dx = x2 - x1;
            dy = y2 - y1;
            dz = z2 - z1;
            x = x1;
            y = y1;
            z = z1;
            amount = dx * dy * dz;
            vec = new Vec3i[amount];
            ptr = 0;
            int i = 0;
            for(int y = y1; y <= y2; y++){
                for(int z = z1; z <= z2; z++){
                    for(int x = x1; x <= x2; x++){
                        vec[i] = new Vec3i(x, y, z);
                        i++;
                    }
                }
            }
        }

        private boolean update(){
            if(ptr >= amount - 1)
                return false;
            ptr++;
            return false;
        }

        private Vec3i curVec(){
            return vec[ptr];
        }

        private BlockPos curPos(){
            return new BlockPos(vec[ptr]);
        }
    }

    private static class Pair<E> {

        E first = null;

        E second = null;

        private Pair(){
        }

        private void setNext(E e){
            if(first == null){
                first = e;
                return;
            }
            if(second == null){
                if(e == first)
                    return;
                second = e;
            }
        }

        private boolean isComplete(){
            return first != null && second != null;
        }
    }
}
