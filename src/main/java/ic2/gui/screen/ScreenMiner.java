

package ic2.gui.screen;

import ic2.gui.menu.MenuMiner;
import ic2.tileentity.TileEntityMiner;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;

public class ScreenMiner
extends ScreenContainerAbstract {
    protected final TileEntityMiner tileEntity;

    public ScreenMiner(ContainerInventory inventory, TileEntityMiner tileEntity) {
        super((MenuAbstract)new MenuMiner(inventory, tileEntity));
        this.tileEntity = tileEntity;
    }

    protected void drawGuiContainerForegroundLayer() {
        I18n i18n = I18n.getInstance();
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("tile.ic2.machine.miner.name"), 62, 6, 0x404040);
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("container.inventory.name"), 8, this.ySize - 96 + 2, 0x404040);
        if (this.tileEntity.stuckOn != null) {
            this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("gui.ic2.miner_stuck") + " " + this.tileEntity.stuckOn, 30, 40, 0xA04040);
        }
    }

    protected void drawGuiContainerBackgroundLayer(float partialTick) {
        this.mc.textureManager.loadTexture("/assets/ic2/textures/gui/GUIMiner.png").bind();
        GLRenderer.setColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
        if (this.tileEntity.energy > 0) {
            int l = this.tileEntity.gaugeEnergyScaled(14);
            this.drawTexturedModalRect(x + 81, y + 41 + 14 - l, 176, 14 - l, 14, l);
        }
    }
}

