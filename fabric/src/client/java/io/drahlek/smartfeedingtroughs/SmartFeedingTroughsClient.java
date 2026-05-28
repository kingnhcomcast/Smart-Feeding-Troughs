package io.drahlek.smartfeedingtroughs;

import io.drahlek.dirigo.registrars.BlockEntityRegistrar;
import io.drahlek.smartfeedingtroughs.blocks.SmartFeedingTroughBlock;
import io.drahlek.smartfeedingtroughs.blocks.SmartFeedingTroughMenu;
import io.drahlek.smartfeedingtroughs.blocks.SmartFeedingTroughScreen;
import io.drahlek.smartfeedingtroughs.blocks.entity.SmartFeedingTroughBlockEntity;
import io.drahlek.smartfeedingtroughs.client.renderer.SmartFeedingTroughRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class SmartFeedingTroughsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Constants.LOG.info("{} Client Initialize", Constants.MOD_NAME);

        MenuScreens.register(SmartFeedingTroughMenu.SMART_TROUGH.get(), SmartFeedingTroughScreen::new);
        BlockEntityRenderers.register(
                BlockEntityRegistrar.get(SmartFeedingTroughBlock.NAME, SmartFeedingTroughBlockEntity.class),
                SmartFeedingTroughRenderer::new
        );
    }

}
