package ic2.net;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import turniplabs.halplibe.helper.network.NetworkMessage;
import turniplabs.halplibe.helper.network.UniversalPacket;

public class MachineEventMessage
implements NetworkMessage {
    public int x;
    public int y;
    public int z;
    public int event;

    @Override
    public void encodeToUniversalPacket(UniversalPacket packet) {
        packet.writeInt(this.x);
        packet.writeInt(this.y);
        packet.writeInt(this.z);
        packet.writeInt(this.event);
    }

    @Override
    public void decodeFromUniversalPacket(UniversalPacket packet) {
        this.x = packet.readInt();
        this.y = packet.readInt();
        this.z = packet.readInt();
        this.event = packet.readInt();
    }

    @Override
    public void handle(@NotNull NetworkMessage.NetworkContext context) {
        if (context.player != null) {
            World world = context.player.world;
            if (world != null) {
                this.apply(world, context.player);
            }
        }
    }

    public void handleServerEnv(@NotNull NetworkMessage.NetworkContext context) {
    }

    private void apply(World world, Player player) {
        TileEntity te = world.getTileEntity((TilePosc)new TilePos(this.x, this.y, this.z));
        if (te instanceof MachineEventHandler) {
            MachineEventHandler handler = (MachineEventHandler)te;
            if (!handler.allowsPlayer(this.event, player)) {
                if (player != null) {
                    String owner = handler.getAccessOwner();
                    player.sendMessage(TextFormatting.Base.RED, owner == null ? "Access denied." : "Access denied: this machine belongs to " + owner + ".");
                }
                return;
            }
            handler.handleEvent(this.event);
        }
    }

    public static interface MachineEventHandler {
        public void handleEvent(int var1);

        default public boolean allowsPlayer(int event, Player player) {
            return true;
        }

        default public String getAccessOwner() {
            return null;
        }
    }
}
