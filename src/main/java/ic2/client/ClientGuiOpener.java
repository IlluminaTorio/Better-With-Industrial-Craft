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
        if (te instanceof ic2.tileentity.TileEntityDroneDepot) {
            ic2.tileentity.TileEntityDroneDepot depot = (ic2.tileentity.TileEntityDroneDepot)te;
            depot.claimOwnership(player);
            if (!depot.canAccess(player)) {
                player.sendMessage(net.minecraft.core.net.command.TextFormatting.Base.RED, "Access denied: this depot belongs to " + depot.owner + ".");
                return;
            }
        }
        if (te instanceof ic2.tileentity.TileEntityDispatcherTerminal) {
            ic2.tileentity.TileEntityDispatcherTerminal terminal = (ic2.tileentity.TileEntityDispatcherTerminal)te;
            terminal.claimOwnership(player);
            if (!terminal.canAccess(player)) {
                player.sendMessage(net.minecraft.core.net.command.TextFormatting.Base.RED, "Access denied: this terminal belongs to " + terminal.owner + ".");
                return;
            }
        }
        IC2GuiHandler.openClientGui(guiId, 0, te.tilePos.x(), te.tilePos.y(), te.tilePos.z());
    }
}
