

package ic2.gui.screen;

import ic2.gui.menu.MenuElectricBlock;
import ic2.tileentity.TileEntityElectricBlock;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;

public class ScreenElectricBlock
extends ScreenContainerAbstract {
    protected final TileEntityElectricBlock tileEntity;
    protected final String titleKey;

    public ScreenElectricBlock(ContainerInventory inventory, TileEntityElectricBlock tileEntity, String titleKey) {
        super((MenuAbstract)new MenuElectricBlock(inventory, tileEntity));
        this.tileEntity = tileEntity;
        this.titleKey = titleKey;
    }

    protected void drawGuiContainerForegroundLayer() {
        I18n i18n = I18n.getInstance();
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey(this.titleKey), 62, 6, 0x404040);
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("container.inventory.name"), 8, this.ySize - 96 + 2, 0x404040);
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("gui.ic2.power_level"), 79, 25, 0x404040);
        this.drawStringNoShadow(this.fontRenderer, " " + this.tileEntity.energy, 110, 35, 0x404040);
        this.drawStringNoShadow(this.fontRenderer, "/" + this.tileEntity.maxStorage, 110, 45, 0x404040);
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("gui.ic2.output") + " " + this.tileEntity.output + " EU/t", 85, 60, 0x404040);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTick) {
        this.mc.textureManager.loadTexture("/assets/ic2/textures/gui/GUIElectricBlock.png").bind();
        GLRenderer.setColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
        if (this.tileEntity.energy > 0) {
            int i1 = this.tileEntity.gaugeEnergyScaled(24);
            this.drawTexturedModalRect(x + 79, y + 34, 176, 14, i1 + 1, 16);
        }
    }
}

