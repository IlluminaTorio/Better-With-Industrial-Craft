

package ic2.worldgen;

import ic2.IC2Blocks;
import java.util.Random;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.WorldFeature;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;

public class WorldFeatureRubberTree
extends WorldFeature {
    public static final int MAX_HEIGHT = 8;
    private final TilePos pos = new TilePos();

    public boolean place(World world, Random random, int x, int y, int z) {
        int surface;
        for (surface = y; surface > 0 && world.isAirBlock(x, surface - 1, z); --surface) {
        }
        return this.grow(world, x, surface, z, random);
    }

    public boolean grow(World world, int x, int y, int z, Random random) {
        int i;
        int treeSpotChance = 25;
        int h = this.getGrowHeight(world, x, y, z);
        if (h < 2) {
            return false;
        }
        int height = h / 2;
        h -= h / 2;
        height += random.nextInt(h + 1);
        Block<?> rubWood = IC2Blocks.rubberWood;
        Block<?> rubLeaves = IC2Blocks.rubberLeaves;
        for (i = 0; i < height; ++i) {
            this.pos.set(x, y + i, z);
            if (random.nextInt(100) <= treeSpotChance) {
                treeSpotChance -= 10;
                world.setBlockTypeData((TilePosc)this.pos, rubWood, random.nextInt(4) + 2);
            } else {
                world.setBlockTypeData((TilePosc)this.pos, rubWood, 1);
            }
            if (height >= 4 && (height >= 7 || i <= 1) && i <= 2) continue;
            for (int a = x - 2; a <= x + 2; ++a) {
                for (int b = z - 2; b <= z + 2; ++b) {
                    boolean gen;
                    int c = i + 4 - height;
                    if (c < 1) {
                        c = 1;
                    }
                    boolean bl = gen = a > x - 2 && a < x + 2 && b > z - 2 && b < z + 2 || a > x - 2 && a < x + 2 && random.nextInt(c) == 0 || b > z - 2 && b < z + 2 && random.nextInt(c) == 0;
                    if (!gen || !world.isAirBlock(a, y + i, b)) continue;
                    this.pos.set(a, y + i, b);
                    world.setBlockTypeData((TilePosc)this.pos, rubLeaves, 0);
                }
            }
        }
        for (i = 0; i <= height / 4 + random.nextInt(2); ++i) {
            if (!world.isAirBlock(x, y + height + i, z)) continue;
            this.pos.set(x, y + height + i, z);
            world.setBlockTypeData((TilePosc)this.pos, rubLeaves, 0);
        }
        return true;
    }

    public int getGrowHeight(World world, int x, int y, int z) {
        Block below = world.getBlockType((TilePosc)this.pos.set(x, y - 1, z));
        if (below != Blocks.GRASS && below != Blocks.DIRT && below != Blocks.SAND) {
            return 0;
        }
        Block at = world.getBlockType((TilePosc)this.pos.set(x, y, z));
        if (at != null && at != Blocks.AIR && at != IC2Blocks.rubberSapling) {
            return 0;
        }
        int height = 1;
        while (world.isAirBlock(x, y + 1, z) && height < 8) {
            ++height;
            ++y;
        }
        return height;
    }
}

