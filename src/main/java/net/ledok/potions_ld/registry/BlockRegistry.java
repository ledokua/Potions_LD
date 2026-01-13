package net.ledok.potions_ld.registry;

import net.ledok.potions_ld.PotionsLdMod;
import net.ledok.potions_ld.blocks.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockRegistry {

    public static final Block POTION_CAULDRON = registerBlock("potion_cauldron",
            new PotionCauldronBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON)));

    // Tier 1 Crop
    public static final Block VITALITY_BUSH_1 = registerBlockWithoutItem("vitality_bush_1",
            new VitalityBushBlockT1(BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH),
                    () -> ItemRegistry.VITALITY_HERB_1,
                    () -> ItemRegistry.VITALITY_SEED_2
            ));

    // Tier 2 Crop
    public static final Block VITALITY_BUSH_2 = registerBlockWithoutItem("vitality_bush_2",
            new VitalityBushBlockT2(BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH),
                    () -> ItemRegistry.VITALITY_HERB_2,
                    () -> ItemRegistry.VITALITY_SEED_3
            ));

    // Tier 3 Crop
    public static final Block VITALITY_BUSH_3 = registerBlockWithoutItem("vitality_bush_3",
            new VitalityBushBlockT3(BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH),
                    () -> ItemRegistry.VITALITY_HERB_3,
                    () -> ItemRegistry.VITALITY_SEED_4
            ));

    // Tier 4 Crop
    public static final Block VITALITY_BUSH_4 = registerBlockWithoutItem("vitality_bush_4",
            new VitalityBushBlockT4(BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH),
                    () -> ItemRegistry.VITALITY_HERB_4,
                    () -> ItemRegistry.VITALITY_SEED_5
            ));

    // Tier 5 Crop
    public static final Block VITALITY_BUSH_5 = registerBlockWithoutItem("vitality_bush_5",
            new VitalityBushBlockT5(BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH),
                    () -> ItemRegistry.VITALITY_HERB_5,
                    () -> null // No next tier seed
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
