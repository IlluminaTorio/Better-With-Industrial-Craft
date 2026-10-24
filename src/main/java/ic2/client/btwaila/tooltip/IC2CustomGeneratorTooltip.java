package ic2.client.btwaila.tooltip;

import ic2.tileentity.TileEntityCustomGenerator;
import ic2.tileentity.TileEntityOceanCurrentGenerator;
import ic2.tileentity.TileEntityTurbineSolar;
import ic2.tileentity.TileEntityWaveGenerator;
import toufoumaster.btwaila.gui.components.AdvancedInfoComponent;
import toufoumaster.btwaila.util.ProgressBarOptions;

public class IC2CustomGeneratorTooltip extends toufoumaster.btwaila.tooltips.TileTooltip<TileEntityCustomGenerator> {
        @Override
        public void initTooltip() {
                addClass(TileEntityTurbineSolar.class);
                addClass(TileEntityOceanCurrentGenerator.class);
                addClass(TileEntityWaveGenerator.class);
        }

        @Override
        public void drawAdvancedTooltip(TileEntityCustomGenerator tile, AdvancedInfoComponent c) {
                ProgressBarOptions storage = new ProgressBarOptions(0, "EU: ", true, true);
                c.drawProgressBarWithText(tile.storage, tile.maxStorage, storage, 0);
        }
}
