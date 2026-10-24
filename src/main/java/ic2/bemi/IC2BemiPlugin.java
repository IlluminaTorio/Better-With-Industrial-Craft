package ic2.bemi;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiRecipeSorting;
import dev.emi.emi.api.stack.EmiStack;
import ic2.IC2;
import ic2.IC2Blocks;
import ic2.recipe.RecipeEntryIC2Machine;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.block.Block;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.RecipeGroup;
import net.minecraft.core.data.registry.recipe.RecipeNamespace;
import net.minecraft.core.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class IC2BemiPlugin
implements EmiPlugin {
    private static final String[] MACHINE_GROUPS = new String[]{"macerator", "extractor", "compressor", "canner", "wood_gasser", "plasmafier", "rare_earth_extractor", "slow_grinder", "electrolyzer", "rotary_macerator", "singularity_compressor"};
    private static final Map<Identifier, IC2Category> CATEGORIES = new HashMap<Identifier, IC2Category>();

    @Override
    public void register(EmiRegistry registry) {
        try {
            IC2BemiPlugin.registerCategories(registry);
            IC2BemiPlugin.registerMachineRecipes(registry);
            IC2BemiPlugin.registerElectricWorkstations(registry);
        }
        catch (Throwable t) {
            IC2.LOGGER.warn("BEMI: plugin registration failed: {}", (Object)t.toString());
        }
        finally {
            CATEGORIES.clear();
        }
    }

    private static void registerElectricWorkstations(EmiRegistry registry) {
        try {
            registry.addWorkstation(dev.emi.emi.VanillaPlugin.SMELTING, EmiStack.of(IC2Blocks.electricFurnace.getDefaultStack()));
            registry.addWorkstation(dev.emi.emi.VanillaPlugin.SMELTING, EmiStack.of(IC2Blocks.ironFurnace.getDefaultStack()));
            registry.addWorkstation(dev.emi.emi.VanillaPlugin.SMELTING, EmiStack.of(IC2Blocks.inductionFurnace.getDefaultStack()));
            registry.addWorkstation(dev.emi.emi.VanillaPlugin.TROMMEL, EmiStack.of(IC2Blocks.electricTrommel.getDefaultStack()));
            registry.addWorkstation(dev.emi.emi.VanillaPlugin.BLAST_SMELTING, EmiStack.of(IC2Blocks.electricBlastFurnace.getDefaultStack()));
        }
        catch (Throwable t) {
            IC2.LOGGER.warn("BEMI: failed to register electric machine workstations: {}", (Object)t.toString());
        }
    }

    private static void registerCategories(EmiRegistry registry) {
        IC2BemiPlugin.category(registry, "macerator", "Macerator", IC2Blocks.macerator, false);
        IC2BemiPlugin.category(registry, "extractor", "Extractor", IC2Blocks.extractor, false);
        IC2BemiPlugin.category(registry, "compressor", "Compressor", IC2Blocks.compressor, false);
        IC2BemiPlugin.category(registry, "canner", "Canning Machine", IC2Blocks.canner, false);
        IC2BemiPlugin.category(registry, "wood_gasser", "Wood Gasifier", IC2Blocks.woodGasser, true);
        IC2BemiPlugin.category(registry, "plasmafier", "Plasmafier", IC2Blocks.plasmafier, false);
        IC2BemiPlugin.category(registry, "rare_earth_extractor", "Rare Earth Extractor", IC2Blocks.rareEarthExtractor, false);
        IC2BemiPlugin.category(registry, "slow_grinder", "Slow Grinder", IC2Blocks.slowGrinder, false);
        IC2BemiPlugin.category(registry, "electrolyzer", "Electrolyzer", IC2Blocks.electrolyzer, false);
        IC2BemiPlugin.category(registry, "rotary_macerator", "Rotary Macerator", IC2Blocks.rotaryMacerator, false);
        IC2BemiPlugin.category(registry, "singularity_compressor", "Singularity Compressor", IC2Blocks.singularityCompressor, false);
        IC2BemiPlugin.advWorkstations(registry);
    }

    private static void advWorkstations(EmiRegistry registry) {
        try {
            IC2Category macerator = CATEGORIES.get(new Identifier(IC2.MOD_ID, "bemi_macerator"));
            IC2Category compressor = CATEGORIES.get(new Identifier(IC2.MOD_ID, "bemi_compressor"));
            if (macerator != null) {
                registry.addWorkstation((EmiRecipeCategory)macerator, EmiStack.of(IC2Blocks.rotaryMacerator.getDefaultStack()));
            }
            if (compressor != null) {
                registry.addWorkstation((EmiRecipeCategory)compressor, EmiStack.of(IC2Blocks.singularityCompressor.getDefaultStack()));
            }
        }
        catch (Throwable t) {
            IC2.LOGGER.warn("BEMI: failed to register advanced machine workstations: {}", (Object)t.toString());
        }
    }

    private static void category(EmiRegistry registry, String group, String name, Block<?> machine, boolean electricVariant) {
        Identifier id = new Identifier(IC2.MOD_ID, "bemi_" + group);
        IC2Category category = new IC2Category(id, name, EmiStack.of(machine.getDefaultStack()));
        registry.addCategory(category);
        registry.addWorkstation(category, EmiStack.of(machine.getDefaultStack()));
        if (electricVariant) {
            registry.addWorkstation(category, EmiStack.of(IC2Blocks.woodGasserElec.getDefaultStack()));
        }
        CATEGORIES.put(id, category);
    }

    private static void registerMachineRecipes(EmiRegistry registry) {
        for (String group : MACHINE_GROUPS) {
            IC2Category category = CATEGORIES.get(new Identifier(IC2.MOD_ID, "bemi_" + group));
            if (category == null) continue;
            List<RecipeEntryIC2Machine> recipes = IC2BemiPlugin.getMachineRecipes(group);
            int index = 0;
            for (RecipeEntryIC2Machine recipe : recipes) {
                Identifier id = new Identifier(IC2.MOD_ID, "bemi/" + group + "/" + index);
                IC2BemiPlugin.addRecipeSafe(registry, () -> new EmiIC2MachineRecipe(id, category, recipe), recipe);
                ++index;
            }
        }
    }

    private static List<RecipeEntryIC2Machine> getMachineRecipes(String group) {
        ArrayList<RecipeEntryIC2Machine> result = new ArrayList<RecipeEntryIC2Machine>();
        try {
            RecipeNamespace ns = (RecipeNamespace)Registries.RECIPES.getItem(IC2.MOD_ID);
            if (ns == null) {
                return result;
            }
            RecipeGroup<?> g = (RecipeGroup<?>)ns.getItem(group);
            if (g == null) {
                return result;
            }
            for (Object entry : g.getAllRecipes()) {
                if (!(entry instanceof RecipeEntryIC2Machine)) continue;
                result.add((RecipeEntryIC2Machine)entry);
            }
        }
        catch (Throwable t) {
            IC2.LOGGER.warn("BEMI: failed to collect {} recipes: {}", (Object)group, (Object)t.toString());
        }
        return result;
    }

    private static void addRecipeSafe(EmiRegistry registry, Supplier<EmiRecipe> supplier, Object original) {
        try {
            registry.addRecipe(supplier.get());
        }
        catch (Throwable t) {
            IC2.LOGGER.warn("BEMI: failed to add recipe {}: {}", (Object)String.valueOf(original), (Object)t.toString());
        }
    }

    public static class IC2Category
    extends EmiRecipeCategory {
        private final String name;

        public IC2Category(Identifier id, String name, EmiStack icon) {
            super(id, icon, icon, EmiRecipeSorting.compareOutputThenInput());
            this.name = name;
        }

        @Override
        public Text getName() {
            return EmiPort.literal(this.name);
        }
    }
}
