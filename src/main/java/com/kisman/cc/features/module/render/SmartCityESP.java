package com.kisman.cc.features.module.render;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.ModuleInfo;
import com.kisman.cc.features.subsystem.subsystems.EnemyManagerKt;
import com.kisman.cc.features.subsystem.subsystems.Target;
import com.kisman.cc.features.subsystem.subsystems.Targetable;
import com.kisman.cc.features.subsystem.subsystems.TargetsNearest;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.util.RenderingRewritePattern;
import com.kisman.cc.util.entity.EntityUtil;
import com.kisman.cc.util.world.CustomBlockState;
import com.kisman.cc.util.world.CustomWorld;
import com.kisman.cc.util.world.WorldUtilKt;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.cubic.dynamictask.AbstractTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Say good bye to your fps
 * @author Cubic
 * TODO: Make this work
 * TODO: Massively improve performance
 */
@ModuleInfo(
        name = "SmartCityESP(FpsKiller)",
        category = Category.RENDER,
        wip = true
)
@Targetable
@TargetsNearest
public class SmartCityESP extends Module {

    private final Setting ignoreIfNotSurrounded = register(new Setting("IgnoreIfNotSurrounded", this, false));
    private final RenderingRewritePattern renderer = new RenderingRewritePattern(this).preInit().init();

    public SmartCityESP(){
        super.setDisplayInfo(() -> "[" + (target == null ? "no target no fun" : target.getName()) + "]");
    }

    @Target
    public EntityPlayer target = null;

    private BlockPos renderPos = null;

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event){
        if(mc.player == null || mc.world == null)
            return;

        if(renderPos == null)
            return;

        renderer.draw(renderPos);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        MinecraftForge.EVENT_BUS.unregister(this);
        this.target = null;
        this.renderPos = null;
    }

    @Override
    public void update() {
        if(mc.player == null || mc.world == null){
            this.target = null;
            this.renderPos = null;
            return;
        }

        this.target = EnemyManagerKt.nearest();
        if(this.target == null){
            this.renderPos = null;
            return;
        }

        List<BlockPos> surroundingBlocks = getSurroundingBlocks();

        if(ignoreIfNotSurrounded.getValBoolean() && surroundingBlocks.stream().map(pos -> mc.world.getBlockState(pos).getMaterial()).anyMatch(Material::isReplaceable)){
            this.target = null;
            this.renderPos = null;
            return;
        }

        Map<Double, BlockPos> map = new HashMap<>();
        for(BlockPos pos : surroundingBlocks)
            map.put(calcDamage(this.target, pos), pos);
        double bestDamage = map.keySet().stream().max(Double::compare).orElse(0.0);
        this.renderPos = map.get(bestDamage);
    }

    private double calcDamage(Entity entity, BlockPos blockPos){
        CustomWorld world = new CustomWorld(mc.world);
        world.override("getBlockState", AbstractTask.types(IBlockState.class, BlockPos.class).task(args -> {
            BlockPos pos = args.fetch(0);
            if(blockPos == pos)
                return new CustomBlockState(Blocks.AIR, mc.world.getBlockState(pos));
            return mc.world.getBlockState(pos);
        }));
        BlockPos bestPlacePos = getBestPlacePos(world, entity);
        if(bestPlacePos == null)
            return 0;
        return calculateDamage(world, bestPlacePos.getX() + 0.5, bestPlacePos.getY() + 1.0, bestPlacePos.getZ() + 0.5, entity);
    }

    private BlockPos getBestPlacePos(World world, Entity entity){
        Map<Float, BlockPos> map = new HashMap<>();
        for(BlockPos pos : WorldUtilKt.sphere(5))
            map.put(calculateDamage(world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, entity), pos);
        Float max = map.keySet().stream().max(Float::compare).orElse(0.0f);
        BlockPos pos = map.get(max);
        if(pos == null)
            return null;
        return map.get(max);
    }

    private List<BlockPos> getSurroundingBlocks(){
        /*
        List<BlockPos> list = new ArrayList<>();
        list.add(new BlockPos(this.target.posX + 0.3, this.target.posY, this.target.posZ + 0.3));
        list.add(new BlockPos(this.target.posX + 0.3, this.target.posY, this.target.posZ - 0.3));
        list.add(new BlockPos(this.target.posX - 0.3, this.target.posY, this.target.posZ + 0.3));
        list.add(new BlockPos(this.target.posX - 0.3, this.target.posY, this.target.posZ - 0.3));
        List<BlockPos> blocks = new ArrayList<>();
        for(BlockPos pos : list)
            for(EnumFacing facing : EnumFacing.HORIZONTALS)
                if(!mc.world.getBlockState(pos.offset(facing)).getBlock().isReplaceable(mc.world, pos.offset(facing)))
                    if(!blocks.contains(pos.offset(facing)))
                        blocks.add(pos.offset(facing));
        blocks.removeAll(list);
        return list;
         */
        List<BlockPos> list = new ArrayList<>();
        for(EnumFacing facing : EnumFacing.HORIZONTALS)
            list.add(new BlockPos(this.target.posX, this.target.posY, this.target.posZ).offset(facing));
        return list;
    }

    public static float calculateDamage(World world, double posX, double posY, double posZ, Entity entity) {
        return calculateDamage(world, posX, posY, posZ, entity, entity.getEntityBoundingBox(), 0);
    }

    public static float calculateDamage(World world, double posX, double posY, double posZ, Entity entity, AxisAlignedBB bb, int interlopedAmount) {
        float doubleExplosionSize = 12.0F;
        double dist = entity.getDistance(posX, posY, posZ);

        if (dist > doubleExplosionSize) return 0f;

        if (interlopedAmount > 0) {
            Vec3d l_Interloped = EntityUtil.getInterpolatedAmount(entity, interlopedAmount);
            dist = EntityUtil.getDistance(l_Interloped.x, l_Interloped.y, l_Interloped.z, posX, posY, posZ);
        }

        double distancedsize = dist / (double) doubleExplosionSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity =  0;

        try {
            blockDensity = world.getBlockDensity(vec3d, bb);
        } catch (Exception ignored) {}

        double v = (1.0D - distancedsize) * blockDensity;
        float damage = (int) ((v * v + v) / 2.0D * 7.0D * doubleExplosionSize + 1.0D);
        double finald = 1.0D;
        if (entity instanceof EntityLivingBase) finald = getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(world, damage), new Explosion(world, null, posX, posY, posZ, 6F, false, true));
        return (float) finald;
    }

    public static float getBlastReduction(EntityLivingBase entity, float damage, Explosion explosion) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer) entity;
            DamageSource ds = DamageSource.causeExplosionDamage(explosion);

            try {
                damage = CombatRules.getDamageAfterAbsorb(damage, (float) ep.getTotalArmorValue(), (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            } catch (NullPointerException nullpointer) {
                damage = 0;
            }

            int k;

            k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);

            try {
                k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            } catch (NullPointerException nullpointer) {
                return 0;
            }

            float f = MathHelper.clamp(k, 0.0F, 20.0F);
            damage *= 1.0F - f / 25.0F;
            if (entity.isPotionActive(Potion.getPotionById(11))) damage -= damage / 4;
            // damage = Math.max(damage - ep.getAbsorptionAmount(), 0.0F);
            return damage;
        }

        damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        return damage;
    }

    public static float getDamageMultiplied(final World p_World, float damage) {
        int diff = p_World.getDifficulty().getDifficultyId();
        return damage * (diff == 0 ? 0 : (diff == 2 ? 1 : (diff == 1 ? 0.5f : 1.5f)));
    }
}
