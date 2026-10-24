package ic2.gui.screen;

import ic2.gui.menu.MenuElectricTrommel;
import ic2.tileentity.TileEntityElectricTrommel;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;

public class ScreenElectricTrommel
extends ScreenContainerAbstract {
    public final TileEntityElectricTrommel tileEntity;

    public ScreenElectricTrommel(ContainerInventory inventory, TileEntityElectricTrommel tileEntity) {
        super((MenuAbstract)new MenuElectricTrommel(inventory, tileEntity));
        this.ySize = 192;
        this.tileEntity = tileEntity;
    }

    protected void drawGuiContainerForegroundLayer() {
        I18n i18n = I18n.getInstance();
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("gui.ic2.electric_trommel.label.title"), 60, 6, 0x404040);
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("container.inventory.name"), 8, this.ySize - 96 + 2, 0x404040);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTick) {
        GLRenderer.setColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.textureManager.loadTexture("/assets/ic2/textures/gui/GUIElectricTrommel.png").bind();
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
        if (this.tileEntity.energy > 0) {
            int l = this.tileEntity.gaugeEnergyScaled(14);
            this.drawTexturedModalRect(x + 34, y + 35 + 14 - l, 176, 14 - l, 14, l);
        }
        int frame = this.tileEntity.gaugeProgressScaled(8) % 4;
        GLRenderer.pushFrame();
        GLRenderer.modelM4f().translate(x + 105, y + 50, 0.0f);
        this.drawTexturedModalRect(0, 0, 176 + frame * 16, 16, 16, 16);
        GLRenderer.popFrame();
    }
}
