package com.kisman.cc.features.schematica.schematica.world.storage;

import com.kisman.cc.features.schematica.schematica.api.ISchematic;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Schematic implements ISchematic {
    private static final ItemStack DEFAULT_ICON = new ItemStack(Blocks.GRASS);

    private ItemStack icon;
    private final short[][][] blocks;
    private final byte[][][] metadata;
    private final List<TileEntity> tileEntities = new ArrayList<TileEntity>();
    private final List<Entity> entities = new ArrayList<Entity>();
    private final int width;
    private final int height;
    private final int length;
    private String author;

    public Schematic(ItemStack icon, int width, int height, int length) {
        this(icon, width, height, length, "");
    }

    public Schematic(ItemStack icon, int width, int height, int length, @Nonnull String author) {
        if (author == null) {
            throw new IllegalArgumentException("Author cannot be null");
        }

        this.icon = icon;
        this.blocks = new short[width][height][length];
        this.metadata = new byte[width][height][length];

        this.width = width;
        this.height = height;
        this.length = length;

        this.author = author;
    }

    @Override
    public IBlockState getBlockState(BlockPos pos) {
        if (!isValid(pos)) {
            return Blocks.AIR.getDefaultState();
        }

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        Block block = Block.REGISTRY.getObjectById(this.blocks[x][y][z]);

        return block.getStateFromMeta(this.metadata[x][y][z]);
    }

    @Override
    public boolean setBlockState(BlockPos pos, IBlockState blockState) {
        if (!isValid(pos)) {
            return false;
        }

        Block block = blockState.getBlock();
        int id = Block.REGISTRY.getIDForObject(block);
        if (id == -1) {
            return false;
        }

        int meta = block.getMetaFromState(blockState);
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        blocks[x][y][z] = (short) id;
        metadata[x][y][z] = (byte) meta;
        return true;
    }

    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        for (TileEntity tileEntity : tileEntities) {
            if (tileEntity.getPos().equals(pos)) {
                return tileEntity;
            }
        }

        return null;
    }

    @Override
    public List<TileEntity> getTileEntities() {
        return tileEntities;
    }

    @Override
    public void setTileEntity(BlockPos pos, TileEntity tileEntity) {
        if (!isValid(pos)) {
            return;
        }

        removeTileEntity(pos);

        if (tileEntity != null) {
            this.tileEntities.add(tileEntity);
        }
    }

    @Override
    public void removeTileEntity(BlockPos pos) {
        Iterator<TileEntity> iterator = tileEntities.iterator();

        while (iterator.hasNext()) {
            TileEntity tileEntity = iterator.next();
            if (tileEntity.getPos().equals(pos)) {
                iterator.remove();
            }
        }
    }

    @Override
    public List<Entity> getEntities() {
        return entities;
    }

    @Override
    public void addEntity(Entity entity) {
        if (entity == null || entity.getUniqueID() == null || entity instanceof EntityPlayer) {
            return;
        }

        for (Entity e : entities) {
            if (entity.getUniqueID().equals(e.getUniqueID())) {
                return;
            }
        }

        entities.add(entity);
    }

    @Override
    public void removeEntity(Entity entity) {
        if (entity == null || entity.getUniqueID() == null) {
            return;
        }

        Iterator<Entity> iterator = entities.iterator();
        while (iterator.hasNext()) {
            Entity e = iterator.next();
            if (entity.getUniqueID().equals(e.getUniqueID())) {
                iterator.remove();
            }
        }
    }

    @Override
    public ItemStack getIcon() {
        return icon;
    }

    @Override
    public void setIcon(ItemStack icon) {
        if (icon != null) {
            this.icon = icon;
        } else {
            this.icon = DEFAULT_ICON.copy();
        }
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public int getHeight() {
        return height;
    }

    private boolean isValid(BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        return !(x < 0 || y < 0 || z < 0 || x >= this.width || y >= this.height || z >= this.length);
    }

    @Override
    @Nonnull
    public String getAuthor() {
        return author;
    }

    @Override
    public void setAuthor(@Nonnull String author) {
        if (author == null) {
            throw new IllegalArgumentException("Author cannot be null");
        }
        this.author = author;
    }
}
