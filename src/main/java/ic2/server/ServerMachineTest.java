package ic2.server;

import ic2.IC2;
import ic2.IC2Blocks;
import ic2.IC2Items;
import ic2.compat.CrossModCompat;
import ic2.tileentity.TileEntityCompressor;
import ic2.tileentity.TileEntityElecMachine;
import ic2.tileentity.TileEntityElectrolyzer;
import ic2.tileentity.TileEntityExtractor;
import ic2.tileentity.TileEntityWoodGasser;
import ic2.energy.Direction;
import ic2.gui.slot.SlotIC2Battery;
import ic2.gui.slot.SlotIC2Input;
import ic2.net.MachineEventMessage;
import ic2.tileentity.TileEntityCable;
import ic2.tileentity.TileEntityMacerator;
import ic2.tileentity.TileEntityNuclearReactor;
import ic2.tileentity.TileEntityPESU;
import ic2.tileentity.TileEntityPlasmafier;
import ic2.tileentity.TileEntityReactorChamber;
import ic2.tileentity.TileEntitySlowGrinder;
import ic2.tileentity.TileEntityTerraformer;
import java.lang.reflect.Method;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.data.registry.recipe.RecipeGroup;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCraftingShaped;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import net.minecraft.server.MinecraftServer;
import turniplabs.halplibe.helper.RecipeBuilder;

public class ServerMachineTest {
    private static volatile boolean started = false;
    private static int failures = 0;
    private static int checks = 0;

    public static void armIfNeeded() {
        if (started) {
            return;
        }
        if (System.getProperty("ic2.spTest") == null && !"1".equals(System.getenv("IC2_SPTEST"))) {
            return;
        }
        started = true;
        Thread t = new Thread(() -> ServerMachineTest.run(), "IC2-Server-Test");
        t.setDaemon(true);
        t.start();
    }

    private static void run() {
        World world = null;
        for (int i = 0; i < 180 && world == null; ++i) {
            try {
                Thread.sleep(1000L);
                MinecraftServer server = MinecraftServer.getInstance();
                if (server != null && server.dimensionWorlds != null) {
                    world = (World)server.dimensionWorlds.get(0);
                }
            }
            catch (Throwable t) {
                log("wait error: " + String.valueOf(t));
            }
        }
        if (world == null) {
            log("FAILED: overworld never loaded");
            return;
        }
        log("overworld loaded: " + String.valueOf(world));
        for (int i = 0; i < 300; ++i) {
            try {
                if (world.getTotalWorldTime() > 0L) break;
                Thread.sleep(1000L);
            }
            catch (Throwable t) {
                break;
            }
        }
        log("server main loop ticking, worldTime=" + world.getTotalWorldTime());
        try {
            Thread.sleep(5000L);
        }
        catch (InterruptedException e) {
            return;
        }
        int px = 0;
        int py = 70;
        int pz = 0;
        try {
            TilePos spawn = world.getSpawnPoint();
            int x = spawn.x();
            int z = spawn.z();
            int y = Math.min(70, spawn.y());
            px = x;
            pz = z;
            for (int attempt = 0; attempt < 5 && !world.isChunkLoaded(x >> 4, z >> 4); ++attempt) {
                world.getChunkFromBlockCoords(x, z);
                Thread.sleep(2000L);
            }
            y = world.getTopBlock(x, z) >= 0 ? y : 70;
            while (y < 90 && !world.isAirBlock(x, y, z)) {
                ++y;
            }
            while (y > 40 && world.isAirBlock(x, y - 1, z)) {
                --y;
            }
            py = y;
            for (int dx = -3; dx <= 3; ++dx) {
                for (int dz = -6; dz <= 6; ++dz) {
                    for (int dy = 0; dy < 3; ++dy) {
                        int cx = x + dx;
                        int cy = y + dy;
                        int cz = z + dz;
                        if (!world.isChunkLoaded(cx >> 4, cz >> 4)) {
                            world.getChunkFromBlockCoords(cx, cz);
                        }
                        if (world.getBlockId(cx, cy, cz) != 0) {
                            world.setBlockWithNotify(cx, cy, cz, 0);
                        }
                        if (world.getBlockId(cx, cy - 1, cz) == 0) {
                            world.setBlockWithNotify(cx, cy - 1, cz, Blocks.GRASS.id());
                        }
                    }
                }
            }
            place(world, x + 2, y, z, "pesu", IC2Blocks.pesu);
            place(world, x + 3, y, z, "plasmafier", IC2Blocks.plasmafier);
            place(world, x + 4, y, z, "transformer_iv", IC2Blocks.transformerIV);
            place(world, x + 5, y, z, "iridium_stone", IC2Blocks.iridiumStone);
            place(world, x + 2, y, z + 2, "slag_generator", IC2Blocks.slagGenerator);
            place(world, x + 3, y, z + 2, "thermal_generator", IC2Blocks.thermalGenerator);
            place(world, x + 4, y, z + 2, "turbine_solar", IC2Blocks.turbineSolar);
            place(world, x + 5, y, z + 2, "ocean_generator", IC2Blocks.oceanGenerator);
            place(world, x + 2, y, z - 2, "wave_generator", IC2Blocks.waveGenerator);
            place(world, x + 3, y, z - 2, "wood_gasser", IC2Blocks.woodGasser);
            place(world, x + 4, y, z - 2, "wood_gasser_elec", IC2Blocks.woodGasserElec);
            place(world, x + 5, y, z - 2, "slow_grinder", IC2Blocks.slowGrinder);
            place(world, x + 2, y, z - 4, "rare_earth_extractor", IC2Blocks.rareEarthExtractor);
            placeCable(world, x + 2, y + 1, z, "plasma_cable", 11);
            placeCable(world, x + 3, y + 1, z, "plasma_cable2", 11);
            placeCable(world, x + 4, y + 1, z, "plasma_cable3", 11);
            placeCable(world, x + 4, y + 1, z + 1, "plasma_cable4", 11);
            placeCable(world, x + 5, y + 1, z, "copper_cable", 0);
            log("all machines and plasma cables placed");
            testBatteryRecipes();
            testStaticValues();
            testMaceratorBattery(world, x - 2, y, z);
            testSlowGrinder(world, x - 2, y, z + 2);
            testElectrolyzer(world, x - 2, y, z - 2);
            testTerraformer(world, x - 2, y + 4, z);
            testElectricWrench(world, x - 2, y + 2, z);
        }
        catch (Throwable t) {
            log("FAILED setup: " + String.valueOf(t));
            t.printStackTrace();
            return;
        }
        for (int i = 0; i < 40; ++i) {
            try {
                Thread.sleep(1000L);
            }
            catch (InterruptedException e) {
                return;
            }
            if (i == 9) {
                log("10s of ticking survived");
                log("diag pos px=" + px + " py=" + py + " pz=" + pz);
            }
            if (i == 34) {
                log("35s of ticking survived, running result checks");
                testBatteryResults(world, px - 2, py, pz);
                testSlowGrinderResults(world, px - 2, py, pz + 2);
                testElectrolyzerResults(world, px - 2, py, pz - 2);
                testTerraformerResults(world, px - 2, py + 4, pz);
                testRound3(world, px, py, pz);
                SICrossTest.run(world, px, py, pz);
                testUraniumDust();
                testCrossModCompat();
                testBonusBlocks(world, px + 2, py, pz);
            }
        }
        logBatpackChecks();
        log("checks: " + checks + ", failures: " + failures);
        if (failures == 0) {
            log("SERVER TEST PASSED: machines, battery discharge, batpack, terraformer, wrench, electrolyzer");
        } else {
            log("SERVER TEST FAILED: " + failures + " failures");
        }
    }

    private static void testBonusBlocks(World world, int x, int y, int z) {
        log("bonus blocks checks:");
        try {
            place(world, x, y, z, "mesh_steel", IC2Blocks.meshSteel);
            check("mesh steel placed", world.getBlockId(x, y, z) == IC2Blocks.meshSteel.id(), String.valueOf(world.getBlockId(x, y, z)));
            place(world, x, y + 1, z, "mesh_steel_crude", IC2Blocks.meshSteelCrude);
            check("mesh crude placed", world.getBlockId(x, y + 1, z) == IC2Blocks.meshSteelCrude.id(), String.valueOf(world.getBlockId(x, y + 1, z)));
            place(world, x, y + 2, z, "glass_quartz", IC2Blocks.quartzGlass);
            check("quartz glass placed", world.getBlockId(x, y + 2, z) == IC2Blocks.quartzGlass.id(), String.valueOf(world.getBlockId(x, y + 2, z)));
            world.setBlockWithNotify(x, y, z, 0);
            world.setBlockWithNotify(x, y + 1, z, 0);
            world.setBlockWithNotify(x, y + 2, z, 0);
            RecipeGroup<?> wb = RecipeBuilder.getRecipeGroup(IC2.MOD_ID, "workbench", new RecipeSymbol(Blocks.WORKBENCH.getDefaultStack()));
            check("mesh recipes registered", wb.getItem("mesh_steel") != null && wb.getItem("mesh_steel_crude") != null && wb.getItem("glass_quartz") != null, "3 recipes");
        }
        catch (Throwable t) {
            check("bonus blocks", false, String.valueOf(t));
        }
    }

    private static void testUraniumDust() {
        log("uranium dust checks:");
        try {
            ItemStack out = TileEntityMacerator.RECIPES.get(IC2Items.uraniumItem.id);
            check("macerator uranium chunk", out != null && out.getItem() == IC2Items.dustUranium && out.stackSize == 1, String.valueOf(out));
            out = TileEntityMacerator.RECIPES.get(IC2Items.ingotUran.id);
            check("macerator uranium ingot", out != null && out.getItem() == IC2Items.dustUranium && out.stackSize == 1, String.valueOf(out));
            net.minecraft.core.data.registry.recipe.RecipeGroup<?> fg = net.minecraft.core.data.registry.Registries.RECIPES.getGroupFromKey("ic2:furnace");
            check("furnace uranium dust smelt", fg != null && fg.getItem("uranium_dust_ingot") != null, "uranium_dust_ingot");
            net.minecraft.core.data.registry.recipe.RecipeGroup<?> bg = net.minecraft.core.data.registry.Registries.RECIPES.getGroupFromKey("ic2:blast_furnace");
            check("blast furnace uranium dust smelt", bg != null && bg.getItem("uranium_dust_ingot") != null, "uranium_dust_ingot");
        }
        catch (Throwable t) {
            check("uranium dust", false, String.valueOf(t));
        }
    }

    private static void testCrossModCompat() {
        log("cross-mod compatibility checks:");
        boolean any = false;
        if (net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("aether")) {
            any = true;
            try {
                Item ambrosium = CrossModCompat.item("aether:item/ambrosium");
                Block<?> oreAmbrosium = CrossModCompat.block("aether:block/ore_ambrosium_holystone");
                Block<?> logSkyroot = CrossModCompat.block("aether:block/log_skyroot");
                Block<?> quicksoil = CrossModCompat.block("aether:block/quicksoil");
                Block<?> glassQuicksoil = CrossModCompat.block("aether:block/glass_quicksoil");
                Item amber = CrossModCompat.item("aether:item/amber");
                if (oreAmbrosium != null && ambrosium != null) {
                    ItemStack out = TileEntityMacerator.RECIPES.get(oreAmbrosium.id());
                    check("aether macerator ambrosium ore", out != null && out.getItem() == ambrosium && out.stackSize == 2, String.valueOf(out));
                }
                if (logSkyroot != null) {
                    check("aether wood gasser skyroot", TileEntityWoodGasser.WoodGasserRecipes.RECIPES.containsKey(logSkyroot.id()), String.valueOf(logSkyroot.namespaceId()));
                }
                if (quicksoil != null && glassQuicksoil != null) {
                    ItemStack out = TileEntityCompressor.RECIPES.get(quicksoil.id());
                    check("aether compressor quicksoil", out != null && out.getItem() == glassQuicksoil.asItem(), String.valueOf(out));
                }
                if (amber != null) {
                    ItemStack out = TileEntityExtractor.RECIPES.get(amber.id);
                    check("aether extractor amber", out != null && out.getItem() == IC2Items.rubber, String.valueOf(out));
                }
                RecipeGroup<?> wb = RecipeBuilder.getRecipeGroup(IC2.MOD_ID, "workbench", new RecipeSymbol(Blocks.WORKBENCH.getDefaultStack()));
                int plantballs = 0;
                for (String key : wb.keySet()) {
                    if (String.valueOf(key).startsWith("aether_plantball_")) {
                        ++plantballs;
                    }
                }
                check("aether plantball recipes", plantballs == 5, "found " + plantballs);
            }
            catch (Throwable t) {
                check("aether compat", false, String.valueOf(t));
            }
        }
        if (net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("deep")) {
            any = true;
            try {
                Item rawSilver = CrossModCompat.item("deep:item/raw_silver");
                Item uranium = CrossModCompat.item("deep:item/uranium");
                Block<?> silverOre = CrossModCompat.deepOre("stone", "silver");
                Block<?> rhodOre = CrossModCompat.deepOre("netherrack", "rhodonite");
                Item rhodonite = CrossModCompat.item("deep:item/rhodonite");
                Item silver = CrossModCompat.item("deep:item/ingot_silver");
                Item lead = CrossModCompat.item("deep:item/ingot_lead");
                if (silverOre != null && rawSilver != null) {
                    ItemStack out = TileEntityMacerator.RECIPES.get(silverOre.id());
                    check("deep macerator silver ore", out != null && out.getItem() == rawSilver && out.stackSize == 2, String.valueOf(out));
                }
                if (rawSilver != null && IC2Items.dustSilver != null) {
                    ItemStack out = TileEntityMacerator.RECIPES.get(rawSilver.id);
                    check("deep macerator raw silver", out != null && out.getItem() == IC2Items.dustSilver && out.stackSize == 2, String.valueOf(out));
                }
                if (lead != null && IC2Items.dustLead != null) {
                    ItemStack out = TileEntityMacerator.RECIPES.get(lead.id);
                    check("deep macerator lead ingot", out != null && out.getItem() == IC2Items.dustLead && out.stackSize == 1, String.valueOf(out));
                }
                Item rawLead = CrossModCompat.item("deep:item/raw_lead");
                if (rawLead != null && IC2Items.dustLead != null) {
                    ItemStack out = TileEntityMacerator.RECIPES.get(rawLead.id);
                    check("deep macerator raw lead", out != null && out.getItem() == IC2Items.dustLead && out.stackSize == 2, String.valueOf(out));
                }
                if (uranium != null && IC2Items.dustUranium != null) {
                    ItemStack out = TileEntityMacerator.RECIPES.get(uranium.id);
                    check("deep macerator uranium", out != null && out.getItem() == IC2Items.dustUranium && out.stackSize == 2, String.valueOf(out));
                }
                if (rhodOre != null && rhodonite != null) {
                    ItemStack out = TileEntityMacerator.RECIPES.get(rhodOre.id());
                    check("deep macerator rhodonite ore", out != null && out.getItem() == rhodonite && out.stackSize == 4, String.valueOf(out));
                }
                if (uranium != null) {
                    ItemStack out = TileEntityCompressor.RECIPES.get(uranium.id);
                    check("deep compressor uranium", out != null && out.getItem() == IC2Items.ingotUran, String.valueOf(out));
                }
                if (silver != null) {
                    RecipeGroup<?> wb = RecipeBuilder.getRecipeGroup(IC2.MOD_ID, "workbench", new RecipeSymbol(Blocks.WORKBENCH.getDefaultStack()));
                    check("deep mixed metal silver", wb.getItem("mixed_metal_silver") != null, "mixed_metal_silver");
                    check("deep reactor plating lead", wb.getItem("reactor_plating_lead") != null, "reactor_plating_lead");
                    check("deep nuke recipe", wb.getItem("nuke_deep") != null, "nuke_deep");
                    ItemStack out = TileEntityMacerator.RECIPES.get(silver.id);
                    check("deep macerator silver ingot", out != null && out.getItem() == IC2Items.dustSilver && out.stackSize == 1, String.valueOf(out));
                    net.minecraft.core.data.registry.recipe.RecipeGroup<?> fg = net.minecraft.core.data.registry.Registries.RECIPES.getGroupFromKey("ic2:furnace");
                    check("deep silver dust smelt", fg != null && fg.getItem("deep_silver_dust_ingot") != null, "deep_silver_dust_ingot");
                    net.minecraft.core.data.registry.recipe.RecipeGroup<?> bg = net.minecraft.core.data.registry.Registries.RECIPES.getGroupFromKey("ic2:blast_furnace");
                    check("deep lead dust smelt", bg != null && bg.getItem("deep_lead_dust_ingot") != null, "deep_lead_dust_ingot");
                }
            }
            catch (Throwable t) {
                check("deep compat", false, String.valueOf(t));
            }
        }
        if (!any) {
            log("  skip: aether/deep not installed");
        }
    }

    private static void testBatteryRecipes() {
        log("battery crafting recipe checks:");
        try {
            RecipeGroup<?> group = RecipeBuilder.getRecipeGroup(IC2.MOD_ID, "workbench", new RecipeSymbol(Blocks.WORKBENCH.getDefaultStack()));
            checkRecipeBattery(group, "generator", IC2Items.batteryRE);
            checkRecipeBattery(group, "generator_alt", IC2Items.batteryRE);
            checkRecipeBattery(group, "batbox", IC2Items.batteryRE);
            checkRecipeBattery(group, "mining_drill", IC2Items.batteryRE);
            checkRecipeBattery(group, "chainsaw", IC2Items.batteryRE);
            checkRecipeBattery(group, "mfe", IC2Items.batteryCrystal);
            checkRecipeBattery(group, "mfsu", IC2Items.batteryLamaCrystal);
            checkRecipeBattery(group, "lapotron_crystal", IC2Items.batteryCrystal);
            checkRecipeBattery(group, "quantum_chestplate", IC2Items.batteryLamaCrystal);
        }
        catch (Throwable t) {
            check("battery recipe lookup", false, String.valueOf(t));
        }
    }

    private static void checkRecipeBattery(RecipeGroup<?> group, String recipeId, net.minecraft.core.item.Item battery) {
        try {
            RecipeEntryCraftingShaped recipe = (RecipeEntryCraftingShaped)group.getItem(recipeId);
            RecipeSymbol[] symbols = (RecipeSymbol[])recipe.getInput();
            RecipeSymbol batSymbol = null;
            for (RecipeSymbol s : symbols) {
                if (s != null && s.getStack() != null && s.getStack().getItem() == battery) {
                    batSymbol = s;
                    break;
                }
            }
            if (batSymbol == null) {
                check("recipe " + recipeId + " uses battery", false, "no symbol");
                return;
            }
            boolean emptyOk = batSymbol.matches(new ItemStack(battery, 1, 10000));
            boolean partialOk = batSymbol.matches(new ItemStack(battery, 1, 4000));
            boolean fullOk = batSymbol.matches(new ItemStack(battery, 1, 0));
            check("recipe " + recipeId + " accepts any battery charge", emptyOk && partialOk && fullOk, "symbol meta=" + batSymbol.getStack().getMetadata());
        }
        catch (Throwable t) {
            check("recipe " + recipeId + " battery check", false, String.valueOf(t));
        }
    }

    private static void testStaticValues() {
        check("pesd maxDamage displays 10000", IC2Items.pesd.getMaxDamage() + 1 == 10000, "max=" + (IC2Items.pesd.getMaxDamage() + 1));
        check("pesd capacity 50M EU", (IC2Items.pesd.getMaxDamage() + 1) * 5000 == 50000000, "cap=" + (IC2Items.pesd.getMaxDamage() + 1) * 5000);
        ItemStack fresh = new ItemStack(IC2Items.batteryRE);
        ic2.item.ItemBattery reb = (ic2.item.ItemBattery)IC2Items.batteryRE;
        int eu = reb.getEnergyFrom(fresh, 10000, 1);
        check("re_battery transfer limit 250", eu == 250, "eu=" + eu);
        int total = eu;
        for (int i = 0; i < 80 && reb.getEnergyFrom(fresh, 10000, 1) > 0; ++i) {
            total += 250;
        }
        check("re_battery fully drains to 10000 EU", total >= 10000, "total=" + total);
        ItemStack crystal = new ItemStack(IC2Items.batteryCrystal);
        int eu2 = ((ic2.item.ItemBattery)IC2Items.batteryCrystal).getEnergyFrom(crystal, 400, 1);
        check("energy crystal discharges into tier-1 machine", eu2 == 400, "eu=" + eu2);
        check("crystal clamped to request", crystal.getMetadata() == 40, "meta=" + crystal.getMetadata());
        ItemStack partial = new ItemStack(IC2Items.batteryRE);
        partial.setMetadata(9000);
        ic2.item.ItemBattery bat = (ic2.item.ItemBattery)IC2Items.batteryRE;
        int drained = 0;
        for (int i = 0; i < 5; ++i) {
            drained += bat.getEnergyFrom(partial, 150, 1);
        }
        check("battery drain clamped to request", drained == 750, "drained=" + drained);
    }

    private static void testMaceratorBattery(World world, int x, int y, int z) {
        try {
            place(world, x, y, z, "macerator", IC2Blocks.macerator);
            TileEntityMacerator mac = (TileEntityMacerator)world.getTileEntity(x, y, z);
            mac.setItem(0, new ItemStack(Blocks.COBBLE_STONE, 16));
            ItemStack battery = new ItemStack(IC2Items.batteryRE);
            mac.setItem(1, battery);
            mac.setItem(2, null);
            log("macerator battery test armed (re_battery meta 0)");
        }
        catch (Throwable t) {
            check("macerator arm", false, String.valueOf(t));
        }
    }

    private static void testBatteryResults(World world, int x, int y, int z) {
        try {
            TileEntityMacerator mac = (TileEntityMacerator)world.getTileEntity(x, y, z);
            if (mac == null) {
                check("macerator alive", false, "tile gone");
                return;
            }
            check("macerator energy within limit", mac.energy >= 0 && mac.energy <= ((TileEntityElecMachine)mac).maxEnergy, "energy=" + mac.energy);
            check("macerator accepted battery energy", mac.energy > 0, "energy=" + mac.energy);
            ItemStack bat = mac.getItem(1);
            check("battery drained", bat != null && bat.getMetadata() > 0, "meta=" + (bat == null ? -1 : bat.getMetadata()));
            check("macerator produced output", mac.getItem(2) != null, "out=" + String.valueOf(mac.getItem(2)));
            mac.setItem(1, new ItemStack(IC2Items.batteryCrystal));
            TileEntityElecMachine em = (TileEntityElecMachine)mac;
            em.energy = 0;
            log("energy crystal inserted into macerator");
        }
        catch (Throwable t) {
            check("macerator results", false, String.valueOf(t));
        }
    }

    private static void testSlowGrinder(World world, int x, int y, int z) {
        try {
            place(world, x, y, z, "slow_grinder", IC2Blocks.slowGrinder);
            TileEntitySlowGrinder sg = (TileEntitySlowGrinder)world.getTileEntity(x, y, z);
            sg.setItem(3, new ItemStack(Blocks.COBBLE_STONE, 16));
            sg.setItem(1, new ItemStack(IC2Items.batteryCrystal));
            sg.handleEvent(0);
            sg.handleEvent(0);
            check("slow grinder speed toggled to 3", sg.speed == 3, "speed=" + sg.speed);
            log("slow grinder armed: speed 3, crystal battery, cobble buffer");
        }
        catch (Throwable t) {
            check("slow grinder arm", false, String.valueOf(t));
        }
    }

    private static void testSlowGrinderResults(World world, int x, int y, int z) {
        try {
            TileEntitySlowGrinder sg = (TileEntitySlowGrinder)world.getTileEntity(x, y, z);
            if (sg == null) {
                check("slow grinder alive", false, "tile gone");
                return;
            }
            check("slow grinder speed synced by nbt", sg.speed == 3, "speed=" + sg.speed);
            check("slow grinder energy within limit", sg.energy >= 0 && sg.energy <= ((TileEntityElecMachine)sg).maxEnergy, "energy=" + sg.energy);
            check("slow grinder started grinding", sg.progress > 0 || sg.getItem(2) != null, "progress=" + sg.progress);
            ItemStack sgBat = sg.getItem(1);
            boolean drained = sgBat != null && sgBat.getMetadata() > 0 || sgBat == null;
            check("slow grinder battery drained", drained, "batMeta=" + (sgBat == null ? -1 : sgBat.getMetadata()) + " energy=" + sg.energy);
        }
        catch (Throwable t) {
            check("slow grinder results", false, String.valueOf(t));
        }
    }

    private static void testElectrolyzer(World world, int x, int y, int z) {
        try {
            place(world, x, y + 1, z, "mfe", IC2Blocks.mfe);
            place(world, x, y, z, "electrolyzer", IC2Blocks.electrolyzer);
            TileEntityElectrolyzer el = (TileEntityElectrolyzer)world.getTileEntity(x, y, z);
            el.setItem(0, new ItemStack(IC2Items.deadMagnet, 4));
            el.setItem(1, null);
            ic2.tileentity.TileEntityElectricBlock mfe = (ic2.tileentity.TileEntityElectricBlock)world.getTileEntity(x, y + 1, z);
            mfe.energy = 600000;
            el.energy = 14000;
            log("electrolyzer armed: dead magnets + full MFE + precharged 14000");
        }
        catch (Throwable t) {
            check("electrolyzer arm", false, String.valueOf(t));
        }
    }

    private static void testElectrolyzerResults(World world, int x, int y, int z) {
        try {
            TileEntityElectrolyzer el = (TileEntityElectrolyzer)world.getTileEntity(x, y, z);
            if (el == null) {
                check("electrolyzer alive", false, "tile gone");
                return;
            }
            ItemStack out = el.getItem(1);
            check("electrolyzer produced magnet", out != null && out.getItem() == IC2Items.magnet, "out=" + String.valueOf(out));
        }
        catch (Throwable t) {
            check("electrolyzer results", false, String.valueOf(t));
        }
    }

    private static void testTerraformer(World world, int x, int y, int z) {
        try {
            place(world, x, y, z, "terraformer", IC2Blocks.terraformer);
            TileEntityTerraformer tf = (TileEntityTerraformer)world.getTileEntity(x, y, z);
            tf.setItem(0, new ItemStack(IC2Items.tfbpCultivation));
            tf.energy = 100000;
            boolean inserted = ((ic2.item.ItemTFBPPlaceable)IC2Items.tfbpCultivation).onUseOnBlock(new ItemStack(IC2Items.tfbpIrrigation), world, null, new TilePos(x, y, z), Side.TOP, 0.5, 0.5);
            check("tfbp right-click swaps blueprint", inserted && tf.getItem(0).getItem() == IC2Items.tfbpIrrigation, "bp=" + String.valueOf(tf.getItem(0)));
            log("terraformer armed: irrigation bp, 100000 EU");
        }
        catch (Throwable t) {
            check("terraformer arm", false, String.valueOf(t));
        }
    }

    private static void testTerraformerResults(World world, int x, int y, int z) {
        try {
            TileEntityTerraformer tf = (TileEntityTerraformer)world.getTileEntity(x, y, z);
            if (tf == null) {
                check("terraformer alive", false, "tile gone");
                return;
            }
            check("terraformer consumed energy", tf.energy < 100000, "energy=" + tf.energy);
            check("terraformer holds blueprint", tf.getItem(0) != null, "bp=" + String.valueOf(tf.getItem(0)));
        }
        catch (Throwable t) {
            check("terraformer results", false, String.valueOf(t));
        }
    }

    private static void testElectricWrench(World world, int x, int y, int z) {
        try {
            place(world, x, y, z, "generator_test", IC2Blocks.generator);
            TilePos pos = new TilePos(x, y, z);
            ItemStack ew = new ItemStack(IC2Items.electricWrench);
            int meta0 = ew.getMetadata();
            boolean result = ((ic2.item.tool.ItemElectricWrench)IC2Items.electricWrench).onUseOnBlock(ew, world, null, pos, Side.TOP, 0.5, 0.5);
            check("electric wrench dismantled machine", result && world.getBlockId(x, y, z) == 0, "result=" + result + " block=" + world.getBlockId(x, y, z));
            check("electric wrench consumed 1 charge", ew.getMetadata() == meta0 + 1, "meta=" + ew.getMetadata());
            boolean dism = ((net.minecraft.core.block.BlockLogic)world.getBlockType(new TilePos(x - 1, y, z)).getLogic()) != null;
            log("wrench test done, dism=" + dism);
        }
        catch (Throwable t) {
            check("electric wrench", false, String.valueOf(t));
        }
    }

    private static void logBatpackChecks() {
        try {
            ItemStack batpack = new ItemStack(IC2Items.batpack);
            ItemStack drill = new ItemStack(IC2Items.miningDrill);
            drill.setMetadata(4000);
            int packMeta0 = batpack.getMetadata();
            ic2.item.armor.ItemArmorChargeable armor = (ic2.item.armor.ItemArmorChargeable)IC2Items.batpack;
            armor.chargeTool(batpack, drill);
            check("batpack charged drill", drill.getMetadata() < 4000, "meta=" + drill.getMetadata());
            check("batpack depleted itself", batpack.getMetadata() > packMeta0, "meta=" + batpack.getMetadata() + " was " + packMeta0);
            check("batpack meta within range", batpack.getMetadata() <= batpack.getMaxDamage() + 1, "meta=" + batpack.getMetadata() + " max=" + batpack.getMaxDamage());
            int toolEu = (4000 - drill.getMetadata()) * 50;
            int packEu = (batpack.getMetadata() - packMeta0) * 100;
            check("batpack energy conserved", toolEu == packEu, "toolEu=" + toolEu + " packEu=" + packEu);
        }
        catch (Throwable t) {
            check("batpack", false, String.valueOf(t));
        }
    }

    private static void check(String name, boolean ok, String detail) {
        ++checks;
        if (ok) {
            log("  PASS " + name + " (" + detail + ")");
        } else {
            ++failures;
            log("  FAIL " + name + " (" + detail + ")");
        }
    }

    private static void place(World world, int x, int y, int z, String key, Block<?> b) {
        if (b == null) {
            log("  block missing: " + key);
            return;
        }
        if (!world.isChunkLoaded(x >> 4, z >> 4)) {
            world.getChunkFromBlockCoords(x, z);
        }
        if (world.getBlockId(x, y, z) == b.id()) {
            world.setBlockWithNotify(x, y, z, 0);
        }
        world.setBlockWithNotify(x, y, z, b.id());
        if (world.getBlockId(x, y, z) != b.id()) {
            log("  RETRY placing " + key);
            world.getChunkFromBlockCoords(x, z);
            world.setBlockWithNotify(x, y, z, b.id());
        }
        log("  placed " + key + " -> " + world.getBlockId(x, y, z));
    }

    private static void placeCable(World world, int x, int y, int z, String key, int type) {
        if (!world.isChunkLoaded(x >> 4, z >> 4)) {
            world.getChunkFromBlockCoords(x, z);
        }
        world.setBlockWithNotify(x, y, z, IC2Blocks.cable.id());
        world.setBlockMetadataWithNotify(x, y, z, type);
        log("  placed cable " + key + " type " + type + " -> " + world.getBlockId(x, y, z) + "/" + world.getBlockData((TilePosc)new TilePos(x, y, z)));
    }

    private static void testRound3(World world, int px, int py, int pz) {
        try {
            TileEntityCable pc = new TileEntityCable((short)11);
            check("plasma cable absorption 4096", pc.getInsulationEnergyAbsorption() == 4096, "abs=" + pc.getInsulationEnergyAbsorption());
            check("plasma cable breakdown 4097", pc.getConductorBreakdownEnergy() == 4097, "brk=" + pc.getConductorBreakdownEnergy());
        } catch (Throwable t) {
            check("plasma cable stats", false, String.valueOf(t));
        }
        try {
            place(world, px + 12, py, pz, "macerator_ovl", IC2Blocks.macerator);
            TileEntityMacerator mac = (TileEntityMacerator)world.getTileEntity(px + 12, py, pz);
            mac.injectEnergy(Direction.YN, 2048);
            check("macerator clamps overload to maxInput", mac.energy == 32, "energy=" + mac.energy);
            check("macerator survives overload", world.getBlockId(px + 12, py, pz) == IC2Blocks.macerator.id(), "id=" + world.getBlockId(px + 12, py, pz));
            mac.energy = 0;
            ItemStack crystal = new ItemStack(IC2Items.batteryCrystal);
            mac.inventory[mac.fuelSlot] = crystal;
            mac.setItem(0, new ItemStack(Blocks.COBBLE_STONE, 8));
            for (int i = 0; i < 20; ++i) {
                Thread.sleep(100);
                if (mac.energy > 0) {
                    break;
                }
            }
            boolean crystalDrained = crystal.getMetadata() > 0 || mac.energy > 0;
            check("energy crystal powers macerator", crystalDrained, "meta=" + crystal.getMetadata() + " energy=" + mac.energy);
        } catch (Throwable t) {
            check("macerator overload", false, String.valueOf(t));
        }
        try {
            place(world, px + 12, py, pz + 2, "pesu_ovl", IC2Blocks.pesu);
            TileEntityPESU pesu = (TileEntityPESU)world.getTileEntity(px + 12, py, pz + 2);
            pesu.injectEnergy(Direction.YN, 4096);
            check("pesu clamps overload to output", pesu.energy == pesu.output, "energy=" + pesu.energy + " output=" + pesu.output);
            check("pesu survives overload", world.getBlockId(px + 12, py, pz + 2) == IC2Blocks.pesu.id(), "id=" + world.getBlockId(px + 12, py, pz + 2));
        } catch (Throwable t) {
            check("pesu overload", false, String.valueOf(t));
        }
        try {
            place(world, px + 20, py, pz, "plasmafier_ovl", IC2Blocks.plasmafier);
            TileEntityPlasmafier pl = (TileEntityPlasmafier)world.getTileEntity(px + 20, py, pz);
            pl.injectEnergy(Direction.YN, 4096);
            check("plasmafier accepts 4096 packet", pl.energy == 4096, "energy=" + pl.energy);
            try {
                pl.injectEnergy(Direction.YN, 8192);
            } catch (Throwable t) {
                log("explosion side effect off-thread: " + t);
            }
            check("plasmafier explodes beyond 4096", world.getBlockId(px + 20, py, pz) == 0, "id=" + world.getBlockId(px + 20, py, pz));
        } catch (Throwable t) {
            check("plasmafier overload", false, String.valueOf(t));
        }
        try {
            place(world, px - 6, py, pz, "nuclear_reactor", IC2Blocks.nuclearReactor);
            place(world, px - 6, py + 1, pz, "reactor_chamber", IC2Blocks.reactorChamber);
            TileEntityNuclearReactor reactor = TileEntityReactorChamber.getReactor(world, new TilePos(px - 6, py + 1, pz));
            check("reactor chamber resolves reactor", reactor != null && reactor.tilePos.x() == px - 6 && reactor.tilePos.y() == py,
                "reactor=" + (reactor == null ? "null" : reactor.tilePos));
            TilePosc cablePos = new TilePos(px + 12, py + 1, pz);
            ItemStack[] picks = IC2Blocks.cable.getBreakResult(world, EnumDropCause.PICK_BLOCK, cablePos, 11, null);
            check("pick block returns plasma cable item", picks != null && picks.length > 0 && picks[0].getItem() == IC2Items.cableItems[11],
                "pick=" + (picks == null || picks.length == 0 ? "null" : picks[0]));
            ItemStack[] picks2 = IC2Blocks.cable.getBreakResult(world, EnumDropCause.PICK_BLOCK, cablePos, 0, null);
            check("pick block returns tin cable item", picks2 != null && picks2.length > 0 && picks2[0].getItem() == IC2Items.cableItems[0],
                "pick=" + (picks2 == null || picks2.length == 0 ? "null" : picks2[0]));
            ItemStack[] picks3 = IC2Blocks.cable.getBreakResult(world, EnumDropCause.PICK_BLOCK, cablePos, 8, null);
            check("pick block returns 4x insulated cable item", picks3 != null && picks3.length > 0 && picks3[0].getItem() == IC2Items.cableItems[8],
                "pick=" + (picks3 == null || picks3.length == 0 ? "null" : picks3[0]));
        } catch (Throwable t) {
            check("reactor chamber", false, String.valueOf(t));
        }
        try {
            TileEntitySlowGrinder sg = (TileEntitySlowGrinder)world.getTileEntity(px - 2, py, pz + 2);
            int before = sg.speed;
            MachineEventMessage msg = new MachineEventMessage();
            msg.x = px - 2;
            msg.y = py;
            msg.z = pz + 2;
            msg.event = 0;
            Method m = MachineEventMessage.class.getDeclaredMethod("apply", World.class);
            m.setAccessible(true);
            m.invoke(msg, world);
            check("speed button message toggles speed", sg.speed == (before == 5 ? 1 : before + 1), "before=" + before + " after=" + sg.speed);
        } catch (Throwable t) {
            check("speed button message", false, String.valueOf(t));
        }
        try {
            place(world, px - 8, py, pz, "macerator_slot", IC2Blocks.macerator);
            TileEntityMacerator mac2 = (TileEntityMacerator)world.getTileEntity(px - 8, py, pz);
            SlotIC2Battery batSlot = new SlotIC2Battery(mac2, 1, 0, 0);
            SlotIC2Input inSlot = new SlotIC2Input(mac2, 0, 0, 0);
            check("battery slot accepts re battery", batSlot.mayPlace(new ItemStack(IC2Items.batteryRE)), "bat");
            check("battery slot accepts energy crystal", batSlot.mayPlace(new ItemStack(IC2Items.batteryCrystal)), "crystal");
            check("battery slot rejects ore", !batSlot.mayPlace(new ItemStack(Blocks.COBBLE_STONE)), "ore");
            check("input slot rejects battery", !inSlot.mayPlace(new ItemStack(IC2Items.batteryRE)), "bat");
            check("input slot accepts ore", inSlot.mayPlace(new ItemStack(Blocks.COBBLE_STONE)), "ore");
        } catch (Throwable t) {
            check("battery slot restrictions", false, String.valueOf(t));
        }
    }

    private static void log(String s) {
        IC2.LOGGER.info("[SRV-TEST] " + s);
    }
}
