

package ic2.item.tool;

import ic2.entity.EntityMiningLaser;
import ic2.item.tool.ItemElectricTool;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;

public class ItemMiningLaser
extends ItemElectricTool {
    public static int setting = 0;
    private static final String[] MODES = new String[]{"Mining", "Low-Focus", "Long-Range", "Scatter", "Explosive"};
    private static final int[] CONSUME = new int[]{62, 5, 500, 1000, 500};
    public static final int RATIO = 10;

    public ItemMiningLaser(String name, String namespaceId, int id) {
        super(name, namespaceId, id, 3, 2, 10, 40, 8002);
        this.setMaxStackSize(1);
    }

    @Override
    protected boolean isEffective(Block<?> block) {
        return false;
    }

    public int getDamageVsEntity(@NotNull ItemStack selfStack, @NotNull Entity entity) {
        return 10;
    }

    @Override
    protected float getEfficiency() {
        return 1.0f;
    }

    @Nullable
    public ItemStack onUse(@NotNull ItemStack selfStack, @NotNull World world, @NotNull Player player) {
        if (world.isClientSide) {
            return selfStack;
        }
        if (Keyboard.isKeyDown((int)Keyboard.KEY_B)) {
            setting = (setting + 1) % 5;
            player.sendMessage(TextFormatting.Base.GRAY, "Laser Mode: " + MODES[setting]);
        } else {
            int consume = CONSUME[setting];
            if (!this.use(selfStack, consume, (Mob)player)) {
                return selfStack;
            }
            switch (setting) {
                case 0: {
                    world.entityJoinedWorld((Entity)new EntityMiningLaser(world, (Mob)player, 8.0f, false));
                    break;
                }
                case 1: {
                    world.entityJoinedWorld((Entity)new EntityMiningLaser(world, (Mob)player, 1.5f, false));
                    break;
                }
                case 2: {
                    world.entityJoinedWorld((Entity)new EntityMiningLaser(world, (Mob)player, 32.0f, false));
                    break;
                }
                case 3: {
                    for (int x = -2; x <= 2; ++x) {
                        for (int z = -2; z <= 2; ++z) {
                            world.entityJoinedWorld((Entity)new EntityMiningLaser(world, (Mob)player, 6.0f, false, player.yRot + 20.0f * (float)x, player.xRot + 20.0f * (float)z));
                        }
                    }
                    break;
                }
                case 4: {
                    world.entityJoinedWorld((Entity)new EntityMiningLaser(world, (Mob)player, 12.0f, true));
                }
            }
            world.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, player.x, player.y, player.z, "random.fizz", 1.0f, 1.0f);
        }
        return selfStack;
    }
}

