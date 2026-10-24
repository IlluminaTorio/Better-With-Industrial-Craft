

package ic2.tileentity;

import ic2.tileentity.TileEntityElectricBlock;

public class TileEntityElectricMFE
extends TileEntityElectricBlock {
    public TileEntityElectricMFE() {
        super(2, 128, 600000);
    }

    @Override
    public String getMachineName() {
        return "MFE";
    }

    @Override
    public String getGuiTitleKey() {
        return "tile.ic2.energy.mfe.name";
    }
}

