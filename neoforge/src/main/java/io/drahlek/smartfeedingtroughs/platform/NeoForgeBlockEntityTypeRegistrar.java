package io.drahlek.smartfeedingtroughs.platform;

import io.drahlek.smartfeedingtroughs.platform.services.IBlockEntityTypeRegistrar;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class NeoForgeBlockEntityTypeRegistrar implements IBlockEntityTypeRegistrar {
    private static final Map<String, DeferredRegister<BlockEntityType<?>>> REGISTRIES = new HashMap<>();
    private static final Set<String> INITIALIZED_MODS = new HashSet<>();

    public void initialize(IEventBus eventBus, String modId) {
        if (INITIALIZED_MODS.add(modId)) {
            getOrCreateRegistry(modId).register(eventBus);
        }
    }

    @Override
    public <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntityType(
            String modId,
            String name,
            BlockEntityFactory<T> factory
    ) {
        DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> holder = getOrCreateRegistry(modId)
                .register(name, () -> {
                    Block block = BuiltInRegistries.BLOCK.get(
                            ResourceLocation.fromNamespaceAndPath(modId, name)
                    );

                    return createType(factory, block);
                });
        return holder::get;
    }

    private static DeferredRegister<BlockEntityType<?>> getOrCreateRegistry(String modId) {
        return REGISTRIES.computeIfAbsent(modId, id -> DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, id));
    }

    private static <T extends BlockEntity> BlockEntityType<T> createType(
            BlockEntityFactory<T> factory,
            net.minecraft.world.level.block.Block... validBlocks
    ) {
        return BlockEntityType.Builder.of(
                (pos, state) -> factory.create(pos, state),
                validBlocks
        ).build(null);
    }
}
