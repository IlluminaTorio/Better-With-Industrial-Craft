package ic2.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.pos.TilePosc;

public abstract class TileEntityAdvMachine
extends TileEntityElecMachine {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_ENERGY = 1;
    public static final int SLOT_OUTPUT = 2;
    public static final int MAX_SPEED = 10000;
    public static final int MIN_ACTIVE_SPEED = 3000;
    public static final int PROGRESS_MAX = 4000;
    public static final int SPEED_DECAY = 4;
    public int speed = 0;
    public int progress = 0;
    public int soundTicker;
    public final int spinConsume;
    public final int opConsume;
    public final int speedDivisor;
    public final int maxInput;

    public TileEntityAdvMachine(int spinConsume, int opConsume, int speedDivisor, int maxInput) {
        super(3, 1, MAX_SPEED, maxInput);
        this.spinConsume = spinConsume;
        this.opConsume = opConsume;
        this.speedDivisor = speedDivisor;
        this.maxInput = maxInput;
    }

    @Override
    public void readAdditionalData(CompoundTag tag) {
        super.readAdditionalData(tag);
        this.speed = tag.getInteger("speed");
        this.progress = tag.getInteger("progress");
    }

    @Override
    public void writeAdditionalData(CompoundTag tag) {
        super.writeAdditionalData(tag);
        tag.putInt("speed", this.speed);
        tag.putInt("progress", this.progress);
    }

    public int gaugeSpeedScaled(int i) {
        return this.speed * i / MAX_SPEED;
    }

    public int gaugeProgressScaled(int i) {
        return this.progress * i / PROGRESS_MAX;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.worldObj.isClientSide) {
            return;
        }
        boolean changed = false;
        boolean canWork = this.canOperate();
        if (this.energy <= this.spinConsume + this.opConsume && canWork) {
            changed = this.provideEnergy();
        }
        boolean spinning = this.energy > 0 && (canWork || this.worldObj.hasNeighborSignal((TilePosc)this.tilePos));
        if (spinning) {
            this.energy -= this.spinConsume;
            if (this.speed < MAX_SPEED) {
                ++this.speed;
            }
        } else if (this.speed > 0) {
            this.speed = Math.max(0, this.speed - SPEED_DECAY);
        }
        boolean operating = this.energy > this.opConsume && canWork && this.speed >= this.speedDivisor;
        if (operating) {
            this.energy -= this.opConsume;
            this.soundTicker += 1;
            if (this.soundTicker % this.getLoopingTime() == 0) {
                this.playWorkSound();
            }
            this.progress += this.speed / this.speedDivisor;
            if (this.progress >= PROGRESS_MAX) {
                this.progress = 0;
                this.operate();
                changed = true;
            }
        }
        if (this.speed == 0 && this.progress > 0) {
            this.progress = 0;
        }
        boolean active = this.speed >= MIN_ACTIVE_SPEED;
        if (this.active != active) {
            this.active = active;
            this.worldObj.notifyBlockChange((TilePosc)this.tilePos, this.getBlock());
            changed = true;
        }
        if (changed) {
            this.setChanged();
        }
    }

    public void playWorkSound() {
        this.worldObj.playSoundEffect(null, net.minecraft.core.sound.SoundCategory.WORLD_SOUNDS, (double)this.tilePos.x() + 0.5, (double)this.tilePos.y() + 0.5, (double)this.tilePos.z() + 0.5, this.getLoopSound(), 1.0f, 1.0f);
    }

    public void operate() {
        if (!this.canOperate()) {
            return;
        }
        ItemStack itemstack = this.getResultFor(this.inventory[SLOT_INPUT]);
        if (itemstack == null) {
            return;
        }
        if (this.inventory[SLOT_OUTPUT] == null) {
            this.inventory[SLOT_OUTPUT] = itemstack.copy();
        } else {
            this.inventory[SLOT_OUTPUT].stackSize += itemstack.stackSize;
        }
        if (this.inventory[SLOT_INPUT].getItem().hasContainerItem()) {
            this.inventory[SLOT_INPUT] = new ItemStack(this.inventory[SLOT_INPUT].getItem().getContainerItem());
        } else {
            --this.inventory[SLOT_INPUT].stackSize;
            if (this.inventory[SLOT_INPUT].stackSize <= 0) {
                this.inventory[SLOT_INPUT] = null;
            }
        }
    }

    public boolean canOperate() {
        if (this.inventory[SLOT_INPUT] == null) {
            return false;
        }
        ItemStack itemstack = this.getResultFor(this.inventory[SLOT_INPUT]);
        if (itemstack == null) {
            return false;
        }
        if (this.inventory[SLOT_OUTPUT] == null) {
            return true;
        }
        if (!this.inventory[SLOT_OUTPUT].isItemEqual(itemstack)) {
            return false;
        }
        return this.inventory[SLOT_OUTPUT].stackSize + itemstack.stackSize <= this.inventory[SLOT_OUTPUT].getMaxStackSize();
    }

    public abstract ItemStack getResultFor(ItemStack var1);

    public abstract String getLoopSound();

    public abstract int getLoopingTime();

    public abstract String getGuiTexture();

    public abstract String getGuiTitleKey();

    public abstract String getSpeedUnitKey();

    @Override
    public abstract String getMachineName();
}
