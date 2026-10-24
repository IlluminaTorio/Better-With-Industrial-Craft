package ic2.tileentity;

import ic2.IC2Config;

public class TileEntityCapacitorBank
extends TileEntityElectricBlock {
    public TileEntityCapacitorBank() {
        super(5, IC2Config.iesuOutput(), IC2Config.iesuMaxStorage());
    }

    @Override
    public String getNameByTier() {
        return "IESU";
    }

    @Override
    public String getGuiTitleKey() {
        return "tile.ic2.energy.capbank.name";
    }

    @Override
    public String getMachineName() {
        return "IESU";
    }
}
