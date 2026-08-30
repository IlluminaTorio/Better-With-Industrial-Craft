

package ic2.gui.menu;

import ic2.tileentity.TileEntityCanner;
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

public class MenuCanner
extends MenuAbstract {
    public TileEntityCanner tileEntity;
    public int progress = 0;
    public int energy = 0;

    public MenuCanner(ContainerInventory inventory, TileEntityCanner tileEntity) {
        this.tileEntity = tileEntity;
        this.addSlot(new Slot((Container)tileEntity, 0, 69, 17));
        this.addSlot(new Slot((Container)tileEntity, 1, 30, 45));
        this.addSlot((Slot)new SlotFurnace(inventory.player, (Container)tileEntity, 2, 119, 35));
        this.addSlot(new Slot((Container)tileEntity, 3, 69, 53));
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
        if (slot.index >= 0 && slot.index < 4) {
            return this.getSlots(0, 4, false);
        }
        if (action == InventoryAction.MOVE_ALL) {
            if (slot.index >= 4 && slot.index < 4) {
                return this.getSlots(4, 27, false);
            }
            if (slot.index >= 4 && slot.index < 40) {
                return this.getSlots(4, 9, false);
            }
        }
        if (slot.index >= 4 && slot.index < 40) {
            return this.getSlots(4, 36, false);
        }
        return null;
    }

    public IntList getTargetSlots(@NotNull InventoryAction action, @NotNull Slot slot, int target, Player player) {
        if (slot.index >= 4 && slot.index < 40) {
            if (slot.index >= 4 && slot.index < 4) {
                return this.getSlots(4, 9, false);
            }
            return this.getSlots(4, 36, false);
        }
        if (slot.index >= 0 && slot.index < 4) {
            if (slot.index == 2) {
                return this.getSlots(4, 36, true);
            }
            return null;
        }
        return null;
    }
}

