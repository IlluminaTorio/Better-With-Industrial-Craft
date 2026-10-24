package ic2.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import ic2.IC2Config;
import ic2.IC2Items;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.pos.TilePosc;

public class TileEntityPlasmafier
extends TileEntityElecMachine {
    public static final int maxInput = 4096;
    public static final int maxPlasma = 10000;
    public static final int plasmaPerCell = 1000;
    public int potential = 0;
    public int plasma = 0;

    public TileEntityPlasmafier() {
        super(3, 0, 512000, maxInput);
    }

    public static int plasmaPerUu() {
        return Math.round(1000.0f / (float)IC2Config.plasmafierUuPerPlasma());
    }

    public int gaugePlasmaScaled(int i) {
        return this.plasma * i / maxPlasma;
    }

    public boolean running() {
        return this.hasEnergy() && this.potential > 0 && this.plasma < maxPlasma;
    }

    public boolean hasEnergy() {
        return this.energy >= IC2Config.plasmafierEuPerTick();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.worldObj.isClientSide) {
            return;
        }
        boolean wasRunning = this.active;
        boolean needsInvUpdate = this.consumeUu();
        if (this.hasEnergy() && this.potential > 0 && this.plasma < maxPlasma) {
            this.energy -= IC2Config.plasmafierEuPerTick();
            --this.potential;
            ++this.plasma;
            needsInvUpdate = true;
        }
        if (this.fillCell()) {
            needsInvUpdate = true;
        }
        if (this.active != this.running()) {
            this.active = this.running();
            this.worldObj.notifyBlockChange((TilePosc)this.tilePos, this.getBlock());
            needsInvUpdate = true;
        }
        if (needsInvUpdate) {
            this.setChanged();
        }
    }

    public boolean consumeUu() {
        if (!this.hasEnergy()) {
            return false;
        }
        if (this.inventory[0] != null && this.inventory[0].getItem() == IC2Items.uuMatter && this.potential < 5) {
            --this.inventory[0].stackSize;
            if (this.inventory[0].stackSize <= 0) {
                this.inventory[0] = null;
            }
            this.potential += TileEntityPlasmafier.plasmaPerUu();
            return true;
        }
        return false;
    }

    public boolean fillCell() {
        if (this.plasma < plasmaPerCell || this.inventory[1] == null || this.inventory[1].getItem() != IC2Items.cellEmpty) {
            return false;
        }
        if (this.inventory[2] != null && (this.inventory[2].getItem() != IC2Items.plasmaCell || this.inventory[2].stackSize >= this.inventory[2].getMaxStackSize())) {
            return false;
        }
        this.plasma -= plasmaPerCell;
        --this.inventory[1].stackSize;
        if (this.inventory[1].stackSize <= 0) {
            this.inventory[1] = null;
        }
        if (this.inventory[2] == null) {
            this.inventory[2] = new ItemStack(IC2Items.plasmaCell);
        } else {
            ++this.inventory[2].stackSize;
        }
        return true;
    }

    @Override
    public String getMachineName() {
        return "Plasmafier";
    }

    @Override
    public boolean explodesOnOverload() {
        return true;
    }

    @Override
    public void readAdditionalData(CompoundTag tag) {
        super.readAdditionalData(tag);
        this.potential = tag.getInteger("potential");
        this.plasma = tag.getInteger("plasma");
    }

    @Override
    public void writeAdditionalData(CompoundTag tag) {
        super.writeAdditionalData(tag);
        tag.putInt("potential", this.potential);
        tag.putInt("plasma", this.plasma);
    }
}
