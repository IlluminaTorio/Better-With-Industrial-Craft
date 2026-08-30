

package ic2.entity;

import com.mojang.nbt.tags.CompoundTag;
import ic2.IC2Blocks;
import ic2.util.ExplosionIC2;
import java.util.List;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.HitResult;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;

public class EntityMiningLaser
extends Entity {
    public float range = 8.0f;
    public boolean explosive = false;
    public double startX = 0.0;
    public double startY = 0.0;
    public double startZ = 0.0;
    public boolean doesLaserBelongToPlayer = false;
    public Mob owner;
    private int ticksInAir = 0;

    public EntityMiningLaser(World world) {
        super(world);
        this.setSize(0.5f, 0.5f);
    }

    public EntityMiningLaser(World world, Mob entityliving, float r, boolean ex) {
        super(world);
        this.owner = entityliving;
        this.doesLaserBelongToPlayer = entityliving instanceof Player;
        this.setSize(0.8f, 0.8f);
        this.setPos(entityliving.x, entityliving.y + (double)entityliving.getHeadHeight(), entityliving.z);
        double mx = -((double)(MathHelper.cos((float)(entityliving.yRot / 180.0f * 3.141593f)) * 0.16f));
        double mz = MathHelper.sin((float)(entityliving.yRot / 180.0f * 3.141593f)) * 0.16f;
        this.setPos(this.x + mx, this.y - 0.1, this.z + mz);
        double yawRad = entityliving.yRot / 180.0f * 3.141593f;
        double pitchRad = entityliving.xRot / 180.0f * 3.141593f;
        double motionX = -Math.sin(yawRad) * Math.cos(pitchRad);
        double motionY = -Math.sin(pitchRad);
        double motionZ = Math.cos(yawRad) * Math.cos(pitchRad);
        this.setLaserHeading(motionX, motionY, motionZ, 1.0f);
        this.range = r;
        this.explosive = ex;
        this.startX = this.x;
        this.startY = this.y;
        this.startZ = this.z;
    }

    public EntityMiningLaser(World world, Mob entityliving, float r, boolean ex, float rotX, float rotY) {
        this(world, entityliving, r, ex);
        this.yRot = rotX;
        this.xRot = rotY;
        double yawRad = rotX / 180.0f * 3.141593f;
        double pitchRad = rotY / 180.0f * 3.141593f;
        this.setLaserHeading(-Math.sin(yawRad) * Math.cos(pitchRad), -Math.sin(pitchRad), Math.cos(yawRad) * Math.cos(pitchRad), 1.0f);
    }

    protected void defineSynchedData() {
    }

    public void setLaserHeading(double d, double d1, double d2, float f) {
        float f2 = MathHelper.sqrt((double)((float)(d * d + d1 * d1 + d2 * d2)));
        d /= (double)f2;
        d1 /= (double)f2;
        d2 /= (double)f2;
        this.xd = d *= (double)f;
        this.yd = d1 *= (double)f;
        this.zd = d2 *= (double)f;
        float f3 = MathHelper.sqrt((double)((float)(d * d + d2 * d2)));
        this.yRot = (float)(Math.atan2(d, d2) * 180.0 / 3.1415927410125732);
        this.xRot = (float)(Math.atan2(d1, f3) * 180.0 / 3.1415927410125732);
    }

    public void tick() {
        super.tick();
        if (Math.sqrt(this.distanceToSqr(this.startX, this.startY, this.startZ)) > (double)this.range) {
            if (this.explosive) {
                this.explode();
            }
            this.remove();
            return;
        }
        ++this.ticksInAir;
        Vector3d start = new Vector3d(this.x, this.y, this.z);
        Vector3d end = new Vector3d(this.x + this.xd, this.y + this.yd, this.z + this.zd);
        HitResult hit = this.world.checkBlockCollisionBetweenPoints((Vector3dc)start, (Vector3dc)end, false, true, false);
        AABBd sweep = new AABBd(Math.min(start.x, end.x) - 0.5, Math.min(start.y, end.y) - 0.5, Math.min(start.z, end.z) - 0.5, Math.max(start.x, end.x) + 0.5, Math.max(start.y, end.y) + 0.5, Math.max(start.z, end.z) + 0.5);
        List entities = this.world.getEntitiesWithinAABB(Mob.class, (AABBdc)sweep);
        Mob hitEntity = null;
        double nearest = 0.0;
        for (Object mobObj : entities) {
            Mob mob = (Mob)mobObj;
            if (!mob.isAlive() || mob == this.owner && this.ticksInAir < 5) continue;
            double dist = start.distanceSquared((Vector3dc)new Vector3d(mob.x, mob.y + (double)(mob.bbHeight / 2.0f), mob.z));
            if (hitEntity != null && !(dist < nearest)) continue;
            hitEntity = mob;
            nearest = dist;
        }
        if (hitEntity != null) {
            Player p;
            Mob dist;
            if (this.explosive) {
                this.explode();
                this.remove();
                return;
            }
            int damage = (int)Math.sqrt((double)(this.range * this.range) - this.distanceToSqr(this.startX, this.startY, this.startZ));
            if (damage < 1) {
                damage = 0;
            }
            hitEntity.hurt((Entity)((dist = this.owner) instanceof Player ? (p = (Player)dist) : null), damage, DamageType.COMBAT);
            this.remove();
            return;
        }
        if (hit instanceof HitResult.Tile) {
            int z;
            int y;
            HitResult.Tile tileHit = (HitResult.Tile)hit;
            if (this.explosive) {
                this.explode();
                this.remove();
                return;
            }
            int x = tileHit.tilePos.x();
            if (!this.canMine(x, y = tileHit.tilePos.y(), z = tileHit.tilePos.z())) {
                this.remove();
                return;
            }
            Block block = this.world.getBlockType(tileHit.tilePos);
            if (block == null || block == Blocks.AIR) {
                this.remove();
                return;
            }
            float resis = block.getBlastResistance((Entity)this) + 0.3f;
            this.range -= resis / 6.3f;
            int data = this.world.getBlockData(tileHit.tilePos);
            block.getLogic().dropWithCause(this.world, EnumDropCause.EXPLOSION, tileHit.tilePos, data, this.world.getTileEntity(tileHit.tilePos), null);
            this.world.setBlockTypeNotify(tileHit.tilePos, Blocks.AIR);
            Material material = block.getMaterial();
            if (this.world.rand.nextInt(10) == 0 && (material == Materials.WOOD || material == Materials.PLANT || material == Materials.LEAVES)) {
                this.world.setBlockTypeNotify(tileHit.tilePos, Blocks.FIRE);
            }
        }
        this.x += this.xd;
        this.y += this.yd;
        this.z += this.zd;
        this.setPos(this.x, this.y, this.z);
        if (this.isAlive() && this.ticksInAir > 400) {
            this.remove();
        }
    }

    public void explode() {
        ExplosionIC2 explosion = new ExplosionIC2(this.world, null, this.x, this.y, this.z, 2.0f, 0.85f, 0.55f);
        explosion.explode();
        explosion.addEffects(true);
    }

    public boolean canMine(int x, int y, int z) {
        Block block = this.world.getBlock(x, y, z);
        return block != Blocks.OBSIDIAN && block != Blocks.BEDROCK && block != IC2Blocks.reinforcedStone && block != IC2Blocks.reinforcedGlass && block != IC2Blocks.personalSafe && block != IC2Blocks.reinforcedDoorBottom && block != IC2Blocks.reinforcedDoorTop && block != IC2Blocks.tradeOMat;
    }

    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        tag.putBoolean("player", this.doesLaserBelongToPlayer);
    }

    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        this.doesLaserBelongToPlayer = tag.getBoolean("player");
    }

    public float getShadowHeightOffs() {
        return 0.0f;
    }

    protected boolean makeStepSound() {
        return false;
    }
}

