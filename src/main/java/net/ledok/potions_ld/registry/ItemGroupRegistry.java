package net.ledok.potions_ld.registry;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.ledok.potions_ld.PotionsLdMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ItemGroupRegistry {
    public static final CreativeModeTab POTIONS_LD_GROUP = Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            ResourceLocation.fromNamespaceAndPath(PotionsLdMod.MOD_ID, "potions_ld_group"),
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(ItemRegistry.HEALING_POTION_1))
                    .title(Component.translatable("itemGroup.potions_ld.potions_ld_group"))
                    .displayItems((context, entries) -> {
                        // Potions
                        entries.accept(ItemRegistry.HEALING_POTION_1);
                        entries.accept(ItemRegistry.HEALING_POTION_2);
                        entries.accept(ItemRegistry.HEALING_POTION_3);
                        entries.accept(ItemRegistry.HEALING_POTION_4);
                        entries.accept(ItemRegistry.HEALING_POTION_5);
                        entries.accept(ItemRegistry.HEALING_POTION_6);
                        
                        // Crafting stations
                        entries.accept(BlockRegistry.POTION_CAULDRON);

                        // Farming
                        entries.accept(ItemRegistry.VITALITY_SEED_1);
                        entries.accept(ItemRegistry.VITALITY_HERB_1);
                        entries.accept(ItemRegistry.VITALITY_SEED_2);
                        entries.accept(ItemRegistry.VITALITY_HERB_2);
                        entries.accept(ItemRegistry.VITALITY_SEED_3);
                        entries.accept(ItemRegistry.VITALITY_HERB_3);
                        entries.accept(ItemRegistry.VITALITY_SEED_4);
                        entries.accept(ItemRegistry.VITALITY_HERB_4);
                        entries.accept(ItemRegistry.VITALITY_SEED_5);
                        entries.accept(ItemRegistry.VITALITY_HERB_5);

                        // Upgrades
                        entries.accept(ItemRegistry.SPEED_UPGRADE);
                        entries.accept(ItemRegistry.EFFICIENCY_UPGRADE);
                        entries.accept(ItemRegistry.FORTUNE_UPGRADE);
                    })
                    .build()
    );

    public static void initialize() {
        // Loads the class
    }
}
