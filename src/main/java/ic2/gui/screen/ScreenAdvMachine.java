package ic2.gui.screen;

import ic2.gui.menu.MenuAdvMachine;
import ic2.tileentity.TileEntityAdvMachine;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;

public class ScreenAdvMachine
extends ScreenContainerAbstract {
    protected final TileEntityAdvMachine tileEntity;
    protected final String texture;
    protected final String titleKey;
    protected final String speedUnitKey;

    public ScreenAdvMachine(ContainerInventory inventory, TileEntityAdvMachine tileEntity, String texture, String titleKey, String speedUnitKey) {
        super((MenuAbstract)new MenuAdvMachine(inventory, tileEntity));
        this.tileEntity = tileEntity;
        this.texture = texture;
        this.titleKey = titleKey;
        this.speedUnitKey = speedUnitKey;
    }

    protected void drawGuiContainerForegroundLayer() {
        I18n i18n = I18n.getInstance();
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey(this.titleKey), 58, 6, 0x404040);
        String unit = i18n.translateKey(this.speedUnitKey);
        this.drawStringNoShadow(this.fontRenderer, this.tileEntity.speed + " " + unit, 79, 58, 0x404040);
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("container.inventory.name"), 8, this.ySize - 96 + 2, 0x404040);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTick) {
        this.mc.textureManager.loadTexture("/assets/ic2/textures/gui/" + this.texture).bind();
        GLRenderer.setColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
        if (this.tileEntity.energy > 0) {
            int l = this.tileEntity.gaugeEnergyScaled(14);
            this.drawTexturedModalRect(x + 56, y + 36 + 14 - l, 176, 14 - l, 14, l);
        }
        int w = this.tileEntity.gaugeProgressScaled(24);
        this.drawTexturedModalRect(x + 79, y + 34, 176, 14, w + 1, 16);
        int f = this.tileEntity.gaugeSpeedScaled(53);
        if (f > 0) {
            this.drawTexturedModalRect(x + 11, y + 71 - f, 200, 53 - f, 5, f);
        }
    }
}
