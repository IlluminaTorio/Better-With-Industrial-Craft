package ic2.tileentity;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;

public class TileEntitySingularityCompressor
extends TileEntityAdvMachine {
    public static final Map<Integer, ItemStack> RECIPES = new HashMap<Integer, ItemStack>();

    public TileEntitySingularityCompressor() {
        super(1, 15, 50, 4096);
    }

    @Override
    public ItemStack getResultFor(ItemStack itemstack) {
        ItemStack own = RECIPES.get(itemstack.getItem().id);
        if (own != null) {
            return own;
        }
        return TileEntityCompressor.RECIPES.get(itemstack.getItem().id);
    }

    @Override
    public String getMachineName() {
        return "Singularity Compressor";
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
        return "GUISingularityCompressor.png";
    }

    @Override
    public String getGuiTitleKey() {
        return "gui.ic2.singularity_compressor.label.title";
    }

    @Override
    public String getSpeedUnitKey() {
        return "gui.ic2.singularity_compressor.label.psi";
    }

    public static void initRecipes() {
        RECIPES.put(Items.AMMO_SNOWBALL.id, new ItemStack(Blocks.BLOCK_SNOW));
        RECIPES.put(Blocks.ICE.id(), new ItemStack(Blocks.PERMAICE));
    }
}
