package ic2.gui.screen;

import ic2.gui.menu.MenuWoodGasserElec;
import ic2.tileentity.TileEntityWoodGasserElec;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;

public class ScreenWoodGasserElec
extends ScreenContainerAbstract {
    protected final TileEntityWoodGasserElec tileEntity;

    public ScreenWoodGasserElec(ContainerInventory inventory, TileEntityWoodGasserElec tileEntity) {
        super((MenuAbstract)new MenuWoodGasserElec(inventory, tileEntity));
        this.tileEntity = tileEntity;
    }

    protected void drawGuiContainerForegroundLayer() {
        I18n i18n = I18n.getInstance();
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("tile.ic2.machine.wood_gasser_elec.name"), 46, 6, 0x404040);
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("container.inventory.name"), 8, this.ySize - 96 + 2, 0x404040);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTick) {
        this.mc.textureManager.loadTexture("/assets/ic2/textures/gui/GUIWoodGasserElec.png").bind();
        GLRenderer.setColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
        int charge = (int)(this.tileEntity.getChargeLevel() * 14.0f);
        if (charge > 0) {
            this.drawTexturedModalRect(x + 46, y + 36 + 14 - charge, 176, 14 - charge, 14, charge);
        }
        int i1 = this.tileEntity.gaugeGasScaled(41);
        if (i1 > 41) {
            i1 = 41;
        }
        if (i1 < 0) {
            i1 = 0;
        }
        this.drawTexturedModalRect(x + 132, y + 20 + 41 - i1, 176, 72, 12, 5);
        if (i1 > 0) {
            this.drawTexturedModalRect(x + 132, y + 25 + 41 - i1, 176, 34, 12, i1);
        }
        this.drawTexturedModalRect(x + 131, y + 19, 188, 31, 13, 47);
        int j1 = this.tileEntity.gaugeProgressScaled(24);
        this.drawTexturedModalRect(x + 69, y + 34, 176, 14, j1 + 1, 16);
    }
}
