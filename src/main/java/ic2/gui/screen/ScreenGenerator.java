

package ic2.gui.screen;

import ic2.gui.menu.MenuGenerator;
import ic2.tileentity.TileEntityBaseGenerator;
import ic2.tileentity.TileEntityGeoGenerator;
import ic2.tileentity.TileEntitySolarGenerator;
import ic2.tileentity.TileEntityWaterGenerator;
import ic2.tileentity.TileEntityWindGenerator;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;

public class ScreenGenerator
extends ScreenContainerAbstract {
    protected final TileEntityBaseGenerator tileEntity;
    protected final String texture;
    protected final String titleKey;

    public ScreenGenerator(ContainerInventory inventory, TileEntityBaseGenerator tileEntity, String texture, String titleKey) {
        super((MenuAbstract)new MenuGenerator(inventory, tileEntity));
        this.tileEntity = tileEntity;
        this.texture = texture;
        this.titleKey = titleKey;
    }

    protected void drawGuiContainerForegroundLayer() {
        I18n i18n = I18n.getInstance();
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey(this.titleKey), 58, 6, 0x404040);
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("container.inventory.name"), 8, this.ySize - 96 + 2, 0x404040);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTick) {
        this.mc.textureManager.loadTexture("/assets/ic2/textures/gui/" + this.texture).bind();
        GLRenderer.setColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
        TileEntityBaseGenerator tileEntityBaseGenerator = this.tileEntity;
        if (tileEntityBaseGenerator instanceof TileEntitySolarGenerator) {
            TileEntitySolarGenerator solar = (TileEntitySolarGenerator)tileEntityBaseGenerator;
            if (solar.sunIsVisible) {
                this.drawTexturedModalRect(x + 80, y + 45, 176, 0, 14, 14);
            }
            return;
        }
        if (this.tileEntity instanceof TileEntityWindGenerator) {
            int l = this.tileEntity.gaugeFuelScaled(14);
            this.drawTexturedModalRect(x + 80, y + 45 + 14 - l, 176, 14 - l, 14, l);
            return;
        }
        if (this.tileEntity instanceof TileEntityWaterGenerator) {
            if (this.tileEntity.fuel > 0) {
                int l = this.tileEntity.gaugeFuelScaled(14);
                this.drawTexturedModalRect(x + 80, y + 36 + 14 - l, 176, 14 - l, 14, l);
            }
            return;
        }
        if (this.tileEntity instanceof TileEntityGeoGenerator) {
            if (this.tileEntity.fuel > 0) {
                int i1 = this.tileEntity.gaugeFuelScaled(24);
                this.drawTexturedModalRect(x + 94, y + 35, 176, 14, i1, 17);
            }
            return;
        }
        if (this.tileEntity.fuel > 0) {
            int l = this.tileEntity.gaugeFuelScaled(12);
            this.drawTexturedModalRect(x + 66, y + 36 + 12 - l, 176, 12 - l, 14, l + 2);
        }
        int i1 = this.tileEntity.gaugeStorageScaled(24);
        this.drawTexturedModalRect(x + 94, y + 35, 176, 14, i1, 17);
    }
}

