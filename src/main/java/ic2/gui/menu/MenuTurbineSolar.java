package ic2.gui.menu;

import ic2.tileentity.TileEntityTurbineSolar;
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

public class MenuTurbineSolar
extends MenuAbstract {
    public TileEntityTurbineSolar tileEntity;
    public int heat = 0;
    public int water = 0;

    public MenuTurbineSolar(ContainerInventory inventory, TileEntityTurbineSolar tileEntity) {
        this.tileEntity = tileEntity;
        this.addSlot(new Slot((Container)tileEntity, 0, 62, 17));
        this.addSlot(new Slot((Container)tileEntity, 1, 62, 53));
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
            if (this.heat != this.tileEntity.heat) {
                crafter.updateCraftingInventoryInfo((MenuAbstract)this, 0, this.tileEntity.heat);
            }
            if (this.water != this.tileEntity.water) {
                crafter.updateCraftingInventoryInfo((MenuAbstract)this, 1, this.tileEntity.water);
            }
        }
        this.heat = this.tileEntity.heat;
        this.water = this.tileEntity.water;
    }

    public void setData(int id, int value) {
        switch (id) {
            case 0: {
                this.tileEntity.heat = value;
                break;
            }
            case 1: {
                this.tileEntity.water = value;
                break;
            }
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
