package ic2.gui.screen;

import ic2.gui.menu.MenuElectricBlastFurnace;
import ic2.tileentity.TileEntityElectricBlastFurnace;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;

public class ScreenElectricBlastFurnace
extends ScreenContainerAbstract {
    public final TileEntityElectricBlastFurnace tileEntity;

    public ScreenElectricBlastFurnace(ContainerInventory inventory, TileEntityElectricBlastFurnace tileEntity) {
        super((MenuAbstract)new MenuElectricBlastFurnace(inventory, tileEntity));
        this.tileEntity = tileEntity;
    }

    protected void drawGuiContainerForegroundLayer() {
        I18n i18n = I18n.getInstance();
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("gui.ic2.electric_blast_furnace.label.title"), 60, 6, 0x404040);
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("gui.furnace.label.inventory"), 8, this.ySize - 96 + 2, 0x404040);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTick) {
        GLRenderer.setColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.textureManager.loadTexture("/assets/ic2/textures/gui/GUIElectricBlastFurnace.png").bind();
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
        if (this.tileEntity.energy > 0) {
            int l = this.tileEntity.gaugeEnergyScaled(14);
            this.drawTexturedModalRect(x + 56, y + 36 + 14 - l, 176, 14 - l, 14, l);
        }
        if (this.tileEntity.progress > 0) {
            int w = this.tileEntity.gaugeProgressScaled(24);
            this.drawTexturedModalRect(x + 79, y + 34, 176, 14, w + 1, 16);
        }
    }
}
