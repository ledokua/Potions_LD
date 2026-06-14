package net.ledok.potions_ld.compat.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.ledok.potions_ld.recipe.CountedIngredient;
import net.ledok.potions_ld.recipe.PotionBrewingRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class PotionBrewingEmiRecipe implements EmiRecipe {

    // 2x2 input grid positions (matches the alchemy table layout).
    private static final int[][] INPUT_SLOTS = {{0, 2}, {18, 2}, {0, 20}, {18, 20}};

    private final EmiRecipeCategory category;
    private final ResourceLocation id;
    private final List<EmiIngredient> inputs;
    private final EmiStack output;
    private final int cookingTime;

    public PotionBrewingEmiRecipe(EmiRecipeCategory category, ResourceLocation id, PotionBrewingRecipe recipe) {
        this.category = category;
        this.id = id;
        this.cookingTime = recipe.cookingTime();
        this.inputs = new ArrayList<>();
        for (CountedIngredient ci : recipe.ingredients()) {
            this.inputs.add(EmiIngredient.of(ci.ingredient(), ci.count()));
        }
        this.output = EmiStack.of(recipe.output());
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return category;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(output);
    }

    @Override
    public int getDisplayWidth() {
        return 120;
    }

    @Override
    public int getDisplayHeight() {
        return 40;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        for (int i = 0; i < inputs.size() && i < INPUT_SLOTS.length; i++) {
            widgets.addSlot(inputs.get(i), INPUT_SLOTS[i][0], INPUT_SLOTS[i][1]);
        }

        // cookingTime is in ticks; EMI's filling arrow wants milliseconds (20 ticks = 1000 ms).
        widgets.addFillingArrow(42, 9, cookingTime * 50);
        widgets.addSlot(output, 70, 4).large(true).recipeContext(this);
        widgets.addText(Component.translatable("jei.potions_ld.cooking_time", cookingTime / 20f), 42, 30, 0xFF555555, false);
    }
}
