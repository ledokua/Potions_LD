package net.ledok.potions_ld.blocks;

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

public abstract class BaseVitalityBushBlock extends BushBlock {
    private static final VoxelShape SAPLING_SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 8.0D, 13.0D);
    private static final VoxelShape MID_GROWTH_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    protected final Supplier<Item> harvestItem;
    protected final Supplier<Item> nextTierSeed;

    public BaseVitalityBushBlock(Properties properties, Supplier<Item> harvestItem, Supplier<Item> nextTierSeed) {
        super(properties);
        this.harvestItem = harvestItem;
        this.nextTierSeed = nextTierSeed;
    }

    protected abstract IntegerProperty getAgeProperty();
    protected abstract int getMaxAge();
    protected abstract int getGrowthChance();
    protected abstract float getBonemealGrowChance();
    protected abstract float getBonemealRegressChance();
    protected abstract int getHarvestResetAge();

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(harvestItem.get());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(getAgeProperty()) == 0) {
            return SAPLING_SHAPE;
        } else {
            return state.getValue(getAgeProperty()) < getMaxAge() ? MID_GROWTH_SHAPE : super.getShape(state, level, pos, context);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(getAgeProperty());
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(getAgeProperty()) < getMaxAge();
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int age = state.getValue(getAgeProperty());
        if (age < getMaxAge() && level.getRawBrightness(pos.above(), 0) >= 9) {
            if (random.nextInt(getGrowthChance()) == 0) {
                BlockState newState = state.setValue(getAgeProperty(), age + 1);
                level.setBlock(pos, newState, 2);
                level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(newState));
            }
        }
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(Items.BONE_MEAL)) {
            if (state.getValue(getAgeProperty()) >= getMaxAge() - 1) {
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
        if (state.getValue(getAgeProperty()) < getMaxAge()) {
            if (random.nextFloat() < getBonemealGrowChance()) {
                int newAge = Math.min(getMaxAge(), state.getValue(getAgeProperty()) + 1);
                level.setBlock(pos, state.setValue(getAgeProperty(), newAge), 2);
            } else {
                level.levelEvent(2005, pos, 0);
                 if (random.nextFloat() < getBonemealRegressChance() && state.getValue(getAgeProperty()) > 0) {
                     level.setBlock(pos, state.setValue(getAgeProperty(), state.getValue(getAgeProperty()) - 1), 2);
                 }
            }
        }
    }
}
