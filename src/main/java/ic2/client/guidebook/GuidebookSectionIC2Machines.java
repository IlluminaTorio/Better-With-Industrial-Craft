package ic2.client.guidebook;

import ic2.IC2;
import ic2.IC2Blocks;
import ic2.recipe.RecipeEntryIC2Machine;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.guidebook.GuidebookPage;
import net.minecraft.client.gui.guidebook.GuidebookSection;
import net.minecraft.client.gui.guidebook.SearchableGuidebookSection;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.RecipeGroup;
import net.minecraft.core.data.registry.recipe.RecipeNamespace;
import net.minecraft.core.data.registry.recipe.SearchQuery;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.collection.Pair;
import net.minecraft.core.util.helper.MathHelper;

public class GuidebookSectionIC2Machines extends SearchableGuidebookSection {
    private static final String[] GROUPS = new String[]{"macerator", "extractor", "compressor", "canner", "mass_fabricator", "wood_gasser", "plasmafier", "rare_earth_extractor", "slow_grinder", "electrolyzer", "rotary_macerator", "singularity_compressor"};
    private static final int RECIPES_PER_PAGE = 3;
    private final List<GuidebookPage> pages = new ArrayList<GuidebookPage>();
    private Pair<String, List<GuidebookPage>> filteredPages = null;

    public GuidebookSectionIC2Machines() {
        super("guidebook.section.ic2.machines", new ItemStack(IC2Blocks.macerator), 0x5C6E63, 0x3A4740);
        this.reloadRecipes();
    }

    public void reloadRecipes() {
        this.pages.clear();
        List<RecipeEntryIC2Machine> allRecipes = collectRecipes();
        int total = allRecipes.size();
        int totalPages = MathHelper.ceilInt(total, RECIPES_PER_PAGE);
        for (int i = 0; i < totalPages; ++i) {
            int j = i * RECIPES_PER_PAGE;
            List<RecipeEntryIC2Machine> chunk = new ArrayList<RecipeEntryIC2Machine>(allRecipes.subList(Math.min(j, total), Math.min(j + RECIPES_PER_PAGE, total)));
            if (!chunk.isEmpty()) {
                this.pages.add(new RecipePageIC2(this, chunk));
            }
        }
    }

    @Override
    public List<GuidebookPage> getPages() {
        return this.pages;
    }

    @Override
    public List<GuidebookSection.Index> getIndices() {
        return null;
    }

    @Override
    public List<GuidebookPage> searchPages(SearchQuery query) {
        if (this.filteredPages != null && Objects.equals((Object)this.filteredPages.getLeft(), (Object)query.rawQuery)) {
            return this.filteredPages.getRight();
        }
        List<RecipeEntryIC2Machine> filtered = new ArrayList<RecipeEntryIC2Machine>();
        for (RecipeEntryIC2Machine recipe : collectRecipes()) {
            if (!recipe.matchesQueryIgnoreExceptions(query)) continue;
            filtered.add(recipe);
        }
        List<GuidebookPage> result = new ArrayList<GuidebookPage>();
        int total = filtered.size();
        int totalPages = MathHelper.ceilInt(total, RECIPES_PER_PAGE);
        for (int i = 0; i < totalPages; ++i) {
            int j = i * RECIPES_PER_PAGE;
            List<RecipeEntryIC2Machine> chunk = new ArrayList<RecipeEntryIC2Machine>(filtered.subList(Math.min(j, total), Math.min(j + RECIPES_PER_PAGE, total)));
            if (chunk.isEmpty()) continue;
            result.add(new RecipePageIC2(this, chunk, query));
        }
        this.filteredPages = Pair.of(query.rawQuery, result);
        return result;
    }

    private static List<RecipeEntryIC2Machine> collectRecipes() {
        List<RecipeEntryIC2Machine> result = new ArrayList<RecipeEntryIC2Machine>();
        try {
            RecipeNamespace ns = (RecipeNamespace)Registries.RECIPES.getItem(IC2.MOD_ID);
            if (ns == null) {
                return result;
            }
            for (String group : GROUPS) {
                RecipeGroup<?> g = (RecipeGroup<?>)ns.getItem(group);
                if (g == null) continue;
                for (Object entry : g.getAllRecipes()) {
                    if (!(entry instanceof RecipeEntryIC2Machine)) continue;
                    result.add((RecipeEntryIC2Machine)entry);
                }
            }
        }
        catch (Throwable t) {
            IC2.LOGGER.warn("Guidebook: failed to collect IC2 machine recipes: {}", (Object)t.toString());
        }
        return result;
    }
}
