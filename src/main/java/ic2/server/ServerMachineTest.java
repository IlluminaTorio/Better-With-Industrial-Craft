package ic2.server;

import ic2.IC2;
import ic2.IC2Blocks;
import ic2.IC2Items;
import ic2.compat.CrossModCompat;
import ic2.entity.EntityDynamite;
import ic2.entity.EntityDrone;
import ic2.entity.EntityIC2Explosive;
import ic2.entity.EntityMiningLaser;
import ic2.tileentity.TileEntityCompressor;
import ic2.tileentity.TileEntityElecMachine;
import ic2.tileentity.TileEntityElectricTrommel;
import ic2.tileentity.TileEntityElectricBlastFurnace;
import ic2.tileentity.TileEntityAdvMachine;
import ic2.tileentity.TileEntityRotaryMacerator;
import ic2.tileentity.TileEntitySingularityCompressor;
import ic2.tileentity.TileEntityWeatherController;
import ic2.tileentity.TileEntityElectrolyzer;
import ic2.tileentity.TileEntityExtractor;
import ic2.tileentity.TileEntityWoodGasser;
import ic2.energy.Direction;
import ic2.gui.slot.SlotIC2Battery;
import ic2.gui.slot.SlotIC2Charge;
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
import net.minecraft.core.block.BlockLogicChest;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.data.registry.recipe.RecipeGroup;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCraftingShaped;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryTrommel;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityDispatcher;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryBlastFurnace;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import net.minecraft.server.MinecraftServer;
import turniplabs.halplibe.helper.RecipeBuilder;
import net.minecraft.core.item.Items;
import net.minecraft.core.item.ItemBucket;
import ic2.util.IC2EnergyTiers;
import ic2.energy.CablePorts;
import ic2.util.BucketUtil;
import ic2.tileentity.TileEntityDroneDepot;
import ic2.tileentity.TileEntityWaterGenerator;
import ic2.tileentity.TileEntityTurbineSolar;
import com.mojang.nbt.tags.CompoundTag;

public class ServerMachineTest {
    private static volatile boolean started = false;

    public static boolean isTestMode() {
        return started;
    }
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
        long baseWorldTime = world.getTotalWorldTime();
        log("waiting for real ticking, baseWorldTime=" + baseWorldTime);
        for (int i = 0; i < 300; ++i) {
            try {
                if (world.getTotalWorldTime() > baseWorldTime + 40L) break;
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
        try {
            MinecraftServer serverPeaceful = MinecraftServer.getInstance();
            serverPeaceful.difficulty = 0;
            for (Object w : serverPeaceful.dimensionWorlds.values()) {
                ((World)w).setDifficulty(0, true);
            }
            log("difficulty set to peaceful for test zone");
        }
        catch (Throwable t) {
            log("peaceful setup failed: " + String.valueOf(t));
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
            for (int dx = -12; dx <= 18; ++dx) {
                for (int dz = -30; dz <= 6; ++dz) {
                    for (int dy = 0; dy < 6; ++dy) {
                        int cx = x + dx;
                        int cy = y + dy;
                        int cz = z + dz;
                        if (!world.isChunkLoaded(cx >> 4, cz >> 4)) {
                            world.getChunkFromBlockCoords(cx, cz);
                        }
                        if (world.getBlockId(cx, cy, cz) != 0) {
                            world.setBlockWithNotify(cx, cy, cz, 0);
                        }
                        if (dy == 0) {
                            boolean filled = false;
                            for (int fy = 0; fy < 10; ++fy) {
                                int by = cy - 1 - fy;
                                if (world.getBlockId(cx, by, cz) != 0) {
                                    break;
                                }
                                world.setBlockWithNotify(cx, by, cz, (fy == 0 ? Blocks.GRASS : Blocks.DIRT).id());
                                filled = true;
                            }
                            if (filled && !world.isChunkLoaded(cx >> 4, cz >> 4)) {
                                world.getChunkFromBlockCoords(cx, cz);
                            }
                        }
                    }
                }
            }
            log("test zone flattened: dx -12..18, dz -30..6, ground filled at y-1");
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
            testElectricTrommel(world, x - 2, y, z - 4);
            testElectricBlastFurnace(world, x - 2, y, z - 6);
            testRotaryMacerator(world, x - 2, y, z - 8);
            testSingularityCompressor(world, x - 2, y, z - 10);
            testWeatherController(world, x - 2, y, z - 12);
            testNewMachines(world, x - 2, y, z - 14);
            testTerraformer(world, x - 2, y + 4, z);
            testElectricWrench(world, x - 2, y + 2, z);
            testBatBoxCharge(world, x - 4, y, z - 2);
        }
        catch (Throwable t) {
            log("FAILED setup: " + String.valueOf(t));
            t.printStackTrace();
            return;
        }
        for (int i = 0; i < 100; ++i) {
            try {
                Thread.sleep(1000L);
            }
            catch (InterruptedException e) {
                return;
            }
            if (i == 9) {
                log("10s of ticking survived");
                log("diag pos px=" + px + " py=" + py + " pz=" + pz);
                testDroneSend(world, px - 2, py, pz - 20);
            }
            if (i == 94) {
                log("95s of ticking survived, running result checks");
                testBatteryResults(world, px - 2, py, pz);
                testBatBoxChargeResults(world, px - 4, py, pz - 2);
                testSlowGrinderResults(world, px - 2, py, pz + 2);
                testElectrolyzerResults(world, px - 2, py, pz - 2);
                testElectricTrommelResults(world, px - 2, py, pz - 4);
                testElectricBlastFurnaceResults(world, px - 2, py, pz - 6);
                testRotaryMaceratorResults(world, px - 2, py, pz - 8);
                testSingularityCompressorResults(world, px - 2, py, pz - 10);
                testWeatherControllerResults(world, px - 2, py, pz - 12);
                testNewMachinesResults(world, px - 2, py, pz - 14);
                testDroneResults(world, px - 2, py, pz - 20);
                testTerraformerResults(world, px - 2, py + 4, pz);
                testRound3(world, px, py, pz);
                SICrossTest.run(world, px, py, pz);
                testUraniumDust();
                testCrossModCompat();
                testBonusBlocks(world, px + 2, py, pz);
                testV131Fixes(world, px + 6, py, pz);
                testBrushAndBuckets(world, px + 8, py, pz);
                testEntityTracking(world, px, py + 3, pz);
            }
        }
        logBatpackChecks();
        logBatpackToolChargeChecks();
        log("checks: " + checks + ", failures: " + failures);
        if (failures == 0) {
            log("SERVER TEST PASSED: machines, battery discharge, tool charging, batpack, terraformer, wrench, electrolyzer");
        } else {
            log("SERVER TEST FAILED: " + failures + " failures");
        }
    }

    private static void testV131Fixes(World world, int x, int y, int z) {
        log("1.3.1 fixes checks:");
        try {
            ItemStack ironWater = ItemBucket.createItemStack(Items.BUCKET_IRON, ItemBucket.STATE_WATER, 1);
            ItemStack steelWater = ItemBucket.createItemStack(Items.BUCKET_STEEL, ItemBucket.STATE_WATER, 1);
            check("bucket util iron water", BucketUtil.isBucketWithState(ironWater, ItemBucket.STATE_WATER), "iron");
            check("bucket util steel water", BucketUtil.isBucketWithState(steelWater, ItemBucket.STATE_WATER), "steel");
            ItemStack steelEmpty = BucketUtil.toEmpty(steelWater);
            check("bucket util toEmpty steel", steelEmpty.getItem() == Items.BUCKET_STEEL && ItemBucket.STATE_EMPTY.equals(ItemBucket.getState(steelEmpty)), steelEmpty.getItem().namespaceID.toString());
            ItemStack steelRefilled = BucketUtil.filledCopy(steelEmpty, ItemBucket.STATE_LAVA);
            check("bucket util filledCopy steel lava", steelRefilled.getItem() == Items.BUCKET_STEEL && ItemBucket.STATE_LAVA.equals(ItemBucket.getState(steelRefilled)), "steel lava");
            TileEntityWaterGenerator waterGen = new TileEntityWaterGenerator();
            waterGen.inventory[1] = steelWater.copy();
            boolean gained = waterGen.gainFuel();
            check("water gen accepts steel bucket", gained && waterGen.fuel == 500, "fuel=" + waterGen.fuel);
            check("water gen returns steel empty", waterGen.inventory[1] != null && waterGen.inventory[1].getItem() == Items.BUCKET_STEEL, "steel empty");
            check("drone rejects battery", TileEntityDroneDepot.isEnergyCarrier(new ItemStack(IC2Items.batteryRE)), "re_battery");
            check("drone rejects crystal", TileEntityDroneDepot.isEnergyCarrier(new ItemStack(IC2Items.batteryCrystal)), "energy_crystal");
            check("drone rejects electric block", TileEntityDroneDepot.isEnergyCarrier(IC2Blocks.mfe.getDefaultStack()), "mfe");
            check("drone allows helmet", !TileEntityDroneDepot.isEnergyCarrier(new ItemStack(IC2Items.bronzeHelmet)), "bronze_helmet");
            check("drone allows dust", !TileEntityDroneDepot.isEnergyCarrier(new ItemStack(IC2Items.dustIron)), "dust_iron");
            check("cable colors neutral", TileEntityCable.colorsCompatible(0, 5) && TileEntityCable.colorsCompatible(3, 3), "0-5, 3-3");
            check("cable colors conflict", !TileEntityCable.colorsCompatible(3, 4), "3-4");
            check("teleporter energy tier", IC2EnergyTiers.tierForStack(IC2Blocks.teleporter.getDefaultStack()) == 3, "tier " + IC2EnergyTiers.tierForStack(IC2Blocks.teleporter.getDefaultStack()));
            CablePorts ports = CablePorts.get(world);
            int lx = x + 64;
            ports.putPort(lx, y, z, (short)1, (short)2);
            check("cable ports roundtrip", ports.portMeta(world, lx, y, z) == (1 << 4 | 2), "meta=" + ports.portMeta(world, lx, y, z));
            ports.removePort(lx, y, z);
            check("cable ports removed", ports.portMeta(world, lx, y, z) == -1, "gone");
            int dy = y + 6;
            place(world, x, dy - 1, z, "door_floor", Blocks.STONE);
            place(world, x, dy, z, "reinforced_door_bottom", IC2Blocks.reinforcedDoorBottom);
            world.setBlockWithNotify(x, dy + 1, z, IC2Blocks.reinforcedDoorTop.id());
            world.setBlockWithNotify(x, dy - 1, z, 0);
            boolean doorStays = world.getBlockId(x, dy, z) == IC2Blocks.reinforcedDoorBottom.id() && world.getBlockId(x, dy + 1, z) == IC2Blocks.reinforcedDoorTop.id();
            check("reinforced door survives floor loss", doorStays, "bottom=" + world.getBlockId(x, dy, z) + " top=" + world.getBlockId(x, dy + 1, z));
            world.setBlockWithNotify(x, dy, z, 0);
            world.setBlockWithNotify(x, dy + 1, z, 0);
        }
        catch (Throwable t) {
            check("1.3.1 fixes section", false, String.valueOf(t));
        }
    }

    private static void testBrushAndBuckets(World world, int x, int y, int z) {
        log("brush and bucket checks:");
        try {
            placeCable(world, x, y + 1, z, "paint_cable", 0);
            TilePosc cablePos = new TilePos(x, y + 1, z);
            ic2.block.BlockLogicCable cableLogic = (ic2.block.BlockLogicCable)world.getBlockType(cablePos).getLogic();
            check("cable logic is IPainted", cableLogic instanceof net.minecraft.core.block.IPainted, String.valueOf(cableLogic.getClass()));
            TileEntityCable cable = (TileEntityCable)world.getTileEntity(cablePos);
            check("cable te present", cable != null, "te=" + world.getTileEntity(cablePos));
            ItemStack brush = new ItemStack(Items.PAINTBRUSH);
            net.minecraft.core.item.ItemPaintBrush.setColor(brush, net.minecraft.core.util.helper.DyeColor.RED);
            boolean clicked = Items.PAINTBRUSH.onUseOnBlock(brush, world, null, cablePos, Side.TOP, 0.5, 0.5);
            check("brush paints cable", clicked && cable != null && cable.color == 1, "ret=" + clicked + " color=" + (cable == null ? -1 : cable.color));
            check("brush color readback", cableLogic.getColor(world, cablePos) == net.minecraft.core.util.helper.DyeColor.RED, "dye=" + cableLogic.getColor(world, cablePos));
            int meta0 = brush.getMetadata();
            boolean clickedAgain = Items.PAINTBRUSH.onUseOnBlock(brush, world, null, cablePos, Side.TOP, 0.5, 0.5);
            check("brush same color no-op", !clickedAgain && brush.getMetadata() == meta0, "ret=" + clickedAgain + " meta=" + brush.getMetadata() + "/" + meta0);
            net.minecraft.core.item.ItemPaintBrush.setColor(brush, net.minecraft.core.util.helper.DyeColor.BLACK);
            boolean clickedBlack = Items.PAINTBRUSH.onUseOnBlock(brush, world, null, cablePos, Side.TOP, 0.5, 0.5);
            check("brush black clears color", clickedBlack && cable != null && cable.color == 0, "ret=" + clickedBlack + " color=" + (cable == null ? -1 : cable.color));
            CompoundTag nbt = new CompoundTag();
            cable.writeAdditionalData(nbt);
            check("cable nbt color short", nbt.getShort("color") == 0, "nbtColor=" + nbt.getShort("color"));
            check("painted metadata helpers", cableLogic.toMetadata(net.minecraft.core.util.helper.DyeColor.RED) == 1 && cableLogic.stripColorFromMetadata(3) == 3, "meta helpers");
            placeCable(world, x + 1, y + 1, z, "uninsulated_paint_cable", 1);
            TilePosc unPos = new TilePos(x + 1, y + 1, z);
            TileEntityCable unCable = (TileEntityCable)world.getTileEntity(unPos);
            net.minecraft.core.item.ItemPaintBrush.setColor(brush, net.minecraft.core.util.helper.DyeColor.RED);
            boolean clickedUn = Items.PAINTBRUSH.onUseOnBlock(brush, world, null, unPos, Side.TOP, 0.5, 0.5);
            check("uninsulated cable keeps no color", unCable != null && unCable.color == 0, "ret=" + clickedUn + " color=" + (unCable == null ? -1 : unCable.color));
            world.setBlockWithNotify(x, y + 1, z, 0);
            world.setBlockWithNotify(x + 1, y + 1, z, 0);

            ItemStack steelWater = ItemBucket.createItemStack(Items.BUCKET_STEEL, ItemBucket.STATE_WATER, 1);
            TileEntityTurbineSolar turbine = new TileEntityTurbineSolar();
            turbine.inventory[1] = steelWater.copy();
            boolean gained = turbine.gainFuel();
            check("turbine accepts steel bucket", gained && turbine.water == 2000, "ret=" + gained + " water=" + turbine.water);
            check("turbine returns steel empty", turbine.inventory[1] != null && turbine.inventory[1].getItem() == Items.BUCKET_STEEL && ItemBucket.STATE_EMPTY.equals(ItemBucket.getState(turbine.inventory[1])), "slot=" + turbine.inventory[1]);
            net.minecraft.core.player.inventory.slot.Slot slot = new net.minecraft.core.player.inventory.slot.Slot((Container)turbine, 1, 0, 0);
            check("turbine slot accepts steel bucket", slot.mayPlace(steelWater), "mayPlace");
            ItemStack ironLava = ItemBucket.createItemStack(Items.BUCKET_IRON, ItemBucket.STATE_LAVA, 1);
            check("bucket util water vs lava", BucketUtil.isBucketWithState(steelWater, ItemBucket.STATE_WATER) && !BucketUtil.isBucketWithState(steelWater, ItemBucket.STATE_LAVA) && BucketUtil.isBucketWithState(ironLava, ItemBucket.STATE_LAVA), "states");
            check("non bucket items rejected", !BucketUtil.isEmptyBucket(new ItemStack(Items.PAINTBRUSH)) && !BucketUtil.isBucketWithState(new ItemStack(Items.FOOD_BREAD), ItemBucket.STATE_WATER), "non buckets");
            ItemStack steelEmpty = new ItemStack(Items.BUCKET_STEEL);
            ItemStack steelRefill = BucketUtil.filledCopy(steelEmpty, ItemBucket.STATE_WATER);
            check("steel empty refill keeps type", steelRefill.getItem() == Items.BUCKET_STEEL && ItemBucket.STATE_WATER.equals(ItemBucket.getState(steelRefill)), "refill");
        }
        catch (Throwable t) {
            check("brush and bucket section", false, String.valueOf(t));
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
            check("furnace uranium raw smelt", fg != null && fg.getItem("uranium_refine") != null, "uranium_refine");
            check("compressor has no uranium ingot", TileEntityCompressor.RECIPES.get(IC2Items.uraniumItem.id) == null, "removed");
            check("singularity has no ore ingot", TileEntitySingularityCompressor.RECIPES.get(IC2Blocks.oreUranium.id()) == null, "removed");
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
                    check("deep compressor uranium removed", out == null, String.valueOf(out));
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
        ItemStack flat = new ItemStack(IC2Items.batteryRE);
        flat.setMetadata(10000);
        int recharged = reb.giveEnergyTo(flat, 500, 1);
        check("re_battery recharges in storage", recharged == 250 && flat.getMetadata() == 9750, "eu=" + recharged + " meta=" + flat.getMetadata());
    }

    private static void testBatBoxCharge(World world, int x, int y, int z) {
        try {
            place(world, x, y, z, "batbox_charge", IC2Blocks.batBox);
            TileEntity te = world.getTileEntity(x, y, z);
            check("batbox charge TE", te instanceof ic2.tileentity.TileEntityElectricBlock, te == null ? "null" : te.getClass().getSimpleName());
            if (te instanceof ic2.tileentity.TileEntityElectricBlock) {
                ic2.tileentity.TileEntityElectricBlock box = (ic2.tileentity.TileEntityElectricBlock)te;
                ItemStack drill = new ItemStack(IC2Items.miningDrill);
                drill.setMetadata(drill.getMaxDamage() + 1);
                box.setItem(0, drill);
                box.setItem(1, new ItemStack(IC2Items.batteryRE));
                box.energy = 0;
                log("batbox charge armed: drained drill + full re battery");
            }
        }
        catch (Throwable t) {
            check("batbox charge arm", false, String.valueOf(t));
        }
    }

    private static void testBatBoxChargeResults(World world, int x, int y, int z) {
        try {
            TileEntity te = world.getTileEntity(x, y, z);
            if (!(te instanceof ic2.tileentity.TileEntityElectricBlock)) {
                check("batbox charge result TE", false, te == null ? "null" : te.getClass().getSimpleName());
                return;
            }
            ic2.tileentity.TileEntityElectricBlock box = (ic2.tileentity.TileEntityElectricBlock)te;
            ItemStack drill = box.getItem(0);
            check("batbox drill present", drill != null && drill.getItem() == IC2Items.miningDrill, String.valueOf(drill));
            int meta = drill == null ? -1 : drill.getMetadata();
            check("batbox charged drill", drill != null && meta < drill.getMaxDamage() + 1, "meta=" + meta + "/" + (drill == null ? -1 : drill.getMaxDamage() + 1));
            ItemStack bat = box.getItem(1);
            check("batbox battery drained into box", bat == null || bat.getMetadata() > 0, "meta=" + (bat == null ? -1 : bat.getMetadata()));
        }
        catch (Throwable t) {
            check("batbox charge results", false, String.valueOf(t));
        }
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

    private static void testElectricTrommel(World world, int x, int y, int z) {
        try {
            place(world, x, y, z, "electric_trommel", IC2Blocks.electricTrommel);
            world.setBlockDataNotify(new TilePos(x, y, z), net.minecraft.core.util.helper.Direction.SOUTH.id);
            TileEntity te = world.getTileEntity(x, y, z);
            check("electric trommel TE", te instanceof TileEntityElectricTrommel, te == null ? "null" : te.getClass().getSimpleName());
            if (!(te instanceof TileEntityElectricTrommel)) {
                return;
            }
            RecipeEntryTrommel first = null;
            for (RecipeEntryTrommel r : net.minecraft.core.data.registry.Registries.RECIPES.getAllTrommelRecipes()) {
                first = r;
                break;
            }
            check("trommel recipes present", first != null, String.valueOf(first));
            if (first == null) {
                return;
            }
            java.util.List<ItemStack> stacks = ((RecipeSymbol)first.getInput()).resolve();
            check("trommel input resolvable", stacks != null && !stacks.isEmpty(), String.valueOf(stacks == null ? "null" : stacks.size()));
            if (stacks == null || stacks.isEmpty()) {
                return;
            }
            ItemStack input = stacks.get(0).copy();
            input.stackSize = 16;
            TileEntityElectricTrommel trommel = (TileEntityElectricTrommel)te;
            for (int slot : TileEntityElectricTrommel.SLOTS_INGREDIENT) {
                trommel.setItem(slot, input.copy());
            }
            trommel.setItem(TileEntityElectricTrommel.SLOT_ENERGY, new ItemStack(IC2Items.batteryRE));
            TilePos front = new TilePos(x, y, z).add(net.minecraft.core.util.helper.Direction.SOUTH);
            place(world, front.x(), front.y(), front.z(), "trommel_output_chest", Blocks.CHEST_PLANKS_OAK);
            log("electric trommel armed: " + input.toString() + " x64 + battery, chest at front");
        }
        catch (Throwable t) {
            check("electric trommel arm", false, String.valueOf(t));
        }
    }

    private static void testElectricTrommelResults(World world, int x, int y, int z) {
        try {
            TileEntity te = world.getTileEntity(x, y, z);
            if (!(te instanceof TileEntityElectricTrommel)) {
                check("electric trommel result TE", false, te == null ? "null" : te.getClass().getSimpleName());
                return;
            }
            TileEntityElectricTrommel trommel = (TileEntityElectricTrommel)te;
            int remaining = 0;
            for (int slot : TileEntityElectricTrommel.SLOTS_INGREDIENT) {
                ItemStack s = trommel.getItem(slot);
                if (s != null) {
                    remaining += s.stackSize;
                }
            }
            check("electric trommel consumed input", remaining < 64, "remaining=" + remaining + " energy=" + trommel.energy);
            TilePos front = new TilePos(x, y, z).add(net.minecraft.core.util.helper.Direction.SOUTH);
            int output = 0;
            Container chest = BlockLogicChest.getInventory(world, front);
            if (chest != null) {
                for (int i = 0; i < chest.getContainerSize(); ++i) {
                    ItemStack s = chest.getItem(i);
                    if (s != null) {
                        output += s.stackSize;
                    }
                }
            }
            check("electric trommel output in chest", output > 0, "output=" + output);
        }
        catch (Throwable t) {
            check("electric trommel results", false, String.valueOf(t));
        }
    }

    private static void testElectricBlastFurnace(World world, int x, int y, int z) {
        try {
            place(world, x, y, z, "electric_blast_furnace", IC2Blocks.electricBlastFurnace);
            TileEntity te = world.getTileEntity(x, y, z);
            check("electric blast furnace TE", te instanceof TileEntityElectricBlastFurnace, te == null ? "null" : te.getClass().getSimpleName());
            if (!(te instanceof TileEntityElectricBlastFurnace)) {
                return;
            }
            java.util.List<RecipeEntryBlastFurnace> all = net.minecraft.core.data.registry.Registries.RECIPES.getAllBlastFurnaceRecipes();
            check("blast furnace recipes present", all != null && !all.isEmpty(), String.valueOf(all == null ? 0 : all.size()));
            RecipeEntryBlastFurnace steel = null;
            RecipeEntryBlastFurnace any = null;
            for (RecipeEntryBlastFurnace r : all) {
                if (any == null) {
                    any = r;
                }
                ItemStack out = (ItemStack)r.getOutput();
                if (out != null && out.getItem() == net.minecraft.core.item.Items.INGOT_STEEL_CRUDE) {
                    steel = r;
                }
            }
            check("blast furnace steel recipe present", steel != null, String.valueOf(any));
            RecipeEntryBlastFurnace use = steel != null ? steel : any;
            if (use == null) {
                return;
            }
            net.minecraft.core.data.registry.recipe.RecipeSymbol[] inputs = (net.minecraft.core.data.registry.recipe.RecipeSymbol[])use.getInput();
            TileEntityElectricBlastFurnace furnace = (TileEntityElectricBlastFurnace)te;
            ItemStack leftInput = inputs[0].resolve().get(0).copy();
            leftInput.stackSize = 16;
            furnace.setItem(TileEntityElectricBlastFurnace.SLOT_LEFT, leftInput);
            if (inputs.length > 1) {
                ItemStack rightInput = inputs[1].resolve().get(0).copy();
                rightInput.stackSize = 16;
                furnace.setItem(TileEntityElectricBlastFurnace.SLOT_RIGHT, rightInput);
            }
            furnace.setItem(TileEntityElectricBlastFurnace.SLOT_ENERGY, new ItemStack(IC2Items.batteryRE));
            log("electric blast furnace armed: " + use.getInput().toString() + " -> " + String.valueOf((ItemStack)use.getOutput()));
        }
        catch (Throwable t) {
            check("electric blast furnace arm", false, String.valueOf(t));
        }
    }

    private static void testElectricBlastFurnaceResults(World world, int x, int y, int z) {
        try {
            TileEntity te = world.getTileEntity(x, y, z);
            if (!(te instanceof TileEntityElectricBlastFurnace)) {
                check("electric blast furnace result TE", false, te == null ? "null" : te.getClass().getSimpleName());
                return;
            }
            TileEntityElectricBlastFurnace furnace = (TileEntityElectricBlastFurnace)te;
            ItemStack left = furnace.getItem(TileEntityElectricBlastFurnace.SLOT_LEFT);
            ItemStack right = furnace.getItem(TileEntityElectricBlastFurnace.SLOT_RIGHT);
            int remaining = (left == null ? 0 : left.stackSize) + (right == null ? 0 : right.stackSize);
            check("electric blast furnace consumed input", remaining < 32, "remaining=" + remaining + " energy=" + furnace.energy);
            ItemStack out = furnace.getItem(TileEntityElectricBlastFurnace.SLOT_RESULT);
            check("electric blast furnace output produced", out != null && out.stackSize > 0, String.valueOf(out));
        }
        catch (Throwable t) {
            check("electric blast furnace results", false, String.valueOf(t));
        }
    }

    private static void testRotaryMacerator(World world, int x, int y, int z) {
        try {
            place(world, x, y, z, "rotary_macerator", IC2Blocks.rotaryMacerator);
            TileEntity te = world.getTileEntity(x, y, z);
            check("rotary macerator TE", te instanceof TileEntityRotaryMacerator, te == null ? "null" : te.getClass().getSimpleName());
            if (!(te instanceof TileEntityRotaryMacerator)) {
                return;
            }
            check("rotary exclusive recipes present", TileEntityRotaryMacerator.RECIPES.size() >= 3, String.valueOf(TileEntityRotaryMacerator.RECIPES.size()));
            check("rotary ice recipe", TileEntityRotaryMacerator.RECIPES.get(Blocks.ICE.id()) != null, String.valueOf(TileEntityRotaryMacerator.RECIPES.get(Blocks.ICE.id())));
            TileEntityRotaryMacerator rotary = (TileEntityRotaryMacerator)te;
            ItemStack iron = new ItemStack(IC2Items.ingotRefinedIron, 16);
            rotary.setItem(TileEntityAdvMachine.SLOT_INPUT, iron);
            rotary.setItem(TileEntityAdvMachine.SLOT_ENERGY, new ItemStack(IC2Items.batteryLamaCrystal));
            check("rotary can operate from macerator recipes", rotary.canOperate(), String.valueOf(rotary.getResultFor(iron)));
            log("rotary macerator armed: refined iron x16 + battery");
        }
        catch (Throwable t) {
            check("rotary macerator arm", false, String.valueOf(t));
        }
    }

    private static void testRotaryMaceratorResults(World world, int x, int y, int z) {
        try {
            TileEntity te = world.getTileEntity(x, y, z);
            if (!(te instanceof TileEntityRotaryMacerator)) {
                check("rotary macerator result TE", false, te == null ? "null" : te.getClass().getSimpleName());
                return;
            }
            TileEntityRotaryMacerator rotary = (TileEntityRotaryMacerator)te;
            ItemStack in = rotary.getItem(TileEntityAdvMachine.SLOT_INPUT);
            int remaining = in == null ? 0 : in.stackSize;
            check("rotary macerator consumed input", remaining < 16, "remaining=" + remaining + " speed=" + rotary.speed + " energy=" + rotary.energy);
            ItemStack out = rotary.getItem(TileEntityAdvMachine.SLOT_OUTPUT);
            check("rotary macerator output produced", out != null && out.stackSize > 0, String.valueOf(out));
            check("rotary macerator output is dust", out != null && out.getItem() == IC2Items.dustIron, String.valueOf(out));
        }
        catch (Throwable t) {
            check("rotary macerator results", false, String.valueOf(t));
        }
    }

    private static void testSingularityCompressor(World world, int x, int y, int z) {
        try {
            place(world, x, y, z, "singularity_compressor", IC2Blocks.singularityCompressor);
            TileEntity te = world.getTileEntity(x, y, z);
            check("singularity compressor TE", te instanceof TileEntitySingularityCompressor, te == null ? "null" : te.getClass().getSimpleName());
            if (!(te instanceof TileEntitySingularityCompressor)) {
                return;
            }
            check("singularity exclusive recipes present", TileEntitySingularityCompressor.RECIPES.size() >= 2, String.valueOf(TileEntitySingularityCompressor.RECIPES.size()));
            TileEntitySingularityCompressor singularity = (TileEntitySingularityCompressor)te;
            ItemStack snowball = new ItemStack(net.minecraft.core.item.Items.AMMO_SNOWBALL, 16);
            singularity.setItem(TileEntityAdvMachine.SLOT_INPUT, snowball);
            singularity.setItem(TileEntityAdvMachine.SLOT_ENERGY, new ItemStack(IC2Items.batteryLamaCrystal));
            ItemStack want = singularity.getResultFor(snowball);
            check("singularity snowball exclusive recipe", want != null && want.getItem() == Blocks.BLOCK_SNOW.asItem(), String.valueOf(want));
            check("singularity can operate", singularity.canOperate(), String.valueOf(want));
            log("singularity compressor armed: snowball x16 + battery");
        }
        catch (Throwable t) {
            check("singularity compressor arm", false, String.valueOf(t));
        }
    }

    private static void testSingularityCompressorResults(World world, int x, int y, int z) {
        try {
            TileEntity te = world.getTileEntity(x, y, z);
            if (!(te instanceof TileEntitySingularityCompressor)) {
                check("singularity compressor result TE", false, te == null ? "null" : te.getClass().getSimpleName());
                return;
            }
            TileEntitySingularityCompressor singularity = (TileEntitySingularityCompressor)te;
            ItemStack in = singularity.getItem(TileEntityAdvMachine.SLOT_INPUT);
            int remaining = in == null ? 0 : in.stackSize;
            ItemStack probe = in == null ? new ItemStack(net.minecraft.core.item.Items.AMMO_SNOWBALL) : in;
            ItemStack want = singularity.getResultFor(probe);
            check("singularity compressor consumed input", remaining < 16, "remaining=" + remaining + " speed=" + singularity.speed + " energy=" + singularity.energy + " progress=" + singularity.progress + " canOp=" + singularity.canOperate() + " want=" + want + " batMeta=" + (singularity.getItem(1) == null ? -1 : singularity.getItem(1).getMetadata()));
            ItemStack out = singularity.getItem(TileEntityAdvMachine.SLOT_OUTPUT);
            check("singularity compressor output produced", out != null && out.stackSize > 0, String.valueOf(out));
            check("singularity compressor output is snow", out != null && out.getItem() == Blocks.BLOCK_SNOW.asItem(), String.valueOf(out));
        }
        catch (Throwable t) {
            check("singularity compressor results", false, String.valueOf(t));
        }
    }

    private static void testWeatherController(World world, int x, int y, int z) {
        try {
            place(world, x, y, z, "weather_controller", IC2Blocks.weatherController);
            TileEntity te = world.getTileEntity(x, y, z);
            check("weather controller TE", te instanceof TileEntityWeatherController, te == null ? "null" : te.getClass().getSimpleName());
            if (!(te instanceof TileEntityWeatherController)) {
                return;
            }
            TileEntityWeatherController wc = (TileEntityWeatherController)te;
            check("weather controller tier", wc.maxInput == 4096, String.valueOf(wc.maxInput));
            check("weather controller buffer", wc.maxEnergy == 10000000, String.valueOf(wc.maxEnergy));
            log("weather controller armed");
        }
        catch (Throwable t) {
            check("weather controller arm", false, String.valueOf(t));
        }
    }

    private static void testWeatherControllerResults(World world, int x, int y, int z) {
        try {
            TileEntity te = world.getTileEntity(x, y, z);
            if (!(te instanceof TileEntityWeatherController)) {
                check("weather controller result TE", false, te == null ? "null" : te.getClass().getSimpleName());
                return;
            }
            TileEntityWeatherController wc = (TileEntityWeatherController)te;
            wc.energy = TileEntityWeatherController.COST_CLEAR + TileEntityWeatherController.COST_STORM;
            wc.handleEvent(TileEntityWeatherController.EVENT_STORM);
            check("storm purchased", wc.energy == TileEntityWeatherController.COST_CLEAR && world.getWeatherManager().getCurrentWeather() == net.minecraft.core.world.weather.Weathers.OVERWORLD_STORM, "energy=" + wc.energy + " weather=" + world.getWeatherManager().getCurrentWeather());
            wc.handleEvent(TileEntityWeatherController.EVENT_CLEAR);
            check("clear purchased", wc.energy == 0 && world.getWeatherManager().getCurrentWeather() == net.minecraft.core.world.weather.Weathers.OVERWORLD_CLEAR, "energy=" + wc.energy + " weather=" + world.getWeatherManager().getCurrentWeather());
            wc.handleEvent(TileEntityWeatherController.EVENT_RAIN);
            check("rain refused when empty", wc.energy == 0 && world.getWeatherManager().getCurrentWeather() == net.minecraft.core.world.weather.Weathers.OVERWORLD_CLEAR, "energy=" + wc.energy + " weather=" + world.getWeatherManager().getCurrentWeather());
        }
        catch (Throwable t) {
            check("weather controller results", false, String.valueOf(t));
        }
    }

    private static void testNewMachines(World world, int x, int y, int z) {
        try {
            place(world, x, y, z, "sprinkler", IC2Blocks.sprinkler);
            TileEntity sprinklerTe = world.getTileEntity(x, y, z);
            check("sprinkler TE", sprinklerTe instanceof ic2.tileentity.TileEntitySprinkler, sprinklerTe == null ? "null" : sprinklerTe.getClass().getSimpleName());
            if (sprinklerTe instanceof ic2.tileentity.TileEntitySprinkler) {
                ic2.tileentity.TileEntitySprinkler sprinkler = (ic2.tileentity.TileEntitySprinkler)sprinklerTe;
                check("sprinkler tier", sprinkler.maxInput == 32, String.valueOf(sprinkler.maxInput));
                check("sprinkler buffer", sprinkler.maxEnergy == 5000, String.valueOf(sprinkler.maxEnergy));
                sprinkler.energy = 5000;
            }
            world.setBlockWithNotify(x, y - 1, z + 1, net.minecraft.core.block.Blocks.FARMLAND_DIRT.id());
            world.setBlockMetadataWithNotify(x, y - 1, z + 1, 15);
            world.setBlockWithNotify(x + 1, y - 1, z + 1, net.minecraft.core.block.Blocks.FARMLAND_DIRT.id());
            world.setBlockMetadataWithNotify(x + 1, y - 1, z + 1, 15);
            log("farmland T+0: idA=" + world.getBlockId(x, y - 1, z + 1) + " dataA=" + world.getBlockData(new TilePos(x, y - 1, z + 1)) + " idB=" + world.getBlockId(x + 1, y - 1, z + 1) + " dataB=" + world.getBlockData(new TilePos(x + 1, y - 1, z + 1)) + " underA=" + world.getBlockId(x, y - 2, z + 1) + " sprinkler=" + world.getBlockId(x, y, z) + " sprinklerTE=" + (world.getTileEntity(x, y, z) != null));
            place(world, x, y, z - 2, "harvester", IC2Blocks.harvester);
            try {
                Thread.sleep(1200L);
            }
            catch (InterruptedException e) {
                return;
            }
            log("farmland T+1.2s: idA=" + world.getBlockId(x, y - 1, z + 1) + " dataA=" + world.getBlockData(new TilePos(x, y - 1, z + 1)) + " idB=" + world.getBlockId(x + 1, y - 1, z + 1) + " dataB=" + world.getBlockData(new TilePos(x + 1, y - 1, z + 1)) + " aboveA=" + world.getBlockId(x, y, z + 1) + " aboveB=" + world.getBlockId(x + 1, y, z + 1));
            try {
                Thread.sleep(1300L);
            }
            catch (InterruptedException e) {
                return;
            }
            log("sprinkler diag T+2.5s: idA=" + world.getBlockId(x, y - 1, z + 1) + " idB=" + world.getBlockId(x + 1, y - 1, z + 1) + " dataA=" + world.getBlockData(new TilePos(x, y - 1, z + 1)) + " dataB=" + world.getBlockData(new TilePos(x + 1, y - 1, z + 1)));
            TileEntity harvesterTe = world.getTileEntity(x, y, z - 2);
            check("harvester TE", harvesterTe instanceof ic2.tileentity.TileEntityHarvester, harvesterTe == null ? "null" : harvesterTe.getClass().getSimpleName());
            if (harvesterTe instanceof ic2.tileentity.TileEntityHarvester) {
                ic2.tileentity.TileEntityHarvester harvester = (ic2.tileentity.TileEntityHarvester)harvesterTe;
                check("harvester slots", harvester.getContainerSize() == 10, String.valueOf(harvester.getContainerSize()));
                check("harvester tier", harvester.maxInput == 128, String.valueOf(harvester.maxInput));
                check("harvester battery slot 9", ic2.tileentity.TileEntityHarvester.SLOT_ENERGY == 9, String.valueOf(ic2.tileentity.TileEntityHarvester.SLOT_ENERGY));
                harvester.energy = 20000;
            }
            world.setBlockWithNotify(x + 1, y - 1, z - 2, net.minecraft.core.block.Blocks.PUMPKIN.id());
            place(world, x, y, z - 4, "capacitor_bank_a", IC2Blocks.capacitorBank);
            place(world, x + 1, y, z - 4, "capacitor_bank_b", IC2Blocks.capacitorBank);
            TileEntity bankATe = world.getTileEntity(x, y, z - 4);
            TileEntity bankBTe = world.getTileEntity(x + 1, y, z - 4);
            check("capacitor bank A TE", bankATe instanceof ic2.tileentity.TileEntityCapacitorBank, bankATe == null ? "null" : bankATe.getClass().getSimpleName());
            check("capacitor bank B TE", bankBTe instanceof ic2.tileentity.TileEntityCapacitorBank, bankBTe == null ? "null" : bankBTe.getClass().getSimpleName());
            if (bankATe instanceof ic2.tileentity.TileEntityCapacitorBank && bankBTe instanceof ic2.tileentity.TileEntityCapacitorBank) {
                ic2.tileentity.TileEntityCapacitorBank bankA = (ic2.tileentity.TileEntityCapacitorBank)bankATe;
                ic2.tileentity.TileEntityCapacitorBank bankB = (ic2.tileentity.TileEntityCapacitorBank)bankBTe;
                check("iesu output", bankA.output == ic2.IC2Config.iesuOutput(), String.valueOf(bankA.output));
                check("iesu storage", bankA.maxStorage == ic2.IC2Config.iesuMaxStorage(), String.valueOf(bankA.maxStorage));
                check("iesu tier V", ic2.util.IC2EnergyTiers.tierOf(bankA.output) == 5, ic2.util.IC2EnergyTiers.roman(ic2.util.IC2EnergyTiers.tierOf(bankA.output)));
                bankA.energy = 1000000;
                bankB.energy = 3000000;
            }
            place(world, x, y, z - 6, "drone_depot_a", IC2Blocks.droneDepot);
            place(world, x + 3, y, z - 6, "drone_depot_b", IC2Blocks.droneDepot);
            place(world, x + 6, y, z - 6, "drone_depot_c", IC2Blocks.droneDepot);
            place(world, x + 9, y, z - 6, "drone_depot_d", IC2Blocks.droneDepot);
            place(world, x, y, z - 9, "dispatcher_terminal", IC2Blocks.dispatcherTerminal);
            TileEntity depotATe = world.getTileEntity(x, y, z - 6);
            TileEntity depotBTe = world.getTileEntity(x + 3, y, z - 6);
            TileEntity depotCTe = world.getTileEntity(x + 6, y, z - 6);
            TileEntity depotDTe = world.getTileEntity(x + 9, y, z - 6);
            TileEntity termTe = world.getTileEntity(x, y, z - 9);
            check("drone depot A TE", depotATe instanceof ic2.tileentity.TileEntityDroneDepot, depotATe == null ? "null" : depotATe.getClass().getSimpleName());
            check("drone depot B TE", depotBTe instanceof ic2.tileentity.TileEntityDroneDepot, depotBTe == null ? "null" : depotBTe.getClass().getSimpleName());
            check("drone depot C TE", depotCTe instanceof ic2.tileentity.TileEntityDroneDepot, depotCTe == null ? "null" : depotCTe.getClass().getSimpleName());
            check("drone depot D TE", depotDTe instanceof ic2.tileentity.TileEntityDroneDepot, depotDTe == null ? "null" : depotDTe.getClass().getSimpleName());
            check("dispatcher terminal TE", termTe instanceof ic2.tileentity.TileEntityDispatcherTerminal, termTe == null ? "null" : termTe.getClass().getSimpleName());
            if (depotATe instanceof ic2.tileentity.TileEntityDroneDepot && depotBTe instanceof ic2.tileentity.TileEntityDroneDepot) {
                ic2.tileentity.TileEntityDroneDepot depotA = (ic2.tileentity.TileEntityDroneDepot)depotATe;
                ic2.tileentity.TileEntityDroneDepot depotB = (ic2.tileentity.TileEntityDroneDepot)depotBTe;
                depotA.owner = "TestOwner";
                depotB.owner = "TestOwner";
                check("drone depot energy sink", depotA.demandsEnergy() && depotA.acceptsEnergyFrom(null, ic2.energy.Direction.YP), "demands+accepts");
                int back = depotA.injectEnergy(ic2.energy.Direction.YN, 32);
                check("drone depot accepts EU", back == 0 && depotA.energy == 32, "back=" + back + " energy=" + depotA.energy);
                depotA.handleEvent(ic2.tileentity.TileEntityDroneDepot.EVENT_CHANNEL_UP);
                depotB.handleEvent(ic2.tileentity.TileEntityDroneDepot.EVENT_CHANNEL_UP);
                check("drone depots on channel 1", depotA.channel == 1 && depotB.channel == 1, "A=" + depotA.channel + " B=" + depotB.channel);
                depotA.handleEvent(ic2.tileentity.TileEntityDroneDepot.EVENT_CHANNEL_DOWN);
                check("drone depot channel down", depotA.channel == 0, String.valueOf(depotA.channel));
                depotA.handleEvent(ic2.tileentity.TileEntityDroneDepot.EVENT_CHANNEL_UP);
                depotA.setItem(0, new ItemStack(IC2Items.circuit, 2));
                depotA.setItem(1, new ItemStack(IC2Items.circuit, 2));
            }
            if (depotCTe instanceof ic2.tileentity.TileEntityDroneDepot) {
                ic2.tileentity.TileEntityDroneDepot depotC = (ic2.tileentity.TileEntityDroneDepot)depotCTe;
                depotC.owner = "TestOwner";
                depotC.handleEvent(ic2.tileentity.TileEntityDroneDepot.EVENT_CHANNEL_UP);
            }
            if (depotDTe instanceof ic2.tileentity.TileEntityDroneDepot) {
                ic2.tileentity.TileEntityDroneDepot depotD = (ic2.tileentity.TileEntityDroneDepot)depotDTe;
                depotD.owner = "Alex";
                depotD.handleEvent(ic2.tileentity.TileEntityDroneDepot.EVENT_CHANNEL_UP);
            }
            ic2.util.DroneAccessSavedData grants = ic2.util.DroneAccessSavedData.get(world);
            if (grants != null) {
                grants.grant("TestOwner", "Alex");
                check("drone grant saved", grants.hasGrant("TestOwner", "Alex"), String.valueOf(grants.hasGrant("TestOwner", "Alex")));
                check("drone revoke saved", grants.revoke("TestOwner", "Alex"), "revoke failed");
                check("drone revoke cleared", !grants.hasGrant("TestOwner", "Alex"), "still granted");
            }
            placeCable(world, x, y + 1, z - 14, "superconductor_cable", 12);
            TileEntity cableTe = world.getTileEntity(x, y + 1, z - 14);
            check("superconductor cable TE", cableTe instanceof TileEntityCable, cableTe == null ? "null" : cableTe.getClass().getSimpleName());
            if (cableTe instanceof TileEntityCable) {
                TileEntityCable cable = (TileEntityCable)cableTe;
                check("superconductor zero loss", cable.getConductionLoss() == 0.0, String.valueOf(cable.getConductionLoss()));
                check("superconductor limit 16384", cable.getConductorBreakdownEnergy() == 16385 && cable.getInsulationEnergyAbsorption() == 16384, "breakdown=" + cable.getConductorBreakdownEnergy() + " insulation=" + cable.getInsulationEnergyAbsorption());
            }
            check("superconductor cable item", IC2Items.cableItems[12] != null && ((ic2.item.ItemCablePlaceable)IC2Items.cableItems[12]).getCableType() == 12, String.valueOf(IC2Items.cableItems[12]));
            log("new machines armed: sprinkler 5000 EU, harvester 20000 EU + pumpkin, banks 1M/3M, depots channel 1 + circuit x2");
        }
        catch (Throwable t) {
            check("new machines arm", false, String.valueOf(t));
        }
    }

    private static void testNewMachinesResults(World world, int x, int y, int z) {
        try {
            TileEntity sprinklerTe = world.getTileEntity(x, y, z);
            if (sprinklerTe instanceof ic2.tileentity.TileEntitySprinkler) {
                ic2.tileentity.TileEntitySprinkler sprinkler = (ic2.tileentity.TileEntitySprinkler)sprinklerTe;
                check("sprinkler consumed EU", sprinkler.energy < 5000, "energy=" + sprinkler.energy);
                int soilA = world.getBlockData(new TilePos(x, y - 1, z + 1));
                int soilB = world.getBlockData(new TilePos(x + 1, y - 1, z + 1));
                int idA = world.getBlockId(x, y - 1, z + 1);
                int idB = world.getBlockId(x + 1, y - 1, z + 1);
                boolean farmland = idA == net.minecraft.core.block.Blocks.FARMLAND_DIRT.id() && idB == net.minecraft.core.block.Blocks.FARMLAND_DIRT.id();
                check("sprinkler hydrated soil", farmland && soilA >= 12 && soilB >= 12, "A=" + soilA + " B=" + soilB + " idA=" + idA + " idB=" + idB);
            } else {
                check("sprinkler alive", false, "tile gone");
            }
            TileEntity harvesterTe = world.getTileEntity(x, y, z - 2);
            if (harvesterTe instanceof ic2.tileentity.TileEntityHarvester) {
                ic2.tileentity.TileEntityHarvester harvester = (ic2.tileentity.TileEntityHarvester)harvesterTe;
                ItemStack out = harvester.getItem(0);
                if (out == null) {
                    out = harvester.getItem(1);
                }
                check("harvester collected pumpkin", out != null && out.stackSize > 0, "out=" + String.valueOf(out));
                int cropBlock = world.getBlockId(x + 1, y - 1, z - 2);
                check("harvester replanted pumpkin crop", cropBlock == net.minecraft.core.block.Blocks.CROPS_PUMPKIN.id(), "block=" + cropBlock);
                check("harvester paid energy", harvester.energy < 20000, "energy=" + harvester.energy);
            } else {
                check("harvester alive", false, "tile gone");
            }
            TileEntity bankATe = world.getTileEntity(x, y, z - 4);
            TileEntity bankBTe = world.getTileEntity(x + 1, y, z - 4);
            if (bankATe instanceof ic2.tileentity.TileEntityCapacitorBank && bankBTe instanceof ic2.tileentity.TileEntityCapacitorBank) {
                ic2.tileentity.TileEntityCapacitorBank bankA = (ic2.tileentity.TileEntityCapacitorBank)bankATe;
                ic2.tileentity.TileEntityCapacitorBank bankB = (ic2.tileentity.TileEntityCapacitorBank)bankBTe;
                check("iesu energy conserved", bankA.energy + bankB.energy >= 3999000 && bankA.energy + bankB.energy <= 4000000, "sum=" + (bankA.energy + bankB.energy));
            } else {
                check("iesu alive", false, "tiles gone");
            }
        }
        catch (Throwable t) {
            check("new machines results", false, String.valueOf(t));
        }
    }

    private static void testDroneSend(World world, int x, int y, int z) {
        try {
            TileEntity depotATe = world.getTileEntity(x, y, z);
            if (depotATe instanceof ic2.tileentity.TileEntityDroneDepot) {
                ic2.tileentity.TileEntityDroneDepot depotA = (ic2.tileentity.TileEntityDroneDepot)depotATe;
                int before = depotA.energy;
                if (before < ic2.tileentity.TileEntityDroneDepot.SEND_COST) {
                    depotA.energy = 50000;
                    before = 50000;
                }
                runOnServer(() -> depotA.handleEvent(ic2.tileentity.TileEntityDroneDepot.EVENT_SEND));
                check("drone send paid EU", depotA.energy == before - ic2.tileentity.TileEntityDroneDepot.SEND_COST, "before=" + before + " energy=" + depotA.energy);
                dumpDrones(world, "drones right after send");
                java.util.List<ic2.net.TerminalDataMessage.DroneView> views = ic2.net.TerminalDataMessage.scan(world, "TestOwner");
                check("terminal sees own drones", views.size() == 2, "views=" + views.size());
                java.util.List<ic2.net.TerminalDataMessage.DroneView> foreign = ic2.net.TerminalDataMessage.scan(world, "Alex");
                check("terminal hides foreign drones", foreign.isEmpty(), "views=" + foreign.size());
            }
        }
        catch (Throwable t) {
            check("drone send", false, String.valueOf(t));
        }
    }

    private static void dumpDrones(World world, String tag) {
        int n = 0;
        StringBuilder sb = new StringBuilder();
        for (Object o : world.entities) {
            if (o instanceof ic2.entity.EntityDrone) {
                ic2.entity.EntityDrone d = (ic2.entity.EntityDrone)o;
                sb.append("id=").append(d.id).append(" phase=").append(d.phase).append(" pos=").append((int)d.x).append(",").append((int)d.y).append(",").append((int)d.z).append(" payload=").append(d.payload.size()).append("; ");
                ++n;
            }
        }
        log(tag + ": drones=" + n + " [" + sb + "]");
    }

    private static void testDroneResults(World world, int x, int y, int z) {
        try {
            TileEntity depotATe = world.getTileEntity(x, y, z);
            TileEntity depotBTe = world.getTileEntity(x + 3, y, z);
            TileEntity depotCTe = world.getTileEntity(x + 6, y, z);
            TileEntity depotDTe = world.getTileEntity(x + 9, y, z);
            if (depotATe instanceof ic2.tileentity.TileEntityDroneDepot && depotBTe instanceof ic2.tileentity.TileEntityDroneDepot && depotCTe instanceof ic2.tileentity.TileEntityDroneDepot && depotDTe instanceof ic2.tileentity.TileEntityDroneDepot) {
                ic2.tileentity.TileEntityDroneDepot depotA = (ic2.tileentity.TileEntityDroneDepot)depotATe;
                ic2.tileentity.TileEntityDroneDepot depotB = (ic2.tileentity.TileEntityDroneDepot)depotBTe;
                ic2.tileentity.TileEntityDroneDepot depotC = (ic2.tileentity.TileEntityDroneDepot)depotCTe;
                ic2.tileentity.TileEntityDroneDepot depotD = (ic2.tileentity.TileEntityDroneDepot)depotDTe;
                check("drone depot A emptied", depotA.getItem(0) == null && depotA.getItem(1) == null, "slots=" + String.valueOf(depotA.getItem(0)) + "/" + String.valueOf(depotA.getItem(1)));
                dumpDrones(world, "drones at results");
                int dropped = 0;
                for (Object o : world.entities) {
                    if (o instanceof net.minecraft.core.entity.EntityItem) {
                        net.minecraft.core.entity.EntityItem ei = (net.minecraft.core.entity.EntityItem)o;
                        if (ei.item != null && ei.item.getItem() == IC2Items.circuit && Math.abs(ei.x - (double)x) < 16 && Math.abs(ei.z - (double)z) < 16) {
                            ++dropped;
                        }
                    }
                }
                log("circuits dropped near depots: " + dropped);
                ItemStack arrived = depotB.getItem(0);
                check("drone delivered to B", arrived != null && arrived.stackSize == 2 && arrived.getItem() == IC2Items.circuit, "slot0=" + String.valueOf(arrived));
                ItemStack arrivedC = depotC.getItem(0);
                check("drone delivered equal share to C", arrivedC != null && arrivedC.stackSize == 2 && arrivedC.getItem() == IC2Items.circuit, "slot0=" + String.valueOf(arrivedC));
                ItemStack foreign = depotD.getItem(0);
                check("foreign depot excluded from channel", foreign == null, "slot0=" + String.valueOf(foreign));
                org.joml.primitives.AABBd box = new org.joml.primitives.AABBd((double)x - 8.0, (double)y - 8.0, (double)z - 8.0, (double)x + 11.0, (double)y + 8.0, (double)z + 8.0);
                java.util.List<ic2.entity.EntityDrone> drones = world.getEntitiesWithinAABB(ic2.entity.EntityDrone.class, box);
                check("drone completed flight", drones.isEmpty(), "drones=" + drones.size());
                check("terminal scan empty after delivery", ic2.net.TerminalDataMessage.scan(world, "TestOwner").isEmpty(), "drones in flight");
            } else {
                check("drone depots alive", false, "tiles gone");
            }
        }
        catch (Throwable t) {
            check("drone results", false, String.valueOf(t));
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

    private static void logBatpackToolChargeChecks() {
        try {
            net.minecraft.core.world.World world = null;
            MinecraftServer server = MinecraftServer.getInstance();
            if (server != null && server.dimensionWorlds != null && server.dimensionWorlds.size() > 0) {
                world = (net.minecraft.core.world.World)server.dimensionWorlds.get(0);
            }
            if (world == null) {
                check("batpack tool charge path", false, "no world");
                return;
            }
            Player player = new Player(world){

                @Override
                public void animate4() {
                }

                @Override
                public void sendMessage(boolean flag, net.minecraft.core.net.command.TextFormatting.Base format, String message) {
                }

                @Override
                public void sendMessageTranslated(boolean flag, net.minecraft.core.net.command.TextFormatting.Base format, String key, String... args) {
                }

                @Override
                protected void defineSynchedData() {
                    super.defineSynchedData();
                }

                @Override
                public void readAdditionalSaveData(com.mojang.nbt.tags.CompoundTag tag) {
                }

                @Override
                public void addAdditionalSaveData(com.mojang.nbt.tags.CompoundTag tag) {
                }
            };
            ItemStack pack = new ItemStack(IC2Items.batpack);
            player.inventory.armorInventory[net.minecraft.core.enums.HumanArmorShape.CHEST.getSlotIndex()] = pack;
            ItemStack drill = new ItemStack(IC2Items.diamondDrill);
            drill.setMetadata(drill.getMaxDamage() - 1);
            int packMeta0 = pack.getMetadata();
            int drillMeta0 = drill.getMetadata();
            boolean ok = ((ic2.item.tool.ItemElectricTool)IC2Items.diamondDrill).use(drill, 1, player);
            check("tool use with batpack succeeded", ok, "ok=" + ok);
            check("batpack charged held tool during use", drill.getMetadata() < drillMeta0, "drill " + drillMeta0 + " -> " + drill.getMetadata());
            check("batpack drained from chest slot", pack.getMetadata() > packMeta0, "pack " + packMeta0 + " -> " + pack.getMetadata());
            long toolGainEu = (long)(drillMeta0 - drill.getMetadata()) * 80;
            long packSpendEu = (long)(pack.getMetadata() - packMeta0) * 100;
            check("batpack/tool energy conserved", packSpendEu >= toolGainEu && packSpendEu - toolGainEu <= 200, "toolGain=" + toolGainEu + " packSpend=" + packSpendEu);
        }
        catch (Throwable t) {
            check("batpack tool charge path", false, String.valueOf(t));
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
            Method m = MachineEventMessage.class.getDeclaredMethod("apply", World.class, net.minecraft.core.entity.player.Player.class);
            m.setAccessible(true);
            m.invoke(msg, world, null);
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
            SlotIC2Charge chargeSlot = new SlotIC2Charge(mac2, 0, 0, 0);
            ItemStack drainedDrill = new ItemStack(IC2Items.miningDrill);
            drainedDrill.setMetadata(drainedDrill.getMaxDamage() + 1);
            check("charge slot accepts drained drill", chargeSlot.mayPlace(drainedDrill), "drill");
            check("charge slot accepts re battery", chargeSlot.mayPlace(new ItemStack(IC2Items.batteryRE)), "bat");
            check("charge slot accepts batpack", chargeSlot.mayPlace(new ItemStack(IC2Items.batpack)), "pack");
            check("charge slot rejects redstone", !chargeSlot.mayPlace(new ItemStack(net.minecraft.core.item.Items.DUST_REDSTONE)), "dust");
            check("charge slot rejects cobble", !chargeSlot.mayPlace(new ItemStack(Blocks.COBBLE_STONE)), "cobble");
            check("battery slot rejects drill", !batSlot.mayPlace(drainedDrill), "drill");
        } catch (Throwable t) {
            check("battery slot restrictions", false, String.valueOf(t));
        }
    }

    private static void testEntityTracking(World world, int x, int y, int z) {
        log("entity tracking checks:");
        try {
            EntityDynamite dynamite = new EntityDynamite(world, 0.5, 0.5, 0.5, true);
            dynamite.fuse = 77;
            net.minecraft.core.net.entity.ITrackedEntry<?> trackedDynamite = net.minecraft.core.net.entity.NetEntityHandler.getTrackedEntry((Entity)dynamite);
            check("dynamite tracked entry", trackedDynamite != null, String.valueOf(trackedDynamite));
            EntityMiningLaser laser = new EntityMiningLaser(world);
            net.minecraft.core.net.entity.ITrackedEntry<?> trackedLaser = net.minecraft.core.net.entity.NetEntityHandler.getTrackedEntry((Entity)laser);
            check("laser tracked entry", trackedLaser != null, String.valueOf(trackedLaser));
            EntityIC2Explosive explosive = new EntityIC2Explosive(world);
            net.minecraft.core.net.entity.ITrackedEntry<?> trackedExplosive = net.minecraft.core.net.entity.NetEntityHandler.getTrackedEntry((Entity)explosive);
            check("explosive tracked entry", trackedExplosive != null, String.valueOf(trackedExplosive));
            EntityDrone drone = new EntityDrone(world);
            net.minecraft.core.net.entity.ITrackedEntry<?> trackedDrone = net.minecraft.core.net.entity.NetEntityHandler.getTrackedEntry((Entity)drone);
            check("drone tracked entry", trackedDrone != null, String.valueOf(trackedDrone));
            Class<?>[] expected = new Class<?>[]{EntityMiningLaser.class, EntityDynamite.class, EntityIC2Explosive.class, EntityDrone.class};
            for (int type = 4010; type <= 4013; ++type) {
                net.minecraft.core.net.entity.IVehicleEntry<?> veh = net.minecraft.core.net.entity.NetEntityHandler.getTypeEntry(type);
                check("vehicle type " + type, veh != null && veh.getAppliedClass() == expected[type - 4010], String.valueOf(veh));
            }
            CompoundTag tag = new CompoundTag();
            dynamite.addAdditionalSaveData(tag);
            Entity clientDynamite = net.minecraft.core.net.entity.NetEntityHandler.getTypeEntry(4011).getEntity(world, 1.5, 70.5, 2.5, -1, true, 0.1, 0.2, 0.3, null, tag);
            check("dynamite client factory", clientDynamite instanceof EntityDynamite && ((EntityDynamite)clientDynamite).sticky && ((EntityDynamite)clientDynamite).fuse == 77 && clientDynamite.x == 1.5, String.valueOf(clientDynamite));
            Entity clientLaser = net.minecraft.core.net.entity.NetEntityHandler.getTypeEntry(4010).getEntity(world, 2.5, 70.5, 3.5, -1, false, 0.0, 0.0, 0.0, null, null);
            check("laser client factory", clientLaser instanceof EntityMiningLaser && clientLaser.x == 2.5, String.valueOf(clientLaser));
            EntityMiningLaser dispatcherLaser = EntityDispatcher.getInstance().createEntityInWorld(EntityMiningLaser.class, world);
            check("laser dispatcher mapping", dispatcherLaser != null, String.valueOf(dispatcherLaser));
            wireRoundTrip(world, "laser", 4010, EntityMiningLaser.class, x + 6.5, y + 0.5, z + 0.5, e -> {
                EntityMiningLaser l = new EntityMiningLaser(world);
                l.setPos(x + 6.5, y + 0.5, z + 0.5);
                l.range = 12.0f;
                l.explosive = true;
                l.startX = l.x;
                l.startY = l.y;
                l.startZ = l.z;
                return l;
            }, le -> le instanceof EntityMiningLaser && ((EntityMiningLaser)le).range == 12.0f && ((EntityMiningLaser)le).explosive, null, null);
            wireRoundTrip(world, "dynamite", 4011, EntityDynamite.class, x + 4.5, y + 0.5, z + 0.5, e -> {
                EntityDynamite d = new EntityDynamite(world, x + 4.5, y + 0.5, z + 0.5, true);
                d.fuse = 77;
                return d;
            }, de -> de instanceof EntityDynamite && ((EntityDynamite)de).sticky && ((EntityDynamite)de).fuse >= 76 && ((EntityDynamite)de).fuse <= 77, null, null);
            wireRoundTrip(world, "industrial tnt", 4012, EntityIC2Explosive.class, x + 0.5, y + 0.5, z + 0.5, e -> new EntityIC2Explosive(world, x + 0.5, y + 0.5, z + 0.5, 60, 3.0f, 0.9f, 0.3f, IC2Blocks.industrialTnt), xe -> xe instanceof EntityIC2Explosive && ((EntityIC2Explosive)xe).fuse >= 58, 60, IC2Blocks.industrialTnt);
            wireRoundTrip(world, "nuke", 4012, EntityIC2Explosive.class, x + 2.5, y + 0.5, z + 0.5, e -> new EntityIC2Explosive(world, x + 2.5, y + 0.5, z + 0.5, 300, 40.0f, 0.05f, 1.5f, IC2Blocks.nuke), xe -> xe instanceof EntityIC2Explosive && ((EntityIC2Explosive)xe).fuse >= 298, 300, IC2Blocks.nuke);
            wireRoundTrip(world, "drone", 4013, EntityDrone.class, x + 8.5, y + 0.5, z + 0.5, e -> {
                EntityDrone d = new EntityDrone(world);
                d.setPos(x + 8.5, y + 0.5, z + 0.5);
                return d;
            }, de -> de instanceof EntityDrone && Math.abs(de.x - (double)(x + 8)) < 1.0, null, null);
            testPrimeEndToEnd(world, x + 10, y, z, IC2Blocks.industrialTnt, "industrial tnt");
            testPrimeEndToEnd(world, x + 12, y, z, IC2Blocks.nuke, "nuke");
        }
        catch (Throwable t) {
            check("entity tracking", false, String.valueOf(t));
        }
    }

    private interface EntityMaker {
        Entity create(int seed);
    }

    private interface EntityVerify {
        boolean verify(Entity e);
    }

    private static void wireRoundTrip(World world, String label, int expectedType, Class<?> entityClass, double sx, double sy, double sz, EntityMaker maker, EntityVerify verify, Integer expectedFuse, Block<?> expectedBlock) {
        net.minecraft.server.entity.EntityTrackerImpl tracker = MinecraftServer.getInstance().getEntityTracker(0);
        Entity serverEntity = maker.create(0);
        runOnServer(() -> world.entityJoinedWorld(serverEntity));
        try {
            net.minecraft.server.entity.EntityTrackerEntryImpl entry = (net.minecraft.server.entity.EntityTrackerEntryImpl)tracker.trackedEntityHashTable.get(serverEntity.id);
            check(label + " tracker entry", entry != null, String.valueOf(entry));
            if (entry == null) {
                return;
            }
            check(label + " tracking distance", entry.trackingDistance > 0 && entry.trackingDistance <= 160, String.valueOf(entry.trackingDistance));
            net.minecraft.core.net.packet.PacketAddEntity packet = (net.minecraft.core.net.packet.PacketAddEntity)net.minecraft.core.net.entity.NetEntityHandler.getSpawnPacket(entry);
            check(label + " packet type", packet != null && packet.type == (short)expectedType, String.valueOf(packet));
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.DataOutputStream dos = new java.io.DataOutputStream(baos);
            packet.write(dos);
            net.minecraft.core.net.packet.PacketAddEntity p2 = new net.minecraft.core.net.packet.PacketAddEntity();
            p2.read(new java.io.DataInputStream(new java.io.ByteArrayInputStream(baos.toByteArray())));
            check(label + " wire type", p2.type == (short)expectedType, String.valueOf(p2.type));
            check(label + " wire has tag", p2.tag != null, String.valueOf(p2.tag));
            Entity clientEntity = net.minecraft.core.net.entity.NetEntityHandler.getTypeEntry(p2.type).getEntity(world, p2.xPosition / 32.0, p2.yPosition / 32.0, p2.zPosition / 32.0, p2.metaData, p2.hasVelocity, p2.xVelocity / 8000.0, p2.yVelocity / 8000.0, p2.zVelocity / 8000.0, null, p2.tag);
            check(label + " client class", clientEntity != null && clientEntity.getClass() == entityClass, String.valueOf(clientEntity));
            check(label + " client pos", clientEntity != null && Math.abs(clientEntity.x - (p2.xPosition / 32.0)) < 0.01 && Math.abs(clientEntity.y - (p2.yPosition / 32.0)) < 0.01 && Math.abs(clientEntity.z - (p2.zPosition / 32.0)) < 0.01, clientEntity == null ? "-" : clientEntity.x + "," + clientEntity.y + "," + clientEntity.z);
            check(label + " client spawn pos", clientEntity != null && Math.abs(clientEntity.x - sx) < 2.0 && Math.abs(clientEntity.y - sy) < 2.0 && Math.abs(clientEntity.z - sz) < 2.0, clientEntity == null ? "-" : clientEntity.x + "," + clientEntity.y + "," + clientEntity.z);
            if (verify != null) {
                check(label + " client state", clientEntity != null && verify.verify(clientEntity), clientEntity == null ? "-" : "state");
            }
            if (expectedFuse != null && clientEntity instanceof EntityIC2Explosive) {
                int cf = ((EntityIC2Explosive)clientEntity).fuse;
                check(label + " client fuse", cf <= (int)expectedFuse && cf >= (int)expectedFuse - 3, String.valueOf(cf));
            }
            if (expectedBlock != null && clientEntity instanceof EntityIC2Explosive) {
                check(label + " client render block", ((EntityIC2Explosive)clientEntity).renderBlock == expectedBlock, String.valueOf(((EntityIC2Explosive)clientEntity).renderBlock));
            }
        }
        catch (Throwable t) {
            check(label + " wire round trip", false, String.valueOf(t));
        }
        finally {
            serverEntity.remove();
        }
    }

    private static void testPrimeEndToEnd(World world, int x, int y, int z, Block<?> block, String label) {
        try {
            place(world, x, y, z, "prime_" + label, block);
            int expectedFuse = block == IC2Blocks.nuke ? 300 : 60;
            runOnServer(() -> ((ic2.block.BlockLogicIC2Explosive)block.getLogic()).prime(world, new net.minecraft.core.world.pos.TilePos(x, y, z)));
            check(label + " block removed on prime", world.getBlockId(x, y, z) == 0, String.valueOf(world.getBlockId(x, y, z)));
            net.minecraft.server.entity.EntityTrackerImpl tracker = MinecraftServer.getInstance().getEntityTracker(0);
            EntityIC2Explosive primed = null;
            for (Object o : tracker.trackedEntityHashTable.values()) {
                net.minecraft.server.entity.EntityTrackerEntryImpl te = (net.minecraft.server.entity.EntityTrackerEntryImpl)o;
                if (te.trackedEntity instanceof EntityIC2Explosive && Math.abs(te.trackedEntity.x - ((double)x + 0.5)) < 0.5 && Math.abs(te.trackedEntity.z - ((double)z + 0.5)) < 0.5) {
                    primed = (EntityIC2Explosive)te.trackedEntity;
                    break;
                }
            }
            check(label + " prime tracked", primed != null, "not tracked");
            if (primed != null) {
                check(label + " prime fuse", primed.fuse <= expectedFuse && primed.fuse >= expectedFuse - 3, String.valueOf(primed.fuse));
                check(label + " prime render block", primed.renderBlock == block, String.valueOf(primed.renderBlock));
                primed.remove();
            }
        }
        catch (Throwable t) {
            check(label + " prime end to end", false, String.valueOf(t));
        }
    }

    private static void runOnServer(Runnable task) {
        java.util.concurrent.atomic.AtomicBoolean done = new java.util.concurrent.atomic.AtomicBoolean(false);
        ic2.util.ServerTaskQueue.post(() -> {
            try {
                task.run();
            }
            finally {
                done.set(true);
            }
        });
        for (int i = 0; i < 100 && !done.get(); ++i) {
            try {
                Thread.sleep(100L);
            }
            catch (InterruptedException e) {
                return;
            }
        }
    }

    private static void log(String s) {
        IC2.LOGGER.info("[SRV-TEST] " + s);
    }
}
