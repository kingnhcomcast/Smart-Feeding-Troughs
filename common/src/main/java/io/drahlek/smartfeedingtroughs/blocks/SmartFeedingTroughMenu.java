package io.drahlek.smartfeedingtroughs.blocks;

import io.drahlek.dirigo.menu.DirigoContainerMenu;
import io.drahlek.dirigo.services.Services;
import io.drahlek.smartfeedingtroughs.Constants;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

public class SmartFeedingTroughMenu extends DirigoContainerMenu {
    public static final Supplier<MenuType<SmartFeedingTroughMenu>> SMART_TROUGH = Services.MENU_TYPE_REGISTRAR.registerMenuType(
            Constants.MOD_ID,
            SmartFeedingTroughBlock.NAME,
            SmartFeedingTroughMenu::new
    );

    // Client-side constructor
    public SmartFeedingTroughMenu(final int containerId, final Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(SmartFeedingTroughBlock.SLOT_COUNT));
    }

    // Server-side constructor
    public SmartFeedingTroughMenu(final int containerId, final Inventory inventory, final Container container) {
        super(SMART_TROUGH.get(), containerId, container, SmartFeedingTroughBlock.SLOT_COUNT);

        this.addContainerRowSlots(SmartFeedingTroughBlock.SLOT_COUNT, 53, 20);
        this.addStandardInventorySlots(inventory, 8, 51);
    }

    public static void register() {
        // Intentionally empty; calling this forces class initialization and platform registration.
    }
}
