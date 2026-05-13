package io.drahlek.smartbreedingtroughs.mixin;

import io.drahlek.smartbreedingtroughs.animal.ISmartTroughClaimedAnimal;
import io.drahlek.smartbreedingtroughs.blocks.entity.SmartBreedingTroughBlockEntity;
import io.drahlek.smartbreedingtroughs.goals.FeedFromTroughGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Animal.class)
public abstract class AnimalMixin extends AgeableMob implements ISmartTroughClaimedAnimal {
    @Unique
    private BlockPos smartbreedingtroughs$claimedTroughPos;

    protected AnimalMixin(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);
    }

    @Invoker("playEatingSound")
    @Override
    public abstract void smartbreedingtroughs$playEatingSound();

    @Inject(method = "<init>", at = @At("TAIL"))
    private void smartbreedingtroughs$addTroughGoal(
            EntityType<? extends Animal> entityType,
            Level level,
            CallbackInfo ci
    ) {
        Animal animal = (Animal) (Object) this;
        // TODO dynamically determine priority.
        this.goalSelector.addGoal(3, new FeedFromTroughGoal(animal));
    }

    @Override
    public void smartbreedingtroughs$claim(SmartBreedingTroughBlockEntity trough) {
        this.smartbreedingtroughs$claimedTroughPos = trough.getBlockPos();
    }

    @Override
    public void smartbreedingtroughs$releaseClaim() {
        this.smartbreedingtroughs$claimedTroughPos = null;
    }

    @Override
    public @Nullable BlockPos smartbreedingtroughs$getClaimedTroughPos() {
        return this.smartbreedingtroughs$claimedTroughPos;
    }

    @Override
    public @Nullable SmartBreedingTroughBlockEntity smartbreedingtroughs$getClaimedTrough(Level level) {
        if (this.smartbreedingtroughs$claimedTroughPos == null) {
            return null;
        }

        if (level.getBlockEntity(this.smartbreedingtroughs$claimedTroughPos) instanceof SmartBreedingTroughBlockEntity trough) {
            return trough;
        }

        return null;
    }

    @Override
    public boolean smartbreedingtroughs$isClaimed() {
        return this.smartbreedingtroughs$claimedTroughPos != null;
    }
}
