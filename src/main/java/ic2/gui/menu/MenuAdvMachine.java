package ic2.gui.menu;

import ic2.gui.slot.SlotIC2Battery;
import ic2.gui.slot.SlotIC2Input;
import ic2.tileentity.TileEntityAdvMachine;
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

public class MenuAdvMachine
extends MenuAbstract {
    public final TileEntityAdvMachine tileEntity;
    public int progress = 0;
    public int energy = 0;
    public int speed = 0;

    public MenuAdvMachine(ContainerInventory inventory, TileEntityAdvMachine tileEntity) {
        this.tileEntity = tileEntity;
        this.addSlot(new SlotIC2Input((Container)tileEntity, 0, 56, 17));
        this.addSlot(new SlotIC2Battery((Container)tileEntity, 1, 56, 53));
        this.addSlot((Slot)new SlotFurnace(inventory.player, (Container)tileEntity, 2, 116, 35));
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
            if (this.energy != this.tileEntity.energy) {
                listener.updateCraftingInventoryInfo((MenuAbstract)this, 1, this.tileEntity.energy);
            }
            if (this.speed == this.tileEntity.speed) continue;
            listener.updateCraftingInventoryInfo((MenuAbstract)this, 2, this.tileEntity.speed);
        }
        this.progress = this.tileEntity.progress;
        this.energy = this.tileEntity.energy;
        this.speed = this.tileEntity.speed;
    }

    @Override
    public void setData(int id, int value) {
        if (id == 0) {
            this.tileEntity.progress = value;
        }
        if (id == 1) {
            this.tileEntity.energy = value;
        }
        if (id == 2) {
            this.tileEntity.speed = value;
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.tileEntity.stillValid(player);
    }

    @Override
    public IntList getMoveSlots(@NotNull InventoryAction action, @NotNull Slot slot, int target, Player player) {
        if (slot.index >= 0 && slot.index < 3) {
            return this.getSlots(0, 3, false);
        }
        if (slot.index >= 3 && slot.index < 30) {
            return this.getSlots(3, 27, false);
        }
        return slot.index >= 30 && slot.index < 39 ? this.getSlots(30, 9, false) : null;
    }

    @Override
    public IntList getTargetSlots(@NotNull InventoryAction action, @NotNull Slot slot, int target, Player player) {
        if (slot.index >= 3 && slot.index < 39) {
            if (action != InventoryAction.MOVE_ALL) {
                return this.getSlots(0, 3, false);
            }
            return slot.index < 30 ? this.getSlots(30, 9, false) : this.getSlots(3, 27, false);
        }
        if (slot.index < 0 || slot.index >= 3) {
            return null;
        }
        return slot.index == 2 ? this.getSlots(3, 36, true) : this.getSlots(3, 36, false);
    }
}
