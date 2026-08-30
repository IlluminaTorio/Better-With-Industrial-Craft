

package ic2;

import ic2.IC2;
import ic2.IC2Blocks;
import ic2.IC2Items;
import ic2.item.ItemCablePlaceable;
import ic2.tileentity.TileEntityCompressor;
import ic2.tileentity.TileEntityExtractor;
import ic2.tileentity.TileEntityMacerator;
import java.util.List;
import java.util.Map;
import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.crafting.LookupFuelFurnace;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.RecipeEntryBase;
import net.minecraft.core.data.registry.recipe.RecipeGroup;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryFurnace;
import net.minecraft.core.item.IItemConvertible;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemBucket;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.DyeColor;
import turniplabs.halplibe.helper.RecipeBuilder;
import turniplabs.halplibe.helper.recipeBuilders.RecipeBuilderShaped;

public class IC2Recipes {
    public static void initNamespaces() {
        RecipeBuilder.initNameSpace((String)IC2.MOD_ID);
        RecipeBuilder.getRecipeGroup((String)IC2.MOD_ID, (String)"macerator", (RecipeSymbol)new RecipeSymbol(IC2Blocks.macerator.getDefaultStack()));
        RecipeBuilder.getRecipeGroup((String)IC2.MOD_ID, (String)"extractor", (RecipeSymbol)new RecipeSymbol(IC2Blocks.extractor.getDefaultStack()));
        RecipeBuilder.getRecipeGroup((String)IC2.MOD_ID, (String)"compressor", (RecipeSymbol)new RecipeSymbol(IC2Blocks.compressor.getDefaultStack()));
        RecipeBuilder.getRecipeGroup((String)IC2.MOD_ID, (String)"canner", (RecipeSymbol)new RecipeSymbol(IC2Blocks.canner.getDefaultStack()));
    }

    public static void onRecipesReady() {
        IC2Recipes.initMachineRecipes();
        IC2Recipes.registerSmelting();
        IC2Recipes.registerBasics();
        IC2Recipes.registerCables();
        IC2Recipes.registerComponents();
        IC2Recipes.registerMachines();
        IC2Recipes.registerGenerators();
        IC2Recipes.registerEnergy();
        IC2Recipes.registerTools();
        IC2Recipes.registerArmor();
        IC2Recipes.registerMisc();
        IC2Recipes.registerSteelAlternatives();
        IC2Recipes.registerMachineRecipeGroups();
        IC2Recipes.registerTrommelInjections();
        IC2Recipes.registerBlastFurnace();
        IC2Recipes.registerItemGroups();
        
        ic2.si.SIConverters.registerRecipes();
        
        ic2.recipe.CrossModEquivalence.applyIfSignalIndustries();
    }

    private static void registerItemGroups() {
        try {
            List<ItemStack> logs = (List<ItemStack>)Registries.ITEM_GROUPS.getItem("minecraft:logs");
            if (logs != null && logs.stream().noneMatch(s -> s.getItem() == IC2Blocks.rubberWood.asItem())) {
                logs.add(new ItemStack(IC2Blocks.rubberWood));
            }
            List<ItemStack> leaves = (List<ItemStack>)Registries.ITEM_GROUPS.getItem("minecraft:leaves");
            if (leaves != null && leaves.stream().noneMatch(s -> s.getItem() == IC2Blocks.rubberLeaves.asItem())) {
                leaves.add(new ItemStack(IC2Blocks.rubberLeaves));
            }
        }
        catch (Exception e) {
            IC2.LOGGER.error("Failed to add rubber wood to minecraft:logs group", (Throwable)e);
        }
    }

    private static void initMachineRecipes() {
        TileEntityMacerator.initRecipes();
        TileEntityExtractor.initRecipes();
        TileEntityCompressor.initRecipes();
    }

    private static void registerMachineRecipeGroups() {
        RecipeGroup macerator = RecipeBuilder.getRecipeGroup((String)IC2.MOD_ID, (String)"macerator", (RecipeSymbol)new RecipeSymbol(IC2Blocks.macerator.getDefaultStack()));
        int i = 0;
        for (Map.Entry<Integer, ItemStack> e : TileEntityMacerator.RECIPES.entrySet()) {
            ItemStack itemStack = IC2Recipes.stackForId(e.getKey());
            if (itemStack == null) continue;
            macerator.register("macerator_" + i++, (RecipeEntryBase)new ic2.recipe.RecipeEntryIC2Machine(new RecipeSymbol(itemStack), (ItemStack)e.getValue()));
        }
        RecipeGroup extractor = RecipeBuilder.getRecipeGroup((String)IC2.MOD_ID, (String)"extractor", (RecipeSymbol)new RecipeSymbol(IC2Blocks.extractor.getDefaultStack()));
        i = 0;
        for (Map.Entry entry : TileEntityExtractor.RECIPES.entrySet()) {
            ItemStack input = IC2Recipes.stackForId((Integer)entry.getKey());
            if (input == null) continue;
            extractor.register("extractor_" + i++, (RecipeEntryBase)new ic2.recipe.RecipeEntryIC2Machine(new RecipeSymbol(input), (ItemStack)entry.getValue()));
        }
        RecipeGroup compressor = RecipeBuilder.getRecipeGroup((String)IC2.MOD_ID, (String)"compressor", (RecipeSymbol)new RecipeSymbol(IC2Blocks.compressor.getDefaultStack()));
        i = 0;
        for (Map.Entry<Integer, ItemStack> e : TileEntityCompressor.RECIPES.entrySet()) {
            ItemStack input = IC2Recipes.stackForId(e.getKey());
            if (input == null) continue;
            compressor.register("compressor_" + i++, (RecipeEntryBase)new ic2.recipe.RecipeEntryIC2Machine(new RecipeSymbol(input), e.getValue()));
        }
        RecipeGroup canner = RecipeBuilder.getRecipeGroup((String)IC2.MOD_ID, (String)"canner", (RecipeSymbol)new RecipeSymbol(IC2Blocks.canner.getDefaultStack()));
        canner.register("canner_tin_can", (RecipeEntryBase)new ic2.recipe.RecipeEntryIC2Machine(new RecipeSymbol(IC2Items.tinCan.getDefaultStack()), IC2Items.filledTinCan.getDefaultStack()));
        canner.register("canner_fuel_can", (RecipeEntryBase)new ic2.recipe.RecipeEntryIC2Machine(new RecipeSymbol(IC2Items.cellCoalfuel.getDefaultStack()), IC2Items.fuelCanFilled.getDefaultStack()));
        canner.register("canner_fuel_can_bio", (RecipeEntryBase)new ic2.recipe.RecipeEntryIC2Machine(new RecipeSymbol(IC2Items.cellBiofuel.getDefaultStack()), IC2Items.fuelCanFilled.getDefaultStack()));
        
        RecipeGroup massFab = RecipeBuilder.getRecipeGroup((String)IC2.MOD_ID, (String)"mass_fabricator", (RecipeSymbol)new RecipeSymbol(IC2Blocks.massFabricator.getDefaultStack()));
        int u = 0;
        for (Object[] r : UU_RECIPES) {
            int count = (Integer)r[0];
            ItemStack input = new ItemStack(IC2Items.uuMatter, Math.min(count, 9));
            massFab.register("uu_" + u++, (RecipeEntryBase)new ic2.recipe.RecipeEntryIC2Machine(new RecipeSymbol(input), (ItemStack)((ItemStack)r[1]).copy()));
        }
    }

    private static ItemStack stackForId(int id) {
        Item item;
        if (id < Item.itemsList.length && (item = Item.itemsList[id]) != null) {
            return item.getDefaultStack();
        }
        Block block = Blocks.getBlock((int)id);
        return block != null ? block.getDefaultStack() : null;
    }

    private static void registerSmelting() {
        IC2Recipes.furnace("refined_iron", Items.INGOT_IRON, IC2Items.ingotRefinedIron.getDefaultStack());
        IC2Recipes.shared("raw_tin", IC2Items.rawTin, IC2Items.ingotTin.getDefaultStack());
        IC2Recipes.shared("raw_copper", IC2Items.rawCopper, IC2Items.ingotCopper.getDefaultStack());
        IC2Recipes.shared("uranium_refine", IC2Items.uraniumItem, IC2Items.ingotUran.getDefaultStack());
        IC2Recipes.furnace("steel_ingot", IC2Items.dustSteel, Items.INGOT_STEEL.getDefaultStack());
        IC2Recipes.furnace("iron_ingot", IC2Items.dustIron, Items.INGOT_IRON.getDefaultStack());
        IC2Recipes.furnace("gold_ingot", IC2Items.dustGold, Items.INGOT_GOLD.getDefaultStack());
        IC2Recipes.furnace("tin_ingot_dust", IC2Items.dustTin, IC2Items.ingotTin.getDefaultStack());
        IC2Recipes.furnace("copper_ingot_dust", IC2Items.dustCopper, IC2Items.ingotCopper.getDefaultStack());
        IC2Recipes.furnace("bronze_ingot", IC2Items.dustBronze, IC2Items.ingotBronze.getDefaultStack());
        IC2Recipes.furnace("coal_dust", IC2Items.hydratedCoalDust, IC2Items.dustCoal.getDefaultStack());
        IC2Recipes.furnace("charcoal", IC2Blocks.rubberWood, new ItemStack(Items.COAL, 1, 1));
    }

    private static void furnace(String id, Object input, ItemStack output) {
        RecipeBuilder.Furnace((String)IC2.MOD_ID).setInput(IC2Recipes.stackOf(input)).create(id, output);
    }

    private static void shared(String id, Object input, ItemStack output) {
        RecipeBuilder.Furnace((String)IC2.MOD_ID).setInput(IC2Recipes.stackOf(input)).create(id, output);
        RecipeBuilder.BlastFurnace((String)IC2.MOD_ID).setInput(IC2Recipes.stackOf(input)).create(id, output);
    }

    private static void smallDust(Item small, Item full, String name) {
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"SSS", "SSS", "SSS"}).addInput('S', (IItemConvertible)small).create("small_" + name + "_dust", full.getDefaultStack());
        RecipeBuilder.Shapeless((String)IC2.MOD_ID).addInput((IItemConvertible)full).create(name + "_dust_split", new ItemStack(small, 9));
    }

    private static ItemStack stackOf(Object o) {
        if (o instanceof Item) {
            Item item = (Item)o;
            return item.getDefaultStack();
        }
        if (o instanceof Block) {
            Block block = (Block)o;
            return block.getDefaultStack();
        }
        if (o instanceof ItemStack) {
            ItemStack stack = (ItemStack)o;
            return stack;
        }
        throw new IllegalArgumentException("Bad recipe input: " + String.valueOf(o));
    }

    private static void registerBasics() {
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"W"}).addInput('W', IC2Blocks.rubberWood).create("rubber_planks", new ItemStack(Blocks.PLANKS_OAK_PAINTED, 4, DyeColor.ORANGE.blockMeta));
        IC2Recipes.smallDust(IC2Items.dustIronSmall, IC2Items.dustIron, "iron");
        IC2Recipes.smallDust(IC2Items.dustGoldSmall, IC2Items.dustGold, "gold");
        IC2Recipes.smallDust(IC2Items.dustCopperSmall, IC2Items.dustCopper, "copper");
        IC2Recipes.smallDust(IC2Items.dustTinSmall, IC2Items.dustTin, "tin");
        IC2Recipes.smallDust(IC2Items.dustBronzeSmall, IC2Items.dustBronze, "bronze");
        IC2Recipes.smallDust(IC2Items.dustSteelSmall, IC2Items.dustSteel, "steel");
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{" P ", "PPP", "P  "}).addInput('P', (IItemConvertible)Blocks.PLANKS_OAK).create("treetap", IC2Items.treetap.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"c", "C", "C"}).addInput('c', (IItemConvertible)IC2Items.cableItems[0]).addInput('C', (IItemConvertible)IC2Items.circuit).create("frequency_transmitter", IC2Items.frequencyTransmitter.getDefaultStack());
        RecipeBuilder.Shapeless((String)IC2.MOD_ID).addInput((IItemConvertible)IC2Items.dustTin).addInput((IItemConvertible)IC2Items.dustCopper).addInput((IItemConvertible)IC2Items.dustCopper).addInput((IItemConvertible)IC2Items.dustCopper).create("bronze_dust", new ItemStack(IC2Items.dustBronze, 2));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"III", "BBB", "TTT"}).addInput('I', (IItemConvertible)IC2Items.ingotRefinedIron).addInput('B', (IItemConvertible)IC2Items.ingotBronze).addInput('T', (IItemConvertible)IC2Items.ingotTin).create("mixed_metal", new ItemStack(IC2Items.ingotMixedMetal, 2));
        IC2Recipes.metalBlock("copper", IC2Items.ingotCopper, IC2Blocks.copperBlock);
        IC2Recipes.metalBlock("tin", IC2Items.ingotTin, IC2Blocks.tinBlock);
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"MMM", "MMM", "MMM"}).addInput('M', (IItemConvertible)IC2Items.stickyResin).create("resin_block", IC2Blocks.resinBlock.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"B"}).addInput('B', IC2Blocks.resinBlock).create("resin_from_block", new ItemStack(IC2Items.stickyResin, 9));
        IC2Recipes.metalBlock("bronze", IC2Items.ingotBronze, IC2Blocks.bronzeBlock);
        IC2Recipes.metalBlock("uranium", IC2Items.ingotUran, IC2Blocks.uraniumBlock);
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"RRR", "RRR"}).addInput('R', (IItemConvertible)IC2Items.rubber).create("rubber_sheet", new ItemStack(IC2Blocks.rubberSheet, 3));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"RGR", "GRG", "RGR"}).addInput('R', (IItemConvertible)Items.DUST_REDSTONE).addInput('G', (IItemConvertible)IC2Items.dustGold).create("glowstone_dust", new ItemStack(Items.DUST_GLOWSTONE, 1));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"RCR", "CRC", "RCR"}).addInput('R', (IItemConvertible)Items.DUST_REDSTONE).addInput('C', (IItemConvertible)IC2Items.dustCoal).create("coal_from_dust", new ItemStack(Items.COAL, 3));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"T T", "TTT"}).addInput('T', (IItemConvertible)IC2Items.ingotTin).create("tin_can", new ItemStack(IC2Items.tinCan, 4));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{" TT", "T T", "TTT"}).addInput('T', (IItemConvertible)IC2Items.ingotTin).create("fuel_can", IC2Items.fuelCanEmpty.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{" T ", "T T", " T "}).addInput('T', (IItemConvertible)IC2Items.ingotTin).create("empty_cell", new ItemStack(IC2Items.cellEmpty, 16));
        RecipeBuilder.Shapeless((String)IC2.MOD_ID).addInput((IItemConvertible)IC2Items.cellEmpty).addInput(IC2Recipes.waterBucket()).create("water_cell", IC2Items.cellWater.getDefaultStack());
        RecipeBuilder.Shapeless((String)IC2.MOD_ID).addInput((IItemConvertible)IC2Items.cellEmpty).addInput(IC2Recipes.lavaBucket()).create("lava_cell", IC2Items.cellLava.getDefaultStack());
        RecipeBuilder.Shapeless((String)IC2.MOD_ID).addInput((IItemConvertible)IC2Items.cellWater).addInput((IItemConvertible)IC2Items.cellWater).addInput((IItemConvertible)IC2Items.cellLava).addInput((IItemConvertible)IC2Items.cellLava).create("obsidian_from_cells", new ItemStack(Blocks.OBSIDIAN, 1));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"CCC", "CWC", "CCC"}).addInput('C', (IItemConvertible)IC2Items.dustCoal).addInput('W', IC2Recipes.waterBucket()).create("hydrated_coal_dust", IC2Items.hydratedCoalDust.getDefaultStack());
        IC2Recipes.plantball("plantball_sapling", Blocks.SAPLING_OAK);
        IC2Recipes.plantball("plantball_leaves", Blocks.LEAVES_OAK);
        IC2Recipes.plantball("plantball_sugarcane", Items.SUGARCANE);
        IC2Recipes.plantball("plantball_wheat", Items.WHEAT);
        IC2Recipes.plantball("plantball_rubber", IC2Blocks.rubberSapling);
        IC2Recipes.plantball("plantball_cactus", Blocks.CACTUS);
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"T T", " T "}).addInput('T', (IItemConvertible)IC2Items.ingotTin).create("tin_bucket", new ItemStack(Items.BUCKET_IRON, 1));
        LookupFuelFurnace.instance.addFuelEntry(Items.SUGARCANE.id, 50);
        LookupFuelFurnace.instance.addFuelEntry(Blocks.CACTUS.id(), 50);
        LookupFuelFurnace.instance.addFuelEntry(IC2Blocks.rubberSapling.id(), 80);
        LookupFuelFurnace.instance.addFuelEntry(IC2Items.scrap.id, 350);
        RecipeBuilder.Shapeless((String)IC2.MOD_ID).addInput((IItemConvertible)IC2Items.cellEmpty).addInput((IItemConvertible)IC2Items.hydratedCoal).create("hydrated_coal_cell", IC2Items.cellHydratedCoal.getDefaultStack());
        RecipeBuilder.Shapeless((String)IC2.MOD_ID).addInput((IItemConvertible)IC2Items.cellEmpty).addInput((IItemConvertible)IC2Items.compressedPlants).addInput((IItemConvertible)IC2Items.compressedPlants).create("bio_cell", IC2Items.cellBiomass.getDefaultStack());
        RecipeBuilder.Shapeless((String)IC2.MOD_ID).addInput((IItemConvertible)IC2Items.cellEmpty).addInput((IItemConvertible)IC2Items.ingotUran).create("uranium_cell", IC2Items.cellUran.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"CCC", "CUC", "CCC"}).addInput('C', (IItemConvertible)IC2Items.cellEmpty).addInput('U', (IItemConvertible)IC2Items.ingotUran).create("near_depleted_cells", new ItemStack(IC2Items.cellNearDepletedUranium, 8));
        RecipeBuilder.Shapeless((String)IC2.MOD_ID).addInput((IItemConvertible)IC2Items.cellNearDepletedUranium).addInput((IItemConvertible)IC2Items.dustCoal).create("depleted_isotope", new ItemStack(IC2Items.cellDepletedIsotope, 1, 9999));
        RecipeBuilder.Shapeless((String)IC2.MOD_ID).addInput((IItemConvertible)IC2Items.cellReEnrichedUranium).addInput((IItemConvertible)IC2Items.dustCoal).create("uranium_cell_reenriched", IC2Items.cellUran.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"CCC", "CFC", "CCC"}).addInput('C', (IItemConvertible)IC2Items.dustCoal).addInput('F', (IItemConvertible)Items.FLINT).create("coal_ball", IC2Items.coalBall.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"DDD", " S ", " S "}).addInput('D', (IItemConvertible)IC2Items.industrialDiamond).addInput('S', (IItemConvertible)Items.STICK).create("diamond_pickaxe", new ItemStack(Items.TOOL_PICKAXE_DIAMOND, 1));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"DD ", "DS ", " S "}).addInput('D', (IItemConvertible)IC2Items.industrialDiamond).addInput('S', (IItemConvertible)Items.STICK).create("diamond_axe", new ItemStack(Items.TOOL_AXE_DIAMOND, 1));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"D", "S", "S"}).addInput('D', (IItemConvertible)IC2Items.industrialDiamond).addInput('S', (IItemConvertible)Items.STICK).create("diamond_shovel", new ItemStack(Items.TOOL_SHOVEL_DIAMOND, 1));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"D", "D", "S"}).addInput('D', (IItemConvertible)IC2Items.industrialDiamond).addInput('S', (IItemConvertible)Items.STICK).create("diamond_sword", new ItemStack(Items.TOOL_SWORD_DIAMOND, 1));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"DD ", " S ", " S "}).addInput('D', (IItemConvertible)IC2Items.industrialDiamond).addInput('S', (IItemConvertible)Items.STICK).create("diamond_hoe", new ItemStack(Items.TOOL_HOE_DIAMOND, 1));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"DDD", "D D"}).addInput('D', (IItemConvertible)IC2Items.industrialDiamond).create("diamond_helmet", new ItemStack(Items.ARMOR_HELMET_DIAMOND, 1));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"D D", "DDD", "DDD"}).addInput('D', (IItemConvertible)IC2Items.industrialDiamond).create("diamond_chestplate", new ItemStack(Items.ARMOR_CHESTPLATE_DIAMOND, 1));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"DDD", "D D", "D D"}).addInput('D', (IItemConvertible)IC2Items.industrialDiamond).create("diamond_leggings", new ItemStack(Items.ARMOR_LEGGINGS_DIAMOND, 1));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"D D", "D D"}).addInput('D', (IItemConvertible)IC2Items.industrialDiamond).create("diamond_boots", new ItemStack(Items.ARMOR_BOOTS_DIAMOND, 1));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{" DD", "DDD", "D D"}).addInput('D', (IItemConvertible)IC2Items.industrialDiamond).create("diamond_wolf_armor", Items.ARMOR_WOLF_DIAMOND.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"DD ", "DDD", "D D"}).addInput('D', (IItemConvertible)IC2Items.industrialDiamond).create("diamond_wolf_armor_mirrored", Items.ARMOR_WOLF_DIAMOND.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"DDD", "DDD", "DDD"}).addInput('D', (IItemConvertible)IC2Items.industrialDiamond).create("diamond_block", new ItemStack(Blocks.BLOCK_DIAMOND, 1));
        RecipeBuilder.Shapeless((String)IC2.MOD_ID).addInput((IItemConvertible)Blocks.BLOCK_DIAMOND).create("diamond_block_to_industrial", new ItemStack(IC2Items.industrialDiamond, 9));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"PPP", "PDP", "PPP"}).addInput('P', (IItemConvertible)Blocks.PLANKS_OAK).addInput('D', (IItemConvertible)IC2Items.industrialDiamond).create("jukebox", new ItemStack(Blocks.JUKEBOX, 1));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"SSS", "SSS", "SSS"}).addInput('S', (IItemConvertible)IC2Items.scrap).create("scrap_box", IC2Items.scrapBox.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{" C ", " A ", "R R"}).addInput('C', (IItemConvertible)IC2Items.circuit).addInput('A', (IItemConvertible)IC2Items.circuitAdvanced).addInput('R', (IItemConvertible)Items.DUST_REDSTONE).create("tfbp_empty", IC2Items.tfbpEmpty.getDefaultStack());
        IC2Recipes.tfbp(IC2Items.tfbpCultivation, Items.SEEDS_WHEAT, "tfbp_cultivation");
        IC2Recipes.tfbp(IC2Items.tfbpIrrigation, IC2Recipes.waterBucket(), "tfbp_irrigation");
        IC2Recipes.tfbp(IC2Items.tfbpDesertification, Blocks.SAND, "tfbp_desertification");
        IC2Recipes.tfbp(IC2Items.tfbpChilling, Items.AMMO_SNOWBALL, "tfbp_chilling");
        IC2Recipes.tfbp(IC2Items.tfbpFlatification, Blocks.DIRT, "tfbp_flatification");
        RecipeBuilder.Shapeless((String)IC2.MOD_ID).addInput((IItemConvertible)IC2Items.tfbpCultivation).create("tfbp_un_cultivation", IC2Items.tfbpEmpty.getDefaultStack());
        RecipeBuilder.Shapeless((String)IC2.MOD_ID).addInput((IItemConvertible)IC2Items.tfbpIrrigation).create("tfbp_un_irrigation", IC2Items.tfbpEmpty.getDefaultStack());
        RecipeBuilder.Shapeless((String)IC2.MOD_ID).addInput((IItemConvertible)IC2Items.tfbpDesertification).create("tfbp_un_desertification", IC2Items.tfbpEmpty.getDefaultStack());
        RecipeBuilder.Shapeless((String)IC2.MOD_ID).addInput((IItemConvertible)IC2Items.tfbpChilling).create("tfbp_un_chilling", IC2Items.tfbpEmpty.getDefaultStack());
        RecipeBuilder.Shapeless((String)IC2.MOD_ID).addInput((IItemConvertible)IC2Items.tfbpFlatification).create("tfbp_un_flatification", IC2Items.tfbpEmpty.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"TLT", "cDc", "cMc"}).addInput('T', (IItemConvertible)IC2Items.tfbpEmpty).addInput('L', (IItemConvertible)Blocks.GRASS).addInput('c', (IItemConvertible)IC2Items.cableItems[0]).addInput('D', (IItemConvertible)IC2Items.industrialDiamond).addInput('M', IC2Blocks.advancedMachineBlock).create("terraformer", IC2Blocks.terraformer.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"TLT", "cDc", "cMc"}).addInput('T', (IItemConvertible)IC2Items.tfbpEmpty).addInput('L', (IItemConvertible)Blocks.GRASS).addInput('c', (IItemConvertible)IC2Items.cableItems[0]).addInput('D', (IItemConvertible)Items.DIAMOND).addInput('M', IC2Blocks.advancedMachineBlock).create("terraformer_alt", IC2Blocks.terraformer.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"B B", "BsB", " B "}).addInput('B', (IItemConvertible)IC2Items.ingotBronze).addInput('s', (IItemConvertible)Items.STICK).create("bronze_ladder", new ItemStack(Blocks.LADDER_OAK, 8));
    }

    private static void plantball(String id, Object ingredient) {
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"PPP", "P P", "PPP"}).addInput('P', IC2Recipes.stackOf(ingredient)).create(id, IC2Items.plantball.getDefaultStack());
    }

    private static void tfbp(Item result, Object ingredient, String id) {
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{" # ", "#T#", " # "}).addInput('#', IC2Recipes.stackOf(ingredient)).addInput('T', (IItemConvertible)IC2Items.tfbpEmpty).create(id, result.getDefaultStack());
    }

    private static void metalBlock(String name, Item ingot, Block block) {
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"MMM", "MMM", "MMM"}).addInput('M', (IItemConvertible)ingot).create(name + "_block", block.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"B"}).addInput('B', (IItemConvertible)block).create(name + "_ingots", new ItemStack(ingot, 9));
    }

    private static void registerCables() {
        ItemCablePlaceable copper = IC2Items.cableItems[0];
        ItemCablePlaceable copperO = IC2Items.cableItems[1];
        ItemCablePlaceable gold = IC2Items.cableItems[2];
        ItemCablePlaceable goldI = IC2Items.cableItems[3];
        ItemCablePlaceable goldII = IC2Items.cableItems[4];
        ItemCablePlaceable hv = IC2Items.cableItems[5];
        ItemCablePlaceable hvI = IC2Items.cableItems[6];
        ItemCablePlaceable hvII = IC2Items.cableItems[7];
        ItemCablePlaceable hvIIII = IC2Items.cableItems[8];
        ItemCablePlaceable glass = IC2Items.cableItems[9];
        ItemCablePlaceable tin = IC2Items.cableItems[10];
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"RRR", "CCC", "RRR"}).addInput('C', (IItemConvertible)IC2Items.ingotCopper).addInput('R', (IItemConvertible)IC2Items.rubber).create("cable_copper", new ItemStack((Item)copper, 6));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"RCR", "RCR", "RCR"}).addInput('C', (IItemConvertible)IC2Items.ingotCopper).addInput('R', (IItemConvertible)IC2Items.rubber).create("cable_copper_alt", new ItemStack((Item)copper, 6));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"CCC"}).addInput('C', (IItemConvertible)IC2Items.ingotCopper).create("cable_copper_uninsulated", new ItemStack((Item)copperO, 6));
        RecipeBuilder.Shapeless((String)IC2.MOD_ID).addInput((IItemConvertible)IC2Items.rubber).addInput((IItemConvertible)copperO).create("cable_copper_insulate", copper.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"GGG"}).addInput('G', (IItemConvertible)Items.INGOT_GOLD).create("cable_gold", new ItemStack((Item)gold, 12));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{" R ", "RGR", " R "}).addInput('G', (IItemConvertible)Items.INGOT_GOLD).addInput('R', (IItemConvertible)IC2Items.rubber).create("cable_gold_insulated", new ItemStack((Item)goldI, 4));
        RecipeBuilder.Shapeless((String)IC2.MOD_ID).addInput((IItemConvertible)IC2Items.rubber).addInput((IItemConvertible)gold).create("cable_gold_insulate", goldI.getDefaultStack());
        RecipeBuilder.Shapeless((String)IC2.MOD_ID).addInput((IItemConvertible)IC2Items.rubber).addInput((IItemConvertible)goldI).create("cable_gold_2x", goldII.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"III"}).addInput('I', (IItemConvertible)IC2Items.ingotRefinedIron).create("cable_hv", new ItemStack((Item)hv, 12));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{" R ", "RIR", " R "}).addInput('I', (IItemConvertible)IC2Items.ingotRefinedIron).addInput('R', (IItemConvertible)IC2Items.rubber).create("cable_hv_insulated", new ItemStack((Item)hvI, 4));
        RecipeBuilder.Shapeless((String)IC2.MOD_ID).addInput((IItemConvertible)IC2Items.rubber).addInput((IItemConvertible)hv).create("cable_hv_insulate", hvI.getDefaultStack());
        RecipeBuilder.Shapeless((String)IC2.MOD_ID).addInput((IItemConvertible)IC2Items.rubber).addInput((IItemConvertible)hvI).create("cable_hv_2x", hvII.getDefaultStack());
        RecipeBuilder.Shapeless((String)IC2.MOD_ID).addInput((IItemConvertible)IC2Items.rubber).addInput((IItemConvertible)hvII).create("cable_hv_4x", hvIIII.getDefaultStack());
        RecipeBuilder.Shapeless((String)IC2.MOD_ID).addInput((IItemConvertible)IC2Items.rubber).addInput((IItemConvertible)IC2Items.rubber).addInput((IItemConvertible)hvI).create("cable_hv_2x_alt", hvII.getDefaultStack());
        RecipeBuilder.Shapeless((String)IC2.MOD_ID).addInput((IItemConvertible)IC2Items.rubber).addInput((IItemConvertible)IC2Items.rubber).addInput((IItemConvertible)hv).create("cable_hv_2x_alt2", hvII.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"GGG", "RDR", "GGG"}).addInput('G', (IItemConvertible)Blocks.GLASS).addInput('R', (IItemConvertible)Items.DUST_REDSTONE).addInput('D', (IItemConvertible)Items.DIAMOND).create("cable_glass_fibre", new ItemStack((Item)glass, 16));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"GGG", "RDR", "GGG"}).addInput('G', (IItemConvertible)Blocks.GLASS).addInput('R', (IItemConvertible)Items.DUST_REDSTONE).addInput('D', (IItemConvertible)IC2Items.industrialDiamond).create("cable_glass_fibre_alt", new ItemStack((Item)glass, 16));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"TTT"}).addInput('T', (IItemConvertible)IC2Items.ingotTin).create("cable_tin", new ItemStack((Item)tin, 9));
    }

    private static void registerComponents() {
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"CCC", "RIR", "CCC"}).addInput('I', (IItemConvertible)IC2Items.ingotRefinedIron).addInput('R', (IItemConvertible)Items.DUST_REDSTONE).addInput('C', (IItemConvertible)IC2Items.cableItems[0]).create("circuit", IC2Items.circuit.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"CRC", "CIC", "CRC"}).addInput('I', (IItemConvertible)IC2Items.ingotRefinedIron).addInput('R', (IItemConvertible)Items.DUST_REDSTONE).addInput('C', (IItemConvertible)IC2Items.cableItems[0]).create("circuit_alt", IC2Items.circuit.getDefaultStack());
        ItemStack lapis = new ItemStack(Items.DYE, 1, 4);
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"RGR", "LCL", "RGR"}).addInput('R', (IItemConvertible)Items.DUST_REDSTONE).addInput('G', (IItemConvertible)Items.DUST_GLOWSTONE).addInput('L', lapis).addInput('C', (IItemConvertible)IC2Items.circuit).create("circuit_advanced", IC2Items.circuitAdvanced.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"RLR", "GCG", "RLR"}).addInput('R', (IItemConvertible)Items.DUST_REDSTONE).addInput('G', (IItemConvertible)Items.DUST_GLOWSTONE).addInput('L', lapis).addInput('C', (IItemConvertible)IC2Items.circuit).create("circuit_advanced_alt", IC2Items.circuitAdvanced.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{" C ", "TRT", "TRT"}).addInput('T', (IItemConvertible)IC2Items.ingotTin).addInput('R', (IItemConvertible)Items.DUST_REDSTONE).addInput('C', (IItemConvertible)IC2Items.cableItems[0]).create("re_battery", new ItemStack((Item)IC2Items.batteryRE, 1, 10001));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"c", "C", "R"}).addInput('c', (IItemConvertible)IC2Items.cableItems[0]).addInput('C', (IItemConvertible)IC2Items.hydratedCoalDust).addInput('R', (IItemConvertible)Items.DUST_REDSTONE).create("single_use_battery", new ItemStack(IC2Items.singleUseBattery, 8));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"c", "R", "C"}).addInput('c', (IItemConvertible)IC2Items.cableItems[0]).addInput('R', (IItemConvertible)Items.DUST_REDSTONE).addInput('C', (IItemConvertible)IC2Items.hydratedCoalDust).create("single_use_battery_alt", new ItemStack(IC2Items.singleUseBattery, 8));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"RRR", "RDR", "RRR"}).addInput('R', (IItemConvertible)Items.DUST_REDSTONE).addInput('D', (IItemConvertible)Items.DIAMOND).create("energy_crystal", new ItemStack((Item)IC2Items.batteryCrystal, 1, 10001));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"RRR", "RDR", "RRR"}).addInput('R', (IItemConvertible)Items.DUST_REDSTONE).addInput('D', (IItemConvertible)IC2Items.industrialDiamond).create("energy_crystal_alt", new ItemStack((Item)IC2Items.batteryCrystal, 1, 10001));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"LCL", "LDL", "LCL"}).addInput('L', lapis).addInput('C', (IItemConvertible)IC2Items.circuit).addInput('D', (IItemConvertible)IC2Items.batteryCrystal).create("lapotron_crystal", new ItemStack((Item)IC2Items.batteryLamaCrystal, 1, 10001));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"SSS", "SAS", "SSS"}).addInput('S', (IItemConvertible)Blocks.STONE).addInput('A', (IItemConvertible)IC2Items.advancedAlloy).create("reinforced_stone", new ItemStack(IC2Blocks.reinforcedStone, 8));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"GAG", "GGG", "GAG"}).addInput('G', (IItemConvertible)Blocks.GLASS).addInput('A', (IItemConvertible)IC2Items.advancedAlloy).create("reinforced_glass", new ItemStack(IC2Blocks.reinforcedGlass, 7));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"GGG", "AGA", "GGG"}).addInput('G', (IItemConvertible)Blocks.GLASS).addInput('A', (IItemConvertible)IC2Items.advancedAlloy).create("reinforced_glass_alt", new ItemStack(IC2Blocks.reinforcedGlass, 7));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"SS", "SS", "SS"}).addInput('S', IC2Blocks.reinforcedStone).create("reinforced_door", IC2Items.reinforcedDoorItem.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{" C ", "CAC", " C "}).addInput('C', (IItemConvertible)IC2Items.ingotCopper).addInput('A', (IItemConvertible)IC2Items.advancedAlloy).create("reactor_plating", IC2Items.reactorPlating.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"ici", "CPC"}).addInput('C', (IItemConvertible)IC2Items.cellCoolant).addInput('c', (IItemConvertible)IC2Items.circuitAdvanced).addInput('i', (IItemConvertible)IC2Items.ingotCopper).addInput('P', (IItemConvertible)IC2Items.reactorPlating).create("heat_disperser", IC2Items.heatDisperser.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"ACA", "PMP", "APA"}).addInput('A', (IItemConvertible)IC2Items.advancedAlloy).addInput('C', (IItemConvertible)IC2Items.heatDisperser).addInput('P', (IItemConvertible)IC2Items.reactorPlating).addInput('M', IC2Blocks.machineBlock).create("reactor_chamber", IC2Blocks.reactorChamber.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"CC", "CC"}).addInput('C', (IItemConvertible)IC2Items.dustCoal).create("carbon_fibre", IC2Items.rawCarbonFibre.getDefaultStack());
        RecipeBuilder.Shapeless((String)IC2.MOD_ID).addInput((IItemConvertible)IC2Items.rawCarbonFibre).addInput((IItemConvertible)IC2Items.rawCarbonFibre).create("carbon_mesh", IC2Items.rawCarbonMesh.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"II", "II"}).addInput('I', (IItemConvertible)IC2Items.ingotRefinedIron).create("industrial_credit", new ItemStack(IC2Items.industrialCredit, 8));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"III", "III"}).addInput('I', (IItemConvertible)Items.INGOT_IRON).create("iron_fence", new ItemStack(IC2Blocks.ironFence, 12));
    }

    private static void registerMachines() {
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"III", "I I", "III"}).addInput('I', (IItemConvertible)Items.INGOT_IRON).create("machine_block", IC2Blocks.machineBlock.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{" I ", "I I", "IFI"}).addInput('I', (IItemConvertible)Items.INGOT_IRON).addInput('F', (IItemConvertible)Blocks.FURNACE_STONE_IDLE).create("iron_furnace", IC2Blocks.ironFurnace.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"M"}).addInput('M', IC2Blocks.machineBlock).create("machine_to_iron", new ItemStack(IC2Items.ingotRefinedIron, 8));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{" C ", "RFR"}).addInput('C', (IItemConvertible)IC2Items.circuit).addInput('R', (IItemConvertible)Items.DUST_REDSTONE).addInput('F', IC2Blocks.ironFurnace).create("electric_furnace", IC2Blocks.electricFurnace.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"FFF", "SMS", " C "}).addInput('F', (IItemConvertible)Items.FLINT).addInput('S', (IItemConvertible)Blocks.COBBLE_STONE).addInput('M', IC2Blocks.machineBlock).addInput('C', (IItemConvertible)IC2Items.circuit).create("macerator", IC2Blocks.macerator.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"TMT", "TCT"}).addInput('T', (IItemConvertible)IC2Items.treetap).addInput('M', IC2Blocks.machineBlock).addInput('C', (IItemConvertible)IC2Items.circuit).create("extractor", IC2Blocks.extractor.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"S S", "SMS", "SCS"}).addInput('S', (IItemConvertible)Blocks.STONE).addInput('M', IC2Blocks.machineBlock).addInput('C', (IItemConvertible)IC2Items.circuit).create("compressor", IC2Blocks.compressor.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"TCT", "TMT", "TTT"}).addInput('T', (IItemConvertible)IC2Items.ingotTin).addInput('M', IC2Blocks.machineBlock).addInput('C', (IItemConvertible)IC2Items.circuit).create("canning_machine", IC2Blocks.canner.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{" G ", "DMD", "IDI"}).addInput('G', (IItemConvertible)Items.DUST_GLOWSTONE).addInput('D', (IItemConvertible)Blocks.DIRT).addInput('M', IC2Blocks.compressor).addInput('I', (IItemConvertible)IC2Items.ingotRefinedIron).create("recycler", IC2Blocks.recycler.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"c c", "cCc", "EME"}).addInput('E', (IItemConvertible)IC2Items.cellEmpty).addInput('c', (IItemConvertible)IC2Items.cableItems[0]).addInput('C', (IItemConvertible)IC2Items.circuit).addInput('M', IC2Blocks.machineBlock).create("electrolyzer", IC2Blocks.electrolyzer.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"CCC", "CFC", "CMC"}).addInput('C', (IItemConvertible)IC2Items.ingotCopper).addInput('F', IC2Blocks.electricFurnace).addInput('M', IC2Blocks.advancedMachineBlock).create("induction_furnace", IC2Blocks.inductionFurnace.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"RFR", "RMR", "RFR"}).addInput('R', (IItemConvertible)Items.DUST_REDSTONE).addInput('F', IC2Blocks.ironFence).addInput('M', IC2Blocks.machineBlock).create("magnetizer", IC2Blocks.magnetizer.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"I I", "I I", "ITI"}).addInput('I', (IItemConvertible)IC2Items.ingotRefinedIron).addInput('T', (IItemConvertible)IC2Items.treetap).create("mining_pipe", new ItemStack(IC2Blocks.miningPipe, 8));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"CMC", " P ", " P "}).addInput('P', IC2Blocks.miningPipe).addInput('M', IC2Blocks.machineBlock).addInput('C', (IItemConvertible)IC2Items.circuit).create("miner", IC2Blocks.miner.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"cCc", "cMc", "PTP"}).addInput('c', (IItemConvertible)IC2Items.cellEmpty).addInput('C', (IItemConvertible)IC2Items.circuit).addInput('T', (IItemConvertible)IC2Items.treetap).addInput('P', IC2Blocks.miningPipe).addInput('M', IC2Blocks.machineBlock).create("pump", IC2Blocks.pump.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"c", "M", "C"}).addInput('c', (IItemConvertible)IC2Items.circuit).addInput('M', IC2Blocks.machineBlock).addInput('C', (IItemConvertible)Blocks.CHEST_PLANKS_OAK).create("personal_safe", IC2Blocks.personalSafe.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"RRR", "CMC"}).addInput('R', (IItemConvertible)Items.DUST_REDSTONE).addInput('C', (IItemConvertible)Blocks.CHEST_PLANKS_OAK).addInput('M', IC2Blocks.machineBlock).create("trade_o_mat", IC2Blocks.tradeOMat.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{" A ", "CMC", " A "}).addInput('A', (IItemConvertible)IC2Items.advancedAlloy).addInput('C', (IItemConvertible)IC2Items.carbonPlate).addInput('M', IC2Blocks.machineBlock).create("advanced_machine", IC2Blocks.advancedMachineBlock.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{" C ", "AMA", " C "}).addInput('A', (IItemConvertible)IC2Items.advancedAlloy).addInput('C', (IItemConvertible)IC2Items.carbonPlate).addInput('M', IC2Blocks.machineBlock).create("advanced_machine_alt", IC2Blocks.advancedMachineBlock.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"GCG", "ALA", "GCG"}).addInput('A', IC2Blocks.advancedMachineBlock).addInput('L', (IItemConvertible)IC2Items.batteryLamaCrystal).addInput('G', (IItemConvertible)Items.DUST_GLOWSTONE).addInput('C', (IItemConvertible)IC2Items.circuitAdvanced).create("mass_fabricator", IC2Blocks.massFabricator.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"FFF", "TTT", "FFF"}).addInput('F', (IItemConvertible)Items.FLINT).addInput('T', (IItemConvertible)Blocks.TNT).create("industrial_tnt", new ItemStack(IC2Blocks.industrialTnt, 4));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"FTF", "FTF", "FTF"}).addInput('F', (IItemConvertible)Items.FLINT).addInput('T', (IItemConvertible)Blocks.TNT).create("industrial_tnt_alt", new ItemStack(IC2Blocks.industrialTnt, 4));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"S", "T"}).addInput('S', (IItemConvertible)Items.STRING).addInput('T', IC2Blocks.industrialTnt).create("dynamite", new ItemStack(IC2Items.dynamiteItem, 8));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"DDD", "DRD", "DDD"}).addInput('D', (IItemConvertible)IC2Items.dynamiteItem).addInput('R', (IItemConvertible)IC2Items.stickyResin).create("sticky_dynamite", new ItemStack(IC2Items.stickyDynamite, 8));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"UUU", "UTU", "UUU"}).addInput('U', (IItemConvertible)IC2Items.uraniumItem).addInput('T', IC2Blocks.industrialTnt).create("nuke", IC2Blocks.nuke.getDefaultStack());
    }

    private static void registerBlastFurnace() {
        String[][] oreSets;
        for (String[] set : oreSets = new String[][]{{"copper", "copper"}, {"tin", "tin"}, {"uranium", "uranium"}}) {
            Block[] blockArray;
            String metal;
            ItemStack out = switch (metal = set[0]) {
                case "copper" -> IC2Items.ingotCopper.getDefaultStack();
                case "tin" -> IC2Items.ingotTin.getDefaultStack();
                default -> IC2Items.uraniumItem.getDefaultStack();
            };
            switch (metal) {
                case "copper": {
                    Block[] blockArray2 = new Block[5];
                    blockArray2[0] = IC2Blocks.oreCopper;
                    blockArray2[1] = IC2Blocks.oreCopperBasalt;
                    blockArray2[2] = IC2Blocks.oreCopperLimestone;
                    blockArray2[3] = IC2Blocks.oreCopperGranite;
                    blockArray = blockArray2;
                    blockArray2[4] = IC2Blocks.oreCopperPermafrost;
                    break;
                }
                case "tin": {
                    Block[] blockArray3 = new Block[5];
                    blockArray3[0] = IC2Blocks.oreTin;
                    blockArray3[1] = IC2Blocks.oreTinBasalt;
                    blockArray3[2] = IC2Blocks.oreTinLimestone;
                    blockArray3[3] = IC2Blocks.oreTinGranite;
                    blockArray = blockArray3;
                    blockArray3[4] = IC2Blocks.oreTinPermafrost;
                    break;
                }
                default: {
                    Block[] blockArray4 = new Block[5];
                    blockArray4[0] = IC2Blocks.oreUranium;
                    blockArray4[1] = IC2Blocks.oreUraniumBasalt;
                    blockArray4[2] = IC2Blocks.oreUraniumLimestone;
                    blockArray4[3] = IC2Blocks.oreUraniumGranite;
                    blockArray = blockArray4;
                    blockArray4[4] = IC2Blocks.oreUraniumPermafrost;
                }
            }
            Block[] ores = blockArray;
            String[] variants = new String[]{"", "_basalt", "_limestone", "_granite", "_permafrost"};
            for (int v = 0; v < ores.length; ++v) {
                IC2Recipes.shared(metal + "_ore" + variants[v], ores[v], out);
            }
        }
        IC2Recipes.blastFurnace("iron_dust", IC2Items.dustIron, Items.INGOT_IRON.getDefaultStack());
        IC2Recipes.blastFurnace("gold_dust", IC2Items.dustGold, Items.INGOT_GOLD.getDefaultStack());
        IC2Recipes.blastFurnace("copper_dust", IC2Items.dustCopper, IC2Items.ingotCopper.getDefaultStack());
        IC2Recipes.blastFurnace("tin_dust", IC2Items.dustTin, IC2Items.ingotTin.getDefaultStack());
        IC2Recipes.blastFurnace("bronze_dust", IC2Items.dustBronze, IC2Items.ingotBronze.getDefaultStack());
        IC2Recipes.blastFurnace("steel_dust", IC2Items.dustSteel, Items.INGOT_STEEL.getDefaultStack());
    }

    private static void blastFurnace(String id, Object input, ItemStack output) {
        RecipeBuilder.BlastFurnace((String)IC2.MOD_ID).setInput(IC2Recipes.stackOf(input)).create(id, output);
    }

    private static void registerGenerators() {
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"B", "M", "F"}).addInput('B', (IItemConvertible)IC2Items.batteryRE).addInput('M', IC2Blocks.machineBlock).addInput('F', (IItemConvertible)Blocks.FURNACE_STONE_IDLE).create("generator", IC2Blocks.generator.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"B", "I", "F"}).addInput('B', (IItemConvertible)IC2Items.batteryRE).addInput('I', (IItemConvertible)IC2Items.ingotRefinedIron).addInput('F', IC2Blocks.ironFurnace).create("generator_alt", IC2Blocks.generator.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"gCg", "gCg", "IGI"}).addInput('G', IC2Blocks.generator).addInput('C', (IItemConvertible)IC2Items.cellEmpty).addInput('g', (IItemConvertible)Blocks.GLASS).addInput('I', (IItemConvertible)IC2Items.ingotRefinedIron).create("geothermal_generator", IC2Blocks.geothermalGenerator.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"SPS", "PGP", "SPS"}).addInput('S', (IItemConvertible)Items.STICK).addInput('P', (IItemConvertible)Blocks.PLANKS_OAK).addInput('G', IC2Blocks.generator).create("water_mill", IC2Blocks.waterMill.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"CgC", "gCg", "cGc"}).addInput('G', IC2Blocks.generator).addInput('C', (IItemConvertible)IC2Items.dustCoal).addInput('g', (IItemConvertible)Blocks.GLASS).addInput('c', (IItemConvertible)IC2Items.cableItems[0]).create("solar_panel", IC2Blocks.solarPanel.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"I I", " G ", "I I"}).addInput('I', (IItemConvertible)Items.INGOT_IRON).addInput('G', IC2Blocks.generator).create("wind_mill", IC2Blocks.windMill.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"AcA", "CGC", "AcA"}).addInput('A', (IItemConvertible)IC2Items.advancedAlloy).addInput('C', IC2Blocks.reactorChamber).addInput('c', (IItemConvertible)IC2Items.circuitAdvanced).addInput('G', IC2Blocks.generator).create("nuclear_reactor", IC2Blocks.nuclearReactor.getDefaultStack());
    }

    private static void registerEnergy() {
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"PCP", "BBB", "PPP"}).addInput('P', (IItemConvertible)Blocks.PLANKS_OAK).addInput('C', (IItemConvertible)IC2Items.cableItems[0]).addInput('B', (IItemConvertible)IC2Items.batteryRE).create("batbox", IC2Blocks.batBox.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"cCc", "CMC", "cCc"}).addInput('c', (IItemConvertible)IC2Items.cableItems[0]).addInput('C', (IItemConvertible)IC2Items.batteryCrystal).addInput('M', IC2Blocks.machineBlock).create("mfe", IC2Blocks.mfe.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"LCL", "LML", "LAL"}).addInput('L', (IItemConvertible)IC2Items.batteryLamaCrystal).addInput('C', (IItemConvertible)IC2Items.circuitAdvanced).addInput('M', IC2Blocks.mfe).addInput('A', IC2Blocks.advancedMachineBlock).create("mfsu", IC2Blocks.mfsu.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"PCP", "ccc", "PCP"}).addInput('P', (IItemConvertible)Blocks.PLANKS_OAK).addInput('C', (IItemConvertible)IC2Items.cableItems[0]).addInput('c', (IItemConvertible)IC2Items.ingotCopper).create("transformer_lv", IC2Blocks.transformerLV.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{" C ", " M ", " C "}).addInput('C', (IItemConvertible)IC2Items.cableItems[0]).addInput('M', IC2Blocks.machineBlock).create("transformer_mv", IC2Blocks.transformerMV.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{" c ", "CED", " c "}).addInput('E', IC2Blocks.transformerMV).addInput('D', (IItemConvertible)IC2Items.batteryCrystal).addInput('c', (IItemConvertible)IC2Items.cableItems[0]).addInput('C', (IItemConvertible)IC2Items.circuit).create("transformer_hv", IC2Blocks.transformerHV.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{" C ", "CGC", " E "}).addInput('C', (IItemConvertible)IC2Items.circuitAdvanced).addInput('G', IC2Blocks.generator).addInput('E', (IItemConvertible)IC2Items.batteryLamaCrystal).create("tesla_coil", IC2Blocks.teslaCoil.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"GDG", "DMD", "GDG"}).addInput('G', (IItemConvertible)Items.DIAMOND).addInput('D', IC2Blocks.transformerHV).addInput('M', IC2Blocks.advancedMachineBlock).create("teleporter", IC2Blocks.teleporter.getDefaultStack());
    }

    private static void registerTools() {
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{" I ", "ICI", "IBI"}).addInput('I', (IItemConvertible)IC2Items.ingotRefinedIron).addInput('C', (IItemConvertible)IC2Items.circuit).addInput('B', (IItemConvertible)IC2Items.batteryRE).create("mining_drill", IC2Items.miningDrill.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{" D ", "DdD"}).addInput('D', (IItemConvertible)Items.DIAMOND).addInput('d', (IItemConvertible)IC2Items.miningDrill).create("diamond_drill", IC2Items.diamondDrill.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{" II", "ICI", "BI "}).addInput('I', (IItemConvertible)IC2Items.ingotRefinedIron).addInput('C', (IItemConvertible)IC2Items.circuit).addInput('B', (IItemConvertible)IC2Items.batteryRE).create("chainsaw", IC2Items.chainsaw.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"B B", "BBB", " B "}).addInput('B', (IItemConvertible)IC2Items.ingotBronze).create("wrench", IC2Items.wrench.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"A A", " A ", "I I"}).addInput('A', (IItemConvertible)IC2Items.ingotRefinedIron).addInput('I', (IItemConvertible)Items.INGOT_IRON).create("insulation_cutter", IC2Items.insulationCutter.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{" G ", "CBC", "ccc"}).addInput('G', (IItemConvertible)Items.DUST_GLOWSTONE).addInput('B', (IItemConvertible)IC2Items.batteryRE).addInput('C', (IItemConvertible)IC2Items.circuit).addInput('c', (IItemConvertible)IC2Items.cableItems[0]).create("eu_reader", IC2Items.euReader.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{" G ", "CBC", "ccc"}).addInput('G', (IItemConvertible)Items.DUST_GLOWSTONE).addInput('B', (IItemConvertible)IC2Items.batteryRE).addInput('C', (IItemConvertible)IC2Items.circuit).addInput('c', (IItemConvertible)IC2Items.cableItems[0]).create("od_scanner", IC2Items.odScanner.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{" G ", "GCG", "cSc"}).addInput('G', (IItemConvertible)Items.DUST_GLOWSTONE).addInput('C', (IItemConvertible)IC2Items.circuitAdvanced).addInput('S', (IItemConvertible)IC2Items.odScanner).addInput('c', (IItemConvertible)IC2Items.cableItems[0]).create("ov_scanner", IC2Items.ovScanner.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{" CC", " IC", "I  "}).addInput('C', (IItemConvertible)Blocks.WOOL).addInput('I', (IItemConvertible)Items.INGOT_IRON).create("painter", IC2Items.painter.getDefaultStack());
        String[] colors = new String[]{"black", "red", "green", "brown", "blue", "purple", "cyan", "light_gray", "gray", "pink", "lime", "yellow", "light_blue", "magenta", "orange", "white"};
        for (int i = 0; i < 16; ++i) {
            RecipeBuilder.Shapeless((String)IC2.MOD_ID).addInput((IItemConvertible)IC2Items.painter).addInput(new ItemStack(Items.DYE, 1, i)).create("painter_" + colors[i], IC2Items.painters[i].getDefaultStack());
        }
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"Rcc", "AAC", " AA"}).addInput('R', (IItemConvertible)Items.DUST_REDSTONE).addInput('c', (IItemConvertible)IC2Items.batteryCrystal).addInput('A', (IItemConvertible)IC2Items.advancedAlloy).addInput('C', (IItemConvertible)IC2Items.circuitAdvanced).create("mining_laser", IC2Items.toolMiningLaser.getDefaultStack());
    }

    private static void registerArmor() {
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"BBB", "B B"}).addInput('B', (IItemConvertible)IC2Items.ingotBronze).create("bronze_helmet", IC2Items.bronzeHelmet.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"B B", "BBB", "BBB"}).addInput('B', (IItemConvertible)IC2Items.ingotBronze).create("bronze_chestplate", IC2Items.bronzeChestplate.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"BBB", "B B", "B B"}).addInput('B', (IItemConvertible)IC2Items.ingotBronze).create("bronze_leggings", IC2Items.bronzeLeggings.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"B B", "B B"}).addInput('B', (IItemConvertible)IC2Items.ingotBronze).create("bronze_boots", IC2Items.bronzeBoots.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{" XX", "XXX", "X X"}).addInput('X', (IItemConvertible)IC2Items.ingotBronze).create("bronze_wolf_armor", IC2Items.bronzeWolfArmor.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"R R", "R R", "RCR"}).addInput('R', (IItemConvertible)IC2Items.rubber).addInput('C', (IItemConvertible)Blocks.WOOL).create("rubber_boots", IC2Items.rubberBoots.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"A A", "ALA", "AIA"}).addInput('A', (IItemConvertible)IC2Items.advancedAlloy).addInput('L', IC2Recipes.lavaBucket()).addInput('I', (IItemConvertible)Items.TOOL_COMPASS).create("composite_vest", IC2Items.compositeVest.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"ICI", "IFI", "R R"}).addInput('I', (IItemConvertible)IC2Items.ingotRefinedIron).addInput('C', (IItemConvertible)IC2Items.circuit).addInput('F', (IItemConvertible)IC2Items.fuelCanEmpty).addInput('R', (IItemConvertible)Items.DUST_REDSTONE).create("jetpack", IC2Items.jetpack.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"ICI", "IBI", "G G"}).addInput('I', (IItemConvertible)IC2Items.ingotRefinedIron).addInput('C', (IItemConvertible)IC2Items.circuitAdvanced).addInput('B', IC2Blocks.batBox).addInput('G', (IItemConvertible)Items.DUST_GLOWSTONE).create("electric_jetpack", IC2Items.electricJetpack.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"BCB", "BTB", "B B"}).addInput('B', (IItemConvertible)IC2Items.batteryRE).addInput('C', (IItemConvertible)IC2Items.circuit).addInput('T', (IItemConvertible)IC2Items.ingotTin).create("batpack", IC2Items.batpack.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"CcC", "CGC"}).addInput('C', (IItemConvertible)IC2Items.carbonPlate).addInput('c', (IItemConvertible)IC2Items.batteryCrystal).addInput('G', (IItemConvertible)Blocks.GLASS).create("nano_helmet", IC2Items.nanoHelmet.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"C C", "CcC", "CCC"}).addInput('C', (IItemConvertible)IC2Items.carbonPlate).addInput('c', (IItemConvertible)IC2Items.batteryCrystal).create("nano_chestplate", IC2Items.nanoBodyarmor.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"CcC", "C C", "C C"}).addInput('C', (IItemConvertible)IC2Items.carbonPlate).addInput('c', (IItemConvertible)IC2Items.batteryCrystal).create("nano_leggings", IC2Items.nanoLeggings.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"C C", "CcC"}).addInput('C', (IItemConvertible)IC2Items.carbonPlate).addInput('c', (IItemConvertible)IC2Items.batteryCrystal).create("nano_boots", IC2Items.nanoBoots.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"GA ", "GA ", "CcC"}).addInput('G', (IItemConvertible)Items.DUST_GLOWSTONE).addInput('A', (IItemConvertible)IC2Items.advancedAlloy).addInput('C', (IItemConvertible)IC2Items.carbonPlate).addInput('c', (IItemConvertible)IC2Items.batteryCrystal).create("nano_saber", IC2Items.nanoSaberOff.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"ILI", "CGC"}).addInput('I', (IItemConvertible)IC2Items.iridiumPlate).addInput('L', (IItemConvertible)IC2Items.batteryLamaCrystal).addInput('C', (IItemConvertible)IC2Items.circuitAdvanced).addInput('G', IC2Blocks.reinforcedGlass).create("quantum_helmet", IC2Items.quantumHelmet.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"A A", "ILI", "IAI"}).addInput('A', (IItemConvertible)IC2Items.advancedAlloy).addInput('I', (IItemConvertible)IC2Items.iridiumPlate).addInput('L', (IItemConvertible)IC2Items.batteryLamaCrystal).create("quantum_chestplate", IC2Items.quantumBodyarmor.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"MLM", "I I", "G G"}).addInput('M', IC2Blocks.machineBlock).addInput('I', (IItemConvertible)IC2Items.iridiumPlate).addInput('L', (IItemConvertible)IC2Items.batteryLamaCrystal).addInput('G', (IItemConvertible)Items.DUST_GLOWSTONE).create("quantum_leggings", IC2Items.quantumLeggings.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"I I", "RLR"}).addInput('I', (IItemConvertible)IC2Items.iridiumPlate).addInput('L', (IItemConvertible)IC2Items.batteryLamaCrystal).addInput('R', (IItemConvertible)IC2Items.rubberBoots).create("quantum_boots", IC2Items.quantumBoots.getDefaultStack());
    }

    private static void registerMisc() {
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"BBB", " S ", " S "}).addInput('B', (IItemConvertible)IC2Items.ingotBronze).addInput('S', (IItemConvertible)Items.STICK).create("bronze_pickaxe", IC2Items.bronzePickaxe.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"BB", "SB", "S "}).addInput('B', (IItemConvertible)IC2Items.ingotBronze).addInput('S', (IItemConvertible)Items.STICK).create("bronze_axe", IC2Items.bronzeAxe.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"BB", "S ", "S "}).addInput('B', (IItemConvertible)IC2Items.ingotBronze).addInput('S', (IItemConvertible)Items.STICK).create("bronze_hoe", IC2Items.bronzeHoe.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"B", "B", "S"}).addInput('B', (IItemConvertible)IC2Items.ingotBronze).addInput('S', (IItemConvertible)Items.STICK).create("bronze_sword", IC2Items.bronzeSword.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{" B ", " S ", " S "}).addInput('B', (IItemConvertible)IC2Items.ingotBronze).addInput('S', (IItemConvertible)Items.STICK).create("bronze_shovel", IC2Items.bronzeShovel.getDefaultStack());
        IC2Recipes.uu("uu_stone", new String[]{" M "}, new ItemStack(Blocks.STONE, 16));
        IC2Recipes.uu("uu_dirt", new String[]{"M  ", "M  "}, new ItemStack(Blocks.DIRT, 16));
        IC2Recipes.uu("uu_sand", new String[]{" M ", "M M"}, new ItemStack(Blocks.SAND, 16));
        IC2Recipes.uu("uu_gravel", new String[]{"  M", " M ", "M  "}, new ItemStack(Blocks.GRAVEL, 16));
        IC2Recipes.uu("uu_glass", new String[]{" M ", "M M", " M "}, new ItemStack(Blocks.GLASS, 32));
        IC2Recipes.uu("uu_obsidian", new String[]{"M M", "M M", "   "}, new ItemStack(Blocks.OBSIDIAN, 12));
        IC2Recipes.uu("uu_log", new String[]{" M ", "   ", "   "}, new ItemStack(Blocks.LOG_OAK, 16));
        IC2Recipes.uu("uu_wool", new String[]{" M ", " M ", " M "}, new ItemStack(Blocks.WOOL, 16));
        IC2Recipes.uu("uu_uranium", new String[]{"M M", "M M", "M M"}, new ItemStack(IC2Items.uraniumItem, 1));
        IC2Recipes.uu("uu_copper_ore", new String[]{"  M", "M M", "   "}, new ItemStack(IC2Blocks.oreCopper, 5));
        IC2Recipes.uu("uu_tin_ore", new String[]{"   ", "M M", "  M"}, new ItemStack(IC2Blocks.oreTin, 5));
        IC2Recipes.uu("uu_diamond", new String[]{"MMM", "MMM", "MMM"}, new ItemStack(Items.DIAMOND, 1));
        IC2Recipes.uu("uu_lapis", new String[]{" M ", " M ", " MM"}, new ItemStack(Items.DYE, 9, 4));
        IC2Recipes.uu("uu_redstone", new String[]{"  M", "M  ", "  M"}, new ItemStack(Items.DUST_REDSTONE, 2));
        IC2Recipes.uu("uu_coal", new String[]{"MMM", "M  ", "MMM"}, new ItemStack(Items.COAL, 15));
        IC2Recipes.uu("uu_iridium", new String[]{"MMM", " M ", "MMM"}, new ItemStack(IC2Items.iridiumPlate, 1));
        IC2Recipes.uu("uu_grass", new String[]{"M M", "MMM", "   "}, new ItemStack(Blocks.GRASS, 16));
        IC2Recipes.uu("uu_clay", new String[]{"   ", "MMM", "MMM"}, new ItemStack(Blocks.BLOCK_CLAY, 8));
        IC2Recipes.uu("uu_snow", new String[]{"MMM", "MMM", "   "}, new ItemStack(Blocks.BLOCK_SNOW, 16));
        IC2Recipes.uu("uu_ice", new String[]{" M ", "MMM", " M "}, new ItemStack(Blocks.ICE, 8));
        IC2Recipes.uu("uu_mossy", new String[]{"M M", " M ", "M M"}, new ItemStack(Blocks.COBBLE_STONE_MOSSY, 2));
        IC2Recipes.uu("uu_bricks", new String[]{"M M", " M ", "M M"}, new ItemStack(Blocks.BRICK_CLAY, 2));
        IC2Recipes.uu("uu_glowstone", new String[]{"M M", "   ", "M M"}, new ItemStack(Items.DUST_GLOWSTONE, 8));
        IC2Recipes.uu("uu_sugarcane", new String[]{"M", "M", "M"}, new ItemStack(Items.SUGARCANE, 16));
        IC2Recipes.uu("uu_cactus", new String[]{"M", "M", "M"}, new ItemStack(Blocks.CACTUS, 16));
        IC2Recipes.uu("uu_piston", new String[]{"M M", " M ", "M M"}, new ItemStack(Blocks.PISTON_BASE, 2));
        IC2Recipes.uu("uu_redstone_more", new String[]{"MMM", "   ", "   "}, new ItemStack(Items.DUST_REDSTONE, 24));
        IC2Recipes.uu("uu_steel_ingot", new String[]{" M ", "M M", " M "}, new ItemStack(Items.INGOT_STEEL, 2));
    }

    private static void uu(String id, String[] shape, ItemStack out) {
        
        int count = 0;
        for (String row : shape) {
            for (char c : row.toCharArray()) {
                if (c == 'M') {
                    ++count;
                }
            }
        }
        UU_RECIPES.add(new Object[]{Integer.valueOf(Math.max(1, count)), out});
        RecipeBuilderShapedHolder.build(shape, out, id);
    }

    
    public static final java.util.List<Object[]> UU_RECIPES = new java.util.ArrayList<Object[]>();

    private static void registerSteelAlternatives() {
        Item steel = Items.INGOT_STEEL;
        if (steel == null) {
            return;
        }
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"II", "IC"}).addInput('I', (IItemConvertible)IC2Items.dustIron).addInput('C', (IItemConvertible)IC2Items.dustCoal).create("steel_dust", new ItemStack(IC2Items.dustSteel, 2));
        RecipeBuilder.Shapeless((String)IC2.MOD_ID).addInput((IItemConvertible)steel).create("steel_refined_iron", IC2Items.ingotRefinedIron.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"XX", "XX"}).addInput('X', (IItemConvertible)IC2Items.ingotBronze).create("bronze_bricks", new ItemStack(IC2Blocks.bronzeBrick, 4));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"XX", "XX"}).addInput('X', (IItemConvertible)IC2Items.ingotCopper).create("copper_bricks", new ItemStack(IC2Blocks.copperBrick, 4));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"XX", "XX", "XX"}).addInput('X', (IItemConvertible)IC2Items.ingotBronze).create("bronze_door", new ItemStack(IC2Items.bronzeDoorItem, 3));
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"X X", "X X", "X X"}).addInput('X', (IItemConvertible)IC2Items.ingotBronze).create("iron_fence_bronze", new ItemStack(IC2Blocks.ironFence, 16));
        IC2Recipes.registerCoalChunkVariants();
    }

    private static void registerCoalChunkVariants() {
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"###", "#O#", "###"}).addInput('#', (IItemConvertible)IC2Items.compressedCoalBall).addInput('O', (IItemConvertible)Blocks.OBSIDIAN).create("coal_chunk_obsidian", IC2Items.coalChunk.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"###", "#O#", "###"}).addInput('#', (IItemConvertible)IC2Items.compressedCoalBall).addInput('O', (IItemConvertible)Blocks.BRICK_CLAY).create("coal_chunk_brick", IC2Items.coalChunk.getDefaultStack());
        RecipeBuilder.Shaped((String)IC2.MOD_ID).setShape(new String[]{"###", "#O#", "###"}).addInput('#', (IItemConvertible)IC2Items.compressedCoalBall).addInput('O', (IItemConvertible)Blocks.BLOCK_IRON).create("coal_chunk_iron", IC2Items.coalChunk.getDefaultStack());
    }

    private static void registerTrommelInjections() {
        try {
            RecipeBuilder.ModifyTrommel((String)"minecraft", (String)"gravel").addEntry(new WeightedRandomLootObject(new ItemStack(IC2Items.rawCopper), 1, 2), 4.0);
            RecipeBuilder.ModifyTrommel((String)"minecraft", (String)"gravel").addEntry(new WeightedRandomLootObject(new ItemStack(IC2Items.rawTin), 1, 2), 4.0);
            RecipeBuilder.ModifyTrommel((String)"minecraft", (String)"gravel").addEntry(new WeightedRandomLootObject(new ItemStack(IC2Items.uraniumItem), 1), 0.5);
            RecipeBuilder.ModifyTrommel((String)"minecraft", (String)"dirt").addEntry(new WeightedRandomLootObject(new ItemStack(IC2Items.rawCopper), 1), 0.5);
            RecipeBuilder.ModifyTrommel((String)"minecraft", (String)"dirt").addEntry(new WeightedRandomLootObject(new ItemStack(IC2Items.rawTin), 1), 0.5);
            RecipeBuilder.ModifyTrommel((String)"minecraft", (String)"rich_dirt").addEntry(new WeightedRandomLootObject(new ItemStack(IC2Items.rawCopper), 1, 2), 14.0);
            RecipeBuilder.ModifyTrommel((String)"minecraft", (String)"rich_dirt").addEntry(new WeightedRandomLootObject(new ItemStack(IC2Items.rawTin), 1, 2), 14.0);
            RecipeBuilder.ModifyTrommel((String)"minecraft", (String)"rich_dirt").addEntry(new WeightedRandomLootObject(new ItemStack(IC2Items.uraniumItem), 1), 2.0);
            RecipeBuilder.ModifyTrommel((String)"minecraft", (String)"sand").addEntry(new WeightedRandomLootObject(new ItemStack(IC2Items.rawTin), 1), 0.5);
            RecipeBuilder.ModifyTrommel((String)"minecraft", (String)"clay").addEntry(new WeightedRandomLootObject(new ItemStack(IC2Items.rawCopper), 1), 1.0);
            RecipeBuilder.ModifyTrommel((String)"minecraft", (String)"soulsand").addEntry(new WeightedRandomLootObject(new ItemStack(IC2Items.uraniumItem), 1), 0.75);
        }
        catch (Exception e) {
            IC2.LOGGER.error("Failed to inject IC2 items into BTA trommel recipes", (Throwable)e);
        }
    }

    private static ItemStack waterBucket() {
        ItemStack bucket = new ItemStack(Items.BUCKET_IRON);
        ItemBucket.setState((ItemStack)bucket, (NamespaceID)ItemBucket.STATE_WATER);
        return bucket;
    }

    private static ItemStack lavaBucket() {
        ItemStack bucket = new ItemStack(Items.BUCKET_IRON);
        ItemBucket.setState((ItemStack)bucket, (NamespaceID)ItemBucket.STATE_LAVA);
        return bucket;
    }

    private static final class RecipeBuilderShapedHolder {
        private RecipeBuilderShapedHolder() {
        }

        static void build(String[] shape, ItemStack out, String id) {
            RecipeBuilderShaped b = RecipeBuilder.Shaped((String)IC2.MOD_ID, (String[])shape).addInput('M', (IItemConvertible)IC2Items.uuMatter);
            b.create(id, out);
        }
    }
}

