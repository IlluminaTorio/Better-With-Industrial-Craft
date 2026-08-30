

package ic2.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import ic2.IC2Blocks;
import ic2.IC2Config;
import ic2.energy.Direction;
import ic2.energy.IEnergySink;
import ic2.tileentity.TileEntityIC2Machine;
import ic2.util.ExplosionIC2;
import java.util.List;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.world.pos.TilePosc;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;

public class TileEntityLuminator
extends TileEntityIC2Machine
implements IEnergySink {
    public int energy = 0;
    public int mode = 0;
    public boolean powered = false;
    public int ticker = 0;

    public TileEntityLuminator() {
        super(0);
    }

    @Override
    public void readAdditionalData(CompoundTag tag) {
        super.readAdditionalData(tag);
        this.energy = tag.getInteger("energy");
        this.mode = tag.getInteger("mode");
        this.powered = tag.getBoolean("powered");
    }

    @Override
    public void writeAdditionalData(CompoundTag tag) {
        super.writeAdditionalData(tag);
        tag.putInt("energy", this.energy);
        tag.putInt("mode", this.mode);
        tag.putBoolean("powered", this.powered);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.worldObj.isClientSide) {
            return;
        }
        ++this.ticker;
        if (this.ticker % 20 != 0) {
            return;
        }
        if (this.ticker % 160 == 0) {
            int consume = 5;
            if (this.mode == 1) {
                consume = 10;
            } else if (this.mode == 2) {
                consume = 40;
            }
            if (consume > this.energy) {
                this.energy = 0;
                this.powered = false;
            } else {
                this.energy -= consume;
                this.powered = true;
            }
            this.updateLighting();
        }
        if (this.powered) {
            this.burnMobs();
        }
    }

    public void switchStrength() {
        this.mode = (this.mode + 1) % 3;
        this.updateLighting();
        this.setChanged();
    }

    public void updateLighting() {
        this.worldObj.markBlockNeedsUpdate((TilePosc)this.tilePos);
        this.worldObj.notifyBlockChange((TilePosc)this.tilePos, this.getBlock());
    }

    @Override
    public boolean demandsEnergy() {
        return this.energy < this.getMaxEnergy();
    }

    @Override
    public int injectEnergy(Direction directionFrom, int amount) {
        if (amount > 32) {
            this.poof();
            return 0;
        }
        this.energy += amount;
        int re = 0;
        if (this.energy > this.getMaxEnergy()) {
            re = this.energy - this.getMaxEnergy();
            this.energy = this.getMaxEnergy();
        }
        return re;
    }

    public int getMaxEnergy() {
        if (this.mode == 1) {
            return 20;
        }
        if (this.mode == 2) {
            return 80;
        }
        return 10;
    }

    public void poof() {
        this.worldObj.setBlockWithNotify(this.tilePos.x(), this.tilePos.y(), this.tilePos.z(), 0);
        if (IC2Config.machineExplosions()) {
            ExplosionIC2 explosion = new ExplosionIC2(this.worldObj, null, 0.5 + (double)this.tilePos.x(), 0.5 + (double)this.tilePos.y(), 0.5 + (double)this.tilePos.z(), 0.5f, 0.85f, 2.0f);
            explosion.explode();
        }
    }

    public void burnMobs() {
        int x = this.tilePos.x();
        int y = this.tilePos.y();
        int z = this.tilePos.z();
        boolean xplus = this.isTransparent(x + 1, y, z);
        boolean xminus = this.isTransparent(x - 1, y, z);
        boolean yplus = this.isTransparent(x, y + 1, z);
        boolean yminus = this.isTransparent(x, y - 1, z);
        boolean zplus = this.isTransparent(x, y, z + 1);
        boolean zminus = this.isTransparent(x, y, z - 1);
        int xplusI = xplus ? 3 : (yplus || yminus || zplus || zminus ? 1 : 0);
        int xminusI = xminus ? 3 : (yplus || yminus || zplus || zminus ? 1 : 0);
        int yplusI = yplus ? 3 : (xplus || xminus || zplus || zminus ? 1 : 0);
        int yminusI = yminus ? 3 : (xplus || xminus || zplus || zminus ? 1 : 0);
        int zplusI = zplus ? 3 : (yplus || yminus || xplus || xminus ? 1 : 0);
        int zminusI = zminus ? 3 : (yplus || yminus || xplus || xminus ? 1 : 0);
        int minX = x - xminusI;
        int maxX = x + xplusI + 1;
        int minY = y - yminusI;
        int maxY = y + yplusI + 2;
        int minZ = z - zminusI;
        int maxZ = z + zplusI + 1;
        AABBd box = new AABBd((double)minX, (double)minY, (double)minZ, (double)maxX, (double)maxY, (double)maxZ);
        List list = this.worldObj.getEntitiesWithinAABB(Mob.class, (AABBdc)box);
        for (Object entObj : list) {
            Mob ent = (Mob)entObj;
            int fire;
            if (!ent.isAlive() || (fire = ent.remainingFireTicks) >= 100) continue;
            if ((fire += 30) >= 100) {
                fire = 100;
            }
            ent.remainingFireTicks = fire;
        }
    }

    private boolean isTransparent(int x, int y, int z) {
        Block block = this.worldObj.getBlock(x, y, z);
        return block == null || block == Blocks.AIR || block == Blocks.GLASS || block == IC2Blocks.reinforcedGlass;
    }

    @Override
    public boolean acceptsEnergyFrom(TileEntity emitter, Direction direction) {
        return true;
    }

    @Override
    public String getMachineName() {
        return "Luminator";
    }
}

