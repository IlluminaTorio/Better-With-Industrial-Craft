package ic2.gui.menu;

import ic2.gui.slot.SlotIC2Battery;
import ic2.tileentity.TileEntityElectricTrommel;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.crafting.ContainerListener;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import net.minecraft.core.player.inventory.slot.Slot;
import org.jetbrains.annotations.NotNull;

public class MenuElectricTrommel
extends MenuAbstract {
    public final TileEntityElectricTrommel tileEntity;
    public int progress = 0;
    public int energy = 0;

    public MenuElectricTrommel(ContainerInventory inventory, TileEntityElectricTrommel tileEntity) {
        this.tileEntity = tileEntity;
        this.addSlot(new Slot((Container)tileEntity, 0, 105, 30));
        this.addSlot(new Slot((Container)tileEntity, 1, 85, 50));
        this.addSlot(new Slot((Container)tileEntity, 2, 125, 50));
        this.addSlot(new Slot((Container)tileEntity, 3, 105, 70));
        this.addSlot(new SlotIC2Battery((Container)tileEntity, 4, 33, 50));
        for (int i = 0; i < 3; ++i) {
            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot((Container)inventory, k + i * 9 + 9, 8 + k * 18, 110 + i * 18));
            }
        }
        for (int j = 0; j < 9; ++j) {
            this.addSlot(new Slot((Container)inventory, j, 8 + j * 18, 168));
        }
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        for (ContainerListener listener : this.containerListeners) {
            if (this.progress != this.tileEntity.progress) {
                listener.updateCraftingInventoryInfo((MenuAbstract)this, 0, this.tileEntity.progress);
            }
            if (this.energy == this.tileEntity.energy) continue;
            listener.updateCraftingInventoryInfo((MenuAbstract)this, 1, this.tileEntity.energy);
        }
        this.progress = this.tileEntity.progress;
        this.energy = this.tileEntity.energy;
    }

    @Override
    public void setData(int id, int value) {
        if (id == 0) {
            this.tileEntity.progress = value;
        }
        if (id == 1) {
            this.tileEntity.energy = value;
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.tileEntity.stillValid(player);
    }

    @Override
    public IntList getMoveSlots(@NotNull InventoryAction action, @NotNull Slot slot, int target, Player player) {
        if (slot.index >= 0 && slot.index < 4) {
            return this.getSlots(0, 4, false);
        }
        if (slot.index == 4) {
            return this.getSlots(4, 1, false);
        }
        if (action == InventoryAction.MOVE_ALL) {
            if (slot.index >= 5 && slot.index < 32) {
                return this.getSlots(5, 27, false);
            }
            if (slot.index >= 32 && slot.index < 41) {
                return this.getSlots(32, 9, false);
            }
        }
        return action == InventoryAction.MOVE_SIMILAR && slot.index >= 5 && slot.index < 41 ? this.getSlots(5, 36, false) : null;
    }

    @Override
    public IntList getTargetSlots(@NotNull InventoryAction action, @NotNull Slot slot, int target, Player player) {
        if (slot.index >= 0 && slot.index < 5) {
            return this.getSlots(5, 36, false);
        }
        if (slot.index >= 5 && slot.index < 41) {
            if (target == 1) {
                return this.getSlots(0, 4, false);
            }
            if (target == 2) {
                return this.getSlots(4, 1, false);
            }
            if (slot.index >= 5 && slot.index < 32) {
                return this.getSlots(32, 9, false);
            }
            if (slot.index >= 32 && slot.index < 41) {
                return this.getSlots(5, 27, false);
            }
        }
        return null;
    }
}
