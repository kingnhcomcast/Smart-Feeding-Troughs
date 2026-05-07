package io.drahlek.smartbreedingtroughs;

import io.drahlek.dirigo.registrars.ItemRegistrar;
import io.drahlek.smartbreedingtroughs.blocks.entity.SmartBreedingTroughBlockEntityTypes;
import io.drahlek.dirigo.registrars.BlockRegistrar;

// This class is part of the common project meaning it is shared between all supported loaders. Code written here can only
// import and access the vanilla codebase, libraries used by vanilla, and optionally third party libraries that provide
// common compatible binaries. This means common code can not directly use loader specific concepts such as Forge events
// however it will be compatible with all supported mod loaders.
public class SmartBreedingTroughCommon {
    public static final String BLOCKS_PACKAGE = Constants.GROUP + ".blocks";
    public static final String ITEMS_PACKAGE = Constants.GROUP + ".items";

    // The loader specific projects are able to import and use any code from the common project. This allows you to
    // write the majority of your code here and load it from your loader specific projects. This example has some
    // code that gets invoked by the entry point of the loader specific projects.
    public static void init() {
        Constants.LOG.info("{} Common Initialize", Constants.MOD_NAME);
        //SmartBreedingTroughConfig.instance();
        SmartBreedingTroughBlockEntityTypes.register();
        BlockRegistrar.registerBlocks(Constants.MOD_ID, BLOCKS_PACKAGE);
        ItemRegistrar.registerItems(Constants.MOD_ID, ITEMS_PACKAGE);
    }
}
