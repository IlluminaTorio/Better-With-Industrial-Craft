

package ic2.si;

import ic2.IC2;
import ic2.IC2Config;
import net.fabricmc.loader.api.FabricLoader;

public final class SIConverters {
    private SIConverters() {
    }

    public static boolean isCatalystInstalled() {
        return FabricLoader.getInstance().isModLoaded("signalindustries");
    }


    public static void registerRecipes() {
        if (!SIConverters.isCatalystInstalled()) {
            return;
        }
        try {
            Class<?> items = Class.forName("sunsetsatellite.signalindustries.SIItems");
            net.minecraft.core.item.Item crystalEmpty = (net.minecraft.core.item.Item)items.getField("signalumCrystalEmpty").get(null);
            net.minecraft.core.item.Item crystalAlloyPlate = (net.minecraft.core.item.Item)items.getField("crystalAlloyPlate").get(null);


            turniplabs.halplibe.helper.RecipeBuilder.Shaped((String)ic2.IC2.MOD_ID)
                            .setShape(new String[]{"CaC", "SMS", "CaC"})
                            .addInput('M', (net.minecraft.core.item.IItemConvertible)ic2.IC2Blocks.advancedMachineBlock)
                            .addInput('C', (net.minecraft.core.item.IItemConvertible)ic2.IC2Items.circuitAdvanced)
                            .addInput('a', (net.minecraft.core.item.IItemConvertible)crystalAlloyPlate)
                            .addInput('S', (net.minecraft.core.item.IItemConvertible)crystalEmpty)
                            .create("converter_eu_to_signal", ic2.IC2Blocks.converterEuToCatalyst.getDefaultStack());


            turniplabs.halplibe.helper.RecipeBuilder.Shaped((String)ic2.IC2.MOD_ID)
                            .setShape(new String[]{"CIC", "RMR", "CIC"})
                            .addInput('M', (net.minecraft.core.item.IItemConvertible)ic2.IC2Blocks.advancedMachineBlock)
                            .addInput('C', (net.minecraft.core.item.IItemConvertible)ic2.IC2Items.circuitAdvanced)
                            .addInput('I', (net.minecraft.core.item.IItemConvertible)ic2.IC2Items.ingotRefinedIron)
                            .addInput('R', (net.minecraft.core.item.IItemConvertible)net.minecraft.core.item.Items.DUST_REDSTONE)
                            .create("converter_signal_to_eu", ic2.IC2Blocks.converterCatalystToEu.getDefaultStack());
            IC2.LOGGER.info("Signal Industries converter recipes registered");
        }
        catch (Throwable e) {
            IC2.LOGGER.error("Failed to register SI converter recipes", (Throwable)e);
        }


        if (!FabricLoader.getInstance().isModLoaded("catalyst-energy")) {
            return;
        }
        try {

            turniplabs.halplibe.helper.RecipeBuilder.Shaped((String)ic2.IC2.MOD_ID)
                            .setShape(new String[]{"CIC", "BMB", "CIC"})
                            .addInput('M', (net.minecraft.core.item.IItemConvertible)ic2.IC2Blocks.advancedMachineBlock)
                            .addInput('B', ic2.IC2Recipes.anyCharge(ic2.IC2Items.batteryRE))
                            .addInput('C', (net.minecraft.core.item.IItemConvertible)ic2.IC2Items.circuit)
                            .addInput('I', (net.minecraft.core.item.IItemConvertible)ic2.IC2Items.ingotRefinedIron)
                            .create("converter_eu_to_energy", ic2.IC2Blocks.converterEuToEnergy.getDefaultStack());

            turniplabs.halplibe.helper.RecipeBuilder.Shaped((String)ic2.IC2.MOD_ID)
                            .setShape(new String[]{"AIA", "BMB", "AIA"})
                            .addInput('M', (net.minecraft.core.item.IItemConvertible)ic2.IC2Blocks.advancedMachineBlock)
                            .addInput('B', ic2.IC2Recipes.anyCharge(ic2.IC2Items.batteryRE))
                            .addInput('A', (net.minecraft.core.item.IItemConvertible)ic2.IC2Items.circuitAdvanced)
                            .addInput('I', (net.minecraft.core.item.IItemConvertible)ic2.IC2Items.ingotRefinedIron)
                            .create("converter_energy_to_eu", ic2.IC2Blocks.converterEnergyToEu.getDefaultStack());
            IC2.LOGGER.info("Catalyst energy converter recipes registered");
        }
        catch (Throwable e) {
            IC2.LOGGER.error("Failed to register catalyst-energy converter recipes", (Throwable)e);
        }
    }

    public static void registerBlocks(int euToCatalystId, int catalystToEuId) {
        if (SIConverters.isCatalystInstalled()) {
            try {
                Class<?> impl = Class.forName("ic2.si.SIConvertersImpl");
                impl.getMethod("register", Integer.TYPE, Integer.TYPE).invoke(null, euToCatalystId, catalystToEuId);
            }
            catch (Exception e) {
                IC2.LOGGER.error("Failed to register SI energy converters", (Throwable)e);
            }
        }


        if (FabricLoader.getInstance().isModLoaded("catalyst-energy")) {
            try {
                Class<?> impl = Class.forName("ic2.si.SIConvertersImpl");
                impl.getMethod("registerEnergy", Integer.TYPE, Integer.TYPE)
                                .invoke(null, IC2Config.block("converter_eu_to_energy"), IC2Config.block("converter_energy_to_eu"));
            }
            catch (Exception e) {
                IC2.LOGGER.error("Failed to register Catalyst energy converters", (Throwable)e);
            }
        }
    }
}

