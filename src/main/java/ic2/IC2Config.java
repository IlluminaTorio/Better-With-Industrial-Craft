

package ic2;

import ic2.IC2;
import turniplabs.halplibe.util.TomlConfigHandler;
import turniplabs.halplibe.util.toml.Toml;

public class IC2Config {
    private static final int blockIdStart = 1300;
    private static final int itemIdStart = 18000;
    public static final TomlConfigHandler config;

    public static int block(String key) {
        return config.getInt("BlockIDs." + key);
    }

    public static int item(String key) {
        return config.getInt("ItemIDs." + key);
    }

    public static boolean machineExplosions() {
        return config.getBoolean("Gameplay.machineExplosions");
    }

    
    public static boolean voltageSystemOff() {
        return !machineExplosions();
    }

    public static boolean nuclearMeltdowns() {
        return config.getBoolean("Gameplay.nuclearMeltdowns");
    }

    
    public static boolean cableOverloadBurn() {
        return config.getBoolean("Gameplay.cableOverloadBurn");
    }

    static {
        Toml defaults = new Toml("IndustrialCraft 2 (BTA Edition) configuration.");
        defaults.addCategory("BlockIDs");
        defaults.addCategory("ItemIDs");
        defaults.addCategory("WorldGen");
        defaults.addCategory("These options modify world generation. 0 disables a feature.", "WorldGen");
        defaults.addCategory("Gameplay");
        defaults.addCategory("These options modify gameplay behaviour.", "Gameplay");
        String[] blockKeys = new String[]{"ore_copper", "ore_tin", "ore_uranium", "ore_copper_basalt", "ore_copper_limestone", "ore_copper_granite", "ore_copper_permafrost", "ore_tin_basalt", "ore_tin_limestone", "ore_tin_granite", "ore_tin_permafrost", "ore_uranium_basalt", "ore_uranium_limestone", "ore_uranium_granite", "ore_uranium_permafrost", "bronze_brick", "bronze_door", "bronze_door_top", "converter_eu_to_catalyst", "converter_catalyst_to_eu", "copper_block", "tin_block", "bronze_block", "uranium_block", "machine", "advanced_machine", "iron_furnace", "electric_furnace", "macerator", "extractor", "compressor", "canning_machine", "recycler", "electrolyzer", "induction_furnace", "mass_fabricator", "terraformer", "miner", "pump", "magnetizer", "generator", "geothermal_generator", "water_mill", "solar_panel", "wind_mill", "nuclear_reactor", "reactor_chamber", "batbox", "mfe", "mfsu", "transformer_lv", "transformer_mv", "transformer_hv", "cable", "luminator", "personal_safe", "trade_o_mat", "teleporter", "tesla_coil", "reinforced_stone", "reinforced_glass", "reinforced_door", "reinforced_door_top", "iron_fence", "rubber_sheet", "resin_block", "rubber_wood", "rubber_leaves", "rubber_sapling", "mining_pipe", "mining_tip", "industrial_tnt", "nuke", "dynamite", "dynamite_remote", "copper_brick", "converter_eu_to_energy", "converter_energy_to_eu"};
        int bid = 1300;
        for (String k : blockKeys) {
            defaults.addEntry("BlockIDs." + k, (Object)bid++);
        }
        String[] itemKeys = new String[]{"dust_coal", "dust_iron", "dust_gold", "dust_copper", "dust_tin", "dust_bronze", "dust_iron_small", "dust_steel", "raw_copper", "raw_tin", "bronze_door_item", "refined_iron", "ingot_copper", "ingot_tin", "ingot_bronze", "ingot_mixed_metal", "ingot_uranium", "uranium_item", "re_battery", "energy_crystal", "lapotron_crystal", "single_use_battery", "cell_empty", "cell_lava", "cell_coalfuel", "cell_biofuel", "cell_water", "cell_hydrated_coal", "cell_biomass", "cell_uranium", "cell_coolant", "cell_depleted_isotope", "cell_reenriched_uranium", "cell_near_depleted_uranium", "cell_electrolyzed_water", "mining_drill", "diamond_drill", "chainsaw", "fuel_can_empty", "fuel_can_filled", "hydrated_coal_dust", "hydrated_coal", "plantball", "compressed_plants", "tin_can", "filled_tin_can", "od_scanner", "ov_scanner", "dynamite_o_mote", "dynamite_item", "sticky_dynamite", "sticky_resin", "rubber", "treetap", "rubber_boots", "jetpack", "electric_jetpack", "mining_laser", "reactor_plating", "heat_disperser", "batpack", "raw_carbon_fibre", "raw_carbon_mesh", "carbon_plate", "nano_saber", "nano_saber_off", "bronze_pickaxe", "bronze_axe", "bronze_sword", "bronze_shovel", "bronze_hoe", "bronze_helmet", "bronze_chestplate", "bronze_leggings", "bronze_boots", "wrench", "electric_wrench", "eu_reader", "painter", "insulation_cutter", "iridium_plate", "frequency_transmitter", "circuit", "circuit_advanced", "scrap", "uu_matter", "advanced_alloy", "industrial_credit", "reinforced_door_item", "composite_vest", "nanosuit_helmet", "nanosuit_bodyarmor", "nanosuit_leggings", "nanosuit_boots", "quantum_helmet", "quantum_bodyarmor", "quantum_leggings", "quantum_boots", "tfbp_empty", "tfbp_cultivation", "tfbp_irrigation", "tfbp_chilling", "tfbp_desertification", "tfbp_flatification", "industrial_diamond", "coal_chunk", "compressed_coal_ball", "coal_ball", "scrap_box", "cable_copper", "cable_copper_uninsulated", "cable_gold", "cable_gold_insulated", "cable_gold_insulated_2x", "cable_hv", "cable_hv_insulated", "cable_hv_insulated_2x", "cable_hv_insulated_4x", "cable_glass_fibre", "cable_tin", "painter_black", "painter_red", "painter_green", "painter_brown", "painter_blue", "painter_purple", "painter_cyan", "painter_light_gray", "painter_gray", "painter_pink", "painter_lime", "painter_yellow", "painter_light_blue", "painter_magenta", "painter_orange", "painter_white", "armor_wolf_bronze", "dust_gold_small", "dust_copper_small", "dust_tin_small", "dust_bronze_small", "dust_steel_small"};
        int iid = 18000;
        for (String k : itemKeys) {
            defaults.addEntry("ItemIDs." + k, (Object)iid++);
        }
        defaults.addEntry("WorldGen.copperEnabled", "Default is true.", (Object)true);
        defaults.addEntry("WorldGen.tinEnabled", "Default is true.", (Object)true);
        defaults.addEntry("WorldGen.uraniumEnabled", "Default is true.", (Object)true);
        defaults.addEntry("WorldGen.rubberTreesEnabled", "Default is true.", (Object)true);
        defaults.addEntry("Gameplay.machineExplosions", "Machines explode when receiving excess voltage. Set to false to disable: the machine will safely break and drop instead. Default is true.", (Object)true);
        defaults.addEntry("Gameplay.nuclearMeltdowns", "Nuclear reactors explode on overheat. Set to false to disable: the reactor will safely break and drop instead. Default is true.", (Object)true);
        defaults.addEntry("Gameplay.cableOverloadBurn", "Cables burn away when overloaded by too much voltage (original IC2 behaviour). Set to false to disable: excess energy simply fails to pass through the cable, but the cable never disappears. Default is false.", (Object)false);
        config = new TomlConfigHandler(IC2.MOD_ID, defaults);
    }
}

