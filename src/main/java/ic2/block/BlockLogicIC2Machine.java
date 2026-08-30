

package ic2.block;

import ic2.net.IC2Network;
import ic2.tileentity.TileEntityIC2Machine;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicRotatable;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockLogicIC2Machine
extends BlockLogicRotatable {
    public final int guiId;

    public BlockLogicIC2Machine(@NotNull Block<?> block, int guiId) {
        super(block, Materials.METAL);
        this.guiId = guiId;
    }

    public boolean onInteracted(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Player player, @Nullable Side side, double xHit, double yHit) {
        if (world.isClientSide) {
            return true;
        }
        
        if (tryChargeWithBattery(world, tilePos, player)) {
            return true;
        }
        TileEntity tile = world.getTileEntity(tilePos);
        if (tile instanceof TileEntityIC2Machine) {
            TileEntityIC2Machine machine = (TileEntityIC2Machine)tile;
            IC2Network.openMachineGui(player, machine, this.guiId);
        }
        return true;
    }

    
    private boolean tryChargeWithBattery(World world, TilePosc tilePos, Player player) {
        ItemStack held = player.getCurrentEquippedItem();
        if (held == null) return false;
        TileEntity te = world.getTileEntity(tilePos);
        if (!(te instanceof ic2.energy.IEnergySink sink)) return false;
        int free;
        if (te instanceof ic2.tileentity.TileEntityElecMachine em) {
            free = em.maxEnergy - em.energy;
        } else if (te instanceof ic2.tileentity.TileEntityElectricBlock eb) {
            free = eb.maxStorage - eb.energy;
        } else {
            return false;
        }
        if (free <= 0 || !sink.demandsEnergy()) return false;
        int given = 0;
        if (held.getItem() instanceof ic2.item.ItemBattery battery) {
            given = battery.getEnergyFrom(held, free, 3);
        } else if (held.getItem() == ic2.IC2Items.singleUseBattery) {
            given = Math.min(1000, free);
            if (given > 0) {
                --held.stackSize;
                if (held.stackSize <= 0) {
                    player.inventory.setItem(player.inventory.getCurrentSlot(), null);
                }
            }
        }
        if (given <= 0) return false;
        sink.injectEnergy(ic2.energy.Direction.YN, given);
        ((TileEntityIC2Machine)te).setChanged();
        world.playSoundEffect(player, net.minecraft.core.sound.SoundCategory.WORLD_SOUNDS,
                tilePos.x() + 0.5, tilePos.y() + 0.5, tilePos.z() + 0.5, "random.click", 0.5f, 1.8f);
        return true;
    }

    public @NotNull ItemStack @Nullable [] getBreakResult(@NotNull World world, @NotNull EnumDropCause dropCause, @NotNull TilePosc tilePos, int data, @Nullable TileEntity tileEntity) {
        ItemStack[] itemStackArray;
        
        if (dropCause != EnumDropCause.PICK_BLOCK && tileEntity instanceof TileEntityIC2Machine) {
            TileEntityIC2Machine machine = (TileEntityIC2Machine)tileEntity;
            machine.dropContents(world, tilePos.x(), tilePos.y(), tilePos.z());
        }
        switch (dropCause) {
            case PICK_BLOCK: 
            case EXPLOSION: 
            case PROPER_TOOL: 
            case SILK_TOUCH: 
            case PISTON_CRUSH: {
                ItemStack[] itemStackArray2 = new ItemStack[1];
                itemStackArray = itemStackArray2;
                itemStackArray2[0] = this.block.getDefaultStack();
                break;
            }
            default: {
                itemStackArray = null;
            }
        }
        return itemStackArray;
    }

    public boolean dismantleWithWrench(World world, TilePosc pos, Player player) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityIC2Machine) {
            TileEntityIC2Machine machine = (TileEntityIC2Machine)te;
            machine.dropContents(world, pos.x(), pos.y(), pos.z());
        }
        world.setBlockWithNotify(pos.x(), pos.y(), pos.z(), 0);
        EntityItem dropped = new EntityItem(world, (double)pos.x() + 0.5, (double)pos.y() + 0.5, (double)pos.z() + 0.5, this.block.getDefaultStack());
        if (!world.isClientSide) {
            world.entityJoinedWorld((Entity)dropped);
        }
        world.playSoundEffect((Entity)player, SoundCategory.WORLD_SOUNDS, (double)pos.x() + 0.5, (double)pos.y() + 0.5, (double)pos.z() + 0.5, "random.click", 0.6f, 1.0f);
        return true;
    }

    public static Direction getFacing(World world, TilePosc pos) {
        return BlockLogicRotatable.getDirectionFromMeta((int)world.getBlockData(pos));
    }
}

