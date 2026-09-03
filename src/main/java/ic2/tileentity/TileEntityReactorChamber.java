

package ic2.tileentity;

import ic2.energy.Direction;
import ic2.energy.EnergyNet;
import ic2.energy.IEnergySource;
import ic2.tileentity.TileEntityIC2Block;
import ic2.tileentity.TileEntityNuclearReactor;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;

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

    public static TileEntityNuclearReactor getReactor(World world, TilePosc pos) {
        if (world != null && pos != null) {
            for (int dx = -1; dx <= 1; ++dx) {
                for (int dy = -1; dy <= 1; ++dy) {
                    for (int dz = -1; dz <= 1; ++dz) {
                        TileEntity te = world.getTileEntity(new TilePos(pos.x() + dx, pos.y() + dy, pos.z() + dz));
                        if (te instanceof TileEntityNuclearReactor) {
                            return (TileEntityNuclearReactor)te;
                        }
                    }
                }
            }
            return null;
        }
        return null;
    }

    public TileEntityNuclearReactor getReactor() {
        return TileEntityReactorChamber.getReactor(this.worldObj, this.tilePos);
    }
}

