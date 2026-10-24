

package ic2.item.tool;

import ic2.item.tool.ItemElectricTool;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.tag.BlockTags;

public class ItemDrill
extends ItemElectricTool {
    public final boolean diamond;

    public ItemDrill(String name, String namespaceId, int id, boolean diamond) {
        super(name, namespaceId, id, diamond ? 3 : 2, 1, diamond ? 80 : 50, diamond ? 80 : 100, diamond ? 122 : 202);
        this.diamond = diamond;
    }

    @Override
    protected boolean isEffective(Block<?> block) {
        return super.isEffective(block) || block.hasTag(BlockTags.MINEABLE_BY_SHOVEL);
    }

    @Override
    protected float getEfficiency() {
        return this.diamond ? 16.0f : 8.0f;
    }
}

