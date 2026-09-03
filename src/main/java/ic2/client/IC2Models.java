

package ic2.client;

import ic2.IC2;
import ic2.IC2Blocks;
import ic2.IC2Items;
import ic2.block.BlockModelCable;
import ic2.block.BlockModelIC2Machine;
import ic2.block.BlockModelRubLeaves;
import ic2.block.BlockModelRubWood;
import ic2.client.EntityRendererIC2Explosive;
import ic2.client.EntityRendererMiningLaser;
import ic2.entity.EntityDynamite;
import ic2.entity.EntityIC2Explosive;
import ic2.entity.EntityMiningLaser;
import ic2.si.SIConverters;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.minecraft.client.render.EntityRendererDispatcher;
import net.minecraft.client.render.block.color.BlockColorCustom;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.client.render.block.model.BlockModel;
import net.minecraft.client.render.block.model.BlockModelCrossedSquares;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.block.model.BlockModelTransparent;
import net.minecraft.client.render.block.model.generic.BlockModelGenericDoor;
import net.minecraft.client.render.colorizer.Colorizers;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererSprite;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.core.block.Block;
import net.minecraft.core.item.Item;
import net.minecraft.core.util.helper.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IC2Models {
    public static final Logger LOGGER = LoggerFactory.getLogger((String)(IC2.MOD_ID + "/models"));

    public void initBlockModels(BlockModelDispatcher dispatcher) {
        LOGGER.info("Initializing IC2 block models...");
        for (Block b : new Block[]{IC2Blocks.oreCopper, IC2Blocks.oreCopperBasalt, IC2Blocks.oreCopperLimestone, IC2Blocks.oreCopperGranite, IC2Blocks.oreCopperPermafrost}) {
            dispatcher.addDispatch(b, this.allSides(b, "ore_copper_" + IC2Models.stoneSuffix(b)));
        }
        for (Block b : new Block[]{IC2Blocks.oreTin, IC2Blocks.oreTinBasalt, IC2Blocks.oreTinLimestone, IC2Blocks.oreTinGranite, IC2Blocks.oreTinPermafrost}) {
            dispatcher.addDispatch(b, this.allSides(b, "ore_tin_" + IC2Models.stoneSuffix(b)));
        }
        for (Block b : new Block[]{IC2Blocks.oreUranium, IC2Blocks.oreUraniumBasalt, IC2Blocks.oreUraniumLimestone, IC2Blocks.oreUraniumGranite, IC2Blocks.oreUraniumPermafrost}) {
            dispatcher.addDispatch(b, this.allSides(b, "ore_uranium_" + IC2Models.stoneSuffix(b)));
        }
        dispatcher.addDispatch(IC2Blocks.copperBlock, this.allSides(IC2Blocks.copperBlock, "copper_block"));
        dispatcher.addDispatch(IC2Blocks.tinBlock, this.allSides(IC2Blocks.tinBlock, "tin_block"));
        dispatcher.addDispatch(IC2Blocks.bronzeBlock, this.allSides(IC2Blocks.bronzeBlock, "bronze_block"));
        dispatcher.addDispatch(IC2Blocks.uraniumBlock, this.allSides(IC2Blocks.uraniumBlock, "uranium_block"));
        dispatcher.addDispatch(IC2Blocks.machineBlock, this.allSides(IC2Blocks.machineBlock, "machine_casing"));
        dispatcher.addDispatch(IC2Blocks.advancedMachineBlock, this.allSides(IC2Blocks.advancedMachineBlock, "adv_machine_block"));
        dispatcher.addDispatch(IC2Blocks.ironFurnace, (BlockModel)new BlockModelIC2Machine(IC2Blocks.ironFurnace, "iron_furnace_top", "iron_furnace_bottom", "iron_furnace_side", "iron_furnace_front", "iron_furnace_front_active"));
        dispatcher.addDispatch(IC2Blocks.electricFurnace, (BlockModel)new BlockModelIC2Machine(IC2Blocks.electricFurnace, "machine_top", "machine_casing", "machine_side", "elec_furnace_front", "elec_furnace_front_active"));
        dispatcher.addDispatch(IC2Blocks.macerator, (BlockModel)new BlockModelIC2Machine(IC2Blocks.macerator, "machine_top", "macerator_bottom", "macerator_bottom", "macerator_front", "macerator_front"));
        dispatcher.addDispatch(IC2Blocks.extractor, (BlockModel)new BlockModelIC2Machine(IC2Blocks.extractor, "extractor_top", "machine_casing", "extractor_side", "extractor_front", "extractor_front_active"));
        dispatcher.addDispatch(IC2Blocks.compressor, (BlockModel)new BlockModelIC2Machine(IC2Blocks.compressor, "machine_top", "machine_casing", "machine_side", "compressor_front", "compressor_front_active"));
        dispatcher.addDispatch(IC2Blocks.canner, (BlockModel)new BlockModelIC2Machine(IC2Blocks.canner, "machine_top", "machine_casing", "machine_side", "canner_front", "canner_front_active"));
        dispatcher.addDispatch(IC2Blocks.recycler, (BlockModel)new BlockModelIC2Machine(IC2Blocks.recycler, "machine_top", "recycler_bottom", "machine_side", "machine_side", null));
        dispatcher.addDispatch(IC2Blocks.electrolyzer, (BlockModel)new BlockModelIC2Machine(IC2Blocks.electrolyzer, "machine_top", "machine_casing", "machine_casing", "mfe_front", null));
        dispatcher.addDispatch(IC2Blocks.inductionFurnace, (BlockModel)new BlockModelIC2Machine(IC2Blocks.inductionFurnace, "machine_top", "machine_casing", "machine_side", "induction_front", "induction_front_active"));
        dispatcher.addDispatch(IC2Blocks.massFabricator, this.allSides(IC2Blocks.massFabricator, "adv_machine_block"));
        dispatcher.addDispatch(IC2Blocks.terraformer, (BlockModel)new BlockModelIC2Machine(IC2Blocks.terraformer, "machine_top", "machine_casing", "machine_side", "terraformer_front", null));
        dispatcher.addDispatch(IC2Blocks.miner, (BlockModel)new BlockModelIC2Machine(IC2Blocks.miner, "miner_top", "machine_casing", "machine_side", "miner_front", null));
        dispatcher.addDispatch(IC2Blocks.pump, (BlockModel)this.allSides(IC2Blocks.pump, "machine_casing").setTex(IC2.MOD_ID + ":block/machine_top", new Side[]{Side.TOP}));
        dispatcher.addDispatch(IC2Blocks.magnetizer, (BlockModel)this.allSides(IC2Blocks.magnetizer, "machine_casing").setTex(IC2.MOD_ID + ":block/machine_top", new Side[]{Side.TOP}));
        dispatcher.addDispatch(IC2Blocks.generator, (BlockModel)new BlockModelIC2Machine(IC2Blocks.generator, "machine_top", "machine_casing", "machine_casing", "generator_front", "generator_front_active"));
        dispatcher.addDispatch(IC2Blocks.geothermalGenerator, (BlockModel)new BlockModelIC2Machine(IC2Blocks.geothermalGenerator, "machine_top", "machine_casing", "machine_casing", "geothermal_front", "geothermal_front_active"));
        dispatcher.addDispatch(IC2Blocks.waterMill, this.allSides(IC2Blocks.waterMill, "water_mill_side"));
        dispatcher.addDispatch(IC2Blocks.solarPanel, (BlockModel)this.allSides(IC2Blocks.solarPanel, "machine_casing").setTex(IC2.MOD_ID + ":block/solar_panel_top", new Side[]{Side.TOP}));
        dispatcher.addDispatch(IC2Blocks.windMill, this.allSides(IC2Blocks.windMill, "windmill_front"));
        dispatcher.addDispatch(IC2Blocks.nuclearReactor, (BlockModel)new BlockModelIC2Machine(IC2Blocks.nuclearReactor, "reactor_top", "reactor_side", "reactor_side", "reactor_side", "reactor_core_active"));
        dispatcher.addDispatch(IC2Blocks.reactorChamber, this.allSides(IC2Blocks.reactorChamber, "reactor_chamber"));
        dispatcher.addDispatch(IC2Blocks.batBox, (BlockModel)new BlockModelIC2Machine(IC2Blocks.batBox, "machine_top", "batbox_side", "batbox_side", "batbox_front", null));
        dispatcher.addDispatch(IC2Blocks.mfe, (BlockModel)new BlockModelIC2Machine(IC2Blocks.mfe, "machine_top", "machine_casing", "machine_casing", "mfe_front", null));
        dispatcher.addDispatch(IC2Blocks.mfsu, (BlockModel)new BlockModelIC2Machine(IC2Blocks.mfsu, "machine_top", "mfsu_side", "mfsu_side", "mfsu_front", null));
        dispatcher.addDispatch(IC2Blocks.transformerLV, (BlockModel)new BlockModelIC2Machine(IC2Blocks.transformerLV, "machine_top", "machine_casing", "machine_casing", "lv_transformer_front", null));
        dispatcher.addDispatch(IC2Blocks.transformerMV, (BlockModel)new BlockModelIC2Machine(IC2Blocks.transformerMV, "machine_top", "machine_casing", "machine_casing", "mv_transformer_front", null));
        dispatcher.addDispatch(IC2Blocks.transformerHV, (BlockModel)new BlockModelIC2Machine(IC2Blocks.transformerHV, "machine_top", "machine_casing", "machine_casing", "hv_transformer_front", null));
        dispatcher.addDispatch(IC2Blocks.cable, (BlockModel)new BlockModelCable(IC2Blocks.cable));
        dispatcher.addDispatch(IC2Blocks.luminator, (BlockModel)this.allSides(IC2Blocks.luminator, "luminator").onRenderLayer(1));
        dispatcher.addDispatch(IC2Blocks.personalSafe, (BlockModel)new BlockModelIC2Machine(IC2Blocks.personalSafe, "personal_safe_top", "personal_safe_bottom", "personal_safe_side", "personal_safe_front", null));
        dispatcher.addDispatch(IC2Blocks.tradeOMat, (BlockModel)new BlockModelIC2Machine(IC2Blocks.tradeOMat, "machine_top", "machine_casing", "machine_side", "tradeomat_front", null));
        dispatcher.addDispatch(IC2Blocks.teleporter, this.allSides(IC2Blocks.teleporter, "teleporter_side"));
        dispatcher.addDispatch(IC2Blocks.teslaCoil, this.allSides(IC2Blocks.teslaCoil, "machine_casing"));
        dispatcher.addDispatch(IC2Blocks.reinforcedStone, this.allSides(IC2Blocks.reinforcedStone, "reinforced_stone"));
        dispatcher.addDispatch(IC2Blocks.reinforcedGlass, (BlockModel)this.allSides(IC2Blocks.reinforcedGlass, "reinforced_glass").onRenderLayer(1));
        dispatcher.addDispatch(IC2Blocks.meshSteel, (BlockModel)new BlockModelTransparent<>(IC2Blocks.meshSteel, true).onRenderLayer(1).setAllTextures(IC2.MOD_ID + ":block/mesh_steel"));
        dispatcher.addDispatch(IC2Blocks.meshSteelCrude, (BlockModel)new BlockModelTransparent<>(IC2Blocks.meshSteelCrude, true).onRenderLayer(1).setAllTextures(IC2.MOD_ID + ":block/mesh_steel_crude"));
        dispatcher.addDispatch(IC2Blocks.quartzGlass, (BlockModel)new BlockModelTransparent<>(IC2Blocks.quartzGlass, false).onRenderLayer(1).setAllTextures(IC2.MOD_ID + ":block/glass_quartz"));
        dispatcher.addDispatch(IC2Blocks.reinforcedDoorBottom, (BlockModel)new BlockModelGenericDoor(IC2Blocks.reinforcedDoorBottom, IC2.MOD_ID + ":block/door/reinforced", true));
        dispatcher.addDispatch(IC2Blocks.reinforcedDoorTop, (BlockModel)new BlockModelGenericDoor(IC2Blocks.reinforcedDoorTop, IC2.MOD_ID + ":block/door/reinforced", false));

        dispatcher.addDispatch(IC2Blocks.ironFence, new net.minecraft.client.render.block.model.BlockModelFence<>(
                        (net.minecraft.core.block.Block<ic2.block.BlockLogicIronFence>)(Object)IC2Blocks.ironFence)
                        .setAllTextures(IC2.MOD_ID + ":block/iron_fence"));
        dispatcher.addDispatch(IC2Blocks.rubberSheet, (BlockModel)this.allSides(IC2Blocks.rubberSheet, "rubber_sheet").onRenderLayer(1));
        dispatcher.addDispatch(IC2Blocks.resinBlock, this.allSides(IC2Blocks.resinBlock, "resin_block"));
        dispatcher.addDispatch(IC2Blocks.bronzeBrick, this.allSides(IC2Blocks.bronzeBrick, "bronze_brick"));
        dispatcher.addDispatch(IC2Blocks.copperBrick, this.allSides(IC2Blocks.copperBrick, "copper_brick"));
        dispatcher.addDispatch(IC2Blocks.bronzeDoorBottom, (BlockModel)new BlockModelGenericDoor(IC2Blocks.bronzeDoorBottom, IC2.MOD_ID + ":block/door/bronze", true));
        dispatcher.addDispatch(IC2Blocks.bronzeDoorTop, (BlockModel)new BlockModelGenericDoor(IC2Blocks.bronzeDoorTop, IC2.MOD_ID + ":block/door/bronze", false));
        dispatcher.addDispatch(IC2Blocks.rubberWood, (BlockModel)new BlockModelRubWood(IC2Blocks.rubberWood));
        dispatcher.addDispatch(IC2Blocks.rubberLeaves, (BlockModel)new BlockModelRubLeaves(IC2Blocks.rubberLeaves, IC2.MOD_ID + ":block/rubber_leaves").onRenderLayer(1));
        dispatcher.addDispatch(IC2Blocks.rubberSapling, (BlockModel)new BlockModelCrossedSquares(IC2Blocks.rubberSapling).setAllTextures(IC2.MOD_ID + ":block/rubber_sapling"));
        dispatcher.addDispatch(IC2Blocks.miningPipe, (BlockModel)this.allSides(IC2Blocks.miningPipe, "mining_pipe").onRenderLayer(1));
        dispatcher.addDispatch(IC2Blocks.miningTip, (BlockModel)this.allSides(IC2Blocks.miningTip, "mining_tip").onRenderLayer(1));
        dispatcher.addDispatch(IC2Blocks.industrialTnt, IC2Models.tntLike(IC2Blocks.industrialTnt, "industrial_tnt"));
        dispatcher.addDispatch(IC2Blocks.nuke, IC2Models.tntLike(IC2Blocks.nuke, "nuke"));
        dispatcher.addDispatch(IC2Blocks.dynamite, IC2Models.tntLike(IC2Blocks.dynamite, "dynamite"));
        dispatcher.addDispatch(IC2Blocks.dynamiteRemote, IC2Models.tntLike(IC2Blocks.dynamiteRemote, "dynamite_remote"));
        dispatcher.addDispatch(IC2Blocks.slagGenerator, (BlockModel)new ic2.block.BlockModelIC2MachineEx(IC2Blocks.slagGenerator, "machine_top", "machine_casing", "machine_side", "slag_front", "slag_front_active"));
        dispatcher.addDispatch(IC2Blocks.thermalGenerator, (BlockModel)new ic2.block.BlockModelIC2MachineEx(IC2Blocks.thermalGenerator, "machine_top", "machine_casing", "machine_casing", "thermal_front", "thermal_front_active"));
        dispatcher.addDispatch(IC2Blocks.turbineSolar, (BlockModel)new ic2.block.BlockModelIC2MachineEx(IC2Blocks.turbineSolar, "turbine_solar_top", "machine_casing", "machine_side", null, null, "turbine_solar_top_active", null, false));
        dispatcher.addDispatch(IC2Blocks.oceanGenerator, (BlockModel)new ic2.block.BlockModelIC2MachineEx(IC2Blocks.oceanGenerator, "machine_casing", "machine_casing", "machine_casing", "ocean_front", null, null, "water_gen_side", true));
        dispatcher.addDispatch(IC2Blocks.waveGenerator, (BlockModel)new ic2.block.BlockModelIC2MachineEx(IC2Blocks.waveGenerator, "machine_casing", "machine_casing", "machine_casing", "wave_front", null, null, "water_gen_side", false));
        dispatcher.addDispatch(IC2Blocks.woodGasser, (BlockModel)new ic2.block.BlockModelIC2MachineEx(IC2Blocks.woodGasser, "wood_gasser_top", "machine_casing", "machine_side", "wood_gasser_front", "wood_gasser_front_active"));
        dispatcher.addDispatch(IC2Blocks.woodGasserElec, (BlockModel)new ic2.block.BlockModelIC2MachineEx(IC2Blocks.woodGasserElec, "wood_gasser_top", "machine_casing", "machine_side", "wood_gasser_elec_front", "wood_gasser_elec_front_active"));
        dispatcher.addDispatch(IC2Blocks.slowGrinder, (BlockModel)new ic2.block.BlockModelIC2MachineEx(IC2Blocks.slowGrinder, "slow_grinder_top", "machine_casing", "machine_side", "slow_grinder_front", null, "slow_grinder_top_active", null, false));
        dispatcher.addDispatch(IC2Blocks.rareEarthExtractor, (BlockModel)new ic2.block.BlockModelIC2MachineEx(IC2Blocks.rareEarthExtractor, "wood_gasser_top", "machine_casing", "rare_earth_side", "rare_earth_front", "rare_earth_front_active", null, "rare_earth_side_active", false));
        dispatcher.addDispatch(IC2Blocks.plasmafier, (BlockModel)new ic2.block.BlockModelIC2MachineEx(IC2Blocks.plasmafier, "plasmafier_top", "plasmafier_bottom", "plasmafier_side", "plasmafier_front", "plasmafier_front_active"));
        dispatcher.addDispatch(IC2Blocks.pesu, (BlockModel)new ic2.block.BlockModelIC2MachineEx(IC2Blocks.pesu, "machine_top", "pesu_side", "pesu_side", "pesu_front", null));
        dispatcher.addDispatch(IC2Blocks.transformerIV, (BlockModel)new ic2.block.BlockModelIC2MachineEx(IC2Blocks.transformerIV, "machine_top", "machine_casing", "machine_casing", "transformer_iv_front", null));
        dispatcher.addDispatch(IC2Blocks.iridiumStone, this.allSides(IC2Blocks.iridiumStone, "iridium_stone"));
        if (SIConverters.isCatalystInstalled()) {
            dispatcher.addDispatch(IC2Blocks.converterEuToCatalyst, this.allSides(IC2Blocks.converterEuToCatalyst, "converter_eu_to_catalyst"));
            dispatcher.addDispatch(IC2Blocks.converterCatalystToEu, this.allSides(IC2Blocks.converterCatalystToEu, "converter_catalyst_to_eu"));
        }
        if (IC2Blocks.converterEuToEnergy != null) {
            dispatcher.addDispatch(IC2Blocks.converterEuToEnergy, this.allSides(IC2Blocks.converterEuToEnergy, "converter_eu_to_energy"));
            dispatcher.addDispatch(IC2Blocks.converterEnergyToEu, this.allSides(IC2Blocks.converterEnergyToEu, "converter_energy_to_eu"));
        }
        LOGGER.info("IC2 block models done.");
    }

    private static String stoneSuffix(Block<?> block) {
        if (block == IC2Blocks.oreCopperBasalt || block == IC2Blocks.oreTinBasalt || block == IC2Blocks.oreUraniumBasalt) {
            return "basalt";
        }
        if (block == IC2Blocks.oreCopperLimestone || block == IC2Blocks.oreTinLimestone || block == IC2Blocks.oreUraniumLimestone) {
            return "limestone";
        }
        if (block == IC2Blocks.oreCopperGranite || block == IC2Blocks.oreTinGranite || block == IC2Blocks.oreUraniumGranite) {
            return "granite";
        }
        if (block == IC2Blocks.oreCopperPermafrost || block == IC2Blocks.oreTinPermafrost || block == IC2Blocks.oreUraniumPermafrost) {
            return "permafrost";
        }
        return "stone";
    }

    private static BlockModelStandard<?> tntLike(Block<?> block, String sideTex) {
        return new BlockModelStandard(block).setTex(IC2.MOD_ID + ":block/" + sideTex, new Side[]{Side.NORTH, Side.SOUTH, Side.EAST, Side.WEST}).setTex(IC2.MOD_ID + ":block/" + sideTex + "_top", new Side[]{Side.TOP}).setTex(IC2.MOD_ID + ":block/" + sideTex + "_bottom", new Side[]{Side.BOTTOM});
    }

    public void initBlockColors(BlockColorDispatcher dispatcher) {
        dispatcher.addDispatch(IC2Blocks.rubberLeaves, new BlockColorCustom(Colorizers.oak));
        LOGGER.info("IC2 block colors done.");
    }

    public void initItemModels(ItemModelDispatcher dispatcher) {
        LOGGER.info("Initializing IC2 item models...");
        int count = 0;
        try {
            for (Field f : IC2Items.class.getFields()) {
                Item[] arr;
                if (!Modifier.isStatic(f.getModifiers())) continue;
                Object v = f.get(null);
                if (v instanceof Item) {
                    Item item = (Item)v;
                    this.registerStandardItem(dispatcher, item);
                    ++count;
                    continue;
                }
                if (!(v instanceof Item[])) continue;
                for (Item item : arr = (Item[])v) {
                    if (item == null) continue;
                    this.registerStandardItem(dispatcher, item);
                    ++count;
                }
            }
        }
        catch (IllegalAccessException e) {
            LOGGER.error("Failed to register IC2 item models", (Throwable)e);
        }

        try {
            String[] chargeSuffixes = new String[]{"_full", "_75", "_50", "_25", "_empty"};
            dispatcher.addDispatch(new ItemModelBattery(IC2Items.batteryRE, IC2.MOD_ID + ":item/re_battery", chargeSuffixes));
            dispatcher.addDispatch(new ItemModelBattery(IC2Items.batteryCrystal, IC2.MOD_ID + ":item/energy_crystal", chargeSuffixes));
            dispatcher.addDispatch(new ItemModelBattery(IC2Items.batteryLamaCrystal, IC2.MOD_ID + ":item/lapotron_crystal", chargeSuffixes));
            dispatcher.addDispatch(new ItemModelBattery(IC2Items.pesd, IC2.MOD_ID + ":item/pesd", chargeSuffixes));
        }
        catch (Throwable e) {
            LOGGER.error("Failed to register battery charge models", (Throwable)e);
        }
        LOGGER.info("IC2 item models done: {} items.", (Object)count);
    }

    private void registerStandardItem(ItemModelDispatcher dispatcher, Item item) {
        if (dispatcher.hasDispatch(item)) {
            return;
        }
        ItemModelStandard model = new ItemModelStandard(item, false);
        model.setIcon(item.namespaceID.namespace() + ":item/" + item.namespaceID.value());
        dispatcher.addDispatch(model);
    }

    private BlockModelStandard<?> allSides(Block<?> block, String texName) {
        return new BlockModelStandard(block).setAllTextures(IC2.MOD_ID + ":block/" + texName);
    }

    public void initEntityRenderers(EntityRendererDispatcher dispatcher) {
        LOGGER.info("Initializing IC2 entity renderers...");
        dispatcher.assignRenderer(EntityIC2Explosive.class, (EntityRenderer)new EntityRendererIC2Explosive());
        dispatcher.assignRenderer(EntityDynamite.class, (EntityRenderer)new EntityRendererSprite(IC2Items.dynamiteItem).setScale(0.5f));
        dispatcher.assignRenderer(EntityMiningLaser.class, (EntityRenderer)new EntityRendererMiningLaser());
        LOGGER.info("IC2 entity renderers done.");
    }
}

