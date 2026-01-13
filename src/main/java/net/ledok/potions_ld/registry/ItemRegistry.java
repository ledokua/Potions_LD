package net.ledok.potions_ld.registry;

import net.ledok.potions_ld.PotionsLdMod;
import net.ledok.potions_ld.items.StationUpgradeItem;
import net.ledok.potions_ld.items.PercentageHealItem;
import net.ledok.potions_ld.items.VitalityHerbItem;
import net.ledok.potions_ld.items.VitalitySeedItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

@SuppressWarnings("unused")
public class ItemRegistry {
    // HEALING POTIONS
    public static final Item HEALING_POTION_1 = ItemInit.register(
            new PercentageHealItem(new Item.Properties().stacksTo(8), 0.1f, 20, 160, 50, true),
            "healing_potion_1"
    );
    public static final Item HEALING_POTION_2 = ItemInit.register(
            new PercentageHealItem(new Item.Properties().stacksTo(16), 0.25f, 20, 200, 50, true),
            "healing_potion_2"
    );
    public static final Item HEALING_POTION_3 = ItemInit.register(
            new PercentageHealItem(new Item.Properties().stacksTo(16), 0.35f, 20, 200, 100, true),
            "healing_potion_3"
    );
    public static final Item HEALING_POTION_4 = ItemInit.register(
            new PercentageHealItem(new Item.Properties().stacksTo(16), 0.5f, 18, 350, 150, true),
            "healing_potion_4"
    );
    public static final Item HEALING_POTION_5 = ItemInit.register(
            new PercentageHealItem(new Item.Properties().stacksTo(16), 0.75f, 25, 440, 200, true),
            "healing_potion_5"
    );
    public static final Item HEALING_POTION_6 = ItemInit.register(
            // healPercentage: 100%, useTimeTicks: 0.25s, sicknessTicks: 10m
            new PercentageHealItem(new Item.Properties().stacksTo(1), 1f, 5, 12000, 600, false),
            "healing_potion_6"
    );

    // Tier 1
    public static final Item VITALITY_SEED_1 = ItemInit.register(
            new VitalitySeedItem(BlockRegistry.VITALITY_BUSH_1, new Item.Properties()),
            "vitality_seed_1"
    );
    public static final Item VITALITY_HERB_1 = ItemInit.register(
            new VitalityHerbItem(new Item.Properties()),
            "vitality_herb_1"
    );

    // Tier 2
    public static final Item VITALITY_SEED_2 = ItemInit.register(
            new VitalitySeedItem(BlockRegistry.VITALITY_BUSH_2, new Item.Properties()),
            "vitality_seed_2"
    );
    public static final Item VITALITY_HERB_2 = ItemInit.register(
            new VitalityHerbItem(new Item.Properties()),
            "vitality_herb_2"
    );

    // Tier 3
    public static final Item VITALITY_SEED_3 = ItemInit.register(
            new VitalitySeedItem(BlockRegistry.VITALITY_BUSH_3, new Item.Properties()),
            "vitality_seed_3"
    );
    public static final Item VITALITY_HERB_3 = ItemInit.register(
            new VitalityHerbItem(new Item.Properties()),
            "vitality_herb_3"
    );

    // Tier 4
    public static final Item VITALITY_SEED_4 = ItemInit.register(
            new VitalitySeedItem(BlockRegistry.VITALITY_BUSH_4, new Item.Properties()),
            "vitality_seed_4"
    );
    public static final Item VITALITY_HERB_4 = ItemInit.register(
            new VitalityHerbItem(new Item.Properties()),
            "vitality_herb_4"
    );

    // Tier 5
    public static final Item VITALITY_SEED_5 = ItemInit.register(
            new VitalitySeedItem(BlockRegistry.VITALITY_BUSH_5, new Item.Properties()),
            "vitality_seed_5"
    );
    public static final Item VITALITY_HERB_5 = ItemInit.register(
            new VitalityHerbItem(new Item.Properties()),
            "vitality_herb_5"
    );

    // Upgrades
    public static final Item SPEED_UPGRADE = ItemInit.register(
            new StationUpgradeItem(new Item.Properties().stacksTo(1)),
            "speed_upgrade"
    );
    public static final Item EFFICIENCY_UPGRADE = ItemInit.register(
            new StationUpgradeItem(new Item.Properties().stacksTo(1)),
            "efficiency_upgrade"
    );
    public static final Item FORTUNE_UPGRADE = ItemInit.register(
            new StationUpgradeItem(new Item.Properties().stacksTo(1)),
            "fortune_upgrade"
    );

    public static class ItemInit {
        public static Item register(Item item, String id) {
            ResourceLocation itemID = ResourceLocation.fromNamespaceAndPath(PotionsLdMod.MOD_ID, id);

            // Register the item to the built-in registry for items.
            Item registeredItem = Registry.register(BuiltInRegistries.ITEM, itemID, item);

            // Return the registered item.
            return registeredItem;
        }
    }

    public static void initialize() {
    }
}
