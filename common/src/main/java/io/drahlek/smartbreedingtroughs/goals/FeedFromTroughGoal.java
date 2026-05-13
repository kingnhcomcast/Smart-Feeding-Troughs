package io.drahlek.smartbreedingtroughs.goals;

import io.drahlek.smartbreedingtroughs.animal.ISmartTroughClaimedAnimal;
import io.drahlek.smartbreedingtroughs.blocks.entity.SmartBreedingTroughBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class FeedFromTroughGoal extends Goal {
    private static final double SPEED = 1.0D;
    private static final int REPATH_INTERVAL = 20;

    private final Animal animal;
    private final ISmartTroughClaimedAnimal troughAnimal;
    private int repathCooldown;

    public FeedFromTroughGoal(Animal animal) {
        this.animal = animal;
        this.troughAnimal = animal instanceof ISmartTroughClaimedAnimal smartTroughAnimal ? smartTroughAnimal : null;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return troughAnimal != null
                && troughAnimal.smartbreedingtroughs$getClaimedTroughPos() != null
                && animal.getAge() == 0
                && animal.canFallInLove()
                && claimedTroughStillValid();
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void start() {
        this.repathCooldown = 0;
    }

    @Override
    public void tick() {
        BlockPos troughPos = troughAnimal.smartbreedingtroughs$getClaimedTroughPos();
        if (troughPos == null) {
            return;
        }

        this.animal.getLookControl().setLookAt(
                troughPos.getX() + 0.5D,
                troughPos.getY() + 0.5D,
                troughPos.getZ() + 0.5D
        );

        if (isCloseEnoughToEat()) {
            SmartBreedingTroughBlockEntity trough = troughAnimal.smartbreedingtroughs$getClaimedTrough(animal.level());
            if (trough != null) {
                trough.feedAnimal(animal);
            }
        } else {
            moveToTrough();
        }
    }

    @Override
    public void stop() {
        animal.getNavigation().stop();
    }

    private void moveToTrough() {
        BlockPos troughPos = troughAnimal.smartbreedingtroughs$getClaimedTroughPos();
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
            SmartBreedingTroughBlockEntity trough = troughAnimal.smartbreedingtroughs$getClaimedTrough(animal.level());
            if (trough != null) {
                trough.releaseAnimal(animal);
            } else {
                troughAnimal.smartbreedingtroughs$releaseClaim();
            }
        }
    }

    private boolean isCloseEnoughToEat() {
        BlockPos troughPos = troughAnimal.smartbreedingtroughs$getClaimedTroughPos();
        return troughPos != null && animal.distanceToSqr(Vec3.atCenterOf(troughPos)) <= 4.0D;
    }

    private boolean claimedTroughStillValid() {
        BlockPos troughPos = troughAnimal.smartbreedingtroughs$getClaimedTroughPos();
        if (troughPos == null) {
            return false;
        }

        if (!(animal.level().getBlockEntity(troughPos) instanceof SmartBreedingTroughBlockEntity trough)) {
            troughAnimal.smartbreedingtroughs$releaseClaim();
            return false;
        }

        if (!trough.hasBreedingFoodFor(animal)) {
            trough.releaseAnimal(animal);
            return false;
        }

        return true;
    }
}
