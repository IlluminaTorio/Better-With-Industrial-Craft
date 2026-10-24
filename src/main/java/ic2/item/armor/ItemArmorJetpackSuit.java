

package ic2.item.armor;

import ic2.IC2;
import net.minecraft.core.item.ItemStack;








public class ItemArmorJetpackSuit
extends ItemArmorChargeable {
    public final boolean electric;
    public final boolean quantum;

    public ItemArmorJetpackSuit(String name, String namespaceId, int id, boolean quantum, boolean electric, int maxCharge) {
        super(name, namespaceId, id, quantum ? IC2Armor.QUANTUM : IC2Armor.NANO, net.minecraft.core.enums.HumanArmorShape.CHEST, 1, 0, quantum ? 2 : 1, maxCharge);
        this.electric = electric;
        this.quantum = quantum;
    }

    @Override
    public String getTranslatedDescription(ItemStack stack) {
        String base = super.getTranslatedDescription(stack);
        int eu = this.getEnergy(stack);
        int max = (stack.getMaxDamage() + 1) * this.ratio;
        String euLine = net.minecraft.core.net.command.TextFormatting.LIME + "EU: " + eu + " / " + max;
        if (base == null || base.isEmpty()) {
            return euLine;
        }
        return euLine + "\n" + base;
    }
}
