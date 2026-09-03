package ic2.gui.screen;

import ic2.gui.menu.MenuTurbineSolar;
import ic2.tileentity.TileEntityTurbineSolar;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;

public class ScreenTurbineSolar
extends ScreenContainerAbstract {
    protected final TileEntityTurbineSolar tileEntity;

    public ScreenTurbineSolar(ContainerInventory inventory, TileEntityTurbineSolar tileEntity) {
        super((MenuAbstract)new MenuTurbineSolar(inventory, tileEntity));
        this.tileEntity = tileEntity;
    }


    protected void drawGuiContainerForegroundLayer() {
        I18n i18n = I18n.getInstance();
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("tile.ic2.generator.turbine_solar.name"), 58, 6, 0x404040);
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("container.inventory.name"), 8, this.ySize - 96 + 2, 0x404040);
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("gui.ic2.heat"), 10, 44, 0x404040);
        this.drawStringNoShadow(this.fontRenderer, (int)(this.tileEntity.heatPercent() * 100.0f) + "%", 10, 52, 0x404040);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTick) {
        this.mc.textureManager.loadTexture("/assets/ic2/textures/gui/GUITurbineSolar.png").bind();
        GLRenderer.setColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
        if (this.tileEntity.sunIsVisible) {
            this.drawTexturedModalRect(x + 62, y + 36, 176, 0, 14, 14);
        }
        int k1 = this.tileEntity.water * 41 / 40000;
        if (k1 > 41) {
            k1 = 41;
        }
        if (k1 < 0) {
            k1 = 0;
        }
        this.drawTexturedModalRect(x + 99, y + 61 - k1, 176, 55, 12, 5);
        if (k1 > 0) {
            this.drawTexturedModalRect(x + 99, y + 25 + 41 - k1, 176, 14, 12, k1);
        }
        this.drawTexturedModalRect(x + 98, y + 19, 188, 14, 13, 47);
    }
}
