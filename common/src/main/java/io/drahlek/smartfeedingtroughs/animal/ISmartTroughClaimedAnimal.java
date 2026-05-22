package io.drahlek.smartfeedingtroughs.animal;

import io.drahlek.smartfeedingtroughs.blocks.entity.FeedingBlockEntity;
import io.drahlek.smartfeedingtroughs.blocks.entity.SmartFeedingTroughBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface ISmartTroughClaimedAnimal {
    void smartfeedingtroughs$claim(FeedingBlockEntity trough);

    void smartfeedingtroughs$releaseClaim();

    BlockPos smartfeedingtroughs$getClaimedTroughPos();

    FeedingBlockEntity smartfeedingtroughs$getClaimedTrough(Level level);

    boolean smartfeedingtroughs$isClaimed();
}
