package ic2.block;

import ic2.IC2Items;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicFence;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;


public class BlockLogicIronFence extends BlockLogicFence {

        public BlockLogicIronFence(Block<?> block) {
                super(block);

                this.material = Materials.METAL;
        }


        private boolean isPole(WorldSource world, int x, int y, int z) {
                return world.getBlock(x - 1, y, z) != this.block
                                                && world.getBlock(x + 1, y, z) != this.block
                                                && world.getBlock(x, y, z - 1) != this.block
                                                && world.getBlock(x, y, z + 1) != this.block;
        }

        @Override
        public boolean canConnectTo(@NotNull WorldSource source, @NotNull TilePosc tilePos, @NotNull Side side) {
                if (side.isVertical()) return false;
                return source.getBlock(tilePos.x() + side.direction().offsetX(), tilePos.y(), tilePos.z() + side.direction().offsetZ())
                                                == this.block;
        }

        @Override
        public void onEntityCollision(@NotNull World world, @NotNull TilePosc tilePos, @NotNull net.minecraft.core.entity.Entity entity) {
                int x = tilePos.x(), y = tilePos.y(), z = tilePos.z();
                if (!this.isPole(world, x, y, z) || !(entity instanceof Player player)) {
                        return;
                }
                boolean powered = world.getBlockData(tilePos) > 0;
                boolean metalShoes = false;
                ItemStack shoes = player.inventory.armorInventory[0];
                if (shoes != null) {
                        int id = shoes.getItem().id;


                        if (id == Items.ARMOR_BOOTS_IRON.id
                                                        || id == Items.ARMOR_BOOTS_GOLD.id
                                                        || id == Items.ARMOR_BOOTS_DIAMOND.id
                                                        || id == Items.ARMOR_BOOTS_STEEL.id
                                                        || id == IC2Items.bronzeBoots.id
                                                        || id == IC2Items.nanoBoots.id
                                                        || id == IC2Items.quantumBoots.id) {
                                metalShoes = true;
                        }
                }
                if (!powered || !metalShoes) {
                        if (player.isSneaking()) {
                                if (player.yd < -0.25) {
                                        player.yd *= 0.9;
                                } else {
                                        player.fallDistance = 0.0f;
                                }
                        }
                } else {


                        int data = world.getBlockData(tilePos);
                        if (data > 0) {
                                if (world.isClientSide) {
                                        world.setBlockData(tilePos, data - 1);
                                } else {
                                        world.setBlockDataNotify(tilePos, data - 1);
                                }
                        }
                        player.yd += 0.075;
                        if (player.yd > 0.0) {
                                player.yd *= 1.03;
                                player.fallDistance = 0.0f;
                        }
                        if (player.isSneaking()) {
                                if (player.yd > 0.3) {
                                        player.yd = 0.3;
                                }
                        } else if (player.yd > 1.5) {
                                player.yd = 1.5;
                        }
                }
        }
}
