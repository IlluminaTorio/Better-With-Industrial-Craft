

package ic2.gui.menu;

import ic2.tileentity.TileEntityMiner;
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

public class MenuMiner
extends MenuAbstract {
    public TileEntityMiner tileEntity;
    public int miningTicker = 0;
    public int energy = 0;

    public MenuMiner(ContainerInventory inventory, TileEntityMiner tileEntity) {
        this.tileEntity = tileEntity;
        this.addSlot(new SlotIC2Battery((Container)tileEntity, 0, 81, 59));
        this.addSlot(new Slot((Container)tileEntity, 1, 117, 22));
        this.addSlot(new Slot((Container)tileEntity, 2, 81, 22));
        this.addSlot((Slot)new SlotFurnace(inventory.player, (Container)tileEntity, 3, 45, 22));
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
            if (this.energy != this.tileEntity.energy) {
                crafter.updateCraftingInventoryInfo((MenuAbstract)this, 0, this.tileEntity.energy);
            }
            if (this.miningTicker == this.tileEntity.miningTicker) continue;
            crafter.updateCraftingInventoryInfo((MenuAbstract)this, 1, this.tileEntity.miningTicker);
        }
        this.energy = this.tileEntity.energy;
        this.miningTicker = this.tileEntity.miningTicker;
    }

    public void setData(int id, int value) {
        switch (id) {
            case 0: {
                this.tileEntity.energy = value;
                break;
            }
            case 1: {
                this.tileEntity.miningTicker = value;
            }
        }
    }

    public boolean stillValid(Player player) {
        return this.tileEntity.stillValid(player);
    }

        public IntList getMoveSlots(@NotNull InventoryAction action, @NotNull Slot slot, int target, Player player) {
        if (slot.index >= 0 && slot.index < 4) {
            return this.getSlots(0, 4, false);
        } else if (slot.index >= 4 && slot.index < 31) {
            return this.getSlots(4, 27, false);
        } else {
            return slot.index >= 31 && slot.index < 40 ? this.getSlots(31, 9, false) : null;
        }
    }


        public IntList getTargetSlots(@NotNull InventoryAction action, @NotNull Slot slot, int target, Player player) {
        if (slot.index >= 4 && slot.index < 40) {
            if (action != InventoryAction.MOVE_ALL) {
                return this.getSlots(0, 4, false);
            } else {
                return slot.index < 31 ? this.getSlots(31, 9, false) : this.getSlots(4, 27, false);
            }
        } else if (slot.index < 0 || slot.index >= 4) {
            return null;
        } else {
            return slot.index == 3 ? this.getSlots(4, 36, true) : this.getSlots(4, 36, false);
        }
    }

}

