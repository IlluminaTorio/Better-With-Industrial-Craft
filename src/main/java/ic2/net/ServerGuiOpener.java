

package ic2.net;

import ic2.gui.menu.MenuCanner;
import ic2.gui.menu.MenuElectricBlock;
import ic2.gui.menu.MenuElectricMachine;
import ic2.gui.menu.MenuElectrolyzer;
import ic2.gui.menu.MenuGenerator;
import ic2.gui.menu.MenuInduction;
import ic2.gui.menu.MenuIronFurnace;
import ic2.gui.menu.MenuMatter;
import ic2.gui.menu.MenuNuclearReactor;
import ic2.gui.menu.MenuPersonalSafe;
import ic2.mixin.PlayerServerAccessor;
import ic2.net.IC2Network;
import ic2.tileentity.TileEntityBaseGenerator;
import ic2.tileentity.TileEntityCanner;
import ic2.tileentity.TileEntityElectricBlock;
import ic2.tileentity.TileEntityElectricMachine;
import ic2.tileentity.TileEntityElectrolyzer;
import ic2.tileentity.TileEntityInduction;
import ic2.tileentity.TileEntityIronFurnace;
import ic2.tileentity.TileEntityMatter;
import ic2.tileentity.TileEntityNuclearReactor;
import ic2.tileentity.TileEntityPersonalChest;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.crafting.ContainerListener;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import net.minecraft.server.entity.player.PlayerServer;
import org.jetbrains.annotations.Nullable;
import turniplabs.halplibe.helper.network.NetworkHandler;
import turniplabs.halplibe.helper.network.NetworkMessage;

public class ServerGuiOpener
implements IC2Network.GuiOpener {
    @Override
    public void openMachineGui(Player player, TileEntity te, int guiId) {
        if (!(player instanceof PlayerServer)) {
            return;
        }
        PlayerServer serverPlayer = (PlayerServer)player;
        MenuAbstract menu = ServerGuiOpener.createServerMenu(guiId, serverPlayer, te);
        if (menu == null) {
            return;
        }
        PlayerServerAccessor accessor = (PlayerServerAccessor)serverPlayer;
        accessor.ic2$invokeGetNextWindowId();
        int windowId = accessor.ic2$getCurrentWindowId();
        serverPlayer.containerMenu.onCraftGuiClosed((Player)serverPlayer);
        serverPlayer.containerMenu = menu;
        menu.containerId = windowId;
        menu.addSlotListener((ContainerListener)serverPlayer);
        IC2Network.OpenGuiMessage msg = new IC2Network.OpenGuiMessage();
        msg.guiId = guiId;
        msg.windowId = windowId;
        msg.x = te.tilePos.x();
        msg.y = te.tilePos.y();
        msg.z = te.tilePos.z();
        NetworkHandler.sendToPlayer((Player)player, (NetworkMessage)msg);
    }

    @Nullable
    private static MenuAbstract createServerMenu(int guiId, PlayerServer player, TileEntity te) {
        return switch (guiId) {
            case 0 -> {
                if (te instanceof TileEntityElectricMachine) {
                    TileEntityElectricMachine m = (TileEntityElectricMachine)te;
                    yield new MenuElectricMachine(player.inventory, m);
                }
                yield null;
            }
            case 1 -> {
                if (te instanceof TileEntityIronFurnace) {
                    TileEntityIronFurnace f = (TileEntityIronFurnace)te;
                    yield new MenuIronFurnace(player.inventory, f);
                }
                yield null;
            }
            case 2 -> {
                if (te instanceof TileEntityBaseGenerator) {
                    TileEntityBaseGenerator g = (TileEntityBaseGenerator)te;
                    yield new MenuGenerator(player.inventory, g);
                }
                yield null;
            }
            case 3 -> {
                if (te instanceof TileEntityElectricBlock) {
                    TileEntityElectricBlock b = (TileEntityElectricBlock)te;
                    yield new MenuElectricBlock(player.inventory, b);
                }
                yield null;
            }
            case 4 -> {
                if (te instanceof TileEntityCanner) {
                    TileEntityCanner c = (TileEntityCanner)te;
                    yield new MenuCanner(player.inventory, c);
                }
                yield null;
            }
            case 5 -> {
                if (te instanceof TileEntityElectrolyzer) {
                    TileEntityElectrolyzer e = (TileEntityElectrolyzer)te;
                    yield new MenuElectrolyzer(player.inventory, e);
                }
                yield null;
            }
            case 6 -> {
                if (te instanceof TileEntityInduction) {
                    TileEntityInduction i = (TileEntityInduction)te;
                    yield new MenuInduction(player.inventory, i);
                }
                yield null;
            }
            case 7 -> {
                if (te instanceof TileEntityMatter) {
                    TileEntityMatter m = (TileEntityMatter)te;
                    yield new MenuMatter(player.inventory, m);
                }
                yield null;
            }
            case 8 -> {
                if (te instanceof TileEntityNuclearReactor) {
                    TileEntityNuclearReactor r = (TileEntityNuclearReactor)te;
                    yield new MenuNuclearReactor(player.inventory, r);
                }
                yield null;
            }
            case 11 -> {
                if (te instanceof TileEntityPersonalChest) {
                    TileEntityPersonalChest p = (TileEntityPersonalChest)te;
                    yield new MenuPersonalSafe(player.inventory, p);
                }
                yield null;
            }
            default -> null;
        };
    }
}

