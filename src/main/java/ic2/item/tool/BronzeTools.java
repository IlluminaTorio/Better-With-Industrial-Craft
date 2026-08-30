

package ic2.item.tool;

import net.minecraft.core.item.material.ToolMaterial;
import net.minecraft.core.item.tool.ItemToolAxe;
import net.minecraft.core.item.tool.ItemToolHoe;
import net.minecraft.core.item.tool.ItemToolPickaxe;
import net.minecraft.core.item.tool.ItemToolShovel;
import net.minecraft.core.item.tool.ItemToolSword;

public class BronzeTools {
    public static final ToolMaterial BRONZE = new ToolMaterial().setDurability(350).setEfficiency(6.0f, 8.0f).setMiningLevel(2);

    public static class Sword
    extends ItemToolSword {
        public Sword(String name, String namespaceId, int id) {
            super(name, namespaceId, id, BRONZE);
        }
    }

    public static class Hoe
    extends ItemToolHoe {
        public Hoe(String name, String namespaceId, int id) {
            super(name, namespaceId, id, BRONZE);
        }
    }

    public static class Shovel
    extends ItemToolShovel {
        public Shovel(String name, String namespaceId, int id) {
            super(name, namespaceId, id, BRONZE);
        }
    }

    public static class Axe
    extends ItemToolAxe {
        public Axe(String name, String namespaceId, int id) {
            super(name, namespaceId, id, BRONZE);
        }
    }

    public static class Pickaxe
    extends ItemToolPickaxe {
        public Pickaxe(String name, String namespaceId, int id) {
            super(name, namespaceId, id, BRONZE);
        }
    }
}

