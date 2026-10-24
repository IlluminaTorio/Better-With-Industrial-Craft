

package ic2.energy;

import ic2.energy.IEnergyAcceptor;
import ic2.energy.IEnergyEmitter;

public interface IEnergyConductor
extends IEnergyAcceptor,
IEnergyEmitter {
    public double getConductionLoss();

    public int getInsulationEnergyAbsorption();

    public int getInsulationBreakdownEnergy();

    public int getConductorBreakdownEnergy();

    public void removeInsulation();

    public void removeConductor();
}

