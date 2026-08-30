

package ic2.item.armor;

import ic2.energy.IChargeableItem;
import net.minecraft.core.enums.HumanArmorShape;
import net.minecraft.core.enums.IArmorShape;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemArmor;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.material.ArmorMaterial;

public class ItemArmorChargeable
extends ItemArmor<HumanArmorShape>
implements IChargeableItem {
    public int ratio;
    public int transfer;
    public int tier;

    public ItemArmorChargeable(String name, String namespaceId, int id, ArmorMaterial material, HumanArmorShape shape, int ratio, int transfer, int tier, int maxCharge) {
        super(name, namespaceId, id, material, shape);
        this.ratio = ratio;
        this.transfer = transfer;
        this.tier = tier;
        this.setMaxDamage(Math.max(1, maxCharge / ratio - 1));
        this.setMaxStackSize(1);
    }

    @Override
    public int giveEnergyTo(ItemStack armor, int offer, int tier) {
        if (tier < this.tier || armor.getMetadata() <= 0) {
            return 0;
        }
        int need = armor.getMetadata() * this.ratio;
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
        armor.setMetadata(armor.getMetadata() - offer / this.ratio);
        return offer;
    }

    public boolean use(ItemStack armor, int amount) {
        int damage = (amount + this.ratio - 1) / this.ratio;
        if (armor.getMetadata() + damage > armor.getMaxDamage() + 1) {
            return false;
        }
        armor.setMetadata(armor.getMetadata() + damage);
        return true;
    }

    public int getEnergy(ItemStack armor) {
        return (armor.getMaxDamage() + 1 - armor.getMetadata()) * this.ratio;
    }

    public boolean canChargeTools() {
        return "ic2:batpack".equals(this.namespaceID.toString());
    }

    public void chargeTool(ItemStack armor, ItemStack tool) {
        int giveTool;
        if (!this.canChargeTools() || tool == null) {
            return;
        }
        Item item = tool.getItem();
        if (!(item instanceof IChargeableItem)) {
            return;
        }
        IChargeableItem toolItem = (IChargeableItem)item;
        Item item2 = tool.getItem();
        if (!(item2 instanceof ElectricItemProxy)) {
            return;
        }
        ElectricItemProxy proxy = (ElectricItemProxy)item2;
        int toolNeed = tool.getMetadata() * proxy.ratio();
        if (toolNeed <= 0) {
            return;
        }
        int armorFree = armor.getMetadata() * this.ratio;
        if (armorFree <= 0) {
            return;
        }
        int give = Math.min(toolNeed, armorFree);
        if ((giveTool = (give -= give % this.ratio) - give % proxy.ratio()) <= 0) {
            return;
        }
        tool.setMetadata(tool.getMetadata() - giveTool / proxy.ratio());
        armor.setMetadata(armor.getMetadata() - giveTool / this.ratio);
    }

    public static interface ElectricItemProxy
    extends IChargeableItem {
        public int ratio();
    }
}

