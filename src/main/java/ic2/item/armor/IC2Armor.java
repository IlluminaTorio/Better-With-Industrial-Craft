

package ic2.item.armor;

import net.minecraft.core.enums.HumanArmorShape;
import net.minecraft.core.enums.IArmorShape;
import net.minecraft.core.item.ItemArmor;
import net.minecraft.core.item.material.ArmorMaterial;
import turniplabs.halplibe.helper.ArmorHelper;

public class IC2Armor {
    public static final ArmorMaterial BRONZE = ArmorHelper.createArmorMaterial((String)"ic2", (String)"bronze", (int)480, (float)45.0f, (float)45.0f, (float)45.0f, (float)45.0f);
    public static final ArmorMaterial COMPOSITE = ArmorHelper.createArmorMaterial((String)"ic2", (String)"composite", (int)1200, (float)55.0f, (float)150.0f, (float)55.0f, (float)55.0f);
    public static final ArmorMaterial RUBBER = ArmorHelper.createArmorMaterial((String)"ic2", (String)"rubber", (int)100, (float)5.0f, (float)5.0f, (float)5.0f, (float)60.0f);
    public static final ArmorMaterial NANO = ArmorHelper.createArmorMaterial((String)"ic2", (String)"nano", (int)2000, (float)70.0f, (float)70.0f, (float)70.0f, (float)70.0f);
    public static final ArmorMaterial QUANTUM = ArmorHelper.createArmorMaterial((String)"ic2", (String)"quantum", (int)4000, (float)85.0f, (float)85.0f, (float)100.0f, (float)100.0f);
    public static final ArmorMaterial JETPACK = ArmorHelper.createArmorMaterial((String)"ic2", (String)"jetpack", (int)200, (float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f);
    public static final ArmorMaterial BATPACK = ArmorHelper.createArmorMaterial((String)"ic2", (String)"batpack", (int)200, (float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f);

    public static class RubberBoots
    extends ItemArmor<HumanArmorShape> {
        public RubberBoots(String name, String namespaceId, int id) {
            super(name, namespaceId, id, RUBBER, HumanArmorShape.BOOTS);
        }
    }

    public static class Composite
    extends ItemArmor<HumanArmorShape> {
        public Composite(String name, String namespaceId, int id) {
            super(name, namespaceId, id, COMPOSITE, HumanArmorShape.CHEST);
        }
    }

    public static class Bronze
    extends ItemArmor<HumanArmorShape> {
        public Bronze(String name, String namespaceId, int id, HumanArmorShape shape) {
            super(name, namespaceId, id, BRONZE, shape);
        }
    }
}

