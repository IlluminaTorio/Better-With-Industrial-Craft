

package ic2.gui.screen;

import ic2.gui.menu.MenuElectrolyzer;
import ic2.tileentity.TileEntityElectrolyzer;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;

public class ScreenElectrolyzer
extends ScreenContainerAbstract {
    protected final TileEntityElectrolyzer tileEntity;

    public ScreenElectrolyzer(ContainerInventory inventory, TileEntityElectrolyzer tileEntity) {
        super((MenuAbstract)new MenuElectrolyzer(inventory, tileEntity));
        this.tileEntity = tileEntity;
    }

    protected void drawGuiContainerForegroundLayer() {
        I18n i18n = I18n.getInstance();
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("tile.ic2.machine.electrolyzer.name"), 58, 6, 0x404040);
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("container.inventory.name"), 8, this.ySize - 96 + 2, 0x404040);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTick) {
        this.mc.textureManager.loadTexture("/assets/ic2/textures/gui/GUIElectrolyzer.png").bind();
        GLRenderer.setColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
        if (this.tileEntity.energy > 0) {
            int i1 = this.tileEntity.gaugeEnergyScaled(24);
            this.drawTexturedModalRect(x + 79, y + 34, 176, 14, i1 + 1, 16);
        }
    }
}

