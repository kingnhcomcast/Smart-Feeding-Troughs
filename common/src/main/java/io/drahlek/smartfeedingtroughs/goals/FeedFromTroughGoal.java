package io.drahlek.smartfeedingtroughs.goals;

import io.drahlek.smartfeedingtroughs.Constants;
import io.drahlek.smartfeedingtroughs.animal.ISmartTroughClaimedAnimal;
import io.drahlek.smartfeedingtroughs.blocks.entity.SmartFeedingTroughBlockEntity;
import io.drahlek.smartfeedingtroughs.config.SmartFeedingTroughConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class FeedFromTroughGoal extends Goal {
    private static final double SPEED = 1.0D;
    private static final int REPATH_INTERVAL = 20;
    private static final int FEED_CHANCE_COOLDOWN = 40;

    private final Animal animal;
    private final ISmartTroughClaimedAnimal troughAnimal;
    private int repathCooldown;
    private int feedChanceCooldown;

    public FeedFromTroughGoal(Animal animal) {
        this.animal = animal;
        this.troughAnimal = animal instanceof ISmartTroughClaimedAnimal smartTroughAnimal ? smartTroughAnimal : null;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return troughAnimal != null
                && troughAnimal.smartfeedingtroughs$getClaimedTroughPos() != null
                && animal.getAge() == 0
                && animal.canFallInLove()
                && claimedTroughStillValid()
                && isNearbyMate();
    }

    private boolean isNearbyMate() {
        SmartFeedingTroughBlockEntity trough = troughAnimal.smartfeedingtroughs$getClaimedTrough(animal.level());
        if (trough != null) {
            return trough.isMateAvailable(animal);
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void start() {
        this.repathCooldown = 0;
        this.feedChanceCooldown = 0;
    }

    @Override
    public void tick() {
        if(feedChanceCooldown-- > 0) {
            return;
        }

        BlockPos troughPos = troughAnimal.smartfeedingtroughs$getClaimedTroughPos();
        if (troughPos == null) {
            return;
        }

        this.animal.getLookControl().setLookAt(
                troughPos.getX() + 0.5D,
                troughPos.getY() + 0.5D,
                troughPos.getZ() + 0.5D
        );

        if (isCloseEnoughToEat()) {
            SmartFeedingTroughBlockEntity trough = troughAnimal.smartfeedingtroughs$getClaimedTrough(animal.level());
            if (trough != null) {
                trough.feedAnimal(animal);
            }
        } else {
            //add some randomness if they feed, to avoid all of them coming at trough at the exact same time
            float feedChance = SmartFeedingTroughConfig.data().getFeedChance();
            Constants.LOG.debug("Feed chance: {}", feedChance);
            if (animal.getRandom().nextFloat() >= feedChance) {
                Constants.LOG.debug("Feed chance failed for {}:{}", animal.getName().getString(), animal.getId());
                this.feedChanceCooldown = FEED_CHANCE_COOLDOWN;
                return;
            }

            moveToTrough();
        }
    }

    @Override
    public void stop() {
        animal.getNavigation().stop();
    }

    private void moveToTrough() {
        BlockPos troughPos = troughAnimal.smartfeedingtroughs$getClaimedTroughPos();
        if (troughPos == null) {
            return;
        }

        if (!animal.getNavigation().isDone() && repathCooldown > 0) {
            repathCooldown--;
            return;
        }

        repathCooldown = REPATH_INTERVAL;

        if (!animal.getNavigation().moveTo(
                troughPos.getX() + 0.5D,
                troughPos.getY(),
                troughPos.getZ() + 0.5D,
                SPEED
        )) {
            SmartFeedingTroughBlockEntity trough = troughAnimal.smartfeedingtroughs$getClaimedTrough(animal.level());
            if (trough != null) {
                trough.releaseAnimal(animal);
            } else {
                troughAnimal.smartfeedingtroughs$releaseClaim();
            }
        }
    }

    private boolean isCloseEnoughToEat() {
        BlockPos troughPos = troughAnimal.smartfeedingtroughs$getClaimedTroughPos();
        return troughPos != null && animal.distanceToSqr(Vec3.atCenterOf(troughPos)) <= 4.0D;
    }

    private boolean claimedTroughStillValid() {
        BlockPos troughPos = troughAnimal.smartfeedingtroughs$getClaimedTroughPos();
        if (troughPos == null) {
            return false;
        }

        if (!(animal.level().getBlockEntity(troughPos) instanceof SmartFeedingTroughBlockEntity trough)) {
            troughAnimal.smartfeedingtroughs$releaseClaim();
            return false;
        }

        if (!trough.hasFeedingFoodFor(animal)) {
            trough.releaseAnimal(animal);
            return false;
        }

        return !trough.isAtMaxCapacity();
    }
}
