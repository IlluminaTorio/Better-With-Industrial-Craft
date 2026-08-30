

package ic2.item;

import ic2.item.ItemIC2;
import net.minecraft.core.item.ItemStack;

public class ItemFuelCan
extends ItemIC2 {
    public ItemFuelCan(String name, String namespaceId, int id) {
        super(name, namespaceId, id);
        this.setMaxDamage(73727);
        this.setMaxStackSize(1);
    }

    public int getFuelEnergy(ItemStack stack) {
        return stack.getMetadata() * 1000;
    }
}

