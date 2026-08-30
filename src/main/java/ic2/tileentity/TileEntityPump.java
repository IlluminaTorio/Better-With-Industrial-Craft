

package ic2.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import ic2.IC2Items;
import ic2.tileentity.TileEntityElecMachine;
import ic2.tileentity.TileEntityGeoGenerator;
import ic2.tileentity.TileEntityMiner;
import java.util.Random;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityChest;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.item.ItemBucket;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;

public class TileEntityPump
extends TileEntityElecMachine {
    public int soundTicker = new Random().nextInt(64);
    public int pumpCharge = 0;

    public TileEntityPump() {
        super(2, 1, 200, 32);
    }

    @Override
    public void readAdditionalData(CompoundTag tag) {
        super.readAdditionalData(tag);
        this.pumpCharge = tag.getInteger("pumpCharge");
    }

    @Override
    public void writeAdditionalData(CompoundTag tag) {
        super.writeAdditionalData(tag);
        tag.putInt("pumpCharge", this.pumpCharge);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.worldObj.isClientSide) {
            return;
        }
        boolean needsInvUpdate = false;
        if (this.energy > 0 && !this.isPumpReady()) {
            --this.energy;
            ++this.pumpCharge;
            ++this.soundTicker;
        }
        if (this.energy <= this.maxEnergy) {
            needsInvUpdate = this.provideEnergy();
        }
        if (this.isPumpReady()) {
            needsInvUpdate = this.pump();
        }
        if (this.active == this.isPumpReady() && this.energy > 0) {
            this.active = !this.active;
            this.worldObj.notifyBlockChange((TilePosc)this.tilePos, this.getBlock());
        }
        if (needsInvUpdate) {
            this.setChanged();
        }
    }

    public boolean pump() {
        int x = this.tilePos.x();
        int y = this.tilePos.y();
        int z = this.tilePos.z();
        Block below = this.worldObj.getBlock(x, y - 1, z);
        if (!this.canHarvest() && below != null && below.getMaterial() == Materials.WATER) {
            this.fountain();
            return false;
        }
        if (below != null && TileEntityPump.isLiquid(below) && this.worldObj.getBlockData((TilePosc)new TilePos(x, y - 1, z)) == 0) {
            this.worldObj.setBlockWithNotify(x, y - 1, z, 0);
            return this.pumpThis(below);
        }
        return false;
    }

    private static boolean isLiquid(Block<?> block) {
        return block != null && (block.getMaterial() == Materials.WATER || block.getMaterial() == Materials.LAVA);
    }

    public boolean pumpThis(Block<?> liquid) {
        boolean lava;
        boolean bl = lava = liquid.getMaterial() == Materials.LAVA;
        if (lava && this.deliverLavaToGeo()) {
            this.pumpCharge = 0;
            return true;
        }
        if (this.inventory[0] != null && this.inventory[0].getItem() == Items.BUCKET_IRON && ItemBucket.STATE_EMPTY.equals((Object)ItemBucket.getState((ItemStack)this.inventory[0]))) {
            ItemStack filled = new ItemStack(Items.BUCKET_IRON);
            ItemBucket.setState((ItemStack)filled, (NamespaceID)(lava ? ItemBucket.STATE_LAVA : ItemBucket.STATE_WATER));
            TileEntityMiner.distributeDrop(this.worldObj, this.tilePos.x(), this.tilePos.y(), this.tilePos.z(), filled);
            this.inventory[0] = null;
            this.pumpCharge = 0;
            return true;
        }
        if (this.inventory[0] != null && this.inventory[0].getItem() == IC2Items.cellEmpty) {
            ItemStack drop = new ItemStack(lava ? IC2Items.cellLava : IC2Items.cellWater);
            --this.inventory[0].stackSize;
            if (this.inventory[0].stackSize <= 0) {
                this.inventory[0] = null;
            }
            TileEntityMiner.distributeDrop(this.worldObj, this.tilePos.x(), this.tilePos.y(), this.tilePos.z(), drop);
            this.pumpCharge = 0;
            return true;
        }
        this.pumpCharge = 0;
        return this.putInChestBucket(lava);
    }

    public boolean putInChestBucket(boolean lava) {
        int z;
        int y;
        int x = this.tilePos.x();
        return this.putInChestBucket(x, (y = this.tilePos.y()) + 1, z = this.tilePos.z(), lava) || this.putInChestBucket(x, y - 1, z, lava) || this.putInChestBucket(x + 1, y, z, lava) || this.putInChestBucket(x - 1, y, z, lava) || this.putInChestBucket(x, y, z + 1, lava) || this.putInChestBucket(x, y, z - 1, lava);
    }

    public boolean putInChestBucket(int x, int y, int z, boolean lava) {
        TileEntity te = this.worldObj.getTileEntity((TilePosc)new TilePos(x, y, z));
        if (!(te instanceof TileEntityChest)) {
            return false;
        }
        TileEntityChest chest = (TileEntityChest)te;
        for (int i = 0; i < chest.getContainerSize(); ++i) {
            ItemStack stack = chest.getItem(i);
            if (stack == null || stack.getItem() != Items.BUCKET_IRON || !ItemBucket.STATE_EMPTY.equals((Object)ItemBucket.getState((ItemStack)stack))) continue;
            ItemBucket.setState((ItemStack)stack, (NamespaceID)(lava ? ItemBucket.STATE_LAVA : ItemBucket.STATE_WATER));
            return true;
        }
        return false;
    }

    public void fountain() {
        if (this.worldObj.getTotalWorldTime() % 10L == 0L) {
            --this.pumpCharge;
        }
        int x = this.tilePos.x();
        int y = this.tilePos.y();
        int z = this.tilePos.z();
        int top = 0;
        for (int i = 1; i < 4; ++i) {
            Block block = this.worldObj.getBlock(x, y + i, z);
            if (block != Blocks.AIR && block != Blocks.FLUID_WATER_STILL) continue;
            top = i;
        }
        if (top != 0) {
            this.worldObj.setBlockAndMetadataWithNotify(x, y + top, z, Blocks.FLUID_WATER_STILL.id(), 1);
        }
    }

    public boolean isPumpReady() {
        return this.pumpCharge >= 200;
    }

    public boolean canHarvest() {
        if (!this.isPumpReady()) {
            return false;
        }
        return this.inventory[0] != null && (this.inventory[0].getItem() == IC2Items.cellEmpty || this.inventory[0].getItem() == Items.BUCKET_IRON && ItemBucket.STATE_EMPTY.equals((Object)ItemBucket.getState((ItemStack)this.inventory[0]))) || this.isBucketInChestAvailable();
    }

    public boolean isBucketInChestAvailable() {
        int z;
        int y;
        int x = this.tilePos.x();
        return this.isBucketInChestAvailable(x, (y = this.tilePos.y()) + 1, z = this.tilePos.z()) || this.isBucketInChestAvailable(x, y - 1, z) || this.isBucketInChestAvailable(x + 1, y, z) || this.isBucketInChestAvailable(x - 1, y, z) || this.isBucketInChestAvailable(x, y, z + 1) || this.isBucketInChestAvailable(x, y, z - 1);
    }

    public boolean isBucketInChestAvailable(int x, int y, int z) {
        TileEntity te = this.worldObj.getTileEntity((TilePosc)new TilePos(x, y, z));
        if (!(te instanceof TileEntityChest)) {
            return false;
        }
        TileEntityChest chest = (TileEntityChest)te;
        for (int i = 0; i < chest.getContainerSize(); ++i) {
            ItemStack stack = chest.getItem(i);
            if (stack == null || stack.getItem() != Items.BUCKET_IRON || !ItemBucket.STATE_EMPTY.equals((Object)ItemBucket.getState((ItemStack)stack))) continue;
            return true;
        }
        return false;
    }

    public boolean deliverLavaToGeo() {
        int z;
        int y;
        int x = this.tilePos.x();
        return this.deliverLavaToGeo(x, (y = this.tilePos.y()) + 1, z = this.tilePos.z()) || this.deliverLavaToGeo(x, y - 1, z) || this.deliverLavaToGeo(x + 1, y, z) || this.deliverLavaToGeo(x - 1, y, z) || this.deliverLavaToGeo(x, y, z + 1) || this.deliverLavaToGeo(x, y, z - 1);
    }

    public boolean deliverLavaToGeo(int x, int y, int z) {
        TileEntity te = this.worldObj.getTileEntity((TilePosc)new TilePos(x, y, z));
        if (te instanceof TileEntityGeoGenerator) {
            TileEntityGeoGenerator geo = (TileEntityGeoGenerator)te;
            if (geo.fuel < geo.maxLava) {
                geo.fuel = Math.min(geo.maxLava, geo.fuel + 2000);
                geo.setChanged();
                return true;
            }
        }
        return false;
    }

    public void pumpFromMiner(Block<?> liquid) {
        if (liquid == null) {
            return;
        }
        if (this.canHarvest()) {
            this.pumpThis(liquid);
        }
    }

    @Override
    public String getMachineName() {
        return "Pump";
    }
}

