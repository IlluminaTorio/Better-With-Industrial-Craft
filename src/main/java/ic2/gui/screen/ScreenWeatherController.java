package ic2.gui.screen;

import ic2.net.IC2Network;
import ic2.net.MachineEventMessage;
import ic2.tileentity.TileEntityWeatherController;
import net.minecraft.client.gui.ButtonElement;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;

public class ScreenWeatherController
extends ScreenContainerAbstract {
    protected final TileEntityWeatherController tileEntity;
    protected ButtonElement clearButton;
    protected ButtonElement rainButton;
    protected ButtonElement stormButton;

    public ScreenWeatherController(ContainerInventory inventory, TileEntityWeatherController tileEntity) {
        super((MenuAbstract)new ic2.gui.menu.MenuWeatherController(inventory, tileEntity));
        this.tileEntity = tileEntity;
    }

    @Override
    public void init() {
        super.init();
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        I18n i18n = I18n.getInstance();
        this.clearButton = this.add(new ButtonElement(0, x + 8, y + 17, 48, 14, i18n.translateKey("gui.ic2.weather_controller.button.clear")));
        this.rainButton = this.add(new ButtonElement(1, x + 64, y + 17, 48, 14, i18n.translateKey("gui.ic2.weather_controller.button.rain")));
        this.stormButton = this.add(new ButtonElement(2, x + 120, y + 17, 48, 14, i18n.translateKey("gui.ic2.weather_controller.button.storm")));
        this.clearButton.listener = button -> this.sendWeatherEvent(0);
        this.rainButton.listener = button -> this.sendWeatherEvent(1);
        this.stormButton.listener = button -> this.sendWeatherEvent(2);
    }

    private void sendWeatherEvent(int event) {
        MachineEventMessage message = new MachineEventMessage();
        message.x = this.tileEntity.tilePos.x();
        message.y = this.tileEntity.tilePos.y();
        message.z = this.tileEntity.tilePos.z();
        message.event = event;
        IC2Network.sendToServer(message);
    }

    protected void drawGuiContainerForegroundLayer() {
        I18n i18n = I18n.getInstance();
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("gui.ic2.weather_controller.label.title"), 60, 6, 0x404040);
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("container.inventory.name"), 8, this.ySize - 96 + 2, 0x404040);
        int[] costs = new int[]{TileEntityWeatherController.COST_CLEAR, TileEntityWeatherController.COST_RAIN, TileEntityWeatherController.COST_STORM};
        int[] labelX = new int[]{32, 92, 144};
        for (int i = 0; i < 3; ++i) {
            String cost = costs[i] / 1000000 + "M EU";
            int color = this.tileEntity.energy >= costs[i] ? 0x404040 : 0xA02020;
            this.drawStringCenteredNoShadow(this.fontRenderer, cost, labelX[i], 34, color);
        }
        String stored = this.tileEntity.energy + " EU";
        this.drawStringCenteredNoShadow(this.fontRenderer, stored, 88, 73, 0x404040);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTick) {
        GLRenderer.setColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.textureManager.loadTexture("/assets/ic2/textures/gui/GUIWeatherController.png").bind();
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
        if (this.tileEntity.energy > 0) {
            int l = this.tileEntity.gaugeEnergyScaled(14);
            this.drawTexturedModalRect(x + 56, y + 36 + 14 - l, 176, 14 - l, 14, l);
        }
    }
}
