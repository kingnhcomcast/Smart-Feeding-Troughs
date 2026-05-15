package io.drahlek.smartfeedingtroughs.platform;

import io.drahlek.smartfeedingtroughs.platform.services.IBlockEntityTypeRegistrar;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class FabricBlockEntityTypeRegistrar implements IBlockEntityTypeRegistrar {
    @Override
    public <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntityType(
            String modId,
            String name,
            BlockEntityFactory<T> factory
    ) {
        BlockEntityType<T> type = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(modId, name),
                FabricBlockEntityTypeBuilder.create(factory::create).build()
        );
        return () -> type;
    }
}
