package io.drahlek.smartfeedingtroughs.blocks.entity;

import io.drahlek.dirigo.annotation.BlockEntity;
import io.drahlek.dirigo.registrars.BlockEntityRegistrar;
import io.drahlek.smartfeedingtroughs.Constants;
import io.drahlek.smartfeedingtroughs.animal.ISmartTroughClaimedAnimal;
import io.drahlek.smartfeedingtroughs.blocks.EdibleHayBlock;
import io.drahlek.smartfeedingtroughs.config.SmartFeedingTroughConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

@BlockEntity(id = EdibleHayBlock.NAME)
public class EdibleHayBlockEntity extends FeedingBlockEntity {

    public static final ItemStack WHEAT_STACK = new ItemStack(Items.WHEAT);

    public EdibleHayBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityRegistrar.get(EdibleHayBlock.NAME, EdibleHayBlockEntity.class), worldPosition, blockState);
    }

    @Override
    protected @Nullable BlockPos getClaimFeedingPos(Animal animal) {
        if (animal instanceof ISmartTroughClaimedAnimal claimedAnimal) {
            if (!animal.isAlive()
                    || animal.getAge() != 0
                    || !animal.canFallInLove()
                    || !hasFeedingFoodFor(animal)
                    || getAvailableMate(animal) == null
                    || claimedAnimal.smartfeedingtroughs$getClaimedTrough(level) instanceof EdibleHayBlockEntity) {
                return null;
            }

            BlockPos feedingPos = getReachableFeedingPos(animal);
            if (feedingPos == null) {
                return null;
            }

            //steal the claim
            if(claimedAnimal.smartfeedingtroughs$isClaimed()) {
                if (Constants.LOG.isDebugEnabled()) {
                    Constants.LOG.debug("Stealing claim for {} with {}", Constants.describeEntity(animal), Constants.describeBlockEntity(this));
                }
                claimedAnimal.smartfeedingtroughs$releaseClaim();
            }

            return feedingPos;
        }
        return null;
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
        return animals.size() >= getCharges();
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
    public ItemStack getFeedingFoodFor(Animal animal) {
        return hasFeedingFoodFor(animal) ? WHEAT_STACK.copy() : ItemStack.EMPTY;
    }

    @Override
    protected void onAnimalFed(Animal animal) {
        releaseAnimal(animal);
    }

    @Override
    protected @Nullable Animal getAvailableMate(Animal animal) {
        int range = SmartFeedingTroughConfig.data().getRange();
        AABB area = new AABB(this.worldPosition).inflate(range);

        for (Animal mate : level.getEntitiesOfClass(Animal.class, area)) {
            if (mate != animal
                    && mate.isAlive()
                    && mate.getClass() == animal.getClass()
                    && mate.getAge() == 0
                    && (mate.isInLove() || (mate.canFallInLove() && hasFeedingFoodFor(mate)))) {
                return mate;
            }
        }

        return null;
    }

    @Override
    public boolean hasFeedingFoodFor(Animal animal) {
        return animal.isFood(WHEAT_STACK);
    }
}
