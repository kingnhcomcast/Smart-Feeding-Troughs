package io.drahlek.smartfeedingtroughs.blocks.entity;

import com.google.common.collect.Lists;
import io.drahlek.smartfeedingtroughs.animal.ISmartTroughClaimedAnimal;
import io.drahlek.smartfeedingtroughs.Constants;
import io.drahlek.smartfeedingtroughs.blocks.SmartFeedingTroughBlock;
import io.drahlek.smartfeedingtroughs.blocks.SmartFeedingTroughMenu;
import io.drahlek.smartfeedingtroughs.config.SmartFeedingTroughConfig;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SmartFeedingTroughBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    private static final int[] SLOTS = {0, 1, 2, 3};
    private NonNullList<ItemStack> items = NonNullList.withSize(SmartFeedingTroughBlock.SLOT_COUNT, ItemStack.EMPTY);
    private List<Animal> animals = Lists.newArrayList();

    public SmartFeedingTroughBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(SmartFeedingTroughBlockEntityTypes.smartFeedingTrough(), blockPos, blockState);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, SmartFeedingTroughBlockEntity trough) {
        if (level.isClientSide()) {
            return;
        }

        int feedcheckInterval = Math.max(1, SmartFeedingTroughConfig.data().getTroughClaimCheckInterval());
        if (level.getGameTime() % feedcheckInterval != 0) {
            return;
        }

        trough.feedCheck(level);
    }

    private void feedCheck(Level level) {
        Constants.LOG.debug("Feed check started");

        //verify claimed animals
        verifyClaimedAnimals();

        //if trough is empty or we are at max cap, release claim on all animals
        if(isEmpty()) {
            Constants.LOG.debug("Trough is empty");
            return;
        }

        //check max capacity and exit to avoid needless computation
        if (isAtMaxCapacity()) {
            Constants.LOG.debug("Trough is at max capacity");
            return;
        }

        //locate animals
        claimAnimals(level);
    }

    public boolean isAtMaxCapacity() {
        if(animals.size() >= SmartFeedingTroughConfig.data().getMaxClaimedAnimals()) {
            return true;
        }
        return false;
    }

    public void feedAnimal(Animal animal) {
        if (!animals.contains(animal)
                || !(animal instanceof ISmartTroughClaimedAnimal claimedAnimal)
                || animal.getAge() != 0
                || !animal.canFallInLove()) {
            return;
        }

        if (!this.worldPosition.equals(claimedAnimal.smartfeedingtroughs$getClaimedTroughPos())) {
            animals.remove(animal);
            return;
        }

        if (animal.distanceToSqr(Vec3.atCenterOf(this.worldPosition)) > 4.0D) {
            return;
        }

        ItemStack consumedFood = consumeFoodFor(animal);
        if (!consumedFood.isEmpty()) {
            Constants.LOG.debug("Feeding {}({})", animal.getName().getString(), animal.getId());
            animal.setInLove(null);
            animal.playSound(animal.getEatingSound(consumedFood), 1.0F, 1.0F);
        }
    }

    public boolean hasFeedingFoodFor(Animal animal) {
        for (ItemStack item : this.items) {
            if (!item.isEmpty() && animal.isFood(item)) {
                return true;
            }
        }

        return false;
    }

    public void releaseAnimal(Animal animal) {
        if (animal instanceof ISmartTroughClaimedAnimal claimedAnimal
                && this.worldPosition.equals(claimedAnimal.smartfeedingtroughs$getClaimedTroughPos())) {
            claimedAnimal.smartfeedingtroughs$releaseClaim();
        }

        animals.remove(animal);
    }

    public void releaseAllAnimals() {
        for (Animal animal : animals) {
            if (animal instanceof ISmartTroughClaimedAnimal claimedAnimal
                    && this.worldPosition.equals(claimedAnimal.smartfeedingtroughs$getClaimedTroughPos())) {
                claimedAnimal.smartfeedingtroughs$releaseClaim();
            }
        }
        animals.clear();
    }

    @Override
    public void setRemoved() {
        releaseAllAnimals();
        super.setRemoved();
    }

    private ItemStack consumeFoodFor(Animal animal) {
        for (int slot = 0; slot < items.size(); slot++) {
            ItemStack stack = items.get(slot);

            if (!stack.isEmpty() && animal.isFood(stack)) {
                return this.removeItem(slot, 1);
            }
        }

        return ItemStack.EMPTY;

    }

    private void verifyClaimedAnimals() {
        int range = SmartFeedingTroughConfig.data().getRange();
        AABB claimArea = new AABB(this.worldPosition).inflate(range);

        //release animal if they have been killed, or if we no longer have any food
        animals.removeIf(animal -> {
            if (!(animal instanceof ISmartTroughClaimedAnimal claimedAnimal)) {
                return true;
            }

            BlockPos claimedTroughPos = claimedAnimal.smartfeedingtroughs$getClaimedTroughPos();
            boolean claimedByThisTrough = this.worldPosition.equals(claimedTroughPos);
            boolean remove = !animal.isAlive()
                    || !claimArea.contains(animal.position())
                    || !hasFeedingFoodFor(animal)
                    || !claimedByThisTrough;

            if (remove && claimedByThisTrough) {
                claimedAnimal.smartfeedingtroughs$releaseClaim();
            }

            return remove;
        });
        Constants.LOG.debug("Claimed animals size {}", animals.size());
    }


    /**
     *
     *         in range
     *         can path to trough
     *         not yet claimed by another trough
     *         adult
     *         */
    private void claimAnimals(Level level) {
        int range = SmartFeedingTroughConfig.data().getRange();
        int maxAnimals = SmartFeedingTroughConfig.data().getMaxClaimedAnimals();
        AABB area = new AABB(this.worldPosition).inflate(range);

        //get all animals that are in range
        for (Animal animal : level.getEntitiesOfClass(Animal.class, area, this::canClaim)) {
            if (animal instanceof ISmartTroughClaimedAnimal claimedAnimal && !claimedAnimal.smartfeedingtroughs$isClaimed()) {
                Constants.LOG.debug("Claimed animal {}({})", animal.getDisplayName().getString(), animal.getId());
                animals.add(animal);
                claimedAnimal.smartfeedingtroughs$claim(this);
                if (animals.size() >= maxAnimals) {
                    break;
                }
            }
        }
    }

    private boolean canClaim(Animal animal) {
        if (animal instanceof ISmartTroughClaimedAnimal claimedAnimal) {
            return hasFeedingFoodFor(animal) &&
                    !claimedAnimal.smartfeedingtroughs$isClaimed() &&
                    canPathToTrough(animal);
        }
        return false;
    }

    private boolean canPathToTrough(Animal animal) {
        Path path = animal.getNavigation().createPath(this.worldPosition, 0);
        return path != null && path.canReach();
    }

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
    public void clearContent() {
        this.items.clear();
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput components) {
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

    public boolean isMateAvailable(Animal animal) {
        for(Animal mate : animals) {
            if (mate != animal
                    && mate.getClass() == animal.getClass()
                    && animal.getAge() == 0
                    && animal.canFallInLove()) {
                return true;
            }
        }
        return false;
    }
}
