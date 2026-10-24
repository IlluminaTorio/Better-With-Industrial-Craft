package ic2.client.btwaila.tooltip;

import ic2.tileentity.TileEntityIronFurnace;
import toufoumaster.btwaila.gui.components.AdvancedInfoComponent;
import toufoumaster.btwaila.util.ProgressBarOptions;


public class IC2IronFurnaceTooltip extends toufoumaster.btwaila.tooltips.TileTooltip<TileEntityIronFurnace> {
	@Override
	public void initTooltip() {
		addClass(TileEntityIronFurnace.class);
	}

	@Override
	public void drawAdvancedTooltip(TileEntityIronFurnace tile, AdvancedInfoComponent c) {
		ProgressBarOptions progress = new ProgressBarOptions(0, "Progress: ", true, true);
		c.drawProgressBarWithText(tile.progress, tile.operationLength, progress, 0);
		if (tile.maxFuel > 0 && tile.fuel > 0) {
			ProgressBarOptions fuel = new ProgressBarOptions(0, "Fuel: ", true, true);
			c.drawProgressBarWithText(tile.fuel, tile.maxFuel, fuel, 0);
		}
	}
}
