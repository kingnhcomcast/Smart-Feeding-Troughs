package io.drahlek.smartfeedingtroughs.client;

import io.drahlek.dirigo.registrars.BlockEntityRegistrar;
import io.drahlek.smartfeedingtroughs.Constants;
import io.drahlek.smartfeedingtroughs.blocks.SmartFeedingTroughBlock;
import io.drahlek.smartfeedingtroughs.blocks.SmartFeedingTroughMenu;
import io.drahlek.smartfeedingtroughs.blocks.SmartFeedingTroughScreen;
import io.drahlek.smartfeedingtroughs.blocks.entity.SmartFeedingTroughBlockEntity;
import io.drahlek.smartfeedingtroughs.client.renderer.SmartFeedingTroughRenderer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public final class SmartFeedingTroughsClient {
    private SmartFeedingTroughsClient() {
    }

    public static void init(IEventBus modBus) {
        // Perform logic in that should only be executed on the physical client
        Constants.LOG.info("{} Client Initialize", Constants.MOD_NAME);
        modBus.addListener(SmartFeedingTroughsClient::registerMenuScreens);
        modBus.addListener(SmartFeedingTroughsClient::registerRenderers);
    }

    private static void registerMenuScreens(FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(SmartFeedingTroughMenu.SMART_TROUGH.get(), SmartFeedingTroughScreen::new));
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                BlockEntityRegistrar.get(SmartFeedingTroughBlock.NAME, SmartFeedingTroughBlockEntity.class),
                SmartFeedingTroughRenderer::new
        );
    }
}
