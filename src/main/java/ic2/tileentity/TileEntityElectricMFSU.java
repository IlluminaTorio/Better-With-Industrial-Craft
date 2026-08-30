

package ic2.tileentity;

import ic2.tileentity.TileEntityElectricBlock;

public class TileEntityElectricMFSU
extends TileEntityElectricBlock {
    public TileEntityElectricMFSU() {
        super(3, 512, 10000000);
    }

    @Override
    public String getMachineName() {
        return "MFSU";
    }

    @Override
    public String getGuiTitleKey() {
        return "tile.ic2.energy.mfsu.name";
    }
}

