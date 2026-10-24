package ic2.tileentity;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.pos.TilePos;

public class TileEntityHarvester
extends TileEntityElecMachine {
    public static final int RADIUS = 4;
    public static final int COST_PER_CROP = 200;
    public static final int INTERVAL = 20;
    public static final int SLOT_ENERGY = 9;

    public TileEntityHarvester() {
        super(10, TileEntityHarvester.SLOT_ENERGY, 20000, 128);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.worldObj.isClientSide) {
            return;
        }
        boolean worked = false;
        if (this.worldObj.getTotalWorldTime() % (long)TileEntityHarvester.INTERVAL == 0L && this.energy >= TileEntityHarvester.COST_PER_CROP) {
            worked = this.harvestOne();
        }
        if (this.active != worked) {
            this.active = worked;
            this.worldObj.notifyBlockChange(this.tilePos, this.getBlock());
        }
    }

    private boolean harvestOne() {
        for (int dy = 0; dy >= -1; --dy) {
            for (int dx = -TileEntityHarvester.RADIUS; dx <= TileEntityHarvester.RADIUS; ++dx) {
                for (int dz = -TileEntityHarvester.RADIUS; dz <= TileEntityHarvester.RADIUS; ++dz) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    TilePos target = this.tilePos.add(dx, dy, dz, new TilePos());
                    if (this.harvestAt(target)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean harvestAt(TilePos target) {
        Block<?> block = this.worldObj.getBlockType(target);
        if (block == Blocks.CROPS_WHEAT) {
            int data = this.worldObj.getBlockData(target);
            if (data < 7) {
                return false;
            }
            ItemStack[] drops = block.getLogic().getBreakResult(this.worldObj, EnumDropCause.PROPER_TOOL, data, null);
            this.worldObj.setBlockDataNotify(target, 0);
            this.insertDrops(drops);
            this.energy -= TileEntityHarvester.COST_PER_CROP;
            this.setChanged();
            return true;
        }
        if (block == Blocks.PUMPKIN) {
            int data = this.worldObj.getBlockData(target);
            ItemStack[] drops = block.getLogic().getBreakResult(this.worldObj, EnumDropCause.PROPER_TOOL, data, null);
            this.worldObj.setBlockTypeNotify(target, Blocks.CROPS_PUMPKIN);
            this.worldObj.setBlockDataNotify(target, 0);
            this.insertDrops(drops);
            this.energy -= TileEntityHarvester.COST_PER_CROP;
            this.setChanged();
            return true;
        }
        return false;
    }

    private void insertDrops(ItemStack[] drops) {
        if (drops == null) {
            return;
        }
        for (ItemStack drop : drops) {
            if (drop == null || drop.stackSize <= 0) continue;
            for (int i = 0; i < TileEntityHarvester.SLOT_ENERGY; ++i) {
                ItemStack cur = this.inventory[i];
                if (cur == null || cur.itemID != drop.itemID || cur.getMetadata() != drop.getMetadata() || cur.stackSize + drop.stackSize > cur.getMaxStackSize()) continue;
                cur.stackSize += drop.stackSize;
                drop.stackSize = 0;
                break;
            }
            if (drop.stackSize <= 0) continue;
            for (int i = 0; i < TileEntityHarvester.SLOT_ENERGY; ++i) {
                if (this.inventory[i] != null) continue;
                this.inventory[i] = drop.copy();
                drop.stackSize = 0;
                break;
            }
            if (drop.stackSize <= 0) continue;
            EntityItem entity = new EntityItem(this.worldObj, (double)this.tilePos.x() + 0.5, (double)this.tilePos.y() + 0.5, (double)this.tilePos.z() + 0.5, drop);
            this.worldObj.entityJoinedWorld((Entity)entity);
        }
    }

    @Override
    public int[] getQuickGrabSlots() {
        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < TileEntityHarvester.SLOT_ENERGY; ++i) {
            slots.add(i);
        }
        int[] out = new int[slots.size()];
        for (int i = 0; i < out.length; ++i) {
            out[i] = slots.get(i);
        }
        return out;
    }

    @Override
    public String getMachineName() {
        return "Harvester";
    }
}
