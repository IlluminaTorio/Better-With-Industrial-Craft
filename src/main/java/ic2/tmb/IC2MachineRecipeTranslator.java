package ic2.tmb;

import net.minecraft.core.data.registry.recipe.RecipeEntryBase;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.item.ItemStack;
import turing.tmb.vanilla.FurnaceRecipeTranslator;


public class IC2MachineRecipeTranslator
extends FurnaceRecipeTranslator<RecipeEntryBase<RecipeSymbol, ItemStack, Void>> {
    public IC2MachineRecipeTranslator(RecipeEntryBase<RecipeSymbol, ItemStack, Void> recipe) {
        super(recipe);
    }
}
