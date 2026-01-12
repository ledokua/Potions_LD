package net.ledok.potions_ld.registry;

import net.ledok.potions_ld.PotionsLdMod;
import net.ledok.potions_ld.recipe.PotionBrewingRecipe;
import net.ledok.potions_ld.recipe.PotionBrewingRecipeSerializer;
import net.ledok.potions_ld.recipe.PotionBrewingRecipeType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;

public class RecipeRegistry {
    public static void initialize() {
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, ResourceLocation.fromNamespaceAndPath(PotionsLdMod.MOD_ID, PotionBrewingRecipeSerializer.ID), PotionBrewingRecipeSerializer.INSTANCE);
        
        PotionBrewingRecipeType.INSTANCE = Registry.register(
                BuiltInRegistries.RECIPE_TYPE,
                ResourceLocation.fromNamespaceAndPath(PotionsLdMod.MOD_ID, PotionBrewingRecipeType.ID),
                new RecipeType<PotionBrewingRecipe>() {
                    @Override
                    public String toString() {
                        return PotionBrewingRecipeType.ID;
                    }
                }
        );
    }
}
