package ic2.gui.menu;

import ic2.gui.slot.SlotIC2Battery;
import ic2.tileentity.TileEntityHarvester;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.crafting.ContainerListener;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import net.minecraft.core.player.inventory.slot.Slot;
import net.minecraft.core.player.inventory.slot.SlotFurnace;
import org.jetbrains.annotations.NotNull;

public class MenuHarvester
extends MenuAbstract {
    public final TileEntityHarvester tileEntity;
    public int energy = 0;

    public MenuHarvester(ContainerInventory inventory, TileEntityHarvester tileEntity) {
        this.tileEntity = tileEntity;
        this.addSlot(new SlotIC2Battery((Container)tileEntity, TileEntityHarvester.SLOT_ENERGY, 56, 53));
        int slot = 0;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 3; ++col) {
                this.addSlot((Slot)new SlotFurnace(inventory.player, (Container)tileEntity, slot++, 98 + col * 18, 17 + row * 18));
            }
        }
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
        if (slot.index >= 1 && slot.index < 10) {
            return this.getSlots(1, 9, false);
        }
        if (action == InventoryAction.MOVE_ALL) {
            if (slot.index >= 10 && slot.index < 37) {
                return this.getSlots(10, 27, false);
            }
            if (slot.index >= 37 && slot.index < 46) {
                return this.getSlots(37, 9, false);
            }
        }
        return action == InventoryAction.MOVE_SIMILAR && slot.index >= 10 && slot.index < 46 ? this.getSlots(10, 36, false) : null;
    }

    @Override
    public IntList getTargetSlots(@NotNull InventoryAction action, @NotNull Slot slot, int target, Player player) {
        if (slot.index >= 10 && slot.index < 46) {
            return this.getSlots(1, 9, false);
        }
        if (slot.index >= 0 && slot.index < 10) {
            return this.getSlots(10, 36, false);
        }
        return null;
    }
}
