package net.ledok.potions_ld.blocks.entity;

import net.ledok.potions_ld.PotionsLdMod;
import net.ledok.potions_ld.registry.BlockRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BlockEntityTypeRegistry {
    public static BlockEntityType<AlchemyTableBlockEntity> ALCHEMY_TABLE;

    public static void initialize() {
        ALCHEMY_TABLE = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(PotionsLdMod.MOD_ID, "alchemy_table"),
                BlockEntityType.Builder.of(AlchemyTableBlockEntity::new, BlockRegistry.ALCHEMY_TABLE).build(null)
        );
    }
}
