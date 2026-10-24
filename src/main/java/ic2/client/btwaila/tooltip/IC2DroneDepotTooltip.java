package ic2.client.btwaila.tooltip;

import ic2.tileentity.TileEntityDroneDepot;
import toufoumaster.btwaila.gui.components.AdvancedInfoComponent;
import toufoumaster.btwaila.util.ProgressBarOptions;

public class IC2DroneDepotTooltip extends toufoumaster.btwaila.tooltips.TileTooltip<TileEntityDroneDepot> {
        @Override
        public void initTooltip() {
                addClass(TileEntityDroneDepot.class);
        }

        @Override
        public void drawAdvancedTooltip(TileEntityDroneDepot tile, AdvancedInfoComponent c) {
                ProgressBarOptions energy = new ProgressBarOptions(0, "EU: ", true, true);
                c.drawProgressBarWithText(tile.energy, TileEntityDroneDepot.MAX_ENERGY, energy, 0);
                c.drawStringWithShadow("Ch: " + tile.channel, 0);
        }
}
