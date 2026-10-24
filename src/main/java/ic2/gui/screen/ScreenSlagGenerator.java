package ic2.gui.screen;

import ic2.gui.menu.MenuSlagGenerator;
import ic2.tileentity.TileEntitySlagGenerator;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;

public class ScreenSlagGenerator
extends ScreenContainerAbstract {
    protected final TileEntitySlagGenerator tileEntity;

    public ScreenSlagGenerator(ContainerInventory inventory, TileEntitySlagGenerator tileEntity) {
        super((MenuAbstract)new MenuSlagGenerator(inventory, tileEntity));
        this.tileEntity = tileEntity;
    }

    protected void drawGuiContainerForegroundLayer() {
        I18n i18n = I18n.getInstance();
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("tile.ic2.generator.slag.name"), 58, 6, 0x404040);
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("container.inventory.name"), 8, this.ySize - 96 + 2, 0x404040);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTick) {
        this.mc.textureManager.loadTexture("/assets/ic2/textures/gui/GUISlagGenerator.png").bind();
        GLRenderer.setColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
        if (this.tileEntity.fuel > 0) {
            int l = this.tileEntity.gaugeFuelScaled(12);
            this.drawTexturedModalRect(x + 66, y + 36 + 12 - l, 176, 12 - l, 14, l + 2);
        }
        int i1 = this.tileEntity.gaugeStorageScaled(24);
        this.drawTexturedModalRect(x + 94, y + 35, 176, 14, i1, 17);
    }
}
