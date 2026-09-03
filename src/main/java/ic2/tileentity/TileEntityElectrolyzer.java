

package ic2.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import ic2.IC2Items;
import ic2.tileentity.TileEntityElectricBlock;
import ic2.tileentity.TileEntityIC2Machine;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;

public class TileEntityElectrolyzer
extends TileEntityIC2Machine {
    public TileEntityElectricBlock mfe;
    public int ticker = 0;
    public int energy = 0;

    public TileEntityElectrolyzer() {
        super(2);
    }

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
        super.tick();
        if (this.worldObj.isClientSide) {
            return;
        }
        boolean needsInvUpdate = false;
        boolean turnActive = false;
        if (this.ticker++ % 16 == 0) {
            this.mfe = this.lookForMFE();
        }
        if (this.mfe == null) {
            return;
        }
        if (this.shouldDrain() && this.canDrain()) {
            needsInvUpdate = this.drain();
            turnActive = true;
        }
        if (this.shouldPower() && (this.canPower() || this.energy > 0)) {
            needsInvUpdate = this.power();
            turnActive = true;
        }
        if (this.active != turnActive) {
            this.active = turnActive;
            this.worldObj.notifyBlockChange((TilePosc)this.tilePos, this.getBlock());
            needsInvUpdate = true;
        }
        if (needsInvUpdate) {
            this.setChanged();
        }
    }

    public boolean shouldDrain() {
        return this.mfe != null && (double)this.mfe.energy / (double)this.mfe.maxStorage >= 0.7;
    }

    public boolean shouldPower() {
        return this.mfe != null && (double)this.mfe.energy / (double)this.mfe.maxStorage <= 0.3;
    }

    public boolean canDrain() {
        if (this.inventory[0] == null) {
            return false;
        }
        boolean magnet = this.inventory[0].getItem() == IC2Items.deadMagnet;
        boolean waterCell = this.inventory[0].getItem() == IC2Items.cellWater;
        if (!magnet && !waterCell) {
            return false;
        }
        if (this.inventory[1] == null) {
            return true;
        }
        if (magnet) {
            return this.inventory[1].getItem() == IC2Items.magnet && this.inventory[1].stackSize < this.inventory[1].getMaxStackSize();
        }
        return this.inventory[1].getItem() == IC2Items.cellElectrolyzedWater && this.inventory[1].stackSize < this.inventory[1].getMaxStackSize();
    }

    public boolean canPower() {
        return (this.inventory[0] == null || this.inventory[0].getItem() == IC2Items.cellWater && this.inventory[0].stackSize < this.inventory[0].getMaxStackSize()) && this.inventory[1] != null && this.inventory[1].getItem() == IC2Items.cellElectrolyzedWater;
    }

    public boolean drain() {
        this.mfe.energy -= TileEntityElectrolyzer.processRate();
        this.energy += TileEntityElectrolyzer.processRate();
        if (this.energy >= 15000) {
            this.energy -= 15000;
            boolean magnet = this.inventory[0].getItem() == IC2Items.deadMagnet;
            --this.inventory[0].stackSize;
            if (this.inventory[0].stackSize <= 0) {
                this.inventory[0] = null;
            }
            if (this.inventory[1] == null) {
                this.inventory[1] = magnet ? new ItemStack(IC2Items.magnet) : new ItemStack(IC2Items.cellElectrolyzedWater);
            } else {
                ++this.inventory[1].stackSize;
            }
            return true;
        }
        return false;
    }

    public boolean power() {
        if (this.energy > 0) {
            int out = TileEntityElectrolyzer.processRate();
            if (out > this.energy) {
                out = this.energy;
            }
            this.energy -= out;
            this.mfe.energy += out;
            return false;
        }
        this.energy += 13500;
        --this.inventory[1].stackSize;
        if (this.inventory[1].stackSize <= 0) {
            this.inventory[1] = null;
        }
        if (this.inventory[0] == null) {
            this.inventory[0] = new ItemStack(IC2Items.cellWater);
        } else {
            ++this.inventory[0].stackSize;
        }
        return true;
    }

    public static int processRate() {
        return 10;
    }

    public TileEntityElectricBlock lookForMFE() {
        int z;
        int y;
        int x = this.tilePos.x();
        TileEntityElectricBlock found = this.findAt(x, (y = this.tilePos.y()) - 1, z = this.tilePos.z());
        if (found != null) {
            return found;
        }
        found = this.findAt(x, y + 1, z);
        if (found != null) {
            return found;
        }
        found = this.findAt(x - 1, y, z);
        if (found != null) {
            return found;
        }
        found = this.findAt(x + 1, y, z);
        if (found != null) {
            return found;
        }
        found = this.findAt(x, y, z - 1);
        if (found != null) {
            return found;
        }
        return this.findAt(x, y, z + 1);
    }

    private TileEntityElectricBlock findAt(int x, int y, int z) {
        TileEntity tileEntity = this.worldObj.getTileEntity((TilePosc)new TilePos(x, y, z));
        if (tileEntity instanceof TileEntityElectricBlock) {
            TileEntityElectricBlock block = (TileEntityElectricBlock)tileEntity;
            if (block.tier > 1) {
                return block;
            }
        }
        return null;
    }

    public int gaugeEnergyScaled(int i) {
        return this.energy * i / 10000;
    }

    @Override
    public String getMachineName() {
        return "Electrolyzer";
    }
}

