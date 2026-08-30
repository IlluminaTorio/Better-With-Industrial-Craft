

package ic2.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import ic2.tileentity.TileEntityElecMachine;
import java.util.Random;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryFurnace;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.world.pos.TilePosc;

public class TileEntityInduction
extends TileEntityElecMachine {
    public static int maxHeat = 10000;
    public int heat = 0;
    public int progress = 0;
    public int soundTicker = new Random().nextInt(64);

    public TileEntityInduction() {
        super(5, 1, maxHeat, 128);
    }

    @Override
    public void readAdditionalData(CompoundTag tag) {
        super.readAdditionalData(tag);
        this.heat = tag.getInteger("heat");
        this.progress = tag.getInteger("progress");
    }

    @Override
    public void writeAdditionalData(CompoundTag tag) {
        super.writeAdditionalData(tag);
        tag.putInt("heat", this.heat);
        tag.putInt("progress", this.progress);
    }

    public String getHeat() {
        return this.heat * 100 / maxHeat + "%";
    }

    public int gaugeProgressScaled(int i) {
        return i * this.progress / 4000;
    }

    public int gaugeHeatScaled(int i) {
        return i * this.heat / maxHeat;
    }

    public int gaugeFuelScaled(int i) {
        return i * this.energy / this.maxEnergy;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.worldObj.isClientSide) {
            return;
        }
        boolean isActive = false;
        boolean needsInvUpdate = false;
        if (this.energy > 0 && (this.canOperate() || this.isRedstonePowered())) {
            --this.energy;
            if (this.heat < maxHeat) {
                ++this.heat;
            }
            isActive = true;
        } else if (this.heat > 0) {
            this.heat -= 4;
        }
        if (this.energy > 0 && this.canOperate()) {
            this.energy -= 15;
            isActive = true;
        }
        if (this.heat == 0) {
            this.progress = 0;
        }
        if (this.energy <= this.maxEnergy) {
            needsInvUpdate = this.provideEnergy();
        }
        if (this.energy > 0 && this.canOperate()) {
            ++this.soundTicker;
            if (this.soundTicker % 64 == 0) {
                this.worldObj.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, (double)this.tilePos.x() + 0.5, (double)this.tilePos.y() + 0.5, (double)this.tilePos.z() + 0.5, "random.fizz", 1.0f, 1.0f);
            }
            this.progress += this.heat / 30;
            if (this.progress >= 4000) {
                this.progress = 0;
                this.operate();
                needsInvUpdate = true;
            }
        }
        if (this.heat >= 3000) {
            isActive = true;
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

    public void operate() {
        this.operate(0, 2);
        this.operate(3, 4);
    }

    public void operate(int input, int output) {
        if (!this.canOperate(input, output)) {
            return;
        }
        ItemStack itemstack = this.getResultFor(this.inventory[input]);
        if (this.inventory[output] == null) {
            this.inventory[output] = itemstack.copy();
        } else {
            this.inventory[output].stackSize += itemstack.stackSize;
        }
        if (this.inventory[input].getItem().hasContainerItem()) {
            this.inventory[input] = new ItemStack(this.inventory[input].getItem().getContainerItem());
        } else {
            --this.inventory[input].stackSize;
        }
        if (this.inventory[input].stackSize <= 0) {
            this.inventory[input] = null;
        }
    }

    public boolean canOperate() {
        return this.canOperate(0, 2) || this.canOperate(3, 4);
    }

    public boolean canOperate(int input, int output) {
        if (this.inventory[input] == null) {
            return false;
        }
        ItemStack itemstack = this.getResultFor(this.inventory[input]);
        if (itemstack == null) {
            return false;
        }
        if (this.inventory[output] == null) {
            return true;
        }
        if (!this.inventory[output].isItemEqual(itemstack)) {
            return false;
        }
        return this.inventory[output].stackSize + itemstack.stackSize <= this.inventory[output].getMaxStackSize();
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

    protected boolean isRedstonePowered() {
        return this.worldObj.hasNeighborSignal((TilePosc)this.tilePos);
    }

    @Override
    public String getMachineName() {
        return "Induction Furnace";
    }

    public String getGuiTexture() {
        return "GUIInduction.png";
    }

    public String getGuiTitleKey() {
        return "tile.ic2.machine.induction_furnace.name";
    }
}

