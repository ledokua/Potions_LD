package net.ledok.potions_ld.util;

import net.ledok.potions_ld.PotionsLdMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class ModTags {
    public static class Biomes {
        public static final TagKey<Biome> HAS_VITALITY_BUSH =
                createTag("has_vitality_bush");

        private static TagKey<Biome> createTag(String name) {
            return TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(PotionsLdMod.MOD_ID, name));
        }
    }
}
