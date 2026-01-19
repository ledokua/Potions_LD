package net.ledok.potions_ld.blocks;

import com.mojang.serialization.MapCodec;
import net.ledok.potions_ld.blocks.entity.AlchemyTableBlockEntity;
import net.ledok.potions_ld.blocks.entity.BlockEntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class AlchemyTableBlock extends BaseEntityBlock {
    public static final MapCodec<AlchemyTableBlock> CODEC = simpleCodec(AlchemyTableBlock::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<ChestType> CHEST_TYPE = BlockStateProperties.CHEST_TYPE;
    public static final BooleanProperty HAS_SPEED_UPGRADE = BooleanProperty.create("has_speed_upgrade");
    public static final BooleanProperty HAS_EFFICIENCY_UPGRADE = BooleanProperty.create("has_efficiency_upgrade");
    public static final BooleanProperty HAS_FORTUNE_UPGRADE = BooleanProperty.create("has_fortune_upgrade");

    public AlchemyTableBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(CHEST_TYPE, ChestType.SINGLE)
                .setValue(HAS_SPEED_UPGRADE, false)
                .setValue(HAS_EFFICIENCY_UPGRADE, false)
                .setValue(HAS_FORTUNE_UPGRADE, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        // Create block entity for both parts so hoppers can connect to either
        return new AlchemyTableBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (state.getValue(CHEST_TYPE) == ChestType.LEFT) {
            return createTickerHelper(blockEntityType, BlockEntityTypeRegistry.ALCHEMY_TABLE, AlchemyTableBlockEntity::tick);
        }
        return null;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            BlockPos mainPos = state.getValue(CHEST_TYPE) == ChestType.RIGHT ? pos.relative(state.getValue(FACING).getCounterClockWise()) : pos;
            BlockEntity blockEntity = level.getBlockEntity(mainPos);
            if (blockEntity instanceof AlchemyTableBlockEntity alchemyTable) {
                player.openMenu(alchemyTable);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection().getOpposite();
        BlockPos blockPos = context.getClickedPos();
        BlockPos rightPos = blockPos.relative(direction.getClockWise());
        Level level = context.getLevel();

        if (level.getBlockState(rightPos).canBeReplaced(context)) {
            return this.defaultBlockState().setValue(FACING, direction).setValue(CHEST_TYPE, ChestType.LEFT);
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (state.getValue(CHEST_TYPE) == ChestType.LEFT) {
            Direction facing = state.getValue(FACING);
            BlockPos rightPos = pos.relative(facing.getClockWise());
            level.setBlock(rightPos, state.setValue(CHEST_TYPE, ChestType.RIGHT), 3);
        }
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        ChestType chestType = state.getValue(CHEST_TYPE);
        Direction facing = state.getValue(FACING);
        BlockPos otherPartPos = (chestType == ChestType.LEFT) ? pos.relative(facing.getClockWise()) : pos.relative(facing.getCounterClockWise());
        BlockState otherPartState = level.getBlockState(otherPartPos);

        if (otherPartState.is(this) && otherPartState.getValue(CHEST_TYPE) != chestType) {
            level.setBlock(otherPartPos, Blocks.AIR.defaultBlockState(), 35);
            level.levelEvent(player, 2001, otherPartPos, Block.getId(otherPartState));
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            if (state.getValue(CHEST_TYPE) == ChestType.LEFT) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof AlchemyTableBlockEntity) {
                    Containers.dropContents(level, pos, (AlchemyTableBlockEntity) blockEntity);
                    level.updateNeighbourForOutputSignal(pos, this);
                }
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, CHEST_TYPE, HAS_SPEED_UPGRADE, HAS_EFFICIENCY_UPGRADE, HAS_FORTUNE_UPGRADE);
    }
}
