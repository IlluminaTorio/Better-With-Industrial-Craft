package ic2.tileentity;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.pos.TilePos;

public class TileEntitySprinkler
extends TileEntityElecMachine {
    public static final int RADIUS = 2;
    public static final int UPKEEP = 2;
    public static final int BOOST_COST = 5;
    public static final int INTERVAL = 20;

    public TileEntitySprinkler() {
        super(3, 0, 5000, 32);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.worldObj.isClientSide) {
            return;
        }
        boolean working = this.energy > TileEntitySprinkler.UPKEEP;
        if (working) {
            this.energy -= TileEntitySprinkler.UPKEEP;
            if (this.worldObj.getTotalWorldTime() % (long)TileEntitySprinkler.INTERVAL == 0L) {
                this.boostRandomCrop();
            }
            this.hydrateSoil();
        }
        if (this.active != working) {
            this.active = working;
            this.worldObj.notifyBlockChange(this.tilePos, this.getBlock());
            this.setChanged();
        }
    }

    private void boostRandomCrop() {
        for (int attempt = 0; attempt < 2; ++attempt) {
            int dx = this.worldObj.rand.nextInt(2 * TileEntitySprinkler.RADIUS + 1) - TileEntitySprinkler.RADIUS;
            int dz = this.worldObj.rand.nextInt(2 * TileEntitySprinkler.RADIUS + 1) - TileEntitySprinkler.RADIUS;
            int dy = -(this.worldObj.rand.nextInt(2) + 1);
            TilePos target = this.tilePos.add(dx, dy, dz, new TilePos());
            Block<?> block = this.worldObj.getBlockType(target);
            if (block != Blocks.CROPS_WHEAT && block != Blocks.CROPS_PUMPKIN) continue;
            int data = this.worldObj.getBlockData(target);
            int max = block == Blocks.CROPS_WHEAT ? 7 : 4;
            if (data >= max || this.energy < TileEntitySprinkler.BOOST_COST + TileEntitySprinkler.UPKEEP) continue;
            TilePos above = target.up(new TilePos());
            if (this.worldObj.getBlockLightValue(above) < 9) continue;
            this.worldObj.setBlockDataNotify(target, data + 1);
            this.energy -= TileEntitySprinkler.BOOST_COST;
        }
    }

    private void hydrateSoil() {
        for (int dy = -1; dy >= -2; --dy) {
            for (int dx = -TileEntitySprinkler.RADIUS; dx <= TileEntitySprinkler.RADIUS; ++dx) {
                for (int dz = -TileEntitySprinkler.RADIUS; dz <= TileEntitySprinkler.RADIUS; ++dz) {
                    TilePos target = this.tilePos.add(dx, dy, dz, new TilePos());
                    Block<?> block = this.worldObj.getBlockType(target);
                    if (block != net.minecraft.core.block.Blocks.FARMLAND_DIRT) continue;
                    if (this.worldObj.getBlockData(target) >= 15) continue;
                    this.worldObj.setBlockMetadataWithNotify(target.x(), target.y(), target.z(), 15);
                }
            }
        }
    }

    @Override
    public String getMachineName() {
        return "Sprinkler";
    }
}
