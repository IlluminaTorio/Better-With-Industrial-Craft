

package ic2.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import ic2.IC2Blocks;
import ic2.tileentity.TileEntityElectricBlock;
import ic2.tileentity.TileEntityIC2Machine;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;

public class TileEntityTeleporter
extends TileEntityIC2Machine {
    public static final List<TileEntityTeleporter> TELEPORTERS = new ArrayList<TileEntityTeleporter>();
    public int targetFreq = -1;
    public int ownFreq = new Random().nextInt(32000);
    public boolean registered = false;

    public TileEntityTeleporter() {
        super(0);
    }

    @Override
    public void readAdditionalData(CompoundTag tag) {
        super.readAdditionalData(tag);
        this.targetFreq = tag.getInteger("targetFreq");
        this.ownFreq = tag.getInteger("ownFreq");
    }

    @Override
    public void writeAdditionalData(CompoundTag tag) {
        super.writeAdditionalData(tag);
        tag.putInt("targetFreq", this.targetFreq);
        tag.putInt("ownFreq", this.ownFreq);
    }

    public boolean setFrequency(ItemStack trans, Player player) {
        int freq = trans.getMetadata();
        if (freq == 0 || freq == this.ownFreq || freq == this.targetFreq) {
            return false;
        }
        this.targetFreq = freq;
        if (player != null) {
            player.sendMessage(TextFormatting.Base.GRAY, "Teleporter target frequency set to: " + this.targetFreq);
        }
        return true;
    }

    public void getFrequency(ItemStack trans, Player player) {
        int freq = trans.getMetadata();
        if (freq == this.ownFreq) {
            return;
        }
        trans.setMetadata(this.ownFreq);
        if (player != null) {
            player.sendMessage(TextFormatting.Base.GRAY, "Transmitter frequency set to: " + this.ownFreq);
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (this.registered) {
            TELEPORTERS.remove((Object)this);
            this.registered = false;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.worldObj.isClientSide) {
            return;
        }
        if (!this.registered) {
            if (!TileEntityTeleporter.register(this)) {
                IC2Blocks.explodeMachineAt(this.worldObj, this.tilePos.x(), this.tilePos.y(), this.tilePos.z());
            } else {
                this.registered = true;
            }
            return;
        }
        if (this.targetFreq == -1 || !this.redstoned()) {
            return;
        }
        int[] coords = TileEntityTeleporter.getCoords(this.targetFreq);
        if (coords == null) {
            return;
        }
        int x = this.tilePos.x();
        int y = this.tilePos.y();
        int z = this.tilePos.z();
        AABBd box = new AABBd((double)(x - 1), (double)y, (double)(z - 1), (double)(x + 2), (double)(y + 2), (double)(z + 2));
        List list = this.worldObj.getEntitiesWithinAABB(Mob.class, (AABBdc)box);
        Mob user = null;
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            Mob m;
            user = m = (Mob)iterator.next();
        }
        if (user == null) {
            return;
        }
        this.teleport(user, new int[]{x, y, z}, coords);
    }

    public void teleport(Mob user, int[] thisCoords, int[] targetCoords) {
        int[] tele = new int[]{targetCoords[0] - thisCoords[0], targetCoords[1] - thisCoords[1], targetCoords[2] - thisCoords[2]};
        double distance = Math.sqrt(Math.abs(tele[0]) * Math.abs(tele[0]) + Math.abs(tele[1]) * Math.abs(tele[1]) + Math.abs(tele[2]) * Math.abs(tele[2]));
        int weight = this.getWeightOf(user);
        if ((double)weight * distance > (double)this.getAvailableEnergy()) {
            return;
        }
        this.consumeEnergy((int)((double)weight * distance));
        this.worldObj.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, user.x, user.y, user.z, "portal.portal", 1.0f, 1.0f);
        user.setPos(user.x + (double)tele[0], user.y + (double)tele[1], user.z + (double)tele[2]);
        user.fallDistance = 0.0f;
        this.worldObj.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, user.x, user.y, user.z, "portal.portal", 1.0f, 1.0f);
        this.setChanged();
    }

    public void consumeEnergy(int energy) {
        int z;
        int y;
        int x = this.tilePos.x();
        int number = this.isElectricBlock(x, (y = this.tilePos.y()) - 1, z = this.tilePos.z()) + this.isElectricBlock(x + 1, y, z) + this.isElectricBlock(x - 1, y, z) + this.isElectricBlock(x, y, z + 1) + this.isElectricBlock(x, y, z - 1);
        if (number == 0) {
            return;
        }
        int gained = 0;
        gained += this.drainFrom(x, y - 1, z, energy / number);
        gained += this.drainFrom(x + 1, y, z, energy / number);
        gained += this.drainFrom(x - 1, y, z, energy / number);
        gained += this.drainFrom(x, y, z + 1, energy / number);
        if ((energy -= (gained += this.drainFrom(x, y, z - 1, energy / number))) <= 0 || energy < number) {
            return;
        }
        this.consumeEnergy(energy);
    }

    public int drainFrom(int x, int y, int z, int need) {
        if (this.isElectricBlock(x, y, z) == 0) {
            return 0;
        }
        TileEntityElectricBlock te = (TileEntityElectricBlock)this.worldObj.getTileEntity((TilePosc)new TilePos(x, y, z));
        if (need > te.energy) {
            need = te.energy;
        }
        te.energy -= need;
        te.setChanged();
        return need;
    }

    public int getAvailableEnergy() {
        int z;
        int y;
        int e = 0;
        int x = this.tilePos.x();
        if (this.isElectricBlock(x, (y = this.tilePos.y()) - 1, z = this.tilePos.z()) > 0) {
            e += ((TileEntityElectricBlock)this.worldObj.getTileEntity((TilePosc)new TilePos((int)x, (int)(y - 1), (int)z))).energy;
        }
        if (this.isElectricBlock(x + 1, y, z) > 0) {
            e += ((TileEntityElectricBlock)this.worldObj.getTileEntity((TilePosc)new TilePos((int)(x + 1), (int)y, (int)z))).energy;
        }
        if (this.isElectricBlock(x - 1, y, z) > 0) {
            e += ((TileEntityElectricBlock)this.worldObj.getTileEntity((TilePosc)new TilePos((int)(x - 1), (int)y, (int)z))).energy;
        }
        if (this.isElectricBlock(x, y, z + 1) > 0) {
            e += ((TileEntityElectricBlock)this.worldObj.getTileEntity((TilePosc)new TilePos((int)x, (int)y, (int)(z + 1)))).energy;
        }
        if (this.isElectricBlock(x, y, z - 1) > 0) {
            e += ((TileEntityElectricBlock)this.worldObj.getTileEntity((TilePosc)new TilePos((int)x, (int)y, (int)(z - 1)))).energy;
        }
        return e;
    }

    public int isElectricBlock(int x, int y, int z) {
        TileEntity tileEntity = this.worldObj.getTileEntity((TilePosc)new TilePos(x, y, z));
        if (tileEntity instanceof TileEntityElectricBlock) {
            TileEntityElectricBlock block = (TileEntityElectricBlock)tileEntity;
            if (block.energy > 0) {
                return 1;
            }
        }
        return 0;
    }

    public int getWeightOf(Mob user) {
        if (user instanceof Player) {
            Player player = (Player)user;
            int w = 1000;
            for (ItemStack stack : player.inventory.mainInventory) {
                if (stack == null) continue;
                w += 100 * stack.stackSize / stack.getMaxStackSize();
            }
            for (ItemStack stack : player.inventory.armorInventory) {
                if (stack == null) continue;
                w += 100;
            }
            return w;
        }
        return 100;
    }

    public static boolean register(TileEntityTeleporter tele) {
        for (TileEntityTeleporter t : TELEPORTERS) {
            if (t.ownFreq != tele.ownFreq) continue;
            return false;
        }
        TELEPORTERS.add(tele);
        return true;
    }

    public static int[] getCoords(int freq) {
        for (TileEntityTeleporter t : TELEPORTERS) {
            if (t.ownFreq != freq) continue;
            return new int[]{t.tilePos.x(), t.tilePos.y(), t.tilePos.z()};
        }
        return null;
    }

    public boolean redstoned() {
        return this.worldObj.hasNeighborSignal((TilePosc)this.tilePos);
    }

    @Override
    public String getMachineName() {
        return "Teleporter";
    }
}

