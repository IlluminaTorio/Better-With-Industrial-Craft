package ic2.net;

import net.minecraft.core.block.entity.TileEntity;
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
                this.apply(world);
            }
        }
    }

    public void handleServerEnv(@NotNull NetworkMessage.NetworkContext context) {
    }

    private void apply(World world) {
        TileEntity te = world.getTileEntity((TilePosc)new TilePos(this.x, this.y, this.z));
        if (te instanceof MachineEventHandler) {
            ((MachineEventHandler)te).handleEvent(this.event);
        }
    }

    public static interface MachineEventHandler {
        public void handleEvent(int var1);
    }
}
