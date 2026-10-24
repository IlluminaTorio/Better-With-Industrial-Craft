package ic2.net;

import ic2.gui.menu.MenuDispatcherTerminal;
import ic2.tileentity.TileEntityDispatcherTerminal;
import java.util.List;
import net.minecraft.core.entity.player.Player;
import net.minecraft.server.entity.player.PlayerServer;

public class ServerTerminalBroadcaster
implements IC2Network.TerminalBroadcaster {
    @Override
    public void broadcast(TileEntityDispatcherTerminal terminal, List<TerminalDataMessage.DroneView> views) {
        if (terminal.worldObj == null || terminal.worldObj.isClientSide) {
            return;
        }
        for (Player player : terminal.worldObj.players) {
            if (!(player instanceof PlayerServer)) continue;
            PlayerServer serverPlayer = (PlayerServer)player;
            if (!(serverPlayer.containerMenu instanceof MenuDispatcherTerminal)) continue;
            MenuDispatcherTerminal menu = (MenuDispatcherTerminal)serverPlayer.containerMenu;
            if (menu.tileEntity != terminal) continue;
            TerminalDataMessage.sendTo((Player)serverPlayer, terminal, views);
        }
    }
}
