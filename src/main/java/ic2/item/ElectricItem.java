

package ic2.item;

import ic2.energy.IChargeableItem;
import ic2.item.ItemIC2;
import ic2.item.armor.ItemArmorChargeable;
import net.minecraft.core.item.ItemStack;

public abstract class ElectricItem
extends ItemIC2
implements IChargeableItem,
ItemArmorChargeable.ElectricItemProxy {
    public int tier;
    public int ratio;
    public int transfer;

    public ElectricItem(String name, String namespaceId, int id, int tier, int ratio, int transfer) {
        super(name, namespaceId, id);
        this.tier = tier;
        this.ratio = ratio;
        this.transfer = transfer;
    }

    @Override
    public int ratio() {
        return this.ratio;
    }

    @Override
    public int giveEnergyTo(ItemStack tool, int offer, int tier) {
        if (tier < this.tier || tool.getMetadata() <= 0) {
            return 0;
        }
        int need = tool.getMetadata() * this.ratio;
        if (this.transfer != 0 && offer > this.transfer) {
            offer = this.transfer;
        }
        if (need < offer) {
            offer = need;
        }
        while (offer % this.ratio != 0) {
            --offer;
        }
        if (offer <= 0) {
            return 0;
        }
        tool.setMetadata(tool.getMetadata() - offer / this.ratio);
        return offer;
    }

    public boolean use(ItemStack tool, int amount) {
        if (tool.getMetadata() + amount > tool.getMaxDamage() + 1) {
            tool.setMetadata(tool.getMaxDamage() + 1);
            return false;
        }
        tool.setMetadata(tool.getMetadata() + amount);
        return true;
    }
}

