package net.ledok.potions_ld.compat.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.ledok.potions_ld.PotionsLdMod;
import net.ledok.potions_ld.recipe.PotionBrewingRecipe;
import net.ledok.potions_ld.recipe.PotionBrewingRecipeType;
import net.ledok.potions_ld.registry.BlockRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

public class PotionsLdEmiPlugin implements EmiPlugin {

    public static final EmiRecipeCategory ALCHEMY_TABLE = new EmiRecipeCategory(
            ResourceLocation.fromNamespaceAndPath(PotionsLdMod.MOD_ID, "potion_brewing"),
            EmiStack.of(BlockRegistry.ALCHEMY_TABLE));

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(ALCHEMY_TABLE);
        registry.addWorkstation(ALCHEMY_TABLE, EmiStack.of(BlockRegistry.ALCHEMY_TABLE));

        // Every recipe of our brewing type (potions + tier-up seeds) is shown automatically.
        for (RecipeHolder<PotionBrewingRecipe> holder : registry.getRecipeManager().getAllRecipesFor(PotionBrewingRecipeType.INSTANCE)) {
            registry.addRecipe(new PotionBrewingEmiRecipe(ALCHEMY_TABLE, holder.id(), holder.value()));
        }
    }
}
