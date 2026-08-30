package ic2.client.btwaila;

import ic2.IC2;
import ic2.client.btwaila.tooltip.IC2ElecMachineTooltip;
import ic2.client.btwaila.tooltip.IC2EnergyBlockTooltip;
import ic2.client.btwaila.tooltip.IC2GeneratorTooltip;
import ic2.client.btwaila.tooltip.IC2IronFurnaceTooltip;
import ic2.client.btwaila.tooltip.IC2MachineTooltip;
import ic2.client.btwaila.tooltip.IC2ReactorTooltip;
import org.slf4j.Logger;
import toufoumaster.btwaila.entryplugins.waila.BTWailaCustomTooltipPlugin;
import toufoumaster.btwaila.tooltips.TooltipRegistry;


public class BTWailaIC2Plugin implements BTWailaCustomTooltipPlugin {
        @Override
        public void initializePlugin(TooltipRegistry tooltipRegistry, Logger logger) {
                logger.info("Loading tooltips from " + IC2.MOD_ID + "..");
                tooltipRegistry.register(new IC2MachineTooltip());
                tooltipRegistry.register(new IC2ElecMachineTooltip());
                tooltipRegistry.register(new IC2GeneratorTooltip());
                tooltipRegistry.register(new IC2EnergyBlockTooltip());
                tooltipRegistry.register(new IC2ReactorTooltip());
                tooltipRegistry.register(new IC2IronFurnaceTooltip());
        }
}
