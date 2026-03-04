package net.ledok.potions_ld.world;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.ledok.potions_ld.PotionsLdMod;
import net.ledok.potions_ld.util.ModTags;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class ModBiomeModifications {
    private static final ResourceKey<PlacedFeature> VITALITY_BUSH_PLACED_KEY = ResourceKey.create(
            net.minecraft.core.registries.Registries.PLACED_FEATURE,
            ResourceLocation.fromNamespaceAndPath(PotionsLdMod.MOD_ID, "vitality_bush_placed")
    );

    public static void initialize() {
        BiomeModifications.addFeature(
                BiomeSelectors.tag(ModTags.Biomes.HAS_VITALITY_BUSH),
                GenerationStep.Decoration.VEGETAL_DECORATION,
                VITALITY_BUSH_PLACED_KEY
        );
    }
}
