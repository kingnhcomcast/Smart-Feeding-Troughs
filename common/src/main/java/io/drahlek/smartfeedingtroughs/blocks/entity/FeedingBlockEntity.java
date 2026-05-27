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
import org.jspecify.annotations.Nullable;

import java.util.List;
@Getter
public abstract class FeedingBlockEntity extends BlockEntity {
    private static final long BIRTH_RESERVATION_TIMEOUT_TICKS = 600L;

    protected List<Animal> animals = Lists.newArrayList();
    private int reservedParents;
    private int completedReservedParents;
    private long lastBirthReservationGameTime;
    private int feedCheckOffset = -1;

    public FeedingBlockEntity(BlockEntityType<? extends FeedingBlockEntity> entityType, BlockPos blockPos, BlockState blockState) {
        super(entityType, blockPos, blockState);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, FeedingBlockEntity trough) {
        if (level.isClientSide()) {
            return;
        }

        int feedcheckInterval = Math.max(1, SmartFeedingTroughConfig.data().getTroughClaimCheckInterval());
        if ((level.getGameTime() % feedcheckInterval) != trough.getFeedCheckOffset(level, feedcheckInterval)) {
            return;
        }

        trough.feedCheck(level);
    }

    public void feedCheck(Level level) {
        if (Constants.LOG.isDebugEnabled()) {
            Constants.LOG.debug("Feed check started for {}", Constants.describeBlockEntity(this));
        }

        //verify claimed animals
        verifyClaimedAnimals();
        resolveBirthReservations(level);

        //if trough is empty or we are at max cap, release claim on all animals
        if(!isFoodAvailable()) {
            if (Constants.LOG.isDebugEnabled()) {
                Constants.LOG.debug("Trough is empty for {}", Constants.describeBlockEntity(this));
            }
            return;
        }

        //check max capacity and exit to avoid needless computation
        if (isAtMaxCapacity()) {
            if (Constants.LOG.isDebugEnabled()) {
                Constants.LOG.debug("Trough is at max capacity for {}", Constants.describeBlockEntity(this));
            }
            return;
        }

        //locate animals
        claimAnimals(level);
    }

    public abstract boolean isFoodAvailable();

    public boolean isAtMaxCapacity() {
        return hasCapacityLimit()
                && animals.size() + (reservedParents / 2.0D) >= SmartFeedingTroughConfig.data().getMaxClaimedAnimals();
    }

    public void feedAnimal(Animal animal) {
        Animal mate = getAvailableMate(animal);
        if (!animals.contains(animal)
                || !(animal instanceof ISmartTroughClaimedAnimal claimedAnimal)
                || animal.getAge() != 0
                || !animal.canFallInLove()
                || mate == null) {
            return;
        }

        if (animal.distanceToSqr(Vec3.atCenterOf(this.worldPosition)) > 4.0D) {
            return;
        }

        if (!tryReserveBirth(animal)) {
            return;
        }

        ItemStack consumedFood = consumeFoodFor(animal);
        if (!consumedFood.isEmpty()) {
            if (Constants.LOG.isDebugEnabled()) {
                Constants.LOG.debug("Feeding {} from {}", Constants.describeEntity(animal), Constants.describeBlockEntity(this));
            }
            animal.setInLove(null);
            claimedAnimal.smartfeedingtroughs$playEatingSound();
            onAnimalFed(animal);
        }
    }

    protected abstract ItemStack consumeFoodFor(Animal animal);

    protected void onAnimalFed(Animal animal) {
    }

    public boolean isMateAvailable(Animal animal) {
        return getAvailableMate(animal) != null;
    }

    protected @Nullable Animal getAvailableMate(Animal animal) {
        for(Animal mate : animals) {
            if (mate != animal
                    && mate.isAlive()
                    && mate.getClass() == animal.getClass()
                    && mate.getAge() == 0
                    && (mate.isInLove() || (mate.canFallInLove() && hasFeedingFoodFor(mate)))
                    && hasBirthSlot()) {
                return mate;
            }
        }
        return null;
    }

    /**
     *         in range
     *         can path to trough
     *         not yet claimed by another trough
     */
    private void claimAnimals(Level level) {
        int range = SmartFeedingTroughConfig.data().getRange();
        AABB area = new AABB(this.worldPosition).inflate(range);

        //get all animals that are in range
        for (Animal animal : level.getEntitiesOfClass(Animal.class, area)) {
            if(isAtMaxCapacity()) {
                break;
            }

            BlockPos feedingPos = getClaimFeedingPos(animal);
            if (feedingPos == null) {
                continue;
            }

            ISmartTroughClaimedAnimal claimedAnimal = (ISmartTroughClaimedAnimal) animal;
            if (Constants.LOG.isDebugEnabled()) {
                Constants.LOG.debug("Claimed animal {} for {}", Constants.describeEntity(animal), Constants.describeBlockEntity(this));
            }
            animals.add(animal);
            claimedAnimal.smartfeedingtroughs$claim(this, feedingPos);
        }
    }


    protected @Nullable BlockPos getClaimFeedingPos(Animal animal) {
        if (animal instanceof ISmartTroughClaimedAnimal claimedAnimal) {
            if (!animal.isAlive()
                    || claimedAnimal.smartfeedingtroughs$isClaimed()
                    || !hasFeedingFoodFor(animal)) {
                return null;
            }

            return getReachableFeedingPos(animal);
        }
        return null;
    }

    public void releaseAnimal(Animal animal) {
        if (animal instanceof ISmartTroughClaimedAnimal claimedAnimal) {
            BlockPos claimedTroughPos = claimedAnimal.smartfeedingtroughs$getClaimedTroughPos();
            if (this.worldPosition.equals(claimedTroughPos)) {
                claimedAnimal.smartfeedingtroughs$releaseClaim();
            }
        }

        animals.remove(animal);
    }

    public @Nullable BlockPos getReachableFeedingPos(Animal animal) {
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
        if (Constants.LOG.isDebugEnabled()) {
            Constants.LOG.debug("Claimed animals size {} for {}", animals.size(), Constants.describeBlockEntity(this));
        }
    }

    public abstract boolean hasFeedingFoodFor(Animal animal);

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        releaseAllAnimals();
        super.preRemoveSideEffects(pos, state);
    }

    public void releaseAllAnimals() {
        for (Animal animal : animals) {
            if (animal instanceof ISmartTroughClaimedAnimal claimedAnimal
                    && this.worldPosition.equals(claimedAnimal.smartfeedingtroughs$getClaimedTroughPos())) {
                claimedAnimal.smartfeedingtroughs$releaseClaim();
            }
        }
        animals.clear();
        reservedParents = 0;
        completedReservedParents = 0;
    }

    public boolean hasBirthSlot() {
        return !hasCapacityLimit() || !isAtMaxCapacity();
    }

    public void completeBirthReservation() {
        if (!hasCapacityLimit()) {
            return;
        }

        completedReservedParents = Math.min(reservedParents, completedReservedParents + 2);
    }

    protected boolean hasCapacityLimit() {
        return true;
    }

    private boolean tryReserveBirth(Animal animal) {
        if (!hasCapacityLimit()) {
            return true;
        }

        if (isAtMaxCapacity()) {
            if (Constants.LOG.isDebugEnabled()) {
                Constants.LOG.debug("Skipping feed for {} from {}, no birth capacity remains", Constants.describeEntity(animal), Constants.describeBlockEntity(this));
            }
            return false;
        }

        reservedParents++;
        lastBirthReservationGameTime = animal.level().getGameTime();
        return true;
    }

    private void resolveBirthReservations(Level level) {
        long gameTime = level.getGameTime();
        reservedParents = Math.max(0, reservedParents - completedReservedParents);
        completedReservedParents = 0;

        if (reservedParents > 0 && gameTime - lastBirthReservationGameTime > BIRTH_RESERVATION_TIMEOUT_TICKS) {
            reservedParents = 0;
        }
    }

    //this staggers feedchecks so all troughs dont fire at the same tick
    private int getFeedCheckOffset(Level level, int feedcheckInterval) {
        //cover case when interval has been decreased in config
        if (feedCheckOffset < 0 || feedCheckOffset >= feedcheckInterval) {
            feedCheckOffset = level.getRandom().nextInt(feedcheckInterval);
        }

        return feedCheckOffset;
    }
}
