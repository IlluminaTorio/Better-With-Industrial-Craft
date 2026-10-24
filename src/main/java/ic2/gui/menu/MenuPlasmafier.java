package ic2.gui.menu;

import ic2.tileentity.TileEntityPlasmafier;
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
import ic2.gui.slot.SlotIC2Item;
import ic2.IC2Items;

public class MenuPlasmafier
extends MenuAbstract {
    public TileEntityPlasmafier tileEntity;
    public int energy = 0;
    public int potential = 0;
    public int plasma = 0;

    public MenuPlasmafier(ContainerInventory inventory, TileEntityPlasmafier tileEntity) {
        this.tileEntity = tileEntity;
        this.addSlot(new SlotIC2Item((Container)tileEntity, 0, 44, 35, IC2Items.uuMatter));
        this.addSlot(new SlotIC2Item((Container)tileEntity, 1, 116, 23, IC2Items.cellEmpty));
        this.addSlot(new SlotFurnace(inventory.player, (Container)tileEntity, 2, 116, 45));
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
            if (this.potential != this.tileEntity.potential) {
                crafter.updateCraftingInventoryInfo((MenuAbstract)this, 1, this.tileEntity.potential);
            }
            if (this.plasma != this.tileEntity.plasma) {
                crafter.updateCraftingInventoryInfo((MenuAbstract)this, 2, this.tileEntity.plasma);
            }
        }
        this.energy = this.tileEntity.energy;
        this.potential = this.tileEntity.potential;
        this.plasma = this.tileEntity.plasma;
    }

    public void setData(int id, int value) {
        switch (id) {
            case 0: {
                this.tileEntity.energy = value;
                break;
            }
            case 1: {
                this.tileEntity.potential = value;
                break;
            }
            case 2: {
                this.tileEntity.plasma = value;
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
