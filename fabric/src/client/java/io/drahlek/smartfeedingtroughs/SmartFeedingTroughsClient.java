package io.drahlek.smartfeedingtroughs;

import io.drahlek.smartfeedingtroughs.blocks.SmartFeedingTroughMenu;
import io.drahlek.smartfeedingtroughs.blocks.SmartFeedingTroughScreen;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

public class SmartFeedingTroughsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Constants.LOG.info("{} Client Initialize", Constants.MOD_NAME);

        MenuScreens.register(SmartFeedingTroughMenu.SMART_TROUGH.get(), SmartFeedingTroughScreen::new);
    }

}
