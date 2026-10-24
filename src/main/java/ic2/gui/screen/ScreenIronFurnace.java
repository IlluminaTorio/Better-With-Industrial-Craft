

package ic2.gui.screen;

import ic2.gui.menu.MenuIronFurnace;
import ic2.tileentity.TileEntityIronFurnace;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;

public class ScreenIronFurnace
extends ScreenContainerAbstract {
    protected final TileEntityIronFurnace tileEntity;

    public ScreenIronFurnace(ContainerInventory inventory, TileEntityIronFurnace tileEntity) {
        super((MenuAbstract)new MenuIronFurnace(inventory, tileEntity));
        this.tileEntity = tileEntity;
    }

    protected void drawGuiContainerForegroundLayer() {
        I18n i18n = I18n.getInstance();
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("gui.ic2.iron_furnace"), 60, 6, 0x404040);
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("container.inventory.name"), 8, this.ySize - 96 + 2, 0x404040);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTick) {
        this.mc.textureManager.loadTexture("/assets/minecraft/textures/gui/container/furnace.png").bind();
        GLRenderer.setColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
        if (this.tileEntity.isBurning()) {
            int fireHeight = this.tileEntity.gaugeFuelScaled(12);
            this.drawTexturedModalRect(x + 56, y + 36 + 12 - fireHeight, 176, 12 - fireHeight, 14, fireHeight + 2);
            int arrowWidth = this.tileEntity.gaugeProgressScaled(24);
            this.drawTexturedModalRect(x + 79, y + 34, 176, 14, arrowWidth + 1, 16);
        }
    }
}

