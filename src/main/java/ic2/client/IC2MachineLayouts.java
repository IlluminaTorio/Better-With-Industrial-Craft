package ic2.client;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.data.registry.Registry;
import net.minecraft.core.data.registry.recipe.RecipeEntryBase;
import net.minecraft.core.data.registry.recipe.RecipeGroup;
import net.minecraft.core.data.registry.recipe.RecipeNamespace;

public class IC2MachineLayouts {
    public static class Layout {
        public final String nameKey;
        public final int w;
        public final int h;
        public final int inX;
        public final int inY;
        public final int in2X;
        public final int in2Y;
        public final int outX;
        public final int outY;
        public final int arrowX;
        public final int arrowY;

        public Layout(String nameKey, int w, int h, int inX, int inY, int in2X, int in2Y, int outX, int outY, int arrowX, int arrowY) {
            this.nameKey = nameKey;
            this.w = w;
            this.h = h;
            this.inX = inX;
            this.inY = inY;
            this.in2X = in2X;
            this.in2Y = in2Y;
            this.outX = outX;
            this.outY = outY;
            this.arrowX = arrowX;
            this.arrowY = arrowY;
        }

        public boolean hasSecondarySlot() {
            return this.in2X >= 0;
        }
    }

    private static final Map<String, Layout> LAYOUTS = new HashMap<String, Layout>();

    private static void put(String group, int w, int h, int inX, int inY, int in2X, int in2Y, int outX, int outY, int arrowX, int arrowY) {
        LAYOUTS.put(group, new Layout("tile.ic2.machine." + group + ".name", w, h, inX, inY, in2X, in2Y, outX, outY, arrowX, arrowY));
    }

    static {
        put("macerator", 120, 40, 45, 12, -1, -1, 101, 12, 68, 12);
        put("rotary_macerator", 120, 40, 45, 12, -1, -1, 101, 12, 68, 12);
        put("extractor", 120, 40, 45, 12, -1, -1, 101, 12, 68, 12);
        put("compressor", 120, 40, 45, 12, -1, -1, 101, 12, 68, 12);
        put("singularity_compressor", 120, 40, 45, 12, -1, -1, 101, 12, 68, 12);
        put("rare_earth_extractor", 120, 40, 45, 12, -1, -1, 101, 12, 68, 12);
        put("slow_grinder", 120, 40, 45, 12, -1, -1, 101, 12, 68, 12);
        put("electrolyzer", 120, 40, 45, 12, -1, -1, 101, 12, 68, 12);
        put("mass_fabricator", 120, 40, 45, 12, -1, -1, 101, 12, 68, 12);
        put("wood_gasser", 120, 40, 45, 12, -1, -1, 101, 12, 68, 12);
        put("canner", 120, 44, 45, 3, 45, 25, 101, 14, 68, 14);
        put("plasmafier", 120, 44, 45, 3, 45, 25, 101, 14, 68, 14);
    }

    public static Layout get(String group) {
        return LAYOUTS.get(group);
    }

    public static Layout getLayoutFor(RecipeEntryBase<?, ?, ?> recipe) {
        try {
            RecipeGroup<?> group = recipe.parent;
            if (group == null) {
                return null;
            }
            Registry ns = group.getParent();
            if (ns == null) {
                return null;
            }
            String groupKey = ns.getKey((Object)group);
            return groupKey == null ? null : LAYOUTS.get(groupKey);
        }
        catch (Throwable t) {
            return null;
        }
    }
}
