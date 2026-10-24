package ic2.gui.menu;

import ic2.tileentity.TileEntityDroneDepot;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.crafting.ContainerListener;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.slot.Slot;
import org.jetbrains.annotations.NotNull;

public class MenuDroneDepot
extends MenuAbstract {
    public final TileEntityDroneDepot tileEntity;
    public int channel = 0;
    public int peerStatus = 0;

    public MenuDroneDepot(ContainerInventory inventory, TileEntityDroneDepot tileEntity) {
        this.tileEntity = tileEntity;
        int slot = 0;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 3; ++col) {
                this.addSlot(new Slot((Container)tileEntity, slot++, 8 + col * 18, 17 + row * 18){
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return !TileEntityDroneDepot.isEnergyCarrier(stack);
                    }
                });
            }
        }
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
        int peer = this.tileEntity.hasPeer() ? 1 : 0;
        for (ContainerListener listener : this.containerListeners) {
            if (this.channel != this.tileEntity.channel) {
                listener.updateCraftingInventoryInfo((MenuAbstract)this, 0, this.tileEntity.channel);
            }
            if (this.peerStatus != peer) {
                listener.updateCraftingInventoryInfo((MenuAbstract)this, 1, peer);
            }
        }
        this.channel = this.tileEntity.channel;
        this.peerStatus = peer;
    }

    @Override
    public void setData(int id, int value) {
        if (id == 0) {
            this.tileEntity.channel = value;
        } else if (id == 1) {
            this.peerStatus = value;
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.tileEntity.stillValid(player);
    }

    @Override
    public IntList getMoveSlots(@NotNull InventoryAction action, @NotNull Slot slot, int target, Player player) {
        if (slot.index >= 0 && slot.index < 9) {
            return this.getSlots(0, 9, false);
        }
        if (action == InventoryAction.MOVE_ALL) {
            if (slot.index >= 9 && slot.index < 36) {
                return this.getSlots(9, 27, false);
            }
            if (slot.index >= 36 && slot.index < 45) {
                return this.getSlots(36, 9, false);
            }
        }
        return action == InventoryAction.MOVE_SIMILAR && slot.index >= 9 && slot.index < 45 ? this.getSlots(9, 36, false) : null;
    }

    @Override
    public IntList getTargetSlots(@NotNull InventoryAction action, @NotNull Slot slot, int target, Player player) {
        if (slot.index >= 9 && slot.index < 45) {
            if (slot.index >= 9 && slot.index < 36) {
                return this.getSlots(36, 9, false);
            }
            return this.getSlots(9, 27, false);
        }
        if (slot.index >= 0 && slot.index < 9) {
            return this.getSlots(9, 36, false);
        }
        return null;
    }
}
