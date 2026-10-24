package ic2.net;

import ic2.command.CommandDroneAccess;
import org.jetbrains.annotations.NotNull;
import turniplabs.halplibe.helper.network.NetworkMessage;
import turniplabs.halplibe.helper.network.UniversalPacket;

public class GrantAccessMessage
implements NetworkMessage {
    public static final int ACTION_GRANT = 0;
    public static final int ACTION_REVOKE = 1;
    public static final int ACTION_LIST = 2;
    public int action;
    public String target = "";

    @Override
    public void encodeToUniversalPacket(UniversalPacket packet) {
        packet.writeInt(this.action);
        packet.writeString(this.target);
    }

    @Override
    public void decodeFromUniversalPacket(UniversalPacket packet) {
        this.action = packet.readInt();
        this.target = packet.readString();
    }

    @Override
    public void handle(@NotNull NetworkMessage.NetworkContext context) {
        if (context.player == null || context.player.world == null) {
            return;
        }
        CommandDroneAccess.apply(context.player.world, context.player.username, this.action, this.target);
    }

    @Override
    public void handleClientEnv(@NotNull NetworkMessage.NetworkContext context) {
    }
}
