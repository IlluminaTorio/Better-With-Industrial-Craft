

package ic2.gui.menu;

import ic2.tileentity.TileEntityTradeOMat;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import net.minecraft.core.player.inventory.slot.Slot;
import net.minecraft.core.player.inventory.slot.SlotFurnace;
import org.jetbrains.annotations.NotNull;

public class MenuTradeOMat
extends MenuAbstract {
    public TileEntityTradeOMat tileEntity;
    public boolean owner;

    public MenuTradeOMat(ContainerInventory inventory, TileEntityTradeOMat tileEntity, boolean owner) {
        this.tileEntity = tileEntity;
        this.owner = owner;
        if (owner) {
            this.addSlot(new Slot((Container)tileEntity, 0, 24, 17));
            this.addSlot(new Slot((Container)tileEntity, 1, 24, 53));
            this.addSlot(new Slot((Container)tileEntity, 2, 80, 17));
            this.addSlot((Slot)new SlotFurnace(inventory.player, (Container)tileEntity, 3, 80, 53));
        } else {
            this.addSlot(new Slot((Container)tileEntity, 2, 143, 17));
            this.addSlot((Slot)new SlotFurnace(inventory.player, (Container)tileEntity, 3, 143, 53));
        }
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                this.addSlot(new Slot((Container)inventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot((Container)inventory, i, 8 + i * 18, 142));
        }
    }

    public boolean stillValid(Player player) {
        return this.tileEntity.stillValid(player);
    }

    public IntList getMoveSlots(@NotNull InventoryAction action, @NotNull Slot slot, int target, Player player) {
        int machineSlots;
        int n = machineSlots = this.owner ? 4 : 2;
        if (slot.index >= 0 && slot.index < machineSlots) {
            return this.getSlots(0, machineSlots, false);
        }
        if (slot.index >= machineSlots && slot.index < machineSlots + 36) {
            return this.getSlots(machineSlots, 36, false);
        }
        return null;
    }

    public IntList getTargetSlots(@NotNull InventoryAction action, @NotNull Slot slot, int target, Player player) {
        int machineSlots;
        int n = machineSlots = this.owner ? 4 : 2;
        if (slot.index >= machineSlots && slot.index < machineSlots + 36) {
            return this.getSlots(0, machineSlots, false);
        }
        if (slot.index >= 0 && slot.index < machineSlots) {
            return this.getSlots(machineSlots, 36, true);
        }
        return null;
    }
}

