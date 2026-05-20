package io.drahlek.smartfeedingtroughs.blocks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class FeedingBlockEntity extends BlockEntity {
    public FeedingBlockEntity(BlockEntityType<? extends FeedingBlockEntity> entityType, BlockPos blockPos, BlockState blockState) {
        super(entityType, blockPos, blockState);
    }
}
