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

public class VitalityBushBlockT4 extends BaseVitalityBushBlock {
    public static final MapCodec<VitalityBushBlockT4> CODEC = simpleCodec(properties -> new VitalityBushBlockT4(properties, () -> Items.GLOW_BERRIES, () -> Items.WHEAT_SEEDS));
    public static final int MAX_AGE = 6;
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, MAX_AGE);

    public VitalityBushBlockT4(Properties properties, Supplier<Item> harvestItem, Supplier<Item> nextTierSeed) {
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
        return 14;
    }

    @Override
    protected float getBonemealGrowChance() {
        return 0.2f;
    }

    @Override
    protected float getBonemealRegressChance() {
        return 0.5f;
    }

    @Override
    protected int getHarvestResetAge() {
        return 3;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        int age = state.getValue(AGE);
        
        if (age >= MAX_AGE - 2) {
            if (age == MAX_AGE - 2) {
                // Special drops logic
                if (level.random.nextFloat() < 0.3f) {
                    popResource(level, pos, new ItemStack(ItemRegistry.VITALITY_HERB_1, 2 + level.random.nextInt(5)));
                }
                if (level.random.nextFloat() < 0.4f) {
                    popResource(level, pos, new ItemStack(ItemRegistry.VITALITY_HERB_2, 1 + level.random.nextInt(3)));
                }
                if (level.random.nextFloat() < 0.2f) {
                    popResource(level, pos, new ItemStack(ItemRegistry.VITALITY_HERB_3, 1 + level.random.nextInt(2)));
                }
                if (level.random.nextFloat() < 0.1f) {
                    popResource(level, pos, new ItemStack(ItemRegistry.VITALITY_HERB_4, 1));
                }
            } else {
                // Normal drops
                int count = 1 + level.random.nextInt(2);
                popResource(level, pos, new ItemStack(harvestItem.get(), count + (age == MAX_AGE ? 1 : 0)));
                
                if (age == MAX_AGE && nextTierSeed != null && level.random.nextFloat() < 0.01f) {
                    popResource(level, pos, new ItemStack(nextTierSeed.get()));
                }
            }

            level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
            BlockState newState = state.setValue(AGE, getHarvestResetAge());
            level.setBlock(pos, newState, 2);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, newState));
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }
}
