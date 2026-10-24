package ic2.net;

import com.mojang.nbt.tags.CompoundTag;
import ic2.IC2;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.net.entity.EntityTracker;
import net.minecraft.core.net.entity.EntityTrackerEntry;
import net.minecraft.core.net.entity.ITrackedEntry;
import net.minecraft.core.net.entity.IVehicleEntry;
import net.minecraft.core.net.packet.PacketAddEntity;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NetEntryIC2Entity<T extends Entity>
implements IVehicleEntry<T>,
ITrackedEntry<T> {
    private static final Set<Class<?>> LOGGED_NETWORK_SPAWNS = new HashSet();

    public interface Factory<T extends Entity> {
        @Nullable
        T create(World world, double x, double y, double z, int metadata, boolean hasVelocity, double xd, double yd, double zd, Entity owner, @Nullable CompoundTag tag);
    }

    private final Class<T> entityClass;
    private final Factory<T> factory;
    private final int trackingDistance;
    private final int movementPacketDelay;
    private final boolean motionUpdates;

    public NetEntryIC2Entity(Class<T> entityClass, Factory<T> factory, int trackingDistance, int movementPacketDelay, boolean motionUpdates) {
        this.entityClass = entityClass;
        this.factory = factory;
        this.trackingDistance = trackingDistance;
        this.movementPacketDelay = movementPacketDelay;
        this.motionUpdates = motionUpdates;
    }

    @Override
    @NotNull
    public Class<? extends T> getAppliedClass() {
        return this.entityClass;
    }

    @Override
    public int getTrackingDistance() {
        return this.trackingDistance;
    }

    @Override
    public int getMovementPacketDelay() {
        return this.movementPacketDelay;
    }

    @Override
    public boolean sendMotionUpdates() {
        return this.motionUpdates;
    }

    @Override
    public void onEntityTracked(EntityTracker tracker, EntityTrackerEntry trackerEntry, T trackedObject) {
    }

    @Override
    @NotNull
    public PacketAddEntity getSpawnPacket(EntityTrackerEntry tracker, T trackedObject) {
        CompoundTag tag = new CompoundTag();
        trackedObject.addAdditionalSaveData(tag);
        return new PacketAddEntity(trackedObject, -1, -1, trackedObject.xd, trackedObject.yd, trackedObject.zd, tag);
    }

    @Override
    @Nullable
    public Entity getEntity(World world, double x, double y, double z, int metadata, boolean hasVelocity, double xd, double yd, double zd, Entity owner, @Nullable CompoundTag tag) {
        if (!LOGGED_NETWORK_SPAWNS.contains(this.entityClass)) {
            LOGGED_NETWORK_SPAWNS.add(this.entityClass);
            IC2.LOGGER.info("IC2 network spawn: {} at {},{},{}", (Object)this.entityClass.getSimpleName(), Math.floor(x), Math.floor(y), Math.floor(z));
        }
        T entity = this.factory.create(world, x, y, z, metadata, hasVelocity, xd, yd, zd, owner, tag);
        if (entity == null) {
            return null;
        }
        entity.setPos(x, y, z);
        if (tag != null) {
            entity.readAdditionalSaveData(tag);
        }
        return entity;
    }
}
