package net.ledok.potions_ld.blocks.entity;

import net.ledok.potions_ld.PotionsLdMod;
import net.ledok.potions_ld.registry.BlockRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BlockEntityTypeRegistry {
    public static BlockEntityType<PotionCauldronBlockEntity> POTION_CAULDRON;

    public static void initialize() {
        POTION_CAULDRON = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(PotionsLdMod.MOD_ID, "potion_cauldron"),
                BlockEntityType.Builder.of(PotionCauldronBlockEntity::new, BlockRegistry.POTION_CAULDRON).build(null)
        );
    }
}
