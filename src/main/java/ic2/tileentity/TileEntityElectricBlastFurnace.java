package ic2.tileentity;

import java.util.List;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryBlastFurnace;
import net.minecraft.core.item.ItemStack;

public class TileEntityElectricBlastFurnace
extends TileEntityElecMachine {
    public static final int SLOT_LEFT = 0;
    public static final int SLOT_RIGHT = 1;
    public static final int SLOT_RESULT = 2;
    public static final int SLOT_ENERGY = 3;
    public final int energyConsume = 2;
    public final int operationLength = 50;
    public int progress = 0;

    public TileEntityElectricBlastFurnace() {
        super(4, 3, 2 * 50 + 31, 32);
    }

    @Override
    public void readAdditionalData(com.mojang.nbt.tags.CompoundTag tag) {
        super.readAdditionalData(tag);
        this.progress = tag.getShort("progress");
    }

    @Override
    public void writeAdditionalData(com.mojang.nbt.tags.CompoundTag tag) {
        super.writeAdditionalData(tag);
        tag.putShort("progress", (short)this.progress);
    }

    public int gaugeProgressScaled(int i) {
        return this.progress * i / this.operationLength;
    }

    public boolean canSmelt() {
        return this.findRecipeOutput() != null && this.outputFits(this.findRecipeOutput());
    }

    private RecipeEntryBlastFurnace findRecipe() {
        List<RecipeEntryBlastFurnace> list = Registries.RECIPES.getAllBlastFurnaceRecipes();
        for (RecipeEntryBlastFurnace recipe : list) {
            if (recipe == null || !recipe.matches(this.inventory[SLOT_LEFT], this.inventory[SLOT_RIGHT])) continue;
            return recipe;
        }
        return null;
    }

    private ItemStack findRecipeOutput() {
        RecipeEntryBlastFurnace recipe = this.findRecipe();
        return recipe == null ? null : (ItemStack)recipe.getOutput();
    }

    private boolean outputFits(ItemStack output) {
        ItemStack result = this.inventory[SLOT_RESULT];
        if (result == null) {
            return true;
        }
        if (!result.isItemEqual(output)) {
            return false;
        }
        return result.stackSize + output.stackSize <= this.getMaxStackSize() && result.stackSize + output.stackSize <= result.getMaxStackSize();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.worldObj.isClientSide) {
            return;
        }
        boolean changed = false;
        boolean canSmelt = this.canSmelt();
        if (this.energy <= this.energyConsume * this.operationLength && canSmelt) {
            this.provideEnergy();
        }
        boolean operating = this.energy >= this.energyConsume && canSmelt;
        if (operating) {
            this.energy -= this.energyConsume;
            ++this.progress;
            if (this.progress >= this.operationLength) {
                this.progress = 0;
                this.smeltItem();
                changed = true;
            }
        } else {
            this.progress = 0;
        }
        if (this.active != operating) {
            this.active = operating;
            this.worldObj.notifyBlockChange((net.minecraft.core.world.pos.TilePosc)this.tilePos, this.getBlock());
            changed = true;
        }
        if (changed) {
            this.setChanged();
        }
    }

    public void smeltItem() {
        if (!this.canSmelt()) {
            return;
        }
        ItemStack output = this.findRecipeOutput().copy();
        ItemStack result = this.inventory[SLOT_RESULT];
        if (result == null) {
            this.inventory[SLOT_RESULT] = output;
        } else {
            result.stackSize += output.stackSize;
        }
        if (this.inventory[SLOT_LEFT] != null) {
            --this.inventory[SLOT_LEFT].stackSize;
            if (this.inventory[SLOT_LEFT].stackSize <= 0) {
                this.inventory[SLOT_LEFT] = null;
            }
        }
        if (this.inventory[SLOT_RIGHT] != null) {
            --this.inventory[SLOT_RIGHT].stackSize;
            if (this.inventory[SLOT_RIGHT].stackSize <= 0) {
                this.inventory[SLOT_RIGHT] = null;
            }
        }
    }

    @Override
    public String getMachineName() {
        return "Electric Blast Furnace";
    }
}
