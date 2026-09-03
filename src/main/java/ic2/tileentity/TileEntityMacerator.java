

package ic2.tileentity;

import ic2.IC2Blocks;
import ic2.IC2Items;
import ic2.tileentity.TileEntityElectricMachine;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;

public class TileEntityMacerator
extends TileEntityElectricMachine {
    public static final Map<Integer, ItemStack> RECIPES = new HashMap<Integer, ItemStack>();

    public TileEntityMacerator() {
        super(3, 2, 300, 32);
    }

    @Override
    public ItemStack getResultFor(ItemStack itemstack) {
        return RECIPES.get(itemstack.getItem().id);
    }

    @Override
    public String getMachineName() {
        return "Macerator";
    }

    @Override
    public String getLoopSound() {
        return "random.fizz";
    }

    @Override
    public int getLoopingTime() {
        return 60;
    }

    @Override
    public String getGuiTexture() {
        return "GUIMacerator.png";
    }

    @Override
    public String getGuiTitleKey() {
        return "tile.ic2.machine.macerator.name";
    }

    public static void initRecipes() {
        RECIPES.put(Blocks.ORE_IRON_STONE.id(), new ItemStack(IC2Items.dustIron, 2));
        RECIPES.put(Blocks.ORE_IRON_BASALT.id(), new ItemStack(IC2Items.dustIron, 2));
        RECIPES.put(Blocks.ORE_IRON_LIMESTONE.id(), new ItemStack(IC2Items.dustIron, 2));
        RECIPES.put(Blocks.ORE_IRON_GRANITE.id(), new ItemStack(IC2Items.dustIron, 2));
        RECIPES.put(Blocks.ORE_IRON_PERMAFROST.id(), new ItemStack(IC2Items.dustIron, 2));
        RECIPES.put(Blocks.ORE_GOLD_STONE.id(), new ItemStack(IC2Items.dustGold, 2));
        RECIPES.put(Blocks.ORE_GOLD_BASALT.id(), new ItemStack(IC2Items.dustGold, 2));
        RECIPES.put(Blocks.ORE_GOLD_LIMESTONE.id(), new ItemStack(IC2Items.dustGold, 2));
        RECIPES.put(Blocks.ORE_GOLD_GRANITE.id(), new ItemStack(IC2Items.dustGold, 2));
        RECIPES.put(Blocks.ORE_GOLD_PERMAFROST.id(), new ItemStack(IC2Items.dustGold, 2));
        RECIPES.put(Items.ORE_RAW_IRON.id, new ItemStack(IC2Items.dustIron, 2));
        RECIPES.put(Items.ORE_RAW_GOLD.id, new ItemStack(IC2Items.dustGold, 2));
        RECIPES.put(IC2Items.rawCopper.id, new ItemStack(IC2Items.dustCopper, 2));
        RECIPES.put(IC2Items.rawTin.id, new ItemStack(IC2Items.dustTin, 2));
        for (Block b : new Block[]{IC2Blocks.oreCopper, IC2Blocks.oreCopperBasalt, IC2Blocks.oreCopperLimestone, IC2Blocks.oreCopperGranite, IC2Blocks.oreCopperPermafrost}) {
            RECIPES.put(b.id(), new ItemStack(IC2Items.dustCopper, 2));
        }
        for (Block b : new Block[]{IC2Blocks.oreTin, IC2Blocks.oreTinBasalt, IC2Blocks.oreTinLimestone, IC2Blocks.oreTinGranite, IC2Blocks.oreTinPermafrost}) {
            RECIPES.put(b.id(), new ItemStack(IC2Items.dustTin, 2));
        }
        for (Block b : new Block[]{IC2Blocks.oreUranium, IC2Blocks.oreUraniumBasalt, IC2Blocks.oreUraniumLimestone, IC2Blocks.oreUraniumGranite, IC2Blocks.oreUraniumPermafrost}) {
            RECIPES.put(b.id(), new ItemStack(IC2Items.uraniumItem, 2));
        }
        RECIPES.put(Items.COAL.id, new ItemStack(IC2Items.dustCoal));
        RECIPES.put(IC2Items.ingotRefinedIron.id, new ItemStack(IC2Items.dustIron));
        RECIPES.put(IC2Items.ingotTin.id, new ItemStack(IC2Items.dustTin));
        RECIPES.put(IC2Items.ingotCopper.id, new ItemStack(IC2Items.dustCopper));
        RECIPES.put(Items.INGOT_IRON.id, new ItemStack(IC2Items.dustIron));
        RECIPES.put(Items.INGOT_GOLD.id, new ItemStack(IC2Items.dustGold));
        RECIPES.put(Items.INGOT_STEEL.id, new ItemStack(IC2Items.dustSteel));
        RECIPES.put(IC2Items.ingotBronze.id, new ItemStack(IC2Items.dustBronze));
        RECIPES.put(IC2Items.uraniumItem.id, new ItemStack(IC2Items.dustUranium));
        RECIPES.put(IC2Items.ingotUran.id, new ItemStack(IC2Items.dustUranium));
        RECIPES.put(Blocks.COBWEB.id(), new ItemStack(Items.STRING));
        RECIPES.put(Blocks.STONE.id(), new ItemStack(Blocks.COBBLE_STONE));
        RECIPES.put(Blocks.BASALT.id(), new ItemStack(Blocks.COBBLE_BASALT));
        RECIPES.put(Blocks.LIMESTONE.id(), new ItemStack(Blocks.COBBLE_LIMESTONE));
        RECIPES.put(Blocks.GRANITE.id(), new ItemStack(Blocks.COBBLE_GRANITE));
        RECIPES.put(Blocks.PERMAFROST.id(), new ItemStack(Blocks.COBBLE_PERMAFROST));
        RECIPES.put(Blocks.GRAVEL.id(), new ItemStack(Items.FLINT));
        RECIPES.put(Blocks.SANDSTONE.id(), new ItemStack(Blocks.SAND));
        RECIPES.put(Blocks.COBBLE_STONE.id(), new ItemStack(Blocks.SAND));
        RECIPES.put(Blocks.COBBLE_BASALT.id(), new ItemStack(Blocks.SAND));
        RECIPES.put(Blocks.COBBLE_LIMESTONE.id(), new ItemStack(Blocks.SAND));
        RECIPES.put(Blocks.COBBLE_GRANITE.id(), new ItemStack(Blocks.SAND));
    }
}

