

package ic2;

import ic2.IC2;
import ic2.IC2Config;
import ic2.IC2Items;
import ic2.block.BlockLogicCable;
import ic2.block.BlockLogicDynamite;
import ic2.block.BlockLogicIC2Explosive;
import ic2.block.BlockLogicIC2Machine;
import ic2.block.BlockLogicIC2Ore;
import ic2.block.BlockLogicIridiumStone;
import ic2.block.BlockLogicIronFence;
import ic2.block.BlockLogicLuminator;
import ic2.block.BlockLogicRubLeaves;
import ic2.block.BlockLogicRubSapling;
import ic2.block.BlockLogicRubWood;
import ic2.block.BlockLogicTeleporter;
import ic2.entity.EntityIC2Explosive;
import ic2.si.SIConverters;
import ic2.tileentity.TileEntityCable;
import ic2.tileentity.TileEntityCanner;
import ic2.tileentity.TileEntityCompressor;
import ic2.tileentity.TileEntityElecFurnace;
import ic2.tileentity.TileEntityElectricBatBox;
import ic2.tileentity.TileEntityElectricMFE;
import ic2.tileentity.TileEntityElectricMFSU;
import ic2.tileentity.TileEntityElectrolyzer;
import ic2.tileentity.TileEntityExtractor;
import ic2.tileentity.TileEntityGenerator;
import ic2.tileentity.TileEntityGeoGenerator;
import ic2.tileentity.TileEntityIC2Machine;
import ic2.tileentity.TileEntityInduction;
import ic2.tileentity.TileEntityIronFurnace;
import ic2.tileentity.TileEntityLuminator;
import ic2.tileentity.TileEntityMacerator;
import ic2.tileentity.TileEntityMagnetizer;
import ic2.tileentity.TileEntityMatter;
import ic2.tileentity.TileEntityMiner;
import ic2.tileentity.TileEntityNuclearReactor;
import ic2.tileentity.TileEntityPersonalChest;
import ic2.tileentity.TileEntityPump;
import ic2.tileentity.TileEntityReactorChamber;
import ic2.tileentity.TileEntityRecycler;
import ic2.tileentity.TileEntitySolarGenerator;
import ic2.tileentity.TileEntityTeleporter;
import ic2.tileentity.TileEntityTerraformer;
import ic2.tileentity.TileEntityTesla;
import ic2.tileentity.TileEntityTradeOMat;
import ic2.tileentity.TileEntityTransformerHV;
import ic2.tileentity.TileEntityTransformerLV;
import ic2.tileentity.TileEntityTransformerMV;
import ic2.tileentity.TileEntityWaterGenerator;
import ic2.tileentity.TileEntityWindGenerator;
import ic2.tileentity.TileEntitySlagGenerator;
import ic2.tileentity.TileEntityThermalGenerator;
import ic2.tileentity.TileEntityTurbineSolar;
import ic2.tileentity.TileEntityOceanCurrentGenerator;
import ic2.tileentity.TileEntityWaveGenerator;
import ic2.tileentity.TileEntityWoodGasser;
import ic2.tileentity.TileEntityWoodGasserElec;
import ic2.tileentity.TileEntitySlowGrinder;
import ic2.tileentity.TileEntityRareEarthExtractor;
import ic2.tileentity.TileEntityPlasmafier;
import ic2.tileentity.TileEntityPESU;
import ic2.tileentity.TileEntityTransformerIV;
import ic2.tileentity.TileEntityIridiumStone;
import java.util.function.Supplier;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicDoor;
import net.minecraft.core.block.BlockLogicGlass;
import net.minecraft.core.block.BlockLogicMesh;
import net.minecraft.core.block.BlockLogicTransparent;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.data.tag.Tag;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.sound.BlockSounds;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import turniplabs.halplibe.helper.BlockBuilder;

public class IC2Blocks {
    public static Block<?> oreCopper;
    public static Block<?> oreCopperBasalt;
    public static Block<?> oreCopperLimestone;
    public static Block<?> oreCopperGranite;
    public static Block<?> oreCopperPermafrost;
    public static Block<?> oreTin;
    public static Block<?> oreTinBasalt;
    public static Block<?> oreTinLimestone;
    public static Block<?> oreTinGranite;
    public static Block<?> oreTinPermafrost;
    public static Block<?> oreUranium;
    public static Block<?> oreUraniumBasalt;
    public static Block<?> oreUraniumLimestone;
    public static Block<?> oreUraniumGranite;
    public static Block<?> oreUraniumPermafrost;
    public static Block<?> bronzeBrick;
    public static Block<?> copperBrick;
    public static Block<BlockLogicDoor> bronzeDoorBottom;
    public static Block<BlockLogicDoor> bronzeDoorTop;
    public static Block<?> converterEuToCatalyst;
    public static Block<?> converterCatalystToEu;
    public static Block<?> converterEuToEnergy;
    public static Block<?> converterEnergyToEu;
    public static Block<?> copperBlock;
    public static Block<?> tinBlock;
    public static Block<?> bronzeBlock;
    public static Block<?> uraniumBlock;
    public static Block<?> machineBlock;
    public static Block<?> advancedMachineBlock;
    public static Block<?> ironFurnace;
    public static Block<?> electricFurnace;
    public static Block<?> macerator;
    public static Block<?> extractor;
    public static Block<?> compressor;
    public static Block<?> canner;
    public static Block<?> recycler;
    public static Block<?> electrolyzer;
    public static Block<?> inductionFurnace;
    public static Block<?> massFabricator;
    public static Block<?> terraformer;
    public static Block<?> miner;
    public static Block<?> pump;
    public static Block<?> magnetizer;
    public static Block<?> generator;
    public static Block<?> geothermalGenerator;
    public static Block<?> waterMill;
    public static Block<?> solarPanel;
    public static Block<?> windMill;
    public static Block<?> nuclearReactor;
    public static Block<?> reactorChamber;
    public static Block<?> batBox;
    public static Block<?> mfe;
    public static Block<?> mfsu;
    public static Block<?> transformerLV;
    public static Block<?> transformerMV;
    public static Block<?> transformerHV;
    public static Block<BlockLogicCable> cable;
    public static Block<?> luminator;
    public static Block<?> personalSafe;
    public static Block<?> tradeOMat;
    public static Block<?> teleporter;
    public static Block<?> teslaCoil;
    public static Block<?> reinforcedStone;
    public static Block<?> reinforcedGlass;
    public static Block<BlockLogicDoor> reinforcedDoorBottom;
    public static Block<BlockLogicDoor> reinforcedDoorTop;
    public static Block<?> ironFence;
    public static Block<?> rubberSheet;
    public static Block<?> resinBlock;
    public static Block<?> rubberWood;
    public static Block<?> rubberLeaves;
    public static Block<?> rubberSapling;
    public static Block<?> miningPipe;
    public static Block<?> miningTip;
    public static Block<?> industrialTnt;
    public static Block<?> nuke;
    public static Block<?> dynamite;
    public static Block<?> dynamiteRemote;
    public static Block<?> slagGenerator;
    public static Block<?> thermalGenerator;
    public static Block<?> turbineSolar;
    public static Block<?> oceanGenerator;
    public static Block<?> waveGenerator;
    public static Block<?> woodGasser;
    public static Block<?> woodGasserElec;
    public static Block<?> slowGrinder;
    public static Block<?> rareEarthExtractor;
    public static Block<?> plasmafier;
    public static Block<?> pesu;
    public static Block<?> transformerIV;
    public static Block<?> iridiumStone;
    public static Block<?> meshSteel;
    public static Block<?> meshSteelCrude;
    public static Block<?> quartzGlass;

    public static void init() {
        String modId = IC2.MOD_ID;
        oreCopper = IC2Blocks.ore("ore.copper", "ore_copper", "ore_copper", IC2Config.block("ore_copper"), Blocks.STONE, 3.0f);
        oreCopperBasalt = IC2Blocks.ore("ore.copper.basalt", "ore_copper_basalt", "ore_copper_basalt", IC2Config.block("ore_copper_basalt"), Blocks.BASALT, 3.0f);
        oreCopperLimestone = IC2Blocks.ore("ore.copper.limestone", "ore_copper_limestone", "ore_copper_limestone", IC2Config.block("ore_copper_limestone"), Blocks.LIMESTONE, 3.0f);
        oreCopperGranite = IC2Blocks.ore("ore.copper.granite", "ore_copper_granite", "ore_copper_granite", IC2Config.block("ore_copper_granite"), Blocks.GRANITE, 3.0f);
        oreCopperPermafrost = IC2Blocks.ore("ore.copper.permafrost", "ore_copper_permafrost", "ore_copper_permafrost", IC2Config.block("ore_copper_permafrost"), Blocks.PERMAFROST, 3.0f);
        oreTin = IC2Blocks.ore("ore.tin", "ore_tin", "ore_tin", IC2Config.block("ore_tin"), Blocks.STONE, 3.0f);
        oreTinBasalt = IC2Blocks.ore("ore.tin.basalt", "ore_tin_basalt", "ore_tin_basalt", IC2Config.block("ore_tin_basalt"), Blocks.BASALT, 3.0f);
        oreTinLimestone = IC2Blocks.ore("ore.tin.limestone", "ore_tin_limestone", "ore_tin_limestone", IC2Config.block("ore_tin_limestone"), Blocks.LIMESTONE, 3.0f);
        oreTinGranite = IC2Blocks.ore("ore.tin.granite", "ore_tin_granite", "ore_tin_granite", IC2Config.block("ore_tin_granite"), Blocks.GRANITE, 3.0f);
        oreTinPermafrost = IC2Blocks.ore("ore.tin.permafrost", "ore_tin_permafrost", "ore_tin_permafrost", IC2Config.block("ore_tin_permafrost"), Blocks.PERMAFROST, 3.0f);
        oreUranium = IC2Blocks.ore("ore.uranium", "ore_uranium", "ore_uranium", IC2Config.block("ore_uranium"), Blocks.STONE, 10.0f);
        oreUraniumBasalt = IC2Blocks.ore("ore.uranium.basalt", "ore_uranium_basalt", "ore_uranium_basalt", IC2Config.block("ore_uranium_basalt"), Blocks.BASALT, 10.0f);
        oreUraniumLimestone = IC2Blocks.ore("ore.uranium.limestone", "ore_uranium_limestone", "ore_uranium_limestone", IC2Config.block("ore_uranium_limestone"), Blocks.LIMESTONE, 10.0f);
        oreUraniumGranite = IC2Blocks.ore("ore.uranium.granite", "ore_uranium_granite", "ore_uranium_granite", IC2Config.block("ore_uranium_granite"), Blocks.GRANITE, 10.0f);
        oreUraniumPermafrost = IC2Blocks.ore("ore.uranium.permafrost", "ore_uranium_permafrost", "ore_uranium_permafrost", IC2Config.block("ore_uranium_permafrost"), Blocks.PERMAFROST, 10.0f);
        bronzeBrick = new BlockBuilder(modId).setHardness(3.0f).setResistance(15.0f).setBlockSound(BlockSounds.METAL).addTags(new Tag[]{BlockTags.MINEABLE_BY_PICKAXE}).build("brick.bronze", "bronze_brick", IC2Config.block("bronze_brick"), b -> new BlockLogic(b, Materials.METAL));
        copperBrick = new BlockBuilder(modId).setHardness(3.0f).setResistance(15.0f).setBlockSound(BlockSounds.METAL).addTags(new Tag[]{BlockTags.MINEABLE_BY_PICKAXE}).build("brick.copper", "copper_brick", IC2Config.block("copper_brick"), b -> new BlockLogic(b, Materials.METAL));
        bronzeDoorBottom = new BlockBuilder(modId).setHardness(3.0f).setResistance(15.0f).setBlockSound(BlockSounds.METAL).addTags(new Tag[]{BlockTags.MINEABLE_BY_PICKAXE, BlockTags.NOT_IN_CREATIVE_MENU}).build("door.bronze.bottom", "bronze_door_bottom", IC2Config.block("bronze_door"), b -> new BlockLogicDoor(b, Materials.METAL, false, true, () -> IC2Items.bronzeDoorItem));
        bronzeDoorTop = new BlockBuilder(modId).setHardness(3.0f).setResistance(15.0f).setBlockSound(BlockSounds.METAL).addTags(new Tag[]{BlockTags.MINEABLE_BY_PICKAXE, BlockTags.NOT_IN_CREATIVE_MENU}).setStatParent(() -> bronzeDoorBottom).build("door.bronze.top", "bronze_door_top", IC2Config.block("bronze_door_top"), b -> new BlockLogicDoor(b, Materials.METAL, true, true, () -> IC2Items.bronzeDoorItem));
        copperBlock = IC2Blocks.metalBlock("copper_block", "storage.copper", "copper_block", 2.0f);
        tinBlock = IC2Blocks.metalBlock("tin_block", "storage.tin", "tin_block", 2.0f);
        bronzeBlock = IC2Blocks.metalBlock("bronze_block", "storage.bronze", "bronze_block", 2.0f);
        uraniumBlock = IC2Blocks.metalBlock("uranium_block", "storage.uranium", "uranium_block", 10.0f);
        machineBlock = IC2Blocks.metalBlock("machine", "machine", "machine_casing", 2.0f);
        advancedMachineBlock = IC2Blocks.metalBlock("advanced_machine", "machine.advanced", "adv_machine_block", 4.0f);
        ironFurnace = IC2Blocks.machine("iron_furnace", "machine.iron_furnace", IC2Config.block("iron_furnace"), 1, TileEntityIronFurnace::new, 3.5f);
        electricFurnace = IC2Blocks.machine("electric_furnace", "machine.electric_furnace", IC2Config.block("electric_furnace"), 0, TileEntityElecFurnace::new, 2.0f);
        macerator = IC2Blocks.machine("macerator", "machine.macerator", IC2Config.block("macerator"), 0, TileEntityMacerator::new, 2.0f);
        extractor = IC2Blocks.machine("extractor", "machine.extractor", IC2Config.block("extractor"), 0, TileEntityExtractor::new, 2.0f);
        compressor = IC2Blocks.machine("compressor", "machine.compressor", IC2Config.block("compressor"), 0, TileEntityCompressor::new, 2.0f);
        canner = IC2Blocks.machine("canning_machine", "machine.canner", IC2Config.block("canning_machine"), 4, TileEntityCanner::new, 2.0f);
        recycler = IC2Blocks.machine("recycler", "machine.recycler", IC2Config.block("recycler"), 0, TileEntityRecycler::new, 2.0f);
        electrolyzer = IC2Blocks.machine("electrolyzer", "machine.electrolyzer", IC2Config.block("electrolyzer"), 5, TileEntityElectrolyzer::new, 2.0f);
        inductionFurnace = IC2Blocks.machine("induction_furnace", "machine.induction_furnace", IC2Config.block("induction_furnace"), 6, TileEntityInduction::new, 2.0f);
        massFabricator = IC2Blocks.machine("mass_fabricator", "machine.mass_fabricator", IC2Config.block("mass_fabricator"), 7, TileEntityMatter::new, 2.0f);
        terraformer = IC2Blocks.machine("terraformer", "machine.terraformer", IC2Config.block("terraformer"), 13, TileEntityTerraformer::new, 2.0f);
        miner = IC2Blocks.machine("miner", "machine.miner", IC2Config.block("miner"), 9, TileEntityMiner::new, 2.0f);
        pump = IC2Blocks.machine("pump", "machine.pump", IC2Config.block("pump"), 10, TileEntityPump::new, 2.0f);
        magnetizer = IC2Blocks.machine("magnetizer", "machine.magnetizer", IC2Config.block("magnetizer"), -1, TileEntityMagnetizer::new, 2.0f);
        generator = IC2Blocks.machine("generator", "generator.generator", IC2Config.block("generator"), 2, TileEntityGenerator::new, 1.5f);
        geothermalGenerator = IC2Blocks.machine("geothermal_generator", "generator.geothermal", IC2Config.block("geothermal_generator"), 2, TileEntityGeoGenerator::new, 1.5f);
        waterMill = IC2Blocks.machine("water_mill", "generator.water_mill", IC2Config.block("water_mill"), 2, TileEntityWaterGenerator::new, 1.5f);
        solarPanel = IC2Blocks.machine("solar_panel", "generator.solar_panel", IC2Config.block("solar_panel"), 2, TileEntitySolarGenerator::new, 1.5f);
        windMill = IC2Blocks.machine("wind_mill", "generator.wind_mill", IC2Config.block("wind_mill"), 2, TileEntityWindGenerator::new, 1.5f);
        nuclearReactor = new BlockBuilder(modId).setHardness(3.0f).setResistance(10.0f).setBlockSound(BlockSounds.METAL).addTags(new Tag[]{BlockTags.MINEABLE_BY_PICKAXE}).setTileEntity(TileEntityNuclearReactor::new).build("reactor.nuclear", "nuclear_reactor", IC2Config.block("nuclear_reactor"), b -> new BlockLogicIC2Machine(b, 8));
        reactorChamber = new BlockBuilder(modId).setHardness(3.0f).setResistance(10.0f).setBlockSound(BlockSounds.METAL).addTags(new Tag[]{BlockTags.MINEABLE_BY_PICKAXE}).setTileEntity(TileEntityReactorChamber::new).build("reactor.chamber", "reactor_chamber", IC2Config.block("reactor_chamber"), b -> new BlockLogicIC2Machine(b, 8));
        batBox = IC2Blocks.machine("batbox", "energy.batbox", IC2Config.block("batbox"), 3, TileEntityElectricBatBox::new, 1.5f);
        mfe = IC2Blocks.machine("mfe", "energy.mfe", IC2Config.block("mfe"), 3, TileEntityElectricMFE::new, 2.0f);
        mfsu = IC2Blocks.machine("mfsu", "energy.mfsu", IC2Config.block("mfsu"), 3, TileEntityElectricMFSU::new, 2.0f);
        transformerLV = IC2Blocks.machine("transformer_lv", "energy.transformer_lv", IC2Config.block("transformer_lv"), 3, TileEntityTransformerLV::new, 1.5f);
        transformerMV = IC2Blocks.machine("transformer_mv", "energy.transformer_mv", IC2Config.block("transformer_mv"), 3, TileEntityTransformerMV::new, 1.5f);
        transformerHV = IC2Blocks.machine("transformer_hv", "energy.transformer_hv", IC2Config.block("transformer_hv"), 3, TileEntityTransformerHV::new, 1.5f);
        cable = new BlockBuilder(modId).setHardness(0.2f).setResistance(0.2f).setBlockSound(BlockSounds.CLOTH).setTileEntity(TileEntityCable::new).build("cable", "cable", IC2Config.block("cable"), BlockLogicCable::new);
        luminator = new BlockBuilder(modId).setHardness(0.3f).setResistance(0.5f).setLuminance(15).setLightOpacity(0).setBlockSound(BlockSounds.GLASS).setTileEntity(TileEntityLuminator::new).build("luminator", "luminator", IC2Config.block("luminator"), b -> new BlockLogicLuminator(b));
        personalSafe = IC2Blocks.machine("personal_safe", "personal.safe", IC2Config.block("personal_safe"), 11, TileEntityPersonalChest::new, 2.5f);
        tradeOMat = IC2Blocks.machine("trade_o_mat", "personal.trade_o_mat", IC2Config.block("trade_o_mat"), 12, TileEntityTradeOMat::new, 2.0f);
        teleporter = new BlockBuilder(IC2.MOD_ID).setHardness(2.0f).setResistance(10.0f).setBlockSound(BlockSounds.METAL).addTags(new Tag[]{BlockTags.MINEABLE_BY_PICKAXE}).setTileEntity(TileEntityTeleporter::new).build("machine.teleporter", "teleporter", IC2Config.block("teleporter"), b -> new BlockLogicTeleporter(b));
        teslaCoil = IC2Blocks.machine("tesla_coil", "machine.tesla_coil", IC2Config.block("tesla_coil"), -1, TileEntityTesla::new, 2.0f);
        reinforcedStone = new BlockBuilder(modId).setHardness(4.0f).setResistance(100.0f).setBlockSound(BlockSounds.STONE).addTags(new Tag[]{BlockTags.MINEABLE_BY_PICKAXE}).build("reinforced.stone", "reinforced_stone", IC2Config.block("reinforced_stone"), b -> new BlockLogic(b, Materials.STONE));
        reinforcedGlass = new BlockBuilder(modId).setHardness(1.0f).setResistance(100.0f).setBlockSound(BlockSounds.GLASS).addTags(new Tag[]{BlockTags.MINEABLE_BY_PICKAXE}).build("reinforced.glass", "reinforced_glass", IC2Config.block("reinforced_glass"), b -> new BlockLogicGlass(b, Materials.GLASS));
        reinforcedDoorBottom = new BlockBuilder(modId).setHardness(10.0f).setResistance(100.0f).setBlockSound(BlockSounds.METAL).addTags(new Tag[]{BlockTags.MINEABLE_BY_PICKAXE, BlockTags.NOT_IN_CREATIVE_MENU}).build("door.reinforced.bottom", "reinforced_door_bottom", IC2Config.block("reinforced_door"), b -> new BlockLogicDoor(b, Materials.METAL, false, true, () -> IC2Items.reinforcedDoorItem));
        reinforcedDoorTop = new BlockBuilder(modId).setHardness(10.0f).setResistance(100.0f).setBlockSound(BlockSounds.METAL).addTags(new Tag[]{BlockTags.MINEABLE_BY_PICKAXE, BlockTags.NOT_IN_CREATIVE_MENU}).setStatParent(() -> reinforcedDoorBottom).build("door.reinforced.top", "reinforced_door_top", IC2Config.block("reinforced_door_top"), b -> new BlockLogicDoor(b, Materials.METAL, true, true, () -> IC2Items.reinforcedDoorItem));
        ironFence = new BlockBuilder(modId).setHardness(1.5f).setResistance(5.0f).setBlockSound(BlockSounds.METAL).addTags(new Tag[]{BlockTags.MINEABLE_BY_PICKAXE}).build("fence.iron", "iron_fence", IC2Config.block("iron_fence"), b -> new BlockLogicIronFence(b));
        rubberSheet = new BlockBuilder(modId).setHardness(0.8f).setResistance(2.0f).setBlockSound(BlockSounds.CLOTH).setSlipperiness(0.98f).build("rubber.sheet", "rubber_sheet", IC2Config.block("rubber_sheet"), b -> new BlockLogicTransparent(b, Materials.CLOTH));
        resinBlock = new BlockBuilder(modId).setHardness(1.6f).setResistance(0.5f).setBlockSound(BlockSounds.GRASS).build("resin", "resin_block", IC2Config.block("resin_block"), b -> new BlockLogic(b, Materials.CLOTH));
        rubberSapling = new BlockBuilder(modId).setHardness(0.0f).setBlockSound(BlockSounds.GRASS).build("rubber.sapling", "rubber_sapling", IC2Config.block("rubber_sapling"), BlockLogicRubSapling::new);
        rubberLeaves = new BlockBuilder(modId).setHardness(0.2f).setResistance(0.2f).setLightOpacity(1).setBlockSound(BlockSounds.GRASS).build("rubber.leaves", "rubber_leaves", IC2Config.block("rubber_leaves"), b -> new BlockLogicRubLeaves(b, rubberSapling));
        rubberWood = new BlockBuilder(modId).setHardness(1.0f).setResistance(1.0f).setBlockSound(BlockSounds.WOOD).addTags(new Tag[]{BlockTags.MINEABLE_BY_AXE, BlockTags.FENCES_CONNECT}).build("rubber.wood", "rubber_wood", IC2Config.block("rubber_wood"), BlockLogicRubWood::new);
        miningPipe = new BlockBuilder(modId).setHardness(6.0f).setResistance(10.0f).setBlockSound(BlockSounds.METAL).addTags(new Tag[]{BlockTags.MINEABLE_BY_PICKAXE, BlockTags.NOT_IN_CREATIVE_MENU}).build("mining.pipe", "mining_pipe", IC2Config.block("mining_pipe"), b -> new BlockLogicTransparent(b, Materials.METAL));
        miningTip = new BlockBuilder(modId).setHardness(6.0f).setResistance(10.0f).setBlockSound(BlockSounds.METAL).addTags(new Tag[]{BlockTags.MINEABLE_BY_PICKAXE, BlockTags.NOT_IN_CREATIVE_MENU}).build("mining.tip", "mining_tip", IC2Config.block("mining_tip"), b -> new BlockLogicTransparent(b, Materials.METAL));
        industrialTnt = new BlockBuilder(modId).setHardness(0.0f).setBlockSound(BlockSounds.GRASS).build("tnt.industrial", "industrial_tnt", IC2Config.block("industrial_tnt"), b -> new BlockLogicIC2Explosive(b, true){

            @Override
            @NotNull
            public EntityIC2Explosive getExplosionEntity(@NotNull World world, float x, float y, float z) {
                return new EntityIC2Explosive(world, x, y, z, 60, 3.0f, 0.9f, 0.3f, industrialTnt);
            }
        });
        nuke = new BlockBuilder(modId).setHardness(0.0f).setBlockSound(BlockSounds.GRASS).build("tnt.nuke", "nuke", IC2Config.block("nuke"), b -> new BlockLogicIC2Explosive(b, false){

            @Override
            @NotNull
            public EntityIC2Explosive getExplosionEntity(@NotNull World world, float x, float y, float z) {
                return new EntityIC2Explosive(world, x, y, z, 300, 40.0f, 0.05f, 1.5f, nuke);
            }
        });
        dynamite = new BlockBuilder(modId).setHardness(0.0f).setBlockSound(BlockSounds.GRASS).build("tnt.dynamite", "dynamite", IC2Config.block("dynamite"), b -> new BlockLogicDynamite(b));
        dynamiteRemote = new BlockBuilder(modId).setHardness(0.0f).setBlockSound(BlockSounds.GRASS).build("tnt.dynamite_remote", "dynamite_remote", IC2Config.block("dynamite_remote"), b -> new BlockLogicDynamite(b));
        SIConverters.registerBlocks(IC2Config.block("converter_eu_to_catalyst"), IC2Config.block("converter_catalyst_to_eu"));
        slagGenerator = IC2Blocks.machine("slag_generator", "generator.slag", IC2Config.block("slag_generator"), 14, TileEntitySlagGenerator::new, 2.0f);
        thermalGenerator = IC2Blocks.machine("thermal_generator", "generator.thermal", IC2Config.block("thermal_generator"), 15, TileEntityThermalGenerator::new, 2.0f);
        turbineSolar = IC2Blocks.machine("turbine_solar", "generator.turbine_solar", IC2Config.block("turbine_solar"), 16, TileEntityTurbineSolar::new, 2.0f);
        oceanGenerator = IC2Blocks.machine("ocean_generator", "generator.ocean", IC2Config.block("ocean_generator"), -1, TileEntityOceanCurrentGenerator::new, 2.0f);
        waveGenerator = IC2Blocks.machine("wave_generator", "generator.wave", IC2Config.block("wave_generator"), -1, TileEntityWaveGenerator::new, 2.0f);
        woodGasser = IC2Blocks.machine("wood_gasser", "machine.wood_gasser", IC2Config.block("wood_gasser"), 18, TileEntityWoodGasser::new, 2.0f);
        woodGasserElec = IC2Blocks.machine("wood_gasser_elec", "machine.wood_gasser_elec", IC2Config.block("wood_gasser_elec"), 19, TileEntityWoodGasserElec::new, 2.0f);
        slowGrinder = IC2Blocks.machine("slow_grinder", "machine.slow_grinder", IC2Config.block("slow_grinder"), 17, TileEntitySlowGrinder::new, 2.0f);
        rareEarthExtractor = IC2Blocks.machine("rare_earth_extractor", "machine.rare_earth_extractor", IC2Config.block("rare_earth_extractor"), 21, TileEntityRareEarthExtractor::new, 2.0f);
        plasmafier = IC2Blocks.machine("plasmafier", "machine.plasmafier", IC2Config.block("plasmafier"), 22, TileEntityPlasmafier::new, 2.0f);
        pesu = IC2Blocks.machine("pesu", "energy.pesu", IC2Config.block("pesu"), 3, TileEntityPESU::new, 2.0f);
        transformerIV = IC2Blocks.machine("transformer_iv", "energy.transformer_iv", IC2Config.block("transformer_iv"), 3, TileEntityTransformerIV::new, 1.5f);
        iridiumStone = new BlockBuilder(modId).setHardness(80.0f).setResistance(150.0f).setBlockSound(BlockSounds.METAL).addTags(new Tag[]{BlockTags.MINEABLE_BY_PICKAXE}).setTileEntity(TileEntityIridiumStone::new).build("machine.iridium_stone", "iridium_stone", IC2Config.block("iridium_stone"), BlockLogicIridiumStone::new);
        meshSteel = new BlockBuilder(modId).setHardness(5.0f).setResistance(2000.0f).setBlockSound(BlockSounds.STONE).addTags(new Tag[]{BlockTags.MINEABLE_BY_PICKAXE}).build("mesh.steel", "mesh_steel", IC2Config.block("mesh_steel"), BlockLogicMesh::new);
        meshSteelCrude = new BlockBuilder(modId).setHardness(5.0f).setResistance(2000.0f).setBlockSound(BlockSounds.STONE).addTags(new Tag[]{BlockTags.MINEABLE_BY_PICKAXE}).build("mesh.steel.crude", "mesh_steel_crude", IC2Config.block("mesh_steel_crude"), BlockLogicMesh::new);
        quartzGlass = new BlockBuilder(modId).setHardness(0.3f).setResistance(0.3f).setUseInternalLight().setBlockSound(BlockSounds.GLASS).addTags(new Tag[]{BlockTags.MINEABLE_BY_PICKAXE}).build("glass.quartz", "glass_quartz", IC2Config.block("glass_quartz"), b -> new BlockLogicGlass(b, Materials.GLASS));
    }

    private static Block<?> metalBlock(String name, String translationKey, String texName, float hardness) {
        return new BlockBuilder(IC2.MOD_ID).setHardness(hardness).setResistance(hardness * 5.0f).setBlockSound(BlockSounds.METAL).addTags(new Tag[]{BlockTags.MINEABLE_BY_PICKAXE}).build(translationKey, name, IC2Config.block(name), b -> new BlockLogic(b, Materials.METAL));
    }

    private static Block<?> ore(String translationKey, String name, String texName, int id, Block<?> parentStone, float hardness) {
        Supplier<ItemStack> drop = switch (name) {
            case "ore_copper", "ore_copper_basalt", "ore_copper_limestone", "ore_copper_granite", "ore_copper_permafrost" -> () -> new ItemStack(IC2Items.rawCopper);
            case "ore_tin", "ore_tin_basalt", "ore_tin_limestone", "ore_tin_granite", "ore_tin_permafrost" -> () -> new ItemStack(IC2Items.rawTin);
            default -> () -> new ItemStack(IC2Items.uraniumItem);
        };
        boolean uranium = name.startsWith("ore_uranium");
        BlockBuilder builder = new BlockBuilder(IC2.MOD_ID).setHardness(hardness).setResistance(hardness * 5.0f).setBlockSound(BlockSounds.STONE).addTags(new Tag[]{BlockTags.MINEABLE_BY_PICKAXE});
        if (uranium) {
            builder.setLuminance(3);
        }
        return builder.build(translationKey, name, id, b -> new BlockLogicIC2Ore(b, parentStone, drop));
    }

    private static Block<?> machine(String name, String translationKey, int id, int guiId, Supplier<? extends TileEntity> te, float hardness) {
        return new BlockBuilder(IC2.MOD_ID).setHardness(hardness).setResistance(hardness * 5.0f).setBlockSound(BlockSounds.METAL).addTags(new Tag[]{BlockTags.MINEABLE_BY_PICKAXE}).setTileEntity(() -> (TileEntity)te.get()).build(translationKey, name, id, b -> new BlockLogicIC2Machine(b, guiId));
    }

    public static void explodeMachineAt(World world, int x, int y, int z) {
        IC2Blocks.explodeMachineAt(world, x, y, z, 1.0f);
    }

    public static void explodeMachineAt(World world, int x, int y, int z, float power) {
        boolean meltdown;
        boolean bl = meltdown = power > 1.0f;
        if (meltdown ? !IC2Config.nuclearMeltdowns() : !IC2Config.machineExplosions()) {
            IC2Blocks.safeRemoveMachine(world, x, y, z);
            return;
        }
        world.setBlockWithNotify(x, y, z, 0);
        world.createExplosion(null, (double)x + 0.5, (double)y + 0.5, (double)z + 0.5, power * 4.0f);
    }

    public static void safeRemoveMachine(World world, int x, int y, int z) {
        ItemStack stack;
        Block block = world.getBlock(x, y, z);
        TileEntity te = world.getTileEntity((TilePosc)new TilePos(x, y, z));
        if (te instanceof TileEntityIC2Machine) {
            TileEntityIC2Machine machine = (TileEntityIC2Machine)te;
            machine.dropContents(world, x, y, z);
        }
        world.setBlockWithNotify(x, y, z, 0);
        if (block != null && (stack = block.getDefaultStack()) != null && !world.isClientSide) {
            world.entityJoinedWorld((Entity)new EntityItem(world, (double)x + 0.5, (double)y + 0.5, (double)z + 0.5, stack));
        }
    }
}

