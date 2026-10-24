

package ic2.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import ic2.energy.EnergyNet;
import net.minecraft.core.block.entity.TileEntity;

public class TileEntityIC2Block
extends TileEntity {
    public boolean addedToEnergyNet = false;
    public boolean active = false;

    public void tick() {
        if (!this.addedToEnergyNet) {
            EnergyNet.getForWorld(this.worldObj).addTileEntity(this);
            this.addedToEnergyNet = true;
        }
    }

    public void invalidate() {
        super.invalidate();
        if (this.addedToEnergyNet && this.worldObj != null) {
            EnergyNet net = EnergyNet.getForWorld(this.worldObj);
            if (net != null) {
                net.removeTileEntity(this);
            }
            this.addedToEnergyNet = false;
        }
    }

    public void readAdditionalData(CompoundTag tag) {
    }

    public void writeAdditionalData(CompoundTag tag) {
    }
}

