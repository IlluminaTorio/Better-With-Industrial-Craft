

package ic2.gui.menu;

import ic2.tileentity.TileEntityPersonalChest;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import net.minecraft.core.player.inventory.slot.Slot;
import org.jetbrains.annotations.NotNull;

public class MenuPersonalSafe
extends MenuAbstract {
    public TileEntityPersonalChest tileEntity;

    public MenuPersonalSafe(ContainerInventory inventory, TileEntityPersonalChest tileEntity) {
        int x;
        int y;
        this.tileEntity = tileEntity;
        int rows = tileEntity.getContainerSize() / 9;
        int yOffset = (rows - 3) * 18;
        for (y = 0; y < rows; ++y) {
            for (x = 0; x < 9; ++x) {
                this.addSlot(new Slot((Container)tileEntity, x + y * 9, 8 + x * 18, 18 + y * 18));
            }
        }
        for (y = 0; y < 3; ++y) {
            for (x = 0; x < 9; ++x) {
                this.addSlot(new Slot((Container)inventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18 + yOffset));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot((Container)inventory, i, 8 + i * 18, 142 + yOffset));
        }
    }

    public boolean stillValid(Player player) {
        return this.tileEntity.stillValid(player);
    }

    public IntList getMoveSlots(@NotNull InventoryAction action, @NotNull Slot slot, int target, Player player) {
        int invStart = this.tileEntity.getContainerSize();
        if (slot.index >= 0 && slot.index < invStart) {
            return this.getSlots(0, invStart, false);
        }
        if (action == InventoryAction.MOVE_ALL) {
            if (slot.index >= invStart && slot.index < invStart + 27) {
                return this.getSlots(invStart, 27, false);
            }
            if (slot.index >= invStart + 27 && slot.index < invStart + 36) {
                return this.getSlots(invStart + 27, 9, false);
            }
        }
        if (slot.index >= invStart && slot.index < invStart + 36) {
            return this.getSlots(invStart, 36, false);
        }
        return null;
    }

    public IntList getTargetSlots(@NotNull InventoryAction action, @NotNull Slot slot, int target, Player player) {
        int invStart = this.tileEntity.getContainerSize();
        if (slot.index >= invStart && slot.index < invStart + 36) {
            if (slot.index >= invStart && slot.index < invStart + 27) {
                return this.getSlots(invStart + 27, 9, false);
            }
            return this.getSlots(invStart, 36, false);
        }
        if (slot.index >= 0 && slot.index < invStart) {
            return this.getSlots(invStart, 36, false);
        }
        return null;
    }
}

