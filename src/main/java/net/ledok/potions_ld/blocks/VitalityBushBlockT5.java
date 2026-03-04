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

public class VitalityBushBlockT5 extends BaseVitalityBushBlock {
    public static final MapCodec<VitalityBushBlockT5> CODEC = simpleCodec(properties -> new VitalityBushBlockT5(properties, () -> Items.GLOW_BERRIES, () -> null));
    public static final int MAX_AGE = 7;
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, MAX_AGE);

    public VitalityBushBlockT5(Properties properties, Supplier<Item> harvestItem, Supplier<Item> nextTierSeed) {
        super(properties, harvestItem, nextTierSeed);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    protected MapCodec<? extends BaseVitalityBushBlock> codec() {
        return CODEC;
    }

    @Override
    protected IntegerProperty getAgeProperty() { return AGE; }

    @Override
    protected int getMaxAge() { return MAX_AGE; }

    @Override
    protected int getGrowthChance() { return 16; }

    @Override
    protected float getBonemealGrowChance() { return 0.2f; }

    @Override
    protected float getBonemealRegressChance() { return 0.6f; }

    @Override
    protected int getHarvestResetAge() { return 3; }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        int age = state.getValue(AGE);
        if (age >= 4) { // Harvestable from age 4
            performHarvest(state, level, pos, player);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }
}
