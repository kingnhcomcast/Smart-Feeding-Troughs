package io.drahlek.smartfeedingtroughs;

import com.mojang.brigadier.CommandDispatcher;
import io.drahlek.dirigo.registrars.BlockEntityRegistrar;
import io.drahlek.dirigo.registrars.CommandRegistrar;
import io.drahlek.dirigo.registrars.ItemRegistrar;
import io.drahlek.smartfeedingtroughs.blocks.SmartFeedingTroughMenu;
import io.drahlek.dirigo.registrars.BlockRegistrar;
import io.drahlek.smartfeedingtroughs.config.SmartFeedingTroughConfig;
import net.minecraft.commands.CommandSourceStack;

// This class is part of the common project meaning it is shared between all supported loaders. Code written here can only
// import and access the vanilla codebase, libraries used by vanilla, and optionally third party libraries that provide
// common compatible binaries. This means common code can not directly use loader specific concepts such as Forge events
// however it will be compatible with all supported mod loaders.
public class SmartFeedingTroughCommon {
    public static final String BLOCKS_PACKAGE = Constants.GROUP + ".blocks";
    public static final String BLOCK_ENTITIES_PACKAGE = Constants.GROUP + ".blocks.entity";
    public static final String ITEMS_PACKAGE = Constants.GROUP + ".items";

    // The loader specific projects are able to import and use any code from the common project. This allows you to
    // write the majority of your code here and load it from your loader specific projects. This example has some
    // code that gets invoked by the entry point of the loader specific projects.
    public static void init() {
        Constants.LOG.info("{} Common Initialize", Constants.MOD_NAME);
        SmartFeedingTroughConfig.instance();
        SmartFeedingTroughMenu.register();
        BlockEntityRegistrar.registerBlockEntities(Constants.MOD_ID, BLOCK_ENTITIES_PACKAGE);
        BlockRegistrar.registerBlocks(Constants.MOD_ID, BLOCKS_PACKAGE);
        ItemRegistrar.registerItems(Constants.MOD_ID, ITEMS_PACKAGE);
    }

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        CommandRegistrar.registerCommands(dispatcher, Constants.MOD_ID, Constants.GROUP);
    }
}
