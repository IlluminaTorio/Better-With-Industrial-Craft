package ic2.gui.menu;

import ic2.tileentity.TileEntitySlowGrinder;
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

public class MenuSlowGrinder
extends MenuAbstract {
    public TileEntitySlowGrinder tileEntity;
    public int progress = 0;
    public int energy = 0;
    public int speed = 0;

    public MenuSlowGrinder(ContainerInventory inventory, TileEntitySlowGrinder tileEntity) {
        this.tileEntity = tileEntity;
        this.addSlot(new Slot((Container)tileEntity, 0, 56, 17));
        this.addSlot(new Slot((Container)tileEntity, 1, 56, 53));
        this.addSlot(new SlotFurnace(inventory.player, (Container)tileEntity, 2, 116, 35));
        for (int j = 0; j < 2; ++j) {
            for (int i = 0; i < 4; ++i) {
                this.addSlot(new Slot((Container)tileEntity, 3 + i + j * 4, 8 + j * 18, 8 + i * 18));
            }
        }
        this.addSlot(new SlotFurnace(inventory.player, (Container)tileEntity, 11, 152, 8));
        this.addSlot(new SlotFurnace(inventory.player, (Container)tileEntity, 12, 152, 26));
        this.addSlot(new SlotFurnace(inventory.player, (Container)tileEntity, 13, 152, 44));
        this.addSlot(new SlotFurnace(inventory.player, (Container)tileEntity, 14, 152, 62));
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
            if (this.energy != this.tileEntity.energy) {
                crafter.updateCraftingInventoryInfo((MenuAbstract)this, 1, this.tileEntity.energy);
            }
            if (this.speed != this.tileEntity.speed) {
                crafter.updateCraftingInventoryInfo((MenuAbstract)this, 2, this.tileEntity.speed);
            }
        }
        this.progress = this.tileEntity.progress;
        this.energy = this.tileEntity.energy;
        this.speed = this.tileEntity.speed;
    }

    public void setData(int id, int value) {
        switch (id) {
            case 0: {
                this.tileEntity.progress = value;
                break;
            }
            case 1: {
                this.tileEntity.energy = value;
                break;
            }
            case 2: {
                this.tileEntity.speed = value;
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
        } else if (slot.index == 2) {
            return this.getSlots(this.tileEntity.getContainerSize(), 36, true);
        } else if (slot.index == 11) {
            return this.getSlots(this.tileEntity.getContainerSize(), 36, true);
        } else if (slot.index == 12) {
            return this.getSlots(this.tileEntity.getContainerSize(), 36, true);
        } else if (slot.index == 13) {
            return this.getSlots(this.tileEntity.getContainerSize(), 36, true);
        } else {
            return slot.index == 14 ? this.getSlots(this.tileEntity.getContainerSize(), 36, true) : this.getSlots(this.tileEntity.getContainerSize(), 36, false);
        }
    }

}
