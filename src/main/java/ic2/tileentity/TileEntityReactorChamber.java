

package ic2.tileentity;

import ic2.energy.Direction;
import ic2.energy.EnergyNet;
import ic2.energy.IEnergySource;
import ic2.tileentity.TileEntityIC2Block;
import ic2.tileentity.TileEntityNuclearReactor;
import net.minecraft.core.block.entity.TileEntity;

public class TileEntityReactorChamber
extends TileEntityIC2Block
implements IEnergySource {
    @Override
    public boolean emitsEnergyTo(TileEntity receiver, Direction direction) {
        return true;
    }

    @Override
    public int getMaxEnergyOutput() {
        return 240 * TileEntityNuclearReactor.pulsePower();
    }

    public int sendEnergy(int send) {
        return EnergyNet.getForWorld(this.worldObj).emitEnergyFrom(this, send);
    }
}

