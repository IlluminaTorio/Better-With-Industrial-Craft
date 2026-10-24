package ic2.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import ic2.entity.EntityDrone;
import ic2.energy.Direction;
import ic2.energy.IChargeableItem;
import ic2.energy.IEnergySink;
import ic2.net.MachineEventMessage;
import ic2.util.DroneAccessSavedData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.block.ItemBlock;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;

public class TileEntityDroneDepot
extends TileEntityIC2Machine
implements MachineEventMessage.MachineEventHandler,
IEnergySink {
    public static final int CHANNELS = 64;
    public static final int EVENT_CHANNEL_DOWN = 0;
    public static final int EVENT_CHANNEL_UP = 1;
    public static final int EVENT_SEND = 2;
    public static final int MAX_ENERGY = 20000;
    public static final int MAX_INPUT = 32;
    public static final int SEND_COST = 4000;
    private static final Map<World, Map<Integer, List<TileEntityDroneDepot>>> REGISTRY = new WeakHashMap();
    public int channel = 0;
    public String owner = null;
    public int energy = 0;
    private boolean wasPowered = false;
    private boolean registered = false;

    public TileEntityDroneDepot() {
        super(9);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.worldObj != null && !this.worldObj.isClientSide) {
            ic2.util.ServerTaskQueue.drain();
            if (!this.registered) {
                this.register();
            }
            boolean powered = this.worldObj.hasNeighborSignal((TilePosc)this.tilePos);
            if (powered && !this.wasPowered) {
                this.sendDrones();
            }
            this.wasPowered = powered;
        }
    }

    private void register() {
        Map<Integer, List<TileEntityDroneDepot>> byChannel = TileEntityDroneDepot.REGISTRY.computeIfAbsent(this.worldObj, w -> new HashMap());
        List<TileEntityDroneDepot> list = byChannel.computeIfAbsent(this.channel, c -> new ArrayList());
        if (!list.contains(this)) {
            list.add(this);
        }
        this.registered = true;
    }

    private void unregister() {
        Map<Integer, List<TileEntityDroneDepot>> byChannel = TileEntityDroneDepot.REGISTRY.get(this.worldObj);
        if (byChannel == null) {
            this.registered = false;
            return;
        }
        List<TileEntityDroneDepot> list = byChannel.get(this.channel);
        if (list != null) {
            list.remove(this);
        }
        this.registered = false;
    }

    @Override
    public void invalidate() {
        if (this.worldObj != null && !this.worldObj.isClientSide && this.registered) {
            this.unregister();
        }
        super.invalidate();
    }

    @Override
    public void handleEvent(int event) {
        if (this.worldObj == null || this.worldObj.isClientSide) {
            return;
        }
        if (event == EVENT_CHANNEL_DOWN || event == EVENT_CHANNEL_UP) {
            this.unregister();
            this.channel = event == EVENT_CHANNEL_UP ? (this.channel + 1) % TileEntityDroneDepot.CHANNELS : (this.channel + TileEntityDroneDepot.CHANNELS - 1) % TileEntityDroneDepot.CHANNELS;
            this.register();
            this.setChanged();
        } else if (event == EVENT_SEND) {
            this.sendDrones();
        }
    }

    @Override
    public boolean allowsPlayer(int event, Player player) {
        return this.canAccess(player);
    }

    @Override
    public String getAccessOwner() {
        return this.owner;
    }

    public boolean canAccess(Player player) {
        if (this.owner == null) {
            return true;
        }
        if (player == null) {
            return false;
        }
        if (this.owner.equals(player.username)) {
            return true;
        }
        return DroneAccessSavedData.isGranted(this.worldObj, this.owner, player.username);
    }

    public void claimOwnership(Player player) {
        if (this.owner == null && player != null && player.username != null) {
            this.owner = player.username;
            this.setChanged();
        }
    }

    private void sendDrones() {
        if (this.energy < TileEntityDroneDepot.SEND_COST) {
            return;
        }
        List<TileEntityDroneDepot> peers = this.findPeers();
        if (peers.isEmpty()) {
            return;
        }
        if (ic2.server.ServerMachineTest.isTestMode()) {
            StringBuilder sb = new StringBuilder();
            for (TileEntityDroneDepot p : peers) {
                sb.append(p.tilePos.x()).append(",").append(p.tilePos.y()).append(",").append(p.tilePos.z()).append("(ch").append(p.channel).append(") ");
            }
            ic2.IC2.LOGGER.info("[SRV-TEST] sendDrones from {} ch{}: peers={}", this.tilePos.x() + "," + this.tilePos.y() + "," + this.tilePos.z(), this.channel, sb.toString());
        }
        List<ItemStack> payload = new ArrayList();
        for (int i = 0; i < this.inventory.length; ++i) {
            if (this.inventory[i] == null) continue;
            if (TileEntityDroneDepot.isEnergyCarrier(this.inventory[i])) continue;
            payload.add(this.inventory[i]);
            this.inventory[i] = null;
        }
        if (payload.isEmpty()) {
            return;
        }
        this.energy -= TileEntityDroneDepot.SEND_COST;
        this.setChanged();
        int n = peers.size();
        List<List<ItemStack>> shares = new ArrayList();
        for (int r = 0; r < n; ++r) {
            shares.add(new ArrayList());
        }
        for (ItemStack stack : payload) {
            int total = stack.stackSize;
            int base = total / n;
            int rem = total % n;
            for (int r = 0; r < n; ++r) {
                int take = base + (r < rem ? 1 : 0);
                if (take <= 0) continue;
                ItemStack part = stack.copy();
                part.stackSize = take;
                shares.get(r).add(part);
            }
        }
        for (int r = 0; r < n; ++r) {
            if (shares.get(r).isEmpty()) continue;
            EntityDrone drone = new EntityDrone(this.worldObj, new TilePos(this.tilePos), new TilePos(peers.get(r).tilePos), shares.get(r));
            drone.owner = this.owner;
            this.worldObj.entityJoinedWorld(drone);
        }
    }

    public static boolean isEnergyCarrier(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        Item item = stack.getItem();
        if (item instanceof IChargeableItem) {
            return true;
        }
        if (item instanceof ItemBlock) {
            Block<?> block = ((ItemBlock)item).getBlock();
            if (block != null && block.entitySupplier != null) {
                try {
                    return block.entitySupplier.get() instanceof TileEntityElectricBlock;
                }
                catch (Throwable t) {
                    return false;
                }
            }
        }
        return false;
    }

    private List<TileEntityDroneDepot> findPeers() {
        List<TileEntityDroneDepot> peers = new ArrayList();
        Map<Integer, List<TileEntityDroneDepot>> byChannel = TileEntityDroneDepot.REGISTRY.get(this.worldObj);
        if (byChannel == null) {
            return peers;
        }
        List<TileEntityDroneDepot> list = byChannel.get(this.channel);
        if (list == null) {
            return peers;
        }
        for (TileEntityDroneDepot depot : list) {
            if (depot == this) continue;
            TileEntity check = this.worldObj.getTileEntity(depot.tilePos);
            if (check != depot) continue;
            if (!sameOwner(this.owner, depot.owner)) continue;
            peers.add(depot);
        }
        return peers;
    }

    private static boolean sameOwner(String a, String b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return a.equals(b);
    }

    public boolean hasPeer() {
        return this.worldObj != null && !this.worldObj.isClientSide && !this.findPeers().isEmpty();
    }

    @Override
    public void readAdditionalData(CompoundTag tag) {
        super.readAdditionalData(tag);
        this.channel = tag.getInteger("channel") % TileEntityDroneDepot.CHANNELS;
        this.owner = tag.containsKey("owner") ? tag.getString("owner") : null;
        this.energy = tag.getInteger("energy");
        this.wasPowered = tag.getBoolean("wasPowered");
    }

    @Override
    public void writeAdditionalData(CompoundTag tag) {
        super.writeAdditionalData(tag);
        tag.putInt("channel", this.channel);
        if (this.owner != null) {
            tag.putString("owner", this.owner);
        }
        tag.putInt("energy", this.energy);
        tag.putBoolean("wasPowered", this.wasPowered);
    }

    @Override
    public boolean demandsEnergy() {
        return this.energy < TileEntityDroneDepot.MAX_ENERGY;
    }

    @Override
    public int injectEnergy(Direction direction, int amount) {
        if (amount > TileEntityDroneDepot.MAX_INPUT) {
            amount = TileEntityDroneDepot.MAX_INPUT;
        }
        int space = TileEntityDroneDepot.MAX_ENERGY - this.energy;
        if (space >= amount) {
            this.energy += amount;
            return 0;
        }
        this.energy = TileEntityDroneDepot.MAX_ENERGY;
        return amount - space;
    }

    @Override
    public boolean acceptsEnergyFrom(TileEntity emitter, Direction direction) {
        return true;
    }

    @Override
    public String getMachineName() {
        return "Drone Depot";
    }
}
