

package ic2.gui;

import ic2.IC2;
import ic2.gui.screen.ScreenCanner;
import ic2.gui.screen.ScreenElectricBlock;
import ic2.gui.screen.ScreenElectricMachine;
import ic2.gui.screen.ScreenElectrolyzer;
import ic2.gui.screen.ScreenGenerator;
import ic2.gui.screen.ScreenInduction;
import ic2.gui.screen.ScreenIronFurnace;
import ic2.gui.screen.ScreenMatter;
import ic2.gui.screen.ScreenMiner;
import ic2.gui.screen.ScreenNuclearReactor;
import ic2.gui.screen.ScreenPersonalSafe;
import ic2.gui.screen.ScreenPump;
import ic2.gui.screen.ScreenTradeOMat;
import ic2.tileentity.TileEntityBaseGenerator;
import ic2.tileentity.TileEntityCanner;
import ic2.tileentity.TileEntityElectricBlock;
import ic2.tileentity.TileEntityElectricMachine;
import ic2.tileentity.TileEntityElectrolyzer;
import ic2.tileentity.TileEntityInduction;
import ic2.tileentity.TileEntityIronFurnace;
import ic2.tileentity.TileEntityMatter;
import ic2.tileentity.TileEntityMiner;
import ic2.tileentity.TileEntityNuclearReactor;
import ic2.tileentity.TileEntityPersonalChest;
import ic2.tileentity.TileEntityPump;
import ic2.tileentity.TileEntityTradeOMat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.PlayerLocal;
import net.minecraft.client.gui.Screen;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.Player;

public class IC2GuiHandler {
    public static void openClientGui(int guiId, int windowId, int x, int y, int z) {
        Minecraft mc = Minecraft.getMinecraft();
        PlayerLocal player = mc.thePlayer;
        if (player == null || player.world == null) {
            IC2.LOGGER.warn("[GUI] player/world null, guiId={}", (Object)guiId);
            return;
        }
        TileEntity te = player.world.getTileEntity(x, y, z);
        if (te == null) {
            IC2.LOGGER.warn("[GUI] TE null at {},{},{} for guiId={}", new Object[]{x, y, z, guiId});
            return;
        }
        IC2.LOGGER.info("[GUI] opening guiId={} at {},{},{} te={}", new Object[]{guiId, x, y, z, te.getClass().getSimpleName()});
        switch (guiId) {
            case 0: {
                if (!(te instanceof TileEntityElectricMachine)) break;
                TileEntityElectricMachine machine = (TileEntityElectricMachine)te;
                mc.displayScreen((Screen)new ScreenElectricMachine(player.inventory, machine, machine.getGuiTexture(), machine.getGuiTitleKey()));
                break;
            }
            case 1: {
                if (!(te instanceof TileEntityIronFurnace)) break;
                TileEntityIronFurnace furnace = (TileEntityIronFurnace)te;
                mc.displayScreen((Screen)new ScreenIronFurnace(player.inventory, furnace));
                break;
            }
            case 2: {
                if (!(te instanceof TileEntityBaseGenerator)) break;
                TileEntityBaseGenerator generator = (TileEntityBaseGenerator)te;
                mc.displayScreen((Screen)new ScreenGenerator(player.inventory, generator, generator.getGuiTexture(), generator.getGuiTitleKey()));
                break;
            }
            case 3: {
                if (!(te instanceof TileEntityElectricBlock)) break;
                TileEntityElectricBlock block = (TileEntityElectricBlock)te;
                mc.displayScreen((Screen)new ScreenElectricBlock(player.inventory, block, block.getGuiTitleKey()));
                break;
            }
            case 4: {
                if (!(te instanceof TileEntityCanner)) break;
                TileEntityCanner canner = (TileEntityCanner)te;
                mc.displayScreen((Screen)new ScreenCanner(player.inventory, canner));
                break;
            }
            case 5: {
                if (!(te instanceof TileEntityElectrolyzer)) break;
                TileEntityElectrolyzer electrolyzer = (TileEntityElectrolyzer)te;
                mc.displayScreen((Screen)new ScreenElectrolyzer(player.inventory, electrolyzer));
                break;
            }
            case 6: {
                if (!(te instanceof TileEntityInduction)) break;
                TileEntityInduction induction = (TileEntityInduction)te;
                mc.displayScreen((Screen)new ScreenInduction(player.inventory, induction));
                break;
            }
            case 7: {
                if (!(te instanceof TileEntityMatter)) break;
                TileEntityMatter matter = (TileEntityMatter)te;
                mc.displayScreen((Screen)new ScreenMatter(player.inventory, matter));
                break;
            }
            case 10: {
                if (!(te instanceof TileEntityPump)) break;
                TileEntityPump pump = (TileEntityPump)te;
                mc.displayScreen((Screen)new ScreenPump(player.inventory, pump));
                break;
            }
            case 12: {
                if (!(te instanceof TileEntityTradeOMat)) break;
                TileEntityTradeOMat tradeOMat = (TileEntityTradeOMat)te;
                boolean owner = tradeOMat.canAccess((Player)player);
                mc.displayScreen((Screen)new ScreenTradeOMat(player.inventory, tradeOMat, owner));
                break;
            }
            case 9: {
                if (!(te instanceof TileEntityMiner)) break;
                TileEntityMiner miner = (TileEntityMiner)te;
                mc.displayScreen((Screen)new ScreenMiner(player.inventory, miner));
                break;
            }
            case 8: {
                if (!(te instanceof TileEntityNuclearReactor)) break;
                TileEntityNuclearReactor reactor = (TileEntityNuclearReactor)te;
                mc.displayScreen((Screen)new ScreenNuclearReactor(player.inventory, reactor));
                break;
            }
            case 11: {
                if (!(te instanceof TileEntityPersonalChest)) break;
                TileEntityPersonalChest chest = (TileEntityPersonalChest)te;
                mc.displayScreen((Screen)new ScreenPersonalSafe(player.inventory, chest));
                break;
            }
            default: {
                IC2.LOGGER.warn("Unknown GUI type: {}", (Object)guiId);
            }
        }
        if (mc.thePlayer != null && mc.thePlayer.containerMenu != null) {
            mc.thePlayer.containerMenu.containerId = windowId;
        }
    }
}

