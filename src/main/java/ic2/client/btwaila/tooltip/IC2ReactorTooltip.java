package ic2.client.btwaila.tooltip;

import ic2.tileentity.TileEntityNuclearReactor;
import toufoumaster.btwaila.gui.components.AdvancedInfoComponent;
import toufoumaster.btwaila.util.ProgressBarOptions;


public class IC2ReactorTooltip extends toufoumaster.btwaila.tooltips.TileTooltip<TileEntityNuclearReactor> {
	@Override
	public void initTooltip() {
		addClass(TileEntityNuclearReactor.class);
	}

	@Override
	public void drawAdvancedTooltip(TileEntityNuclearReactor tile, AdvancedInfoComponent c) {
		int maxHeat = 10000;
		try {
			maxHeat += 1000 * (tile.getReactorSize() - 3);
		}
		catch (Throwable t) {

		}
		ProgressBarOptions heat = new ProgressBarOptions(0, "Heat: ", true, true);
		c.drawProgressBarWithText(tile.heat, maxHeat, heat, 0);
		c.drawStringWithShadow("Output: " + tile.output + " EU", 0);
	}
}
