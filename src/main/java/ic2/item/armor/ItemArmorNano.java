

package ic2.item.armor;

import ic2.energy.IChargeableItem;
import ic2.item.armor.IC2Armor;
import ic2.item.armor.ItemArmorChargeable;
import net.minecraft.core.enums.HumanArmorShape;

public class ItemArmorNano
extends ItemArmorChargeable
implements IChargeableItem {
    public ItemArmorNano(String name, String namespaceId, int id, HumanArmorShape shape) {
        super(name, namespaceId, id, IC2Armor.NANO, shape, 100, 0, 1, 100000);
    }
}

