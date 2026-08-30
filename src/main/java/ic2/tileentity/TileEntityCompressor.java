

package ic2.tileentity;

import ic2.IC2Items;
import ic2.tileentity.TileEntityElectricMachine;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;

public class TileEntityCompressor
extends TileEntityElectricMachine {
    public static final Map<Integer, ItemStack> RECIPES = new HashMap<Integer, ItemStack>();

    public TileEntityCompressor() {
        super(3, 2, 300, 32);
    }

    @Override
    public ItemStack getResultFor(ItemStack itemstack) {
        return RECIPES.get(itemstack.getItem().id);
    }

    @Override
    public String getMachineName() {
        return "Compressor";
    }

    @Override
    public String getLoopSound() {
        return "random.fizz";
    }

    @Override
    public int getLoopingTime() {
        return 64;
    }

    @Override
    public String getGuiTexture() {
        return "GUICompressor.png";
    }

    @Override
    public String getGuiTitleKey() {
        return "tile.ic2.machine.compressor.name";
    }

    public static void initRecipes() {
        RECIPES.put(IC2Items.plantball.id, new ItemStack(IC2Items.compressedPlants));
        RECIPES.put(IC2Items.hydratedCoalDust.id, new ItemStack(IC2Items.hydratedCoal));
        RECIPES.put(Blocks.SAND.id(), new ItemStack(Blocks.SANDSTONE));
        RECIPES.put(IC2Items.cellWater.id, new ItemStack(Items.AMMO_SNOWBALL));
        RECIPES.put(IC2Items.uraniumItem.id, new ItemStack(IC2Items.ingotUran));
        RECIPES.put(IC2Items.ingotMixedMetal.id, new ItemStack(IC2Items.advancedAlloy));
        RECIPES.put(IC2Items.rawCarbonMesh.id, new ItemStack(IC2Items.carbonPlate));
        RECIPES.put(IC2Items.coalBall.id, new ItemStack(IC2Items.compressedCoalBall));
        RECIPES.put(IC2Items.coalChunk.id, new ItemStack(IC2Items.industrialDiamond));
    }
}

