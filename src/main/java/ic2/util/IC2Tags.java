

package ic2.util;

import ic2.IC2;
import ic2.IC2Blocks;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.block.Block;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.item.IItemConvertible;
import net.minecraft.core.item.ItemStack;

public final class IC2Tags {
    private IC2Tags() {
    }

    public static void init() {
        try {
            IC2Tags.addToGroup("minecraft:logs", IC2Blocks.rubberWood);
            IC2Tags.addToGroup("minecraft:leaves", IC2Blocks.rubberLeaves);
            ArrayList<ItemStack> copperOres = new ArrayList<ItemStack>();
            ArrayList<ItemStack> tinOres = new ArrayList<ItemStack>();
            ArrayList<ItemStack> uraniumOres = new ArrayList<ItemStack>();
            for (Block b : new Block[]{IC2Blocks.oreCopper, IC2Blocks.oreCopperBasalt, IC2Blocks.oreCopperLimestone, IC2Blocks.oreCopperGranite, IC2Blocks.oreCopperPermafrost}) {
                copperOres.add(b.getDefaultStack());
            }
            for (Block b : new Block[]{IC2Blocks.oreTin, IC2Blocks.oreTinBasalt, IC2Blocks.oreTinLimestone, IC2Blocks.oreTinGranite, IC2Blocks.oreTinPermafrost}) {
                tinOres.add(b.getDefaultStack());
            }
            for (Block b : new Block[]{IC2Blocks.oreUranium, IC2Blocks.oreUraniumBasalt, IC2Blocks.oreUraniumLimestone, IC2Blocks.oreUraniumGranite, IC2Blocks.oreUraniumPermafrost}) {
                uraniumOres.add(b.getDefaultStack());
            }
            IC2Tags.registerGroup("ic2:copper_ores", copperOres);
            IC2Tags.registerGroup("ic2:tin_ores", tinOres);
            IC2Tags.registerGroup("ic2:uranium_ores", uraniumOres);
        }
        catch (Throwable t) {
            IC2.LOGGER.warn("Failed to register IC2 item groups: {}", (Object)t.toString());
        }
    }

    private static void addToGroup(String key, IItemConvertible item) {
        ItemStack stack;
        if (item == null) {
            return;
        }
        List group = (List)Registries.ITEM_GROUPS.getItem(key);
        if (group == null) {
            return;
        }
        ItemStack itemStack = stack = item.asItem() != null ? new ItemStack(item.asItem()) : null;
        if (stack == null) {
            return;
        }
        for (Object existingObj : group) {
            ItemStack existing = (ItemStack)existingObj;
            if (existing.itemID != stack.itemID) continue;
            return;
        }
        group.add(stack);
    }

    private static void registerGroup(String key, List<ItemStack> stacks) {
        if (Registries.ITEM_GROUPS.getItem(key) == null) {
            Registries.ITEM_GROUPS.register(key, stacks);
        }
    }
}

