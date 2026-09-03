

package ic2.util;

import ic2.tileentity.TileEntityElecMachine;
import ic2.tileentity.TileEntityElectricBlock;
import ic2.tileentity.TileEntityTransformer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.block.ItemBlock;
import net.minecraft.core.net.command.TextFormatting;










public final class IC2EnergyTiers {
    private static final Map<Item, Integer> TIER_CACHE = new ConcurrentHashMap<Item, Integer>();

    private IC2EnergyTiers() {
    }

    public static int tierOf(int maxEuPerTick) {
        if (maxEuPerTick <= 0) {
            return 0;
        }
        int tier = 1;
        int limit = 32;
        while (maxEuPerTick > limit) {
            limit *= 4;
            ++tier;
        }
        return tier;
    }

    public static String roman(int tier) {
        switch (tier) {
            case 1: return "I";
            case 2: return "II";
            case 3: return "III";
            case 4: return "IV";
            case 5: return "V";
            case 6: return "VI";
            case 7: return "VII";
            case 8: return "VIII";
            case 9: return "IX";
            case 10: return "X";
            default: {
                if (tier <= 0) {
                    return "";
                }
                StringBuilder sb = new StringBuilder();
                int rest = tier;
                while (rest >= 10) {
                    sb.append('X');
                    rest -= 10;
                }
                if (rest == 9) {
                    sb.append("IX");
                    rest = 0;
                }
                if (rest >= 5) {
                    sb.append('V');
                    rest -= 5;
                }
                if (rest == 4) {
                    sb.append("IV");
                    rest = 0;
                }
                while (rest > 0) {
                    sb.append('I');
                    --rest;
                }
                return sb.toString();
            }
        }
    }

    public static TextFormatting tierColor(int tier) {
        switch (tier) {
            case 1: return TextFormatting.ORANGE;
            case 2: return TextFormatting.YELLOW;
            case 3: return TextFormatting.CYAN;
            case 4: return TextFormatting.LIME;
            case 5: return TextFormatting.LIGHT_GRAY;
            case 6: return TextFormatting.PURPLE;
            default: return TextFormatting.WHITE;
        }
    }

    public static int tierForItem(Item item) {
        if (item == null) {
            return 0;
        }
        Integer cached = TIER_CACHE.get(item);
        if (cached != null) {
            return cached;
        }
        int tier = computeTierForItem(item);
        TIER_CACHE.put(item, tier);
        return tier;
    }

    private static int computeTierForItem(Item item) {
        if (!(item instanceof ItemBlock)) {
            return 0;
        }
        Block<?> block = ((ItemBlock)item).getBlock();
        if (block == null || block.entitySupplier == null) {
            return 0;
        }
        TileEntity te;
        try {
            te = block.entitySupplier.get();
        }
        catch (Throwable t) {
            return 0;
        }
        if (te == null) {
            return 0;
        }
        if (te instanceof TileEntityElecMachine) {
            return IC2EnergyTiers.tierOf(((TileEntityElecMachine)te).maxInput);
        }
        if (te instanceof TileEntityTransformer) {
            return IC2EnergyTiers.tierOf(((TileEntityTransformer)te).highOutput);
        }
        if (te instanceof TileEntityElectricBlock) {
            return IC2EnergyTiers.tierOf(((TileEntityElectricBlock)te).output);
        }
        if (te instanceof ic2.energy.IEnergySource) {
            return IC2EnergyTiers.tierOf(((ic2.energy.IEnergySource)te).getMaxEnergyOutput());
        }
        return 0;
    }





    public static String tooltipLine(int tier, String translation) {
        if (tier <= 0) {
            return null;
        }
        return TextFormatting.WHITE + translation + ": " + IC2EnergyTiers.tierColor(tier) + IC2EnergyTiers.roman(tier) + TextFormatting.RESET;
    }

    public static int tierForStack(ItemStack stack) {
        if (stack == null) {
            return 0;
        }
        return IC2EnergyTiers.tierForItem(stack.getItem());
    }
}
