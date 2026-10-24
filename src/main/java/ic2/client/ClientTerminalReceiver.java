package ic2.client;

import ic2.gui.menu.MenuDispatcherTerminal;
import ic2.net.TerminalDataMessage;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.core.entity.player.Player;

public class ClientTerminalReceiver {
    public static void apply(int x, int y, int z, List<TerminalDataMessage.DroneView> drones) {
        Minecraft mc = Minecraft.getMinecraft();
        Player player = mc.thePlayer;
        if (player == null || !(player.containerMenu instanceof MenuDispatcherTerminal)) {
            return;
        }
        MenuDispatcherTerminal menu = (MenuDispatcherTerminal)player.containerMenu;
        if (menu.tileEntity.tilePos.x() != x || menu.tileEntity.tilePos.y() != y || menu.tileEntity.tilePos.z() != z) {
            return;
        }
        menu.applyServerData(drones);
    }
}
