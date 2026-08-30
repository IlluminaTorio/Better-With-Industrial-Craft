

package ic2.si;

import ic2.IC2;
import java.util.ArrayList;
import sunsetsatellite.catalyst.fluids.util.Fluid;
import sunsetsatellite.catalyst.fluids.util.FluidStack;

public final class SIEnergy {
    private static Fluid energyFluid;

    
    public static final int EU_PER_MB = 64;

    
    public static final int TANK_CAPACITY = 64;

    
    public static final int EU_BUFFER = 256;

    
    public static final int TRANSFER = 256;

    
    public static final int MAX_EU_OUTPUT = 256;

    
    public static final int MAX_EU_INPUT = 512;

    private SIEnergy() {
    }

    public static Fluid energy() {
        if (energyFluid == null) {
            try {
                Class<?> cls = Class.forName("sunsetsatellite.signalindustries.SIFluids");
                energyFluid = (Fluid)cls.getField("ENERGY").get(null);
            }
            catch (Throwable t) {
                IC2.LOGGER.warn("Signal Industries ENERGY fluid not found: {}", (Object)t.toString());
            }
        }
        return energyFluid;
    }

    public static ArrayList<Fluid> acceptedEnergy() {
        ArrayList<Fluid> list = new ArrayList<Fluid>();
        Fluid f = SIEnergy.energy();
        if (f != null) {
            list.add(f);
        }
        return list;
    }

    public static FluidStack energyStack(int amount) {
        Fluid f = SIEnergy.energy();
        return f != null ? new FluidStack(f, amount) : null;
    }
}
