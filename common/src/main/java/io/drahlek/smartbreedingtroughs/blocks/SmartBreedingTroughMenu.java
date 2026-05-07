package io.drahlek.smartbreedingtroughs.blocks;

import io.drahlek.dirigo.menu.DirigoContainerMenu;
import io.drahlek.dirigo.services.Services;
import io.drahlek.smartbreedingtroughs.Constants;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

public class SmartBreedingTroughMenu extends DirigoContainerMenu {
    public static final Supplier<MenuType<SmartBreedingTroughMenu>> SMART_TROUGH = Services.MENU_TYPE_REGISTRAR.registerMenuType(
            Constants.MOD_ID,
            SmartBreedingTroughBlock.NAME,
            SmartBreedingTroughMenu::new
    );

    // Client-side constructor
    public SmartBreedingTroughMenu(final int containerId, final Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(SmartBreedingTroughBlock.SLOT_COUNT));
    }

    // Server-side constructor
    public SmartBreedingTroughMenu(final int containerId, final Inventory inventory, final Container container) {
        super(SMART_TROUGH.get(), containerId, container, SmartBreedingTroughBlock.SLOT_COUNT);

        this.addContainerRowSlots(SmartBreedingTroughBlock.SLOT_COUNT, 53, 20);
        this.addStandardInventorySlots(inventory, 8, 51);
    }

    public static void register() {
        // Intentionally empty; calling this forces class initialization and platform registration.
    }
}
