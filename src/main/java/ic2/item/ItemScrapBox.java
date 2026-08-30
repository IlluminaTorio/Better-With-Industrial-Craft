

package ic2.item;

import ic2.IC2Items;
import ic2.item.ItemIC2;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class ItemScrapBox
extends ItemIC2 {
    public ItemScrapBox(String name, String namespaceId, int id) {
        super(name, namespaceId, id);
    }

    public ItemStack onUse(@NotNull ItemStack selfStack, @NotNull World world, @NotNull Player player) {
        if (world.isClientSide) {
            return selfStack;
        }
        --selfStack.stackSize;
        ItemStack loot = ItemScrapBox.getRandomLoot();
        if (loot != null) {
            player.inventory.insertItem(loot, true);
        }
        return selfStack;
    }

    private static ItemStack getRandomLoot() {
        int r = (int)(Math.random() * 30.0);
        return switch (r) {
            case 0 -> new ItemStack(Items.FOOD_APPLE_GOLD);
            case 1 -> new ItemStack(Items.FOOD_COOKIE);
            case 2 -> new ItemStack(Items.FOOD_BREAD);
            case 3 -> new ItemStack(Items.INGOT_IRON);
            case 4 -> new ItemStack(Items.INGOT_GOLD);
            case 5 -> new ItemStack(Items.DIAMOND);
            case 6 -> new ItemStack(Items.BONE);
            case 7 -> new ItemStack(Items.GUNPOWDER);
            case 8 -> new ItemStack(Items.FEATHER_CHICKEN);
            case 9 -> new ItemStack(Items.STRING);
            case 10 -> new ItemStack(Items.WHEAT);
            case 11 -> new ItemStack(Items.SEEDS_WHEAT);
            case 12 -> new ItemStack(Items.STICK);
            case 13 -> new ItemStack(Items.AMMO_SNOWBALL);
            case 14 -> new ItemStack(Items.COAL);
            case 15 -> new ItemStack(Items.DUST_REDSTONE);
            case 16 -> new ItemStack(Items.FOOD_APPLE);
            case 17 -> new ItemStack(Items.SUGARCANE);
            case 18 -> new ItemStack(Blocks.COBBLE_STONE, 8);
            case 19 -> new ItemStack(Items.ORE_RAW_IRON, 2);
            case 20 -> new ItemStack(Items.ORE_RAW_GOLD, 2);
            case 21 -> new ItemStack(Items.INGOT_STEEL_CRUDE);
            case 22 -> new ItemStack(Items.AMMO_PEBBLE, 4);
            case 23 -> new ItemStack(Items.FOOD_FISH_RAW, 2);
            case 24 -> new ItemStack(IC2Items.dustIronSmall, 4);
            case 25 -> new ItemStack(IC2Items.dustCopper, 2);
            case 26 -> new ItemStack(IC2Items.dustTin, 2);
            case 27 -> new ItemStack(IC2Items.dustCoal, 2);
            case 28 -> new ItemStack(IC2Items.stickyResin, 2);
            default -> new ItemStack(Blocks.DIRT, 8);
        };
    }
}

