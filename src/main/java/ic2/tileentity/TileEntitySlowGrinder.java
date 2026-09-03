package ic2.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import ic2.IC2Config;
import ic2.IC2Items;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.pos.TilePosc;

public class TileEntitySlowGrinder
extends TileEntityElecMachine
implements ic2.net.MachineEventMessage.MachineEventHandler {
    public static final int maxInput = 128;
    public int progress = 0;
    public int speed = 1;

    public TileEntitySlowGrinder() {
        super(15, 1, 1000, maxInput);
    }

    public float getChargeLevel() {
        float f = (float)this.energy / (float)(this.maxEnergy - maxInput + 1);
        if (f > 1.0f) {
            f = 1.0f;
        }
        return f;
    }

    public int gaugeProgressScaled(int i) {
        int r = (int)((float)(this.progress * i) / (100.0f * IC2Config.slowGrinderSpeedMultiplier()));
        if (r > i) {
            r = i;
        }
        return r;
    }

    public boolean isActive() {
        return this.progress > 0;
    }

    public void toggleSpeed() {
        ++this.speed;
        if (this.speed > 5) {
            this.speed = 1;
        }
    }

    public void handleEvent(int event) {
        if (event == 0) {
            this.toggleSpeed();
            this.setChanged();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.worldObj.isClientSide) {
            return;
        }
        boolean wasActive = this.isActive();
        boolean needsInvUpdate = this.provideEnergy();
        this.compactSlots();
        if (this.canRun()) {
            this.energy -= (int)((float)(this.speed * 10) * IC2Config.slowGrinderEuMultiplier());
            this.progress += this.speed;
            if ((float)this.progress > 100.0f * IC2Config.slowGrinderSpeedMultiplier()) {
                this.progress = 1;
                if (this.inventory[0] == null) {
                    this.giveToMassFabs();
                    return;
                }
                --this.inventory[0].stackSize;
                if (this.inventory[0].stackSize <= 0) {
                    this.inventory[0] = null;
                }
                if (this.worldObj.rand.nextFloat() < this.recycleChance() * IC2Config.slowGrinderRecycleChance()) {
                    if (this.inventory[2] == null) {
                        this.inventory[2] = new ItemStack(IC2Items.scrap);
                        needsInvUpdate = true;
                    } else if (this.inventory[2].getItem() == IC2Items.scrap && this.inventory[2].stackSize < this.inventory[2].getMaxStackSize()) {
                        ++this.inventory[2].stackSize;
                        needsInvUpdate = true;
                    }
                }
            }
        } else {
            this.progress = 0;
        }
        this.giveToMassFabs();
        if (this.active != this.isActive()) {
            this.active = this.isActive();
            this.worldObj.notifyBlockChange((TilePosc)this.tilePos, this.getBlock());
            needsInvUpdate = true;
        }
        if (needsInvUpdate) {
            this.setChanged();
        }
    }

    public float recycleChance() {
        return switch (this.speed) {
            case 1 -> 1.0f;
            case 2 -> 0.84f;
            case 3 -> 0.71f;
            case 4 -> 0.59f;
            case 5 -> 0.5f;
            default -> 1.0f;
        };
    }

    public boolean canRun() {
        return this.hasEnergy() && this.fixOutput() && this.hasOrGetInput();
    }

    public boolean hasEnergy() {
        return this.energy >= (int)((float)(this.speed * 10) * IC2Config.slowGrinderEuMultiplier());
    }

    public void compactSlots() {
        block0: for (int i = 3; i < 11; ++i) {
            ItemStack item = this.inventory[i];
            if (item == null) continue;
            for (int j = 3; j < i; ++j) {
                int amount;
                ItemStack item2 = this.inventory[j];
                if (item2 == null) {
                    this.inventory[j] = this.inventory[i];
                    this.inventory[i] = null;
                    continue block0;
                }
                if (item.getItem() != item2.getItem() || (amount = Math.min(item.stackSize, item2.getMaxStackSize() - item2.stackSize)) <= 0) continue;
                item2.stackSize += amount;
                item.stackSize -= amount;
                if (item.stackSize > 0) continue;
                this.inventory[i] = null;
                continue block0;
            }
        }
    }

    public boolean fixOutput() {
        if (this.inventory[2] != null) {
            if (this.inventory[2].getItem() == IC2Items.scrap) {
                if (this.inventory[2].stackSize == this.inventory[2].getMaxStackSize()) {
                    this.clearOutput();
                }
            } else {
                this.clearOutput();
            }
            if (this.inventory[2] == null) {
                return true;
            }
            return this.inventory[2].getItem() == IC2Items.scrap && this.inventory[2].stackSize < this.inventory[2].getMaxStackSize();
        }
        return true;
    }

    public ItemStack clearOutput() {
        ItemStack item = this.inventory[2];
        for (int i = 0; i < 4; ++i) {
            if (item == null) continue;
            ItemStack item2 = this.inventory[11 + i];
            if (item2 == null) {
                item2 = item;
                item = null;
            } else if (item2.getItem() == item.getItem() && item2.stackSize < item2.getMaxStackSize()) {
                int amount = item2.getMaxStackSize() - item2.stackSize;
                amount = Math.min(amount, item.stackSize);
                item2.stackSize += amount;
                item.stackSize -= amount;
                if (item.stackSize == 0) {
                    item = null;
                }
            }
            this.inventory[11 + i] = item2;
        }
        this.inventory[2] = item;
        return item;
    }

    public void giveToMassFabs() {
        this.giveInSlotToMassFabs(2);
        for (int i = 0; i < 4; ++i) {
            this.giveInSlotToMassFabs(11 + i);
        }
    }

    private void giveInSlotToMassFabs(int slot) {
        ItemStack item = this.inventory[slot];
        if (item == null || item.getItem() != IC2Items.scrap) {
            return;
        }
        int x = this.tilePos.x();
        int y = this.tilePos.y();
        int z = this.tilePos.z();
        item = this.giveToMassFab(item, x + 1, y, z);
        item = this.giveToMassFab(item, x - 1, y, z);
        item = this.giveToMassFab(item, x, y, z + 1);
        item = this.giveToMassFab(item, x, y, z - 1);
        item = this.giveToMassFab(item, x, y - 1, z);
        this.inventory[slot] = item = this.giveToMassFab(item, x, y + 1, z);
    }

    public ItemStack giveToMassFab(ItemStack item, int x, int y, int z) {
        TileEntity te = this.worldObj.getTileEntity(x, y, z);
        if (!(te instanceof TileEntityMatter)) {
            return item;
        }
        TileEntityMatter tile = (TileEntityMatter)te;
        if (tile.inventory[0] == null) {
            tile.inventory[0] = item;
            tile.setChanged();
            return null;
        }
        if (tile.inventory[0].getItem() == IC2Items.scrap && tile.inventory[0].stackSize < tile.inventory[0].getMaxStackSize()) {
            int amount = tile.inventory[0].getMaxStackSize() - tile.inventory[0].stackSize;
            amount = Math.min(amount, item.stackSize);
            tile.inventory[0].stackSize += amount;
            item.stackSize -= amount;
            tile.setChanged();
            if (item.stackSize == 0) {
                return null;
            }
            return item;
        }
        return item;
    }

    public boolean hasOrGetInput() {
        if (this.inventory[0] != null) {
            return true;
        }
        for (int i = 0; i < 8; ++i) {
            ItemStack item = this.inventory[3 + i];
            if (item == null) continue;
            this.inventory[0] = item;
            this.inventory[3 + i] = null;
            return true;
        }
        return false;
    }

    @Override
    public String getMachineName() {
        return "Slow Grinder";
    }

    @Override
    public void readAdditionalData(CompoundTag tag) {
        super.readAdditionalData(tag);
        this.progress = tag.getShort("progress");
        this.speed = Math.max(1, Math.min(5, tag.getInteger("speed")));
    }

    @Override
    public void writeAdditionalData(CompoundTag tag) {
        super.writeAdditionalData(tag);
        tag.putShort("progress", (short)this.progress);
        tag.putInt("speed", this.speed);
    }

    @Override
    public int[] getQuickGrabSlots() {
        return new int[]{2, 11, 12, 13, 14};
    }
}
