

package ic2.gui.screen;

import ic2.gui.menu.MenuNuclearReactor;
import ic2.tileentity.TileEntityNuclearReactor;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;

public class ScreenNuclearReactor
extends ScreenContainerAbstract {
    protected final TileEntityNuclearReactor tileEntity;

    public ScreenNuclearReactor(ContainerInventory inventory, TileEntityNuclearReactor tileEntity) {
        super((MenuAbstract)new MenuNuclearReactor(inventory, tileEntity));
        this.tileEntity = tileEntity;
        this.ySize = 222;
    }

    protected void drawGuiContainerForegroundLayer() {
        I18n i18n = I18n.getInstance();
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("tile.ic2.reactor.nuclear.name"), 55, 6, 0x404040);
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("container.inventory.name"), 8, this.ySize - 96 + 2, 0x404040);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTick) {
        int size = this.tileEntity.getReactorSize();
        this.mc.textureManager.loadTexture("/assets/ic2/textures/gui/GUI6by" + size + ".png").bind();
        GLRenderer.setColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
    }
}

