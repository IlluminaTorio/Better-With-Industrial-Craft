package ic2.gui.screen;

import ic2.gui.menu.MenuWoodGasser;
import ic2.tileentity.TileEntityWoodGasser;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;

public class ScreenWoodGasser
extends ScreenContainerAbstract {
    protected final TileEntityWoodGasser tileEntity;

    public ScreenWoodGasser(ContainerInventory inventory, TileEntityWoodGasser tileEntity) {
        super((MenuAbstract)new MenuWoodGasser(inventory, tileEntity));
        this.tileEntity = tileEntity;
    }

    protected void drawGuiContainerForegroundLayer() {
        I18n i18n = I18n.getInstance();
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("tile.ic2.machine.wood_gasser.name"), 58, 6, 0x404040);
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("container.inventory.name"), 8, this.ySize - 96 + 2, 0x404040);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTick) {
        this.mc.textureManager.loadTexture("/assets/ic2/textures/gui/GUIWoodGasser.png").bind();
        GLRenderer.setColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
        if (this.tileEntity.fuel > 0) {
            int l = this.tileEntity.gaugeFuelScaled(12);
            this.drawTexturedModalRect(x + 56, y + 36 + 12 - l, 176, 12 - l, 14, l + 2);
        }
        int i1 = this.tileEntity.gaugeGasScaled(41);
        if (i1 > 41) {
            i1 = 41;
        }
        if (i1 < 0) {
            i1 = 0;
        }
        this.drawTexturedModalRect(x + 152, y + 20 + 41 - i1, 176, 72, 12, 5);
        if (i1 > 0) {
            this.drawTexturedModalRect(x + 152, y + 25 + 41 - i1, 176, 34, 12, i1);
        }
        this.drawTexturedModalRect(x + 151, y + 19, 188, 31, 13, 47);
        int j1 = this.tileEntity.gaugeProgressScaled(24);
        this.drawTexturedModalRect(x + 79, y + 34, 176, 14, j1 + 1, 16);
    }
}
