

package ic2.peripherals;

import ic2.tileentity.TileEntityNuclearReactor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.minecraft.core.block.entity.TileEntity;








public final class IC2Peripherals {
    private static final Map<Class<? extends TileEntity>, Function<TileEntity, IC2Peripheral>> PROVIDERS = new ConcurrentHashMap<Class<? extends TileEntity>, Function<TileEntity, IC2Peripheral>>();

    private IC2Peripherals() {
    }

    public static void init() {
        IC2Peripherals.registerProvider(TileEntityNuclearReactor.class, te -> new ReactorPeripheral((TileEntityNuclearReactor)te));
    }

    public static void registerProvider(Class<? extends TileEntity> type, Function<TileEntity, IC2Peripheral> factory) {
        PROVIDERS.put(type, factory);
    }

    public static IC2Peripheral getPeripheral(TileEntity te) {
        if (te == null) {
            return null;
        }
        Function<TileEntity, IC2Peripheral> factory = PROVIDERS.get(te.getClass());
        if (factory != null) {
            return factory.apply(te);
        }
        for (Map.Entry<Class<? extends TileEntity>, Function<TileEntity, IC2Peripheral>> e : PROVIDERS.entrySet()) {
            if (e.getKey().isInstance(te)) {
                return e.getValue().apply(te);
            }
        }
        return null;
    }

    public static boolean hasPeripheral(TileEntity te) {
        return IC2Peripherals.getPeripheral(te) != null;
    }
}
