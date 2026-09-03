package ic2.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import ic2.IC2Config;
import ic2.IC2Items;
import ic2.item.ItemFuelCan;
import net.minecraft.core.crafting.LookupFuelFurnace;
import net.minecraft.core.item.ItemStack;


public class TileEntitySlagGenerator
extends TileEntityBaseGenerator {
    public int itemFuelTime = 0;
    public int scrapCounter = 0;
    public int nextScrap;
    public boolean scrapFuel = false;

    public TileEntitySlagGenerator() {
        super(3);
        this.production = IC2Config.slagGeneratorOutput();
        this.nextScrap = (int)((double)IC2Config.slagGeneratorScrapChance() * (0.5 + Math.random()));
    }

    @Override
    public int getMaximumStorage() {
        return 8000;
    }

    @Override
    public int gaugeFuelScaled(int i) {
        if (this.fuel <= 0) {
            return 0;
        }
        if (this.itemFuelTime <= 0) {
            this.itemFuelTime = this.fuel;
        }
        int r = this.fuel * i / this.itemFuelTime;
        if (r > i) {
            r = i;
        }
        return r;
    }

    @Override
    public void tick() {
        boolean wasBurning = this.isConverting();
        super.tick();
        if (this.worldObj.isClientSide) {
            return;
        }
        if (wasBurning && !this.scrapFuel) {
            ++this.scrapCounter;
            if (this.scrapCounter > this.nextScrap) {
                this.nextScrap = (int)((double)IC2Config.slagGeneratorScrapChance() * (0.5 + Math.random()));
                this.scrapCounter = 0;
                boolean placed = false;
                if (this.inventory[2] == null) {
                    this.inventory[2] = new ItemStack(IC2Items.scrap);
                    placed = true;
                } else if (this.inventory[2].getItem() == IC2Items.scrap && this.inventory[2].stackSize < this.inventory[2].getMaxStackSize()) {
                    ++this.inventory[2].stackSize;
                    placed = true;
                }
                if (placed) {
                    this.setChanged();
                }
            }
        }
    }

    @Override
    public boolean gainFuel() {
        if (this.inventory[1] == null) {
            return false;
        }
        int value = TileEntitySlagGenerator.getItemBurnTime(this.inventory[1]);
        if (value <= 0) {
            return false;
        }
        this.scrapFuel = this.inventory[1].getItem() == IC2Items.scrap || this.inventory[1].getItem() == IC2Items.scrapBox;
        if (this.scrapFuel) {
            value /= 10;
        }
        this.fuel += value;
        this.itemFuelTime = value;
        if (this.inventory[1].getItem().hasContainerItem()) {
            this.inventory[1] = new ItemStack(this.inventory[1].getItem().getContainerItem());
        } else {
            --this.inventory[1].stackSize;
        }
        if (this.inventory[1] == null || this.inventory[1].stackSize <= 0) {
            this.inventory[1] = null;
        }
        return true;
    }

    public static int getItemBurnTime(ItemStack itemstack) {
        if (itemstack == null) {
            return 0;
        }
        if (itemstack.getItem() instanceof ItemFuelCan) {
            return ((ItemFuelCan)itemstack.getItem()).getFuelEnergy(itemstack) / 4;
        }
        return LookupFuelFurnace.instance.getFuelYield(itemstack) / 4;
    }

    @Override
    public boolean needsFuel() {
        return this.fuel <= 0;
    }

    @Override
    public boolean isConverting() {
        if (this.fuel <= 0) {
            return false;
        }
        if (this.wasConverting) {
            return this.storage < this.maxStorage;
        }
        return this.storage + this.production * 4 <= this.maxStorage;
    }

    @Override
    public int[] getChargeSlotPos() {
        return new int[]{65, 17};
    }

    @Override
    public int[] getFuelSlotPos() {
        return new int[]{65, 53};
    }

    @Override
    public String getMachineName() {
        return "Slag Generator";
    }

    @Override
    public String getGuiTexture() {
        return "GUISlagGenerator.png";
    }

    @Override
    public String getGuiTitleKey() {
        return "tile.ic2.generator.slag.name";
    }

    @Override
    public void readAdditionalData(CompoundTag tag) {
        super.readAdditionalData(tag);
        this.itemFuelTime = tag.getShort("itemFuelTime");
        this.scrapCounter = tag.getInteger("scrapCounter");
    }

    @Override
    public void writeAdditionalData(CompoundTag tag) {
        super.writeAdditionalData(tag);
        tag.putShort("itemFuelTime", (short)this.itemFuelTime);
        tag.putInt("scrapCounter", this.scrapCounter);
    }

    @Override
    public int[] getQuickGrabSlots() {
        return new int[]{2};
    }
}
