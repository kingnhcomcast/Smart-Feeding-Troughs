package io.drahlek.smartfeedingtroughs.client;

import io.drahlek.dirigo.registrars.BlockEntityRegistrar;
import io.drahlek.smartfeedingtroughs.Constants;
import io.drahlek.smartfeedingtroughs.blocks.SmartFeedingTroughBlock;
import io.drahlek.smartfeedingtroughs.blocks.SmartFeedingTroughMenu;
import io.drahlek.smartfeedingtroughs.blocks.SmartFeedingTroughScreen;
import io.drahlek.smartfeedingtroughs.blocks.entity.SmartFeedingTroughBlockEntity;
import io.drahlek.smartfeedingtroughs.client.renderer.SmartFeedingTroughRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import static io.drahlek.smartfeedingtroughs.Constants.MOD_ID;

@Mod(value = MOD_ID, dist = Dist.CLIENT)
public final class SmartFeedingTroughsClient {
    public SmartFeedingTroughsClient(IEventBus modBus) {
        // Perform logic in that should only be executed on the physical client
        Constants.LOG.info("{} Client Initialize", Constants.MOD_NAME);
        modBus.addListener(this::registerMenuScreens);
        modBus.addListener(this::registerRenderers);
    }

    private void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(SmartFeedingTroughMenu.SMART_TROUGH.get(), SmartFeedingTroughScreen::new);
    }

    private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                BlockEntityRegistrar.get(SmartFeedingTroughBlock.NAME, SmartFeedingTroughBlockEntity.class),
                SmartFeedingTroughRenderer::new
        );
    }
}
