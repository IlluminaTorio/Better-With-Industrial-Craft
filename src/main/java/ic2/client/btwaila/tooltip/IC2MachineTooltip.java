package ic2.client.btwaila.tooltip;

import ic2.tileentity.TileEntityCanner;
import ic2.tileentity.TileEntityCompressor;
import ic2.tileentity.TileEntityElecFurnace;
import ic2.tileentity.TileEntityExtractor;
import ic2.tileentity.TileEntityMacerator;
import ic2.tileentity.TileEntityRecycler;
import ic2.tileentity.TileEntityElectricMachine;
import toufoumaster.btwaila.gui.components.AdvancedInfoComponent;
import toufoumaster.btwaila.util.ProgressBarOptions;


public class IC2MachineTooltip extends toufoumaster.btwaila.tooltips.TileTooltip<TileEntityElectricMachine> {
        @Override
        public void initTooltip() {
                addClass(TileEntityMacerator.class);
                addClass(TileEntityExtractor.class);
                addClass(TileEntityCompressor.class);
                addClass(TileEntityRecycler.class);
                addClass(TileEntityElecFurnace.class);
        }

        @Override
        public void drawAdvancedTooltip(TileEntityElectricMachine tile, AdvancedInfoComponent c) {
                if (tile.maxEnergy > 0) {
                        ProgressBarOptions energy = new ProgressBarOptions(0, "EU: ", true, true);
                        c.drawProgressBarWithText(tile.energy, tile.maxEnergy, energy, 0);
                }
                if (tile.operationLength > 0) {
                        ProgressBarOptions progress = new ProgressBarOptions(0, "Progress: ", true, true);
                        c.drawProgressBarWithText(tile.progress, tile.operationLength, progress, 0);
                }
        }
}
