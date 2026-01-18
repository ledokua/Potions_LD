package net.ledok.potions_ld.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.ledok.potions_ld.client.screen.AlchemyTableScreen;
import net.ledok.potions_ld.registry.BlockRegistry;
import net.ledok.potions_ld.screen.ScreenHandlerTypeRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;

public class Potions_ldClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.VITALITY_BUSH_1, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.VITALITY_BUSH_2, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.VITALITY_BUSH_3, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.VITALITY_BUSH_4, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.VITALITY_BUSH_5, RenderType.cutout());
        
        // Alchemy Table needs cutout for the glass/fluid parts of the upgrades
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.ALCHEMY_TABLE, RenderType.cutout());

        MenuScreens.register(ScreenHandlerTypeRegistry.ALCHEMY_TABLE, AlchemyTableScreen::new);
    }
}
