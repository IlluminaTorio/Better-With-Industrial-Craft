

package ic2.item.armor;

import ic2.item.armor.IC2Armor;
import ic2.item.armor.ItemArmorChargeable;
import net.minecraft.core.enums.HumanArmorShape;

public class ItemArmorQuantum
extends ItemArmorChargeable {
    public ItemArmorQuantum(String name, String namespaceId, int id, HumanArmorShape shape) {
        super(name, namespaceId, id, IC2Armor.QUANTUM, shape, 100, 0, 2, 1000000);
    }
}

