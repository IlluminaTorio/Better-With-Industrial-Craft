

package ic2.client;

import ic2.gui.IC2GuiHandler;
import ic2.net.IC2Network;
import net.minecraft.client.entity.player.PlayerLocal;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.Player;

public class ClientGuiOpener
implements IC2Network.GuiOpener {
    @Override
    public void openMachineGui(Player player, TileEntity te, int guiId) {
        if (!(player instanceof PlayerLocal)) {
            return;
        }
        IC2GuiHandler.openClientGui(guiId, 0, te.tilePos.x(), te.tilePos.y(), te.tilePos.z());
    }
}

