

package ic2.item;

import ic2.energy.IChargeableItem;
import ic2.item.ItemIC2;
import net.minecraft.core.item.ItemStack;

public class ItemBattery
extends ItemIC2
implements IChargeableItem {
    public int ratio;
    public int transfer;
    public boolean rechargeable;
    public int tier;

    public ItemBattery(String name, String namespaceId, int id, int ratio, int transfer, boolean rechargeable, int tier) {
        super(name, namespaceId, id);
        this.ratio = ratio;
        this.transfer = transfer;
        this.rechargeable = rechargeable;
        this.tier = tier;
        this.setMaxDamage(9999);
        this.setMaxStackSize(1);
    }

    public int getEnergyFrom(ItemStack battery, int request, int tier) {
        if (tier < this.tier || battery.getMetadata() > battery.getMaxDamage()) {
            return 0;
        }
        int energy = (battery.getMaxDamage() + 1 - battery.getMetadata()) * this.ratio;
        if (request > energy) {
            request = energy;
        }
        if (this.transfer != 0 && this.transfer < energy) {
            energy = this.transfer;
        }
        while (energy % this.ratio != 0) {
            --energy;
        }
        if (energy <= 0) {
            return 0;
        }
        battery.setMetadata(battery.getMetadata() + energy / this.ratio);
        return energy;
    }

    @Override
    public int giveEnergyTo(ItemStack battery, int offer, int tier) {
        if (!this.rechargeable || tier < this.tier || battery.getMetadata() <= 0) {
            return 0;
        }
        int need = battery.getMetadata() * this.ratio;
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
        battery.setMetadata(battery.getMetadata() - offer / this.ratio);
        return offer;
    }

    public boolean showFullDurability() {
        return false;
    }
}

