package net.ledok.potions_ld.screen;

import net.ledok.potions_ld.items.CauldronUpgradeItem;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class PotionCauldronScreenHandler extends AbstractContainerMenu {
    private final Container inventory;
    private final ContainerData data;
    
    private static final int INPUT_START = 0;
    private static final int INPUT_END = 3;
    private static final int OUTPUT_SLOT = 4;
    private static final int UPGRADE_START = 5;
    private static final int UPGRADE_END = 7;

    public PotionCauldronScreenHandler(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new SimpleContainer(8), new SimpleContainerData(2));
    }

    public PotionCauldronScreenHandler(int syncId, Inventory playerInventory, Container inventory, ContainerData data) {
        super(ScreenHandlerTypeRegistry.POTION_CAULDRON, syncId);
        checkContainerSize(inventory, 8);
        this.inventory = inventory;
        this.data = data;
        inventory.startOpen(playerInventory.player);
        addDataSlots(data);

        // Input Slots (2x2 grid)
        this.addSlot(new Slot(inventory, 0, 30, 22));
        this.addSlot(new Slot(inventory, 1, 48, 22));
        this.addSlot(new Slot(inventory, 2, 30, 40));
        this.addSlot(new Slot(inventory, 3, 48, 40));
        
        // Output Slot
        this.addSlot(new Slot(inventory, OUTPUT_SLOT, 116, 31) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        
        // Upgrade Slots
        this.addSlot(new Slot(inventory, 5, 152, 11) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof CauldronUpgradeItem;
            }
        });
        this.addSlot(new Slot(inventory, 6, 152, 31) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof CauldronUpgradeItem;
            }
        });
        this.addSlot(new Slot(inventory, 7, 152, 51) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof CauldronUpgradeItem;
            }
        });

        // Player Inventory
        for (int m = 0; m < 3; ++m) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
            }
        }
        // Player Hotbar
        for (int m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
        }
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int progressArrowSize = 25; // Changed to 25 as requested

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.getContainerSize()) {
                if (!this.moveItemStackTo(originalStack, this.inventory.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // From player inventory to machine
                if (originalStack.getItem() instanceof CauldronUpgradeItem) {
                    if (!this.moveItemStackTo(originalStack, UPGRADE_START, UPGRADE_END + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!this.moveItemStackTo(originalStack, INPUT_START, INPUT_END + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (originalStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return newStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }
}
