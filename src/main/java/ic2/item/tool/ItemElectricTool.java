

package ic2.item.tool;

import ic2.item.ElectricItem;
import ic2.item.armor.ItemArmorChargeable;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.data.tag.Tag;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.tag.ItemTags;
import net.minecraft.core.item.tool.ItemToolPickaxe;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;

public abstract class ItemElectricTool
extends ElectricItem {
    public static final ToolMaterialProxy ELECTRIC = new ToolMaterialProxy();
    protected final int miningLevel;

    public ItemElectricTool(String name, String namespaceId, int id, int miningLevel) {
        this(name, namespaceId, id, miningLevel, 1, 50, 100, 202);
    }

    public ItemElectricTool(String name, String namespaceId, int id, int miningLevel, int tier, int ratio, int transfer, int maxDamage) {
        super(name, namespaceId, id, tier, ratio, transfer);
        this.miningLevel = miningLevel;
        this.setMaxDamage(maxDamage);
        this.setMaxStackSize(1);
        this.withTags(new Tag[]{ItemTags.PREVENT_LEFT_CLICK_INTERACTIONS});
    }

    protected boolean isEffective(Block<?> block) {
        return block.hasTag(BlockTags.MINEABLE_BY_PICKAXE) || block.hasTag(BlockTags.MINEABLE_BY_AXE);
    }

    public float getStrVsBlock(@NotNull ItemStack selfStack, @NotNull Block<?> block) {
        if (this.isEffective(block)) {
            return this.getEfficiency();
        }
        return 1.0f;
    }

    protected float getEfficiency() {
        return 12.0f;
    }

    public boolean canHarvestBlock(@NotNull ItemStack selfStack, @NotNull Mob mob, @NotNull Block<?> block) {
        int required = ItemToolPickaxe.miningLevels.getOrDefault(block, -1);
        if (required != -1) {
            return this.miningLevel >= required;
        }
        return this.isEffective(block);
    }

    public boolean onBlockDestroyed(@NotNull ItemStack selfStack, @NotNull World world, @NotNull Mob mob, @NotNull Block<?> removedBlock, @NotNull TilePosc blockPos, @NotNull Side side) {
        this.use(selfStack, 1, mob);
        return true;
    }

    public boolean hitEntity(@NotNull ItemStack selfStack, @NotNull Mob target, @NotNull Mob attacker) {
        return true;
    }

    public boolean use(ItemStack tool, int damage, Mob player) {
        this.chargeFromBatpack(tool, player);
        if (tool.getMetadata() + damage > tool.getMaxDamage() + 1) {
            tool.setMetadata(tool.getMaxDamage() + 1);
            return false;
        }
        tool.setMetadata(tool.getMetadata() + damage);
        this.chargeFromBatpack(tool, player);
        return true;
    }

    public void chargeFromBatpack(ItemStack tool, Mob player) {
        if (player == null) {
            return;
        }
        if (player instanceof Player) {
            ItemArmorChargeable armor;
            Item item;
            ItemStack chest;
            Player p = (Player)player;
            if (p.inventory != null && (chest = p.inventory.armorInventory[2]) != null && (item = chest.getItem()) instanceof ItemArmorChargeable && (armor = (ItemArmorChargeable)item).canChargeTools()) {
                armor.chargeTool(chest, tool);
            }
        }
    }

    public static final class ToolMaterialProxy {
        public float getEfficiency(boolean proper) {
            return 12.0f;
        }
    }
}

