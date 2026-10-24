package ic2.gui.screen;

import ic2.gui.menu.MenuRareEarthExtractor;
import ic2.tileentity.TileEntityRareEarthExtractor;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;

public class ScreenRareEarthExtractor
extends ScreenContainerAbstract {
    protected final TileEntityRareEarthExtractor tileEntity;

    public ScreenRareEarthExtractor(ContainerInventory inventory, TileEntityRareEarthExtractor tileEntity) {
        super((MenuAbstract)new MenuRareEarthExtractor(inventory, tileEntity));
        this.tileEntity = tileEntity;
    }

    protected void drawGuiContainerForegroundLayer() {
        I18n i18n = I18n.getInstance();
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("tile.ic2.machine.rare_earth_extractor.name"), 58, 6, 0x404040);
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("container.inventory.name"), 8, this.ySize - 96 + 2, 0x404040);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTick) {
        this.mc.textureManager.loadTexture("/assets/ic2/textures/gui/GUIRareEarthExtractor.png").bind();
        GLRenderer.setColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
        int j1 = (int)(this.tileEntity.getChargeLevel() * 14.0f);
        int k1 = this.tileEntity.gaugeProgressScaled(24);
        int l1 = this.tileEntity.gaugeRareEarthScaled(162);
        if (j1 > 0) {
            this.drawTexturedModalRect(x + 56, y + 36 + 14 - j1, 176, 14 - j1, 14, j1);
        }
        if (k1 > 0) {
            this.drawTexturedModalRect(x + 78, y + 34, 176, 14, k1 + 1, 16);
        }
        this.drawTexturedModalRect(x + 7, y + 12, 7, 168, l1, 3);
    }
}
