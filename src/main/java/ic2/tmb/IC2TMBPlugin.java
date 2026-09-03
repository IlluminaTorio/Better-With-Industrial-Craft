package ic2.tmb;

import ic2.IC2;
import ic2.IC2Blocks;
import ic2.IC2Items;
import ic2.recipe.RecipeEntryIC2Machine;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.RecipeEntryBase;
import net.minecraft.core.data.registry.recipe.RecipeGroup;
import net.minecraft.core.data.registry.recipe.RecipeNamespace;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryFurnace;
import net.minecraft.core.item.IFood;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import turing.tmb.TMB;
import turing.tmb.TypedIngredient;
import turing.tmb.api.ITMBPlugin;
import turing.tmb.api.TMBEntrypoint;
import turing.tmb.api.ingredient.ITypedIngredient;
import turing.tmb.api.recipe.IRecipeCategory;
import turing.tmb.api.runtime.ITMBRuntime;

public class IC2TMBPlugin
implements ITMBPlugin,
TMBEntrypoint {
    public static IC2MachineRecipeCategory maceratorCategory;
    public static IC2MachineRecipeCategory extractorCategory;
    public static IC2MachineRecipeCategory compressorCategory;
    public static IC2MachineRecipeCategory cannerCategory;
    public static IC2MachineRecipeCategory massFabricatorCategory;
    public static IC2MachineRecipeCategory woodGasserCategory;
    public static IC2MachineRecipeCategory plasmafierCategory;
    public static IC2MachineRecipeCategory rareEarthExtractorCategory;
    public static IC2MachineRecipeCategory slowGrinderCategory;
    public static IC2MachineRecipeCategory electrolyzerCategory;

    public String getName() {
        return IC2.MOD_ID;
    }

    public void registerRecipeCategories(ITMBRuntime runtime) {
        maceratorCategory = (IC2MachineRecipeCategory)runtime.getRecipeIndex().registerCategory((IRecipeCategory)new IC2MachineRecipeCategory("Macerator", IC2Blocks.macerator.getDefaultStack()));
        extractorCategory = (IC2MachineRecipeCategory)runtime.getRecipeIndex().registerCategory((IRecipeCategory)new IC2MachineRecipeCategory("Extractor", IC2Blocks.extractor.getDefaultStack()));
        compressorCategory = (IC2MachineRecipeCategory)runtime.getRecipeIndex().registerCategory((IRecipeCategory)new IC2MachineRecipeCategory("Compressor", IC2Blocks.compressor.getDefaultStack()));
        cannerCategory = (IC2MachineRecipeCategory)runtime.getRecipeIndex().registerCategory((IRecipeCategory)new IC2MachineRecipeCategory("Canning Machine", IC2Blocks.canner.getDefaultStack()));
        massFabricatorCategory = (IC2MachineRecipeCategory)runtime.getRecipeIndex().registerCategory((IRecipeCategory)new IC2MachineRecipeCategory("Mass Fabricator", IC2Blocks.massFabricator.getDefaultStack()));
        woodGasserCategory = (IC2MachineRecipeCategory)runtime.getRecipeIndex().registerCategory((IRecipeCategory)new IC2MachineRecipeCategory("Wood Gasifier", IC2Blocks.woodGasser.getDefaultStack()));
        plasmafierCategory = (IC2MachineRecipeCategory)runtime.getRecipeIndex().registerCategory((IRecipeCategory)new IC2MachineRecipeCategory("Plasmafier", IC2Blocks.plasmafier.getDefaultStack()));
        rareEarthExtractorCategory = (IC2MachineRecipeCategory)runtime.getRecipeIndex().registerCategory((IRecipeCategory)new IC2MachineRecipeCategory("Rare Earth Extractor", IC2Blocks.rareEarthExtractor.getDefaultStack()));
        slowGrinderCategory = (IC2MachineRecipeCategory)runtime.getRecipeIndex().registerCategory((IRecipeCategory)new IC2MachineRecipeCategory("Slow Grinder", IC2Blocks.slowGrinder.getDefaultStack()));
        electrolyzerCategory = (IC2MachineRecipeCategory)runtime.getRecipeIndex().registerCategory((IRecipeCategory)new IC2MachineRecipeCategory("Electrolyzer", IC2Blocks.electrolyzer.getDefaultStack()));
    }

    public void registerRecipeCatalysts(ITMBRuntime runtime) {
        runtime.getRecipeIndex().registerCatalyst((IRecipeCategory)maceratorCategory, (ITypedIngredient)TypedIngredient.itemStackIngredient((ItemStack)IC2Blocks.macerator.getDefaultStack()));
        runtime.getRecipeIndex().registerCatalyst((IRecipeCategory)extractorCategory, (ITypedIngredient)TypedIngredient.itemStackIngredient((ItemStack)IC2Blocks.extractor.getDefaultStack()));
        runtime.getRecipeIndex().registerCatalyst((IRecipeCategory)compressorCategory, (ITypedIngredient)TypedIngredient.itemStackIngredient((ItemStack)IC2Blocks.compressor.getDefaultStack()));
        runtime.getRecipeIndex().registerCatalyst((IRecipeCategory)cannerCategory, (ITypedIngredient)TypedIngredient.itemStackIngredient((ItemStack)IC2Blocks.canner.getDefaultStack()));
        runtime.getRecipeIndex().registerCatalyst((IRecipeCategory)massFabricatorCategory, (ITypedIngredient)TypedIngredient.itemStackIngredient((ItemStack)IC2Blocks.massFabricator.getDefaultStack()));
        runtime.getRecipeIndex().registerCatalyst((IRecipeCategory)woodGasserCategory, (ITypedIngredient)TypedIngredient.itemStackIngredient((ItemStack)IC2Blocks.woodGasser.getDefaultStack()));
        runtime.getRecipeIndex().registerCatalyst((IRecipeCategory)woodGasserCategory, (ITypedIngredient)TypedIngredient.itemStackIngredient((ItemStack)IC2Blocks.woodGasserElec.getDefaultStack()));
        runtime.getRecipeIndex().registerCatalyst((IRecipeCategory)plasmafierCategory, (ITypedIngredient)TypedIngredient.itemStackIngredient((ItemStack)IC2Blocks.plasmafier.getDefaultStack()));
        runtime.getRecipeIndex().registerCatalyst((IRecipeCategory)rareEarthExtractorCategory, (ITypedIngredient)TypedIngredient.itemStackIngredient((ItemStack)IC2Blocks.rareEarthExtractor.getDefaultStack()));
        runtime.getRecipeIndex().registerCatalyst((IRecipeCategory)slowGrinderCategory, (ITypedIngredient)TypedIngredient.itemStackIngredient((ItemStack)IC2Blocks.slowGrinder.getDefaultStack()));
        runtime.getRecipeIndex().registerCatalyst((IRecipeCategory)electrolyzerCategory, (ITypedIngredient)TypedIngredient.itemStackIngredient((ItemStack)IC2Blocks.electrolyzer.getDefaultStack()));
    }

    public void registerRecipes(ITMBRuntime runtime) {
        runtime.getRecipeIndex().registerRecipes((IRecipeCategory)maceratorCategory, IC2TMBPlugin.getMachineRecipes("macerator"), IC2MachineRecipeTranslator::new);
        runtime.getRecipeIndex().registerRecipes((IRecipeCategory)extractorCategory, IC2TMBPlugin.getMachineRecipes("extractor"), IC2MachineRecipeTranslator::new);
        runtime.getRecipeIndex().registerRecipes((IRecipeCategory)compressorCategory, IC2TMBPlugin.getMachineRecipes("compressor"), IC2MachineRecipeTranslator::new);
        runtime.getRecipeIndex().registerRecipes((IRecipeCategory)cannerCategory, IC2TMBPlugin.getCannerRecipes(), IC2MachineRecipeTranslator::new);
        runtime.getRecipeIndex().registerRecipes((IRecipeCategory)massFabricatorCategory, IC2TMBPlugin.getMachineRecipes("mass_fabricator"), IC2MachineRecipeTranslator::new);
        runtime.getRecipeIndex().registerRecipes((IRecipeCategory)woodGasserCategory, IC2TMBPlugin.getMachineRecipes("wood_gasser"), IC2MachineRecipeTranslator::new);
        runtime.getRecipeIndex().registerRecipes((IRecipeCategory)plasmafierCategory, IC2TMBPlugin.getMachineRecipes("plasmafier"), IC2MachineRecipeTranslator::new);
        runtime.getRecipeIndex().registerRecipes((IRecipeCategory)rareEarthExtractorCategory, IC2TMBPlugin.getMachineRecipes("rare_earth_extractor"), IC2MachineRecipeTranslator::new);
        runtime.getRecipeIndex().registerRecipes((IRecipeCategory)slowGrinderCategory, IC2TMBPlugin.getMachineRecipes("slow_grinder"), IC2MachineRecipeTranslator::new);
        runtime.getRecipeIndex().registerRecipes((IRecipeCategory)electrolyzerCategory, IC2TMBPlugin.getMachineRecipes("electrolyzer"), IC2MachineRecipeTranslator::new);
    }

    private static List<RecipeEntryBase<RecipeSymbol, ItemStack, Void>> getMachineRecipes(String group) {
        ArrayList<RecipeEntryBase<RecipeSymbol, ItemStack, Void>> result = new ArrayList<RecipeEntryBase<RecipeSymbol, ItemStack, Void>>();
        try {
            RecipeNamespace ns = (RecipeNamespace)Registries.RECIPES.getItem(IC2.MOD_ID);
            if (ns == null) {
                return result;
            }
            RecipeGroup g = (RecipeGroup)ns.getItem(group);
            if (g == null) {
                return result;
            }
            for (Object entryObj : g.getAllRecipes()) {


                if (entryObj instanceof RecipeEntryIC2Machine) {
                    result.add((RecipeEntryIC2Machine)entryObj);
                    continue;
                }
                if (entryObj instanceof RecipeEntryFurnace) {
                    result.add((RecipeEntryFurnace)entryObj);
                }
            }
        }
        catch (Exception e) {
            IC2.LOGGER.warn("TMB: failed to collect {} recipes: {}", (Object)group, (Object)e.toString());
        }
        return result;
    }

    private static List<RecipeEntryBase<RecipeSymbol, ItemStack, Void>> getCannerRecipes() {
        ArrayList<RecipeEntryBase<RecipeSymbol, ItemStack, Void>> result = new ArrayList<RecipeEntryBase<RecipeSymbol, ItemStack, Void>>();
        try {
            ItemStack tinCan = IC2Items.tinCan.getDefaultStack();
            ItemStack filled = IC2Items.filledTinCan.getDefaultStack();
            for (Item item : Item.itemsList) {
                if (item == null || !(item instanceof IFood) || item == IC2Items.filledTinCan) continue;
                try {
                    result.add(new RecipeEntryFurnace(new RecipeSymbol(item.getDefaultStack()), filled));
                }
                catch (Throwable throwable) {

                }
                if (result.size() < 32) {
                    continue;
                }
                break;
            }
        }
        catch (Exception e) {
            IC2.LOGGER.warn("TMB: failed to collect canner recipes: {}", (Object)e.toString());
        }
        return result;
    }

    public void onGatherPlugins(boolean isReload) {
        TMB.registerPlugin((ITMBPlugin)this);
    }
}
