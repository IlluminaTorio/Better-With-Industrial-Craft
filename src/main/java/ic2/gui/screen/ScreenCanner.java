

package ic2.gui.screen;

import ic2.gui.menu.MenuCanner;
import ic2.tileentity.TileEntityCanner;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;

public class ScreenCanner
extends ScreenContainerAbstract {
    protected final TileEntityCanner tileEntity;

    public ScreenCanner(ContainerInventory inventory, TileEntityCanner tileEntity) {
        super((MenuAbstract)new MenuCanner(inventory, tileEntity));
        this.tileEntity = tileEntity;
    }

    protected void drawGuiContainerForegroundLayer() {
        I18n i18n = I18n.getInstance();
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("tile.ic2.machine.canner.name"), 50, 6, 0x404040);
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("container.inventory.name"), 8, this.ySize - 96 + 2, 0x404040);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTick) {
        this.mc.textureManager.loadTexture("/assets/ic2/textures/gui/GUICanner.png").bind();
        GLRenderer.setColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
        if (this.tileEntity.energy > 0) {
            int l = this.tileEntity.gaugeFuelScaled(14);
            this.drawTexturedModalRect(x + 31, y + 27 + 14 - l, 176, 14 - l, 14, l);
        }
        int i1 = this.tileEntity.gaugeProgressScaled(34);
        this.drawTexturedModalRect(x + 74, y + 36, 176, 15, i1, 13);
    }
}

