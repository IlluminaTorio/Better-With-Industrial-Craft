

package ic2.energy;

import ic2.energy.Direction;
import ic2.energy.IEnergyAcceptor;

public interface IEnergySink
extends IEnergyAcceptor {
    public boolean demandsEnergy();

    public int injectEnergy(Direction var1, int var2);
}

