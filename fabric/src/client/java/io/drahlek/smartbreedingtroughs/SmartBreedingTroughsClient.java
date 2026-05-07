package io.drahlek.smartbreedingtroughs;

import io.drahlek.smartbreedingtroughs.blocks.SmartBreedingTroughMenu;
import io.drahlek.smartbreedingtroughs.blocks.SmartBreedingTroughScreen;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

public class SmartBreedingTroughsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Constants.LOG.info("{} Client Initialize", Constants.MOD_NAME);

        MenuScreens.register(SmartBreedingTroughMenu.SMART_TROUGH.get(), SmartBreedingTroughScreen::new);
    }

}
