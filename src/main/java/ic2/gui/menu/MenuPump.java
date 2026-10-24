

package ic2.gui.menu;

import ic2.tileentity.TileEntityPump;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.crafting.ContainerListener;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import net.minecraft.core.player.inventory.slot.Slot;
import org.jetbrains.annotations.NotNull;
import ic2.gui.slot.SlotIC2Battery;
import ic2.gui.slot.SlotIC2Input;

public class MenuPump
extends MenuAbstract {
    public TileEntityPump tileEntity;
    public int energy = 0;

    public MenuPump(ContainerInventory inventory, TileEntityPump tileEntity) {
        this.tileEntity = tileEntity;
        this.addSlot(new SlotIC2Input((Container)tileEntity, 0, 69, 17));
        this.addSlot(new SlotIC2Battery((Container)tileEntity, 1, 69, 53));
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
            if (this.energy == this.tileEntity.energy) continue;
            crafter.updateCraftingInventoryInfo((MenuAbstract)this, 0, this.tileEntity.energy);
        }
        this.energy = this.tileEntity.energy;
    }

    public void setData(int id, int value) {
        if (id == 0) {
            this.tileEntity.energy = value;
        }
    }

    public boolean stillValid(Player player) {
        return this.tileEntity.stillValid(player);
    }

        public IntList getMoveSlots(@NotNull InventoryAction action, @NotNull Slot slot, int target, Player player) {
        if (slot.index >= 0 && slot.index < 2) {
            return this.getSlots(0, 2, false);
        } else if (slot.index >= 2 && slot.index < 29) {
            return this.getSlots(2, 27, false);
        } else {
            return slot.index >= 29 && slot.index < 38 ? this.getSlots(29, 9, false) : null;
        }
    }


        public IntList getTargetSlots(@NotNull InventoryAction action, @NotNull Slot slot, int target, Player player) {
        if (slot.index >= 2 && slot.index < 38) {
            if (action != InventoryAction.MOVE_ALL) {
                return this.getSlots(0, 2, false);
            } else {
                return slot.index < 29 ? this.getSlots(29, 9, false) : this.getSlots(2, 27, false);
            }
        } else {
            return slot.index >= 0 && slot.index < 2 ? this.getSlots(2, 36, false) : null;
        }
    }

}

