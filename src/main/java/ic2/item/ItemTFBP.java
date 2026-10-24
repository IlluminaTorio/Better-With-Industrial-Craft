

package ic2.item;

import ic2.IC2Blocks;
import ic2.IC2Items;
import ic2.block.BlockLogicRubSapling;
import ic2.item.ITerraformingBP;
import ic2.tileentity.TileEntityTerraformer;
import java.util.Random;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicSaplingBase;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import net.minecraft.core.world.weather.Weathers;

public final class ItemTFBP {
    public static final Block<?>[] PLANTS = new Block[]{Blocks.TALLGRASS, Blocks.FLOWER_YELLOW, Blocks.FLOWER_RED, Blocks.SAPLING_OAK, Blocks.CROPS_WHEAT, Blocks.MUSHROOM_BROWN, Blocks.MUSHROOM_RED, IC2Blocks.rubberSapling, Blocks.DEADBUSH};

    private ItemTFBP() {
    }

    public static int pickRandomPlantId(Random random) {
        for (int i = 0; i < PLANTS.length; ++i) {
            if (random.nextInt(5) > 1) continue;
            return PLANTS[i].id();
        }
        return Blocks.TALLGRASS.id();
    }

    public static boolean isPlant(int id) {
        for (Block<?> plant : PLANTS) {
            if (plant == null || plant.id() != id) continue;
            return true;
        }
        return false;
    }

    public static ITerraformingBP of(ItemStack stack) {
        if (stack == null) {
            return null;
        }
        if (stack.getItem() == IC2Items.tfbpCultivation) {
            return new Cultivation();
        }
        if (stack.getItem() == IC2Items.tfbpIrrigation) {
            return new Irrigation();
        }
        if (stack.getItem() == IC2Items.tfbpChilling) {
            return new Chilling();
        }
        if (stack.getItem() == IC2Items.tfbpDesertification) {
            return new Desertification();
        }
        if (stack.getItem() == IC2Items.tfbpFlatification) {
            return new Flatification();
        }
        return null;
    }

    public static class Cultivation
    implements ITerraformingBP {
        @Override
        public int getConsume() {
            return 4000;
        }

        @Override
        public int getRange() {
            return 40;
        }

        @Override
        public boolean terraform(World world, int x, int z, int yCoord) {
            int y = TileEntityTerraformer.getFirstSolidBlockFrom(world, x, z, yCoord + 10);
            if (y == -1) {
                return false;
            }
            if (TileEntityTerraformer.switchGround(world, Blocks.SAND, Blocks.DIRT, x, y, z, true)) {
                return true;
            }
            Block block = world.getBlock(x, y, z);
            if (block == Blocks.DIRT) {
                world.setBlockWithNotify(x, y, z, Blocks.GRASS.id());
                return true;
            }
            if (block == Blocks.GRASS) {
                return this.growPlantsOn(world, x, y + 1, z);
            }
            return false;
        }

        public boolean growPlantsOn(World world, int x, int y, int z) {
            Block block = world.getBlock(x, y, z);
            if (block == null || block == Blocks.AIR || block == Blocks.TALLGRASS && world.rand.nextInt(4) == 0) {
                int plant = ItemTFBP.pickRandomPlantId(world.rand);
                if (plant == Blocks.CROPS_WHEAT.id()) {
                    world.setBlockWithNotify(x, y - 1, z, Blocks.FARMLAND_DIRT.id());
                }
                world.setBlockWithNotify(x, y, z, plant);
                return true;
            }
            return false;
        }
    }

    public static class Irrigation
    implements ITerraformingBP {
        @Override
        public int getConsume() {
            return 1000;
        }

        @Override
        public int getRange() {
            return 60;
        }

        @Override
        public boolean terraform(World world, int x, int z, int yCoord) {
            if (world.rand.nextInt(48000) == 0) {
                world.getWeatherManager().overrideWeather(Weathers.OVERWORLD_RAIN, 24000L);
                return true;
            }
            int y = TileEntityTerraformer.getFirstBlockFrom(world, x, z, yCoord + 10);
            if (y == -1) {
                return false;
            }
            if (TileEntityTerraformer.switchGround(world, Blocks.SAND, Blocks.DIRT, x, y, z, true)) {
                TileEntityTerraformer.switchGround(world, Blocks.SAND, Blocks.DIRT, x, y, z, true);
                return true;
            }
            Block block = world.getBlock(x, y, z);
            if (block == Blocks.TALLGRASS) {
                return this.spreadGrass(world, x + 1, y, z) || this.spreadGrass(world, x - 1, y, z) || this.spreadGrass(world, x, y, z + 1) || this.spreadGrass(world, x, y, z - 1);
            }
            if (block == Blocks.SAPLING_OAK) {
                Irrigation.bonemealGrow(world, x, y, z);
                return true;
            }
            if (block == IC2Blocks.rubberSapling) {
                BlockLogicRubSapling logic = (BlockLogicRubSapling)IC2Blocks.rubberSapling.getLogic();
                logic.growTree(world, (TilePosc)new TilePos(x, y, z), world.rand);
                return true;
            }
            if (block == Blocks.LOG_OAK) {
                int meta = world.getBlockData((TilePosc)new TilePos(x, y, z));
                world.setBlockAndMetadataWithNotify(x, y + 1, z, Blocks.LOG_OAK.id(), meta);
                this.createLeaves(world, x, y + 2, z, meta);
                this.createLeaves(world, x + 1, y + 1, z, meta);
                this.createLeaves(world, x - 1, y + 1, z, meta);
                this.createLeaves(world, x, y + 1, z + 1, meta);
                this.createLeaves(world, x, y + 1, z - 1, meta);
                return true;
            }
            if (block == Blocks.CROPS_WHEAT) {
                world.setBlockData((TilePosc)new TilePos(x, y, z), 7);
                return true;
            }
            if (block == Blocks.FIRE) {
                world.setBlockWithNotify(x, y, z, 0);
                return true;
            }
            return false;
        }

        private static void bonemealGrow(World world, int x, int y, int z) {
            TilePos pos = new TilePos(x, y, z);
            ((BlockLogicSaplingBase)Blocks.SAPLING_OAK.getLogic()).growTree(world, (TilePosc)pos, world.rand);
        }

        public void createLeaves(World world, int x, int y, int z, int meta) {
            if (world.getBlock(x, y, z) == Blocks.AIR) {
                world.setBlockAndMetadataWithNotify(x, y, z, Blocks.LEAVES_OAK.id(), meta);
            }
        }

        public boolean spreadGrass(World world, int x, int y, int z) {
            if (world.rand.nextBoolean()) {
                return false;
            }
            int yy = TileEntityTerraformer.getFirstBlockFrom(world, x, z, y + 16);
            if (yy == -1) {
                return false;
            }
            Block block = world.getBlock(x, yy, z);
            if (block == Blocks.GRASS) {
                return true;
            }
            if (block == Blocks.DIRT && world.getBlock(x, yy + 1, z) == Blocks.AIR) {
                world.setBlockWithNotify(x, yy, z, Blocks.GRASS.id());
                return true;
            }
            return false;
        }
    }

    public static class Chilling
    implements ITerraformingBP {
        @Override
        public int getConsume() {
            return 2000;
        }

        @Override
        public int getRange() {
            return 50;
        }

        @Override
        public boolean terraform(World world, int x, int z, int yCoord) {
            int y = TileEntityTerraformer.getFirstBlockFrom(world, x, z, yCoord + 10);
            if (y == -1) {
                return false;
            }
            Block block = world.getBlock(x, y, z);
            if (block == Blocks.FLUID_WATER_STILL || block == Blocks.FLUID_WATER_FLOWING) {
                world.setBlockWithNotify(x, y, z, Blocks.ICE.id());
                return true;
            }
            if (block == Blocks.ICE && Chilling.isWater(world, x, y - 1, z)) {
                world.setBlockWithNotify(x, y - 1, z, Blocks.ICE.id());
                return true;
            }
            if (block == Blocks.LAYER_SNOW && this.isSurroundedBySnow(world, x, y, z)) {
                world.setBlockWithNotify(x, y, z, Blocks.BLOCK_SNOW.id());
                return true;
            }
            if (block == Blocks.ICE) {
                if (world.getBlock(x, y + 1, z) == Blocks.AIR) {
                    world.setBlockWithNotify(x, y + 1, z, Blocks.LAYER_SNOW.id());
                }
                return false;
            }
            if (block != null && block.getMaterial().isSolid() && world.getBlock(x, y + 1, z) == Blocks.AIR) {
                world.setBlockWithNotify(x, y + 1, z, Blocks.LAYER_SNOW.id());
            }
            return false;
        }

        private static boolean isWater(World world, int x, int y, int z) {
            Block block = world.getBlock(x, y, z);
            return block == Blocks.FLUID_WATER_STILL || block == Blocks.FLUID_WATER_FLOWING;
        }

        public boolean isSurroundedBySnow(World world, int x, int y, int z) {
            return this.isSnowHere(world, x + 1, y, z) && this.isSnowHere(world, x - 1, y, z) && this.isSnowHere(world, x, y, z + 1) && this.isSnowHere(world, x, y, z - 1);
        }

        public boolean isSnowHere(World world, int x, int y, int z) {
            int yy = TileEntityTerraformer.getFirstBlockFrom(world, x, z, y + 16);
            if (yy == -1 || yy < y) {
                return false;
            }
            Block block = world.getBlock(x, yy, z);
            if (block == Blocks.LAYER_SNOW || block == Blocks.BLOCK_SNOW) {
                return true;
            }
            if (block == Blocks.ICE && world.getBlock(x, yy + 1, z) == Blocks.AIR) {
                world.setBlockWithNotify(x, yy + 1, z, Blocks.LAYER_SNOW.id());
            }
            return false;
        }
    }

    public static class Desertification
    implements ITerraformingBP {
        @Override
        public int getConsume() {
            return 2500;
        }

        @Override
        public int getRange() {
            return 40;
        }

        @Override
        public boolean terraform(World world, int x, int z, int yCoord) {
            int y = TileEntityTerraformer.getFirstBlockFrom(world, x, z, yCoord + 10);
            if (y == -1) {
                return false;
            }
            if (TileEntityTerraformer.switchGround(world, Blocks.DIRT, Blocks.SAND, x, y, z, false) || TileEntityTerraformer.switchGround(world, Blocks.GRASS, Blocks.SAND, x, y, z, false) || TileEntityTerraformer.switchGround(world, Blocks.FARMLAND_DIRT, Blocks.SAND, x, y, z, false)) {
                return true;
            }
            Block block = world.getBlock(x, y, z);
            if (block == Blocks.FLUID_WATER_STILL || block == Blocks.FLUID_WATER_FLOWING || block == Blocks.LAYER_SNOW || block == Blocks.LEAVES_OAK || block == IC2Blocks.rubberLeaves || ItemTFBP.isPlant(block.id())) {
                world.setBlockWithNotify(x, y, z, 0);
                return true;
            }
            if (block == Blocks.ICE || block == Blocks.BLOCK_SNOW) {
                world.setBlockWithNotify(x, y, z, Blocks.FLUID_WATER_STILL.id());
                return true;
            }
            if ((block == Blocks.PLANKS_OAK || block == Blocks.LOG_OAK || block == IC2Blocks.rubberWood) && world.rand.nextInt(15) == 0) {
                world.setBlockWithNotify(x, y, z, Blocks.FIRE.id());
                return true;
            }
            return false;
        }
    }

    public static class Flatification
    implements ITerraformingBP {
        @Override
        public int getConsume() {
            return 2500;
        }

        @Override
        public int getRange() {
            return 40;
        }

        @Override
        public boolean terraform(World world, int x, int z, int yCoord) {
            int y = TileEntityTerraformer.getFirstBlockFrom(world, x, z, yCoord + 10);
            if (y == -1) {
                return false;
            }
            Block block = world.getBlock(x, y, z);
            if (block == Blocks.DIRT || block == Blocks.GRASS || block == Blocks.STONE) {
                return TileEntityTerraformer.switchGround(world, block, Blocks.DIRT, x, y, z, false);
            }
            world.setBlockWithNotify(x, y, z, 0);
            return true;
        }
    }
}

