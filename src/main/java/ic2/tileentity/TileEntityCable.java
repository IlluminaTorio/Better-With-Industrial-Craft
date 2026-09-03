

package ic2.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import ic2.energy.Direction;
import ic2.energy.EnergyNet;
import ic2.energy.IEnergyConductor;
import ic2.tileentity.TileEntityIC2Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.world.pos.TilePosc;

public class TileEntityCable
extends TileEntityIC2Block
implements IEnergyConductor {
    public short cableType = 0;
    public short color = 0;

    public TileEntityCable(short type) {
        this.cableType = type;
    }

    public TileEntityCable() {
    }

    @Override
    public void readAdditionalData(CompoundTag tag) {
        super.readAdditionalData(tag);
        this.cableType = tag.getShort("cableType");
        this.color = tag.getShort("color");
    }

    @Override
    public void writeAdditionalData(CompoundTag tag) {
        super.writeAdditionalData(tag);
        tag.putShort("cableType", this.cableType);
        tag.putShort("color", this.color);
    }

    @Override
    public boolean acceptsEnergyFrom(TileEntity emitter, Direction direction) {
        return !(emitter instanceof TileEntityCable) || this.canInteractWithCable((TileEntityCable)emitter);
    }

    @Override
    public boolean emitsEnergyTo(TileEntity receiver, Direction direction) {
        return !(receiver instanceof TileEntityCable) || this.canInteractWithCable((TileEntityCable)receiver);
    }

    public boolean canInteractWithCable(TileEntityCable cable) {
        return this.color == 0 || cable.color == 0 || this.color == cable.color;
    }

    private int cableTypeSynced() {
        if (this.worldObj != null) {
            int meta = this.getBlockMeta();
            int blockType = meta & 0xF;
            if (blockType != this.cableType) {
                this.cableType = (short)blockType;
            }
        }
        return this.cableType;
    }

    @Override
    public double getConductionLoss() {
        return switch (this.cableTypeSynced()) {
            case 0 -> 0.2;
            case 1 -> 0.3;
            case 2 -> 0.5;
            case 3 -> 0.45;
            case 4 -> 0.4;
            case 5 -> 1.0;
            case 6 -> 0.95;
            case 7 -> 0.9;
            case 8 -> 0.8;
            case 9 -> 0.025;
            case 10 -> 0.001;
            case 11 -> ic2.IC2Config.plasmaCableLoss();
            default -> 0.01;
        };
    }

    @Override
    public int getInsulationEnergyAbsorption() {
        return switch (this.cableTypeSynced()) {
            case 0 -> 32;
            case 1 -> 8;
            case 2 -> 8;
            case 3 -> 32;
            case 4 -> 128;
            case 5 -> 0;
            case 6 -> 128;
            case 7 -> 512;
            case 8 -> 9001;
            case 9 -> 9001;
            case 10 -> 0;
            case 11 -> 4096;
            default -> 0;
        };
    }

    @Override
    public int getInsulationBreakdownEnergy() {
        return 9001;
    }

    @Override
    public int getConductorBreakdownEnergy() {
        return switch (this.cableTypeSynced()) {
            case 0 -> 33;
            case 1 -> 33;
            case 2 -> 129;
            case 3 -> 129;
            case 4 -> 129;
            case 5 -> 2049;
            case 6 -> 2049;
            case 7 -> 2049;
            case 8 -> 2049;
            case 9 -> 513;
            case 10 -> 4;
            case 11 -> 4097;
            default -> 0;
        };
    }

    @Override
    public void removeInsulation() {
    }

    @Override
    public void removeConductor() {
        this.worldObj.setBlockWithNotify(this.tilePos.x(), this.tilePos.y(), this.tilePos.z(), 0);
        this.worldObj.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, (double)this.tilePos.x() + 0.5, (double)this.tilePos.y() + 0.5, (double)this.tilePos.z() + 0.5, "random.fizz", 0.5f, 2.6f + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.8f);
    }

    public boolean changeColor(int newColor) {
        if (this.color == newColor || this.cableType == 1 || this.cableType == 2 || this.cableType == 5 || this.cableType == 10) {
            return false;
        }
        EnergyNet.getForWorld(this.worldObj).removeTileEntity(this);
        this.color = (short)newColor;
        EnergyNet.getForWorld(this.worldObj).addTileEntity(this);
        this.worldObj.notifyBlockChange((TilePosc)this.tilePos, this.getBlock());
        return true;
    }
}

