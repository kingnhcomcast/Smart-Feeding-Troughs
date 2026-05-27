package io.drahlek.smartfeedingtroughs.blocks.entity;

import io.drahlek.dirigo.annotation.BlockEntity;
import io.drahlek.dirigo.registrars.BlockEntityRegistrar;
import io.drahlek.smartfeedingtroughs.Constants;
import io.drahlek.smartfeedingtroughs.animal.ISmartTroughClaimedAnimal;
import io.drahlek.smartfeedingtroughs.blocks.EdibleHayBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

@BlockEntity(id = EdibleHayBlock.NAME)
public class EdibleHayBlockEntity extends FeedingBlockEntity {
    public EdibleHayBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityRegistrar.get(EdibleHayBlock.NAME, EdibleHayBlockEntity.class), worldPosition, blockState);
    }

    @Override
    protected boolean canClaim(Animal animal) {
        if (animal instanceof ISmartTroughClaimedAnimal claimedAnimal) {
            boolean canClaim = hasFeedingFoodFor(animal) && canPathToTrough(animal) && !(claimedAnimal.smartfeedingtroughs$getClaimedTrough(level) instanceof EdibleHayBlockEntity);

            //steal the claim
            if(canClaim && claimedAnimal.smartfeedingtroughs$isClaimed()) {
                Constants.LOG.debug("Stealing claim for {} with {}", Constants.describeEntity(animal), Constants.describeBlockEntity(this));
                claimedAnimal.smartfeedingtroughs$releaseClaim();
            }

            return canClaim;
        }
        return false;
    }

    @Override
    public boolean isFoodAvailable() {
        return getCharges() > 0;
    }

    public int getCharges() {
        return getBlockState().getValue(EdibleHayBlock.CHARGES);
    }

    @Override
    public boolean isAtMaxCapacity() {
        return false;
    }

    @Override
    protected boolean hasCapacityLimit() {
        return false;
    }

    @Override
    protected ItemStack consumeFoodFor(Animal animal) {
        int charges = getCharges();
        if (charges <= 1) {
            level.setBlock(this.getBlockPos(), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        } else {
            level.setBlock(this.getBlockPos(), getBlockState().setValue(EdibleHayBlock.CHARGES, charges - 1), Block.UPDATE_ALL);
            setChanged();
        }

        return new ItemStack(Items.WHEAT);
    }

    @Override
    public boolean hasFeedingFoodFor(Animal animal) {
        return animal.isFood(new ItemStack(Items.WHEAT));
    }
}
