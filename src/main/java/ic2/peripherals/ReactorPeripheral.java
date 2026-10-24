

package ic2.peripherals;

import ic2.tileentity.TileEntityNuclearReactor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.item.ItemStack;

















public class ReactorPeripheral
implements IC2Peripheral {
    private final TileEntityNuclearReactor reactor;

    public ReactorPeripheral(TileEntityNuclearReactor reactor) {
        this.reactor = reactor;
    }

    private static final String[] METHODS = new String[]{
        "getHeat", "getMaxHeat", "getHeatPercent", "getOutput", "getMaxOutput",
        "getSize", "isActive", "isExploded", "getInventory"
    };

    @Override
    public String getType() {
        return "ic2_reactor";
    }

    @Override
    public String[] getMethodNames() {
        return METHODS;
    }

    @Override
    public Object[] callMethod(String method, Object[] arguments) throws Exception {
        switch (method) {
            case "getHeat":
                return new Object[]{(double)this.reactor.heat};
            case "getMaxHeat": {
                return new Object[]{(double)this.maxHeat()};
            }
            case "getHeatPercent": {
                int maxHeat = this.maxHeat();
                return new Object[]{maxHeat <= 0 ? 0.0 : Math.min(100.0, this.reactor.heat * 100.0 / maxHeat)};
            }
            case "getOutput":
                return new Object[]{(double)this.reactor.output};
            case "getMaxOutput":
                return new Object[]{(double)this.reactor.getMaxEnergyOutput()};
            case "getSize":
                return new Object[]{(double)this.reactor.getReactorSize()};
            case "isActive":
                return new Object[]{this.reactor.active};
            case "isExploded":
                return new Object[]{this.reactor.exploded};
            case "getInventory":
                return new Object[]{this.inventoryTable()};
        }
        throw new Exception("Unknown method: " + method);
    }

    @Override
    public String getHelp(String method) {
        switch (method) {
            case "getHeat": return "Current reactor heat";
            case "getMaxHeat": return "Heat limit for the current reactor size";
            case "getHeatPercent": return "Reactor heat as percentage 0..100";
            case "getOutput": return "Current EU/t output";
            case "getMaxOutput": return "Maximum EU/t output for the current setup";
            case "getSize": return "Reactor size in chambers";
            case "isActive": return "Whether the reactor is currently producing EU";
            case "isExploded": return "Whether the reactor has exploded";
            case "getInventory": return "Reactor slot contents as a table";
        }
        return null;
    }

    private int maxHeat() {
        int maxHeat = 10000;
        maxHeat += 1000 * (this.reactor.getReactorSize() - 3);
        return maxHeat;
    }

    private List<Map<String, Object>> inventoryTable() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        ItemStack[] inventory = this.reactor.inventory;
        if (inventory == null) {
            return list;
        }
        for (int i = 0; i < inventory.length; ++i) {
            ItemStack stack = inventory[i];
            if (stack == null) continue;
            Map<String, Object> entry = new LinkedHashMap<String, Object>();
            entry.put("slot", (double)i);
            entry.put("name", String.valueOf(stack.getItem().namespaceID));
            entry.put("count", (double)stack.stackSize);
            entry.put("damage", (double)stack.getMetadata());
            list.add(entry);
        }
        return list;
    }
}
