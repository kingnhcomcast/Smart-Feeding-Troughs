package io.drahlek.smartfeedingtroughs.blocks;

import io.drahlek.dirigo.menu.DirigoContainerMenu;
import io.drahlek.dirigo.services.Services;
import io.drahlek.smartfeedingtroughs.Constants;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

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
        addInventorySlots(inventory, 8, 51);
    }

    private void addInventorySlots(Inventory inventory, int x, int y) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(inventory, column + row * 9 + 9, x + column * 18, y + row * 18));
            }
        }

        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(inventory, column, x + column * 18, y + 58));
        }
    }

    public static void register() {
        // Intentionally empty; calling this forces class initialization and platform registration.
    }
}
