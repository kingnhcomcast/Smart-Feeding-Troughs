package io.drahlek.smartfeedingtroughs.items;

import io.drahlek.dirigo.annotation.Item;
import io.drahlek.dirigo.registrars.BlockRegistrar;
import io.drahlek.smartfeedingtroughs.blocks.EdibleHayBlock;
import net.minecraft.world.item.BlockItem;

@Item(id = EdibleHayBlock.NAME, creativeTab = "functional_blocks")
public class EdibleHayBlockItem extends BlockItem {
    public EdibleHayBlockItem(Properties properties) {
        super(BlockRegistrar.blocks.get(EdibleHayBlock.NAME).get(), properties);
    }
}
