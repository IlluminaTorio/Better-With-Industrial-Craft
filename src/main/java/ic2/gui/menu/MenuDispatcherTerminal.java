package ic2.gui.menu;

import ic2.net.IC2Network;
import ic2.net.TerminalDataMessage;
import ic2.tileentity.TileEntityDispatcherTerminal;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import net.minecraft.core.player.inventory.slot.Slot;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class MenuDispatcherTerminal
extends MenuAbstract {
    public final TileEntityDispatcherTerminal tileEntity;
    public final List<TerminalDataMessage.DroneView> drones = new ArrayList();
    private int tickCounter = 0;

    public MenuDispatcherTerminal(ContainerInventory inventory, TileEntityDispatcherTerminal tileEntity) {
        this.tileEntity = tileEntity;
    }

    public void applyServerData(List<TerminalDataMessage.DroneView> views) {
        this.drones.clear();
        this.drones.addAll(views);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        World world = this.tileEntity.worldObj;
        if (world == null || world.isClientSide) {
            return;
        }
        ++this.tickCounter;
        if (this.tickCounter < 10) {
            return;
        }
        this.tickCounter = 0;
        List<TerminalDataMessage.DroneView> views = TerminalDataMessage.scan(world, this.tileEntity.owner);
        this.applyServerData(views);
        IC2Network.broadcastTerminal(this.tileEntity, views);
    }

    @Override
    public void setData(int id, int value) {
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.tileEntity.stillValid(player);
    }

    @Override
    public IntList getMoveSlots(@NotNull InventoryAction action, @NotNull Slot slot, int target, Player player) {
        return null;
    }

    @Override
    public IntList getTargetSlots(@NotNull InventoryAction action, @NotNull Slot slot, int target, Player player) {
        return null;
    }
}
