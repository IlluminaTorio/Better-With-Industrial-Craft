package ic2.gui.screen;

import ic2.gui.menu.MenuHarvester;
import ic2.tileentity.TileEntityHarvester;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;

public class ScreenHarvester
extends ScreenContainerAbstract {
    protected final TileEntityHarvester tileEntity;

    public ScreenHarvester(ContainerInventory inventory, TileEntityHarvester tileEntity) {
        super((MenuAbstract)new MenuHarvester(inventory, tileEntity));
        this.tileEntity = tileEntity;
    }

    protected void drawGuiContainerForegroundLayer() {
        I18n i18n = I18n.getInstance();
        this.drawStringCenteredNoShadow(this.fontRenderer, i18n.translateKey("gui.ic2.harvester.label.title"), 88, 6, 0x404040);
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("container.inventory.name"), 8, this.ySize - 96 + 2, 0x404040);
        String status = this.tileEntity.active ? i18n.translateKey("gui.ic2.harvester.label.working") : i18n.translateKey("gui.ic2.harvester.label.idle");
        this.drawStringCenteredNoShadow(this.fontRenderer, status, 56, 26, this.tileEntity.active ? 0x207020 : 0x505050);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTick) {
        GLRenderer.setColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.textureManager.loadTexture("/assets/ic2/textures/gui/GUIHarvester.png").bind();
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
        if (this.tileEntity.energy > 0) {
            int l = this.tileEntity.gaugeEnergyScaled(14);
            this.drawTexturedModalRect(x + 56, y + 36 + 14 - l, 176, 14 - l, 14, l);
        }
    }
}
