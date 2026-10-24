package ic2.gui.screen;

import ic2.gui.menu.MenuDispatcherTerminal;
import ic2.net.TerminalDataMessage;
import java.util.List;
import net.minecraft.client.gui.ItemElement;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;

public class ScreenDispatcherTerminal
extends ScreenContainerAbstract {
    protected final ic2.tileentity.TileEntityDispatcherTerminal tileEntity;
    protected final MenuDispatcherTerminal menuTerminal;
    private final ItemElement itemElement;
    private static final int ROW_H = 22;
    private static final int MAX_ROWS = 6;

    public ScreenDispatcherTerminal(ContainerInventory inventory, ic2.tileentity.TileEntityDispatcherTerminal tileEntity) {
        super((MenuAbstract)new MenuDispatcherTerminal(inventory, tileEntity));
        this.tileEntity = tileEntity;
        this.menuTerminal = (MenuDispatcherTerminal)this.inventorySlots;
        this.itemElement = new ItemElement(this.mc);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.mc.currentWorld.isClientSide) {
            this.inventorySlots.broadcastChanges();
        }
    }

    protected void drawGuiContainerForegroundLayer() {
        I18n i18n = I18n.getInstance();
        this.drawStringCenteredNoShadow(this.fontRenderer, i18n.translateKey("gui.ic2.terminal.label.title"), 88, 6, 0x40FF70);
        String operator = i18n.translateKey("gui.ic2.terminal.label.operator") + (this.tileEntity.owner == null ? "-" : this.tileEntity.owner);
        this.drawStringNoShadow(this.fontRenderer, operator, 10, 153, 0x2E8B57);
        List<TerminalDataMessage.DroneView> drones = this.menuTerminal.drones;
        if (drones.isEmpty()) {
            this.drawStringCenteredNoShadow(this.fontRenderer, i18n.translateKey("gui.ic2.terminal.label.no_drones"), 88, 78, 0x1F7A46);
            return;
        }
        int shown = Math.min(drones.size(), MAX_ROWS);
        for (int i = 0; i < shown; ++i) {
            TerminalDataMessage.DroneView view = drones.get(i);
            int rowY = 17 + i * ROW_H;
            boolean returning = view.phase == 1;
            int labelColor = returning ? 0xFFB040 : 0x40FF70;
            String arrow = returning ? i18n.translateKey("gui.ic2.terminal.label.returning") : i18n.translateKey("gui.ic2.terminal.label.dest");
            String line = i18n.translateKey("gui.ic2.terminal.label.drone") + (i + 1) + " " + arrow + " " + view.targetX + "," + view.targetY + "," + view.targetZ;
            this.drawStringNoShadow(this.fontRenderer, line, 10, rowY, labelColor);
            int iconX = 10;
            for (int k = 0; k < view.cargo.size() && k < 8; ++k) {
                this.itemElement.render(view.cargo.get(k), iconX, rowY + 10);
                iconX += 16;
            }
            if (view.cargo.size() <= 8) continue;
            this.drawStringNoShadow(this.fontRenderer, "+" + (view.cargo.size() - 8), iconX + 2, rowY + 14, 0x40FF70);
        }
    }

    protected void drawGuiContainerBackgroundLayer(float partialTick) {
        GLRenderer.setColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.textureManager.loadTexture("/assets/ic2/textures/gui/GUIDispatcherTerminal.png").bind();
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
    }
}
