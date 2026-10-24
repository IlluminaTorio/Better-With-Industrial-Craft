package ic2.gui.menu;

import ic2.gui.slot.SlotIC2Battery;
import ic2.tileentity.TileEntityElectricBlastFurnace;
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

public class MenuElectricBlastFurnace
extends MenuAbstract {
    public final TileEntityElectricBlastFurnace tileEntity;
    public int progress = 0;
    public int energy = 0;

    public MenuElectricBlastFurnace(ContainerInventory inventory, TileEntityElectricBlastFurnace tileEntity) {
        this.tileEntity = tileEntity;
        this.addSlot(new Slot((Container)tileEntity, TileEntityElectricBlastFurnace.SLOT_LEFT, 46, 17));
        this.addSlot(new Slot((Container)tileEntity, TileEntityElectricBlastFurnace.SLOT_RIGHT, 66, 17));
        this.addSlot(new SlotIC2Battery((Container)tileEntity, TileEntityElectricBlastFurnace.SLOT_ENERGY, 56, 53));
        this.addSlot(new SlotFurnace(inventory.player, tileEntity, TileEntityElectricBlastFurnace.SLOT_RESULT, 116, 35));
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
        if (slot.index >= 0 && slot.index < 2) {
            return this.getSlots(0, 2, false);
        }
        if (slot.index == 2) {
            return this.getSlots(2, 1, false);
        }
        if (slot.index == 3) {
            return this.getSlots(3, 1, false);
        }
        if (action == InventoryAction.MOVE_ALL) {
            if (slot.index >= 4 && slot.index < 31) {
                return this.getSlots(4, 27, false);
            }
            if (slot.index >= 31 && slot.index < 40) {
                return this.getSlots(31, 9, false);
            }
        }
        return action == InventoryAction.MOVE_SIMILAR && slot.index >= 4 && slot.index < 40 ? this.getSlots(4, 36, false) : null;
    }

    @Override
    public IntList getTargetSlots(@NotNull InventoryAction action, @NotNull Slot slot, int target, Player player) {
        if (slot.index >= 4 && slot.index < 40) {
            if (target == 1) {
                return this.getSlots(0, 2, false);
            }
            if (target == 2) {
                return this.getSlots(2, 1, false);
            }
            if (slot.index >= 4 && slot.index < 31) {
                return this.getSlots(31, 9, false);
            }
            if (slot.index >= 31 && slot.index < 40) {
                return this.getSlots(4, 27, false);
            }
        }
        if (slot.index < 0 || slot.index >= 4) {
            return null;
        }
        return slot.index == 3 ? this.getSlots(4, 36, true) : this.getSlots(4, 36, false);
    }
}
