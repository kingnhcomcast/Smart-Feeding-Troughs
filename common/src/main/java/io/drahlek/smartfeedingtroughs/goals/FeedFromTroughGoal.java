package io.drahlek.smartfeedingtroughs.goals;

import io.drahlek.smartfeedingtroughs.Constants;
import io.drahlek.smartfeedingtroughs.animal.ISmartTroughClaimedAnimal;
import io.drahlek.smartfeedingtroughs.blocks.entity.FeedingBlockEntity;
import io.drahlek.smartfeedingtroughs.config.SmartFeedingTroughConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class FeedFromTroughGoal extends Goal {
    private static final double SPEED = 1.0D;
    private static final int REPATH_INTERVAL = 20;
    private static final int FEED_CHANCE_COOLDOWN = 40;
    private static final int EAT_DELAY = 40;
    private static final int CHEW_INTERVAL = 10;
    private static final int CHEW_PARTICLE_COUNT = 4;

    private final Animal animal;
    private final ISmartTroughClaimedAnimal troughAnimal;
    private int repathCooldown;
    private int feedChanceCooldown = 0;
    private int timeAtTrough;

    public FeedFromTroughGoal(Animal animal) {
        this.animal = animal;
        this.troughAnimal = animal instanceof ISmartTroughClaimedAnimal smartTroughAnimal ? smartTroughAnimal : null;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (feedChanceCooldown-- > 0) {
            return false;
        }

        //add some randomness if they feed, to avoid all of them coming at trough at the exact same time
        float feedChance = SmartFeedingTroughConfig.data().getFeedChance();
        if (animal.getRandom().nextFloat() >= feedChance) {
            if (Constants.LOG.isDebugEnabled()) {
                Constants.LOG.debug("Feed chance failed for {}", Constants.describeEntity(animal));
            }
            this.feedChanceCooldown = FEED_CHANCE_COOLDOWN;
            return false;
        }

        return isReadyToFeed();
    }

    @Override
    public boolean canContinueToUse() {
        return isReadyToFeed();
    }

    @Override
    public void start() {
        this.timeAtTrough = 0;
        this.repathCooldown = 0;
        this.feedChanceCooldown = 0;
    }

    @Override
    public void tick() {
        BlockPos troughPos = troughAnimal.smartfeedingtroughs$getClaimedTroughPos();
        if (troughPos == null) {
            return;
        }

        this.animal.getLookControl().setLookAt(
                troughPos.getX() + 0.5D,
                troughPos.getY(),
                troughPos.getZ() + 0.5D
        );

        if (isCloseEnoughToEat()) {
            animal.getNavigation().stop();
            if (++timeAtTrough >= EAT_DELAY) {
                if (isNearbyMate()) {
                    FeedingBlockEntity trough = troughAnimal.smartfeedingtroughs$getClaimedTrough(animal.level());
                    if (trough != null) {
                        trough.feedAnimal(animal);
                    }
                }
            } else if (timeAtTrough == 1 || timeAtTrough % CHEW_INTERVAL == 0) {
                playFeedingEffects();
            }
        } else {
            timeAtTrough = 0;
            moveToTrough();
        }
    }

    @Override
    public void stop() {
        animal.getNavigation().stop();
        this.timeAtTrough = 0;
        this.repathCooldown = 0;
        this.feedChanceCooldown = 0;
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

        FeedingBlockEntity trough = troughAnimal.smartfeedingtroughs$getClaimedTrough(animal.level());
        if (trough == null) {
            troughAnimal.smartfeedingtroughs$releaseClaim();
            return;
        }

        BlockPos feedingPos = troughAnimal.smartfeedingtroughs$getClaimedFeedingPos();
        if (feedingPos == null) {
            trough.releaseAnimal(animal);
            return;
        }

        if (!animal.getNavigation().moveTo(
                feedingPos.getX() + 0.5D,
                feedingPos.getY(),
                feedingPos.getZ() + 0.5D,
                SPEED
        )) {
            trough.releaseAnimal(animal);
        }
    }

    private void playFeedingEffects() {
        FeedingBlockEntity trough = troughAnimal.smartfeedingtroughs$getClaimedTrough(animal.level());
        if (trough == null) {
            return;
        }

        troughAnimal.smartfeedingtroughs$playEatingSound();

        if (!(animal.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        ItemStack food = trough.getFeedingFoodFor(animal);
        if (food.isEmpty()) {
            return;
        }

        Vec3 particlePos = getFeedingParticlePos();
        serverLevel.sendParticles(
                new ItemParticleOption(ParticleTypes.ITEM, food),
                particlePos.x(),
                particlePos.y(),
                particlePos.z(),
                CHEW_PARTICLE_COUNT,
                0.12D,
                0.08D,
                0.12D,
                0.02D
        );
    }

    private Vec3 getFeedingParticlePos() {
        BlockPos troughPos = troughAnimal.smartfeedingtroughs$getClaimedTroughPos();
        if (troughPos == null) {
            return animal.position().add(0.0D, animal.getBbHeight() * 0.6D, 0.0D);
        }

        Vec3 animalMouthPos = animal.position().add(0.0D, animal.getBbHeight() * 0.6D, 0.0D);
        Vec3 troughCenter = Vec3.atCenterOf(troughPos);
        return animalMouthPos.lerp(troughCenter, 0.35D);
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

        if (!(animal.level().getBlockEntity(troughPos) instanceof FeedingBlockEntity trough)) {
            troughAnimal.smartfeedingtroughs$releaseClaim();
            return false;
        }

        if (!trough.hasFeedingFoodFor(animal)) {
            trough.releaseAnimal(animal);
            return false;
        }

        return true;
    }


    private boolean isReadyToFeed() {
        return troughAnimal != null
                && troughAnimal.smartfeedingtroughs$getClaimedTroughPos() != null
                && animal.getAge() == 0
                && animal.canFallInLove()
                && claimedTroughStillValid()
                && isNearbyMate();
    }

    private boolean isNearbyMate() {
        FeedingBlockEntity trough = troughAnimal.smartfeedingtroughs$getClaimedTrough(animal.level());
        if (trough != null) {
            return trough.isMateAvailable(animal);
        }
        return false;
    }
}
