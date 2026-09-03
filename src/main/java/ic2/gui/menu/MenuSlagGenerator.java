package ic2.gui.menu;

import ic2.tileentity.TileEntitySlagGenerator;
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

public class MenuSlagGenerator
extends MenuAbstract {
    public TileEntitySlagGenerator tileEntity;
    public int fuel = 0;
    public int storage = 0;

    public MenuSlagGenerator(ContainerInventory inventory, TileEntitySlagGenerator tileEntity) {
        this.tileEntity = tileEntity;
        this.addSlot(new Slot((Container)tileEntity, 0, 65, 17));
        this.addSlot(new Slot((Container)tileEntity, 1, 65, 53));
        this.addSlot(new SlotFurnace(inventory.player, (Container)tileEntity, 2, 131, 36));
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
            if (this.fuel != this.tileEntity.fuel) {
                crafter.updateCraftingInventoryInfo((MenuAbstract)this, 0, this.tileEntity.fuel);
            }
            if (this.storage != this.tileEntity.storage) {
                crafter.updateCraftingInventoryInfo((MenuAbstract)this, 1, this.tileEntity.storage);
            }
        }
        this.fuel = this.tileEntity.fuel;
        this.storage = this.tileEntity.storage;
    }

    public void setData(int id, int value) {
        switch (id) {
            case 0: {
                this.tileEntity.fuel = value;
                break;
            }
            case 1: {
                this.tileEntity.storage = value;
                break;
            }
        }
    }

    public boolean stillValid(Player player) {
        return this.tileEntity.stillValid(player);
    }

        public IntList getMoveSlots(@NotNull InventoryAction action, @NotNull Slot slot, int target, Player player) {
        if (slot.index >= 0 && slot.index < this.tileEntity.getContainerSize()) {
            return this.getSlots(0, this.tileEntity.getContainerSize(), false);
        } else if (slot.index >= this.tileEntity.getContainerSize() && slot.index < this.tileEntity.getContainerSize() + 27) {
            return this.getSlots(this.tileEntity.getContainerSize(), 27, false);
        } else {
            return slot.index >= this.tileEntity.getContainerSize() + 27 && slot.index < this.tileEntity.getContainerSize() + 36
                ? this.getSlots(this.tileEntity.getContainerSize() + 27, 9, false)
                : null;
        }
    }


        public IntList getTargetSlots(@NotNull InventoryAction action, @NotNull Slot slot, int target, Player player) {
        if (slot.index >= this.tileEntity.getContainerSize() && slot.index < this.tileEntity.getContainerSize() + 36) {
            if (action != InventoryAction.MOVE_ALL) {
                return this.getSlots(0, this.tileEntity.getContainerSize(), false);
            } else {
                return slot.index < this.tileEntity.getContainerSize() + 27
                    ? this.getSlots(this.tileEntity.getContainerSize() + 27, 9, false)
                    : this.getSlots(this.tileEntity.getContainerSize(), 27, false);
            }
        } else if (slot.index < 0 || slot.index >= this.tileEntity.getContainerSize()) {
            return null;
        } else {
            return slot.index == 2 ? this.getSlots(this.tileEntity.getContainerSize(), 36, true) : this.getSlots(this.tileEntity.getContainerSize(), 36, false);
        }
    }

}
