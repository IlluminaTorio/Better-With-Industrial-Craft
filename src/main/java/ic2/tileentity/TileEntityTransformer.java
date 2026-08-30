

package ic2.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import ic2.IC2Blocks;
import ic2.block.BlockLogicIC2Machine;
import ic2.energy.Direction;
import ic2.energy.EnergyNet;
import ic2.energy.IEnergySink;
import ic2.energy.IEnergySource;
import ic2.tileentity.TileEntityIC2Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.world.pos.TilePosc;

public abstract class TileEntityTransformer
extends TileEntityIC2Block
implements IEnergySink,
IEnergySource {
    public int lowOutput;
    public int highOutput;
    public int maxStorage;
    public int energy = 0;
    public boolean redstone;

    public TileEntityTransformer(int low, int high, int max) {
        this.lowOutput = low;
        this.highOutput = high;
        this.maxStorage = max;
    }

    @Override
    public void readAdditionalData(CompoundTag tag) {
        super.readAdditionalData(tag);
        this.energy = tag.getInteger("energy");
        this.redstone = tag.getBoolean("redstone");
    }

    @Override
    public void writeAdditionalData(CompoundTag tag) {
        super.writeAdditionalData(tag);
        tag.putInt("energy", this.energy);
        tag.putBoolean("redstone", this.redstone);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.worldObj.isClientSide) {
            return;
        }
        this.updateRedstone();
        if (this.redstone) {
            if (this.energy >= this.highOutput) {
                int send = this.highOutput;
                this.energy -= send;
                send = EnergyNet.getForWorld(this.worldObj).emitEnergyFrom(this, send);
                this.energy += send;
            }
        } else {
            for (int i = 0; i < 4 && this.energy >= this.lowOutput; ++i) {
                int send = this.lowOutput;
                this.energy -= send;
                send = EnergyNet.getForWorld(this.worldObj).emitEnergyFrom(this, send);
                this.energy += send;
            }
        }
    }

    public void updateRedstone() {
        boolean red = this.worldObj.hasNeighborSignal((TilePosc)this.tilePos);
        if (red != this.redstone) {
            if (this.addedToEnergyNet) {
                EnergyNet.getForWorld(this.worldObj).removeTileEntity(this);
            }
            this.redstone = red;
            if (this.addedToEnergyNet) {
                EnergyNet.getForWorld(this.worldObj).addTileEntity(this);
            }
        }
    }

    protected net.minecraft.core.util.helper.Direction getFacing() {
        return BlockLogicIC2Machine.getFacing(this.worldObj, (TilePosc)this.tilePos);
    }

    protected boolean facingMatchesDirection(Direction direction) {
        net.minecraft.core.util.helper.Direction facing = this.getFacing();
        if (facing == null) {
            return false;
        }
        return switch (facing) {
            case WEST -> {
                if (direction == Direction.XN) {
                    yield true;
                }
                yield false;
            }
            case EAST -> {
                if (direction == Direction.XP) {
                    yield true;
                }
                yield false;
            }
            case DOWN -> {
                if (direction == Direction.YN) {
                    yield true;
                }
                yield false;
            }
            case UP -> {
                if (direction == Direction.YP) {
                    yield true;
                }
                yield false;
            }
            case NORTH -> {
                if (direction == Direction.ZN) {
                    yield true;
                }
                yield false;
            }
            case SOUTH -> {
                if (direction == Direction.ZP) {
                    yield true;
                }
                yield false;
            }
            default -> false;
        };
    }

    @Override
    public boolean acceptsEnergyFrom(TileEntity emitter, Direction direction) {
        if (this.redstone) {
            return !this.facingMatchesDirection(direction);
        }
        return this.facingMatchesDirection(direction);
    }

    @Override
    public boolean emitsEnergyTo(TileEntity receiver, Direction direction) {
        if (this.redstone) {
            return this.facingMatchesDirection(direction);
        }
        return !this.facingMatchesDirection(direction);
    }

    @Override
    public int getMaxEnergyOutput() {
        if (this.redstone) {
            return this.highOutput;
        }
        return this.lowOutput;
    }

    @Override
    public boolean demandsEnergy() {
        return this.energy < this.maxStorage;
    }

    @Override
    public int injectEnergy(Direction directionFrom, int amount) {
        
        if (!ic2.IC2Config.voltageSystemOff()
                && (this.redstone && amount > this.lowOutput || !this.redstone && amount > this.highOutput)) {
            IC2Blocks.explodeMachineAt(this.worldObj, this.tilePos.x(), this.tilePos.y(), this.tilePos.z());
            return 0;
        }
        int need = amount;
        if (this.energy + amount > this.maxStorage) {
            need = this.maxStorage - this.energy;
        }
        this.energy += need;
        return amount - need;
    }
}

