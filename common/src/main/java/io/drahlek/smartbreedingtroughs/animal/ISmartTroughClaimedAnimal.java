package io.drahlek.smartbreedingtroughs.animal;

import io.drahlek.smartbreedingtroughs.blocks.entity.SmartBreedingTroughBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public interface ISmartTroughClaimedAnimal {
    void smartbreedingtroughs$claim(SmartBreedingTroughBlockEntity trough);

    void smartbreedingtroughs$releaseClaim();

    @Nullable
    BlockPos smartbreedingtroughs$getClaimedTroughPos();

    @Nullable
    SmartBreedingTroughBlockEntity smartbreedingtroughs$getClaimedTrough(Level level);

    void smartbreedingtroughs$playEatingSound();
}
