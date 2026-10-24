

package ic2.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import ic2.energy.Direction;
import ic2.energy.IEnergySink;
import ic2.tileentity.TileEntityIC2Machine;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.DamageType;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;

public class TileEntityTesla
extends TileEntityIC2Machine
implements IEnergySink {
    public int energy = 0;
    public static final int ENERGY_PER_ATTACK = 400;
    public static final int MAX_ENERGY = 4000;

    public TileEntityTesla() {
        super(0);
    }

    @Override
    public void readAdditionalData(CompoundTag tag) {
        super.readAdditionalData(tag);
        this.energy = tag.getInteger("energy");
    }

    @Override
    public void writeAdditionalData(CompoundTag tag) {
        super.writeAdditionalData(tag);
        tag.putInt("energy", this.energy);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.worldObj.isClientSide) {
            return;
        }
        if (this.energy >= 400) {
            AABBd box = new AABBd((double)(this.tilePos.x() - 4), (double)(this.tilePos.y() - 4), (double)(this.tilePos.z() - 4), (double)(this.tilePos.x() + 5), (double)(this.tilePos.y() + 5), (double)(this.tilePos.z() + 5));
            for (Mob mob : this.worldObj.getEntitiesWithinAABB(Mob.class, (AABBdc)box)) {
                if (mob instanceof Player) continue;
                this.energy -= 400;
                mob.hurt(null, 8, DamageType.COMBAT);
                this.worldObj.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, mob.x, mob.y, mob.z, "random.fizz", 0.7f, 1.2f);
                break;
            }
        }
    }

    @Override
    public boolean demandsEnergy() {
        return this.energy < 4000;
    }

    @Override
    public int injectEnergy(Direction direction, int amount) {
        int space = 4000 - this.energy;
        if (space >= amount) {
            this.energy += amount;
            return 0;
        }
        this.energy = 4000;
        return amount - space;
    }

    @Override
    public boolean acceptsEnergyFrom(TileEntity emitter, Direction direction) {
        return true;
    }

    @Override
    public String getMachineName() {
        return "Tesla Coil";
    }
}

