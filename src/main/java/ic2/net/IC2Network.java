

package ic2.net;

import ic2.gui.IC2GuiHandler;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import turniplabs.halplibe.helper.network.NetworkHandler;
import turniplabs.halplibe.helper.network.NetworkMessage;
import turniplabs.halplibe.helper.network.UniversalPacket;

public class IC2Network {
    private static GuiOpener opener = (player, te, guiId) -> {};

    public static void setOpener(GuiOpener o) {
        opener = o;
    }

    public static void init() {
        NetworkHandler.registerNetworkMessage(OpenGuiMessage::new);
    }

    public static void openMachineGui(Player player, TileEntity te, int guiId) {
        opener.openMachineGui(player, te, guiId);
    }

    @FunctionalInterface
    public static interface GuiOpener {
        public void openMachineGui(Player var1, TileEntity var2, int var3);
    }

    public static class OpenGuiMessage
    implements NetworkMessage {
        public int guiId;
        public int windowId;
        public int x;
        public int y;
        public int z;

        public void encodeToUniversalPacket(UniversalPacket packet) {
            packet.writeInt(this.guiId);
            packet.writeInt(this.windowId);
            packet.writeInt(this.x);
            packet.writeInt(this.y);
            packet.writeInt(this.z);
        }

        public void decodeFromUniversalPacket(UniversalPacket packet) {
            this.guiId = packet.readInt();
            this.windowId = packet.readInt();
            this.x = packet.readInt();
            this.y = packet.readInt();
            this.z = packet.readInt();
        }

        public void handle(NetworkMessage.NetworkContext context) {
        }

        public void handleClientEnv(@NotNull NetworkMessage.NetworkContext context) {
            IC2GuiHandler.openClientGui(this.guiId, this.windowId, this.x, this.y, this.z);
        }
    }
}

