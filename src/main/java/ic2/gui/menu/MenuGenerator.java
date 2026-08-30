

package ic2.gui.menu;

import ic2.tileentity.TileEntityBaseGenerator;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.crafting.ContainerListener;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import net.minecraft.core.player.inventory.slot.Slot;
import org.jetbrains.annotations.NotNull;

public class MenuGenerator
extends MenuAbstract {
    public TileEntityBaseGenerator tileEntity;
    public int fuel = 0;
    public int storage = 0;

    public MenuGenerator(ContainerInventory inventory, TileEntityBaseGenerator tileEntity) {
        this.tileEntity = tileEntity;
        int[] charge = tileEntity.getChargeSlotPos();
        this.addSlot(new Slot((Container)tileEntity, 0, charge[0], charge[1]));
        if (tileEntity.getContainerSize() > 1) {
            int[] fuelPos = tileEntity.getFuelSlotPos();
            this.addSlot(new Slot((Container)tileEntity, 1, fuelPos[0], fuelPos[1]));
        }
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                this.addSlot(new Slot((Container)inventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot((Container)inventory, i, 8 + i * 18, 142));
        }
    }

    public void broadcastChanges() {
        super.broadcastChanges();
        for (ContainerListener crafter : this.containerListeners) {
            if (this.fuel != this.tileEntity.fuel) {
                crafter.updateCraftingInventoryInfo((MenuAbstract)this, 0, this.tileEntity.fuel);
            }
            if (this.storage == this.tileEntity.storage) continue;
            crafter.updateCraftingInventoryInfo((MenuAbstract)this, 1, this.tileEntity.storage);
        }
        this.fuel = this.tileEntity.fuel;
        this.storage = this.tileEntity.storage;
    }

    public void setData(int id, int value) {
        switch (id) {
            case 0: {
                this.tileEntity.fuel = value;
                break;
            }
            case 1: {
                this.tileEntity.storage = value;
            }
        }
    }

    public boolean stillValid(Player player) {
        return this.tileEntity.stillValid(player);
    }

    public IntList getMoveSlots(@NotNull InventoryAction action, @NotNull Slot slot, int target, Player player) {
        int machineSlots = this.tileEntity.getContainerSize();
        if (slot.index >= 0 && slot.index < machineSlots) {
            return this.getSlots(0, machineSlots, false);
        }
        return this.getSlots(machineSlots, 36, false);
    }

    public IntList getTargetSlots(@NotNull InventoryAction action, @NotNull Slot slot, int target, Player player) {
        int machineSlots = this.tileEntity.getContainerSize();
        if (slot.index >= machineSlots && slot.index < machineSlots + 36) {
            return this.getSlots(0, machineSlots, false);
        }
        return null;
    }
}

