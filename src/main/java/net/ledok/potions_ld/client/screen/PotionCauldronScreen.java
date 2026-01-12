package net.ledok.potions_ld.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.ledok.potions_ld.PotionsLdMod;
import net.ledok.potions_ld.screen.PotionCauldronScreenHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PotionCauldronScreen extends AbstractContainerScreen<PotionCauldronScreenHandler> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(PotionsLdMod.MOD_ID, "textures/gui/potion_cauldron.png");

    public PotionCauldronScreen(PotionCauldronScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // Draw the empty arrow background
        guiGraphics.blit(TEXTURE, x + 80, y + 31, 176, 16, 25, 16);

        if(menu.isCrafting()) {
            // Draw the filled progress arrow
            guiGraphics.blit(TEXTURE, x + 80, y + 31, 176, 0, menu.getScaledProgress(), 16);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
