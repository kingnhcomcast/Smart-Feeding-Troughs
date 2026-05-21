package io.drahlek.smartfeedingtroughs.animal;

import io.drahlek.smartfeedingtroughs.blocks.entity.FeedingBlockEntity;
import io.drahlek.smartfeedingtroughs.blocks.entity.SmartFeedingTroughBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public interface ISmartTroughClaimedAnimal {
    void smartfeedingtroughs$claim(FeedingBlockEntity trough);

    void smartfeedingtroughs$releaseClaim();

    @Nullable
    BlockPos smartfeedingtroughs$getClaimedTroughPos();

    @Nullable
    FeedingBlockEntity smartfeedingtroughs$getClaimedTrough(Level level);

    void smartfeedingtroughs$playEatingSound();

    boolean smartfeedingtroughs$isClaimed();
}
