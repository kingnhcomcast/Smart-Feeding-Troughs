package io.drahlek.smartfeedingtroughs.items;

import io.drahlek.dirigo.annotation.Item;
import io.drahlek.dirigo.registrars.BlockRegistrar;
import io.drahlek.smartfeedingtroughs.blocks.SmartFeedingTroughBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

@Item(id = SmartFeedingTroughBlock.NAME, creativeTab = "functional_blocks")
public class SmartFeedingTroughItem extends BlockItem {

    public SmartFeedingTroughItem(Properties properties) {
        super(BlockRegistrar.blocks.get(SmartFeedingTroughBlock.NAME).get(), properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.smartfeedingtroughs.smart_trough"));
    }
}
