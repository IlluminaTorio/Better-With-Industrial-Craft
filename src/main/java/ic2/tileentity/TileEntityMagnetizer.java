

package ic2.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import ic2.IC2Blocks;
import ic2.energy.Direction;
import ic2.energy.IEnergySink;
import ic2.tileentity.TileEntityIC2Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;

public class TileEntityMagnetizer
extends TileEntityIC2Block
implements IEnergySink {
    public int energy = 0;
    public int ticker = 0;
    public int maxEnergy = 100;

    @Override
    public void readAdditionalData(CompoundTag tag) {
        super.readAdditionalData(tag);
        this.energy = tag.getInteger("energy");
    }

    @Override
    public void writeAdditionalData(CompoundTag tag) {
        super.writeAdditionalData(tag);
        tag.putInt("energy", this.energy);
    }

    @Override
    public void tick() {
        int need;
        int y;
        super.tick();
        if (this.worldObj.isClientSide) {
            return;
        }
        if (this.ticker-- > 0) {
            return;
        }
        boolean change = false;
        int x = this.tilePos.x();
        int z = this.tilePos.z();
        for (y = this.tilePos.y() - 1; y > 0 && y >= this.tilePos.y() - 20 && this.energy > 0 && this.worldObj.getBlock(x, y, z) == IC2Blocks.ironFence; --y) {
            need = 15 - this.worldObj.getBlockData((TilePosc)new TilePos(x, y, z));
            if (need <= 0) continue;
            change = true;
            if (need > this.energy) {
                this.energy = need;
            }
            this.chargeFence(x, y, z, need);
            this.energy -= need;
        }

        int worldHeight = this.worldObj.getHeightBlocks();
        for (y = this.tilePos.y() + 1; y < worldHeight && y <= this.tilePos.y() + 20 && this.energy > 0 && this.worldObj.getBlock(x, y, z) == IC2Blocks.ironFence; ++y) {
            need = 15 - this.worldObj.getBlockData((TilePosc)new TilePos(x, y, z));
            if (need <= 0) continue;
            change = true;
            if (need > this.energy) {
                this.energy = need;
            }
            this.chargeFence(x, y, z, need);
            this.energy -= need;
        }
        if (!change) {
            this.ticker = 10;
        }
    }

    private void chargeFence(int x, int y, int z, int amount) {
        TilePos pos = new TilePos(x, y, z);


        this.worldObj.setBlockDataNotify((TilePosc)pos, this.worldObj.getBlockData((TilePosc)pos) + amount);
    }

    @Override
    public boolean acceptsEnergyFrom(TileEntity emitter, Direction direction) {
        return true;
    }

    @Override
    public boolean demandsEnergy() {
        return this.energy < this.maxEnergy;
    }

    @Override
    public int injectEnergy(Direction directionFrom, int amount) {
        if (amount > 32 && !ic2.IC2Config.voltageSystemOff()) {
            IC2Blocks.explodeMachineAt(this.worldObj, this.tilePos.x(), this.tilePos.y(), this.tilePos.z());
            return 0;
        }
        this.energy += amount;
        int re = 0;
        if (this.energy > this.maxEnergy) {
            re = this.energy - this.maxEnergy;
            this.energy = this.maxEnergy;
        }
        return re;
    }
}

