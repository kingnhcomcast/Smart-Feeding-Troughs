package io.drahlek.smartfeedingtroughs.animal;

import io.drahlek.smartfeedingtroughs.blocks.entity.SmartFeedingTroughBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public interface ISmartTroughClaimedAnimal {
    void smartfeedingtroughs$claim(SmartFeedingTroughBlockEntity trough);

    void smartfeedingtroughs$releaseClaim();

    @Nullable
    BlockPos smartfeedingtroughs$getClaimedTroughPos();

    @Nullable
    SmartFeedingTroughBlockEntity smartfeedingtroughs$getClaimedTrough(Level level);

    void smartfeedingtroughs$playEatingSound();

    boolean smartfeedingtroughs$isClaimed();
}
