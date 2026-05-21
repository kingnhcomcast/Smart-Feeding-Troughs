package io.drahlek.smartfeedingtroughs.blocks;

import io.drahlek.dirigo.annotation.Block;
import io.drahlek.dirigo.registrars.BlockEntityRegistrar;
import io.drahlek.smartfeedingtroughs.blocks.entity.EdibleHayBlockEntity;
import io.drahlek.smartfeedingtroughs.blocks.entity.FeedingBlockEntity;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HayBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.material.MapColor;
import org.jspecify.annotations.Nullable;

@Block(id = EdibleHayBlock.NAME,
        validBlockEntityTypes = {EdibleHayBlock.NAME},
        registerItem = false)
public class EdibleHayBlock extends HayBlock implements EntityBlock {
    public static final String NAME = "edible_hay_block";

    public EdibleHayBlock(Properties properties) {
        super(properties
                .mapColor(MapColor.COLOR_YELLOW)
                .instrument(NoteBlockInstrument.BANJO)
                .strength(0.5F)
                .sound(SoundType.GRASS)
        );
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new EdibleHayBlockEntity(blockPos, blockState);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState blockState, LootParams.Builder params) {
        if (!(params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof EdibleHayBlockEntity edibleHay)) {
            return List.of();
        }

        int charges = edibleHay.getCharges();
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
}
