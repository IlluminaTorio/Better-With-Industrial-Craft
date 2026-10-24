package ic2.tileentity;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;

public class TileEntityRotaryMacerator
extends TileEntityAdvMachine {
    public static final Map<Integer, ItemStack> RECIPES = new HashMap<Integer, ItemStack>();

    public TileEntityRotaryMacerator() {
        super(1, 25, 30, 4096);
    }

    @Override
    public ItemStack getResultFor(ItemStack itemstack) {
        ItemStack own = RECIPES.get(itemstack.getItem().id);
        if (own != null) {
            return own;
        }
        return TileEntityMacerator.RECIPES.get(itemstack.getItem().id);
    }

    @Override
    public String getMachineName() {
        return "Rotary Macerator";
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
        return "GUIRotaryMacerator.png";
    }

    @Override
    public String getGuiTitleKey() {
        return "gui.ic2.rotary_macerator.label.title";
    }

    @Override
    public String getSpeedUnitKey() {
        return "gui.ic2.rotary_macerator.label.rpm";
    }

    public static void initRecipes() {
        RECIPES.put(Blocks.BLOCK_SNOW.id(), new ItemStack(Items.AMMO_SNOWBALL, 4));
        RECIPES.put(Blocks.ICE.id(), new ItemStack(Items.AMMO_SNOWBALL, 3));
        RECIPES.put(Blocks.GLASS.id(), new ItemStack(Blocks.SAND));
    }
}
