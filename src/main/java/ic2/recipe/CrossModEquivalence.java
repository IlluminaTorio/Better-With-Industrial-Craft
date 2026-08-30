package ic2.recipe;

import ic2.IC2;
import ic2.IC2Items;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.RecipeEntryBase;
import net.minecraft.core.data.registry.recipe.RecipeGroup;
import net.minecraft.core.data.registry.recipe.RecipeNamespace;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryBlastFurnace;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCraftingShaped;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCraftingShapeless;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryFurnace;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;


public final class CrossModEquivalence {
        private CrossModEquivalence() {
        }

        
        private static final List<Item[]> EQUIVALENTS = new ArrayList<Item[]>();

        private static boolean initialized = false;

        public static void applyIfSignalIndustries() {
                if (!FabricLoader.getInstance().isModLoaded("signalindustries")) {
                        return;
                }
                if (initialized) {
                        return;
                }
                initialized = true;
                try {
                        Class<?> items = Class.forName("sunsetsatellite.signalindustries.SIItems");
                        CrossModEquivalence.pair(IC2Items.dustGold, items.getField("goldDust"));
                        CrossModEquivalence.pair(IC2Items.dustIron, items.getField("ironDust"));
                        CrossModEquivalence.pair(IC2Items.dustCoal, items.getField("coalDust"));
                        int[] counter = new int[]{0, 0};
                        CrossModEquivalence.patchAllRecipes(counter);
                        IC2.LOGGER.info("Cross-mod equivalence: {} pairs, {} crafts patched, {} smelts added", (Object)EQUIVALENTS.size(), (Object)counter[0], (Object)counter[1]);
                }
                catch (Throwable t) {
                        IC2.LOGGER.warn("Cross-mod equivalence failed: {}", (Object)t.toString());
                }
        }

        private static void pair(Item ic2Item, java.lang.reflect.Field siField) throws IllegalAccessException {
                if (ic2Item == null) {
                        return;
                }
                Object siItem = siField.get(null);
                if (siItem instanceof Item && siItem != ic2Item) {
                        EQUIVALENTS.add(new Item[]{ic2Item, (Item)siItem});
                }
        }

        
        private static Item equivalentOf(Item item) {
                for (Item[] pair : EQUIVALENTS) {
                        if (pair[0] == item) {
                                return pair[1];
                        }
                        if (pair[1] == item) {
                                return pair[0];
                        }
                }
                return null;
        }

        
        private static RecipeSymbol merge(ItemStack original) {
                Item other = CrossModEquivalence.equivalentOf(original.getItem());
                if (other == null) {
                        return null;
                }
                List<ItemStack> variants = new ArrayList<ItemStack>();
                variants.add(new ItemStack(original.getItem(), original.stackSize, original.getMetadata()));
                variants.add(new ItemStack(other, original.stackSize, original.getMetadata()));
                return new RecipeSymbol(variants);
        }

        private static void patchArray(RecipeSymbol[] symbols, int[] counter) {
                for (int i = 0; i < symbols.length; ++i) {
                        RecipeSymbol symbol = symbols[i];
                        if (symbol == null) {
                                continue;
                        }
                        ItemStack stack = symbol.getStack();
                        if (stack == null) {
                                continue;
                        }
                        RecipeSymbol merged = CrossModEquivalence.merge(stack);
                        if (merged == null) {
                                continue;
                        }
                        symbols[i] = merged;
                        ++counter[0];
                }
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private static void patchAllRecipes(int[] counter) {
                for (RecipeNamespace ns : Registries.RECIPES.values()) {
                        if (ns == null) {
                                continue;
                        }
                        for (RecipeGroup group : ns.values()) {
                                if (group == null) {
                                        continue;
                                }
                                CrossModEquivalence.patchGroup(group, counter);
                        }
                }
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private static void patchGroup(RecipeGroup group, int[] counter) {
                List<Object[]> toAdd = new ArrayList<Object[]>();
                for (Object entryObj : group.getAllRecipes()) {
                        RecipeEntryBase entry = (RecipeEntryBase)entryObj;
                        try {
                                if (entry instanceof RecipeEntryCraftingShaped) {
                                        Object input = entry.getInput();
                                        if (input instanceof RecipeSymbol[]) {
                                                CrossModEquivalence.patchArray((RecipeSymbol[])input, counter);
                                        }
                                        continue;
                                }
                                if (entry instanceof RecipeEntryCraftingShapeless) {
                                        Object input = entry.getInput();
                                        if (input instanceof List) {
                                                List<RecipeSymbol> symbols = (List<RecipeSymbol>)input;
                                                for (int i = 0; i < symbols.size(); ++i) {
                                                        RecipeSymbol symbol = symbols.get(i);
                                                        if (symbol == null) {
                                                                continue;
                                                        }
                                                        ItemStack stack = symbol.getStack();
                                                        if (stack == null) {
                                                                continue;
                                                        }
                                                        RecipeSymbol merged = CrossModEquivalence.merge(stack);
                                                        if (merged == null) {
                                                                continue;
                                                        }
                                                        symbols.set(i, merged);
                                                        ++counter[0];
                                                }
                                        }
                                        continue;
                                }
                                if (entry instanceof RecipeEntryFurnace || entry instanceof RecipeEntryBlastFurnace) {
                                        Object input = entry.getInput();
                                        ItemStack stack = null;
                                        RecipeSymbol symbol = null;
                                        if (input instanceof RecipeSymbol) {
                                                symbol = (RecipeSymbol)input;
                                                stack = symbol.getStack();
                                        } else if (input instanceof RecipeSymbol[] && ((RecipeSymbol[])input).length > 0) {
                                                symbol = ((RecipeSymbol[])input)[0];
                                                stack = symbol.getStack();
                                        }
                                        if (stack == null || CrossModEquivalence.equivalentOf(stack.getItem()) == null) {
                                                continue;
                                        }
                                        
                                        RecipeSymbol merged = CrossModEquivalence.merge(stack);
                                        if (merged == null) {
                                                continue;
                                        }
                                        ItemStack output = (ItemStack)entry.getOutput();
                                        Object duplicate;
                                        if (entry instanceof RecipeEntryFurnace) {
                                                duplicate = new RecipeEntryFurnace(merged, output.copy());
                                        } else {
                                                
                                                List<ItemStack> variants = new ArrayList<ItemStack>();
                                                variants.add(stack.copy());
                                                Item other = CrossModEquivalence.equivalentOf(stack.getItem());
                                                variants.add(new ItemStack(other, stack.stackSize, stack.getMetadata()));
                                                duplicate = new RecipeEntryBlastFurnace(new RecipeSymbol[]{new RecipeSymbol(variants)}, output.copy());
                                        }
                                        toAdd.add(new Object[]{"equiv_" + group.size() + "_" + counter[1], duplicate});
                                        ++counter[1];
                                }
                        }
                        catch (Throwable t) {
                                
                        }
                }
                for (Object[] add : toAdd) {
                        group.register((String)add[0], (RecipeEntryBase)add[1]);
                }
        }
}
