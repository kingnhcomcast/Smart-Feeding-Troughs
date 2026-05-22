package io.drahlek.smartfeedingtroughs.blocks.entity;

import io.drahlek.dirigo.annotation.BlockEntity;
import io.drahlek.dirigo.registrars.BlockEntityRegistrar;
import io.drahlek.smartfeedingtroughs.blocks.SmartFeedingTroughBlock;
import io.drahlek.smartfeedingtroughs.blocks.SmartFeedingTroughMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@BlockEntity(id = SmartFeedingTroughBlock.NAME)
public class SmartFeedingTroughBlockEntity extends FeedingBlockEntity implements WorldlyContainer, MenuProvider {
    private static final int[] SLOTS = {0, 1, 2, 3};
    private NonNullList<ItemStack> items = NonNullList.withSize(SmartFeedingTroughBlock.SLOT_COUNT, ItemStack.EMPTY);

    public SmartFeedingTroughBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityRegistrar.get(SmartFeedingTroughBlock.NAME, SmartFeedingTroughBlockEntity.class), blockPos, blockState);
    }

    /////////////////////////////////////////////////////
    //// FeedingBlockEntity Overrides
    @Override
    public boolean isFoodAvailable() {
        return !isEmpty();
    }

    @Override
    public boolean hasFeedingFoodFor(Animal animal) {
        for (ItemStack item : this.items) {
            if (!item.isEmpty() && animal.isFood(item)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected ItemStack consumeFoodFor(Animal animal) {
        for (int slot = 0; slot < items.size(); slot++) {
            ItemStack stack = items.get(slot);

            if (!stack.isEmpty() && animal.isFood(stack)) {
                return this.removeItem(slot, 1);
            }
        }

        return ItemStack.EMPTY;
    }


    /////////////////////////////////////////////////////
    //// BlockEntity Overrides

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, true, registries);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    protected void applyImplicitComponents(net.minecraft.world.level.block.entity.BlockEntity.DataComponentInput components) {
        super.applyImplicitComponents(components);
        components.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(this.items);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.items));
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        tag.remove("Items");
    }



    /////////////////////////////////////////////////////
    //// WorldlyContainer Overrides
    @Override
    public int getContainerSize() {
        return SmartFeedingTroughBlock.SLOT_COUNT;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack item : this.items) {
            if (!item.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        ItemStack result = ContainerHelper.removeItem(this.items, slot, count);
        if (!result.isEmpty()) {
            this.setChanged();
        }

        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack itemStack) {
        this.items.set(slot, itemStack);
        itemStack.limitSize(this.getMaxStackSize(itemStack));
        this.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public int [] getSlotsForFace(Direction direction) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int i, ItemStack itemStack, Direction direction) {
        return direction != Direction.DOWN;
    }

    @Override
    public boolean canTakeItemThroughFace(int i, ItemStack itemStack, Direction direction) {
        return false;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.smartfeedingtroughs.smart_trough");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new SmartFeedingTroughMenu(containerId, inventory, this);
    }
}
