package ic2.gui.screen;

import ic2.gui.menu.MenuPlasmafier;
import ic2.tileentity.TileEntityPlasmafier;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;

public class ScreenPlasmafier
extends ScreenContainerAbstract {
    protected final TileEntityPlasmafier tileEntity;

    public ScreenPlasmafier(ContainerInventory inventory, TileEntityPlasmafier tileEntity) {
        super((MenuAbstract)new MenuPlasmafier(inventory, tileEntity));
        this.tileEntity = tileEntity;
    }

    protected void drawGuiContainerForegroundLayer() {
        I18n i18n = I18n.getInstance();
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("tile.ic2.machine.plasmafier.name"), 44, 6, 0x404040);
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("container.inventory.name"), 8, this.ySize - 96 + 2, 0x404040);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTick) {
        this.mc.textureManager.loadTexture("/assets/ic2/textures/gui/GUIPlasmafier.png").bind();
        GLRenderer.setColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
        int j1 = this.tileEntity.gaugePlasmaScaled(41);
        if (j1 > 41) {
            j1 = 41;
        }
        if (j1 < 0) {
            j1 = 0;
        }
        this.drawTexturedModalRect(x + 82, y + 59 - j1, 176, 41, 12, 5);
        if (j1 > 0) {
            this.drawTexturedModalRect(x + 82, y + 18 + 41 - j1, 176, 0, 12, j1);
        }
        this.drawTexturedModalRect(x + 81, y + 17, 188, 0, 13, 47);
    }
}
