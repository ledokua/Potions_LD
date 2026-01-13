package net.ledok.potions_ld.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.ledok.potions_ld.PotionsLdMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;

public class ScreenHandlerTypeRegistry {
    public static MenuType<AlchemyTableScreenHandler> ALCHEMY_TABLE;

    public static void initialize() {
        ALCHEMY_TABLE = Registry.register(
                BuiltInRegistries.MENU,
                ResourceLocation.fromNamespaceAndPath(PotionsLdMod.MOD_ID, "alchemy_table"),
                new MenuType<>(AlchemyTableScreenHandler::new, net.minecraft.world.flag.FeatureFlagSet.of())
        );
    }
}
