

package ic2.gui.screen;

import ic2.gui.menu.MenuPump;
import ic2.tileentity.TileEntityPump;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;

public class ScreenPump
extends ScreenContainerAbstract {
    protected final TileEntityPump tileEntity;

    public ScreenPump(ContainerInventory inventory, TileEntityPump tileEntity) {
        super((MenuAbstract)new MenuPump(inventory, tileEntity));
        this.tileEntity = tileEntity;
    }

    protected void drawGuiContainerForegroundLayer() {
        I18n i18n = I18n.getInstance();
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("tile.ic2.machine.pump.name"), 63, 6, 0x404040);
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("container.inventory.name"), 8, this.ySize - 96 + 2, 0x404040);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTick) {
        int i1;
        this.mc.textureManager.loadTexture("/assets/ic2/textures/gui/GUIPump.png").bind();
        GLRenderer.setColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
        if (this.tileEntity.energy > 0) {
            int l = this.tileEntity.energy * 14 / 200;
            if (l > 14) {
                l = 14;
            }
            this.drawTexturedModalRect(x + 69, y + 36 + 14 - l, 176, 14 - l, 14, l);
        }
        if ((i1 = this.tileEntity.pumpCharge * 24 / 200) > 24) {
            i1 = 24;
        }
        this.drawTexturedModalRect(x + 91, y + 31 + 24 - i1, 176, 38 - i1, 17, i1);
    }
}

