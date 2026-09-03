package ic2.client.btwaila.tooltip;

import ic2.tileentity.TileEntityCanner;
import ic2.tileentity.TileEntityElecMachine;
import ic2.tileentity.TileEntityInduction;
import ic2.tileentity.TileEntityMatter;
import ic2.tileentity.TileEntityMiner;
import ic2.tileentity.TileEntityPlasmafier;
import ic2.tileentity.TileEntityPump;
import ic2.tileentity.TileEntityRareEarthExtractor;
import ic2.tileentity.TileEntitySlowGrinder;
import ic2.tileentity.TileEntityTerraformer;
import ic2.tileentity.TileEntityWoodGasserElec;
import toufoumaster.btwaila.gui.components.AdvancedInfoComponent;
import toufoumaster.btwaila.util.ProgressBarOptions;


public class IC2ElecMachineTooltip extends toufoumaster.btwaila.tooltips.TileTooltip<TileEntityElecMachine> {
        @Override
        public void initTooltip() {
                addClass(TileEntityCanner.class);
                addClass(TileEntityMatter.class);
                addClass(TileEntityTerraformer.class);
                addClass(TileEntityMiner.class);
                addClass(TileEntityPump.class);
                addClass(TileEntityInduction.class);
                addClass(TileEntitySlowGrinder.class);
                addClass(TileEntityRareEarthExtractor.class);
                addClass(TileEntityPlasmafier.class);
                addClass(TileEntityWoodGasserElec.class);
        }

        @Override
        public void drawAdvancedTooltip(TileEntityElecMachine tile, AdvancedInfoComponent c) {
                if (tile.maxEnergy > 0) {
                        ProgressBarOptions energy = new ProgressBarOptions(0, "EU: ", true, true);
                        c.drawProgressBarWithText(tile.energy, tile.maxEnergy, energy, 0);
                }
        }
}
