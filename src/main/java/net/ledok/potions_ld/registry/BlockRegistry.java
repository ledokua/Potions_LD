package net.ledok.potions_ld.registry;

import net.ledok.potions_ld.PotionsLdMod;
import net.ledok.potions_ld.blocks.VitalityBushBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockRegistry {

    // Tier 1 Crop
    public static final Block VITALITY_BUSH_1 = registerBlockWithoutItem("vitality_bush_1",
            new VitalityBushBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH),
                    () -> ItemRegistry.VITALITY_HERB_1,
                    () -> ItemRegistry.VITALITY_SEED_2,
                    3 // Max Age
            ));

    // Tier 2 Crop
    public static final Block VITALITY_BUSH_2 = registerBlockWithoutItem("vitality_bush_2",
            new VitalityBushBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH),
                    () -> ItemRegistry.VITALITY_HERB_2,
                    () -> ItemRegistry.VITALITY_SEED_3,
                    4 // Max Age
            ));

    // Tier 3 Crop
    public static final Block VITALITY_BUSH_3 = registerBlockWithoutItem("vitality_bush_3",
            new VitalityBushBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH),
                    () -> ItemRegistry.VITALITY_HERB_3,
                    () -> ItemRegistry.VITALITY_SEED_4,
                    5 // Max Age
            ));

    // Tier 4 Crop
    public static final Block VITALITY_BUSH_4 = registerBlockWithoutItem("vitality_bush_4",
            new VitalityBushBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH),
                    () -> ItemRegistry.VITALITY_HERB_4,
                    () -> ItemRegistry.VITALITY_SEED_5, // Placeholder
                    6 // Max Age
            ));

    private static Block registerBlockWithoutItem(String name, Block block) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(PotionsLdMod.MOD_ID, name);
        return Registry.register(BuiltInRegistries.BLOCK, id, block);
    }

    private static Block registerBlock(String name, Block block) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(PotionsLdMod.MOD_ID, name);
        Registry.register(BuiltInRegistries.BLOCK, id, block);
        Registry.register(BuiltInRegistries.ITEM, id, new net.minecraft.world.item.BlockItem(block, new Item.Properties()));
        return block;
    }

    public static void initialize() {
    }
}
