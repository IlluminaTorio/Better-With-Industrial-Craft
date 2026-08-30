

package ic2.gui.screen;

import ic2.gui.menu.MenuPersonalSafe;
import ic2.tileentity.TileEntityPersonalChest;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.renderer.State;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;

public class ScreenPersonalSafe
extends ScreenContainerAbstract {
    protected final TileEntityPersonalChest tileEntity;
    private final int inventoryRows;

    public ScreenPersonalSafe(ContainerInventory inventory, TileEntityPersonalChest tileEntity) {
        super((MenuAbstract)new MenuPersonalSafe(inventory, tileEntity));
        this.tileEntity = tileEntity;
        this.inventoryRows = tileEntity.getContainerSize() / 9;
        this.ySize = 113 + this.inventoryRows * 18;
    }

    protected void drawGuiContainerForegroundLayer() {
        I18n i18n = I18n.getInstance();
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("tile.ic2.personal.safe.name"), 8, 6, 0x404040);
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("container.inventory.name"), 8, this.ySize - 96 + 2, 0x404040);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTick) {
        GLRenderer.enableState((State)State.BLEND);
        GLRenderer.setColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.mc.textureManager.loadTexture("/assets/minecraft/textures/gui/container/container.png").bind();
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        int h1 = Math.min(this.inventoryRows, 6) * 18 + 17;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, h1);
        int rows = this.inventoryRows;
        while (rows > 6) {
            int h2 = Math.min(rows, 6) * 18;
            this.drawTexturedModalRect(x, y + h1, 0, 17, this.xSize, h2);
            rows -= 6;
            h1 += h2;
        }
        this.drawTexturedModalRect(x, y + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
    }
}

