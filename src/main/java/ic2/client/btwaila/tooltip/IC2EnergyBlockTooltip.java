package ic2.client.btwaila.tooltip;

import ic2.tileentity.TileEntityElectricBatBox;
import ic2.tileentity.TileEntityElectricBlock;
import ic2.tileentity.TileEntityElectricMFE;
import ic2.tileentity.TileEntityElectricMFSU;
import toufoumaster.btwaila.gui.components.AdvancedInfoComponent;
import toufoumaster.btwaila.util.ProgressBarOptions;


public class IC2EnergyBlockTooltip extends toufoumaster.btwaila.tooltips.TileTooltip<TileEntityElectricBlock> {
	@Override
	public void initTooltip() {
		addClass(TileEntityElectricBatBox.class);
		addClass(TileEntityElectricMFE.class);
		addClass(TileEntityElectricMFSU.class);
	}

	@Override
	public void drawAdvancedTooltip(TileEntityElectricBlock tile, AdvancedInfoComponent c) {
		ProgressBarOptions energy = new ProgressBarOptions(0, "EU: ", true, true);
		c.drawProgressBarWithText(tile.energy, tile.maxStorage, energy, 0);
	}
}
