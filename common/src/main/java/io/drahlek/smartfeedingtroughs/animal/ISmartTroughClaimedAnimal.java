package io.drahlek.smartfeedingtroughs.animal;

import io.drahlek.smartfeedingtroughs.blocks.entity.FeedingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface ISmartTroughClaimedAnimal {
    void smartfeedingtroughs$claim(FeedingBlockEntity trough, BlockPos feedingPos);

    void smartfeedingtroughs$releaseClaim();

    @Nullable
    BlockPos smartfeedingtroughs$getClaimedTroughPos();

    @Nullable
    BlockPos smartfeedingtroughs$getClaimedFeedingPos();

    @Nullable
    FeedingBlockEntity smartfeedingtroughs$getClaimedTrough(Level level);

    boolean smartfeedingtroughs$isClaimed();
}
