package io.drahlek.smartfeedingtroughs.blocks;

import io.drahlek.dirigo.annotation.Block;
import io.drahlek.dirigo.registrars.BlockEntityRegistrar;
import io.drahlek.smartfeedingtroughs.blocks.entity.EdibleHayBlockEntity;
import io.drahlek.smartfeedingtroughs.blocks.entity.FeedingBlockEntity;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HayBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

@Block(id = EdibleHayBlock.NAME,
        validBlockEntityTypes = {EdibleHayBlock.NAME},
        registerItem = false)
public class EdibleHayBlock extends HayBlock implements EntityBlock {
    public static final String NAME = "edible_hay_block";
    public static final int MAX_CHARGES = 8;
    public static final IntegerProperty CHARGES = IntegerProperty.create("charges", 1, MAX_CHARGES);
    private static final VoxelShape[] SHAPES = new VoxelShape[MAX_CHARGES + 1];

    static {
        for (int charges = 1; charges <= MAX_CHARGES; charges++) {
            SHAPES[charges] = box(0.0D, 0.0D, 0.0D, 16.0D, charges * 2.0D, 16.0D);
        }
    }

    public EdibleHayBlock(Properties properties) {
        super(properties
                .mapColor(MapColor.COLOR_YELLOW)
                .instrument(NoteBlockInstrument.BANJO)
                .strength(0.5F)
                .sound(SoundType.GRASS)
        );
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AXIS, Direction.Axis.Y)
                .setValue(CHARGES, MAX_CHARGES));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new EdibleHayBlockEntity(blockPos, blockState);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState blockState, LootParams.Builder params) {
        int charges = blockState.getValue(CHARGES);
        return charges <= 0 ? List.of() : List.of(new ItemStack(Items.WHEAT, charges));
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return level.isClientSide()
                || blockEntityType != BlockEntityRegistrar.get(EdibleHayBlock.NAME, EdibleHayBlockEntity.class)
                ? null
                : (BlockEntityTicker<T>) (BlockEntityTicker<EdibleHayBlockEntity>) FeedingBlockEntity::tick;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CHARGES);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[state.getValue(CHARGES)];
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }
}
