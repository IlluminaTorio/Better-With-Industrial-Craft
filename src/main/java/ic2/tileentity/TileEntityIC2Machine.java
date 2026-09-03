

package ic2.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import com.mojang.nbt.tags.Tag;
import ic2.tileentity.TileEntityIC2Block;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;

public abstract class TileEntityIC2Machine
extends TileEntityIC2Block
implements Container {
    public ItemStack[] inventory;

    public TileEntityIC2Machine(int slotCount) {
        this.inventory = new ItemStack[slotCount];
    }

    public int getContainerSize() {
        return this.inventory.length;
    }

    public ItemStack getItem(int i) {
        return this.inventory[i];
    }

    public ItemStack removeItem(int i, int j) {
        if (this.inventory[i] != null) {
            if (this.inventory[i].stackSize <= j) {
                ItemStack itemstack = this.inventory[i];
                this.inventory[i] = null;
                return itemstack;
            }
            ItemStack itemstack1 = this.inventory[i].splitStack(j);
            if (this.inventory[i].stackSize == 0) {
                this.inventory[i] = null;
            }
            return itemstack1;
        }
        return null;
    }

    public void setItem(int i, ItemStack itemstack) {
        this.inventory[i] = itemstack;
        if (itemstack != null && itemstack.stackSize > this.getMaxStackSize()) {
            itemstack.stackSize = this.getMaxStackSize();
        }
    }

    public int getMaxStackSize() {
        return 64;
    }

    public boolean stillValid(Player entityplayer) {
        if (this.worldObj.getTileEntity((TilePosc)this.tilePos) != this) {
            return false;
        }
        return entityplayer.distanceToSqr((double)this.tilePos.x() + 0.5, (double)this.tilePos.y() + 0.5, (double)this.tilePos.z() + 0.5) <= 64.0;
    }

    public void sort() {
    }

    public abstract String getMachineName();

    public String getNameTranslationKey() {
        return "tile." + this.getBlock().getKey() + ".name";
    }

    @Override
    public void readAdditionalData(CompoundTag tag) {
        ListTag nbttaglist = tag.getList("Items");
        this.inventory = new ItemStack[this.getContainerSize()];
        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            CompoundTag nbttagcompound1 = (CompoundTag)nbttaglist.tagAt(i);
            byte byte0 = nbttagcompound1.getByte("Slot");
            if (byte0 < 0 || byte0 >= this.inventory.length) continue;
            this.inventory[byte0] = ItemStack.readItemStackFromNbt((CompoundTag)nbttagcompound1);
        }
    }

    @Override
    public void writeAdditionalData(CompoundTag tag) {
        ListTag nbttaglist = new ListTag();
        for (int i = 0; i < this.inventory.length; ++i) {
            if (this.inventory[i] == null) continue;
            CompoundTag nbttagcompound1 = new CompoundTag();
            nbttagcompound1.putByte("Slot", (byte)i);
            this.inventory[i].writeToNBT(nbttagcompound1);
            nbttaglist.addTag((Tag)nbttagcompound1);
        }
        tag.put("Items", (Tag)nbttaglist);
    }

    public void setChanged() {
        super.setChanged();
    }

    public void dropContents(World world, int x, int y, int z) {
        if (world == null || world.isClientSide) {
            return;
        }
        for (int i = 0; i < this.inventory.length; ++i) {
            ItemStack stack = this.inventory[i];
            if (stack == null) continue;
            this.inventory[i] = null;
            float fx = world.rand.nextFloat() * 0.8f + 0.1f;
            float fy = world.rand.nextFloat() * 0.8f + 0.1f;
            float fz = world.rand.nextFloat() * 0.8f + 0.1f;
            while (stack.stackSize > 0) {
                int split = Math.min(stack.stackSize, world.rand.nextInt(21) + 10);
                ItemStack drop = stack.splitStack(split);
                if (drop.stackSize <= 0) break;
                EntityItem entity = new EntityItem(world, (double)((float)x + fx), (double)((float)y + fy), (double)((float)z + fz), drop);
                entity.xd *= 0.05;
                entity.yd *= 0.05;
                entity.zd *= 0.05;
                world.entityJoinedWorld((Entity)entity);
            }
            if (stack.stackSize > 0) continue;
            this.inventory[i] = null;
        }
    }





    public int[] getQuickGrabSlots() {
        return new int[0];
    }
}

