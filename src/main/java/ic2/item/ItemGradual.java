

package ic2.item;

import ic2.item.ItemIC2;

public class ItemGradual
extends ItemIC2 {
    public ItemGradual(String name, String namespaceId, int id) {
        super(name, namespaceId, id);
        this.setMaxStackSize(1);
        this.setMaxDamage(10000);
    }
}

