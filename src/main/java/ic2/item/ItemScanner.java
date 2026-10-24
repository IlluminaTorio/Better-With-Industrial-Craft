

package ic2.item;

import ic2.IC2Blocks;
import ic2.item.ElectricItem;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemScanner
extends ElectricItem {
    protected static final Map<Integer, Integer> BLOCK_VALUES = new HashMap<Integer, Integer>();
    public final int scanTier;

    public ItemScanner(String name, String namespaceId, int id, int scanTier) {
        super(name, namespaceId, id, scanTier, 50, 50);
        this.scanTier = scanTier;
        this.setMaxDamage(202);
        this.setMaxStackSize(1);
    }

    public boolean onUseOnBlock(@Nullable ItemStack selfStack, @NotNull World world, @Nullable Player player, @NotNull TilePosc pos, @NotNull Side side, double xHit, double yHit) {
        this.scan(selfStack, world, player);
        return true;
    }

    public ItemStack onUse(@NotNull ItemStack selfStack, @NotNull World world, @NotNull Player player) {
        this.scan(selfStack, world, player);
        return selfStack;
    }

    private void scan(ItemStack itemstack, World world, Player entityplayer) {
        int cost;
        int n = cost = this.scanTier == 1 ? 1 : 5;
        if (!this.use(itemstack, cost)) {
            return;
        }
        world.playSoundEffect((Entity)entityplayer, SoundCategory.WORLD_SOUNDS, entityplayer.x, entityplayer.y, entityplayer.z, "random.click", 1.0f, 1.0f);
        boolean adv = this.scanTier == 2;
        Integer value = this.valueOfArea(world, (int)entityplayer.x, (int)entityplayer.y, (int)entityplayer.z, adv);
        if (value != null) {
            entityplayer.sendMessage(adv ? "SCAN RESULT: Ore value in this area is " + value : "SCAN RESULT: Ore density in this area is " + value);
        }
    }

    public Integer valueOfArea(World world, int x, int y, int z, boolean adv) {
        int totalScore = 0;
        int blocksScanned = 0;
        int range = adv ? 4 : 2;
        for (int dx = -range; dx <= range; ++dx) {
            for (int dy = -range; dy <= range; ++dy) {
                for (int dz = -range; dz <= range; ++dz) {
                    Block block = world.getBlock(x + dx, y + dy, z + dz);
                    if (block == null || block == Blocks.AIR) continue;
                    if (adv && ItemScanner.isValuable(block.id())) {
                        totalScore += ItemScanner.valueOf(block.id());
                    } else if (ItemScanner.isValuable(block.id())) {
                        ++totalScore;
                    }
                    ++blocksScanned;
                }
            }
        }
        return blocksScanned > 0 ? Integer.valueOf((int)(1000.0 * (double)totalScore / (double)blocksScanned)) : null;
    }

    public static boolean isValuable(int id) {
        return ItemScanner.valueOf(id) > 0;
    }

    public static int valueOf(int id) {
        return BLOCK_VALUES.getOrDefault(id, 0);
    }

    static {
        Block[][] groups = new Block[][]{{Blocks.ORE_COAL_STONE, Blocks.ORE_COAL_BASALT, Blocks.ORE_COAL_LIMESTONE, Blocks.ORE_COAL_GRANITE, Blocks.ORE_COAL_PERMAFROST}, {IC2Blocks.oreTin}, {IC2Blocks.oreCopper}, {Blocks.ORE_GOLD_STONE, Blocks.ORE_GOLD_BASALT, Blocks.ORE_GOLD_LIMESTONE, Blocks.ORE_GOLD_GRANITE, Blocks.ORE_GOLD_PERMAFROST}, {Blocks.ORE_REDSTONE_STONE, Blocks.ORE_REDSTONE_BASALT, Blocks.ORE_REDSTONE_LIMESTONE, Blocks.ORE_REDSTONE_GRANITE, Blocks.ORE_REDSTONE_PERMAFROST}, {Blocks.ORE_LAPIS_STONE, Blocks.ORE_LAPIS_BASALT, Blocks.ORE_LAPIS_LIMESTONE, Blocks.ORE_LAPIS_GRANITE, Blocks.ORE_LAPIS_PERMAFROST}, {Blocks.ORE_IRON_STONE, Blocks.ORE_IRON_BASALT, Blocks.ORE_IRON_LIMESTONE, Blocks.ORE_IRON_GRANITE, Blocks.ORE_IRON_PERMAFROST}, {IC2Blocks.oreUranium}, {Blocks.ORE_DIAMOND_STONE, Blocks.ORE_DIAMOND_BASALT, Blocks.ORE_DIAMOND_LIMESTONE, Blocks.ORE_DIAMOND_GRANITE, Blocks.ORE_DIAMOND_PERMAFROST}};
        int[] values = new int[]{1, 2, 2, 3, 3, 3, 4, 4, 5};
        for (int g = 0; g < groups.length; ++g) {
            for (Block block : groups[g]) {
                if (block == null) continue;
                BLOCK_VALUES.put(block.id(), values[g]);
            }
        }
    }
}

