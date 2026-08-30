

package ic2.item;

import net.minecraft.core.world.World;

public interface ITerraformingBP {
    public int getConsume();

    public int getRange();

    public boolean terraform(World var1, int var2, int var3, int var4);
}

