package io.drahlek.smartfeedingtroughs.items;

import io.drahlek.dirigo.annotation.Item;
import io.drahlek.dirigo.registrars.BlockRegistrar;
import io.drahlek.smartfeedingtroughs.blocks.EdibleHayBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

@Item(id = EdibleHayBlock.NAME, creativeTab = "functional_blocks")
public class EdibleHayBlockItem extends BlockItem {
    public EdibleHayBlockItem(Properties properties) {
        super(BlockRegistrar.blocks.get(EdibleHayBlock.NAME).get(), properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, net.minecraft.world.item.Item.TooltipContext context, TooltipDisplay display, Consumer<Component> textConsumer, TooltipFlag tooltipFlag) {
        textConsumer.accept(Component.translatable("tooltip.smartfeedingtroughs.edible_hay_block"));
    }
}
