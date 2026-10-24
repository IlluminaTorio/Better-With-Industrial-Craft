

package ic2.gui.screen;

import ic2.gui.menu.MenuMatter;
import ic2.tileentity.TileEntityMatter;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;

public class ScreenMatter
extends ScreenContainerAbstract {
    protected final TileEntityMatter tileEntity;

    public ScreenMatter(ContainerInventory inventory, TileEntityMatter tileEntity) {
        super((MenuAbstract)new MenuMatter(inventory, tileEntity));
        this.tileEntity = tileEntity;
    }

    protected void drawGuiContainerForegroundLayer() {
        I18n i18n = I18n.getInstance();
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("tile.ic2.machine.mass_fabricator.name"), 54, 6, 0x404040);
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("container.inventory.name"), 8, this.ySize - 96 + 2, 0x404040);
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("gui.ic2.progress"), 16, 20, 0x404040);
        this.drawStringNoShadow(this.fontRenderer, this.tileEntity.getProgressAsString(), 16, 28, 0x404040);
        if (this.tileEntity.scrap > 0) {
            this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("gui.ic2.amplifier"), 16, 44, 0x404040);
            this.drawStringNoShadow(this.fontRenderer, "" + this.tileEntity.scrap, 16, 56, 0x404040);
        }
    }

    protected void drawGuiContainerBackgroundLayer(float partialTick) {
        this.mc.textureManager.loadTexture("/assets/ic2/textures/gui/GUIMatter.png").bind();
        GLRenderer.setColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
    }
}

