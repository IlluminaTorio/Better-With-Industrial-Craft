

package ic2.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import ic2.item.ItemFuelCan;
import ic2.tileentity.TileEntityIC2Machine;
import net.minecraft.core.crafting.LookupFuelFurnace;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryFurnace;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.pos.TilePosc;

public class TileEntityIronFurnace
extends TileEntityIC2Machine {
    public int fuel = 0;
    public int maxFuel = 0;
    public int progress = 0;
    public int operationLength = 160;

    public TileEntityIronFurnace() {
        super(3);
    }

    @Override
    public void readAdditionalData(CompoundTag tag) {
        super.readAdditionalData(tag);
        this.fuel = tag.getShort("fuel");
        this.maxFuel = tag.getShort("maxFuel");
        this.progress = tag.getShort("progress");
    }

    @Override
    public void writeAdditionalData(CompoundTag tag) {
        super.writeAdditionalData(tag);
        tag.putShort("fuel", (short)this.fuel);
        tag.putShort("maxFuel", (short)this.maxFuel);
        tag.putShort("progress", (short)this.progress);
    }

    public int gaugeProgressScaled(int i) {
        return this.progress * i / this.operationLength;
    }

    public int gaugeFuelScaled(int i) {
        if (this.maxFuel == 0) {
            this.maxFuel = this.fuel;
            if (this.maxFuel == 0) {
                this.maxFuel = this.operationLength;
            }
        }
        return this.fuel * i / this.maxFuel;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.worldObj.isClientSide) {
            return;
        }
        boolean wasOperating = this.isBurning();
        boolean needsInvUpdate = false;
        if (this.fuel <= 0 && this.canOperate()) {
            this.fuel = this.maxFuel = TileEntityIronFurnace.getFuelValueFor(this.inventory[1]);
            if (this.fuel > 0) {
                if (this.inventory[1].getItem().hasContainerItem()) {
                    this.inventory[1] = new ItemStack(this.inventory[1].getItem().getContainerItem());
                } else {
                    --this.inventory[1].stackSize;
                }
                if (this.inventory[1].stackSize <= 0) {
                    this.inventory[1] = null;
                }
                needsInvUpdate = true;
            }
        }
        if (this.isBurning() && this.canOperate()) {
            ++this.progress;
            if (this.progress >= this.operationLength) {
                this.progress = 0;
                this.operate();
                needsInvUpdate = true;
            }
        } else {
            this.progress = 0;
        }
        if (this.fuel > 0) {
            --this.fuel;
        }
        if (wasOperating != this.isBurning()) {
            this.active = this.isBurning();
            this.worldObj.notifyBlockChange((TilePosc)this.tilePos, this.getBlock());
            needsInvUpdate = true;
        }
        if (needsInvUpdate) {
            this.setChanged();
        }
    }

    public void operate() {
        if (!this.canOperate()) {
            return;
        }
        ItemStack itemstack = this.getResultFor(this.inventory[0]);
        if (this.inventory[2] == null) {
            this.inventory[2] = itemstack.copy();
        } else {
            this.inventory[2].stackSize += itemstack.stackSize;
        }
        if (this.inventory[0].getItem().hasContainerItem()) {
            this.inventory[0] = new ItemStack(this.inventory[0].getItem().getContainerItem());
        } else {
            --this.inventory[0].stackSize;
        }
        if (this.inventory[0].stackSize <= 0) {
            this.inventory[0] = null;
        }
    }

    public boolean isBurning() {
        return this.fuel > 0;
    }

    public boolean canOperate() {
        if (this.inventory[0] == null) {
            return false;
        }
        ItemStack itemstack = this.getResultFor(this.inventory[0]);
        if (itemstack == null) {
            return false;
        }
        if (this.inventory[2] == null) {
            return true;
        }
        if (!this.inventory[2].isItemEqual(itemstack)) {
            return false;
        }
        return this.inventory[2].stackSize + itemstack.stackSize <= this.inventory[2].getMaxStackSize();
    }

    public ItemStack getResultFor(ItemStack stack) {
        if (stack == null) {
            return null;
        }
        for (RecipeEntryFurnace recipe : Registries.RECIPES.getAllFurnaceRecipes()) {
            if (recipe == null || !recipe.matches(stack)) continue;
            return ((ItemStack)recipe.getOutput()).copy();
        }
        return null;
    }

    @Override
    public String getMachineName() {
        return "Iron Furnace";
    }

    public static int getFuelValueFor(ItemStack itemstack) {
        if (itemstack == null) {
            return 0;
        }
        if (itemstack.getItem() instanceof ItemFuelCan) {
            return ((ItemFuelCan)itemstack.getItem()).getFuelEnergy(itemstack);
        }
        return LookupFuelFurnace.instance.getFuelYield(itemstack);
    }
}

