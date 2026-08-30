

package ic2.entity;

import com.mojang.nbt.tags.CompoundTag;
import ic2.util.PointExplosion;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.HitResult;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class EntityDynamite
extends Entity {
    public boolean sticky = false;
    public int stickX;
    public int stickY;
    public int stickZ;
    public int fuse = 100;
    private boolean inGround = false;
    public boolean doesDynamiteBelongToPlayer = false;
    public Mob owner;
    private int ticksInGround;
    private int ticksInAir = 0;

    public EntityDynamite(World world) {
        super(world);
        this.setSize(0.5f, 0.5f);
    }

    public EntityDynamite(World world, double d, double d1, double d2, boolean sticky) {
        super(world);
        this.setSize(0.5f, 0.5f);
        this.setPos(d, d1, d2);
        this.heightOffset = 0.0f;
        this.sticky = sticky;
    }

    public EntityDynamite(World world, Mob entityliving, boolean sticky) {
        super(world);
        this.owner = entityliving;
        this.doesDynamiteBelongToPlayer = entityliving instanceof Player;
        this.setSize(0.5f, 0.5f);
        this.setPos(entityliving.x, entityliving.y + (double)entityliving.getHeadHeight(), entityliving.z);
        this.xd = 0.0;
        this.heightOffset = 0.0f;
        double mx = -((double)(MathHelper.cos((float)(entityliving.yRot / 180.0f * 3.141593f)) * 0.16f));
        double mz = MathHelper.sin((float)(entityliving.yRot / 180.0f * 3.141593f)) * 0.16f;
        this.setPos(this.x + mx, this.y - 0.1, this.z + mz);
        double pitchRad = entityliving.xRot / 180.0f * 3.141593f;
        double yawRad = entityliving.yRot / 180.0f * 3.141593f;
        double motionX = -MathHelper.sin((float)((float)yawRad)) * MathHelper.cos((float)((float)pitchRad));
        double motionY = -MathHelper.sin((float)((float)pitchRad));
        double motionZ = MathHelper.cos((float)((float)yawRad)) * MathHelper.cos((float)((float)pitchRad));
        this.setDynamiteHeading(motionX, motionY, motionZ, 1.0f, 1.0f);
        this.sticky = sticky;
    }

    protected void defineSynchedData() {
    }

    public void setDynamiteHeading(double d, double d1, double d2, float f, float f1) {
        float f2 = MathHelper.sqrt((double)((float)(d * d + d1 * d1 + d2 * d2)));
        d /= (double)f2;
        d1 /= (double)f2;
        d2 /= (double)f2;
        d += this.random.nextGaussian() * (double)0.0075f * (double)f1;
        d1 += this.random.nextGaussian() * (double)0.0075f * (double)f1;
        d2 += this.random.nextGaussian() * (double)0.0075f * (double)f1;
        this.xd = d *= (double)f;
        this.yd = d1 *= (double)f;
        this.zd = d2 *= (double)f;
        float f3 = MathHelper.sqrt((double)((float)(d * d + d2 * d2)));
        this.yRotO = this.yRot = (float)(Math.atan2(d, d2) * 180.0 / 3.1415927410125732);
        this.xRotO = this.xRot = (float)(Math.atan2(d1, f3) * 180.0 / 3.1415927410125732);
        this.ticksInGround = 0;
    }

    public void tick() {
        super.tick();
        if (this.xRotO == 0.0f && this.yRotO == 0.0f) {
            float f = MathHelper.sqrt((double)((float)(this.xd * this.xd + this.zd * this.zd)));
            this.yRotO = this.yRot = (float)(Math.atan2(this.xd, this.zd) * 180.0 / 3.1415927410125732);
            this.xRotO = this.xRot = (float)(Math.atan2(this.yd, f) * 180.0 / 3.1415927410125732);
        }
        if (this.fuse-- <= 0) {
            if (!this.world.isClientSide) {
                this.remove();
                this.explode();
            } else {
                this.remove();
            }
        } else if (this.fuse < 100 && this.fuse % 2 == 0) {
            this.world.spawnParticle("smoke", this.x, this.y + 0.5, this.z, 0.0, 0.0, 0.0, 0, false);
        }
        if (this.inGround) {
            ++this.ticksInGround;
            if (this.ticksInGround >= 200) {
                this.remove();
            }
            if (this.sticky) {
                this.fuse -= 3;
                this.xd = 0.0;
                this.yd = 0.0;
                this.zd = 0.0;
                if (this.world.getBlockId(this.stickX, this.stickY, this.stickZ) != 0) {
                    return;
                }
            }
        }
        ++this.ticksInAir;
        Vector3d oldPos = new Vector3d(this.x, this.y, this.z);
        Vector3d newPos = new Vector3d(this.x + this.xd, this.y + this.yd, this.z + this.zd);
        HitResult movingobjectposition = this.world.checkBlockCollisionBetweenPoints((Vector3dc)oldPos, (Vector3dc)newPos, false, true, false);
        if (movingobjectposition != null && movingobjectposition instanceof HitResult.Tile) {
            HitResult.Tile tileHit = (HitResult.Tile)movingobjectposition;
            this.stickX = tileHit.tilePos.x();
            this.stickY = tileHit.tilePos.y();
            this.stickZ = tileHit.tilePos.z();
            double hx = movingobjectposition.location.x() - this.x;
            double hy = movingobjectposition.location.y() - this.y;
            double hz = movingobjectposition.location.z() - this.z;
            float dist = MathHelper.sqrt((double)((float)(hx * hx + hy * hy + hz * hz)));
            this.x -= hx / (double)dist * 0.05;
            this.y -= hy / (double)dist * 0.05;
            this.z -= hz / (double)dist * 0.05;
            this.x += hx;
            this.y += hy;
            this.z += hz;
            this.xd *= (double)(0.75f - this.random.nextFloat());
            this.yd *= (double)-0.3f;
            this.zd *= (double)(0.75f - this.random.nextFloat());
            this.inGround = true;
        } else {
            this.x += this.xd;
            this.y += this.yd;
            this.z += this.zd;
            this.inGround = false;
        }
        float f2 = MathHelper.sqrt((double)((float)(this.xd * this.xd + this.zd * this.zd)));
        this.yRot = (float)(Math.atan2(this.xd, this.zd) * 180.0 / 3.1415927410125732);
        this.xRot = (float)(Math.atan2(this.yd, f2) * 180.0 / 3.1415927410125732);
        float f3 = 0.98f;
        float f5 = 0.04f;
        if (this.isInWater()) {
            this.fuse += 2000;
            for (int i1 = 0; i1 < 4; ++i1) {
                float f6 = 0.25f;
                this.world.spawnParticle("bubble", this.x - this.xd * (double)f6, this.y - this.yd * (double)f6, this.z - this.zd * (double)f6, this.xd, this.yd, this.zd, 0, false);
            }
            f3 = 0.75f;
        }
        this.xd *= (double)f3;
        this.yd *= (double)f3;
        this.zd *= (double)f3;
        this.yd -= (double)f5;
        this.setPos(this.x, this.y, this.z);
    }

    public void explode() {
        PointExplosion explosion = new PointExplosion(this.world, null, (int)this.x, (int)this.y, (int)this.z, 1.0f, 1.0f, 0.8f);
        explosion.explode(1, 1, 1, 1, 1, 1);
        explosion.addEffects(true);
    }

    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        tag.putByte("inGround", (byte)(this.inGround ? 1 : 0));
        tag.putBoolean("player", this.doesDynamiteBelongToPlayer);
    }

    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        this.inGround = tag.getByte("inGround") == 1;
        this.doesDynamiteBelongToPlayer = tag.getBoolean("player");
    }

    public float getShadowHeightOffs() {
        return 0.0f;
    }

    protected boolean makeStepSound() {
        return false;
    }
}

