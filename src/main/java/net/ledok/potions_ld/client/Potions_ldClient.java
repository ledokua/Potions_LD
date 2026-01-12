package net.ledok.potions_ld.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.ledok.potions_ld.client.screen.PotionCauldronScreen;
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

        MenuScreens.register(ScreenHandlerTypeRegistry.POTION_CAULDRON, PotionCauldronScreen::new);
    }
}
