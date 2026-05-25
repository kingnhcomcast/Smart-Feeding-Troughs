package io.drahlek.smartfeedingtroughs.blocks;

import com.mojang.serialization.MapCodec;
import io.drahlek.dirigo.annotation.Block;
import io.drahlek.dirigo.annotation.Recipe;
import io.drahlek.dirigo.datagen.RecipeContext;
import io.drahlek.smartfeedingtroughs.Constants;
import io.drahlek.smartfeedingtroughs.blocks.entity.SmartFeedingTroughBlockEntity;
import io.drahlek.dirigo.registrars.BlockEntityRegistrar;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@Block(id = SmartFeedingTroughBlock.NAME,
        validBlockEntityTypes = {SmartFeedingTroughBlock.NAME},
        registerItem = false)
public class SmartFeedingTroughBlock extends BaseEntityBlock {
    public static final String NAME = "smart_trough";
    public static final int SLOT_COUNT = 4;
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final MapCodec<SmartFeedingTroughBlock> CODEC = simpleCodec(SmartFeedingTroughBlock::new);
    private static final VoxelShape X_AXIS_SHAPE = Shapes.or(
            box(2.0D, 0.0D, 4.0D, 14.0D, 2.0D, 12.0D),  // bottom
            box(0.0D, 0.0D, 3.0D, 16.0D, 8.0D, 5.0D),   // north
            box(0.0D, 0.0D, 11.0D, 16.0D, 8.0D, 13.0D), // south
            box(0.0D, 0.0D, 5.0D, 2.0D, 8.0D, 11.0D),   // west
            box(14.0D, 0.0D, 5.0D, 16.0D, 8.0D, 11.0D)  // east
    );
    private static final VoxelShape Z_AXIS_SHAPE = Shapes.or(
            box(4.0D, 0.0D, 2.0D, 12.0D, 2.0D, 14.0D),
            box(3.0D, 0.0D, 0.0D, 5.0D, 8.0D, 16.0D),
            box(11.0D, 0.0D, 0.0D, 13.0D, 8.0D, 16.0D),
            box(5.0D, 0.0D, 0.0D, 11.0D, 8.0D, 2.0D),
            box(5.0D, 0.0D, 14.0D, 11.0D, 8.0D, 16.0D)
    );

    public SmartFeedingTroughBlock(BlockBehaviour.Properties properties) {
        super(properties
                .requiresCorrectToolForDrops()
                .mapColor(MapColor.WOOD)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F, 3.0F)
                .sound(SoundType.WOOD)
                .ignitedByLava()
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Recipe
    public static void buildRecipe(RecipeContext recipe) {
        recipe.shaped(
                        RecipeCategory.MISC,
                        BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, SmartFeedingTroughBlock.NAME)),
                        1
                )
                .pattern("S S")
                .pattern("PPP")
                .define('S', ItemTags.WOODEN_SLABS)
                .define('P', ItemTags.PLANKS)
                .unlockedBy("has_planks", recipe.has(ItemTags.PLANKS))
                .unlockedBy("has_slab", recipe.has(ItemTags.WOODEN_SLABS))
                .save(recipe.output());
    }

    @Override
    public MapCodec<SmartFeedingTroughBlock> codec() {
        return CODEC;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof SmartFeedingTroughBlockEntity smartTrough) {
            player.openMenu(smartTrough);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new SmartFeedingTroughBlockEntity(blockPos, blockState);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return level.isClientSide()
                ? null
                : createTickerHelper(
                        blockEntityType,
                        BlockEntityRegistrar.get(SmartFeedingTroughBlock.NAME, SmartFeedingTroughBlockEntity.class),
                        SmartFeedingTroughBlockEntity::tick
                );
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof SmartFeedingTroughBlockEntity smartTrough) {
            Containers.dropContents(level, pos, smartTrough);
            level.updateNeighbourForOutputSignal(pos, this);
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(FACING).getAxis() == Direction.Axis.X ? Z_AXIS_SHAPE : X_AXIS_SHAPE;
    }
}
