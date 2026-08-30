

package ic2.energy;

import ic2.energy.Direction;
import net.minecraft.core.block.entity.TileEntity;

public interface IEnergyEmitter {
    public boolean emitsEnergyTo(TileEntity var1, Direction var2);
}

