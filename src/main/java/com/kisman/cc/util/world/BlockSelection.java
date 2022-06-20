package com.kisman.cc.util.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractQueue;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Queue;
import java.util.function.Consumer;

public class BlockSelection {

    private int x1;
    private int y1;
    private int z1;
    private int x2;
    private int y2;
    private int z2;

    private int dx;
    private int dy;
    private int dz;

    private int amountBlocks;

    private BlockPos[] blocks = null;

    public BlockSelection(Vec3i begin, Vec3i end, boolean loadBlocks){
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
        amountBlocks = dx * dy * dz;
        if(loadBlocks)
            loadBlocks();
    }

    public int getX1() {
        return x1;
    }

    public int getY1() {
        return y1;
    }

    public int getZ1() {
        return z1;
    }

    public int getX2() {
        return x2;
    }

    public int getY2() {
        return y2;
    }

    public int getZ2() {
        return z2;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public int getDz() {
        return dz;
    }

    public int getAmountBlocks() {
        return amountBlocks;
    }

    public BlockPos[] getBlocks() {
        return blocks;
    }

    public Queue<BlockPos> getBlocksAsQueue(){
        return new QueueImpl<>(blocks);
    }

    public void loadBlocks(){
        if(blocks != null) return;
        blocks = new BlockPos[amountBlocks];
        int i = 0;
        for(int x = x1; x <= x2; x++){
            for(int y = y1; y <= y2; y++){
                for(int z = z1; z <= z2; z++){
                    blocks[i] = new BlockPos(x, y, z);
                    i++;
                }
            }
        }
    }

    public void reloadBlocks(){
        blocks = new BlockPos[amountBlocks];
        int i = 0;
        for(int x = x1; x <= x2; x++){
            for(int y = y1; y <= y2; y++){
                for(int z = z1; z <= z2; z++){
                    blocks[i] = new BlockPos(x, y, z);
                    i++;
                }
            }
        }
    }

    private static class QueueImpl<E> extends AbstractQueue<E> implements Queue<E> {

        private Object[] value;

        private int size;

        private int modCount;

        private QueueImpl(E[] elements, int size){
            this.value = elements;
            this.size = size;
            this.modCount = 0;
        }

        private QueueImpl(E[] elements){
            this(elements, elements.length);
        }

        private void ensureCapacity(int n){
            if(size + n < value.length) return;
            int newCap = value.length << 1;
            Object[] newValue = new Object[newCap];
            System.arraycopy(value, 0, newValue, 0, size);
            this.value = newValue;
        }

        @NotNull
        @Override
        public Iterator<E> iterator() {
            return new Itr();
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public boolean offer(E e) {
            ensureCapacity(1);
            value[size++] = e;
            modCount++;
            return true;
        }

        @Override
        public E poll() {
            E e = (E) value[--size];
            value[size] = null;
            modCount++;
            return e;
        }

        @Override
        public E peek() {
            return (E) value[size - 1];
        }

        private class Itr implements Iterator<E> {

            private final int oldModCount;

            private int ptr;

            private Itr(){
                this.oldModCount = modCount;
                this.ptr = 0;
            }

            private void check(){
                if(oldModCount != modCount)
                    throw new ConcurrentModificationException();
            }

            @Override
            public boolean hasNext() {
                check();
                return ptr < size - 1;
            }

            @Override
            public E next() {
                check();
                return (E) value[ptr++];
            }

            @Override
            public void forEachRemaining(Consumer<? super E> action) {
                check();
                if(action == null)
                    throw new NullPointerException();
                while(ptr < size - 1)
                    action.accept((E) value[ptr++]);
            }
        }
    }
}
