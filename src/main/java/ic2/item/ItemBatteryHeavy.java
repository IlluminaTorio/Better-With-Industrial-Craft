package ic2.item;

public class ItemBatteryHeavy
extends ItemBattery {
    public ItemBatteryHeavy(String name, String namespaceId, int id, int ratio, int transfer, boolean rechargeable, int tier, int damageUnits) {
        super(name, namespaceId, id, ratio, transfer, rechargeable, tier);
        this.setMaxDamage(damageUnits);
    }
}
