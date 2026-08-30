

package ic2.gui.screen;

import ic2.gui.menu.MenuTradeOMat;
import ic2.tileentity.TileEntityTradeOMat;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;

public class ScreenTradeOMat
extends ScreenContainerAbstract {
    protected final TileEntityTradeOMat tileEntity;
    protected final boolean owner;

    public ScreenTradeOMat(ContainerInventory inventory, TileEntityTradeOMat tileEntity, boolean owner) {
        super((MenuAbstract)new MenuTradeOMat(inventory, tileEntity, owner));
        this.tileEntity = tileEntity;
        this.owner = owner;
    }

    protected void drawGuiContainerForegroundLayer() {
        I18n i18n = I18n.getInstance();
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("tile.ic2.personal.trade_o_mat.name"), 56, 6, 0x404040);
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("container.inventory.name"), 8, this.ySize - 96 + 2, 0x404040);
        if (this.owner) {
            this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("gui.ic2.trades_performed"), 112, 20, 0x404040);
            this.drawStringNoShadow(this.fontRenderer, "" + this.tileEntity.tradeCount, 112, 36, 0x404040);
        } else {
            String want = this.tileEntity.getWantAsString();
            String offer = this.tileEntity.getOfferAsString();
            this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("gui.ic2.want") + " " + want, 12, 20, 0x404040);
            this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("gui.ic2.offer") + " " + offer, 12, 44, 0x404040);
        }
    }

    protected void drawGuiContainerBackgroundLayer(float partialTick) {
        String texture = this.owner ? "GUITradeOMatOpen.png" : "GUITradeOMatClosed.png";
        this.mc.textureManager.loadTexture("/assets/ic2/textures/gui/" + texture).bind();
        GLRenderer.setColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
    }
}

