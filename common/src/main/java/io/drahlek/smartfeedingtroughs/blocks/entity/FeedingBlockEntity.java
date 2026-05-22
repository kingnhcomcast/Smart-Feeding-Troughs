package io.drahlek.smartfeedingtroughs.blocks.entity;

import com.google.common.collect.Lists;
import io.drahlek.smartfeedingtroughs.Constants;
import io.drahlek.smartfeedingtroughs.animal.ISmartTroughClaimedAnimal;
import io.drahlek.smartfeedingtroughs.config.SmartFeedingTroughConfig;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Objects;

@Getter
public abstract class FeedingBlockEntity extends BlockEntity {
    protected List<Animal> animals = Lists.newArrayList();

    public FeedingBlockEntity(BlockEntityType<? extends FeedingBlockEntity> entityType, BlockPos blockPos, BlockState blockState) {
        super(entityType, blockPos, blockState);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, FeedingBlockEntity trough) {
        if (level.isClientSide()) {
            return;
        }

        int feedcheckInterval = Math.max(1, SmartFeedingTroughConfig.data().getTroughClaimCheckInterval());
        if (level.getGameTime() % feedcheckInterval != 0) {
            return;
        }

        trough.feedCheck(level);
    }

    public void feedCheck(Level level) {
        Constants.LOG.debug("Feed check started for {}", Constants.describeBlockEntity(this));

        //verify claimed animals
        verifyClaimedAnimals();

        //if trough is empty or we are at max cap, release claim on all animals
        if(!isFoodAvailable()) {
            Constants.LOG.debug("Trough is empty for {}", Constants.describeBlockEntity(this));
            return;
        }

        //check max capacity and exit to avoid needless computation
        if (isAtMaxCapacity()) {
            Constants.LOG.debug("Trough is at max capacity for {}", Constants.describeBlockEntity(this));
            return;
        }

        //locate animals
        claimAnimals(level);
    }

    public abstract boolean isFoodAvailable();

    public boolean isAtMaxCapacity() {
        return animals.size() >= SmartFeedingTroughConfig.data().getMaxClaimedAnimals();
    }

    public void feedAnimal(Animal animal) {
        if (!animals.contains(animal)
                || !(animal instanceof ISmartTroughClaimedAnimal claimedAnimal)
                || animal.getAge() != 0
                || !animal.canFallInLove()
                || !isMateAvailable(animal)) {
            return;
        }

        if (animal.distanceToSqr(Vec3.atCenterOf(this.worldPosition)) > 4.0D) {
            return;
        }

        ItemStack consumedFood = consumeFoodFor(animal);
        if (!consumedFood.isEmpty()) {
            Constants.LOG.debug("Feeding {} from {}", Constants.describeEntity(animal), Constants.describeBlockEntity(this));
            animal.setInLove(null);
            animal.playSound(animal.getEatingSound(consumedFood), 1.0F, 1.0F);
        }
    }

    protected abstract ItemStack consumeFoodFor(Animal animal);

    public boolean isMateAvailable(Animal animal) {
        for(Animal mate : animals) {
            if (mate != animal
                    && mate.isAlive()
                    && mate.getClass() == animal.getClass()
                    && mate.getAge() == 0
                    && (mate.isInLove() || (mate.canFallInLove() && hasFeedingFoodFor(mate)))) {
                return true;
            }
        }
        return false;
    }

    /**
     *         in range
     *         can path to trough
     *         not yet claimed by another trough
     *         adult
     *         */
    private void claimAnimals(Level level) {
        int range = SmartFeedingTroughConfig.data().getRange();
        int maxAnimals = SmartFeedingTroughConfig.data().getMaxClaimedAnimals();
        AABB area = new AABB(this.worldPosition).inflate(range);

        //get all animals that are in range
        for (Animal animal : level.getEntitiesOfClass(Animal.class, area, this::canClaim)) {
            if (animal instanceof ISmartTroughClaimedAnimal claimedAnimal && !claimedAnimal.smartfeedingtroughs$isClaimed()) {
                Constants.LOG.debug("Claimed animal {} for {}", Constants.describeEntity(animal), Constants.describeBlockEntity(this));
                animals.add(animal);
                claimedAnimal.smartfeedingtroughs$claim(this);
                if (animals.size() >= maxAnimals) {
                    break;
                }
            }
        }
    }


    protected boolean canClaim(Animal animal) {
        if (animal instanceof ISmartTroughClaimedAnimal claimedAnimal) {
            return hasFeedingFoodFor(animal) &&
                    !claimedAnimal.smartfeedingtroughs$isClaimed() &&
                    canPathToTrough(animal);
        }
        return false;
    }

    public void releaseAnimal(Animal animal) {
        if (animal instanceof ISmartTroughClaimedAnimal claimedAnimal
                && this.worldPosition.equals(Objects.requireNonNull(claimedAnimal.smartfeedingtroughs$getClaimedTroughPos()))) {
            claimedAnimal.smartfeedingtroughs$releaseClaim();
        }

        animals.remove(animal);
    }

    protected boolean canPathToTrough(Animal animal) {
        return getReachableFeedingPos(animal) != null;
    }

    public BlockPos getReachableFeedingPos(Animal animal) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos feedingPos = this.worldPosition.relative(direction);
            Path path = animal.getNavigation().createPath(feedingPos, 0);
            if (path != null && path.canReach()) {
                return feedingPos;
            }
        }

        return null;
    }

    private void verifyClaimedAnimals() {
        int range = SmartFeedingTroughConfig.data().getRange();
        AABB claimArea = new AABB(this.worldPosition).inflate(range);

        //release animal if they have been killed, or if we no longer have any food
        animals.removeIf(animal -> {
            if (!(animal instanceof ISmartTroughClaimedAnimal claimedAnimal)) {
                return true;
            }

            BlockPos claimedTroughPos = claimedAnimal.smartfeedingtroughs$getClaimedTroughPos();
            boolean claimedByThisTrough = this.worldPosition.equals(claimedTroughPos);
            boolean remove = !animal.isAlive()
                    || !claimArea.contains(animal.position())
                    || !hasFeedingFoodFor(animal)
                    || !claimedByThisTrough;

            if (remove && claimedByThisTrough) {
                claimedAnimal.smartfeedingtroughs$releaseClaim();
            }

            return remove;
        });
        Constants.LOG.debug("Claimed animals size {} for {}", animals.size(), Constants.describeBlockEntity(this));
    }

    public abstract boolean hasFeedingFoodFor(Animal animal);

    @Override
    public void setRemoved() {
        releaseAllAnimals();
        super.setRemoved();
    }

    public void releaseAllAnimals() {
        for (Animal animal : animals) {
            if (animal instanceof ISmartTroughClaimedAnimal claimedAnimal
                    && this.worldPosition.equals(claimedAnimal.smartfeedingtroughs$getClaimedTroughPos())) {
                claimedAnimal.smartfeedingtroughs$releaseClaim();
            }
        }
        animals.clear();
    }
}
