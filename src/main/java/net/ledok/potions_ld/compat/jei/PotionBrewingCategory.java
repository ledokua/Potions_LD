package net.ledok.potions_ld.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.ledok.potions_ld.PotionsLdMod;
import net.ledok.potions_ld.registry.BlockRegistry;
import net.ledok.potions_ld.recipe.CountedIngredient;
import net.ledok.potions_ld.recipe.PotionBrewingRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class PotionBrewingCategory implements IRecipeCategory<PotionBrewingRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(PotionsLdMod.MOD_ID, "potion_brewing");
    public static final RecipeType<PotionBrewingRecipe> RECIPE_TYPE = RecipeType.create(PotionsLdMod.MOD_ID, "potion_brewing", PotionBrewingRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableStatic emptyArrow;
    private final IDrawableAnimated arrow;

    public PotionBrewingCategory(IGuiHelper helper) {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(PotionsLdMod.MOD_ID, "textures/gui/alchemy_table.png");
        
        // We crop the background from the GUI texture.
        // x=20, y=15, width=120, height=50 covers the main working area (slots + arrow)
        this.background = helper.createDrawable(texture, 20, 15, 120, 50);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockRegistry.ALCHEMY_TABLE));

        // Create the empty arrow drawable (u=176, v=16, w=25, h=16)
        this.emptyArrow = helper.createDrawable(texture, 176, 16, 25, 16);

        // Create the filled arrow drawable (u=176, v=0, w=25, h=16)
        IDrawableStatic staticArrow = helper.createDrawable(texture, 176, 0, 25, 16);
        
        // Create an animated version that fills from left to right over 100 ticks (5 seconds)
        this.arrow = helper.createAnimatedDrawable(staticArrow, 100, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public RecipeType<PotionBrewingRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.potions_ld.alchemy_table");
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return background.getWidth();
    }

    @Override
    public int getHeight() {
        return background.getHeight();
    }

    @Override
    public void draw(PotionBrewingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics, 0, 0);
        
        // Draw the empty arrow background first
        emptyArrow.draw(guiGraphics, 60, 16);
        
        // Draw the animated filled arrow on top
        arrow.draw(guiGraphics, 60, 16);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PotionBrewingRecipe recipe, IFocusGroup focuses) {
        List<CountedIngredient> ingredients = recipe.ingredients();

        int[][] slots = {
            {9, 6}, {27, 6},
            {9, 25}, {27, 25}
        };

        for (int i = 0; i < ingredients.size() && i < 4; i++) {
            CountedIngredient ci = ingredients.get(i);
            builder.addSlot(RecipeIngredientRole.INPUT, slots[i][0], slots[i][1])
                    .addIngredients(ci.ingredient());
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 96, 16)
                .addItemStack(recipe.output());
    }
}
