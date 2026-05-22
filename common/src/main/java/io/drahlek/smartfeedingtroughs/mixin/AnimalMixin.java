package io.drahlek.smartfeedingtroughs.mixin;

import io.drahlek.smartfeedingtroughs.animal.ISmartTroughClaimedAnimal;
import io.drahlek.smartfeedingtroughs.blocks.entity.FeedingBlockEntity;
import io.drahlek.smartfeedingtroughs.goals.FeedFromTroughGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.UnknownNullability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Animal.class)
public abstract class AnimalMixin extends AgeableMob implements ISmartTroughClaimedAnimal {
    @Unique
    private BlockPos smartfeedingtroughs$claimedTroughPos;

    protected AnimalMixin(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void smartfeedingtroughs$addTroughGoal(
            EntityType<? extends Animal> entityType,
            Level level,
            CallbackInfo ci
    ) {
        //we should insert this goal at the same priority as TemptGoal if present which may be different for each animal
        int priority = 3;
        Animal animal = (Animal) (Object) this;
        for (WrappedGoal goal : this.goalSelector.getAvailableGoals()) {
            if (goal.getGoal() instanceof TemptGoal) {
                priority = goal.getPriority();
                break;
            }
        }

        this.goalSelector.addGoal(priority, new FeedFromTroughGoal(animal));
    }

    @Override
    public void smartfeedingtroughs$claim(@UnknownNullability FeedingBlockEntity trough) {
        this.smartfeedingtroughs$claimedTroughPos = trough.getBlockPos();
    }

    @Override
    public void smartfeedingtroughs$releaseClaim() {
        this.smartfeedingtroughs$claimedTroughPos = null;
    }

    @Override
    public BlockPos smartfeedingtroughs$getClaimedTroughPos() {
        return this.smartfeedingtroughs$claimedTroughPos;
    }

    @Override
    public FeedingBlockEntity smartfeedingtroughs$getClaimedTrough(Level level) {
        if (this.smartfeedingtroughs$claimedTroughPos == null) {
            return null;
        }

        if (level.getBlockEntity(this.smartfeedingtroughs$claimedTroughPos) instanceof FeedingBlockEntity trough) {
            return trough;
        }

        return null;
    }

    @Override
    public boolean smartfeedingtroughs$isClaimed() {
        return this.smartfeedingtroughs$claimedTroughPos != null;
    }
}
