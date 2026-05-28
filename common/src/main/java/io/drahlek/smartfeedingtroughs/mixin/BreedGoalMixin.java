package io.drahlek.smartfeedingtroughs.mixin;

import io.drahlek.smartfeedingtroughs.animal.ISmartTroughClaimedAnimal;
import io.drahlek.smartfeedingtroughs.blocks.entity.FeedingBlockEntity;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.animal.Animal;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BreedGoal.class)
public class BreedGoalMixin {
    @Shadow
    protected Animal animal;

    @Shadow
    @Nullable
    protected Animal partner;

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ai/goal/BreedGoal;breed()V",
                    shift = At.Shift.AFTER
            )
    )
    private void smartfeedingtroughs$afterBreed(CallbackInfo ci) {
        completeBirthReservation();
    }

    private void completeBirthReservation() {
        if (!(this.animal instanceof ISmartTroughClaimedAnimal troughAnimal) || this.partner == null) {
            return;
        }

        FeedingBlockEntity trough = troughAnimal.smartfeedingtroughs$getClaimedTrough(this.animal.level());
        if (trough != null) {
            trough.completeBirthReservation();
        }
    }
}
