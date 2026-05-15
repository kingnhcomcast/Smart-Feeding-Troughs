package io.drahlek.smartfeedingtroughs.blocks.entity;

import io.drahlek.smartfeedingtroughs.Constants;
import io.drahlek.smartfeedingtroughs.blocks.SmartFeedingTroughBlock;
import io.drahlek.smartfeedingtroughs.platform.Services;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public final class SmartFeedingTroughBlockEntityTypes {
    private static Supplier<BlockEntityType<SmartFeedingTroughBlockEntity>> smartFeedingTrough;

    private SmartFeedingTroughBlockEntityTypes() {
    }

    public static void register() {
        if (smartFeedingTrough != null) {
            return;
        }

        smartFeedingTrough = Services.BLOCK_ENTITY_TYPE_REGISTRAR.registerBlockEntityType(
                Constants.MOD_ID,
                SmartFeedingTroughBlock.NAME,
                SmartFeedingTroughBlockEntity::new
        );
    }

    public static BlockEntityType<SmartFeedingTroughBlockEntity> smartFeedingTrough() {
        if (smartFeedingTrough == null) {
            throw new IllegalStateException("Smart feeding trough block entity type has not been registered");
        }

        return smartFeedingTrough.get();
    }
}
