package ic2.entity;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import com.mojang.nbt.tags.Tag;
import ic2.tileentity.TileEntityDroneDepot;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import org.jetbrains.annotations.NotNull;

public class EntityDrone
extends Entity {
    public static final double SPEED = 0.75;
    public static final int MAX_AGE = 24000;
    public static final int PHASE_OUTBOUND = 0;
    public static final int PHASE_RETURN = 1;
    public List<ItemStack> payload = new ArrayList();
    public int phase = 0;
    public int sourceX;
    public int sourceY;
    public int sourceZ;
    public int targetX;
    public int targetY;
    public int targetZ;
    public int age = 0;
    public String owner = null;

    public EntityDrone(World world) {
        super(world);
        this.setSize(0.6f, 0.6f);
    }

    public EntityDrone(World world, TilePos from, TilePos to, List<ItemStack> payload) {
        this(world);
        this.sourceX = from.x();
        this.sourceY = from.y();
        this.sourceZ = from.z();
        this.targetX = to.x();
        this.targetY = to.y();
        this.targetZ = to.z();
        if (payload != null) {
            this.payload = payload;
        }
        this.setPos((double)from.x() + 0.5, (double)from.y() + 1.3, (double)from.z() + 0.5);
        this.yRot = world.rand.nextFloat() * 360.0f;
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public void remove() {
        if (ic2.server.ServerMachineTest.isTestMode() && !this.world.isClientSide) {
            ic2.IC2.LOGGER.info("[SRV-TEST] drone removed: id={} phase={} age={} pos={},{},{} payload={}", this.id, this.phase, this.age, (int)this.x, (int)this.y, (int)this.z, this.payload.size());
        }
        super.remove();
    }

    @Override
    public void tick() {
        super.tick();
        ++this.age;
        if (ic2.server.ServerMachineTest.isTestMode() && !this.world.isClientSide && (this.age <= 3 || this.age % 40 == 0)) {
            ic2.IC2.LOGGER.info("[SRV-TEST] drone tick: id={} phase={} age={} pos={},{},{} payload={}", this.id, this.phase, this.age, (int)this.x, (int)this.y, (int)this.z, this.payload.size());
        }
        if (this.age > EntityDrone.MAX_AGE) {
            if (!this.world.isClientSide) {
                this.dropEverything();
            }
            this.remove();
            return;
        }
        TilePos goal = this.phase == EntityDrone.PHASE_OUTBOUND ? new TilePos(this.targetX, this.targetY, this.targetZ) : new TilePos(this.sourceX, this.sourceY, this.sourceZ);
        if (!this.world.isClientSide) {
            TileEntity te = this.world.getTileEntity(goal);
            if (this.phase == EntityDrone.PHASE_OUTBOUND && (!(te instanceof TileEntityDroneDepot) || !this.world.isChunkLoaded(goal.x() >> 4, goal.z() >> 4))) {
                this.phase = EntityDrone.PHASE_RETURN;
            } else if (this.phase == EntityDrone.PHASE_RETURN && !(te instanceof TileEntityDroneDepot)) {
                this.dropEverything();
                this.remove();
                return;
            }
        }
        double gx = (double)goal.x() + 0.5;
        double gy = (double)goal.y() + 1.3;
        double gz = (double)goal.z() + 0.5;
        double dx = gx - this.x;
        double dy = gy - this.y;
        double dz = gz - this.z;
        double dist = (double)MathHelper.sqrt((float)(dx * dx + dy * dy + dz * dz));
        if (dist < 0.8) {
            if (this.world.isClientSide) {
                return;
            }
            if (this.phase == EntityDrone.PHASE_OUTBOUND) {
                this.deliver(goal);
                this.phase = EntityDrone.PHASE_RETURN;
            } else {
                this.deliver(goal);
                this.dropEverything();
                this.remove();
            }
            return;
        }
        double vx = dx / dist * EntityDrone.SPEED;
        double vy = dy / dist * EntityDrone.SPEED;
        double vz = dz / dist * EntityDrone.SPEED;
        this.x += vx;
        this.y += vy;
        this.z += vz;
        this.setPos(this.x, this.y, this.z);
        float flat = MathHelper.sqrt((float)(vx * vx + vz * vz));
        this.yRot = (float)(Math.atan2(vx, vz) * 180.0 / 3.1415927410125732);
        this.xRot = (float)(Math.atan2(vy, (double)flat) * 180.0 / 3.1415927410125732);
    }

    private void deliver(TilePos goal) {
        TileEntity te = this.world.getTileEntity(goal);
        if (ic2.server.ServerMachineTest.isTestMode() && !this.world.isClientSide) {
            ic2.IC2.LOGGER.info("[SRV-TEST] drone deliver: id={} goal={},{},{} te={} payload={}", this.id, goal.x(), goal.y(), goal.z(), te == null ? "null" : te.getClass().getSimpleName(), this.payload.size());
        }
        if (!(te instanceof TileEntityDroneDepot)) {
            this.dropEverything();
            this.remove();
            return;
        }
        TileEntityDroneDepot depot = (TileEntityDroneDepot)te;
        List<ItemStack> leftover = new ArrayList();
        for (ItemStack stack : this.payload) {
            if (stack == null || stack.stackSize <= 0) continue;
            ItemStack remain = stack.copy();
            for (int i = 0; i < depot.inventory.length && remain.stackSize > 0; ++i) {
                ItemStack cur = depot.inventory[i];
                if (cur == null || cur.itemID != remain.itemID || cur.getMetadata() != remain.getMetadata() || cur.stackSize + remain.stackSize > cur.getMaxStackSize()) continue;
                int move = Math.min(remain.stackSize, cur.getMaxStackSize() - cur.stackSize);
                cur.stackSize += move;
                remain.stackSize -= move;
            }
            for (int i = 0; i < depot.inventory.length && remain.stackSize > 0; ++i) {
                if (depot.inventory[i] != null) continue;
                depot.inventory[i] = remain.copy();
                remain.stackSize = 0;
            }
            if (remain.stackSize > 0) {
                leftover.add(remain);
            }
        }
        depot.setChanged();
        if (ic2.server.ServerMachineTest.isTestMode() && !this.world.isClientSide) {
            ic2.IC2.LOGGER.info("[SRV-TEST] drone deliver done: id={} depot slot0={} leftover={}", this.id, String.valueOf(depot.inventory[0]), leftover.size());
        }
        this.payload.clear();
        for (ItemStack stack : leftover) {
            this.dropAtCurrent(stack);
        }
    }

    private void dropEverything() {
        for (ItemStack stack : this.payload) {
            if (stack == null || stack.stackSize <= 0) continue;
            this.dropAtCurrent(stack);
        }
        this.payload.clear();
    }

    private void dropAtCurrent(ItemStack stack) {
        EntityItem entity = new EntityItem(this.world, this.x, this.y, this.z, stack);
        entity.xd *= 0.05;
        entity.yd *= 0.05;
        entity.zd *= 0.05;
        this.world.entityJoinedWorld(entity);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        tag.putInt("phase", this.phase);
        tag.putInt("age", this.age);
        tag.putInt("sx", this.sourceX);
        tag.putInt("sy", this.sourceY);
        tag.putInt("sz", this.sourceZ);
        tag.putInt("tx", this.targetX);
        tag.putInt("ty", this.targetY);
        tag.putInt("tz", this.targetZ);
        if (this.owner != null) {
            tag.putString("owner", this.owner);
        }
        ListTag items = new ListTag();
        for (ItemStack stack : this.payload) {
            if (stack == null || stack.stackSize <= 0) continue;
            CompoundTag itemTag = new CompoundTag();
            stack.writeToNBT(itemTag);
            items.addTag(itemTag);
        }
        tag.put("Items", (Tag)items);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        this.phase = tag.getInteger("phase");
        this.age = tag.getInteger("age");
        this.sourceX = tag.getInteger("sx");
        this.sourceY = tag.getInteger("sy");
        this.sourceZ = tag.getInteger("sz");
        this.targetX = tag.getInteger("tx");
        this.targetY = tag.getInteger("ty");
        this.targetZ = tag.getInteger("tz");
        this.owner = tag.containsKey("owner") ? tag.getString("owner") : null;
        this.payload = new ArrayList();
        ListTag items = tag.getList("Items");
        for (int i = 0; i < items.tagCount(); ++i) {
            CompoundTag itemTag = (CompoundTag)items.tagAt(i);
            ItemStack stack = ItemStack.readItemStackFromNbt(itemTag);
            if (stack == null || stack.stackSize <= 0) continue;
            this.payload.add(stack);
        }
    }

    public List<ItemStack> getPayload() {
        return this.payload;
    }

    @Override
    public float getShadowHeightOffs() {
        return 0.0f;
    }

    @Override
    protected boolean makeStepSound() {
        return false;
    }
}
