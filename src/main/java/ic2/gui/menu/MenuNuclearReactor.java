

package ic2.gui.menu;

import ic2.tileentity.TileEntityNuclearReactor;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import net.minecraft.core.player.inventory.slot.Slot;
import org.jetbrains.annotations.NotNull;

public class MenuNuclearReactor
extends MenuAbstract {
    public TileEntityNuclearReactor tileEntity;

    public MenuNuclearReactor(ContainerInventory inventory, TileEntityNuclearReactor tileEntity) {
        int x;
        int y;
        this.tileEntity = tileEntity;
        int size = tileEntity.getReactorSize();
        int startX = 89 - 9 * size;
        for (y = 0; y < 6; ++y) {
            for (x = 0; x < size; ++x) {
                this.addSlot(new Slot((Container)tileEntity, x + y * 9, startX + x * 18, 18 + y * 18));
            }
        }
        for (y = 0; y < 3; ++y) {
            for (x = 0; x < 9; ++x) {
                this.addSlot(new Slot((Container)inventory, x + y * 9 + 9, 8 + x * 18, 140 + y * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot((Container)inventory, i, 8 + i * 18, 198));
        }
    }

    public boolean stillValid(Player player) {
        return this.tileEntity.stillValid(player);
    }

    public IntList getMoveSlots(@NotNull InventoryAction action, @NotNull Slot slot, int target, Player player) {
        int invStart = this.tileEntity.getReactorSize() * 6;
        if (slot.index >= 0 && slot.index < invStart) {
            return this.getSlots(0, invStart, false);
        }
        if (slot.index >= invStart && slot.index < invStart + 36) {
            return this.getSlots(invStart, 36, false);
        }
        return null;
    }

    public IntList getTargetSlots(@NotNull InventoryAction action, @NotNull Slot slot, int target, Player player) {
        int invStart = this.tileEntity.getReactorSize() * 6;
        if (slot.index >= invStart && slot.index < invStart + 36) {
            return this.getSlots(0, invStart, false);
        }
        if (slot.index >= 0 && slot.index < invStart) {
            return this.getSlots(invStart, 36, false);
        }
        return null;
    }
}

