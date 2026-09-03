package ic2.compat;

import ic2.IC2;
import ic2.IC2Blocks;
import ic2.IC2Items;
import ic2.IC2Recipes;
import ic2.tileentity.TileEntityCompressor;
import ic2.tileentity.TileEntityExtractor;
import ic2.tileentity.TileEntityMacerator;
import ic2.tileentity.TileEntityWoodGasser;
import java.util.Map;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.item.IItemConvertible;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import turniplabs.halplibe.helper.RecipeBuilder;

public final class CrossModCompat {
    private CrossModCompat() {
    }

    public static void applyAll() {
        CrossModCompat.applyAether();
        CrossModCompat.applyDeep();
    }

    public static Item item(String id) {
        for (Item item : Item.itemsMap.values()) {
            if (item == null || item.namespaceID == null) continue;
            if (id.equals(item.namespaceID.toString())) {
                return item;
            }
        }
        return null;
    }

    public static Block<?> block(String id) {
        for (Block<?> block : Blocks.blocksList) {
            if (block == null || block.namespaceId() == null) continue;
            if (id.equals(block.namespaceId().toString())) {
                return block;
            }
        }
        return null;
    }

    public static Block<?> blockAny(String... ids) {
        for (String id : ids) {
            Block<?> block = CrossModCompat.block(id);
            if (block != null) {
                return block;
            }
        }
        return null;
    }

    public static Block<?> deepOre(String host, String metal) {
        return CrossModCompat.blockAny("deep:block/" + host + "_" + metal + "_ore", "deep:block/block/" + host + "_" + metal + "_ore");
    }

    private static int machine(Map<Integer, ItemStack> recipes, Block<?> input, ItemStack output) {
        if (input == null || output == null || recipes.containsKey(input.id())) {
            return 0;
        }
        recipes.put(input.id(), output);
        return 1;
    }

    private static int machine(Map<Integer, ItemStack> recipes, Item input, ItemStack output) {
        if (input == null || output == null || recipes.containsKey(input.id)) {
            return 0;
        }
        recipes.put(input.id, output);
        return 1;
    }

    private static int gassify(Block<?> log) {
        if (log == null || TileEntityWoodGasser.WoodGasserRecipes.RECIPES.containsKey(log.id())) {
            return 0;
        }
        TileEntityWoodGasser.WoodGasserRecipes.RECIPES.put(log.id(), new ItemStack(Items.COAL, 1, 1));
        return 1;
    }

    private static int plantball(String blockId) {
        Block<?> plant = CrossModCompat.blockAny(blockId, "aether:block/block/" + blockId.substring(blockId.indexOf(47) + 1));
        if (plant == null) {
            return 0;
        }
        String name = "aether_plantball_" + plant.namespaceId().value().replace('/', '_').replace('.', '_');
        RecipeBuilder.Shaped(IC2.MOD_ID).setShape(new String[]{"PPP", "P P", "PPP"}).addInput('P', plant).create(name, IC2Items.plantball.getDefaultStack());
        return 1;
    }

    private static void applyAether() {
        if (!FabricLoader.getInstance().isModLoaded("aether")) {
            return;
        }
        int added = 0;
        try {
            Item ambrosium = CrossModCompat.item("aether:item/ambrosium");
            Item zanite = CrossModCompat.item("aether:item/zanite");
            Item rawGravitite = CrossModCompat.item("aether:item/ore_raw_gravitite");
            Item amber = CrossModCompat.item("aether:item/amber");
            Block<?> cobbleHolystone = CrossModCompat.block("aether:block/cobble_holystone");
            Block<?> glassQuicksoil = CrossModCompat.block("aether:block/glass_quicksoil");
            added += CrossModCompat.machine(TileEntityMacerator.RECIPES, CrossModCompat.blockAny("aether:block/ore_ambrosium_holystone", "aether:block/block/ore_ambrosium_holystone"), ambrosium == null ? null : new ItemStack(ambrosium, 2));
            added += CrossModCompat.machine(TileEntityMacerator.RECIPES, CrossModCompat.blockAny("aether:block/ore_zanite_holystone", "aether:block/block/ore_zanite_holystone"), zanite == null ? null : new ItemStack(zanite, 2));
            added += CrossModCompat.machine(TileEntityMacerator.RECIPES, CrossModCompat.blockAny("aether:block/ore_gravitite_holystone", "aether:block/block/ore_gravitite_holystone"), rawGravitite == null ? null : new ItemStack(rawGravitite, 2));
            added += CrossModCompat.machine(TileEntityMacerator.RECIPES, CrossModCompat.blockAny("aether:block/holystone", "aether:block/block/holystone"), cobbleHolystone == null ? null : cobbleHolystone.getDefaultStack());
            added += CrossModCompat.machine(TileEntityCompressor.RECIPES, CrossModCompat.blockAny("aether:block/quicksoil", "aether:block/block/quicksoil"), glassQuicksoil == null ? null : glassQuicksoil.getDefaultStack());
            added += CrossModCompat.machine(TileEntityExtractor.RECIPES, amber, IC2Items.rubber == null ? null : new ItemStack(IC2Items.rubber, 1));
            added += CrossModCompat.gassify(CrossModCompat.blockAny("aether:block/log_skyroot", "aether:block/block/log_skyroot"));
            added += CrossModCompat.gassify(CrossModCompat.blockAny("aether:block/log_oak_golden", "aether:block/block/log_oak_golden"));
            added += CrossModCompat.plantball("aether:block/sapling_skyroot");
            added += CrossModCompat.plantball("aether:block/leaves_skyroot");
            added += CrossModCompat.plantball("aether:block/leaves_oak_golden");
            added += CrossModCompat.plantball("aether:block/flower_purple");
            added += CrossModCompat.plantball("aether:block/flower_white");
        }
        catch (Throwable t) {
            IC2.LOGGER.warn("Aether compatibility error: {}", (Object)t.toString());
        }
        if (added > 0) {
            IC2.LOGGER.info("Better With Aether compatibility: {} recipes added.", (Object)added);
        }
    }

    private static void applyDeep() {
        if (!FabricLoader.getInstance().isModLoaded("deep")) {
            return;
        }
        int added = 0;
        try {
            Item rawSilver = CrossModCompat.item("deep:item/raw_silver");
            Item rawLead = CrossModCompat.item("deep:item/raw_lead");
            Item uranium = CrossModCompat.item("deep:item/uranium");
            Item amethyst = CrossModCompat.item("deep:item/amethyst");
            Item rhodonite = CrossModCompat.item("deep:item/rhodonite");
            Item topaz = CrossModCompat.item("deep:item/topaz");
            Item ingotSilver = CrossModCompat.item("deep:item/ingot_silver");
            Item ingotLead = CrossModCompat.item("deep:item/ingot_lead");
            String[] hosts = new String[]{"stone", "basalt", "limestone", "granite", "permafrost"};
            for (String host : hosts) {
                added += CrossModCompat.machine(TileEntityMacerator.RECIPES, CrossModCompat.deepOre(host, "silver"), rawSilver == null ? null : new ItemStack(rawSilver, 2));
                added += CrossModCompat.machine(TileEntityMacerator.RECIPES, CrossModCompat.deepOre(host, "lead"), rawLead == null ? null : new ItemStack(rawLead, 2));
                added += CrossModCompat.machine(TileEntityMacerator.RECIPES, CrossModCompat.deepOre(host, "uranium"), uranium == null ? null : new ItemStack(uranium, 2));
                added += CrossModCompat.machine(TileEntityMacerator.RECIPES, CrossModCompat.deepOre(host, "amethyst"), amethyst == null ? null : new ItemStack(amethyst, 2));
            }
            added += CrossModCompat.machine(TileEntityMacerator.RECIPES, CrossModCompat.deepOre("netherrack", "silver"), rawSilver == null ? null : new ItemStack(rawSilver, 2));
            for (String host : new String[]{"netherrack", "basalt", "gloomstone"}) {
                added += CrossModCompat.machine(TileEntityMacerator.RECIPES, CrossModCompat.deepOre(host, "rhodonite"), rhodonite == null ? null : new ItemStack(rhodonite, 4));
            }
            added += CrossModCompat.machine(TileEntityMacerator.RECIPES, CrossModCompat.deepOre("netherrack", "topaz"), topaz == null ? null : new ItemStack(topaz, 2));
            if (rawSilver != null && IC2Items.dustSilver != null) {
                added += CrossModCompat.machine(TileEntityMacerator.RECIPES, rawSilver, new ItemStack(IC2Items.dustSilver, 2));
            }
            if (rawLead != null && IC2Items.dustLead != null) {
                added += CrossModCompat.machine(TileEntityMacerator.RECIPES, rawLead, new ItemStack(IC2Items.dustLead, 2));
            }
            if (uranium != null && IC2Items.dustUranium != null) {
                added += CrossModCompat.machine(TileEntityMacerator.RECIPES, uranium, new ItemStack(IC2Items.dustUranium, 2));
            }
            if (ingotSilver != null && IC2Items.dustSilver != null) {
                added += CrossModCompat.machine(TileEntityMacerator.RECIPES, ingotSilver, new ItemStack(IC2Items.dustSilver));
                IC2Recipes.shared("deep_silver_dust_ingot", IC2Items.dustSilver, ingotSilver.getDefaultStack());
                ++added;
            }
            if (ingotLead != null && IC2Items.dustLead != null) {
                added += CrossModCompat.machine(TileEntityMacerator.RECIPES, ingotLead, new ItemStack(IC2Items.dustLead));
                IC2Recipes.shared("deep_lead_dust_ingot", IC2Items.dustLead, ingotLead.getDefaultStack());
                ++added;
            }
            if (uranium != null) {
                IC2Recipes.shared("deep_uranium_refine", uranium, IC2Items.ingotUran.getDefaultStack());
                added += CrossModCompat.machine(TileEntityCompressor.RECIPES, uranium, IC2Items.ingotUran.getDefaultStack());
                ++added;
            }
            Item silver = ingotSilver;
            if (silver != null) {
                RecipeBuilder.Shaped(IC2.MOD_ID).setShape(new String[]{"III", "BBB", "SSS"}).addInput('I', (IItemConvertible)IC2Items.ingotRefinedIron).addInput('B', (IItemConvertible)IC2Items.ingotBronze).addInput('S', (IItemConvertible)silver).create("mixed_metal_silver", new ItemStack(IC2Items.ingotMixedMetal, 2));
                ++added;
            }
            Item lead = ingotLead;
            if (lead != null) {
                RecipeBuilder.Shaped(IC2.MOD_ID).setShape(new String[]{" L ", "LAL", " L "}).addInput('L', (IItemConvertible)lead).addInput('A', (IItemConvertible)IC2Items.advancedAlloy).create("reactor_plating_lead", IC2Items.reactorPlating.getDefaultStack());
                ++added;
            }
            if (uranium != null) {
                RecipeBuilder.Shaped(IC2.MOD_ID).setShape(new String[]{"UUU", "UTU", "UUU"}).addInput('U', (IItemConvertible)uranium).addInput('T', IC2Blocks.industrialTnt).create("nuke_deep", IC2Blocks.nuke.getDefaultStack());
                ++added;
            }
        }
        catch (Throwable t) {
            IC2.LOGGER.warn("DEEP compatibility error: {}", (Object)t.toString());
        }
        if (added > 0) {
            IC2.LOGGER.info("DEEP compatibility: {} recipes added.", (Object)added);
        }
    }
}
