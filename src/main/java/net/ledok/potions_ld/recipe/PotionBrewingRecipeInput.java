package net.ledok.potions_ld.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record PotionBrewingRecipeInput(ItemStack input) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        if (index == 0) {
            return input;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 1;
    }
}
