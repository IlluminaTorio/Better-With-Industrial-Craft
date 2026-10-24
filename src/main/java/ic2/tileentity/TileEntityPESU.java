package ic2.tileentity;

import ic2.IC2Config;

public class TileEntityPESU
extends TileEntityElectricBlock {
    public TileEntityPESU() {
        super(4, 2048, IC2Config.pesuMaxStorage());
    }

    @Override
    public String getNameByTier() {
        return "PESU";
    }

    @Override
    public int gaugeEnergyScaled(int i) {
        return (int)((long)this.energy * (long)i / (long)this.maxStorage);
    }

    @Override
    public String getMachineName() {
        return "PESU";
    }

    @Override
    public String getGuiTitleKey() {
        return "tile.ic2.energy.pesu.name";
    }
}
