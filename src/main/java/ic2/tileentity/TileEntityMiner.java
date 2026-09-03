

package ic2.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import ic2.IC2Blocks;
import ic2.IC2Items;
import ic2.energy.IChargeableItem;
import ic2.item.ItemScanner;
import ic2.item.tool.ItemDrill;
import ic2.item.tool.ItemElectricTool;
import ic2.tileentity.TileEntityElecMachine;
import ic2.tileentity.TileEntityPump;
import java.util.Random;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.block.ItemBlock;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;

public class TileEntityMiner
extends TileEntityElecMachine {
    public int soundTicker = new Random().nextInt(32);
    public int targetX = 0;
    public int targetY = -1;
    public int targetZ = 0;
    public int miningTicker = 0;
    public String stuckOn = null;

    public TileEntityMiner() {
        super(4, 0, 1000, 32);
    }

    @Override
    public void readAdditionalData(CompoundTag tag) {
        super.readAdditionalData(tag);
        this.targetX = tag.getInteger("targetX");
        this.targetY = tag.getInteger("targetY");
        this.targetZ = tag.getInteger("targetZ");
        this.miningTicker = tag.getInteger("miningTicker");
    }

    @Override
    public void writeAdditionalData(CompoundTag tag) {
        super.writeAdditionalData(tag);
        tag.putInt("targetX", this.targetX);
        tag.putInt("targetY", this.targetY);
        tag.putInt("targetZ", this.targetZ);
        tag.putInt("miningTicker", this.miningTicker);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.worldObj.isClientSide) {
            return;
        }
        boolean wasOperating = this.isOperating();
        boolean needsInvUpdate = false;
        if (this.isOperating()) {
            Item item;
            --this.energy;
            if (this.inventory[1] != null && (item = this.inventory[1].getItem()) instanceof ItemScanner) {
                ItemScanner scanner = (ItemScanner)item;
                this.energy -= scanner.giveEnergyTo(this.inventory[1], this.energy, 2);
            }
            if (this.inventory[3] != null && this.inventory[3].getItem() instanceof ItemDrill) {
                this.energy -= ((IChargeableItem)this.inventory[3].getItem()).giveEnergyTo(this.inventory[3], this.energy, 1);
            }
        }
        if (this.energy <= this.maxEnergy) {
            needsInvUpdate = this.provideEnergy();
        }
        if (wasOperating) {
            needsInvUpdate = this.mine();
        } else if (this.inventory[3] == null) {
            if (this.energy >= 2 && this.canWithdraw()) {
                this.targetY = -1;
                ++this.miningTicker;
                this.energy -= 2;
                if (this.miningTicker >= 20) {
                    this.miningTicker = 0;
                    needsInvUpdate = this.withdrawPipe();
                }
            } else if (this.isStuck()) {
                this.miningTicker = 0;
            }
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

    public boolean mine() {
        if (this.targetY < 0) {
            this.aquireTarget();
            return false;
        }
        if (!this.canReachTarget(this.targetX, this.targetY, this.targetZ, true)) {
            int x = this.targetX - this.tilePos.x();
            int z = this.targetZ - this.tilePos.z();
            if (Math.abs(x) > Math.abs(z)) {
                this.targetX = x > 0 ? (this.targetX = this.targetX - 1) : (this.targetX = this.targetX + 1);
            } else {
                this.targetZ = z > 0 ? (this.targetZ = this.targetZ - 1) : (this.targetZ = this.targetZ + 1);
            }
            return false;
        }
        Block block = this.worldObj.getBlock(this.targetX, this.targetY, this.targetZ);
        if (!this.canMine(block)) {
            this.miningTicker = -1;
            this.stuckOn = block != null ? block.getKey() : null;
            return false;
        }
        this.stuckOn = null;
        ++this.soundTicker;
        ++this.miningTicker;
        --this.energy;
        if (this.inventory[3].getItem() == IC2Items.diamondDrill) {
            this.miningTicker += 3;
            this.energy -= 14;
        }
        if (this.miningTicker >= 200) {
            this.miningTicker = 0;
            this.mineBlock();
            return true;
        }
        return false;
    }

    public void mineBlock() {
        ItemElectricTool drill = (ItemElectricTool)this.inventory[3].getItem();
        drill.use(this.inventory[3], 1);
        Block block = this.worldObj.getBlock(this.targetX, this.targetY, this.targetZ);
        if (block == null || block == Blocks.AIR) {
            return;
        }
        boolean liquid = block.getMaterial() == Materials.WATER || block.getMaterial() == Materials.LAVA;
        TilePos pos = new TilePos(this.targetX, this.targetY, this.targetZ);
        int data = this.worldObj.getBlockData((TilePosc)pos);
        this.worldObj.setBlockWithNotify(this.targetX, this.targetY, this.targetZ, 0);
        if (!liquid) {
            ItemStack[] drops = block.getLogic().getBreakResult(this.worldObj, EnumDropCause.PROPER_TOOL, (TilePosc)pos, data, null);
            if (drops != null) {
                for (ItemStack drop : drops) {
                    if (drop == null) continue;
                    TileEntityMiner.distributeDrop(this.worldObj, this.tilePos.x(), this.tilePos.y(), this.tilePos.z(), drop);
                }
            }
        } else if (block.getMaterial() == Materials.WATER) {
            this.usePump(Blocks.FLUID_WATER_FLOWING);
        } else {
            this.usePump(Blocks.FLUID_LAVA_FLOWING);
        }
        this.energy -= 2 * (this.tilePos.y() - this.targetY);
        if (this.targetX == this.tilePos.x() && this.targetZ == this.tilePos.z()) {
            this.worldObj.setBlockWithNotify(this.targetX, this.targetY, this.targetZ, IC2Blocks.miningPipe.id());
            --this.inventory[2].stackSize;
            if (this.inventory[2].stackSize == 0) {
                this.inventory[2] = null;
            }
            this.energy -= 10;
        }
        this.updateMineTip(this.targetY);
        this.targetY = -1;
    }

    public void usePump(Block<?> liquid) {
        TileEntityPump pump = this.findPump();
        if (pump != null) {
            pump.pumpFromMiner(liquid);
        }
    }

    private TileEntityPump findPump() {
        int x = this.tilePos.x();
        int y = this.tilePos.y();
        int z = this.tilePos.z();
        for (int[] d : new int[][]{{0, 1, 0}, {0, -1, 0}, {1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}}) {
            TileEntityPump pump;
            TileEntity tileEntity = this.worldObj.getTileEntity((TilePosc)new TilePos(x + d[0], y + d[1], z + d[2]));
            if (!(tileEntity instanceof TileEntityPump) || !(pump = (TileEntityPump)tileEntity).canHarvest()) continue;
            return pump;
        }
        return null;
    }

    public boolean withdrawPipe() {
        int y = this.getPipeTip();
        this.worldObj.setBlockWithNotify(this.tilePos.x(), y, this.tilePos.z(), 0);
        TileEntityMiner.distributeDrop(this.worldObj, this.tilePos.x(), this.tilePos.y(), this.tilePos.z(), IC2Blocks.miningPipe.getDefaultStack());
        if (this.inventory[2] != null && this.inventory[2].getItem() != IC2Blocks.miningPipe.asItem() && this.inventory[2].getItem() instanceof ItemBlock) {
            this.worldObj.setBlockWithNotify(this.tilePos.x(), y, this.tilePos.z(), Blocks.getBlock((int)this.inventory[2].getItem().id) != null ? this.inventory[2].getItem().id : 0);
            --this.inventory[2].stackSize;
            if (this.inventory[2].stackSize == 0) {
                this.inventory[2] = null;
            }
            this.updateMineTip(y + 1);
            return true;
        }
        this.updateMineTip(y + 1);
        return false;
    }

    public static void distributeDrop(World worldObj, int xCoord, int yCoord, int zCoord, ItemStack drop) {
        if (TileEntityMiner.putIntoChest(worldObj, xCoord, yCoord + 1, zCoord, drop)) {
            return;
        }
        if (TileEntityMiner.putIntoChest(worldObj, xCoord, yCoord - 1, zCoord, drop)) {
            return;
        }
        if (TileEntityMiner.putIntoChest(worldObj, xCoord + 1, yCoord, zCoord, drop)) {
            return;
        }
        if (TileEntityMiner.putIntoChest(worldObj, xCoord - 1, yCoord, zCoord, drop)) {
            return;
        }
        if (TileEntityMiner.putIntoChest(worldObj, xCoord, yCoord, zCoord + 1, drop)) {
            return;
        }
        if (TileEntityMiner.putIntoChest(worldObj, xCoord, yCoord, zCoord - 1, drop)) {
            return;
        }
        EntityItem entityitem = new EntityItem(worldObj, (double)xCoord, (double)yCoord, (double)zCoord, drop);
        entityitem.pickupDelay = 10;
        if (!worldObj.isClientSide) {
            worldObj.entityJoinedWorld((Entity)entityitem);
        }
    }

    public static boolean putIntoChest(World worldObj, int x, int y, int z, ItemStack item) {
        TileEntity te = worldObj.getTileEntity((TilePosc)new TilePos(x, y, z));
        if (!(te instanceof Container)) {
            return false;
        }
        Container container = (Container)te;
        for (int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack chestStack = container.getItem(i);
            if (chestStack == null) {
                container.setItem(i, item);
                return true;
            }
            if (!chestStack.isItemEqual(item) || chestStack.stackSize + item.stackSize > chestStack.getMaxStackSize()) continue;
            chestStack.stackSize += item.stackSize;
            return true;
        }
        return false;
    }

    public void updateMineTip(int low) {
        if (low == this.tilePos.y()) {
            return;
        }
        int x = this.tilePos.x();
        int z = this.tilePos.z();
        for (int y = this.tilePos.y() - 1; y > low; --y) {
            if (this.worldObj.getBlock(x, y, z) == IC2Blocks.miningPipe) continue;
            this.worldObj.setBlockWithNotify(x, y, z, IC2Blocks.miningPipe.id());
        }
        this.worldObj.setBlockWithNotify(x, low, z, IC2Blocks.miningTip.id());
    }

    public boolean canReachTarget(int x, int y, int z, boolean ignore) {
        if (this.tilePos.x() == x && this.tilePos.z() == z) {
            return true;
        }
        if (!ignore && !this.canPass(this.worldObj.getBlock(x, y, z))) {
            return false;
        }
        int xdif = x - this.tilePos.x();
        int zdif = z - this.tilePos.z();
        if (Math.abs(xdif) > Math.abs(zdif)) {
            x = xdif > 0 ? --x : ++x;
        } else {
            z = zdif > 0 ? --z : ++z;
        }
        return this.canReachTarget(x, y, z, false);
    }

    public void aquireTarget() {
        int y = this.getPipeTip();
        if (this.inventory[1] == null || this.inventory[1].getItem() != IC2Items.odScanner && this.inventory[1].getItem() != IC2Items.ovScanner) {
            this.setTarget(this.tilePos.x(), y - 1, this.tilePos.z());
            return;
        }
        int scanrange = 0;
        ItemScanner item = (ItemScanner)this.inventory[1].getItem();
        if (this.inventory[1].getItem() == IC2Items.odScanner && item.use(this.inventory[1], 1)) {
            scanrange = 2;
        }
        if (this.inventory[1].getItem() == IC2Items.ovScanner && item.use(this.inventory[1], 5)) {
            scanrange = 4;
        }
        if (scanrange > 0) {
            for (int x = this.tilePos.x() - scanrange; x <= this.tilePos.x() + scanrange; ++x) {
                for (int z = this.tilePos.z() - scanrange; z <= this.tilePos.z() + scanrange; ++z) {
                    boolean pumpable;
                    Block block = this.worldObj.getBlock(x, y, z);
                    if (block == null) continue;
                    int id = block.id();
                    boolean valuable = ItemScanner.isValuable(id) && this.canMine(block);
                    boolean bl = pumpable = this.findPump() != null && this.worldObj.getBlockData((TilePosc)new TilePos(x, y, z)) == 0 && (block == Blocks.FLUID_LAVA_STILL || block == Blocks.FLUID_LAVA_FLOWING);
                    if (!valuable && !pumpable) continue;
                    this.setTarget(x, y, z);
                    return;
                }
            }
        }
        this.setTarget(this.tilePos.x(), y - 1, this.tilePos.z());
    }

    public void setTarget(int x, int y, int z) {
        this.targetX = x;
        this.targetY = y;
        this.targetZ = z;
    }

    public int getPipeTip() {
        int y = this.tilePos.y();
        while (this.worldObj.getBlock(this.tilePos.x(), y - 1, this.tilePos.z()) == IC2Blocks.miningPipe || this.worldObj.getBlock(this.tilePos.x(), y - 1, this.tilePos.z()) == IC2Blocks.miningTip) {
            --y;
        }
        return y;
    }

    public boolean canPass(Block<?> block) {
        return block == Blocks.AIR || block.getMaterial() == Materials.WATER || block.getMaterial() == Materials.LAVA || block == IC2Blocks.machineBlock || block == IC2Blocks.miningPipe || block == IC2Blocks.miningTip;
    }

    public boolean isOperating() {
        return this.energy > 100 && this.canOperate();
    }

    public boolean canOperate() {
        if (this.inventory[2] == null || this.inventory[3] == null) {
            return false;
        }
        if (this.inventory[2].getItem() != IC2Blocks.miningPipe.asItem()) {
            return false;
        }
        if (this.inventory[3].getItem() != IC2Items.miningDrill && this.inventory[3].getItem() != IC2Items.diamondDrill) {
            return false;
        }
        return !this.isStuck();
    }

    public boolean isStuck() {
        return this.miningTicker < 0;
    }

    public String getStuckOn() {
        return this.stuckOn;
    }

    public boolean canMine(Block<?> block) {
        Item item;
        if (block == null || block == Blocks.AIR) {
            return true;
        }
        if ((block.getMaterial() == Materials.WATER || block.getMaterial() == Materials.LAVA) && this.findPump() != null) {
            return true;
        }
        if (this.inventory[3] != null && (item = this.inventory[3].getItem()) instanceof ItemElectricTool) {
            ItemElectricTool tool = (ItemElectricTool)item;
            return block.hasTag(BlockTags.MINEABLE_BY_PICKAXE) || block.hasTag(BlockTags.MINEABLE_BY_AXE);
        }
        return false;
    }

    public boolean canWithdraw() {
        Block below = this.worldObj.getBlock(this.tilePos.x(), this.tilePos.y() - 1, this.tilePos.z());
        return below == IC2Blocks.miningPipe || below == IC2Blocks.miningTip;
    }

    public int gaugeEnergyScaled(int i) {
        return this.energy * i / this.maxEnergy;
    }

    @Override
    public String getMachineName() {
        return "Miner";
    }

    @Override
    public int[] getQuickGrabSlots() {
        return new int[]{3};
    }
}

