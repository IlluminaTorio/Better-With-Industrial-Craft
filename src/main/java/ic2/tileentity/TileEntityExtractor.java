

package ic2.tileentity;

import ic2.IC2Blocks;
import ic2.IC2Items;
import ic2.tileentity.TileEntityElectricMachine;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.item.ItemStack;

public class TileEntityExtractor
extends TileEntityElectricMachine {
    public static final Map<Integer, ItemStack> RECIPES = new HashMap<Integer, ItemStack>();

    public TileEntityExtractor() {
        super(3, 2, 300, 32);
    }

    @Override
    public ItemStack getResultFor(ItemStack itemstack) {
        return RECIPES.get(itemstack.getItem().id);
    }

    @Override
    public String getMachineName() {
        return "Extractor";
    }

    @Override
    public String getLoopSound() {
        return "random.fizz";
    }

    @Override
    public int getLoopingTime() {
        return 32;
    }

    @Override
    public String getGuiTexture() {
        return "GUIExtractor.png";
    }

    @Override
    public String getGuiTitleKey() {
        return "tile.ic2.machine.extractor.name";
    }

    public static void initRecipes() {
        RECIPES.put(IC2Blocks.rubberWood.id(), new ItemStack(IC2Items.rubber, 1));
        RECIPES.put(IC2Blocks.rubberSapling.id(), new ItemStack(IC2Items.rubber, 1));
        RECIPES.put(IC2Items.stickyResin.id, new ItemStack(IC2Items.rubber, 3));
        RECIPES.put(IC2Items.cellHydratedCoal.id, new ItemStack(IC2Items.cellCoalfuel));
        RECIPES.put(IC2Items.cellBiomass.id, new ItemStack(IC2Items.cellBiofuel));
        RECIPES.put(IC2Items.cellWater.id, new ItemStack(IC2Items.cellCoolant));
    }
}

