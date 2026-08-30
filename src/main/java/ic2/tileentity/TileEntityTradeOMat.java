

package ic2.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import ic2.tileentity.TileEntityIC2Machine;
import ic2.tileentity.TileEntityPersonalChest;
import java.util.Random;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityChest;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;

public class TileEntityTradeOMat
extends TileEntityIC2Machine {
    public String owner = "null";
    public int tradeCount = 0;
    public Container chest = null;
    public int ticker = new Random().nextInt(16);

    public TileEntityTradeOMat() {
        super(4);
    }

    @Override
    public void readAdditionalData(CompoundTag tag) {
        super.readAdditionalData(tag);
        this.owner = tag.getString("owner");
        this.tradeCount = tag.getInteger("tradeCount");
    }

    @Override
    public void writeAdditionalData(CompoundTag tag) {
        super.writeAdditionalData(tag);
        tag.putString("owner", this.owner);
        tag.putInt("tradeCount", this.tradeCount);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.worldObj.isClientSide) {
            return;
        }
        if (this.ticker++ % 16 == 0) {
            this.chest = this.lookForChest();
        }
        if (this.chest == null) {
            return;
        }
        if (!this.hasOffer()) {
            this.tradeCount = 0;
            return;
        }
        if (this.inventory[3] != null) {
            return;
        }
        if (this.itemStackMatches(this.inventory[0], this.inventory[2]) && this.attemptTrade()) {
            ++this.tradeCount;
            this.setChanged();
            this.worldObj.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, (double)this.tilePos.x() + 0.5, (double)this.tilePos.y() + 0.5, (double)this.tilePos.z() + 0.5, "random.click", 1.0f, 1.0f);
        }
    }

    public boolean attemptTrade() {
        int getOutput = this.getStackFromChest(this.inventory[1]);
        int getInput = this.getEmptySlotFromChest();
        if (getOutput < 0 || getInput < 0) {
            return false;
        }
        this.chest.setItem(getInput, this.inventory[2].copy());
        this.chest.setItem(getOutput, null);
        this.inventory[2] = null;
        this.inventory[3] = this.inventory[1].copy();
        return true;
    }

    public int getEmptySlotFromChest() {
        for (int i = 0; i < this.chest.getContainerSize(); ++i) {
            if (this.chest.getItem(i) != null) continue;
            return i;
        }
        return -1;
    }

    public int getStackFromChest(ItemStack stack) {
        for (int i = 0; i < this.chest.getContainerSize(); ++i) {
            if (!this.itemStackMatches(stack, this.chest.getItem(i))) continue;
            return i;
        }
        return -1;
    }

    public boolean itemStackMatches(ItemStack offer, ItemStack offer2) {
        if (offer == null || offer2 == null) {
            return false;
        }
        return offer.isItemEqual(offer2) && offer.stackSize <= offer2.stackSize;
    }

    public boolean hasOffer() {
        return this.inventory[0] != null && this.inventory[1] != null;
    }

    public Container lookForChest() {
        int z;
        int y;
        int x = this.tilePos.x();
        if (this.isAccessibleChest(x, (y = this.tilePos.y()) + 1, z = this.tilePos.z())) {
            return (Container)this.worldObj.getTileEntity((TilePosc)new TilePos(x, y + 1, z));
        }
        if (this.isAccessibleChest(x, y - 1, z)) {
            return (Container)this.worldObj.getTileEntity((TilePosc)new TilePos(x, y - 1, z));
        }
        if (this.isAccessibleChest(x + 1, y, z)) {
            return (Container)this.worldObj.getTileEntity((TilePosc)new TilePos(x + 1, y, z));
        }
        if (this.isAccessibleChest(x - 1, y, z)) {
            return (Container)this.worldObj.getTileEntity((TilePosc)new TilePos(x - 1, y, z));
        }
        if (this.isAccessibleChest(x, y, z + 1)) {
            return (Container)this.worldObj.getTileEntity((TilePosc)new TilePos(x, y, z + 1));
        }
        if (this.isAccessibleChest(x, y, z - 1)) {
            return (Container)this.worldObj.getTileEntity((TilePosc)new TilePos(x, y, z - 1));
        }
        return null;
    }

    public boolean isAccessibleChest(int x, int y, int z) {
        TileEntity te = this.worldObj.getTileEntity((TilePosc)new TilePos(x, y, z));
        if (te instanceof TileEntityChest) {
            return true;
        }
        if (te instanceof TileEntityPersonalChest) {
            TileEntityPersonalChest pc = (TileEntityPersonalChest)te;
            return this.owner.equals(pc.owner);
        }
        return false;
    }

    public String getWantAsString() {
        if (!this.hasOffer()) {
            return "";
        }
        return this.inventory[0].stackSize + " " + this.inventory[0].getItem().getStatName();
    }

    public String getOfferAsString() {
        if (!this.hasOffer()) {
            return "";
        }
        return this.inventory[1].stackSize + " " + this.inventory[1].getItem().getStatName();
    }

    public boolean canAccess(Player player) {
        if (this.owner.equals("null")) {
            this.owner = player.username;
            return true;
        }
        return this.owner.equalsIgnoreCase(player.username);
    }

    @Override
    public String getMachineName() {
        return "Trade-O-Mat";
    }
}

