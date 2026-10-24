package ic2.tileentity;

import ic2.tileentity.TileEntityElecMachine;
import java.util.Random;
import net.minecraft.core.WeightedRandomBag;
import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicChest;
import net.minecraft.core.block.BlockLogicRotatable;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryTrommel;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;

public class TileEntityElectricTrommel
extends TileEntityElecMachine {
    public static final int[] SLOTS_INGREDIENT = new int[]{0, 1, 2, 3};
    public static final int SLOT_ENERGY = 4;
    public final int energyConsume = 2;
    public final int operationLength = 25;
    public int progress = 0;
    public int nextToSieve = 0;
    private final Random random = new Random();

    public TileEntityElectricTrommel() {
        super(5, 4, 2 * 25 + 31, 32);
    }

    @Override
    public void readAdditionalData(com.mojang.nbt.tags.CompoundTag tag) {
        super.readAdditionalData(tag);
        this.progress = tag.getShort("progress");
        this.nextToSieve = tag.getByte("nextToSieve");
    }

    @Override
    public void writeAdditionalData(com.mojang.nbt.tags.CompoundTag tag) {
        super.writeAdditionalData(tag);
        tag.putShort("progress", (short)this.progress);
        tag.putByte("nextToSieve", (byte)this.nextToSieve);
    }

    public int gaugeProgressScaled(int i) {
        return this.progress * i / this.operationLength;
    }

    public boolean isOperating() {
        return this.energy > 0 && this.canProduce(this.nextToSieve);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.worldObj.isClientSide) {
            return;
        }
        boolean changed = false;
        if (this.nextToSieve >= SLOTS_INGREDIENT.length) {
            this.nextToSieve = 0;
        }
        if (!this.canProduce(this.nextToSieve)) {
            this.nextToSieve = (this.nextToSieve + 1) % SLOTS_INGREDIENT.length;
        }
        if (this.energy <= this.energyConsume * this.operationLength && this.canProduce(this.nextToSieve)) {
            this.provideEnergy();
        }
        boolean operating = this.energy >= this.energyConsume && this.canProduce(this.nextToSieve);
        if (operating) {
            this.energy -= this.energyConsume;
            ++this.progress;
            if (this.progress >= this.operationLength) {
                this.progress = 0;
                this.sieveItem(this.nextToSieve);
                this.nextToSieve = (this.nextToSieve + 1) % SLOTS_INGREDIENT.length;
                changed = true;
            }
        } else if (!this.canProduce(this.nextToSieve)) {
            this.progress = 0;
        }
        if (this.active != operating) {
            this.active = operating;
            this.worldObj.notifyBlockChange((TilePosc)this.tilePos, this.getBlock());
            changed = true;
        }
        if (changed) {
            this.setChanged();
        }
    }

    public boolean canProduce(int slotIndex) {
        return this.inventory[slotIndex] != null && this.canItemBeTrommeled(this.inventory[slotIndex]);
    }

    public void sieveItem(int slotIndex) {
        if (!this.canProduce(slotIndex)) {
            return;
        }
        ItemStack result = this.getItemResult(this.inventory[slotIndex]);
        ItemStack stack = this.inventory[slotIndex];
        --stack.stackSize;
        if (stack.stackSize <= 0) {
            this.inventory[slotIndex] = null;
        }
        if (result == null || result.stackSize <= 0) {
            return;
        }
        Direction direction = BlockLogicRotatable.getDirectionFromMeta(this.worldObj.getBlockMetadata(this.tilePos.x(), this.tilePos.y(), this.tilePos.z()));
        TilePos frontPos = new TilePos(this.tilePos.x(), this.tilePos.y(), this.tilePos.z()).add(direction);
        int adjacentId = this.worldObj.getBlockId(frontPos.x(), frontPos.y(), frontPos.z());
        if (Block.hasLogicClass(Blocks.blocksList[adjacentId], BlockLogicChest.class)) {
            net.minecraft.core.player.inventory.container.Container chest = BlockLogicChest.getInventory(this.worldObj, frontPos);
            for (int i = 0; i < chest.getContainerSize(); ++i) {
                ItemStack slot = chest.getItem(i);
                if (slot != null && slot.itemID == result.itemID && slot.getMetadata() == result.getMetadata()) {
                    while (slot.stackSize + result.stackSize <= slot.getMaxStackSize() && result.stackSize > 0) {
                        ++slot.stackSize;
                        --result.stackSize;
                        chest.setItem(i, slot);
                    }
                    if (result.stackSize <= 0) {
                        return;
                    }
                }
            }
            for (int i = 0; i < chest.getContainerSize(); ++i) {
                if (chest.getItem(i) == null) {
                    chest.setItem(i, result);
                    return;
                }
            }
        }
        EntityItem entity = new EntityItem(this.worldObj, (double)frontPos.x() + 0.5, (double)frontPos.y() + 0.5, (double)frontPos.z() + 0.5, result);
        entity.pickupDelay = 10;
        entity.fling(direction.offsetX() * 0.3125, direction.offsetY() * 0.3125, direction.offsetZ() * 0.3125, 1.0f);
        this.worldObj.entityJoinedWorld(entity);
    }

    private boolean canItemBeTrommeled(ItemStack itemstack) {
        if (itemstack == null) {
            return false;
        }
        for (RecipeEntryTrommel recipe : Registries.RECIPES.getAllTrommelRecipes()) {
            if (!((RecipeSymbol)recipe.getInput()).matches(itemstack)) continue;
            return true;
        }
        return false;
    }

    private ItemStack getItemResult(ItemStack slotItem) {
        for (RecipeEntryTrommel recipe : Registries.RECIPES.getAllTrommelRecipes()) {
            if (!((RecipeSymbol)recipe.getInput()).matches(slotItem)) continue;
            return ((WeightedRandomLootObject)((WeightedRandomBag)recipe.getOutput()).getRandom()).getItemStack();
        }
        return null;
    }

    @Override
    public String getMachineName() {
        return "Electric Trommel";
    }

    @Override
    public int[] getQuickGrabSlots() {
        return new int[0];
    }
}
