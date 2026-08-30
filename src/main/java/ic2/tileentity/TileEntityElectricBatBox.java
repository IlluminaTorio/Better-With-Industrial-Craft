

package ic2.tileentity;

import ic2.tileentity.TileEntityElectricBlock;

public class TileEntityElectricBatBox
extends TileEntityElectricBlock {
    public TileEntityElectricBatBox() {
        super(1, 32, 40000);
    }

    @Override
    public String getMachineName() {
        return "BatBox";
    }

    @Override
    public String getGuiTitleKey() {
        return "tile.ic2.energy.batbox.name";
    }
}

