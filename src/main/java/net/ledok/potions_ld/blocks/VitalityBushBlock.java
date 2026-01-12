package net.ledok.potions_ld.blocks;

import com.mojang.serialization.MapCodec;
import net.ledok.potions_ld.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

// Extend BushBlock instead of SweetBerryBushBlock to avoid property conflict
public class VitalityBushBlock extends BushBlock {
    public static final MapCodec<VitalityBushBlock> CODEC = simpleCodec(properties -> new VitalityBushBlock(properties, () -> Items.GLOW_BERRIES, () -> Items.WHEAT_SEEDS, 3));
    public static final int MAX_AGE = 7; // Increased to support higher tiers
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, MAX_AGE);
    private static final VoxelShape SAPLING_SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 8.0D, 13.0D);
    private static final VoxelShape MID_GROWTH_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    private final Supplier<Item> harvestItem;
    private final Supplier<Item> nextTierSeed;
    private final int maxAge;

    public VitalityBushBlock(Properties properties, Supplier<Item> harvestItem, Supplier<Item> nextTierSeed, int maxAge) {
        super(properties);
        this.harvestItem = harvestItem;
        this.nextTierSeed = nextTierSeed;
        this.maxAge = maxAge;
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(harvestItem.get());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(AGE) == 0) {
            return SAPLING_SHAPE;
        } else {
            return state.getValue(AGE) < maxAge ? MID_GROWTH_SHAPE : super.getShape(state, level, pos, context);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(AGE) < maxAge;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int age = state.getValue(AGE);
        if (age < maxAge && level.getRawBrightness(pos.above(), 0) >= 9) {
            // Growth chance logic
            // Tier 1 (maxAge 3): 1/5 chance
            // Tier 2 (maxAge 4): 1/7 chance
            // Tier 3 (maxAge 5): 1/10 chance
            // Tier 4 (maxAge 6): 1/12 chance
            int chance = 5;
            if (maxAge == 4) chance = 7;
            else if (maxAge == 5) chance = 10;
            else if (maxAge == 6) chance = 12;
            
            if (random.nextInt(chance) == 0) {
                BlockState newState = state.setValue(AGE, age + 1);
                level.setBlock(pos, newState, 2);
                level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(newState));
            }
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        int age = state.getValue(AGE);
        boolean isMaxAge = age == maxAge;
        
        // Determine if harvestable
        boolean canHarvest = false;
        if (maxAge >= 5) { // Tier 3 and 4
            if (age >= maxAge - 2) canHarvest = true;
        } else {
            if (age >= maxAge - 1) canHarvest = true;
        }
        
        if (canHarvest) {
            // Check for special "early harvest" drops
            if (maxAge >= 5 && age == maxAge - 2) {
                // Special drops logic
                if (maxAge == 5) { // Tier 3
                    // 40% Tier 1 (3-4)
                    if (level.random.nextFloat() < 0.4f) {
                        popResource(level, pos, new ItemStack(ItemRegistry.VITALITY_HERB_1, 3 + level.random.nextInt(2)));
                    }
                    // 40% Tier 2 (1-3)
                    if (level.random.nextFloat() < 0.4f) {
                        popResource(level, pos, new ItemStack(ItemRegistry.VITALITY_HERB_2, 1 + level.random.nextInt(3)));
                    }
                    // 20% Tier 3 (1)
                    if (level.random.nextFloat() < 0.2f) {
                        popResource(level, pos, new ItemStack(ItemRegistry.VITALITY_HERB_3, 1));
                    }
                } else if (maxAge == 6) { // Tier 4
                    // 40% Tier 1 (2-6)
                    if (level.random.nextFloat() < 0.4f) {
                        popResource(level, pos, new ItemStack(ItemRegistry.VITALITY_HERB_1, 2 + level.random.nextInt(5)));
                    }
                    // 40% Tier 2 (1-3)
                    if (level.random.nextFloat() < 0.4f) {
                        popResource(level, pos, new ItemStack(ItemRegistry.VITALITY_HERB_2, 1 + level.random.nextInt(3)));
                    }
                    // 20% Tier 3 (1-2)
                    if (level.random.nextFloat() < 0.2f) {
                        popResource(level, pos, new ItemStack(ItemRegistry.VITALITY_HERB_3, 1 + level.random.nextInt(2)));
                    }
                    // 10% Tier 4 (1)
                    if (level.random.nextFloat() < 0.1f) {
                        popResource(level, pos, new ItemStack(ItemRegistry.VITALITY_HERB_4, 1));
                    }
                }
            } else {
                // Normal drops
                int count = 1 + level.random.nextInt(2);
                popResource(level, pos, new ItemStack(harvestItem.get(), count + (isMaxAge ? 1 : 0)));
                
                // Chance for next tier seed only at MAX age
                if (isMaxAge && nextTierSeed != null && level.random.nextFloat() < 0.01f) { // 1% chance
                    popResource(level, pos, new ItemStack(nextTierSeed.get()));
                }
            }

            level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
            
            // Reset logic
            // Tier 1 (maxAge 3): Reset to 1
            // Tier 2 (maxAge 4): Reset to 2
            // Tier 3 (maxAge 5): Reset to 2
            // Tier 4 (maxAge 6): Reset to 3
            int resetAge = 1;
            if (maxAge == 4) resetAge = 2;
            else if (maxAge == 5) resetAge = 2;
            else if (maxAge == 6) resetAge = 3;
            
            BlockState newState = state.setValue(AGE, resetAge);
            level.setBlock(pos, newState, 2);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, newState));
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(Items.BONE_MEAL)) {
            // Prevent bonemeal usage if at max age or one stage before max age
            if (state.getValue(AGE) >= maxAge - 1) {
                return ItemInteractionResult.FAIL;
            }

            if (!level.isClientSide) {
                 performBonemeal((ServerLevel)level, level.random, pos, state);
                 if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
    
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        if (state.getValue(AGE) < maxAge) {
            // Bonemeal logic
            // Tier 1 (maxAge 3): 70% grow, 20% regress
            // Tier 2 (maxAge 4): 60% grow, 30% regress
            // Tier 3 (maxAge 5): 40% grow, 40% regress
            // Tier 4 (maxAge 6): 20% grow, 50% regress
            
            float growChance = 0.7f;
            float regressChance = 0.2f;
            
            if (maxAge == 4) {
                growChance = 0.6f;
                regressChance = 0.3f;
            } else if (maxAge == 5) {
                growChance = 0.4f;
                regressChance = 0.4f;
            } else if (maxAge == 6) {
                growChance = 0.2f;
                regressChance = 0.5f;
            }

            if (random.nextFloat() < growChance) {
                int newAge = Math.min(maxAge, state.getValue(AGE) + 1);
                level.setBlock(pos, state.setValue(AGE, newAge), 2);
            } else {
                level.levelEvent(2005, pos, 0); // Bonemeal particles
                 if (random.nextFloat() < regressChance && state.getValue(AGE) > 0) {
                     level.setBlock(pos, state.setValue(AGE, state.getValue(AGE) - 1), 2);
                 }
            }
        }
    }
}
