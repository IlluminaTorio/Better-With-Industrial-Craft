

package ic2.gui.menu;

import ic2.tileentity.TileEntityInduction;
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
import ic2.gui.slot.SlotIC2Battery;

public class MenuInduction
extends MenuAbstract {
    public TileEntityInduction tileEntity;
    public int progress = 0;
    public int energy = 0;

    public MenuInduction(ContainerInventory inventory, TileEntityInduction tileEntity) {
        this.tileEntity = tileEntity;
        this.addSlot(new Slot((Container)tileEntity, 0, 47, 17));
        this.addSlot(new SlotIC2Battery((Container)tileEntity, 1, 56, 53));
        this.addSlot((Slot)new SlotFurnace(inventory.player, (Container)tileEntity, 2, 113, 35));
        this.addSlot(new Slot((Container)tileEntity, 3, 63, 17));
        this.addSlot((Slot)new SlotFurnace(inventory.player, (Container)tileEntity, 4, 131, 35));
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
            if (this.progress != this.tileEntity.progress) {
                crafter.updateCraftingInventoryInfo((MenuAbstract)this, 0, this.tileEntity.progress);
            }
            if (this.energy == this.tileEntity.energy) continue;
            crafter.updateCraftingInventoryInfo((MenuAbstract)this, 1, this.tileEntity.energy);
        }
        this.progress = this.tileEntity.progress;
        this.energy = this.tileEntity.energy;
    }

    public void setData(int id, int value) {
        switch (id) {
            case 0: {
                this.tileEntity.progress = value;
                break;
            }
            case 1: {
                this.tileEntity.energy = value;
            }
        }
    }

    public boolean stillValid(Player player) {
        return this.tileEntity.stillValid(player);
    }

        public IntList getMoveSlots(@NotNull InventoryAction action, @NotNull Slot slot, int target, Player player) {
        if (slot.index >= 0 && slot.index < 5) {
            return this.getSlots(0, 5, false);
        } else if (slot.index >= 5 && slot.index < 32) {
            return this.getSlots(5, 27, false);
        } else {
            return slot.index >= 32 && slot.index < 41 ? this.getSlots(32, 9, false) : null;
        }
    }


        public IntList getTargetSlots(@NotNull InventoryAction action, @NotNull Slot slot, int target, Player player) {
        if (slot.index >= 5 && slot.index < 41) {
            if (action != InventoryAction.MOVE_ALL) {
                return this.getSlots(0, 5, false);
            } else {
                return slot.index < 32 ? this.getSlots(32, 9, false) : this.getSlots(5, 27, false);
            }
        } else if (slot.index < 0 || slot.index >= 5) {
            return null;
        } else if (slot.index == 2) {
            return this.getSlots(5, 36, true);
        } else {
            return slot.index == 4 ? this.getSlots(5, 36, true) : this.getSlots(5, 36, false);
        }
    }

}

