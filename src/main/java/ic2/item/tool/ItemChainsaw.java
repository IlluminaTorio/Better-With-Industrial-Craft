

package ic2.item.tool;

import ic2.item.tool.ItemElectricTool;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.block.tag.BlockTags;

public class ItemChainsaw
extends ItemElectricTool {
    public ItemChainsaw(String name, String namespaceId, int id) {
        super(name, namespaceId, id, 2, 1, 50, 100, 202);
    }

    @Override
    protected boolean isEffective(Block<?> block) {
        return block.hasTag(BlockTags.MINEABLE_BY_AXE) || block.getMaterial() == Materials.WOOD || block.getMaterial() == Materials.PLANT;
    }

    @Override
    protected float getEfficiency() {
        return 12.0f;
    }
}

