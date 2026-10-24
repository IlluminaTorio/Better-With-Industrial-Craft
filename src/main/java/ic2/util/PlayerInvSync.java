package ic2.util;

import net.minecraft.core.crafting.ContainerListener;
import net.minecraft.core.entity.player.Player;

public final class PlayerInvSync {
    private PlayerInvSync() {
    }

    public static void sync(Player player) {
        if (player == null || player.world == null || player.world.isClientSide) {
            return;
        }
        if (player.inventoryMenu == null) {
            return;
        }
        if (player instanceof ContainerListener) {
            ((ContainerListener)player).updateCraftingInventory(player.inventoryMenu, player.inventoryMenu.getSlotStackList());
        }
    }
}
