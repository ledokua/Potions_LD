package net.ledok.potions_ld.blocks.entity;

import net.ledok.potions_ld.items.CauldronUpgradeItem;
import net.ledok.potions_ld.registry.ItemRegistry;
import net.ledok.potions_ld.recipe.CountedIngredient;
import net.ledok.potions_ld.recipe.PotionBrewingRecipe;
import net.ledok.potions_ld.recipe.PotionBrewingRecipeType;
import net.ledok.potions_ld.screen.PotionCauldronScreenHandler;
import net.ledok.potions_ld.util.ImplementedInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PotionCauldronBlockEntity extends BlockEntity implements MenuProvider, ImplementedInventory {
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(8, ItemStack.EMPTY);
    private static final int INPUT_SLOT_1 = 0;
    private static final int INPUT_SLOT_2 = 1;
    private static final int INPUT_SLOT_3 = 2;
    private static final int INPUT_SLOT_4 = 3;
    private static final int OUTPUT_SLOT = 4;
    private static final int UPGRADE_SLOT_1 = 5;
    private static final int UPGRADE_SLOT_2 = 6;
    private static final int UPGRADE_SLOT_3 = 7;
    
    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 100;
    private RecipeHolder<PotionBrewingRecipe> activeRecipe = null;

    public PotionCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityTypeRegistry.POTION_CAULDRON, pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> PotionCauldronBlockEntity.this.progress;
                    case 1 -> PotionCauldronBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> PotionCauldronBlockEntity.this.progress = value;
                    case 1 -> PotionCauldronBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return ImplementedInventory.super.getItem(slot);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot >= INPUT_SLOT_1 && slot <= INPUT_SLOT_4) {
            return true;
        } else if (slot == OUTPUT_SLOT) {
            return false;
        } else if (slot >= UPGRADE_SLOT_1 && slot <= UPGRADE_SLOT_3) {
            return stack.getItem() instanceof CauldronUpgradeItem && !hasUpgrade(stack.getItem());
        }
        return false;
    }

    private boolean hasUpgrade(Item item) {
        for (int i = UPGRADE_SLOT_1; i <= UPGRADE_SLOT_3; i++) {
            if (inventory.get(i).getItem() == item) {
                return true;
            }
        }
        return false;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.potions_ld.potion_cauldron");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new PotionCauldronScreenHandler(syncId, playerInventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, inventory, registries);
        tag.putInt("potion_cauldron.progress", progress);
        if (activeRecipe != null) {
            tag.putString("potion_cauldron.active_recipe", activeRecipe.id().toString());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ContainerHelper.loadAllItems(tag, inventory, registries);
        progress = tag.getInt("potion_cauldron.progress");
        if (tag.contains("potion_cauldron.active_recipe") && this.level != null) {
            ResourceLocation recipeId = ResourceLocation.parse(tag.getString("potion_cauldron.active_recipe"));
            this.level.getRecipeManager().byKey(recipeId).ifPresent(recipe -> {
                if (recipe.value() instanceof PotionBrewingRecipe) {
                    this.activeRecipe = (RecipeHolder<PotionBrewingRecipe>) recipe;
                }
            });
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PotionCauldronBlockEntity entity) {
        if (level == null || level.isClientSide) {
            return;
        }

        if (entity.progress > 0) {
            if (entity.activeRecipe == null) {
                entity.resetProgress();
                setChanged(level, pos, state);
                return;
            }
            entity.progress++;
            if (entity.progress >= entity.maxProgress) {
                craftItem(entity, entity.activeRecipe);
                entity.resetProgress();
            }
        } else {
            Optional<RecipeHolder<PotionBrewingRecipe>> recipe = findRecipe(entity);
            if (recipe.isPresent() && canCraft(entity, recipe.get())) {
                entity.activeRecipe = recipe.get();
                consumeIngredients(entity, entity.activeRecipe);
                entity.progress = 1;
                
                int baseTime = entity.activeRecipe.value().cookingTime();
                if (entity.hasUpgrade(ItemRegistry.SPEED_UPGRADE)) {
                    entity.maxProgress = Math.max(1, Math.round(baseTime * 0.7f));
                } else {
                    entity.maxProgress = baseTime;
                }
            }
        }
        setChanged(level, pos, state);
    }

    private void resetProgress() {
        this.progress = 0;
        this.activeRecipe = null;
    }

    private static Optional<RecipeHolder<PotionBrewingRecipe>> findRecipe(PotionCauldronBlockEntity entity) {
        if (entity.level == null) return Optional.empty();
        for (RecipeHolder<PotionBrewingRecipe> recipeHolder : entity.level.getRecipeManager().getAllRecipesFor(PotionBrewingRecipeType.INSTANCE)) {
            if (matches(recipeHolder.value(), entity)) {
                return Optional.of(recipeHolder);
            }
        }
        return Optional.empty();
    }
    
    private static boolean matches(PotionBrewingRecipe recipe, PotionCauldronBlockEntity entity) {
        NonNullList<ItemStack> availableItems = NonNullList.create();
        for (int i = INPUT_SLOT_1; i <= INPUT_SLOT_4; i++) {
            availableItems.add(entity.getItem(i).copy());
        }

        for (CountedIngredient countedIngredient : recipe.ingredients()) {
            int requiredCount = countedIngredient.count();
            if (entity.hasUpgrade(ItemRegistry.EFFICIENCY_UPGRADE)) {
                requiredCount = Math.max(1, Math.round(requiredCount * 0.5f));
            }

            boolean satisfied = false;
            for (ItemStack stackInSlot : availableItems) {
                if (countedIngredient.ingredient().test(stackInSlot)) {
                    int available = stackInSlot.getCount();
                    int needed = Math.min(requiredCount, available);
                    stackInSlot.shrink(needed);
                    requiredCount -= needed;
                    if (requiredCount <= 0) {
                        satisfied = true;
                        break;
                    }
                }
            }
            if (!satisfied) {
                return false;
            }
        }
        return true;
    }

    private static boolean canCraft(PotionCauldronBlockEntity entity, RecipeHolder<PotionBrewingRecipe> recipe) {
        if (entity.level == null) return false;
        ItemStack resultItem = recipe.value().getResultItem(entity.level.registryAccess());
        if (resultItem.isEmpty()) return false;

        ItemStack outputStack = entity.getItem(OUTPUT_SLOT);
        if (outputStack.isEmpty()) return true;
        
        if (!ItemStack.isSameItemSameComponents(outputStack, resultItem)) return false;
        
        return outputStack.getCount() + resultItem.getCount() <= outputStack.getMaxStackSize();
    }

    private static void consumeIngredients(PotionCauldronBlockEntity entity, RecipeHolder<PotionBrewingRecipe> recipe) {
        for (CountedIngredient countedIngredient : recipe.value().ingredients()) {
            int ingredientCost = countedIngredient.count();
            if (entity.hasUpgrade(ItemRegistry.EFFICIENCY_UPGRADE)) {
                ingredientCost = Math.max(1, Math.round(ingredientCost * 0.5f));
            }
            
            for (int i = INPUT_SLOT_1; i <= INPUT_SLOT_4; i++) {
                ItemStack stackInSlot = entity.getItem(i);
                if (countedIngredient.ingredient().test(stackInSlot)) {
                    int toRemove = Math.min(ingredientCost, stackInSlot.getCount());
                    stackInSlot.shrink(toRemove);
                    ingredientCost -= toRemove;
                    if (ingredientCost <= 0) {
                        break;
                    }
                }
            }
        }
    }

    private static void craftItem(PotionCauldronBlockEntity entity, RecipeHolder<PotionBrewingRecipe> recipe) {
        if (entity.level == null) return;
        ItemStack resultItem = recipe.value().getResultItem(entity.level.registryAccess());
        int outputAmount = resultItem.getCount();
        if (entity.hasUpgrade(ItemRegistry.FORTUNE_UPGRADE) && entity.level.random.nextFloat() < 0.1f) {
            outputAmount++;
        }
        
        ItemStack outputStack = entity.getItem(OUTPUT_SLOT);
        if (outputStack.isEmpty()) {
            entity.setItem(OUTPUT_SLOT, new ItemStack(resultItem.getItem(), outputAmount));
        } else {
            outputStack.grow(outputAmount);
        }
    }
}
