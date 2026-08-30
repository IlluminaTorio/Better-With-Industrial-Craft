

package ic2.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import ic2.IC2Items;
import ic2.tileentity.TileEntityElecMachine;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemFood;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.world.pos.TilePosc;

public class TileEntityCanner
extends TileEntityElecMachine {
    public int progress = 0;
    public int fuelQuality = 0;
    public int soundTicker = 0;
    public int energyConsume = 1;
    public int operationLength = 600;

    public TileEntityCanner() {
        super(4, 1, 600, 32);
    }

    @Override
    public void readAdditionalData(CompoundTag tag) {
        super.readAdditionalData(tag);
        this.progress = tag.getShort("progress");
        this.fuelQuality = tag.getInteger("fuelQuality");
    }

    @Override
    public void writeAdditionalData(CompoundTag tag) {
        super.writeAdditionalData(tag);
        tag.putShort("progress", (short)this.progress);
        tag.putInt("fuelQuality", this.fuelQuality);
    }

    public int gaugeProgressScaled(int i) {
        int food;
        int l = this.operationLength;
        if (this.getMode() == 1 && this.inventory[0] != null && (food = this.getFoodValue(this.inventory[0])) > 0) {
            l = 50 * food;
        }
        return this.progress * i / l;
    }

    public int gaugeFuelScaled(int i) {
        if (this.energy <= 0) {
            return 0;
        }
        int r = this.energy * i / (this.operationLength * this.energyConsume);
        if (r > i) {
            r = i;
        }
        return r;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.worldObj.isClientSide) {
            return;
        }
        boolean wasOperating = this.isOperating();
        boolean needsInvUpdate = false;
        if (this.energy > 0 && this.canOperate()) {
            this.energy -= this.energyConsume;
        }
        if (this.energy <= this.energyConsume * this.operationLength && this.canOperate()) {
            needsInvUpdate = this.provideEnergy();
        }
        if (wasOperating && this.canOperate()) {
            ++this.soundTicker;
            if (this.soundTicker % 64 == 0) {
                this.worldObj.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, (double)this.tilePos.x() + 0.5, (double)this.tilePos.y() + 0.5, (double)this.tilePos.z() + 0.5, "random.fizz", 1.0f, 1.0f);
            }
            if (this.getMode() == 1) {
                ++this.progress;
                if (this.progress >= this.getFoodValue(this.inventory[0]) * 50) {
                    this.progress = 0;
                    this.operate();
                    needsInvUpdate = true;
                }
            } else {
                ++this.progress;
                if (this.progress % 100 == 0) {
                    this.operate();
                    needsInvUpdate = true;
                    if (this.progress >= 600) {
                        this.progress = 0;
                    }
                }
            }
        }
        if (this.getMode() == 1 && !this.canOperate() || this.getMode() == 0) {
            this.fuelQuality = 0;
            this.progress = 0;
        }
        if (wasOperating != this.isOperating()) {
            this.active = this.isOperating();
            this.worldObj.notifyBlockChange((TilePosc)this.tilePos, this.getBlock());
            needsInvUpdate = true;
        }
        if (needsInvUpdate) {
            this.setChanged();
        }
    }

    public void operate() {
        if (this.getMode() == 1) {
            this.fuelQuality = 0;
            int food = this.getFoodValue(this.inventory[0]);
            --this.inventory[0].stackSize;
            if (this.inventory[0].stackSize <= 0) {
                this.inventory[0] = null;
            }
            this.inventory[3].stackSize -= food;
            if (this.inventory[3].stackSize <= 0) {
                this.inventory[3] = null;
            }
            if (this.inventory[2] == null) {
                this.inventory[2] = new ItemStack(IC2Items.filledTinCan, food);
            } else {
                this.inventory[2].stackSize += food;
            }
            return;
        }
        if (this.getMode() == 2) {
            int fuel = this.getFuelValue(this.inventory[0].getItem().id);
            --this.inventory[0].stackSize;
            if (this.inventory[0].stackSize <= 0) {
                this.inventory[0] = null;
            }
            this.fuelQuality += fuel;
            if (this.progress >= 600) {
                if (this.inventory[3].getItem() == IC2Items.fuelCanEmpty) {
                    --this.inventory[3].stackSize;
                    if (this.inventory[3].stackSize <= 0) {
                        this.inventory[3] = null;
                    }
                    this.inventory[2] = new ItemStack(IC2Items.fuelCanFilled, 1, this.fuelQuality);
                } else {
                    int damage = this.inventory[3].getMetadata();
                    if ((damage -= this.fuelQuality) < 1) {
                        damage = 1;
                    }
                    this.inventory[3] = null;
                    this.inventory[2] = new ItemStack((Item)IC2Items.jetpack, 1, damage);
                }
            }
        }
    }

    public boolean canOperate() {
        int mode = this.getMode();
        if (mode == 0) {
            return false;
        }
        if (this.inventory[0] == null) {
            return false;
        }
        if (mode == 1) {
            int food = this.getFoodValue(this.inventory[0]);
            return food != 0 && this.inventory[3] != null && food <= this.inventory[3].stackSize && (this.inventory[2] == null || this.inventory[2].getItem() == IC2Items.filledTinCan && this.inventory[2].stackSize + food <= this.inventory[2].getMaxStackSize());
        }
        int fuel = this.getFuelValue(this.inventory[0].getItem().id);
        return fuel != 0 && this.inventory[2] == null;
    }

    public boolean isOperating() {
        return this.energy > 0 && this.canOperate();
    }

    public int getMode() {
        if (this.inventory[3] == null) {
            return 0;
        }
        if (this.inventory[3].getItem() == IC2Items.tinCan) {
            return 1;
        }
        if (this.inventory[3].getItem() == IC2Items.fuelCanEmpty || this.inventory[3].getItem() == IC2Items.jetpack) {
            return 2;
        }
        return 0;
    }

    public int getFoodValue(ItemStack item) {
        Item item2 = item.getItem();
        if (item2 instanceof ItemFood) {
            ItemFood food = (ItemFood)item2;
            return (int)Math.ceil((double)food.getHealAmount(item) / 2.0);
        }
        return 0;
    }

    public int getFuelValue(int id) {
        if (id == IC2Items.cellCoalfuel.id) {
            return 2880;
        }
        if (id == IC2Items.cellBiofuel.id) {
            return 1080;
        }
        if (id == Items.DUST_REDSTONE.id && this.fuelQuality > 0) {
            return (int)((double)this.fuelQuality * 0.2);
        }
        if (id == Items.DUST_GLOWSTONE.id && this.fuelQuality > 0) {
            return (int)((double)this.fuelQuality * 0.3);
        }
        return 0;
    }

    @Override
    public String getMachineName() {
        return "Canning Machine";
    }

    public String getGuiTexture() {
        return "GUICanner.png";
    }

    public String getGuiTitleKey() {
        return "tile.ic2.machine.canner.name";
    }
}

