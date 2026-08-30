package ic2.client.btwaila.tooltip;

import ic2.tileentity.TileEntityBaseGenerator;
import ic2.tileentity.TileEntityGenerator;
import ic2.tileentity.TileEntityGeoGenerator;
import ic2.tileentity.TileEntitySolarGenerator;
import ic2.tileentity.TileEntityWaterGenerator;
import ic2.tileentity.TileEntityWindGenerator;
import toufoumaster.btwaila.gui.components.AdvancedInfoComponent;
import toufoumaster.btwaila.util.ProgressBarOptions;


public class IC2GeneratorTooltip extends toufoumaster.btwaila.tooltips.TileTooltip<TileEntityBaseGenerator> {
        @Override
        public void initTooltip() {
                addClass(TileEntityGenerator.class);
                addClass(TileEntityGeoGenerator.class);
                addClass(TileEntityWaterGenerator.class);
                addClass(TileEntitySolarGenerator.class);
                addClass(TileEntityWindGenerator.class);
        }

        @Override
        public void drawAdvancedTooltip(TileEntityBaseGenerator tile, AdvancedInfoComponent c) {
                ProgressBarOptions storage = new ProgressBarOptions(0, "EU: ", true, true);
                c.drawProgressBarWithText(tile.storage, tile.maxStorage, storage, 0);
        }
}
