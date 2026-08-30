

package ic2.item;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicDoor;
import net.minecraft.core.item.ItemDoor;

public class ItemReinforcedDoor
extends ItemDoor {
    public ItemReinforcedDoor(String name, String namespaceId, int id, Block<? extends BlockLogicDoor> doorBottom, Block<? extends BlockLogicDoor> doorTop) {
        super(name, namespaceId, id, doorBottom, doorTop);
        this.setMaxStackSize(1);
    }
}

