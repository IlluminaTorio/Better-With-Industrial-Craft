package ic2.net;

import ic2.entity.EntityDrone;
import ic2.gui.menu.MenuDispatcherTerminal;
import ic2.tileentity.TileEntityDispatcherTerminal;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import turniplabs.halplibe.helper.network.NetworkHandler;
import turniplabs.halplibe.helper.network.NetworkMessage;
import turniplabs.halplibe.helper.network.UniversalPacket;

public class TerminalDataMessage
implements NetworkMessage {
    public int x;
    public int y;
    public int z;
    public final List<DroneView> drones = new ArrayList();

    public static class DroneView {
        public int targetX;
        public int targetY;
        public int targetZ;
        public int phase;
        public final List<ItemStack> cargo = new ArrayList();
    }

    public static void sendTo(net.minecraft.core.entity.player.Player player, TileEntityDispatcherTerminal terminal, List<DroneView> drones) {
        TerminalDataMessage message = new TerminalDataMessage();
        message.x = terminal.tilePos.x();
        message.y = terminal.tilePos.y();
        message.z = terminal.tilePos.z();
        message.drones.addAll(drones);
        NetworkHandler.sendToPlayer(player, message);
    }

    public static List<DroneView> scan(World world, String owner) {
        ArrayList<DroneView> views = new ArrayList<DroneView>();
        if (world == null || world.isClientSide) {
            return views;
        }
        for (Entity entity : world.entities) {
            if (!(entity instanceof EntityDrone)) continue;
            EntityDrone drone = (EntityDrone)entity;
            if (owner == null ? drone.owner != null : !owner.equals(drone.owner)) continue;
            DroneView view = new DroneView();
            view.targetX = drone.targetX;
            view.targetY = drone.targetY;
            view.targetZ = drone.targetZ;
            view.phase = drone.phase;
            for (ItemStack stack : drone.payload) {
                if (stack == null || stack.stackSize <= 0) continue;
                view.cargo.add(stack);
            }
            views.add(view);
        }
        return views;
    }

    @Override
    public void encodeToUniversalPacket(UniversalPacket packet) {
        packet.writeInt(this.x);
        packet.writeInt(this.y);
        packet.writeInt(this.z);
        packet.writeInt(this.drones.size());
        for (DroneView view : this.drones) {
            packet.writeInt(view.targetX);
            packet.writeInt(view.targetY);
            packet.writeInt(view.targetZ);
            packet.writeInt(view.phase);
            packet.writeInt(view.cargo.size());
            for (ItemStack stack : view.cargo) {
                packet.writeItemStack(stack);
            }
        }
    }

    @Override
    public void decodeFromUniversalPacket(UniversalPacket packet) {
        this.x = packet.readInt();
        this.y = packet.readInt();
        this.z = packet.readInt();
        int count = packet.readInt();
        for (int i = 0; i < count; ++i) {
            DroneView view = new DroneView();
            view.targetX = packet.readInt();
            view.targetY = packet.readInt();
            view.targetZ = packet.readInt();
            view.phase = packet.readInt();
            int cargoCount = packet.readInt();
            for (int k = 0; k < cargoCount; ++k) {
                ItemStack stack = packet.readItemStack();
                if (stack == null) continue;
                view.cargo.add(stack);
            }
            this.drones.add(view);
        }
    }

    @Override
    public void handle(@NotNull NetworkMessage.NetworkContext context) {
    }

    @Override
    public void handleClientEnv(@NotNull NetworkMessage.NetworkContext context) {
        ic2.client.ClientTerminalReceiver.apply(this.x, this.y, this.z, this.drones);
    }
}
