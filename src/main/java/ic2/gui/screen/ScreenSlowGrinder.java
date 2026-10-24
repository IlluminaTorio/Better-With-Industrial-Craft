package ic2.gui.screen;

import ic2.gui.menu.MenuSlowGrinder;
import ic2.net.IC2Network;
import ic2.net.MachineEventMessage;
import ic2.tileentity.TileEntitySlowGrinder;
import net.minecraft.client.gui.ButtonElement;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;

public class ScreenSlowGrinder
extends ScreenContainerAbstract {
    protected final TileEntitySlowGrinder tileEntity;
    protected ButtonElement speedButton;

    public ScreenSlowGrinder(ContainerInventory inventory, TileEntitySlowGrinder tileEntity) {
        super((MenuAbstract)new MenuSlowGrinder(inventory, tileEntity));
        this.tileEntity = tileEntity;
    }

    public void init() {
        super.init();
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.speedButton = this.add(new ButtonElement(0, x + 102, y + 15, 44, 11, "Speed: " + this.tileEntity.speed));
        this.speedButton.listener = button -> this.sendSpeedEvent();
    }

    private void sendSpeedEvent() {
        MachineEventMessage message = new MachineEventMessage();
        message.x = this.tileEntity.tilePos.x();
        message.y = this.tileEntity.tilePos.y();
        message.z = this.tileEntity.tilePos.z();
        message.event = 0;
        IC2Network.sendToServer(message);
    }

    protected void drawGuiContainerForegroundLayer() {
        I18n i18n = I18n.getInstance();
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("tile.ic2.machine.slow_grinder.name"), 58, 6, 0x404040);
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("container.inventory.name"), 80, this.ySize - 96 + 2, 0x404040);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTick) {
        this.mc.textureManager.loadTexture("/assets/ic2/textures/gui/GUISlowGrinder.png").bind();
        GLRenderer.setColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
        int j1 = (int)(this.tileEntity.getChargeLevel() * 14.0f);
        int k1 = this.tileEntity.gaugeProgressScaled(24);
        if (j1 > 0) {
            this.drawTexturedModalRect(x + 56, y + 36 + 14 - j1, 176, 14 - j1, 14, j1);
        }
        if (k1 > 0) {
            this.drawTexturedModalRect(x + 79, y + 34, 176, 14, k1 + 1, 16);
        }
        if (this.speedButton != null) {
            this.speedButton.displayString = "Speed: " + this.tileEntity.speed;
        }
    }
}
