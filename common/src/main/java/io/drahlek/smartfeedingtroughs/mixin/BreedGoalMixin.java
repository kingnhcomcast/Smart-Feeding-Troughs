package io.drahlek.smartfeedingtroughs.mixin;

import io.drahlek.smartfeedingtroughs.animal.ISmartTroughClaimedAnimal;
import io.drahlek.smartfeedingtroughs.blocks.entity.SmartFeedingTroughBlockEntity;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BreedGoal.class)
public class BreedGoalMixin {
    @Shadow
    protected Animal animal;

    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    private void smartfeedingtroughs$canUse(CallbackInfoReturnable<Boolean> cir) {
        canUseCheck(cir);
    }

    @Inject(method = "canContinueToUse", at = @At("HEAD"), cancellable = true)
    private void smartfeedingtroughs$canContinueToUse(CallbackInfoReturnable<Boolean> cir) {
        canUseCheck(cir);
    }


    private void canUseCheck(CallbackInfoReturnable<Boolean> cir) {
        if (!(this.animal instanceof ISmartTroughClaimedAnimal troughAnimal)) {
            return;
        }

        SmartFeedingTroughBlockEntity trough = troughAnimal.smartfeedingtroughs$getClaimedTrough(this.animal.level());
        if (trough != null) {
            trough.feedCheck(this.animal.level());
            if (trough.isAtMaxCapacity()) {
                cir.setReturnValue(false);
            }
        }
    }
}
