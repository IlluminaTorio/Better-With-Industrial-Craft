package ic2.gui.screen;

import ic2.net.IC2Network;
import ic2.net.MachineEventMessage;
import ic2.tileentity.TileEntityDroneDepot;
import net.minecraft.client.gui.ButtonElement;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;

public class ScreenDroneDepot
extends ScreenContainerAbstract {
    protected final TileEntityDroneDepot tileEntity;
    protected final ic2.gui.menu.MenuDroneDepot menuDepot;
    protected ButtonElement downButton;
    protected ButtonElement upButton;

    public ScreenDroneDepot(ContainerInventory inventory, TileEntityDroneDepot tileEntity) {
        super((MenuAbstract)new ic2.gui.menu.MenuDroneDepot(inventory, tileEntity));
        this.tileEntity = tileEntity;
        this.menuDepot = (ic2.gui.menu.MenuDroneDepot)this.inventorySlots;
    }

    @Override
    public void init() {
        super.init();
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.downButton = this.add(new ButtonElement(0, x + 78, y + 17, 24, 14, "-"));
        this.upButton = this.add(new ButtonElement(1, x + 146, y + 17, 24, 14, "+"));
        this.downButton.listener = button -> this.sendEvent(TileEntityDroneDepot.EVENT_CHANNEL_DOWN);
        this.upButton.listener = button -> this.sendEvent(TileEntityDroneDepot.EVENT_CHANNEL_UP);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.mc.currentWorld.isClientSide) {
            this.inventorySlots.broadcastChanges();
        }
    }

    private void sendEvent(int event) {
        MachineEventMessage message = new MachineEventMessage();
        message.x = this.tileEntity.tilePos.x();
        message.y = this.tileEntity.tilePos.y();
        message.z = this.tileEntity.tilePos.z();
        message.event = event;
        IC2Network.sendToServer(message);
    }

    protected void drawGuiContainerForegroundLayer() {
        I18n i18n = I18n.getInstance();
        this.drawStringCenteredNoShadow(this.fontRenderer, i18n.translateKey("gui.ic2.drone_depot.label.title"), 88, 6, 0x404040);
        this.drawStringNoShadow(this.fontRenderer, i18n.translateKey("container.inventory.name"), 8, this.ySize - 96 + 2, 0x404040);
        boolean authoritative = !this.mc.currentWorld.isClientSide;
        int channel = authoritative ? this.tileEntity.channel : this.menuDepot.channel;
        boolean linked = authoritative ? this.tileEntity.hasPeer() : this.menuDepot.peerStatus != 0;
        String channelLabel = i18n.translateKey("gui.ic2.drone_depot.label.channel") + channel;
        this.drawStringCenteredNoShadow(this.fontRenderer, channelLabel, 113, 21, 0x404040);
        String status = linked ? i18n.translateKey("gui.ic2.drone_depot.label.linked") : i18n.translateKey("gui.ic2.drone_depot.label.no_peer");
        this.drawStringCenteredNoShadow(this.fontRenderer, status, 113, 53, linked ? 0x207020 : 0xA02020);
        this.drawStringCenteredNoShadow(this.fontRenderer, i18n.translateKey("gui.ic2.drone_depot.label.redstone"), 113, 66, 0x606060);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTick) {
        GLRenderer.setColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.textureManager.loadTexture("/assets/ic2/textures/gui/GUIDroneDepot.png").bind();
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
    }
}
