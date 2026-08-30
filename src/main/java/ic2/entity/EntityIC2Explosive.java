

package ic2.entity;

import com.mojang.nbt.tags.CompoundTag;
import ic2.IC2Blocks;
import ic2.util.ExplosionIC2;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class EntityIC2Explosive
extends Entity {
    public int fuse = 80;
    public float explosivePower = 4.0f;
    public float dropRate = 0.3f;
    public float damageVsEntitys = 1.0f;
    public Block<?> renderBlock;

    public EntityIC2Explosive(World world) {
        super(world);
        this.blocksBuilding = true;
        this.setSize(0.98f, 0.98f);
        this.heightOffset = this.bbHeight / 2.0f;
    }

    public EntityIC2Explosive(World world, double x, double y, double z, int fuseLength, float power, float rate, float damage, Block<?> block) {
        this(world);
        this.setPos(x, y, z);
        float f = (float)(Math.random() * Math.PI * 2.0);
        this.xd = -MathHelper.sin((float)(f * (float)Math.PI / 180.0f)) * 0.02f;
        this.yd = 0.2;
        this.zd = -MathHelper.cos((float)(f * (float)Math.PI / 180.0f)) * 0.02f;
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.fuse = fuseLength;
        this.explosivePower = power;
        this.dropRate = rate;
        this.damageVsEntitys = damage;
        this.renderBlock = block;
    }

    protected void defineSynchedData() {
    }

    protected boolean makeStepSound() {
        return false;
    }

    public boolean isPickable() {
        return !this.removed;
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.yd -= 0.04;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.98;
        this.yd *= 0.98;
        this.zd *= 0.98;
        if (this.onGround) {
            this.xd *= 0.7;
            this.zd *= 0.7;
            this.yd *= -0.5;
        }
        if (this.fuse-- <= 0) {
            if (!this.world.isClientSide) {
                this.remove();
                this.explode();
            } else {
                this.remove();
            }
        } else {
            this.world.spawnParticle("smoke", this.x, this.y + 0.5, this.z, 0.0, 0.0, 0.0, 0, false);
        }
    }

    private void explode() {
        ExplosionIC2 explosion = new ExplosionIC2(this.world, null, this.x, this.y, this.z, this.explosivePower, this.dropRate, this.damageVsEntitys);
        explosion.explode();
        explosion.addEffects(true);
    }

    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        tag.putByte("Fuse", (byte)this.fuse);
        tag.putFloat("Power", this.explosivePower);
        tag.putFloat("DropRate", this.dropRate);
        tag.putFloat("Damage", this.damageVsEntitys);
    }

    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        this.fuse = tag.getByte("Fuse");
        if (tag.containsKey("Power")) {
            this.explosivePower = tag.getFloat("Power");
        }
        if (tag.containsKey("DropRate")) {
            this.dropRate = tag.getFloat("DropRate");
        }
        if (tag.containsKey("Damage")) {
            this.damageVsEntitys = tag.getFloat("Damage");
        }
        if (this.renderBlock == null) {
            this.renderBlock = IC2Blocks.industrialTnt;
        }
    }

    public float getShadowHeightOffs() {
        return 0.0f;
    }
}

