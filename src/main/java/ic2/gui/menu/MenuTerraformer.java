package ic2.gui.menu;

import ic2.tileentity.TileEntityTerraformer;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.crafting.ContainerListener;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import net.minecraft.core.player.inventory.slot.Slot;
import org.jetbrains.annotations.NotNull;

public class MenuTerraformer
extends MenuAbstract {
    public TileEntityTerraformer tileEntity;
    public int energy = 0;

    public MenuTerraformer(ContainerInventory inventory, TileEntityTerraformer tileEntity) {
        this.tileEntity = tileEntity;
        this.addSlot(new Slot((Container)tileEntity, 0, 79, 35){
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack != null && stack.getItem() instanceof ic2.item.ItemTFBPPlaceable;
            }
        });
        this.addPlayerSlots(inventory);
    }

    protected void addPlayerSlots(ContainerInventory inventory) {
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
        switch (id) {
            case 0: {
                this.tileEntity.energy = value;
                break;
            }
        }
    }

    public boolean stillValid(Player player) {
        return this.tileEntity.stillValid(player);
    }

        public IntList getMoveSlots(@NotNull InventoryAction action, @NotNull Slot slot, int target, Player player) {
        if (slot.index >= 0 && slot.index < 1) {
            return this.getSlots(0, 1, false);
        } else if (slot.index >= 1 && slot.index < 28) {
            return this.getSlots(1, 27, false);
        } else {
            return slot.index >= 28 && slot.index < 37 ? this.getSlots(28, 9, false) : null;
        }
    }


        public IntList getTargetSlots(@NotNull InventoryAction action, @NotNull Slot slot, int target, Player player) {
        if (slot.index >= 1 && slot.index < 37) {
            if (action != InventoryAction.MOVE_ALL) {
                return this.getSlots(0, 1, false);
            } else {
                return slot.index < 28 ? this.getSlots(28, 9, false) : this.getSlots(1, 27, false);
            }
        } else {
            return slot.index >= 0 && slot.index < 1 ? this.getSlots(1, 36, false) : null;
        }
    }

}
