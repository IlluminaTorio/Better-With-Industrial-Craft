package ic2.gui.menu;

import ic2.gui.slot.SlotIC2Battery;
import ic2.tileentity.TileEntitySprinkler;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.crafting.ContainerListener;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import net.minecraft.core.player.inventory.slot.Slot;
import org.jetbrains.annotations.NotNull;

public class MenuSprinkler
extends MenuAbstract {
    public final TileEntitySprinkler tileEntity;
    public int energy = 0;

    public MenuSprinkler(ContainerInventory inventory, TileEntitySprinkler tileEntity) {
        this.tileEntity = tileEntity;
        this.addSlot(new SlotIC2Battery((Container)tileEntity, 0, 56, 53));
        for (int i = 0; i < 3; ++i) {
            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot((Container)inventory, k + i * 9 + 9, 8 + k * 18, 84 + i * 18));
            }
        }
        for (int j = 0; j < 9; ++j) {
            this.addSlot(new Slot((Container)inventory, j, 8 + j * 18, 142));
        }
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        for (ContainerListener listener : this.containerListeners) {
            if (this.energy != this.tileEntity.energy) {
                listener.updateCraftingInventoryInfo((MenuAbstract)this, 0, this.tileEntity.energy);
            }
        }
        this.energy = this.tileEntity.energy;
    }

    @Override
    public void setData(int id, int value) {
        if (id == 0) {
            this.tileEntity.energy = value;
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.tileEntity.stillValid(player);
    }

    @Override
    public IntList getMoveSlots(@NotNull InventoryAction action, @NotNull Slot slot, int target, Player player) {
        if (slot.index == 0) {
            return this.getSlots(0, 1, false);
        }
        if (action == InventoryAction.MOVE_ALL) {
            if (slot.index >= 1 && slot.index < 28) {
                return this.getSlots(1, 27, false);
            }
            if (slot.index >= 28 && slot.index < 37) {
                return this.getSlots(28, 9, false);
            }
        }
        return action == InventoryAction.MOVE_SIMILAR && slot.index >= 1 && slot.index < 37 ? this.getSlots(1, 36, false) : null;
    }

    @Override
    public IntList getTargetSlots(@NotNull InventoryAction action, @NotNull Slot slot, int target, Player player) {
        if (slot.index >= 1 && slot.index < 37) {
            if (target == 1) {
                return this.getSlots(0, 1, false);
            }
            if (slot.index >= 1 && slot.index < 28) {
                return this.getSlots(28, 9, false);
            }
            return this.getSlots(1, 27, false);
        }
        if (slot.index != 0) {
            return null;
        }
        return this.getSlots(1, 36, false);
    }
}
