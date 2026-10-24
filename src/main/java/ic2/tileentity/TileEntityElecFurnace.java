

package ic2.tileentity;

import ic2.tileentity.TileEntityElectricMachine;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryFurnace;
import net.minecraft.core.item.ItemStack;

public class TileEntityElecFurnace
extends TileEntityElectricMachine {
    public TileEntityElecFurnace() {
        super(3, 3, 100, 32);
    }

    @Override
    public ItemStack getResultFor(ItemStack stack) {
        if (stack == null) {
            return null;
        }
        for (RecipeEntryFurnace recipe : Registries.RECIPES.getAllFurnaceRecipes()) {
            if (recipe == null || !recipe.matches(stack)) continue;
            return ((ItemStack)recipe.getOutput()).copy();
        }
        return null;
    }

    @Override
    public String getMachineName() {
        return "Electric Furnace";
    }

    @Override
    public String getLoopSound() {
        return "random.fizz";
    }

    @Override
    public int getLoopingTime() {
        return 26;
    }

    @Override
    public String getGuiTexture() {
        return "GUIElecFurnace.png";
    }

    @Override
    public String getGuiTitleKey() {
        return "tile.ic2.machine.electric_furnace.name";
    }
}

