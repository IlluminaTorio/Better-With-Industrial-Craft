

package ic2.mixin;

import ic2.IC2Blocks;
import ic2.IC2Config;
import ic2.block.BlockLogicIC2Ore;
import ic2.worldgen.WorldFeatureRubberTree;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import java.util.Map;
import java.util.Random;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.block.Block;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.biome.Biomes;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.chunk.perlin.overworld.ChunkDecoratorOverworld;
import net.minecraft.core.world.generate.feature.WorldFeatureOre;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ChunkDecoratorOverworld.class}, remap=false)
public class ChunkDecoratorOverworldMixin {
    @Shadow
    @Final
    private World world;

    @Inject(method={"decorate"}, at={@At(value="TAIL")})
    public void ic2$decorate(Chunk chunk, CallbackInfo ci) {
        int z;
        int y;
        int x;
        int n;
        Int2IntArrayMap variants;
        int chunkX = chunk.pos.x;
        int chunkZ = chunk.pos.z;
        int x0 = chunkX * 16;
        int z0 = chunkZ * 16;
        Random rand = new Random(this.world.getRandomSeed());
        long l1 = rand.nextLong() / 2L * 2L + 1L;
        long l2 = rand.nextLong() / 2L * 2L + 1L;
        rand.setSeed((long)chunkX * l1 + (long)chunkZ * l2 ^ this.world.getRandomSeed());
        if (IC2Config.config.getBoolean("WorldGen.rubberTreesEnabled")) {
            Biome biome = this.world.getBlockBiome((TilePosc)new TilePos(x0 + 8, 64, z0 + 8));
            int rubberTrees = 0;
            if (biome == Biomes.OVERWORLD_TAIGA || biome == Biomes.OVERWORLD_PLAINS || biome == Biomes.OVERWORLD_GRASSLANDS || biome == Biomes.OVERWORLD_SHRUBLAND) {
                rubberTrees += rand.nextInt(3);
            }
            if (biome == Biomes.OVERWORLD_FOREST || biome == Biomes.OVERWORLD_BIRCH_FOREST || biome == Biomes.OVERWORLD_BOREAL_FOREST || biome == Biomes.OVERWORLD_MEADOW || biome == Biomes.OVERWORLD_SEASONAL_FOREST) {
                rubberTrees += rand.nextInt(5) + 1;
            }
            if (biome == Biomes.OVERWORLD_RAINFOREST || biome == Biomes.OVERWORLD_SWAMPLAND || biome == Biomes.OVERWORLD_SWAMPLAND_MUDDY) {
                rubberTrees += rand.nextInt(10) + 5;
            }
            if (rand.nextInt(100) + 1 <= rubberTrees * 2) {
                int tx = x0 + rand.nextInt(16);
                int tz = z0 + rand.nextInt(16);
                int ty = this.world.getHeightValue(tx, tz);
                new WorldFeatureRubberTree().place(this.world, rand, tx, ty, tz);
            }
        }
        if (IC2Config.config.getBoolean("WorldGen.copperEnabled")) {
            variants = new Int2IntArrayMap();
            for (Block b : new Block[]{IC2Blocks.oreCopper, IC2Blocks.oreCopperBasalt, IC2Blocks.oreCopperLimestone, IC2Blocks.oreCopperGranite, IC2Blocks.oreCopperPermafrost}) {
                variants.putAll((Map)((BlockLogicIC2Ore)b.getLogic()).variantMap);
            }
            for (n = 0; n < 15; ++n) {
                x = x0 + rand.nextInt(16);
                y = rand.nextInt(40) + rand.nextInt(20) + 10;
                z = z0 + rand.nextInt(16);
                new WorldFeatureOre(variants, 10).place(this.world, rand, (TilePosc)new TilePos(x, y, z));
            }
        }
        if (IC2Config.config.getBoolean("WorldGen.tinEnabled")) {
            variants = new Int2IntArrayMap();
            for (Block b : new Block[]{IC2Blocks.oreTin, IC2Blocks.oreTinBasalt, IC2Blocks.oreTinLimestone, IC2Blocks.oreTinGranite, IC2Blocks.oreTinPermafrost}) {
                variants.putAll((Map)((BlockLogicIC2Ore)b.getLogic()).variantMap);
            }
            for (n = 0; n < 25; ++n) {
                x = x0 + rand.nextInt(16);
                y = rand.nextInt(40);
                z = z0 + rand.nextInt(16);
                new WorldFeatureOre(variants, 6).place(this.world, rand, (TilePosc)new TilePos(x, y, z));
            }
        }
        if (IC2Config.config.getBoolean("WorldGen.uraniumEnabled") && !FabricLoader.getInstance().isModLoaded("deep")) {
            variants = new Int2IntArrayMap();
            for (Block b : new Block[]{IC2Blocks.oreUranium, IC2Blocks.oreUraniumBasalt, IC2Blocks.oreUraniumLimestone, IC2Blocks.oreUraniumGranite, IC2Blocks.oreUraniumPermafrost}) {
                variants.putAll((Map)((BlockLogicIC2Ore)b.getLogic()).variantMap);
            }
            for (int n2 = 0; n2 < 20; ++n2) {
                x = x0 + rand.nextInt(16);
                y = rand.nextInt(64);
                z = z0 + rand.nextInt(16);
                new WorldFeatureOre(variants, 3).place(this.world, rand, (TilePosc)new TilePos(x, y, z));
            }
        }
    }
}

