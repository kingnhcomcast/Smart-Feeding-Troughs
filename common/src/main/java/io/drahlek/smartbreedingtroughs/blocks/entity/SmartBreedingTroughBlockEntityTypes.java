package io.drahlek.smartbreedingtroughs.blocks.entity;

import io.drahlek.smartbreedingtroughs.Constants;
import io.drahlek.smartbreedingtroughs.blocks.SmartBreedingTroughBlock;
import io.drahlek.smartbreedingtroughs.platform.Services;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public final class SmartBreedingTroughBlockEntityTypes {
    private static Supplier<BlockEntityType<SmartBreedingTroughBlockEntity>> smartBreedingTrough;

    private SmartBreedingTroughBlockEntityTypes() {
    }

    public static void register() {
        if (smartBreedingTrough != null) {
            return;
        }

        smartBreedingTrough = Services.BLOCK_ENTITY_TYPE_REGISTRAR.registerBlockEntityType(
                Constants.MOD_ID,
                SmartBreedingTroughBlock.NAME,
                SmartBreedingTroughBlockEntity::new
        );
    }

    public static BlockEntityType<SmartBreedingTroughBlockEntity> smartBreedingTrough() {
        if (smartBreedingTrough == null) {
            throw new IllegalStateException("Smart breeding trough block entity type has not been registered");
        }

        return smartBreedingTrough.get();
    }
}
