package ic2.gui.screen;

import ic2.gui.menu.MenuThermalGenerator;
import ic2.tileentity.TileEntityThermalGenerator;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;

public class ScreenThermalGenerator
extends ScreenContainerAbstract {
    protected final TileEntityThermalGenerator tileEntity;

    public ScreenThermalGenerator(ContainerInventory inventory, TileEntityThermalGenerator tileEntity) {
        super((MenuAbstract)new MenuThermalGenerator(inventory, tileEntity));
        this.tileEntity = tileEntity;
    }


    protected void drawGuiContainerForegroundLayer() {
        I18n i18n = I18n.getInstance();
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("tile.ic2.generator.thermal.name"), 58, 6, 0x404040);
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("container.inventory.name"), 8, this.ySize - 96 + 2, 0x404040);
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("gui.ic2.ambient_heat"), 10, 36, 0x404040);
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("gui.ic2.eu_tick"), 10, 44, 0x404040);
        this.drawStringNoShadow(this.fontRenderer, this.tileEntity.getAmbientHeatEU(), 10, 52, 0x404040);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTick) {
        this.mc.textureManager.loadTexture("/assets/ic2/textures/gui/GUIThermalGenerator.png").bind();
        GLRenderer.setColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
        if (this.tileEntity.significantAmbientHeat()) {
            this.drawTexturedModalRect(x + 66, y + 36, 176, 31, 14, 14);
        }
        if (this.tileEntity.hasLava()) {
            int j1 = this.tileEntity.gaugeLavaScaled(14);
            this.drawTexturedModalRect(x + 66, y + 36 + 14 - j1, 176, 14 - j1, 14, j1);
        }
        int i1 = this.tileEntity.gaugeStorageScaled(24);
        this.drawTexturedModalRect(x + 94, y + 35, 176, 14, i1, 17);
    }
}
