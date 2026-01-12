package net.ledok.potions_ld.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.ArrayList;

public class PotionBrewingRecipeSerializer implements RecipeSerializer<PotionBrewingRecipe> {
    public static final PotionBrewingRecipeSerializer INSTANCE = new PotionBrewingRecipeSerializer();
    public static final String ID = "potion_brewing";

    public static final Codec<ItemStack> ITEM_STACK_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(ItemStack::getItem),
            Codec.INT.optionalFieldOf("count", 1).forGetter(ItemStack::getCount)
    ).apply(instance, ItemStack::new));

    public static final MapCodec<PotionBrewingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CountedIngredient.CODEC.listOf().fieldOf("ingredients").forGetter(PotionBrewingRecipe::ingredients),
            ITEM_STACK_CODEC.fieldOf("result").forGetter(PotionBrewingRecipe::output),
            Codec.INT.optionalFieldOf("cookingTime", 100).forGetter(PotionBrewingRecipe::cookingTime)
    ).apply(instance, PotionBrewingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PotionBrewingRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(ArrayList::new, CountedIngredient.STREAM_CODEC), PotionBrewingRecipe::ingredients,
            ItemStack.STREAM_CODEC, PotionBrewingRecipe::output,
            ByteBufCodecs.INT, PotionBrewingRecipe::cookingTime,
            PotionBrewingRecipe::new
    );

    @Override
    public MapCodec<PotionBrewingRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, PotionBrewingRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
