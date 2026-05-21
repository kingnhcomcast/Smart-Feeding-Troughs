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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;

@BlockEntity(id = EdibleHayBlock.NAME)
public class EdibleHayBlockEntity extends FeedingBlockEntity {
    private static final int CHARGES_DEFAULT = 8;
    private int charges = CHARGES_DEFAULT;

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
        return charges > 0;
    }

    public int getCharges() {
        return this.charges;
    }

    @Override
    public boolean isAtMaxCapacity() {
        return false;
    }

    @Override
    protected ItemStack consumeFoodFor(Animal animal) {
        if (this.charges <= 1) {
            level.setBlock(this.getBlockPos(), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        } else {
            this.charges--;
            setChanged();
        }

        return new ItemStack(Items.WHEAT);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);

        this.charges = input.getInt("charges").orElse(CHARGES_DEFAULT);
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("charges", this.charges);
    }

    @Override
    public boolean hasFeedingFoodFor(Animal animal) {
        return animal.isFood(new ItemStack(Items.WHEAT));
    }
}
