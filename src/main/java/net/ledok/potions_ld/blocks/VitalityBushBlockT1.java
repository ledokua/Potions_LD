package net.ledok.potions_ld.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.Supplier;

public class VitalityBushBlockT1 extends BaseVitalityBushBlock {
    public static final MapCodec<VitalityBushBlockT1> CODEC = simpleCodec(properties -> new VitalityBushBlockT1(properties, () -> Items.GLOW_BERRIES, () -> Items.WHEAT_SEEDS));
    public static final int MAX_AGE = 3;
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, MAX_AGE);

    public VitalityBushBlockT1(Properties properties, Supplier<Item> harvestItem, Supplier<Item> nextTierSeed) {
        super(properties, harvestItem, nextTierSeed);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    protected MapCodec<? extends BaseVitalityBushBlock> codec() {
        return CODEC;
    }

    @Override
    protected IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    protected int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    protected int getGrowthChance() {
        return 5;
    }

    @Override
    protected float getBonemealGrowChance() {
        return 0.7f;
    }

    @Override
    protected float getBonemealRegressChance() {
        return 0.2f;
    }

    @Override
    protected int getHarvestResetAge() {
        return 1;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        int age = state.getValue(AGE);
        if (age >= MAX_AGE - 1) {
            performHarvest(state, level, pos, player);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }
}
