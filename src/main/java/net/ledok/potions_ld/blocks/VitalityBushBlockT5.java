package net.ledok.potions_ld.blocks;

import com.mojang.serialization.MapCodec;
import net.ledok.potions_ld.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
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
        if (age < 4) { // Not harvestable before age 4
            return InteractionResult.PASS;
        }

        // Age 4 Drops
        if (age == 4) {
            if (level.random.nextFloat() < 0.7f) popResource(level, pos, new ItemStack(ItemRegistry.VITALITY_HERB_1, 3 + level.random.nextInt(8))); // 3-10
            if (level.random.nextFloat() < 0.5f) popResource(level, pos, new ItemStack(ItemRegistry.VITALITY_HERB_2, 2 + level.random.nextInt(5))); // 2-6
            if (level.random.nextFloat() < 0.3f) popResource(level, pos, new ItemStack(ItemRegistry.VITALITY_HERB_3, 2 + level.random.nextInt(5))); // 2-6
            if (level.random.nextFloat() < 0.1f) popResource(level, pos, new ItemStack(ItemRegistry.VITALITY_HERB_4, 1 + level.random.nextInt(3))); // 1-3
        }
        // Age 5 Drops
        else if (age == 5) {
            if (level.random.nextFloat() < 0.3f) popResource(level, pos, new ItemStack(ItemRegistry.VITALITY_HERB_1, 2 + level.random.nextInt(4))); // 2-5
            if (level.random.nextFloat() < 0.6f) popResource(level, pos, new ItemStack(ItemRegistry.VITALITY_HERB_2, 2 + level.random.nextInt(8))); // 2-9
            if (level.random.nextFloat() < 0.3f) popResource(level, pos, new ItemStack(ItemRegistry.VITALITY_HERB_3, 2 + level.random.nextInt(5))); // 2-6
            if (level.random.nextFloat() < 0.25f) popResource(level, pos, new ItemStack(ItemRegistry.VITALITY_HERB_4, 1 + level.random.nextInt(3))); // 1-3
            if (level.random.nextFloat() < 0.1f) popResource(level, pos, new ItemStack(ItemRegistry.VITALITY_HERB_5, 1));
        }
        // Age 6 Drops
        else if (age == 6) {
            if (level.random.nextFloat() < 0.3f) popResource(level, pos, new ItemStack(ItemRegistry.VITALITY_HERB_1, 2 + level.random.nextInt(4))); // 2-5
            if (level.random.nextFloat() < 0.4f) popResource(level, pos, new ItemStack(ItemRegistry.VITALITY_HERB_2, 2 + level.random.nextInt(4))); // 2-5
            if (level.random.nextFloat() < 0.6f) popResource(level, pos, new ItemStack(ItemRegistry.VITALITY_HERB_3, 2 + level.random.nextInt(7))); // 2-8
            popResource(level, pos, new ItemStack(ItemRegistry.VITALITY_HERB_4, 1 + level.random.nextInt(2))); // 1-2
            if (level.random.nextFloat() < 0.5f) popResource(level, pos, new ItemStack(ItemRegistry.VITALITY_HERB_5, 1 + level.random.nextInt(3))); // 1-3
        }
        // Age 7 (Max) Drops
        else if (age == 7) {
            if (level.random.nextFloat() < 0.8f) popResource(level, pos, new ItemStack(ItemRegistry.VITALITY_HERB_3, 2 + level.random.nextInt(4))); // 2-5
            popResource(level, pos, new ItemStack(ItemRegistry.VITALITY_HERB_4, 1 + level.random.nextInt(2))); // 1-2
            popResource(level, pos, new ItemStack(ItemRegistry.VITALITY_HERB_5, 1 + level.random.nextInt(2))); // 1-2
        }

        level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
        BlockState newState = state.setValue(AGE, getHarvestResetAge());
        level.setBlock(pos, newState, 2);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, newState));
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
