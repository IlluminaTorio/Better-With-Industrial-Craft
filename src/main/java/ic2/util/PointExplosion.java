

package ic2.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;

public class PointExplosion {
    private final World worldObj;
    public int explosionX;
    public int explosionY;
    public int explosionZ;
    public final Entity exploder;
    public final float explosionSize;
    public final float explosionDropRate;
    public final float explosionDamage;
    public final Set<TilePos3> destroyedBlockPositions = new HashSet<TilePos3>();

    public PointExplosion(World world, Entity entity, int x, int y, int z, float power, float drop, float entitydamage) {
        this.worldObj = world;
        this.exploder = entity;
        this.explosionSize = power;
        this.explosionDropRate = drop;
        this.explosionDamage = entitydamage;
        this.explosionX = x;
        this.explosionY = y;
        this.explosionZ = z;
        if (this.explosionX < 0) {
            --this.explosionX;
        }
        if (this.explosionZ < 0) {
            --this.explosionZ;
        }
    }

    public void explode(int lowX, int lowY, int lowZ, int highX, int highY, int highZ) {
        for (int x = this.explosionX - lowX; x <= this.explosionX + highX; ++x) {
            for (int y = this.explosionY - lowY; y <= this.explosionY + highY; ++y) {
                for (int z = this.explosionZ - lowZ; z <= this.explosionZ + highZ; ++z) {
                    float resis;
                    Block block = this.worldObj.getBlockType((TilePosc)new TilePos(x, y, z));
                    float f = resis = block == Blocks.AIR ? 0.0f : block.getBlastResistance(this.exploder);
                    if (this.explosionSize < resis / 10.0f) continue;
                    this.destroyedBlockPositions.add(new TilePos3(x, y, z));
                }
            }
        }
        this.damageEntities();
    }

    private void damageEntities() {
        float size = this.explosionSize * 2.0f;
        int x1 = MathHelper.floor((double)((double)((float)this.explosionX - size) - 1.0));
        int x2 = MathHelper.floor((double)((double)((float)this.explosionX + size) + 1.0));
        int y1 = MathHelper.floor((double)((double)((float)this.explosionY - size) - 1.0));
        int y2 = MathHelper.floor((double)((double)((float)this.explosionY + size) + 1.0));
        int z1 = MathHelper.floor((double)((double)((float)this.explosionZ - size) - 1.0));
        int z2 = MathHelper.floor((double)((double)((float)this.explosionZ + size) + 1.0));
        ArrayList list = new ArrayList(this.worldObj.getEntitiesWithinAABBExcludingEntity(this.exploder, (AABBdc)new AABBd((double)x1, (double)y1, (double)z1, (double)x2, (double)y2, (double)z2)));
        Vector3d vec3 = new Vector3d((double)this.explosionX, (double)this.explosionY, (double)this.explosionZ);
        for (Object entityObj : list) {
            Entity entity = (Entity)entityObj;
            double d4 = entity.distanceTo((double)this.explosionX, (double)this.explosionY, (double)this.explosionZ) / (double)size;
            if (!(d4 <= 1.0)) continue;
            double xComp = entity.x - (double)this.explosionX;
            double yComp = entity.y - (double)this.explosionY;
            double zComp = entity.z - (double)this.explosionZ;
            double distance = MathHelper.sqrt((double)(xComp * xComp + yComp * yComp + zComp * zComp));
            if (distance == 0.0) {
                distance = 1.0;
            }
            xComp /= distance;
            yComp /= distance;
            zComp /= distance;
            double d12 = this.worldObj.getSeenPercent((Vector3dc)vec3, (AABBdc)entity.bb);
            double d13 = (1.0 - d4) * d12;
            entity.hurt(this.exploder, (int)(((d13 * d13 + d13) / 2.0 * 8.0 * (double)size + 1.0) * (double)this.explosionDamage), DamageType.BLAST);
            double flingForce = d13 * 2.0;
            if (entity.hasNoPhysics()) continue;
            entity.fling(xComp * flingForce, yComp * flingForce, zComp * flingForce, 1.0f);
        }
    }

    public void addEffects(boolean flag) {
        this.worldObj.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, (double)this.explosionX, (double)this.explosionY, (double)this.explosionZ, "random.explode", 4.0f, (1.0f + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2f) * 0.7f);
        ArrayList<TilePos3> positions = new ArrayList<TilePos3>(this.destroyedBlockPositions);
        for (int i = positions.size() - 1; i >= 0; --i) {
            TilePos tilePos;
            Block block;
            TilePos3 pos = positions.get(i);
            if (flag) {
                double d2;
                double d5;
                double d1;
                double d4;
                double d = (float)pos.x + this.worldObj.rand.nextFloat();
                double d3 = d - (double)this.explosionX;
                double d6 = MathHelper.sqrt((double)(d3 * d3 + (d4 = (d1 = (double)((float)pos.y + this.worldObj.rand.nextFloat())) - (double)this.explosionY) * d4 + (d5 = (d2 = (double)((float)pos.z + this.worldObj.rand.nextFloat())) - (double)this.explosionZ) * d5));
                if (d6 == 0.0) {
                    d6 = 1.0;
                }
                double d7 = 0.5 / (d6 / (double)this.explosionSize + 0.1);
                double d8 = d7 * (double)(this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3f);
                this.worldObj.spawnParticle("explode", (d + (double)this.explosionX) / 2.0, (d1 + (double)this.explosionY) / 2.0, (d2 + (double)this.explosionZ) / 2.0, (d3 /= d6) * d8, (d4 /= d6) * d8, (d5 /= d6) * d8, 0, false);
                this.worldObj.spawnParticle("smoke", d, d1, d2, d3 * d8, d4 * d8, d5 * d8, 0, false);
            }
            if ((block = this.worldObj.getBlockType((TilePosc)(tilePos = new TilePos(pos.x, pos.y, pos.z)))) == Blocks.AIR) continue;
            if (this.explosionDropRate >= 1.0f || this.worldObj.rand.nextFloat() < this.explosionDropRate) {
                block.dropWithCause(this.worldObj, EnumDropCause.EXPLOSION, (TilePosc)tilePos, this.worldObj.getBlockData((TilePosc)tilePos), this.worldObj.getTileEntity((TilePosc)tilePos), null);
            }
            this.worldObj.setBlockTypeNotify((TilePosc)tilePos, Blocks.AIR);
            block.onDestroyedByExplosion(this.worldObj, (TilePosc)tilePos);
        }
    }

    public record TilePos3(int x, int y, int z) {
    }
}

