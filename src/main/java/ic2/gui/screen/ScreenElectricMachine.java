

package ic2.gui.screen;

import ic2.gui.menu.MenuElectricMachine;
import ic2.tileentity.TileEntityElectricMachine;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;

public class ScreenElectricMachine
extends ScreenContainerAbstract {
    protected final TileEntityElectricMachine tileEntity;
    protected final String texture;
    protected final String titleKey;

    public ScreenElectricMachine(ContainerInventory inventory, TileEntityElectricMachine tileEntity, String texture, String titleKey) {
        super((MenuAbstract)new MenuElectricMachine(inventory, tileEntity));
        this.tileEntity = tileEntity;
        this.texture = texture;
        this.titleKey = titleKey;
    }

    public ScreenElectricMachine(ContainerInventory inventory, TileEntityElectricMachine tileEntity, String texture, String titleKey, MenuElectricMachine menu) {
        super((MenuAbstract)menu);
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
        if (this.tileEntity.energy > 0) {
            int l = this.tileEntity.gaugeFuelScaled(14);
            this.drawTexturedModalRect(x + 56, y + 36 + 14 - l, 176, 14 - l, 14, l);
        }
        int i1 = this.tileEntity.gaugeProgressScaled(24);
        this.drawTexturedModalRect(x + 79, y + 34, 176, 14, i1 + 1, 16);
    }
}

