

package ic2.util;

import java.util.ArrayList;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.Explosion;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;

public class ExplosionIC2
extends Explosion {
    public final float explosionDropRate;
    public final float explosionDamageVsEntities;

    public ExplosionIC2(@NotNull World world, @Nullable Entity exploder, double x, double y, double z, float power, float dropRate, float damageVsEntities) {
        super(world, exploder, x, y, z, power);
        this.explosionDropRate = dropRate;
        this.explosionDamageVsEntities = damageVsEntities;
    }

    public void explode() {
        this.calculateBlocksToDestroy();
        this.damageEntities();
    }

    protected void damageEntities() {
        float explosionSize2 = this.explosionSize * 2.0f;
        int x1 = MathHelper.floor((double)(this.explosionX - (double)explosionSize2 - 1.0));
        int x2 = MathHelper.floor((double)(this.explosionX + (double)explosionSize2 + 1.0));
        int y1 = MathHelper.floor((double)(this.explosionY - (double)explosionSize2 - 1.0));
        int y2 = MathHelper.floor((double)(this.explosionY + (double)explosionSize2 + 1.0));
        int z1 = MathHelper.floor((double)(this.explosionZ - (double)explosionSize2 - 1.0));
        int z2 = MathHelper.floor((double)(this.explosionZ + (double)explosionSize2 + 1.0));
        ArrayList list = new ArrayList(this.world.getEntitiesWithinAABBExcludingEntity(this.exploder, (AABBdc)new AABBd((double)x1, (double)y1, (double)z1, (double)x2, (double)y2, (double)z2)));
        Vector3d vec3 = new Vector3d(this.explosionX, this.explosionY, this.explosionZ);
        for (Object entityObj : list) {
            Entity entity = (Entity)entityObj;
            double d4 = entity.distanceTo(this.explosionX, this.explosionY, this.explosionZ) / (double)explosionSize2;
            if (!(d4 <= 1.0)) continue;
            double xComp = entity.x - this.explosionX;
            double yComp = entity.y - this.explosionY;
            double zComp = entity.z - this.explosionZ;
            double distance = MathHelper.sqrt((double)(xComp * xComp + yComp * yComp + zComp * zComp));
            if (distance == 0.0) {
                distance = 1.0;
            }
            xComp /= distance;
            yComp /= distance;
            zComp /= distance;
            double d12 = this.world.getSeenPercent((Vector3dc)vec3, (AABBdc)entity.bb);
            double d13 = (1.0 - d4) * d12;
            entity.hurt(this.exploder, (int)(((d13 * d13 + d13) / 2.0 * 8.0 * (double)explosionSize2 + 1.0) * (double)this.explosionDamageVsEntities), DamageType.BLAST);
            double flingForce = d13 * 2.0;
            if (entity.hasNoPhysics()) continue;
            entity.fling(xComp * flingForce, yComp * flingForce, zComp * flingForce, 1.0f);
        }
    }

    public void addEffects(boolean particles) {
        if (!this.world.isClientSide) {
            this.world.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, this.explosionX, this.explosionY, this.explosionZ, "random.explode", 4.0f, (1.0f + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2f) * 0.7f);
        }
        ArrayList positions = new ArrayList(this.destroyedBlockPositions);
        for (int i = positions.size() - 1; i >= 0; --i) {
            Block block;
            TilePos pos = (TilePos)positions.get(i);
            if (particles) {
                double zPos;
                double d5;
                double yPos;
                double d4;
                double xPos = (float)pos.x + this.world.rand.nextFloat();
                double d3 = xPos - this.explosionX;
                double d6 = MathHelper.sqrt((double)(d3 * d3 + (d4 = (yPos = (double)((float)pos.y + this.world.rand.nextFloat())) - this.explosionY) * d4 + (d5 = (zPos = (double)((float)pos.z + this.world.rand.nextFloat())) - this.explosionZ) * d5));
                if (d6 == 0.0) {
                    d6 = 1.0;
                }
                double d7 = 0.5 / (d6 / (double)this.explosionSize + 0.1);
                double d8 = d7 * (double)(this.world.rand.nextFloat() * this.world.rand.nextFloat() + 0.3f);
                this.world.spawnParticle("explode", (xPos + this.explosionX) / 2.0, (yPos + this.explosionY) / 2.0, (zPos + this.explosionZ) / 2.0, (d3 /= d6) * d8, (d4 /= d6) * d8, (d5 /= d6) * d8, 0, false);
                this.world.spawnParticle("smoke", xPos, yPos, zPos, d3 * d8, d4 * d8, d5 * d8, 0, false);
            }
            if ((block = this.world.getBlockType((TilePosc)pos)) == Blocks.AIR) continue;
            this.dropBlockWithChance(this.world, pos, this.explosionDropRate);
            this.world.setBlockTypeNotify((TilePosc)pos, Blocks.AIR);
            block.onDestroyedByExplosion(this.world, (TilePosc)pos);
        }
    }

    private void dropBlockWithChance(World world, TilePos pos, float rate) {
        Block block = world.getBlockType((TilePosc)pos);
        if (block == Blocks.AIR) {
            return;
        }
        if (block.getBlastResistance(null) < 0.0f) {
            return;
        }
        if (rate < 1.0f && world.rand.nextFloat() > rate) {
            return;
        }
        block.dropWithCause(world, EnumDropCause.EXPLOSION, (TilePosc)pos, world.getBlockData((TilePosc)pos), world.getTileEntity((TilePosc)pos), null);
    }
}

