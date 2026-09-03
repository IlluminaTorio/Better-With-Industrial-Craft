package ic2.client.btwaila.tooltip;

import ic2.tileentity.TileEntityNuclearReactor;
import ic2.tileentity.TileEntityReactorChamber;
import toufoumaster.btwaila.gui.components.AdvancedInfoComponent;
import toufoumaster.btwaila.tooltips.TileTooltip;
import toufoumaster.btwaila.util.ProgressBarOptions;

public class IC2ReactorChamberTooltip extends TileTooltip<TileEntityReactorChamber> {
	@Override
	public void initTooltip() {
		this.addClass(TileEntityReactorChamber.class);
	}

	@Override
	public void drawAdvancedTooltip(TileEntityReactorChamber tile, AdvancedInfoComponent c) {
		TileEntityNuclearReactor reactor = tile.getReactor();
		if (reactor == null) {
			c.drawStringWithShadow("Reactor: not connected", 0);
		} else {
			int maxHeat = 10000;
			maxHeat += 1000 * (reactor.getReactorSize() - 3);
			ProgressBarOptions heat = new ProgressBarOptions(0, "Heat: ", true, true);
			c.drawProgressBarWithText(reactor.heat, maxHeat, heat, 0);
			c.drawStringWithShadow("Output: " + reactor.output + " EU", 0);
		}
	}
}
