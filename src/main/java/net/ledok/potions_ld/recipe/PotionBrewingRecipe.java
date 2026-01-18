package net.ledok.potions_ld.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;

public record PotionBrewingRecipe(List<CountedIngredient> ingredients, ItemStack output, int cookingTime) implements Recipe<PotionBrewingRecipeInput> {

    @Override
    public boolean matches(PotionBrewingRecipeInput inventory, Level level) {
        // The real matching logic is now in the BlockEntity to account for upgrades.
        // This method is now less important for our specific implementation.
        return true;
    }

    @Override
    public ItemStack assemble(PotionBrewingRecipeInput inventory, HolderLookup.Provider registryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registryAccess) {
        return output;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        for (CountedIngredient ci : ingredients) {
            list.add(ci.ingredient());
        }
        return list;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PotionBrewingRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return PotionBrewingRecipeType.INSTANCE;
    }
}
