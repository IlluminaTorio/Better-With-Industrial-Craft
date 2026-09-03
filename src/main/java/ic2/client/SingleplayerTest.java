

package ic2.client;

import ic2.IC2;
import ic2.IC2Blocks;
import ic2.IC2Items;
import ic2.block.BlockLogicCable;
import ic2.block.BlockLogicRubWood;
import ic2.block.BlockModelIC2Machine;
import ic2.energy.Direction;
import ic2.energy.EnergyNet;
import ic2.energy.IEnergyAcceptor;
import ic2.energy.IEnergyConductor;
import ic2.energy.IEnergyEmitter;
import ic2.energy.IEnergySink;
import ic2.energy.IEnergySource;
import ic2.gui.IC2GuiHandler;
import ic2.gui.menu.MenuElectricMachine;
import ic2.item.ItemCablePlaceable;
import ic2.item.tool.ItemMiningLaser;
import ic2.si.SIConverters;
import ic2.tileentity.TileEntityBaseGenerator;
import ic2.tileentity.TileEntityCompressor;
import ic2.tileentity.TileEntityElectricBlock;
import ic2.tileentity.TileEntityExtractor;
import ic2.tileentity.TileEntityGenerator;
import ic2.tileentity.TileEntityIC2Block;
import ic2.tileentity.TileEntityMacerator;
import ic2.tileentity.TileEntityCable;
import ic2.tileentity.TileEntityNuclearReactor;
import ic2.tileentity.TileEntityTeleporter;
import ic2.tileentity.TileEntityTerraformer;
import ic2.util.IC2PlayerTicker;
import java.lang.invoke.CallSite;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.PlayerLocal;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.render.block.color.BlockColor;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.client.world.WorldClient;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.WeightedRandomBag;
import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.RecipeEntryBase;
import net.minecraft.core.data.registry.recipe.RecipeGroup;
import net.minecraft.core.data.registry.recipe.RecipeRegistry;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCrafting;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryFurnace;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.enums.HumanArmorShape;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.gamemode.Gamemodes;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.HitResult;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import net.minecraft.core.world.settings.WorldConfiguration;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;
import turniplabs.halplibe.helper.RecipeBuilder;

public class SingleplayerTest {
    private static final List<String> results = new ArrayList<String>();
    private static volatile boolean finished = false;

    public static void start() {
        Thread t = new Thread(() -> {
            try {
                for (int i = 0; i < 300; ++i) {
                    SingleplayerTest.sleep(1000L);
                    Minecraft mc = Minecraft.getMinecraft();
                    if (mc != null && mc.statsCounter != null && mc.renderGlobal != null && mc.currentScreen != null) break;
                }
                SingleplayerTest.sleep(3000L);
                new SingleplayerTest().run();
            }
            catch (Throwable e) {
                IC2.LOGGER.error("[SP-TEST] FATAL: ", e);
                SingleplayerTest.finish();
            }
        }, "IC2-SP-Test");
        t.setDaemon(true);
        t.start();
    }

    private void run() {
        Minecraft mc = Minecraft.getMinecraft();
        SingleplayerTest.log("client ready, currentWorld=" + (mc.currentWorld != null));
        CountDownLatch worldCreated = new CountDownLatch(1);
        this.runOnMainThread(mc, () -> {
            try {
                WorldConfiguration cfg = new WorldConfiguration();
                cfg.setWorldName("ic2-test");
                cfg.setGamemode(Gamemodes.SURVIVAL);
                cfg.setCheatsEnabled(true);
                mc.createAndStartWorld(cfg);
                SingleplayerTest.log("world creation requested (main thread)");
            }
            catch (Throwable e) {
                SingleplayerTest.log("FAILED to create world: " + String.valueOf(e));
            }
            finally {
                worldCreated.countDown();
            }
        });
        if (!SingleplayerTest.await(worldCreated, 150)) {
            SingleplayerTest.log("FAILED: world creation action never ran");
            SingleplayerTest.finish();
            return;
        }
        for (int i = 0; i < 150 && mc.currentWorld == null; ++i) {
            SingleplayerTest.sleep(1000L);
        }
        if (mc.currentWorld == null) {
            SingleplayerTest.log("FAILED: world did not load in 60s");
            SingleplayerTest.finish();
            return;
        }
        SingleplayerTest.log("world loaded! player=" + String.valueOf(mc.thePlayer));
        SingleplayerTest.sleep(15000L);
        PlayerLocal player = mc.thePlayer;
        if (player == null) {
            SingleplayerTest.log("FAILED: player is null");
            SingleplayerTest.finish();
            return;
        }
        int px = MathHelper.floor((double)player.x);
        int py = MathHelper.floor((double)player.y);
        int pz = MathHelper.floor((double)player.z);
        SingleplayerTest.log("player at " + px + "," + py + "," + pz + " blockBelow=" + mc.currentWorld.getBlockId(px, py - 1, pz));
        CountDownLatch placed = new CountDownLatch(1);
        int fx = px;
        int fy = py;
        int fz = pz;
        this.runOnMainThread(mc, () -> {
            try {
                this.place(mc, fx + 2, fy, fz, "generator", IC2Blocks.generator);
                this.place(mc, fx + 3, fy, fz, "cable", IC2Blocks.cable);
                this.place(mc, fx + 4, fy, fz, "macerator", IC2Blocks.macerator);
                this.place(mc, fx + 2, fy, fz + 2, "iron_furnace", IC2Blocks.ironFurnace);
                this.place(mc, fx + 3, fy, fz + 2, "batbox", IC2Blocks.batBox);
                this.place(mc, fx + 4, fy, fz + 2, "extractor", IC2Blocks.extractor);
                this.place(mc, fx + 5, fy, fz, "compressor", IC2Blocks.compressor);
                this.place(mc, fx + 5, fy, fz + 2, "electric_furnace", IC2Blocks.electricFurnace);
                this.place(mc, fx + 6, fy, fz, "recycler", IC2Blocks.recycler);
                this.place(mc, fx + 6, fy, fz + 2, "wind_mill", IC2Blocks.windMill);
                this.place(mc, fx + 7, fy, fz, "solar_panel", IC2Blocks.solarPanel);
                this.place(mc, fx + 7, fy, fz + 2, "geo", IC2Blocks.geothermalGenerator);
                this.place(mc, fx + 7, fy, fz - 2, "water_mill", IC2Blocks.waterMill);
                this.place(mc, fx + 2, fy, fz - 2, "ore_copper", IC2Blocks.oreCopper);
                this.place(mc, fx + 3, fy, fz - 2, "ore_tin", IC2Blocks.oreTin);
                this.place(mc, fx + 4, fy, fz - 2, "ore_uranium", IC2Blocks.oreUranium);
                this.place(mc, fx + 5, fy, fz - 2, "rubber_wood", IC2Blocks.rubberWood);
                this.place(mc, fx + 2, fy + 2, fz - 2, "ore_copper_basalt", IC2Blocks.oreCopperBasalt);
                this.place(mc, fx + 3, fy + 2, fz - 2, "ore_copper_granite", IC2Blocks.oreCopperGranite);
                this.place(mc, fx + 4, fy + 2, fz - 2, "ore_tin_limestone", IC2Blocks.oreTinLimestone);
                this.place(mc, fx + 5, fy + 2, fz - 2, "ore_uranium_permafrost", IC2Blocks.oreUraniumPermafrost);
                this.place(mc, fx + 2, fy + 1, fz - 4, "bronze_brick", IC2Blocks.bronzeBrick);
                this.place(mc, fx + 3, fy + 1, fz - 4, "bronze_door", IC2Blocks.bronzeDoorBottom);
                SingleplayerTest.log("blocks placed");
            }
            catch (Throwable e) {
                SingleplayerTest.log("FAILED placing blocks: " + String.valueOf(e));
            }
            finally {
                placed.countDown();
            }
        });
        SingleplayerTest.await(placed, 15);
        SingleplayerTest.sleep(3000L);
        CountDownLatch advPlaced = new CountDownLatch(1);
        this.runOnMainThread(mc, () -> {
            try {
                this.place(mc, fx + 9, fy, fz, "pesu", IC2Blocks.pesu);
                this.place(mc, fx + 10, fy, fz, "plasmafier", IC2Blocks.plasmafier);
                this.place(mc, fx + 11, fy, fz, "transformer_iv", IC2Blocks.transformerIV);
                this.place(mc, fx + 12, fy, fz, "iridium_stone", IC2Blocks.iridiumStone);
                this.place(mc, fx + 9, fy, fz + 2, "slag_generator", IC2Blocks.slagGenerator);
                this.place(mc, fx + 10, fy, fz + 2, "thermal_generator", IC2Blocks.thermalGenerator);
                this.place(mc, fx + 11, fy, fz + 2, "turbine_solar", IC2Blocks.turbineSolar);
                this.place(mc, fx + 12, fy, fz + 2, "ocean_generator", IC2Blocks.oceanGenerator);
                this.place(mc, fx + 9, fy, fz - 2, "wave_generator", IC2Blocks.waveGenerator);
                this.place(mc, fx + 10, fy, fz - 2, "wood_gasser", IC2Blocks.woodGasser);
                this.place(mc, fx + 11, fy, fz - 2, "wood_gasser_elec", IC2Blocks.woodGasserElec);
                this.place(mc, fx + 12, fy, fz - 2, "slow_grinder", IC2Blocks.slowGrinder);
                this.place(mc, fx + 9, fy, fz - 4, "rare_earth_extractor", IC2Blocks.rareEarthExtractor);
                this.placeCable(mc, fx + 9, fy + 1, fz, "plasma_cable", 11);
                this.placeCable(mc, fx + 10, fy + 1, fz, "plasma_cable2", 11);
                this.placeCable(mc, fx + 11, fy + 1, fz, "copper_cable_top", 0);
                SingleplayerTest.log("adv generator machines placed");
            }
            catch (Throwable e) {
                SingleplayerTest.log("FAILED placing adv machines: " + String.valueOf(e));
            }
            finally {
                advPlaced.countDown();
            }
        });
        SingleplayerTest.await(advPlaced, 15);
        SingleplayerTest.sleep(5000L);
        SingleplayerTest.log("adv machines ticked without crash");
        this.openGui(mc, "pesu", 3, fx + 9, fy, fz);
        SingleplayerTest.sleep(2000L);
        SingleplayerTest.log("screen after pesu GUI: " + this.screenName(mc));
        this.openGui(mc, "plasmafier", 22, fx + 10, fy, fz);
        SingleplayerTest.sleep(2000L);
        SingleplayerTest.log("screen after plasmafier GUI: " + this.screenName(mc));
        this.openGui(mc, "slow_grinder", 17, fx + 12, fy, fz - 2);
        SingleplayerTest.sleep(2000L);
        SingleplayerTest.log("screen after slow grinder GUI: " + this.screenName(mc));
        this.openGui(mc, "wood_gasser", 18, fx + 10, fy, fz - 2);
        SingleplayerTest.sleep(2000L);
        SingleplayerTest.log("screen after wood gasser GUI: " + this.screenName(mc));
        this.openGui(mc, "wood_gasser_elec", 19, fx + 11, fy, fz - 2);
        SingleplayerTest.sleep(2000L);
        SingleplayerTest.log("screen after wood gasser elec GUI: " + this.screenName(mc));
        this.openGui(mc, "rare_earth_extractor", 21, fx + 9, fy, fz - 4);
        SingleplayerTest.sleep(2000L);
        SingleplayerTest.log("screen after rare earth GUI: " + this.screenName(mc));
        this.openGui(mc, "slag_generator", 14, fx + 9, fy, fz + 2);
        SingleplayerTest.sleep(2000L);
        SingleplayerTest.log("screen after slag generator GUI: " + this.screenName(mc));
        this.openGui(mc, "thermal_generator", 15, fx + 10, fy, fz + 2);
        SingleplayerTest.sleep(2000L);
        SingleplayerTest.log("screen after thermal generator GUI: " + this.screenName(mc));
        this.openGui(mc, "turbine_solar", 16, fx + 11, fy, fz + 2);
        SingleplayerTest.sleep(2000L);
        SingleplayerTest.log("screen after turbine solar GUI: " + this.screenName(mc));
        this.openGui(mc, "macerator", 0, fx + 4, fy, fz);
        SingleplayerTest.sleep(3000L);
        SingleplayerTest.log("screen after macerator GUI: " + this.screenName(mc));
        this.openGui(mc, "generator", 2, fx + 2, fy, fz);
        SingleplayerTest.sleep(2500L);
        SingleplayerTest.log("screen after generator GUI: " + this.screenName(mc));
        this.openGui(mc, "solar_panel", 2, fx + 7, fy, fz);
        SingleplayerTest.sleep(2500L);
        SingleplayerTest.log("screen after solar GUI: " + this.screenName(mc));
        this.openGui(mc, "wind_mill", 2, fx + 6, fy, fz + 2);
        SingleplayerTest.sleep(2500L);
        SingleplayerTest.log("screen after wind GUI: " + this.screenName(mc));
        this.openGui(mc, "water_mill", 2, fx + 7, fy, fz - 2);
        SingleplayerTest.sleep(2500L);
        SingleplayerTest.log("screen after water GUI: " + this.screenName(mc));
        this.openGui(mc, "geo", 2, fx + 7, fy, fz + 2);
        SingleplayerTest.sleep(2500L);
        SingleplayerTest.log("screen after geo GUI: " + this.screenName(mc));
        this.openGui(mc, "batbox", 3, fx + 3, fy, fz + 2);
        SingleplayerTest.sleep(2500L);
        SingleplayerTest.log("screen after batbox GUI: " + this.screenName(mc));
        this.openGui(mc, "iron_furnace", 1, fx + 2, fy, fz + 2);
        SingleplayerTest.sleep(2500L);
        SingleplayerTest.log("screen after iron furnace GUI: " + this.screenName(mc));
        this.runOnMainThread(mc, () -> {
            try {
                mc.displayScreen(null);
                SingleplayerTest.log("GUI closed");
            }
            catch (Throwable e) {
                SingleplayerTest.log("close GUI failed: " + String.valueOf(e));
            }
        });
        SingleplayerTest.sleep(2000L);
        SingleplayerTest.log("letting machines tick for 10s...");
        SingleplayerTest.sleep(10000L);
        int genX = fx + 2;
        int genZ = fz;
        int macX = fx + 4;
        this.runOnMainThread(mc, () -> {
            try {
                TileEntity te = mc.currentWorld.getTileEntity((TilePosc)new TilePos(genX, fy, genZ));
                if (te instanceof TileEntityBaseGenerator) {
                    TileEntityBaseGenerator generator = (TileEntityBaseGenerator)te;
                    generator.setItem(1, new ItemStack(Items.COAL, 10));
                    SingleplayerTest.log("generator: coal inserted, fuel=" + generator.fuel);
                } else {
                    SingleplayerTest.log("FUNCTIONAL FAILED: generator TE is " + (te == null ? "null" : te.getClass().getSimpleName()));
                }
                TileEntity te2 = mc.currentWorld.getTileEntity((TilePosc)new TilePos(macX, fy, genZ));
                if (te2 instanceof TileEntityMacerator) {
                    TileEntityMacerator macerator = (TileEntityMacerator)te2;
                    macerator.setItem(0, new ItemStack(IC2Blocks.oreCopper, 4));
                    SingleplayerTest.log("macerator: 4 copper ore inserted");
                } else {
                    SingleplayerTest.log("FUNCTIONAL FAILED: macerator TE is " + (te2 == null ? "null" : te2.getClass().getSimpleName()));
                }
            }
            catch (Throwable e) {
                SingleplayerTest.log("FUNCTIONAL setup failed: " + String.valueOf(e));
            }
        });
        SingleplayerTest.log("waiting 30s for maceration (300 ticks)...");
        SingleplayerTest.sleep(30000L);
        this.runOnMainThread(mc, () -> {
            try {
                TileEntity te;
                TileEntity te2 = mc.currentWorld.getTileEntity((TilePosc)new TilePos(macX, fy, genZ));
                if (te2 instanceof TileEntityMacerator) {
                    TileEntityMacerator macerator = (TileEntityMacerator)te2;
                    ItemStack in = macerator.getItem(0);
                    ItemStack out = macerator.getItem(2);
                    SingleplayerTest.log("FUNCTIONAL RESULT: macerator energy=" + macerator.energy + " progress=" + macerator.progress + " input=" + (in == null ? "null" : in.toString()) + " output=" + (out == null ? "null" : out.toString()));
                    if (out != null && out.stackSize > 0) {
                        SingleplayerTest.log("FUNCTIONAL PASS: energy net + maceration working!");
                    } else {
                        SingleplayerTest.log("FUNCTIONAL ISSUE: no output yet (check energy flow)");
                    }
                }
                if ((te = mc.currentWorld.getTileEntity((TilePosc)new TilePos(genX, fy, genZ))) instanceof TileEntityBaseGenerator) {
                    TileEntityBaseGenerator generator = (TileEntityBaseGenerator)te;
                    SingleplayerTest.log("generator state: fuel=" + generator.fuel + " storage=" + generator.storage);
                }
                TileEntity cableTe = mc.currentWorld.getTileEntity((TilePosc)new TilePos(fx + 3, fy, fz));
                SingleplayerTest.log("diag: generator TE=" + (te == null ? "null" : te.getClass().getSimpleName()) + " cable TE=" + (cableTe == null ? "null" : cableTe.getClass().getSimpleName()) + " macerator TE=" + (te2 == null ? "null" : te2.getClass().getSimpleName()));
                SingleplayerTest.log("diag: generator addedToEnergyNet=" + ((TileEntityIC2Block)te).addedToEnergyNet + " cable addedToEnergyNet=" + ((TileEntityIC2Block)cableTe).addedToEnergyNet + " macerator addedToEnergyNet=" + ((TileEntityIC2Block)te2).addedToEnergyNet);
                SingleplayerTest.log("diag: generator emits=" + (te instanceof IEnergySource) + " cable conductor=" + (cableTe instanceof IEnergyConductor) + " macerator sink=" + (te2 instanceof IEnergySink));
                EnergyNet net = EnergyNet.getForWorld((World)mc.currentWorld);
                Field f = EnergyNet.class.getDeclaredField("energySourceToEnergyPathMap");
                f.setAccessible(true);
                Map map = (Map)f.get(net);
                SingleplayerTest.log("diag: energy net paths: " + map.size() + " sources");
                for (Object entryObj : map.entrySet()) {
                    Map.Entry entry = (Map.Entry)entryObj;
                    SingleplayerTest.log("diag: source=" + entry.getKey().getClass().getSimpleName() + " paths=" + ((List)entry.getValue()).size());
                }
                if (te instanceof IEnergySource) {
                    IEnergySource src = (IEnergySource)te;
                    Method m = EnergyNet.class.getDeclaredMethod("discover", TileEntity.class, Boolean.TYPE, Integer.TYPE);
                    m.setAccessible(true);
                    List paths = (List)m.invoke((Object)net, te, false, src.getMaxEnergyOutput());
                    SingleplayerTest.log("diag: manual discover from generator found " + paths.size() + " paths");
                    for (Object p : paths) {
                        SingleplayerTest.log("diag:   path -> " + String.valueOf(p));
                    }
                    Method g = EnergyNet.class.getDeclaredMethod("getValidReceivers", TileEntity.class, Boolean.TYPE);
                    g.setAccessible(true);
                    List recs = (List)g.invoke((Object)net, te, false);
                    SingleplayerTest.log("diag: getValidReceivers(generator) = " + recs.size());
                    for (Object r : recs) {
                        SingleplayerTest.log("diag:   receiver: " + String.valueOf(r));
                    }
                    List recs2 = (List)g.invoke((Object)net, cableTe, false);
                    SingleplayerTest.log("diag: getValidReceivers(cable) = " + recs2.size());
                    for (Object r : recs2) {
                        SingleplayerTest.log("diag:   receiver: " + String.valueOf(r));
                    }
                    SingleplayerTest.log("diag: gen emitsEnergyTo(cable, XP) = " + ((IEnergyEmitter)te).emitsEnergyTo(cableTe, Direction.XP));
                    SingleplayerTest.log("diag: cable acceptsEnergyFrom(gen, XN) = " + ((IEnergyAcceptor)cableTe).acceptsEnergyFrom(te, Direction.XN));
                    SingleplayerTest.log("diag: cable emitsEnergyTo(macerator, XP) = " + ((IEnergyEmitter)cableTe).emitsEnergyTo(te2, Direction.XP));
                    SingleplayerTest.log("diag: macerator acceptsEnergyFrom(cable, XN) = " + ((IEnergyAcceptor)te2).acceptsEnergyFrom(cableTe, Direction.XN));
                }
                for (Direction d : Direction.values()) {
                    TileEntity n = d.applyToTileEntity((World)mc.currentWorld, te);
                    SingleplayerTest.log("diag: gen " + d.name() + " -> " + (n == null ? "null" : n.getClass().getSimpleName()));
                }
            }
            catch (Throwable e) {
                SingleplayerTest.log("FUNCTIONAL check failed: " + String.valueOf(e));
            }
        });
        SingleplayerTest.sleep(3000L);
        this.deepAudit(mc, fx, fy, fz);
        this.round2Audit(mc, fx, fy, fz);
        this.round3Audit(mc, fx, fy, fz);
        this.round4Audit(mc, fx, fy, fz);
        this.round5Audit(mc, fx, fy, fz);
        this.round6Audit(mc, fx, fy, fz);
        this.round7Audit(mc, fx, fy, fz);
        this.round8Audit(mc);
        try {
            mc.thePlayer.moveTo((double)(fx + 200), (double)(fy + 30), (double)(fz + 200), 0.0f, 0.0f);
            SingleplayerTest.log("teleported for chunk loading");
            SingleplayerTest.sleep(15000L);
        }
        catch (Throwable e) {
            SingleplayerTest.log("teleport failed: " + String.valueOf(e));
        }
        try {
            mc.thePlayer.moveTo((double)fx + 0.5, (double)(fy + 1), (double)fz + 0.5, 0.0f, 0.0f);
            SingleplayerTest.log("teleported back (chunk reload, CME test #29)");
            SingleplayerTest.sleep(10000L);
            this.runOnMainThread(mc, () -> {
                try {
                    TileEntity te = mc.currentWorld.getTileEntity((TilePosc)new TilePos(fx + 2, fy, fz));
                    SingleplayerTest.log("post-reload: generator TE=" + (te == null ? "null" : te.getClass().getSimpleName()) + " world=" + (mc.currentWorld != null));
                    if (te instanceof TileEntityIC2Block) {
                        TileEntityIC2Block b = (TileEntityIC2Block)te;
                        SingleplayerTest.log("post-reload: addedToEnergyNet=" + b.addedToEnergyNet + " (true = \u043a\u043e\u0440\u0440\u0435\u043a\u0442\u043d\u043e \u043f\u0435\u0440\u0435\u043f\u043e\u0434\u043a\u043b\u044e\u0447\u0451\u043d \u043f\u043e\u0441\u043b\u0435 \u0432\u044b\u0433\u0440\u0443\u0437\u043a\u0438 \u0447\u0430\u043d\u043a\u0430)");
                    }
                }
                catch (Throwable e) {
                    SingleplayerTest.log("post-reload check failed: " + String.valueOf(e));
                }
            });
            SingleplayerTest.sleep(3000L);
        }
        catch (Throwable e) {
            SingleplayerTest.log("teleport back failed: " + String.valueOf(e));
        }
        SingleplayerTest.log("final: currentWorld=" + (mc.currentWorld != null) + " player=" + (mc.thePlayer != null) + " screen=" + this.screenName(mc));
        SingleplayerTest.finish();
    }

    private void deepAudit(Minecraft mc, int fx, int fy, int fz) {
        this.runOnMainThread(mc, () -> {
            try {
                HashSet<String> producible = new HashSet<String>();
                RecipeRegistry reg = Registries.RECIPES;
                int crafting = 0;
                int furnace = 0;
                int blast = 0;
                int trommel = 0;
                for (Object r : reg.getAllCraftingRecipes()) {
                    ++crafting;
                    SingleplayerTest.collectOutput(((RecipeEntryBase)r).getOutput(), producible);
                }
                for (Object r : reg.getAllFurnaceRecipes()) {
                    ++furnace;
                    SingleplayerTest.collectOutput(((RecipeEntryBase)r).getOutput(), producible);
                }
                for (Object r : reg.getAllBlastFurnaceRecipes()) {
                    ++blast;
                    SingleplayerTest.collectOutput(((RecipeEntryBase)r).getOutput(), producible);
                }
                int trommelIc2 = 0;
                for (net.minecraft.core.data.registry.recipe.entry.RecipeEntryTrommel r : reg.getAllTrommelRecipes()) {
                    ++trommel;
                    Object out = r.getOutput();
                    if (!(out instanceof WeightedRandomBag)) continue;
                    WeightedRandomBag bag = (WeightedRandomBag)out;
                    for (Object lootObj : bag.getEntries()) {
                        WeightedRandomLootObject loot;
                        ItemStack is;
                        if (!(lootObj instanceof WeightedRandomLootObject) || (is = (loot = (WeightedRandomLootObject)lootObj).getDefinedItemStack()) == null || is.getItem() == null || !"ic2".equals(is.getItem().namespaceID.namespace())) continue;
                        ++trommelIc2;
                        SingleplayerTest.collectOutput(is, producible);
                    }
                }
                SingleplayerTest.log("AUDIT recipes: crafting=" + crafting + " furnace=" + furnace + " blast=" + blast + " trommel=" + trommel + " (ic2 \u0432 \u0442\u0440\u043e\u043c\u043c\u0435\u043b\u044c-\u043b\u0443\u0442\u0430\u0445: " + trommelIc2 + ") unique ic2 outputs=" + producible.size());
                TreeSet<Object> missingItems = new TreeSet<Object>();
                for (Field f : IC2Items.class.getFields()) {
                    Item[] arr;
                    if (!Modifier.isStatic(f.getModifiers())) continue;
                    Object v = f.get(null);
                    if (v instanceof Item) {
                        Item item = (Item)v;
                        String id = item.namespaceID.toString();
                        if (producible.contains(id)) continue;
                        missingItems.add(id);
                        continue;
                    }
                    if (!(v instanceof Item[])) continue;
                    for (Item item : arr = (Item[])v) {
                        String id;
                        if (item == null || producible.contains(id = item.namespaceID.toString())) continue;
                        missingItems.add(id);
                    }
                }
                SingleplayerTest.log("AUDIT items without recipe (" + missingItems.size() + "): " + String.valueOf(missingItems));
                TreeSet<String> missingBlocks = new TreeSet<String>();
                for (Field f : IC2Blocks.class.getFields()) {
                    String id;
                    Block block;
                    Item item;
                    Object v;
                    if (!Modifier.isStatic(f.getModifiers()) || !((v = f.get(null)) instanceof Block) || (item = Item.getItem((int)(block = (Block)v).id())) == null || producible.contains(id = item.namespaceID.toString())) continue;
                    missingBlocks.add(id);
                }
                SingleplayerTest.log("AUDIT blocks without recipe (" + missingBlocks.size() + "): " + String.valueOf(missingBlocks));
            }
            catch (Throwable e) {
                SingleplayerTest.log("AUDIT recipe coverage failed: " + String.valueOf(e));
            }
        });
        SingleplayerTest.sleep(5000L);
        this.runOnMainThread(mc, () -> {
            try {
                WorldClient w = mc.currentWorld;
                TilePos pos = new TilePos(fx + 2, fy + 1, fz - 6);
                w.setBlockTypeData((TilePosc)pos, IC2Blocks.rubberWood, 3);
                BlockLogicRubWood logic = (BlockLogicRubWood)IC2Blocks.rubberWood.getLogic();
                boolean wrongSide = logic.treetapHarvest((World)w, (TilePosc)pos, (Player)mc.thePlayer, Side.WEST, null);
                int metaWrong = w.getBlockData((TilePosc)pos);
                boolean ok = logic.treetapHarvest((World)w, (TilePosc)pos, (Player)mc.thePlayer, Side.SOUTH, new ItemStack((Item)IC2Items.treetap));
                int metaAfter = w.getBlockData((TilePosc)pos);
                int resinBefore = this.countItems((World)w, pos.x(), pos.y(), pos.z(), 3);
                SingleplayerTest.log("TREETAP: wrongSideTap=" + wrongSide + " (expect false) metaAfterWrongSide=" + metaWrong + " (expect 3) southTap=" + ok + " metaAfter=" + metaAfter + " (expect 9) resinNearby=" + resinBefore);
                int taps = 0;
                boolean exhausted = false;
                for (int i = 0; i < 60; ++i) {
                    logic.treetapHarvest((World)w, (TilePosc)pos, (Player)mc.thePlayer, Side.SOUTH, null);
                    ++taps;
                    if (w.getBlockData((TilePosc)pos) != 1) continue;
                    exhausted = true;
                    break;
                }
                int resinTotal = this.countItems((World)w, pos.x(), pos.y(), pos.z(), 4);
                SingleplayerTest.log("TREETAP: tapsToExhaust=" + taps + " exhausted=" + exhausted + " (expect true, bounded) resinTotalNearby=" + resinTotal + " (expect bounded <= ~10)");
                if (!exhausted) {
                    SingleplayerTest.log("TREETAP BUG: \u043f\u044f\u0442\u043d\u043e \u041d\u0415 \u0438\u0441\u0442\u043e\u0449\u0438\u043b\u043e\u0441\u044c \u0437\u0430 60 \u0442\u0430\u043f\u043e\u0432 \u2014 \u0431\u0435\u0441\u043a\u043e\u043d\u0435\u0447\u043d\u0430\u044f \u0441\u043c\u043e\u043b\u0430!");
                }
                w.setBlockType((TilePosc)pos, Blocks.AIR);
            }
            catch (Throwable e) {
                SingleplayerTest.log("TREETAP test failed: " + String.valueOf(e));
            }
        });
        SingleplayerTest.sleep(3000L);
        this.runOnMainThread(mc, () -> SingleplayerTest.cableDebugAudit(mc, fx, fz, fy));
        SingleplayerTest.sleep(2000L);
        this.runOnMainThread(mc, () -> {
            try {
                ItemStack drill = new ItemStack((Item)IC2Items.miningDrill);
                float speedStone = drill.getStrVsBlock(Blocks.STONE);
                float speedOre = drill.getStrVsBlock(IC2Blocks.oreCopper);
                float speedDirt = drill.getStrVsBlock(Blocks.DIRT);
                boolean harvestStone = drill.canHarvestBlock((Mob)mc.thePlayer, Blocks.STONE);
                boolean harvestOre = drill.canHarvestBlock((Mob)mc.thePlayer, IC2Blocks.oreCopper);
                boolean harvestObsidian = drill.canHarvestBlock((Mob)mc.thePlayer, Blocks.OBSIDIAN);
                SingleplayerTest.log("TOOL drill: speedStone=" + speedStone + " (expect 12.0) speedOre=" + speedOre + " speedDirt=" + speedDirt + " harvestStone=" + harvestStone + " harvestOre=" + harvestOre + " (expect true) harvestObsidian=" + harvestObsidian + " (expect false \u0443 level 2)");
                int dmgBefore = drill.getMetadata();
                drill.getItem().onBlockDestroyed(drill, (World)mc.currentWorld, (Mob)mc.thePlayer, Blocks.STONE, (TilePosc)new TilePos(fx, fy, fz), Side.TOP);
                SingleplayerTest.log("TOOL drill: charge used after destroy: " + dmgBefore + " -> " + drill.getMetadata() + " (expect +1)");
                ItemStack laser = new ItemStack(IC2Items.toolMiningLaser);
                float laserSpeed = laser.getStrVsBlock(Blocks.STONE);
                SingleplayerTest.log("TOOL laser: exists=" + (laser.getItem() != null) + " speedStone=" + laserSpeed);
            }
            catch (Throwable e) {
                SingleplayerTest.log("TOOL test failed: " + String.valueOf(e));
            }
        });
        SingleplayerTest.sleep(2000L);
        this.runOnMainThread(mc, () -> {
            try {
                BlockModelIC2Machine model = new BlockModelIC2Machine(IC2Blocks.macerator, "machine_top", "macerator_bottom", "machine_side", "macerator_front", "macerator_front");
                IconCoordinate southFront = model.getBlockTextureFromSideAndMetadata(Side.SOUTH, 0);
                IconCoordinate northSide = model.getBlockTextureFromSideAndMetadata(Side.NORTH, 0);
                boolean ok = southFront != null && southFront != northSide;
                SingleplayerTest.log("ICON: meta0 SOUTH=" + (southFront == null ? "null" : southFront.toString()) + " NORTH=" + (northSide == null ? "null" : northSide.toString()) + " distinct=" + ok + " (expect true: \u0444\u0440\u043e\u043d\u0442 \u0432\u0438\u0434\u0435\u043d \u043d\u0430 \u0438\u043a\u043e\u043d\u043a\u0435 \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u044f)");
                IconCoordinate southFront2 = model.getBlockTextureFromSideAndMetadata(Side.SOUTH, 3);
                SingleplayerTest.log("ICON: meta3(SOUTH facing) SOUTH side front=" + (southFront2 == southFront) + " (expect true)");
            }
            catch (Throwable e) {
                SingleplayerTest.log("ICON test failed: " + String.valueOf(e));
            }
        });
        SingleplayerTest.sleep(1000L);
        this.runOnMainThread(mc, () -> {
            try {
                ArrayList<String> drops = new ArrayList<String>();
                for (Field f : IC2Blocks.class.getFields()) {
                    Object v;
                    if (!Modifier.isStatic(f.getModifiers()) || !f.getName().startsWith("ore") || !((v = f.get(null)) instanceof Block)) continue;
                    Block block = (Block)v;
                    ItemStack[] res = block.getBreakResult((World)mc.currentWorld, EnumDropCause.PROPER_TOOL, (TilePosc)new TilePos(fx, fy, fz), 0, null);
                    String d = f.getName() + " -> ";
                    if (res == null) {
                        d = d + "NOTHING";
                    } else {
                        for (ItemStack s : res) {
                            d = d + s.toString() + " ";
                        }
                    }
                    drops.add(String.valueOf(d));
                }
                for (String string : drops) {
                    SingleplayerTest.log("ORE DROP: " + string);
                }
            }
            catch (Throwable e) {
                SingleplayerTest.log("ORE drop test failed: " + String.valueOf(e));
            }
        });
        SingleplayerTest.sleep(2000L);
        this.runOnMainThread(mc, () -> {
            try {
                WorldClient w = mc.currentWorld;
                this.place(mc, fx + 8, fy, fz, "generator2", IC2Blocks.generator);
                this.place(mc, fx + 9, fy, fz, "cable2", IC2Blocks.cable);
                this.place(mc, fx + 10, fy, fz, "batbox2", IC2Blocks.batBox);
                TileEntity gen = w.getTileEntity((TilePosc)new TilePos(fx + 8, fy, fz));
                if (gen instanceof TileEntityBaseGenerator) {
                    TileEntityBaseGenerator g = (TileEntityBaseGenerator)gen;
                    g.setItem(1, new ItemStack(Items.COAL, 20));
                    SingleplayerTest.log("BATBOX test: coal into generator2, fuel=" + g.fuel);
                }
            }
            catch (Throwable e) {
                SingleplayerTest.log("BATBOX setup failed: " + String.valueOf(e));
            }
        });
        SingleplayerTest.log("BATBOX test: waiting 20s for charging...");
        SingleplayerTest.sleep(20000L);
        this.runOnMainThread(mc, () -> {
            try {
                TileEntity box = mc.currentWorld.getTileEntity((TilePosc)new TilePos(fx + 10, fy, fz));
                if (box instanceof TileEntityElectricBlock) {
                    TileEntityElectricBlock b = (TileEntityElectricBlock)box;
                    SingleplayerTest.log("BATBOX RESULT: energy=" + b.energy + "/" + b.maxStorage + " (expect > 0 \u2014 \u043d\u0430\u043a\u043e\u043f\u043b\u0435\u043d\u0438\u0435 \u0440\u0430\u0431\u043e\u0442\u0430\u0435\u0442)" + (b.energy > 0 ? " PASS" : " FAIL"));
                } else {
                    SingleplayerTest.log("BATBOX RESULT: TE is " + (box == null ? "null" : box.getClass().getSimpleName()));
                }
            }
            catch (Throwable e) {
                SingleplayerTest.log("BATBOX check failed: " + String.valueOf(e));
            }
        });
        SingleplayerTest.sleep(2000L);
        this.runOnMainThread(mc, () -> {
            try {
                this.place(mc, fx + 8, fy, fz - 2, "mfe", IC2Blocks.mfe);
                this.place(mc, fx + 9, fy, fz - 2, "mfsu", IC2Blocks.mfsu);
                this.place(mc, fx + 10, fy, fz - 2, "canner", IC2Blocks.canner);
                this.place(mc, fx + 8, fy, fz + 2, "electrolyzer", IC2Blocks.electrolyzer);
                this.place(mc, fx + 9, fy, fz + 2, "induction", IC2Blocks.inductionFurnace);
                this.place(mc, fx + 10, fy, fz + 2, "mass_fab", IC2Blocks.massFabricator);
                this.place(mc, fx + 8, fy + 1, fz + 4, "reactor", IC2Blocks.nuclearReactor);
                this.place(mc, fx + 9, fy + 1, fz + 4, "miner", IC2Blocks.miner);
                this.place(mc, fx + 10, fy + 1, fz + 4, "pump", IC2Blocks.pump);
                this.place(mc, fx + 8, fy + 1, fz - 4, "safe", IC2Blocks.personalSafe);
                this.place(mc, fx + 9, fy + 1, fz - 4, "tradeomat", IC2Blocks.tradeOMat);
                this.place(mc, fx + 10, fy + 1, fz - 4, "terraformer", IC2Blocks.terraformer);
                mc.thePlayer.moveTo((double)fx + 8.5, (double)(fy + 1), (double)fz + 0.5, 0.0f, 0.0f);
                SingleplayerTest.log("teleported near extended machines (stillValid radius)");
            }
            catch (Throwable e) {
                SingleplayerTest.log("extended GUI placement failed: " + String.valueOf(e));
            }
        });
        SingleplayerTest.sleep(3000L);
        this.openGui(mc, "mfe", 3, fx + 8, fy, fz - 2);
        SingleplayerTest.sleep(2000L);
        SingleplayerTest.log("screen after MFE GUI: " + this.screenName(mc));
        this.openGui(mc, "mfsu", 3, fx + 9, fy, fz - 2);
        SingleplayerTest.sleep(2000L);
        SingleplayerTest.log("screen after MFSU GUI: " + this.screenName(mc));
        this.openGui(mc, "extractor", 0, fx + 4, fy, fz + 2);
        SingleplayerTest.sleep(2000L);
        SingleplayerTest.log("screen after extractor GUI: " + this.screenName(mc));
        this.openGui(mc, "compressor", 0, fx + 5, fy, fz);
        SingleplayerTest.sleep(2000L);
        SingleplayerTest.log("screen after compressor GUI: " + this.screenName(mc));
        this.openGui(mc, "electric_furnace", 0, fx + 5, fy, fz + 2);
        SingleplayerTest.sleep(2000L);
        SingleplayerTest.log("screen after e-furnace GUI: " + this.screenName(mc));
        this.openGui(mc, "recycler", 0, fx + 6, fy, fz);
        SingleplayerTest.sleep(2000L);
        SingleplayerTest.log("screen after recycler GUI: " + this.screenName(mc));
        this.openGui(mc, "canner", 4, fx + 10, fy, fz - 2);
        SingleplayerTest.sleep(2000L);
        SingleplayerTest.log("screen after canner GUI: " + this.screenName(mc));
        this.openGui(mc, "electrolyzer", 5, fx + 8, fy, fz + 2);
        SingleplayerTest.sleep(2000L);
        SingleplayerTest.log("screen after electrolyzer GUI: " + this.screenName(mc));
        this.openGui(mc, "induction", 6, fx + 9, fy, fz + 2);
        SingleplayerTest.sleep(2000L);
        SingleplayerTest.log("screen after induction GUI: " + this.screenName(mc));
        this.openGui(mc, "mass_fab", 7, fx + 10, fy, fz + 2);
        SingleplayerTest.sleep(2000L);
        SingleplayerTest.log("screen after mass fab GUI: " + this.screenName(mc));
        this.openGui(mc, "reactor", 8, fx + 8, fy + 1, fz + 4);
        SingleplayerTest.sleep(2500L);
        SingleplayerTest.log("screen after reactor GUI: " + this.screenName(mc));
        this.openGui(mc, "miner", 9, fx + 9, fy + 1, fz + 4);
        SingleplayerTest.sleep(2000L);
        SingleplayerTest.log("screen after miner GUI: " + this.screenName(mc));
        this.openGui(mc, "pump", 10, fx + 10, fy + 1, fz + 4);
        SingleplayerTest.sleep(2000L);
        SingleplayerTest.log("screen after pump GUI: " + this.screenName(mc));
        this.openGui(mc, "safe", 11, fx + 8, fy + 1, fz - 4);
        SingleplayerTest.sleep(2000L);
        SingleplayerTest.log("screen after safe GUI: " + this.screenName(mc));
        this.openGui(mc, "tradeomat", 12, fx + 9, fy + 1, fz - 4);
        SingleplayerTest.sleep(2000L);
        SingleplayerTest.log("screen after trade-o-mat GUI: " + this.screenName(mc));
        this.runOnMainThread(mc, () -> {
            try {
                TilePos tpos = new TilePos(fx + 10, fy + 1, fz - 4);
                TileEntity tte = mc.currentWorld.getTileEntity((TilePosc)tpos);
                if (tte instanceof TileEntityTerraformer) {
                    TileEntityTerraformer terra = (TileEntityTerraformer)tte;
                    ItemStack bp = new ItemStack(IC2Items.tfbpCultivation, 2);
                    boolean used = IC2Items.tfbpCultivation.onUseOnBlock(bp, (World)mc.currentWorld, (Player)mc.thePlayer, (TilePosc)tpos, Side.NORTH, 0.5, 0.5);
                    ItemStack slot0 = terra.getItem(0);
                    SingleplayerTest.log("TERRAFORMER: blueprint inserted=" + used + " (expect true) slot0=" + (slot0 == null ? "null" : slot0.toString()) + " remaining=" + bp.stackSize);
                    boolean ejected = terra.ejectBlueprint();
                    SingleplayerTest.log("TERRAFORMER: wrench eject=" + ejected + " slot0=" + (terra.getItem(0) == null ? "null" : "full"));
                } else {
                    SingleplayerTest.log("TERRAFORMER: TE is " + (tte == null ? "null" : tte.getClass().getSimpleName()));
                }
            }
            catch (Throwable e) {
                SingleplayerTest.log("TERRAFORMER test failed: " + String.valueOf(e));
            }
        });
        SingleplayerTest.sleep(2000L);
        this.runOnMainThread(mc, () -> mc.displayScreen(null));
        SingleplayerTest.sleep(1500L);
    }

    private static String blockName(World w, int x, int y, int z) {
        Block b = w.getBlockType((TilePosc)new TilePos(x, y, z));
        return b == null ? "null" : b.getKey();
    }

    private void round2Audit(Minecraft mc, int fx, int fy, int fz) {
        CountDownLatch compressorDone = new CountDownLatch(1);
        this.runOnMainThread(mc, () -> {
            try {
                WorldClient w = mc.currentWorld;
                TilePos pos = new TilePos(fx + 5, fy, fz);
                TileEntity te = w.getTileEntity((TilePosc)pos);
                if (te instanceof TileEntityCompressor) {
                    TileEntityCompressor comp = (TileEntityCompressor)te;
                    comp.setItem(0, new ItemStack(Blocks.SAND, 8));
                    comp.energy = 20000;
                    SingleplayerTest.log("R2 compressor: sand + energy inserted, energy=" + comp.energy);
                }
            }
            catch (Throwable e) {
                SingleplayerTest.log("R2 compressor setup failed: " + String.valueOf(e));
            }
            finally {
                compressorDone.countDown();
            }
        });
        SingleplayerTest.await(compressorDone, 10);
        SingleplayerTest.log("R2 compressor: waiting 25s for compression (300 ticks)...");
        SingleplayerTest.sleep(25000L);
        this.runOnMainThread(mc, () -> {
            block10: {
                try {
                    TilePos pos = new TilePos(fx + 5, fy, fz);
                    TileEntity te = mc.currentWorld.getTileEntity((TilePosc)pos);
                    if (!(te instanceof TileEntityCompressor)) break block10;
                    TileEntityCompressor comp = (TileEntityCompressor)te;
                    ItemStack out = comp.getItem(2);
                    SingleplayerTest.log("R2 compressor: output=" + (out == null ? "null" : out.toString()) + " energy=" + comp.energy);
                    if (out == null || out.stackSize <= 0) {
                        SingleplayerTest.log("R2 COMPRESSOR ISSUE: no output after 25s");
                    }
                    IC2GuiHandler.openClientGui(0, mc.thePlayer.containerMenu.containerId, fx + 5, fy, fz);
                    SingleplayerTest.sleep(500L);
                    MenuAbstract menu = mc.thePlayer.containerMenu;
                    SingleplayerTest.log("R2 compressor GUI: " + (mc.currentScreen == null ? "null" : mc.currentScreen.getClass().getSimpleName()));
                    if (menu instanceof MenuElectricMachine) {
                        ItemStack taken;
                        MenuElectricMachine em = (MenuElectricMachine)menu;
                        try {
                            taken = menu.clicked(InventoryAction.CLICK_LEFT, new int[]{2}, (Player)mc.thePlayer);
                            SingleplayerTest.log("R2 CLICK_LEFT output: taken=" + (taken == null ? "null" : taken.toString()) + " held=" + (mc.thePlayer.inventory.getHeldItemStack() == null ? "null" : mc.thePlayer.inventory.getHeldItemStack().toString()));
                        }
                        catch (Throwable t) {
                            SingleplayerTest.log("R2 CLICK_LEFT CRASH REPRO: " + String.valueOf(t));
                        }
                        try {
                            taken = menu.clicked(InventoryAction.MOVE_STACK, new int[]{2}, (Player)mc.thePlayer);
                            SingleplayerTest.log("R2 MOVE_STACK output: taken=" + (taken == null ? "null" : taken.toString()));
                        }
                        catch (Throwable t) {
                            SingleplayerTest.log("R2 MOVE_STACK CRASH REPRO: " + String.valueOf(t));
                        }
                        try {
                            menu.clicked(InventoryAction.SORT, new int[]{2}, (Player)mc.thePlayer);
                            SingleplayerTest.log("R2 SORT output: OK");
                        }
                        catch (Throwable t) {
                            SingleplayerTest.log("R2 SORT CRASH REPRO: " + String.valueOf(t));
                        }
                    }
                    mc.displayScreen(null);
                    SingleplayerTest.log("R2 compressor click test DONE (no crash = fix works)");
                }
                catch (Throwable e) {
                    SingleplayerTest.log("R2 compressor click test failed: " + String.valueOf(e));
                }
            }
        });
        SingleplayerTest.sleep(3000L);
        this.runOnMainThread(mc, () -> {
            try {
                PlayerLocal p = mc.thePlayer;
                p.moveTo(p.x, 64.0, p.z, 0.0f, 0.0f);
                ItemStack stack = new ItemStack((Item)IC2Items.jetpack, 1);
                p.setItemInArmorSlot(HumanArmorShape.CHEST, stack);
                int metaBefore = stack.getMetadata();
                double ydBefore = p.yd;
                Field jumpingField = Mob.class.getDeclaredField("isJumping");
                jumpingField.setAccessible(true);
                jumpingField.setBoolean(p, true);
                IC2PlayerTicker.tick((Player)p);
                jumpingField.setBoolean(p, false);
                SingleplayerTest.log("R2 jetpack: meta " + metaBefore + " -> " + stack.getMetadata() + " (\u0434\u043e\u043b\u0436\u0435\u043d \u0432\u044b\u0440\u0430\u0441\u0442\u0438 = \u0440\u0430\u0441\u0445\u043e\u0434 \u0442\u043e\u043f\u043b\u0438\u0432\u0430), yd " + ydBefore + " -> " + p.yd + " (\u0434\u043e\u043b\u0436\u0435\u043d \u0441\u0442\u0430\u0442\u044c > 0 = \u0442\u044f\u0433\u0430)");
                if (stack.getMetadata() > metaBefore && p.yd > ydBefore) {
                    SingleplayerTest.log("R2 JETPACK PASS: \u0442\u044f\u0433\u0430 \u0438 \u0440\u0430\u0441\u0445\u043e\u0434 \u0442\u043e\u043f\u043b\u0438\u0432\u0430 \u0440\u0430\u0431\u043e\u0442\u0430\u044e\u0442");
                } else {
                    SingleplayerTest.log("R2 JETPACK FAIL: \u0442\u044f\u0433\u0430/\u0440\u0430\u0441\u0445\u043e\u0434 \u043d\u0435 \u0440\u0430\u0431\u043e\u0442\u0430\u044e\u0442!");
                }
                ItemStack stack2 = new ItemStack((Item)IC2Items.electricJetpack, 1);
                p.setItemInArmorSlot(HumanArmorShape.CHEST, stack2);
                int meta2 = stack2.getMetadata();
                p.yd = -1.0;
                IC2PlayerTicker.hoverMode = true;
                IC2PlayerTicker.tick((Player)p);
                IC2PlayerTicker.hoverMode = false;
                SingleplayerTest.log("R2 electric jetpack (hover, \u043f\u0430\u0434\u0435\u043d\u0438\u0435): meta " + meta2 + " -> " + stack2.getMetadata() + " yd=-1.0 -> " + p.yd + " (\u0434\u043e\u043b\u0436\u0435\u043d \u0437\u0430\u043c\u0435\u0434\u043b\u0438\u0442\u044c\u0441\u044f)");
                if (stack2.getMetadata() > meta2 && p.yd > -1.0) {
                    SingleplayerTest.log("R2 ELECTRIC JETPACK PASS: \u043f\u0430\u0440\u0435\u043d\u0438\u0435 \u0440\u0430\u0431\u043e\u0442\u0430\u0435\u0442");
                } else {
                    SingleplayerTest.log("R2 ELECTRIC JETPACK FAIL");
                }
                p.setItemInArmorSlot(HumanArmorShape.CHEST, null);
            }
            catch (Throwable e) {
                SingleplayerTest.log("R2 jetpack test failed: " + String.valueOf(e));
            }
        });
        SingleplayerTest.sleep(2000L);
        this.runOnMainThread(mc, () -> {
            try {
                BlockLogic logic;
                WorldClient w = mc.currentWorld;
                TilePos pos = new TilePos(fx + 3, fy, fz);
                Block block = w.getBlockType((TilePosc)pos);
                BlockLogic blockLogic = logic = block == null ? null : block.getLogic();
                if (logic instanceof BlockLogicCable) {
                    BlockLogicCable cableLogic = (BlockLogicCable)logic;
                    AABBdc box = cableLogic.getCollisionAABB((WorldSource)w, (TilePosc)pos);
                    boolean containsWorldCoords = box != null && box.maxX() > box.minX() + 0.5 && (box.maxX() > (double)(fx + 3) || box.minX() < (double)(fx + 4));
                    SingleplayerTest.log("R2 cable collision: box=" + (box == null ? "null" : String.format("[%.2f..%.2f]", box.minX(), box.maxX())) + " worldCoords=" + containsWorldCoords + " (\u043e\u0436\u0438\u0434\u0430\u0435\u0442\u0441\u044f true)");
                    AABBd playerBox = mc.thePlayer.bb;
                    boolean intersects = box != null && playerBox.intersectsAABB(new AABBd(box));
                    SingleplayerTest.log("R2 cable collision: player intersects cable box=" + intersects);
                    HitResult hit = w.checkBlockCollisionBetweenPoints((Vector3dc)new Vector3d(mc.thePlayer.x, mc.thePlayer.y + 1.6, mc.thePlayer.z), (Vector3dc)new Vector3d((double)fx + 3.5, (double)fy + 0.5, (double)fz + 0.5), false, false, false);
                    SingleplayerTest.log("R2 cable raytrace: " + (hit == null ? "null" : hit.toString()));
                } else {
                    SingleplayerTest.log("R2 cable: logic is " + (logic == null ? "null" : logic.getClass().getSimpleName()));
                }
                int ok = 0;
                int bad = 0;
                for (ItemCablePlaceable item : IC2Items.cableItems) {
                    boolean valid;
                    if (item == null) continue;
                    IconCoordinate tex = TextureRegistry.getTexture((String)("ic2:item/" + item.namespaceID.value()));
                    boolean bl = valid = tex != null && tex.width > 0;
                    if (valid) {
                        ++ok;
                        continue;
                    }
                    ++bad;
                }
                SingleplayerTest.log("R2 cable item icons: " + ok + " ok, " + bad + " bad (\u043e\u0436\u0438\u0434\u0430\u0435\u0442\u0441\u044f 11 ok, 0 bad)");
                Field shockField = EnergyNet.class.getDeclaredField("entityLivingToShockEnergyMap");
                shockField.setAccessible(true);
                Map shockMap = (Map)shockField.get(null);
                SingleplayerTest.log("R2 shock map size (\u043f\u043e\u0441\u043b\u0435 \u0442\u0438\u043a\u043e\u0432 \u0441\u0435\u0442\u0438): " + shockMap.size());
            }
            catch (Throwable e) {
                SingleplayerTest.log("R2 cable test failed: " + String.valueOf(e));
            }
        });
        SingleplayerTest.sleep(2000L);
        this.runOnMainThread(mc, () -> {
            try {
                RecipeRegistry reg = Registries.RECIPES;
                String[] targets = new String[]{"ic2:painter", "ic2:tin_can", "ic2:nuke", "ic2:biofuel_cell", "ic2:re_battery", "ic2:bronze_helmet", "ic2:quantum_helmet"};
                HashSet<String> outputs = new HashSet<String>();
                for (RecipeEntryCrafting r : reg.getAllCraftingRecipes()) {
                    SingleplayerTest.collectOutput(r.getOutput(), outputs);
                }
                for (RecipeEntryFurnace r : reg.getAllFurnaceRecipes()) {
                    SingleplayerTest.collectOutput(r.getOutput(), outputs);
                }
                for (net.minecraft.core.data.registry.recipe.entry.RecipeEntryBlastFurnace r : reg.getAllBlastFurnaceRecipes()) {
                    SingleplayerTest.collectOutput(r.getOutput(), outputs);
                }
                for (String target : targets) {
                    SingleplayerTest.log("R2 recipe visible: " + target + " -> " + (outputs.contains(target) ? "YES" : "NO"));
                }
                boolean biofuelInExtractor = false;
                for (ItemStack e : TileEntityExtractor.RECIPES.values()) {
                    if (e == null || e.getItem() != IC2Items.cellBiofuel) continue;
                    biofuelInExtractor = true;
                }
                boolean biofuelInGroup = false;
                RecipeGroup group = RecipeBuilder.getRecipeGroup((String)"ic2", (String)"extractor", (RecipeSymbol)new RecipeSymbol(IC2Blocks.extractor.getDefaultStack()));
                for (Object entryObj : group.getAllRecipes()) {
                    RecipeEntryFurnace fe;
                    if (!(entryObj instanceof RecipeEntryFurnace) || (fe = (RecipeEntryFurnace)entryObj).getOutput() == null || ((ItemStack)fe.getOutput()).getItem() != IC2Items.cellBiofuel) continue;
                    biofuelInGroup = true;
                }
                SingleplayerTest.log("R2 biofuel_cell: extractor TE map=" + biofuelInExtractor + " recipe group=" + biofuelInGroup);
            }
            catch (Throwable e) {
                SingleplayerTest.log("R2 recipe visibility failed: " + String.valueOf(e));
            }
        });
        SingleplayerTest.sleep(2000L);
        this.runOnMainThread(mc, () -> {
            try {
                BlockColor disp = (BlockColor)BlockColorDispatcher.getInstance().getDispatch(IC2Blocks.rubberLeaves);
                TilePos pos = new TilePos(fx + 5, fy + 1, fz - 2);
                int color = disp.getWorldColor((WorldSource)mc.currentWorld, (TilePosc)pos, 0);
                int r = color >> 16 & 0xFF;
                int g = color >> 8 & 0xFF;
                int b = color & 0xFF;
                boolean tinted = Math.abs(r - g) > 8 || Math.abs(g - b) > 8;
                SingleplayerTest.log("R2 leaves tint: color=#" + Integer.toHexString(color) + " tinted=" + tinted + " dispatch=" + disp.getClass().getSimpleName() + " (\u043e\u0436\u0438\u0434\u0430\u0435\u0442\u0441\u044f BlockColorCustom + tinted=true)");
                if (!tinted) {
                    SingleplayerTest.log("R2 LEAVES TINT FAIL: \u043b\u0438\u0441\u0442\u044c\u044f \u0431\u0443\u0434\u0443\u0442 \u0441\u0435\u0440\u044b\u043c\u0438!");
                }
            }
            catch (Throwable e) {
                SingleplayerTest.log("R2 leaves tint failed: " + String.valueOf(e));
            }
        });
        SingleplayerTest.sleep(1500L);
        this.runOnMainThread(mc, () -> {
            try {
                String[] items;
                String[] stringArray = items = new String[]{"bronze_helmet", "jetpack", "batpack", "nanosuit_helmet", "quantum_boots"};
                int n = stringArray.length;
                for (int i = 0; i < n; ++i) {
                    String item;
                    String q = "ic2:gui/hud/armor_bar/" + (item = stringArray[i]) + "/full";
                    boolean has = TextureRegistry.hasSourceFile((String)q);
                    SingleplayerTest.log("R2 hud icon " + item + ": " + (has ? "OK" : "MISSING"));
                }
            }
            catch (Throwable e) {
                SingleplayerTest.log("R2 hud icons failed: " + String.valueOf(e));
            }
        });
        SingleplayerTest.sleep(1500L);
        this.runOnMainThread(mc, () -> {
            try {
                ItemStack battery = new ItemStack((Item)IC2Items.batteryRE);
                int max = battery.getMaxDamage();
                SingleplayerTest.log("R2 battery maxDamage=" + max + " display=" + (max + 1) + "/" + (max + 1) + " (\u043e\u0436\u0438\u0434\u0430\u0435\u0442\u0441\u044f 10000/10000)");
                ItemStack quantum = new ItemStack(IC2Items.quantumBodyarmor);
                int qmax = quantum.getMaxDamage();
                SingleplayerTest.log("R2 quantum chest maxDamage=" + qmax + " charge=" + (qmax + 1) * 100 + " EU (\u043e\u0436\u0438\u0434\u0430\u0435\u0442\u0441\u044f 1000000)");
            }
            catch (Throwable e) {
                SingleplayerTest.log("R2 durability check failed: " + String.valueOf(e));
            }
        });
        SingleplayerTest.sleep(1500L);
    }

    private static void collectOutput(Object out, Set<String> producible) {
        ItemStack is;
        if (out instanceof ItemStack && (is = (ItemStack)out).getItem() != null && "ic2".equals(is.getItem().namespaceID.namespace())) {
            producible.add(is.getItem().namespaceID.toString());
        }
    }

    private void round3Audit(Minecraft mc, int fx, int fy, int fz) {
        this.runOnMainThread(mc, () -> {
            try {
                String dustKey;
                WorldClient w = mc.currentWorld;
                ItemStack laser = new ItemStack(IC2Items.toolMiningLaser, 1);
                int maxDmg = laser.getMaxDamage();
                boolean laserOk = maxDmg == 8002;
                SingleplayerTest.log("R3 laser: maxDamage=" + maxDmg + " (8002 = 80k EU, \u043e\u0440\u0438\u0433\u0438\u043d\u0430\u043b) " + (laserOk ? "OK" : "FAIL"));
                int meta0 = laser.getMetadata();
                ((ItemMiningLaser)IC2Items.toolMiningLaser).use(laser, 125, (Mob)mc.thePlayer);
                SingleplayerTest.log("R3 laser shot: meta " + meta0 + " -> " + laser.getMetadata() + " (\u043e\u0436\u0438\u0434\u0430\u0435\u0442\u0441\u044f +125)");
                int dx = fx + 11;
                int dy = fy;
                int dz = fz + 4;

                this.place(mc, dx, dy - 1, dz, "door_base", IC2Blocks.bronzeBrick);
                ItemStack doorStack = new ItemStack(IC2Items.bronzeDoorItem, 1);
                try {
                    doorStack.getItem().onUseItemOnBlock(doorStack, (Player)mc.thePlayer, (World)w, dx, dy - 1, dz, Side.TOP, 0.5, 1.0);
                }
                catch (Throwable t) {
                    SingleplayerTest.log("R3 door place throwable: " + String.valueOf(t));
                }
                int bottomId = w.getBlockId(dx, dy, dz);
                int topId = w.getBlockId(dx, dy + 1, dz);
                SingleplayerTest.log("R3 bronze door: bottom=" + bottomId + " top=" + topId + " (\u043e\u0431\u0430 > 0 = \u0434\u0432\u0435\u0440\u044c \u0440\u0430\u0431\u043e\u0442\u0430\u0435\u0442)");
                if (bottomId == 0 || topId == 0) {
                    SingleplayerTest.log("R3 BRONZE DOOR FAIL: \u043d\u0435 \u0441\u0442\u0430\u0432\u0438\u0442\u0441\u044f!");
                }
                boolean wolfOk = IC2Items.bronzeWolfArmor != null;
                String wolfKey = wolfOk ? IC2Items.bronzeWolfArmor.getLanguageKey(laser) : "null";
                boolean wolfTex = TextureRegistry.getTexture((String)"ic2:item/armor_wolf_bronze") != null;
                SingleplayerTest.log("R3 wolf armor: item=" + wolfOk + " key=" + wolfKey + " texture=" + wolfTex);
                this.place(mc, fx + 9, fy, fz, "copper_brick", IC2Blocks.copperBrick);
                boolean copperTex = TextureRegistry.getTexture((String)"ic2:block/copper_brick") != null;
                SingleplayerTest.log("R3 copper bricks: block=" + (IC2Blocks.copperBrick != null) + " texture=" + copperTex);
                TilePos genPos = new TilePos(fx + 2, fy, fz);
                TileEntity genTe = w.getTileEntity((TilePosc)genPos);
                if (genTe instanceof TileEntityGenerator) {
                    TileEntityGenerator gen = (TileEntityGenerator)genTe;
                    gen.fuel = 100;
                    gen.storage = gen.getMaximumStorage();
                    boolean active1 = gen.isConverting();
                    gen.tick();
                    boolean active2 = gen.isConverting();
                    SingleplayerTest.log("R3 generator full-storage: before=" + active1 + " after tick=" + active2 + " fuel=" + gen.fuel + " (\u043e\u0436\u0438\u0434\u0430\u0435\u0442\u0441\u044f true/true \u2014 \u0431\u0435\u0437 \u043c\u0438\u0433\u0430\u043d\u0438\u044f)");
                    if (!active1 || !active2) {
                        SingleplayerTest.log("R3 GENERATOR FLICKER FAIL!");
                    }
                } else {
                    SingleplayerTest.log("R3 generator: TE \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d (" + (genTe == null ? "null" : genTe.getClass().getSimpleName()) + ")");
                }
                TilePos macPos = new TilePos(fx + 4, fy, fz);
                TileEntity macTe = w.getTileEntity((TilePosc)macPos);
                if (macTe instanceof TileEntityMacerator) {
                    TileEntityMacerator mac = (TileEntityMacerator)macTe;
                    mac.inventory[0] = new ItemStack(IC2Items.dustCopper, 8);
                    int before = this.countGroundItems((World)w, fx + 4, fy, fz, 8);
                    MenuElectricMachine menu = new MenuElectricMachine(mc.thePlayer.inventory, mac);
                    menu.clicked(InventoryAction.SORT, new int[]{0}, (Player)mc.thePlayer);
                    menu.clicked(InventoryAction.SORT, new int[]{3}, (Player)mc.thePlayer);
                    int after = this.countGroundItems((World)w, fx + 4, fy, fz, 8);
                    boolean inSlot = mac.inventory[0] != null && mac.inventory[0].stackSize == 8;
                    SingleplayerTest.log("R3 SORT on machine slots: ground items " + before + " -> " + after + ", input slot kept=" + inSlot + " (\u043e\u0436\u0438\u0434\u0430\u0435\u0442\u0441\u044f 0 -> 0, kept=true)");
                    if (after > before || !inSlot) {
                        SingleplayerTest.log("R3 SORT DROPS FAIL!");
                    }
                }
                int painterRecipes = 0;
                for (RecipeEntryBase r : Registries.RECIPES.getAllRecipes()) {
                    if (!r.toString().startsWith("ic2:workbench/painter_")) continue;
                    ++painterRecipes;
                }
                SingleplayerTest.log("R3 painter recipes: " + painterRecipes + " (\u043e\u0436\u0438\u0434\u0430\u0435\u0442\u0441\u044f 16, \u043c\u0435\u0442\u0430 \u043a\u0440\u0430\u0441\u0438\u0442\u0435\u043b\u044f = \u0438\u043d\u0434\u0435\u043a\u0441 \u0446\u0432\u0435\u0442\u0430)");
                if (painterRecipes != 16) {
                    SingleplayerTest.log("R3 PAINTER RECIPES FAIL!");
                }
                String modSeg = (dustKey = IC2Items.dustCopper.getLanguageKey(IC2Items.dustCopper.getDefaultStack())).length() > 5 ? dustKey.substring(5).split("\\.")[0] : "?";
                SingleplayerTest.log("R3 TMB item key: " + dustKey + " -> mod segment '" + modSeg + "' (\u043e\u0436\u0438\u0434\u0430\u0435\u0442\u0441\u044f ic2)");
                if (!"ic2".equals(modSeg)) {
                    SingleplayerTest.log("R3 TMB ITEM KEY FAIL!");
                }
                SingleplayerTest.log("R3 SI converters: catalystInstalled=" + SIConverters.isCatalystInstalled() + " (false = \u0431\u043b\u043e\u043a\u0438 \u0441\u043a\u0440\u044b\u0442\u044b \u0431\u0435\u0437 SI, \u043e\u043a)");
                int chunkRecipes = 0;
                for (RecipeEntryBase r : Registries.RECIPES.getAllRecipes()) {
                    if (!r.toString().startsWith("ic2:workbench/coal_chunk")) continue;
                    ++chunkRecipes;
                }
                SingleplayerTest.log("R3 coal chunk recipes: " + chunkRecipes + " (\u043e\u0436\u0438\u0434\u0430\u0435\u0442\u0441\u044f 3: \u043e\u0431\u0441\u0438\u0434\u0438\u0430\u043d/\u043a\u0438\u0440\u043f\u0438\u0447/\u0436\u0435\u043b\u0435\u0437\u043e)");
                if (chunkRecipes != 3) {
                    SingleplayerTest.log("R3 COAL CHUNK RECIPES FAIL!");
                }
                try {
                    TileEntityTeleporter tp1 = new TileEntityTeleporter();
                    tp1.worldObj = w;
                    TileEntityTeleporter tp2 = new TileEntityTeleporter();
                    tp2.worldObj = w;
                    tp2.ownFreq = 7777;
                    ItemStack trans = new ItemStack(IC2Items.frequencyTransmitter, 1);
                    tp2.getFrequency(trans, (Player)mc.thePlayer);
                    tp1.setFrequency(trans, (Player)mc.thePlayer);
                    SingleplayerTest.log("R3 teleporter: targetFreq=" + tp1.targetFreq + " (\u043e\u0436\u0438\u0434\u0430\u0435\u0442\u0441\u044f 7777)");
                    if (tp1.targetFreq != 7777) {
                        SingleplayerTest.log("R3 TELEPORTER LINK FAIL!");
                    }
                }
                catch (Throwable t) {
                    SingleplayerTest.log("R3 teleporter test error: " + String.valueOf(t));
                }
            }
            catch (Throwable e) {
                SingleplayerTest.log("R3 audit error: " + String.valueOf(e));
            }
        });
        SingleplayerTest.sleep(2500L);
    }

    private int countGroundItems(World w, int x, int y, int z, int radius) {
        int n = 0;
        AABBd aabb = new AABBd((double)(x - radius), (double)(y - radius), (double)(z - radius), (double)(x + radius + 1), (double)(y + radius + 1), (double)(z + radius + 1));
        for (EntityItem e : w.getEntitiesWithinAABB(EntityItem.class, (AABBdc)aabb)) {
            if (e == null || e.item == null) continue;
            n += e.item.stackSize;
        }
        return n;
    }

    private int countItems(World w, int x, int y, int z, int radius) {
        int n = 0;
        AABBd aabb = new AABBd((double)(x - radius), (double)(y - radius), (double)(z - radius), (double)(x + radius + 1), (double)(y + radius + 1), (double)(z + radius + 1));
        for (EntityItem e : w.getEntitiesWithinAABB(EntityItem.class, (AABBdc)aabb)) {
            if (e == null || e.item == null || e.item.getItem() != IC2Items.stickyResin) continue;
            n += e.item.stackSize;
        }
        return n;
    }


    private void round4Audit(Minecraft mc, int fx, int fy, int fz) {
        this.runOnMainThread(mc, () -> {
            try {

                boolean saberOff = IC2Items.nanoSaberOff != null;
                boolean saberOn = IC2Items.nanoSaber != null;
                SingleplayerTest.log("R4 nano saber: on_item=" + saberOn + " off_item=" + saberOff);
                if (saberOff && saberOn) {
                    ItemStack saber = new ItemStack(IC2Items.nanoSaberOff, 1);
                    saber.setMetadata(1);
                    int before = saber.itemID;
                    try {
                        saber = IC2Items.nanoSaberOff.onUse(saber, (World)mc.currentWorld, (Player)mc.thePlayer);
                    } catch (Throwable t) {
                        SingleplayerTest.log("R4 saber toggle throwable: " + String.valueOf(t));
                    }
                    SingleplayerTest.log("R4 saber toggle: " + before + " -> " + saber.itemID + " (ожидается ID включённой)");
                }

                java.util.Set<String> outputs = new java.util.HashSet<>();
                for (Object r : Registries.RECIPES.getAllCraftingRecipes()) {
                    Object out = ((net.minecraft.core.data.registry.recipe.RecipeEntryBase)r).getOutput();
                    if (out instanceof ItemStack is && is.getItem() != null) {
                        outputs.add(is.getItem().namespaceID.toString());
                    }
                }
                String[] expect = {"minecraft:item/armor_helmet_diamond", "minecraft:item/armor_chestplate_diamond",
                                "minecraft:item/armor_leggings_diamond", "minecraft:item/armor_boots_diamond",
                                "minecraft:item/armor_wolf_diamond", "minecraft:block/block_diamond", "minecraft:block/jukebox"};
                for (String e : expect) {
                    SingleplayerTest.log("R4 diamond variant " + e + ": " + (outputs.contains(e) ? "OK" : "MISSING!"));
                }
                boolean compassFromDiamond = false;
                for (Object r : Registries.RECIPES.getAllCraftingRecipes()) {
                    net.minecraft.core.data.registry.recipe.RecipeEntryBase base = (net.minecraft.core.data.registry.recipe.RecipeEntryBase)r;
                    Object out = base.getOutput();
                    if (!(out instanceof ItemStack os) || os.getItem() == null || !os.getItem().namespaceID.toString().equals("minecraft:item/tool_compass")) continue;
                    Object input = base.getInput();
                    if (input instanceof Object[] arr) {
                        for (Object o : arr) {
                            if (o instanceof net.minecraft.core.data.registry.recipe.RecipeSymbol sym && sym.resolve() != null) {
                                for (ItemStack st : sym.resolve()) {
                                    if (st != null && st.getItem() == IC2Items.industrialDiamond) compassFromDiamond = true;
                                }
                            }
                        }
                    }
                }
                boolean compass = compassFromDiamond;
                SingleplayerTest.log("R4 compass recipe from industrial diamond: " + (compass ? "PRESENT (FAIL — должен быть удалён)" : "absent OK"));

                boolean wolfReg = net.minecraft.core.entity.animal.MobWolf.ARMOR_MATERIALS.containsValue(IC2Items.bronzeWolfArmor);
                SingleplayerTest.log("R4 wolf armor registered in MobWolf.ARMOR_MATERIALS: " + wolfReg);

                boolean fenceTex = TextureRegistry.getTexture("ic2:block/iron_fence") != null;
                Block<?> fence = IC2Blocks.ironFence;
                boolean fenceThin = false;
                try {
                    org.joml.primitives.AABBdc b = fence.getLogic().getBoundsFromState((net.minecraft.core.world.WorldSource)mc.currentWorld, (TilePosc)new TilePos(fx, fy, fz));
                    fenceThin = b != null && b.maxY() - b.minY() > 0.99 && (b.maxX() - b.minX() < 0.99 || b.maxZ() - b.minZ() < 0.99);
                } catch (Throwable t) {
                    SingleplayerTest.log("R4 fence bounds throwable: " + String.valueOf(t));
                }
                SingleplayerTest.log("R4 iron fence: texture=" + fenceTex + " thin_bounds=" + fenceThin);

                try {
                    ic2.tileentity.TileEntitySolarGenerator solar = new ic2.tileentity.TileEntitySolarGenerator();
                    SingleplayerTest.log("R4 solar needsFuel: " + solar.needsFuel() + " (ожидается true)");
                } catch (Throwable t) {
                    SingleplayerTest.log("R4 solar throwable: " + String.valueOf(t));
                }

                try {
                    net.minecraft.core.data.registry.recipe.RecipeGroup g = Registries.RECIPES.getGroupFromKey("ic2:canner");
                    int n = g == null ? -1 : g.getAllRecipes().size();
                    SingleplayerTest.log("R4 canner recipes: " + n + " (ожидается 3)");
                } catch (Throwable t) {
                    SingleplayerTest.log("R4 canner recipes check throwable: " + String.valueOf(t));
                }

                int uu = 0;
                for (Object r : Registries.RECIPES.getAllCraftingRecipes()) {
                    boolean hasUU = false;
                    net.minecraft.core.data.registry.recipe.RecipeEntryBase base = (net.minecraft.core.data.registry.recipe.RecipeEntryBase)r;
                    Object input = base.getInput();
                    if (input instanceof Object[] arr) {
                        for (Object o : arr) {
                            if (!hasUU && o instanceof net.minecraft.core.data.registry.recipe.RecipeSymbol sym && sym.resolve() != null) {
                                for (ItemStack st : sym.resolve()) {
                                    if (st != null && st.getItem() == IC2Items.uuMatter) { hasUU = true; break; }
                                }
                            }
                        }
                    }
                    if (hasUU) ++uu;
                }
                SingleplayerTest.log("R4 UU recipes: " + uu + " (ожидается 28)");

                boolean bronzeDust = TileEntityMacerator.RECIPES.get(IC2Items.ingotBronze.id) != null;
                SingleplayerTest.log("R4 macerator bronze ingot->dust: " + bronzeDust);

                boolean voff = ic2.IC2Config.voltageSystemOff();
                SingleplayerTest.log("R4 voltageSystemOff=" + voff + " (machineExplosions=" + ic2.IC2Config.machineExplosions() + ")");

                int furnaceLeak = 0;
                for (Object r : Registries.RECIPES.getAllFurnaceRecipes()) {
                    net.minecraft.core.data.registry.recipe.entry.RecipeEntryFurnace fe = (net.minecraft.core.data.registry.recipe.entry.RecipeEntryFurnace)r;
                    if (fe.toString().contains("macerator_") || fe.toString().contains("extractor_") || fe.toString().contains("compressor_") || fe.toString().contains("canner_")) ++furnaceLeak;
                }
                SingleplayerTest.log("R4 furnace leak (машинные рецепты в печи): " + furnaceLeak + " (ожидается 0)");

                SingleplayerTest.log("R4 SI converters installed=" + ic2.si.SIConverters.isCatalystInstalled() + " (без SI просто нет)");
            }
            catch (Throwable e) {
                SingleplayerTest.log("R4 audit failed: " + String.valueOf(e));
            }
        });
    }

    private void runOnMainThread(Minecraft mc, Runnable action) {
        ActionScreen screen = new ActionScreen(action);
        try {
            mc.displayScreen((Screen)screen);
        }
        catch (Throwable e) {
            SingleplayerTest.log("displayScreen failed, running inline: " + String.valueOf(e));
            action.run();
        }
    }

    private void openGui(Minecraft mc, String name, int guiType, int x, int y, int z) {
        this.runOnMainThread(mc, () -> {
            try {
                IC2GuiHandler.openClientGui(guiType, 0, x, y, z);
                SingleplayerTest.log("GUI open requested: " + name);
            }
            catch (Throwable e) {
                SingleplayerTest.log("FAILED opening GUI " + name + ": " + String.valueOf(e));
            }
        });
    }


    private void round5Audit(Minecraft mc, int fx, int fy, int fz) {
        this.runOnMainThread(mc, () -> {
            try {
                WorldClient w = mc.currentWorld;

                int rx = fx + 12;
                int rz = fz + 2;
                this.place(mc, rx, fy, rz, "nuclear_reactor", IC2Blocks.nuclearReactor);
                TileEntity teR = w.getTileEntity((TilePosc)new TilePos(rx, fy, rz));
                if (teR instanceof ic2.tileentity.TileEntityNuclearReactor reactor) {
                    reactor.inventory[0] = new ItemStack(IC2Items.cellUran, 1);
                    reactor.inventory[1] = new ItemStack(IC2Items.reactorPlating, 1);
                    SingleplayerTest.log("R5 reactor: уран вставлен, ждём 4с (тик реактора = 20 тиков)");
                }
            }
            catch (Throwable e) {
                SingleplayerTest.log("R5 reactor setup failed: " + String.valueOf(e));
            }
        });
        SingleplayerTest.sleep(4000L);
        this.runOnMainThread(mc, () -> {
            try {
                WorldClient w = mc.currentWorld;
                int rx = fx + 12;
                int rz = fz + 2;
                TileEntity teR = w.getTileEntity((TilePosc)new TilePos(rx, fy, rz));
                if (teR instanceof ic2.tileentity.TileEntityNuclearReactor reactor) {
                    ItemStack cell = reactor.inventory[0];
                    boolean cellUsed = cell != null && cell.getMetadata() > 0;
                    boolean works = reactor.heat > 0 || reactor.output > 0 || cellUsed;
                    SingleplayerTest.log("R5 reactor: heat=" + reactor.heat + " output=" + reactor.output
                                    + " cell_meta=" + (cell == null ? -1 : cell.getMetadata())
                                    + " => " + (works ? "РАБОТАЕТ OK" : "FAIL"));

                    reactor.inventory[0] = null;
                    reactor.inventory[1] = null;
                }
                else {
                    SingleplayerTest.log("R5 reactor: TE не найден FAIL");
                }
            }
            catch (Throwable e) {
                SingleplayerTest.log("R5 reactor check failed: " + String.valueOf(e));
            }
        });
        SingleplayerTest.sleep(300L);
        this.runOnMainThread(mc, () -> {
            try {
                WorldClient w = mc.currentWorld;

                int sx = fx + 13;
                this.place(mc, sx, fy, fz, "solar_panel", IC2Blocks.solarPanel);
                TileEntity teS = w.getTileEntity((TilePosc)new TilePos(sx, fy, fz));
                if (teS instanceof ic2.tileentity.TileEntitySolarGenerator solar) {
                    long dayTime = w.getWorldTime();
                    w.setWorldTime(18000L);
                    w.updateSkyBrightness();
                    solar.updateSunVisibility();
                    boolean nightOff = !solar.sunIsVisible;
                    w.setWorldTime(dayTime);
                    w.updateSkyBrightness();
                    solar.updateSunVisibility();
                    boolean dayOn = solar.sunIsVisible;
                    SingleplayerTest.log("R5 solar: ночь=" + (nightOff ? "не работает OK" : "РАБОТАЕТ FAIL")
                                    + ", день=" + (dayOn ? "работает OK" : "не работает FAIL"));
                }

                int mx = fx + 14;
                this.place(mc, mx, fy, fz, "mfsu", IC2Blocks.mfsu);
                this.place(mc, mx + 1, fy, fz, "copper_cable", IC2Blocks.cable);
                this.place(mc, mx + 2, fy, fz, "batbox", IC2Blocks.batBox);
                TileEntity teM = w.getTileEntity((TilePosc)new TilePos(mx, fy, fz));
                if (teM instanceof TileEntityElectricBlock mfsu) {
                    mfsu.energy = 20000;
                }
                SingleplayerTest.log("R5 mfsu+cable: ждём 3с...");
            }
            catch (Throwable e) {
                SingleplayerTest.log("R5 solar/mfsu setup failed: " + String.valueOf(e));
            }
        });
        SingleplayerTest.sleep(3000L);
        this.runOnMainThread(mc, () -> {
            try {
                WorldClient w = mc.currentWorld;
                int mx = fx + 14;
                int cableId = w.getBlockId(mx + 1, fy, fz);
                TileEntity teB = w.getTileEntity((TilePosc)new TilePos(mx + 2, fy, fz));
                int batEnergy = teB instanceof TileEntityElectricBlock bat ? bat.energy : -1;
                boolean cableAlive = cableId == IC2Blocks.cable.id();
                SingleplayerTest.log("R5 mfsu+cable: кабель=" + (cableAlive ? "на месте OK" : "ИСЧЕЗ FAIL")
                                + ", batbox energy=" + batEnergy + " (" + (batEnergy > 0 ? "течёт OK (медь капит 32/т)" : "нет потока FAIL") + ")");

                int gx = fx + 17;
                this.place(mc, gx, fy, fz, "magnetizer", IC2Blocks.magnetizer);
                this.place(mc, gx, fy + 1, fz, "iron_fence_1", IC2Blocks.ironFence);
                this.place(mc, gx, fy + 2, fz, "iron_fence_2", IC2Blocks.ironFence);
                TileEntity teG = w.getTileEntity((TilePosc)new TilePos(gx, fy, fz));
                if (teG instanceof ic2.tileentity.TileEntityMagnetizer mag) {
                    mag.energy = 100;

                    try {
                        Object above1 = w.getBlock(gx, fy + 1, fz);
                        String aboveName = above1 == null ? "null" : String.valueOf(above1);
                        SingleplayerTest.log("R5 magnetizer diag: te_pos=" + mag.tilePos.x() + "," + mag.tilePos.y() + "," + mag.tilePos.z()
                                        + " above=" + aboveName
                                        + " isFence=" + (above1 == IC2Blocks.ironFence));
                        mag.tick();
                        mag.tick();
                        int manualData = w.getBlockData((TilePosc)new TilePos(gx, fy + 1, fz));
                        SingleplayerTest.log("R5 magnetizer manual tick: data=" + manualData + " energy_after=" + mag.energy + " ticker=" + mag.ticker);
                    }
                    catch (Throwable t) {
                        SingleplayerTest.log("R5 magnetizer manual tick throwable: " + String.valueOf(t));
                    }
                    SingleplayerTest.log("R5 magnetizer: energy=100, ждём 1с");
                }
            }
            catch (Throwable e) {
                SingleplayerTest.log("R5 mfsu check failed: " + String.valueOf(e));
            }
        });
        SingleplayerTest.sleep(1500L);
        this.runOnMainThread(mc, () -> {
            try {
                WorldClient w = mc.currentWorld;
                int gx = fx + 17;
                int data1 = w.getBlockData((TilePosc)new TilePos(gx, fy + 1, fz));
                SingleplayerTest.log("R5 magnetizer: заряд ограды=" + data1 + " (" + (data1 > 0 ? "заряжается OK" : "FAIL") + ")");

                int ok = 0;
                String[] bases = new String[]{"re_battery", "energy_crystal", "lapotron_crystal"};
                String[] states = new String[]{"_full", "_75", "_50", "_25", "_empty"};
                for (String base : bases) {
                    for (String state : states) {
                        if (TextureRegistry.hasSourceFile((String)("ic2:item/" + base + state))) {
                            ++ok;
                        }
                    }
                }
                SingleplayerTest.log("R5 battery charge textures: " + ok + "/15 (" + (ok == 15 ? "OK" : "FAIL") + ")");

                int hudOk = 0;
                String[] armor = new String[]{"bronze_helmet", "nanosuit_helmet", "quantum_boots", "jetpack"};
                for (String a : armor) {
                    if (TextureRegistry.hasSourceFile((String)("ic2:gui/hud/armor_bar/" + a + "/full"))) {
                        ++hudOk;
                    }
                }
                SingleplayerTest.log("R5 armor bar icons: " + hudOk + "/4 (" + (hudOk == 4 ? "OK" : "FAIL") + ")");

                try {
                    net.minecraft.core.data.registry.recipe.RecipeNamespace ns =
                                    (net.minecraft.core.data.registry.recipe.RecipeNamespace)Registries.RECIPES.getItem("ic2");
                    int mac = ns.getItem("macerator").getAllRecipes().size();
                    int ext = ns.getItem("extractor").getAllRecipes().size();
                    int com = ns.getItem("compressor").getAllRecipes().size();
                    int can = ns.getItem("canner").getAllRecipes().size();
                    int uu = ns.getItem("mass_fabricator").getAllRecipes().size();
                    boolean anyMachine = ns.getItem("macerator").getAllRecipes().get(0) instanceof ic2.recipe.RecipeEntryIC2Machine;
                    SingleplayerTest.log("R5 TMB groups: macerator=" + mac + " extractor=" + ext + " compressor=" + com
                                    + " canner=" + can + " mass_fabricator=" + uu
                                    + " entryType=" + (anyMachine ? "RecipeEntryIC2Machine OK" : "FAIL"));
                }
                catch (Throwable t) {
                    SingleplayerTest.log("R5 TMB groups check failed: " + String.valueOf(t));
                }

                try {
                    int planks = 0;
                    for (Object r : Registries.RECIPES.getAllCraftingRecipes()) {
                        Object out = ((net.minecraft.core.data.registry.recipe.RecipeEntryBase)r).getOutput();
                        if (out instanceof ItemStack is && is.itemID == net.minecraft.core.block.Blocks.PLANKS_OAK_PAINTED.id()) {
                            planks = is.stackSize;
                        }
                    }
                    SingleplayerTest.log("R5 rubber planks: " + planks + " (ожидается 4) " + (planks == 4 ? "OK" : "FAIL"));
                    String logName = net.minecraft.core.lang.I18n.getInstance().translateKey("tile.ic2.rubber.wood.name");
                    SingleplayerTest.log("R5 rubber wood log name: '" + logName + "' (ожидается Rubber Wood Log)");
                }
                catch (Throwable t) {
                    SingleplayerTest.log("R5 rubber planks check failed: " + String.valueOf(t));
                }
            }
            catch (Throwable e) {
                SingleplayerTest.log("R5 audit failed: " + String.valueOf(e));
            }
        });
    }

    private void round6Audit(Minecraft mc, int fx, int fy, int fz) {


        final int ty = fy + 8;
        final int cx = fx + 24;
        this.runOnMainThread(mc, () -> {
            try {
                WorldClient w = mc.currentWorld;

                int[] metas = new int[]{0, 2, 5, 9, 10};
                int[] expect = new int[]{33, 129, 2049, 513, 4};
                String[] names = new String[]{"медь", "золото", "HV", "стекло", "олово"};
                int okTypes = 0;
                int i = 0;
                for (int m : metas) {
                    int x = cx + i * 2;
                    w.setBlockTypeDataNotify((TilePosc)new TilePos(x, ty, fz), IC2Blocks.cable, m);
                    TileEntity te = w.getTileEntity((TilePosc)new TilePos(x, ty, fz));
                    if (te instanceof TileEntityCable c) {
                        int got = c.getConductorBreakdownEnergy();
                        boolean ok = got == expect[i];
                        SingleplayerTest.log("R6 cable type: meta=" + m + " (" + names[i] + ") breakdown=" + got + " (ожидалось " + expect[i] + ") " + (ok ? "OK" : "FAIL"));
                        if (ok) ++okTypes;
                    } else {
                        SingleplayerTest.log("R6 cable type: TE не кабель FAIL (meta=" + m + ")");
                    }
                    ++i;
                }
                SingleplayerTest.log("R6 cable types: " + okTypes + "/5 " + (okTypes == 5 ? "OK" : "FAIL"));

                for (int j = 0; j < metas.length; ++j) {
                    w.setBlockWithNotify(cx + j * 2, ty, fz, 0);
                }


                int mx = cx + 1;
                int mz = fz + 6;
                this.place(mc, mx, ty, mz, "mfsu_r6", IC2Blocks.mfsu);
                w.setBlockTypeDataNotify((TilePosc)new TilePos(mx + 1, ty, mz), IC2Blocks.cable, 0);
                this.place(mc, mx + 2, ty, mz, "batbox_copper_r6", IC2Blocks.batBox);
                TileEntity teM = w.getTileEntity((TilePosc)new TilePos(mx, ty, mz));
                if (teM instanceof TileEntityElectricBlock mfsu) {
                    mfsu.energy = 20000;
                }
                SingleplayerTest.log("R6 throughput: медь, ждём 2с...");
            }
            catch (Throwable e) {
                SingleplayerTest.log("R6 cable setup failed: " + String.valueOf(e));
            }
        });
        SingleplayerTest.sleep(2000L);
        final int[] copperEnergy = new int[]{-1};
        final int[] copperRate = new int[]{-1};
        this.runOnMainThread(mc, () -> {
            try {
                WorldClient w = mc.currentWorld;
                int mx = cx + 1;
                int mz = fz + 6;
                TileEntity teB = w.getTileEntity((TilePosc)new TilePos(mx + 2, ty, mz));
                copperEnergy[0] = teB instanceof TileEntityElectricBlock bat ? bat.energy : -1;

                try {
                    TileEntity teSrc = w.getTileEntity((TilePosc)new TilePos(mx, ty, mz));
                    if (teSrc instanceof IEnergySource srcTE) {
                        EnergyNet net = EnergyNet.getForWorld((World)w);
                        int before = copperEnergy[0];
                        net.emitEnergyFrom(srcTE, 512);
                        TileEntity teB2 = w.getTileEntity((TilePosc)new TilePos(mx + 2, ty, mz));
                        int after = teB2 instanceof TileEntityElectricBlock b2 ? b2.energy : -1;
                        copperRate[0] = after - before;
                    }
                }
                catch (Throwable t) {
                    SingleplayerTest.log("R6 copper manual emit failed: " + String.valueOf(t));
                }
                SingleplayerTest.log("R6 throughput медь: batbox=" + copperEnergy[0] + " EU за 2с, ручной пакет 512 -> +" + copperRate[0] + " EU (кап 32)");


                int bx = mx + 6;
                w.setBlockWithNotify(mx + 2, ty, mz, 0);
                w.setBlockWithNotify(mx + 1, ty, mz, 0);
                this.place(mc, bx, ty, mz, "mfsu_hv_bang", IC2Blocks.mfsu);
                w.setBlockTypeDataNotify((TilePosc)new TilePos(bx + 1, ty, mz), IC2Blocks.cable, 5);
                this.place(mc, bx + 2, ty, mz, "batbox_overvolt", IC2Blocks.batBox);
                TileEntity teBang = w.getTileEntity((TilePosc)new TilePos(bx, ty, mz));
                if (teBang instanceof TileEntityElectricBlock mfsuBang) {
                    mfsuBang.energy = 20000;
                }
                SingleplayerTest.log("R6 overvoltage: ждём взрыва BatBox 2с...");
            }
            catch (Throwable e) {
                SingleplayerTest.log("R6 throughput swap failed: " + String.valueOf(e));
            }
        });
        SingleplayerTest.sleep(2000L);

        this.runOnMainThread(mc, () -> {
            try {
                WorldClient w = mc.currentWorld;
                int mx = cx + 1;
                int mz = fz + 6;
                int bx = mx + 6;
                boolean overvoltBoom = w.getBlockId(bx + 2, ty, mz) == 0;
                SingleplayerTest.log("R6 overvoltage: BatBox под 512 EU/t " + (overvoltBoom ? "взорвался OK (ориг. IC2)" : "цел FAIL"));

                int hx = mx + 10;
                this.place(mc, hx, ty, mz, "mfsu_hv_r6", IC2Blocks.mfsu);
                w.setBlockTypeDataNotify((TilePosc)new TilePos(hx + 1, ty, mz), IC2Blocks.cable, 5);
                this.place(mc, hx + 2, ty, mz, "mfsu_sink_r6", IC2Blocks.mfsu);
                TileEntity teH = w.getTileEntity((TilePosc)new TilePos(hx, ty, mz));
                if (teH instanceof TileEntityElectricBlock mfsuH) {
                    mfsuH.energy = 20000;
                }
                int dNow = w.getBlockData((TilePosc)new TilePos(hx + 1, ty, mz));
                TileEntity teC = w.getTileEntity((TilePosc)new TilePos(hx + 1, ty, mz));
                int bdNow = teC instanceof TileEntityCable cc ? cc.getConductorBreakdownEnergy() : -1;
                SingleplayerTest.log("R6 diag HV-линия: blockData=" + dNow + " teBreakdown=" + bdNow + " (5/2049 = HV)");
                SingleplayerTest.log("R6 throughput: HV (приёмник MFSU), ждём 2с...");
            }
            catch (Throwable e) {
                SingleplayerTest.log("R6 HV setup failed: " + String.valueOf(e));
            }
        });
        SingleplayerTest.sleep(2000L);
        this.runOnMainThread(mc, () -> {
            try {
                WorldClient w = mc.currentWorld;
                int mx = cx + 1;
                int mz = fz + 6;
                int bx = mx + 6;
                int hx = mx + 10;
                boolean overvoltSrcAlive = w.getBlockId(bx, ty, mz) == IC2Blocks.mfsu.id();
                TileEntity teB = w.getTileEntity((TilePosc)new TilePos(hx + 2, ty, mz));
                int hvBlock = w.getBlockId(hx + 2, ty, mz);
                int dRead = w.getBlockData((TilePosc)new TilePos(hx + 1, ty, mz));
                TileEntity teC2 = w.getTileEntity((TilePosc)new TilePos(hx + 1, ty, mz));
                int bdRead = teC2 instanceof TileEntityCable cc2 ? cc2.getConductorBreakdownEnergy() : -1;
                TileEntity teMfsu = w.getTileEntity((TilePosc)new TilePos(hx, ty, mz));
                int mfsuE = teMfsu instanceof TileEntityElectricBlock m2 ? m2.energy : -1;
                int hvEnergy = teB instanceof TileEntityElectricBlock bat ? bat.energy : -1;

                int hvRate = -1;
                try {
                    if (teMfsu instanceof IEnergySource srcTE) {
                        EnergyNet net = EnergyNet.getForWorld((World)w);
                        int before = hvEnergy;
                        net.emitEnergyFrom(srcTE, 512);
                        TileEntity teB2 = w.getTileEntity((TilePosc)new TilePos(hx + 2, ty, mz));
                        int after = teB2 instanceof TileEntityElectricBlock b2 ? b2.energy : -1;
                        hvRate = after - before;
                    }
                }
                catch (Throwable t) {
                    SingleplayerTest.log("R6 hv manual emit failed: " + String.valueOf(t));
                }
                boolean pass = copperRate[0] >= 0 && hvRate > copperRate[0] * 2;
                SingleplayerTest.log("R6 throughput: медь=" + copperRate[0] + " EU/пакет, HV=" + hvRate + " EU/пакет (512 с самозамыканием источника, без капа = 255) => " + (pass ? "медь ограничена, HV быстрее OK" : "РАЗНИЦЫ НЕТ FAIL") + " [data=" + dRead + " teBD=" + bdRead + " блок=" + hvBlock + " ист=" + mfsuE + " накоплено=" + hvEnergy + "]");

                w.setBlockWithNotify(bx + 1, ty, mz, 0);
                w.setBlockWithNotify(bx, ty, mz, 0);
                w.setBlockWithNotify(hx + 2, ty, mz, 0);
                w.setBlockWithNotify(hx + 1, ty, mz, 0);
                w.setBlockWithNotify(hx, ty, mz, 0);
                w.setBlockWithNotify(mx + 2, ty, mz, 0);
                w.setBlockWithNotify(mx + 1, ty, mz, 0);
                w.setBlockWithNotify(mx, ty, mz, 0);

                boolean noRaw = true;
                try {
                    IC2Items.class.getField("rawUranium");
                    noRaw = false;
                }
                catch (NoSuchFieldException noSuchFieldException) {

                }
                SingleplayerTest.log("R6 uranium merge: rawUranium удалён=" + (noRaw ? "OK" : "FAIL") + ", uraniumItem=" + (IC2Items.uraniumItem != null ? "есть OK" : "нет FAIL"));
            }
            catch (Throwable e) {
                SingleplayerTest.log("R6 throughput check failed: " + String.valueOf(e));
            }
        });

        final int rx = fx + 140;
        final int rz = fz + 140;
        final int ry = fy + 4;
        try {

            mc.currentWorld.setBlockWithNotify(fx + 60, fy, fz + 60, Blocks.STONE.id());
            mc.thePlayer.moveTo((double)fx + 60.5, (double)(fy + 1), (double)fz + 60.5, 0.0f, 0.0f);
            SingleplayerTest.log("R6 meltdown: телепорт для загрузки чанков (дистанция до реактора ~113 > 80 радиуса урона)");
            SingleplayerTest.sleep(3000L);
        }
        catch (Throwable e) {
            SingleplayerTest.log("R6 meltdown teleport failed: " + String.valueOf(e));
        }
        this.runOnMainThread(mc, () -> {
            try {
                WorldClient w = mc.currentWorld;

                w.setBlockWithNotify(rx, ry - 1, rz, Blocks.STONE.id());
                int[][] markers = new int[][]{{6, 0}, {-6, 0}, {10, 0}, {-10, 0}, {0, 6}, {0, -6}, {0, 14}, {14, 0}};
                for (int[] m : markers) {
                    w.setBlockWithNotify(rx + m[0], ry, rz + m[1], Blocks.STONE.id());
                }
                this.place(mc, rx, ry, rz, "reactor_meltdown", IC2Blocks.nuclearReactor);
                TileEntity teR = w.getTileEntity((TilePosc)new TilePos(rx, ry, rz));
                if (teR instanceof TileEntityNuclearReactor reactor) {
                    reactor.heat = 99999;
                    reactor.updateTicker = 19;
                    SingleplayerTest.log("R6 meltdown: реактор heat=99999, ждём 3с...");
                } else {
                    SingleplayerTest.log("R6 meltdown: TE реактора не найден FAIL");
                }
            }
            catch (Throwable e) {
                SingleplayerTest.log("R6 meltdown setup failed: " + String.valueOf(e));
            }
        });
        SingleplayerTest.sleep(3000L);
        this.runOnMainThread(mc, () -> {
            try {
                WorldClient w = mc.currentWorld;
                int reactorId = w.getBlockId(rx, ry, rz);
                boolean gone = reactorId == 0;
                int destroyed = 0;
                int[][] markers = new int[][]{{6, 0}, {-6, 0}, {10, 0}, {-10, 0}, {0, 6}, {0, -6}, {0, 14}, {14, 0}};
                for (int[] m : markers) {
                    boolean alive = w.getBlockId(rx + m[0], ry, rz + m[1]) == Blocks.STONE.id();
                    if (!alive) ++destroyed;
                }
                SingleplayerTest.log("R6 meltdown: реактор=" + (gone ? "исчез OK" : "id=" + reactorId + " FAIL") + ", разрушено маркеров=" + destroyed + "/8 (взрыв " + (destroyed >= 3 ? "ОГРОМНЫЙ OK" : "слабый FAIL") + ")");
            }
            catch (Throwable e) {
                SingleplayerTest.log("R6 meltdown check failed: " + String.valueOf(e));
            }
        });
    }

    private void round7Audit(Minecraft mc, int fx, int fy, int fz) {


        final int ty = fy + 8;
        final int cx = fx + 24;
        final int[] types = new int[]{0, 2, 5, 9, 10};
        final String[] names = new String[]{"медь", "золото", "HV", "стекло", "олово"};
        final int[] emits = new int[]{500, 500, 500, 600, 500};
        final int[] expectCap = new int[]{32, 127, 497, 512, 3};
        final int[] liveRows = new int[]{fz + 34, fz + 38, fz + 42};
        final int longRow = fz + 46;
        SingleplayerTest.sleep(400L);
        this.runOnMainThread(mc, () -> {
            try {
                WorldClient w = mc.currentWorld;
                for (int i = 0; i < types.length; ++i) {
                    int sx = cx + i * 6;
                    int mz = fz + 30;
                    this.place(mc, sx, ty, mz, "gen_r7cap_" + i, IC2Blocks.generator);
                    for (int k = 1; k <= 3; ++k) {
                        w.setBlockTypeDataNotify((TilePosc)new TilePos(sx + k, ty, mz), IC2Blocks.cable, types[i]);
                    }
                    this.place(mc, sx + 4, ty, mz, "sink_r7cap_" + i, IC2Blocks.mfsu);
                    int delta = -1;
                    TileEntity teG = w.getTileEntity((TilePosc)new TilePos(sx, ty, mz));
                    TileEntity teS = w.getTileEntity((TilePosc)new TilePos(sx + 4, ty, mz));
                    if (teG instanceof IEnergySource src && teS instanceof TileEntityElectricBlock sink) {
                        EnergyNet.getForWorld((World)w).emitEnergyFrom(src, emits[i]);
                        delta = sink.energy;
                    }
                    boolean ok = delta == expectCap[i];
                    SingleplayerTest.log("R7 кап " + names[i] + ": пакет " + emits[i] + " -> +" + delta + " EU (ожид. " + expectCap[i] + ") " + (ok ? "OK" : "FAIL"));
                    for (int k = 0; k <= 4; ++k) {
                        w.setBlockWithNotify(sx + k, ty, mz, 0);
                    }
                }
            }
            catch (Throwable e) {
                SingleplayerTest.log("R7 cap matrix failed: " + String.valueOf(e));
            }
        });
        SingleplayerTest.sleep(400L);
        this.runOnMainThread(mc, () -> {
            try {
                WorldClient w = mc.currentWorld;
                for (int i = 0; i < types.length; ++i) {
                    int sx = cx + i * 6;
                    this.place(mc, sx, ty, liveRows[0], "solar_r7_" + i, IC2Blocks.solarPanel);
                    TileEntity teSolar = w.getTileEntity((TilePosc)new TilePos(sx, ty, liveRows[0]));
                    if (teSolar instanceof TileEntityBaseGenerator g) {
                        g.storage = 400;
                    }
                    for (int k = 1; k <= 3; ++k) {
                        w.setBlockTypeDataNotify((TilePosc)new TilePos(sx + k, ty, liveRows[0]), IC2Blocks.cable, types[i]);
                    }
                    this.place(mc, sx + 4, ty, liveRows[0], "sink_sol_r7_" + i, IC2Blocks.batBox);
                    this.place(mc, sx, ty, liveRows[1], "gen_r7live_" + i, IC2Blocks.generator);
                    TileEntity teGen = w.getTileEntity((TilePosc)new TilePos(sx, ty, liveRows[1]));
                    if (teGen instanceof TileEntityBaseGenerator g) {
                        g.storage = 2000;
                    }
                    for (int k = 1; k <= 3; ++k) {
                        w.setBlockTypeDataNotify((TilePosc)new TilePos(sx + k, ty, liveRows[1]), IC2Blocks.cable, types[i]);
                    }
                    this.place(mc, sx + 4, ty, liveRows[1], "sink_gen_r7_" + i, IC2Blocks.batBox);
                    this.place(mc, sx, ty, liveRows[2], "mfsu_r7_" + i, IC2Blocks.mfsu);
                    TileEntity teM = w.getTileEntity((TilePosc)new TilePos(sx, ty, liveRows[2]));
                    if (teM instanceof TileEntityElectricBlock b) {
                        b.energy = 20000;
                    }
                    for (int k = 1; k <= 3; ++k) {
                        w.setBlockTypeDataNotify((TilePosc)new TilePos(sx + k, ty, liveRows[2]), IC2Blocks.cable, types[i]);
                    }
                    this.place(mc, sx + 4, ty, liveRows[2], "sink_mfsu_r7_" + i, IC2Blocks.mfsu);
                }
                SingleplayerTest.log("R7 живые линии (3 блока): солнце 1/t и генератор 5/t -> BatBox, MFSU 512/t -> MFSU; ждём 3с...");
            }
            catch (Throwable e) {
                SingleplayerTest.log("R7 live setup failed: " + String.valueOf(e));
            }
        });
        SingleplayerTest.sleep(3000L);
        this.runOnMainThread(mc, () -> {
            try {
                WorldClient w = mc.currentWorld;
                for (int i = 0; i < types.length; ++i) {
                    int sx = cx + i * 11;
                    this.place(mc, sx, ty, longRow, "gen8_r7_" + i, IC2Blocks.generator);
                    TileEntity teG = w.getTileEntity((TilePosc)new TilePos(sx, ty, longRow));
                    if (teG instanceof TileEntityBaseGenerator g) {
                        g.storage = 5000;
                    }
                    for (int k = 1; k <= 8; ++k) {
                        w.setBlockTypeDataNotify((TilePosc)new TilePos(sx + k, ty, longRow), IC2Blocks.cable, types[i]);
                    }
                    this.place(mc, sx + 9, ty, longRow, "sink8_r7_" + i, IC2Blocks.batBox);
                }
                SingleplayerTest.log("R7 длинные линии x8 от генератора 5/t, ждём 2.5с...");
            }
            catch (Throwable e) {
                SingleplayerTest.log("R7 long setup failed: " + String.valueOf(e));
            }
        });
        SingleplayerTest.sleep(2500L);
        this.runOnMainThread(mc, () -> {
            try {
                WorldClient w = mc.currentWorld;
                for (int i = 0; i < types.length; ++i) {
                    int sx = cx + i * 6;
                    int eSol = this.storageOf(w, sx + 4, ty, liveRows[0]);
                    int eGen = this.storageOf(w, sx + 4, ty, liveRows[1]);
                    int eM = this.storageOf(w, sx + 4, ty, liveRows[2]);
                    SingleplayerTest.log("R7 живой " + names[i] + " x3: солнце1/t=" + eSol + " (физика: медь~60/золото 0/HV 0/стекло~60/олово~60), ген5/t=" + eGen + " (5/4/2/5/3 xt), MFSU512/t=" + eM + " (32/127/509/512/3 xt)");
                }
                for (int i = 0; i < types.length; ++i) {
                    int sx = cx + i * 11;
                    int e8 = this.storageOf(w, sx + 9, ty, longRow);
                    SingleplayerTest.log("R7 длинная " + names[i] + " x8 от ген 5/t: " + e8 + " EU (потери 1.6/4.0/8.0/0.2/0.008: медь~100, золото~50, HV 0 cutoff, стекло~250, олово~150)");
                }
                int withDesc = 0;
                String glassDesc = "";
                String tinDesc = "";
                for (String key : new String[]{"cable_copper", "cable_copper_uninsulated", "cable_gold", "cable_gold_insulated", "cable_gold_insulated_2x", "cable_hv", "cable_hv_insulated", "cable_hv_insulated_2x", "cable_hv_insulated_4x", "cable_glass_fibre", "cable_tin"}) {
                    String d = I18n.getInstance().translateKey("item.ic2." + key + ".desc");
                    if (d != null && !d.isEmpty() && !d.equals("item.ic2." + key + ".desc")) {
                        ++withDesc;
                    }
                    if (key.equals("cable_glass_fibre")) {
                        glassDesc = d;
                    }
                    if (key.equals("cable_tin")) {
                        tinDesc = d;
                    }
                }
                SingleplayerTest.log("R7 описания кабелей: " + withDesc + "/11 с .desc, стекло=[" + glassDesc + "], олово=[" + tinDesc + "]");
                for (int i = 0; i < types.length; ++i) {
                    int sx = cx + i * 6;
                    for (int k = 0; k <= 4; ++k) {
                        w.setBlockWithNotify(sx + k, ty, liveRows[0], 0);
                        w.setBlockWithNotify(sx + k, ty, liveRows[1], 0);
                        w.setBlockWithNotify(sx + k, ty, liveRows[2], 0);
                    }
                }
                for (int i = 0; i < types.length; ++i) {
                    int sx = cx + i * 11;
                    for (int k = 0; k <= 9; ++k) {
                        w.setBlockWithNotify(sx + k, ty, longRow, 0);
                    }
                }
            }
            catch (Throwable e) {
                SingleplayerTest.log("R7 results failed: " + String.valueOf(e));
            }
        });
    }

    private int storageOf(WorldClient w, int x, int y, int z) {
        TileEntity te = w.getTileEntity((TilePosc)new TilePos(x, y, z));
        if (te instanceof TileEntityElectricBlock b) {
            return b.energy;
        }
        if (te instanceof TileEntityBaseGenerator g) {
            return g.storage;
        }
        return -1;
    }


    private void round8Audit(Minecraft mc) {
        this.runOnMainThread(mc, () -> {
            RecipeRegistry original = Registries.RECIPES;
            try {
                List<RecipeEntryBase<?, ?, ?>> all = original.getAllSerializableRecipes();
                ArrayList<String> ic2Jsons = new ArrayList<String>();
                ArrayList<String> ic2Keys = new ArrayList<String>();
                int total = 0;
                for (RecipeEntryBase<?, ?, ?> r : all) {
                    ++total;
                    if (!r.toString().startsWith("ic2:")) continue;
                    ic2Jsons.add(net.minecraft.core.data.DataLoader.serializeRecipe(r));
                    ic2Keys.add(r.toString());
                }
                int uuBefore = this.countRecipesIn(original, "ic2", "mass_fabricator");
                int machineBefore = this.countRecipesIn(original, "ic2", "macerator")
                                + this.countRecipesIn(original, "ic2", "extractor")
                                + this.countRecipesIn(original, "ic2", "compressor")
                                + this.countRecipesIn(original, "ic2", "canner");

                Registries.RECIPES = new RecipeRegistry();

                ic2.IC2Recipes.initNamespaces();

                int synced = 0;
                String firstFail = null;
                for (String json : ic2Jsons) {
                    try {
                        net.minecraft.core.net.packet.PacketRecipeSync packet = new net.minecraft.core.net.packet.PacketRecipeSync();
                        packet.recipe = json;
                        packet.maxRecipes = ic2Jsons.size();
                        net.minecraft.core.data.DataLoader.loadRecipeFromServer(packet);
                        ++synced;
                    }
                    catch (Throwable e) {
                        if (firstFail == null) {
                            firstFail = e.getClass().getSimpleName() + ": " + String.valueOf(e);
                        }
                    }
                }
                int uuAfter = this.countRecipesIn(Registries.RECIPES, "ic2", "mass_fabricator");
                int machineAfter = this.countRecipesIn(Registries.RECIPES, "ic2", "macerator")
                                + this.countRecipesIn(Registries.RECIPES, "ic2", "extractor")
                                + this.countRecipesIn(Registries.RECIPES, "ic2", "compressor")
                                + this.countRecipesIn(Registries.RECIPES, "ic2", "canner");
                SingleplayerTest.log("R8 login sync: " + synced + "/" + ic2Jsons.size() + " ic2 recipes (total serializable " + total + ") "
                                + (firstFail == null && synced == ic2Jsons.size() ? "OK" : "FAIL: " + firstFail));
                SingleplayerTest.log("R8 mass_fabricator group: before=" + uuBefore + " after=" + uuAfter + " "
                                + (uuAfter == uuBefore && uuBefore > 0 ? "OK" : "FAIL"));
                SingleplayerTest.log("R8 machine groups: before=" + machineBefore + " after=" + machineAfter + " "
                                + (machineAfter == machineBefore && machineBefore > 0 ? "OK" : "FAIL"));

                net.minecraft.core.data.registry.recipe.RecipeNamespace nsNow = Registries.RECIPES.getItem("ic2");
                boolean shells = nsNow != null
                                && nsNow.getItem("macerator") != null
                                && nsNow.getItem("mass_fabricator") != null
                                && nsNow.getItem("extractor") != null
                                && nsNow.getItem("compressor") != null
                                && nsNow.getItem("canner") != null
                                && nsNow.getItem("workbench") != null
                                && nsNow.getItem("furnace") != null
                                && nsNow.getItem("blast_furnace") != null
                                && nsNow.getItem("trommel") != null;
                SingleplayerTest.log("R8 group shells after initNamespaces: " + (shells ? "OK (все 9 групп ic2 созданы)" : "FAIL"));
            }
            catch (Throwable e) {
                SingleplayerTest.log("R8 login simulation failed: " + String.valueOf(e));
            }
            finally {
                Registries.RECIPES = original;
            }
        });
        SingleplayerTest.sleep(1500L);
        this.runOnMainThread(mc, () -> {
            try {
                int uu = this.countRecipesIn(Registries.RECIPES, "ic2", "mass_fabricator");
                boolean restored = Registries.RECIPES.getItem("ic2") != null && uu > 0;
                SingleplayerTest.log("R8 registry restored after simulation: mass_fabricator=" + uu + " " + (restored ? "OK" : "FAIL"));
            }
            catch (Throwable e) {
                SingleplayerTest.log("R8 restore check failed: " + String.valueOf(e));
            }
        });
    }

    private int countRecipesIn(RecipeRegistry registry, String namespace, String group) {
        try {
            net.minecraft.core.data.registry.recipe.RecipeNamespace ns = registry.getItem(namespace);
            if (ns == null) {
                return 0;
            }
            RecipeGroup g = ns.getItem(group);
            return g == null ? 0 : g.getAllRecipes().size();
        }
        catch (Throwable e) {
            return -1;
        }
    }

    private void place(Minecraft mc, int x, int y, int z, String key, Block<?> b) {
        try {
            if (b == null) {
                SingleplayerTest.log("  block missing: " + key);
                return;
            }
            mc.currentWorld.setBlockWithNotify(x, y, z, b.id());
            SingleplayerTest.log("  placed " + key + " (id " + b.id() + ") -> " + mc.currentWorld.getBlockId(x, y, z));
        }
        catch (Throwable e) {
            SingleplayerTest.log("  FAILED placing " + key + ": " + String.valueOf(e));
        }
    }

    private void placeCable(Minecraft mc, int x, int y, int z, String key, int type) {
        try {
            if (IC2Blocks.cable == null) {
                SingleplayerTest.log("  cable block missing: " + key);
                return;
            }
            mc.currentWorld.setBlockWithNotify(x, y, z, IC2Blocks.cable.id());
            mc.currentWorld.setBlockMetadataWithNotify(x, y, z, type);
            SingleplayerTest.log("  placed cable " + key + " type " + type + " -> " + mc.currentWorld.getBlockId(x, y, z) + "/" + mc.currentWorld.getBlockData((TilePosc)new TilePos(x, y, z)));
        }
        catch (Throwable e) {
            SingleplayerTest.log("  FAILED placing cable " + key + ": " + String.valueOf(e));
        }
    }

    private String screenName(Minecraft mc) {
        return mc.currentScreen != null ? mc.currentScreen.getClass().getSimpleName() : "null";
    }


    private static void log(String s) {
        IC2.LOGGER.info("[SP-TEST] " + s);
        List<String> list = results;
        synchronized (list) {
            results.add(s);
        }
    }


    private static void finish() {
        finished = true;
        IC2.LOGGER.info("[SP-TEST] ===== RESULTS =====");
        List<String> list = results;
        synchronized (list) {
            for (String r : results) {
                IC2.LOGGER.info("[SP-TEST] " + r);
            }
        }
        IC2.LOGGER.info("[SP-TEST] ===== SPTEST_DONE =====");
    }

    private static boolean await(CountDownLatch latch, int seconds) {
        try {
            return latch.await(seconds, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            return false;
        }
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        }
        catch (InterruptedException interruptedException) {

        }
    }


    private static void cableDebugAudit(Minecraft mc, int fx, int fz, int fy) {
        try {
            World w = mc.currentWorld;
            int cx = fx + 3;
            int cz = fz;
            TilePos cpos = new TilePos(cx, fy, cz);
            Block cblock = w.getBlockType((TilePosc)cpos);
            BlockLogicCable clogic = (BlockLogicCable)IC2Blocks.cable.getLogic();
            SingleplayerTest.log("CABLE debug: block=" + (cblock == null ? "null" : cblock.getKey()) + " meta=" + w.getBlockData((TilePosc)cpos) + " canCollideCheck=" + (cblock != null && cblock.canCollideCheck(w.getBlockData((TilePosc)cpos), false) != false) + " logic.isCollidable=" + clogic.isCollidable());
            Vector3d start = new Vector3d((double)cx + 0.5, (double)fy + 2.5, (double)cz + 0.5);
            Vector3d end = new Vector3d((double)cx + 0.5, (double)fy - 1.5, (double)cz + 0.5);
            HitResult direct = cblock == null ? null : cblock.collisionRayTrace((World)w, (TilePosc)cpos, (Vector3dc)start, (Vector3dc)end, false);
            SingleplayerTest.log("CABLE debug: direct collisionRayTrace=" + (String)(direct == null ? "null" : "HIT " + direct.getClass().getSimpleName()));
            SingleplayerTest.log("CABLE debug: blocks on ray: +2=" + SingleplayerTest.blockName((World)w, cx, fy + 2, cz) + " +1=" + SingleplayerTest.blockName((World)w, cx, fy + 1, cz) + " 0=" + SingleplayerTest.blockName((World)w, cx, fy, cz) + " -1=" + SingleplayerTest.blockName((World)w, cx, fy - 1, cz));
            HitResult hit = w.checkBlockCollisionBetweenPoints((Vector3dc)start, (Vector3dc)end);
            boolean v0;
            if (hit instanceof HitResult.Tile) {
                HitResult.Tile t = (HitResult.Tile)hit;
                v0 = t.tilePos.x() == cx && t.tilePos.y() == fy && t.tilePos.z() == cz;
            } else {
                v0 = false;
            }
            boolean hitCable = v0;
            SingleplayerTest.log("CABLE: raytrace vertical through cable center hit=" + hitCable + " (expect true \u2014 \u043a\u0430\u0431\u0435\u043b\u044c \u0432\u044b\u0431\u0438\u0440\u0430\u0435\u043c)");
            Vector3d start2 = new Vector3d((double)cx + 0.5, (double)fy + 0.5, (double)cz - 2.0);
            Vector3d end2 = new Vector3d((double)cx + 0.5, (double)fy + 0.5, (double)cz + 2.0);
            HitResult hit2 = w.checkBlockCollisionBetweenPoints((Vector3dc)start2, (Vector3dc)end2);
            boolean v1;
            if (hit2 instanceof HitResult.Tile) {
                HitResult.Tile t2 = (HitResult.Tile)hit2;
                v1 = t2.tilePos.x() == cx && t2.tilePos.y() == fy && t2.tilePos.z() == cz;
            } else {
                v1 = false;
            }
            boolean hit2cable = v1;
            SingleplayerTest.log("CABLE: raytrace horizontal into cable hit=" + hit2cable);
            AABBdc bounds = ((BlockLogicCable)IC2Blocks.cable.getLogic()).getBoundsFromState((WorldSource)w, (TilePosc)cpos);
            SingleplayerTest.log("CABLE: bounds=" + String.format("%.2f..%.2f (\u0446\u0435\u043d\u0442\u0440 %.3f..%.3f)", new Object[]{bounds.minY(), bounds.maxY(), bounds.minX(), bounds.maxX()}) + " collision=" + (clogic.getCollisionAABB((WorldSource)w, (TilePosc)cpos) == null ? "\u043d\u0435\u0442 (\u043f\u0440\u043e\u0445\u043e\u0434\u0438\u043c)" : "\u0435\u0441\u0442\u044c"));
        }
        catch (Throwable e) {
            SingleplayerTest.log("CABLE test failed: " + String.valueOf(e));
        }
    }

    private static class ActionScreen
    extends Screen {
        private final Runnable action;
        private boolean done = false;
        private int ticksAfterAction = -1;

        ActionScreen(Runnable action) {
            this.action = action;
        }

        public void tick() {
            if (!this.done) {
                this.done = true;
                try {
                    this.action.run();
                }
                catch (Throwable e) {
                    IC2.LOGGER.error("[SP-TEST] action failed: ", e);
                }
                this.ticksAfterAction = 0;
                return;
            }
            ++this.ticksAfterAction;
            if (this.ticksAfterAction >= 2 && this.mc.currentScreen == this && this.mc.currentWorld != null) {
                this.mc.displayScreen(null);
            }
        }

        public void render(int mx, int my, float partialTick) {
        }
    }
}

