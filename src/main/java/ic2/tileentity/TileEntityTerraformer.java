

package ic2.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import ic2.IC2Blocks;
import ic2.energy.Direction;
import ic2.item.ITerraformingBP;
import ic2.item.ItemTFBP;
import ic2.tileentity.TileEntityElecMachine;
import java.util.Random;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.world.World;

public class TileEntityTerraformer
extends TileEntityElecMachine {
    public int soundTicker = new Random().nextInt(64);
    public int failedAttempts = 0;
    public int lastX = -1;
    public int lastY = -1;
    public int lastZ = -1;

    public TileEntityTerraformer() {
        super(1, 0, 100000, 512);
    }

    @Override
    public void readAdditionalData(CompoundTag tag) {
        super.readAdditionalData(tag);
        this.failedAttempts = tag.getInteger("failedAttempts");
        this.lastX = tag.getInteger("lastX");
        this.lastY = tag.getInteger("lastY");
        this.lastZ = tag.getInteger("lastZ");
    }

    @Override
    public void writeAdditionalData(CompoundTag tag) {
        super.writeAdditionalData(tag);
        tag.putInt("failedAttempts", this.failedAttempts);
        tag.putInt("lastX", this.lastX);
        tag.putInt("lastY", this.lastY);
        tag.putInt("lastZ", this.lastZ);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.worldObj.isClientSide) {
            return;
        }
        ITerraformingBP tfbp = ItemTFBP.of(this.inventory[0]);
        if (tfbp == null) {
            return;
        }
        if (this.energy < tfbp.getConsume()) {
            return;
        }
        ++this.soundTicker;
        if (this.soundTicker % 64 == 0) {
            this.worldObj.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, (double)this.tilePos.x() + 0.5, (double)this.tilePos.y() + 0.5, (double)this.tilePos.z() + 0.5, "random.fizz", 1.0f, 1.0f);
        }
        int x = this.tilePos.x();
        int z = this.tilePos.z();
        if (this.lastY > -1) {
            int range = tfbp.getRange() / 10;
            x = this.lastX - this.worldObj.rand.nextInt(range + 1) + this.worldObj.rand.nextInt(range + 1);
            z = this.lastZ - this.worldObj.rand.nextInt(range + 1) + this.worldObj.rand.nextInt(range + 1);
        } else {
            if (this.failedAttempts > 4) {
                this.failedAttempts = 4;
            }
            int range = tfbp.getRange() * (this.failedAttempts + 1) / 5;
            x = x - this.worldObj.rand.nextInt(range + 1) + this.worldObj.rand.nextInt(range + 1);
            z = z - this.worldObj.rand.nextInt(range + 1) + this.worldObj.rand.nextInt(range + 1);
        }
        if (tfbp.terraform(this.worldObj, x, z, this.tilePos.y())) {
            this.energy -= tfbp.getConsume();
            this.failedAttempts = 0;
            this.lastX = x;
            this.lastZ = z;
            this.lastY = this.tilePos.y();
        } else {
            this.energy -= tfbp.getConsume() / 10;
            ++this.failedAttempts;
            this.lastY = -1;
        }
        this.setChanged();
    }

    @Override
    public int injectEnergy(Direction directionFrom, int amount) {
        if (amount > 512 && !ic2.IC2Config.voltageSystemOff()) {
            IC2Blocks.explodeMachineAt(this.worldObj, this.tilePos.x(), this.tilePos.y(), this.tilePos.z());
            return 0;
        }
        this.energy += amount;
        int re = 0;
        if (this.energy > this.maxEnergy) {
            re = this.energy - this.maxEnergy;
            this.energy = this.maxEnergy;
        }
        return re;
    }

    public boolean ejectBlueprint() {
        if (this.inventory[0] == null) {
            return false;
        }
        EntityItem drop = new EntityItem(this.worldObj, (double)this.tilePos.x() + 0.5, (double)this.tilePos.y() + 1.0, (double)this.tilePos.z() + 0.5, this.inventory[0]);
        if (!this.worldObj.isClientSide) {
            this.worldObj.entityJoinedWorld((Entity)drop);
        }
        this.inventory[0] = null;
        return true;
    }

    @Override
    public String getMachineName() {
        return "Terraformer";
    }

    public static int getFirstBlockFrom(World world, int x, int z, int y) {
        while (y > 0) {
            if (world.getBlock(x, y, z) != Blocks.AIR) {
                return y;
            }
            --y;
        }
        return -1;
    }

    public static int getFirstSolidBlockFrom(World world, int x, int z, int y) {
        while (y > 0) {
            Block block = world.getBlock(x, y, z);
            if (block != null && block != Blocks.AIR && block.getMaterial().isSolid()) {
                return y;
            }
            --y;
        }
        return -1;
    }

    public static boolean switchGround(World world, Block<?> from, Block<?> to, int x, int y, int z, boolean upwards) {
        if (upwards) {
            int saveY = ++y;
            while (world.getBlock(x, y - 1, z) == from) {
                --y;
            }
            if (saveY == y) {
                return false;
            }
            world.setBlockWithNotify(x, y, z, to.id());
            return true;
        }
        while (world.getBlock(x, y, z) == to) {
            --y;
        }
        if (world.getBlock(x, y, z) != from) {
            return false;
        }
        world.setBlockWithNotify(x, y, z, to.id());
        return true;
    }
}

