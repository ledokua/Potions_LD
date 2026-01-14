package net.ledok.potions_ld;

import net.fabricmc.api.ModInitializer;
import net.ledok.potions_ld.blocks.entity.BlockEntityTypeRegistry;
import net.ledok.potions_ld.registry.BlockRegistry;
import net.ledok.potions_ld.registry.EffectRegistry;
import net.ledok.potions_ld.registry.ItemGroupRegistry;
import net.ledok.potions_ld.registry.ItemRegistry;
import net.ledok.potions_ld.registry.RecipeRegistry;
import net.ledok.potions_ld.screen.ScreenHandlerTypeRegistry;
import net.ledok.potions_ld.world.ModBiomeModifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PotionsLdMod implements ModInitializer {
    public static final String MOD_ID = "potions_ld";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // Items must be registered before blocks that use them
        ItemRegistry.initialize();
        BlockRegistry.initialize();
        BlockEntityTypeRegistry.initialize();
        EffectRegistry.initialize();
        RecipeRegistry.initialize();
        ScreenHandlerTypeRegistry.initialize();
        ModBiomeModifications.initialize();
        // Item Group should be last so it can find all items/blocks
        ItemGroupRegistry.initialize();
    }
}
