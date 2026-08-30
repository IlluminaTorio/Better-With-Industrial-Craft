

package ic2.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import ic2.IC2Blocks;
import ic2.IC2Config;
import ic2.IC2Items;
import ic2.energy.Direction;
import ic2.tileentity.TileEntityElecMachine;
import ic2.util.ExplosionIC2;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.pos.TilePosc;

public class TileEntityMatter
extends TileEntityElecMachine {
    public static final int ENERGY_PER_MATTER = 1000000;
    public int scrap = 0;

    public TileEntityMatter() {
        super(2, 0, 1100000, 512);
    }

    @Override
    public void readAdditionalData(CompoundTag tag) {
        super.readAdditionalData(tag);
        this.scrap = tag.getInteger("scrap");
    }

    @Override
    public void writeAdditionalData(CompoundTag tag) {
        super.writeAdditionalData(tag);
        tag.putInt("scrap", this.scrap);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.worldObj.isClientSide) {
            return;
        }
        boolean isActive = this.energy > 0;
        boolean needsInvUpdate = false;
        if (this.scrap < 1000 && this.inventory[0] != null && this.inventory[0].getItem() == IC2Items.scrap) {
            --this.inventory[0].stackSize;
            if (this.inventory[0].stackSize <= 0) {
                this.inventory[0] = null;
            }
            this.scrap += 5000;
            needsInvUpdate = true;
        }
        if (this.energy >= 1000000) {
            needsInvUpdate = this.attemptGeneration();
        }
        if (this.active != isActive) {
            this.active = isActive;
            this.worldObj.notifyBlockChange((TilePosc)this.tilePos, this.getBlock());
            needsInvUpdate = true;
        }
        if (needsInvUpdate) {
            this.setChanged();
        }
    }

    public boolean attemptGeneration() {
        if (this.inventory[1] == null) {
            this.inventory[1] = new ItemStack(IC2Items.uuMatter);
            this.energy -= 1000000;
            return true;
        }
        if (this.inventory[1].getItem() != IC2Items.uuMatter || this.inventory[1].stackSize + 1 > this.inventory[1].getMaxStackSize()) {
            return false;
        }
        this.energy -= 1000000;
        ++this.inventory[1].stackSize;
        return true;
    }

    @Override
    public int injectEnergy(Direction directionFrom, int amount) {
        if (amount > 512) {
            if (!IC2Config.machineExplosions()) {
                IC2Blocks.safeRemoveMachine(this.worldObj, this.tilePos.x(), this.tilePos.y(), this.tilePos.z());
                return 0;
            }
            this.worldObj.setBlockWithNotify(this.tilePos.x(), this.tilePos.y(), this.tilePos.z(), 0);
            ExplosionIC2 explosion = new ExplosionIC2(this.worldObj, null, this.tilePos.x(), this.tilePos.y(), this.tilePos.z(), 60.0f, 0.01f, 1.5f);
            explosion.explode();
            return 0;
        }
        int bonus = amount;
        if (bonus > this.scrap) {
            bonus = this.scrap;
        }
        this.scrap -= bonus;
        this.energy += amount + 5 * bonus;
        int re = 0;
        if (this.energy > this.maxEnergy) {
            re = this.energy - this.maxEnergy;
            this.energy = this.maxEnergy;
        }
        return re;
    }

    public String getProgressAsString() {
        int p = this.energy / 10000;
        if (p > 100) {
            p = 100;
        }
        return p + "%";
    }

    public int gaugeEnergyScaled(int i) {
        return (int)((long)this.energy * (long)i / 1000000L);
    }

    @Override
    public String getMachineName() {
        return "Mass Fabricator";
    }
}

