package net.ledok.potions_ld.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;

public record CountedIngredient(Ingredient ingredient, int count) {

    public static final Codec<CountedIngredient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            // This now correctly expects a nested "ingredient" object, which is standard and robust.
            Ingredient.CODEC.fieldOf("ingredient").forGetter(CountedIngredient::ingredient),
            Codec.INT.optionalFieldOf("count", 1).forGetter(CountedIngredient::count)
    ).apply(instance, CountedIngredient::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CountedIngredient> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, CountedIngredient::ingredient,
            ByteBufCodecs.INT, CountedIngredient::count,
            CountedIngredient::new
    );
}
