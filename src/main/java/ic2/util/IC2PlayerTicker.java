

package ic2.util;

import ic2.IC2;
import ic2.IC2Items;
import ic2.item.armor.ItemArmorChargeable;
import ic2.item.armor.ItemArmorJetpackSuit;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.HumanArmorShape;
import net.minecraft.core.enums.IArmorShape;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.DamageType;
import org.lwjgl.input.Keyboard;

public final class IC2PlayerTicker {
    private static final int KEY_HOVER = 35;
    private static final int KEY_SUIT_ACTIVATE = 29;
    private static final int KEY_FORWARD = 17;
    public static boolean hoverMode = false;
    public static int toggleTimer = 0;
    public static int jetpackSoundTicker = 0;
    private static float rubberFallStorage = 0.0f;

    private IC2PlayerTicker() {
    }

    public static void tick(Player player) {
        if (player == null || player.world == null) {
            return;
        }
        boolean client = player.world.isClientSide;
        try {
            IC2PlayerTicker.tickJetpack(player, client);
            IC2PlayerTicker.tickQuantumSuit(player, client);
            IC2PlayerTicker.tickRubberBoots(player);

            if (!client) {
                ic2.item.tool.ItemNanoSaber.timedLoss(player);
            }
        }
        catch (Throwable t) {
            IC2.LOGGER.warn("IC2 player ticker error: {}", (Object)t.toString());
        }
    }

    private static void tickJetpack(Player player, boolean client) {
        boolean falling;
        boolean electric;
        ItemStack chest = player.inventory.armorItemInSlot((IArmorShape)HumanArmorShape.CHEST);
        if (chest == null) {
            return;
        }
        Item item = chest.getItem();
        if (!(item instanceof ItemArmorChargeable)) {
            return;
        }
        ItemArmorChargeable armor = (ItemArmorChargeable)item;
        boolean suit = item instanceof ItemArmorJetpackSuit;
        boolean bl = electric = chest.getItem() == IC2Items.electricJetpack || suit && ((ItemArmorJetpackSuit)item).electric;
        if (chest.getItem() != IC2Items.jetpack && !(suit && !((ItemArmorJetpackSuit)item).electric) && !electric) {
            return;
        }
        if (toggleTimer > 0) {
            --toggleTimer;
        }
        if (client && toggleTimer <= 0 && Keyboard.isKeyDown((int)35)) {
            IC2PlayerTicker.toggleHover(player);
        }
        boolean jumping = player.isJumping();
        boolean bl2 = falling = player.yd < -0.35;
        if (!(jumping || electric && hoverMode && falling)) {
            return;
        }
        IC2PlayerTicker.useJetpack(player, armor, chest, electric, client);
    }

    private static void toggleHover(Player player) {
        hoverMode = !hoverMode;
        toggleTimer = 10;
        player.sendMessage(TextFormatting.Base.GRAY, "Hover Mode " + (hoverMode ? "activated" : "deactivated") + ".");
    }

    private static void useJetpack(Player player, ItemArmorChargeable armor, ItemStack chest, boolean electric, boolean client) {
        int meta;
        float threshold;
        if (chest.getMetadata() >= chest.getMaxDamage() + 1) {
            return;
        }
        float power = electric ? 0.7f : 1.0f;
        float dropPercentage = electric ? 0.05f : 0.2f;
        float remain = chest.getMaxDamage() + 1 - chest.getMetadata();
        if (remain <= (threshold = (float)(chest.getMaxDamage() + 1) * dropPercentage)) {
            power *= Math.max(0.0f, remain / threshold);
        }
        if (client && Keyboard.isKeyDown((int)17)) {
            float forwardPower;
            float retruster;
            float f = retruster = hoverMode ? 0.5f : 0.15f;
            if (electric) {
                retruster += 0.15f;
            }
            if ((forwardPower = power * retruster * 2.0f) > 0.0f) {
                player.moveRelative(0.0f, 0.4f * forwardPower, 0.02f);
            }
        }
        int worldHeight = player.world.getHeightBlocks();
        double y = player.y;
        int maxFlightHeight = electric ? worldHeight - 28 : worldHeight;
        if (y > (double)(maxFlightHeight - 25)) {
            if (y > (double)maxFlightHeight) {
                y = maxFlightHeight;
            }
            power *= (float)(((double)maxFlightHeight - y) / 25.0);
        }
        double prevYd = player.yd;
        player.yd += 0.2 * (double)power;
        if (player.yd > 0.6) {
            player.yd = 0.6;
        }
        if (hoverMode) {
            float maxHoverY;
            float f = maxHoverY = electric && player.isJumping() ? 0.1f : -0.1f;
            if (player.yd > (double)maxHoverY) {
                player.yd = maxHoverY;
                if (prevYd > player.yd) {
                    player.yd = prevYd;
                }
            }
        }
        int consume = 9;
        if (hoverMode) {
            consume = 6;
        }
        if (electric) {
            consume -= 2;
        }
        if ((meta = chest.getMetadata() + consume) > chest.getMaxDamage() + 1) {
            meta = chest.getMaxDamage() + 1;
        }
        chest.setMetadata(meta);
        if (player.yd > -0.35) {
            player.fallDistance = 0.0f;
        }
        player.remainingFireTicks = 0;
        if (++jetpackSoundTicker % 16 == 0) {
            player.world.playSoundEffect((Entity)player, SoundCategory.WORLD_SOUNDS, player.x, player.y, player.z, "random.fuse", 0.5f, 1.4f);
        }
    }

    private static void tickQuantumSuit(Player player, boolean client) {
        boolean hasBoots;
        ItemStack helmet = player.inventory.armorItemInSlot((IArmorShape)HumanArmorShape.HEAD);
        ItemStack chest = player.inventory.armorItemInSlot((IArmorShape)HumanArmorShape.CHEST);
        ItemStack legs = player.inventory.armorItemInSlot((IArmorShape)HumanArmorShape.LEGS);
        ItemStack boots = player.inventory.armorItemInSlot((IArmorShape)HumanArmorShape.BOOTS);
        boolean hasHelmet = helmet != null && helmet.getItem() == IC2Items.quantumHelmet;
        boolean hasChest = chest != null && (chest.getItem() == IC2Items.quantumBodyarmor || chest.getItem() == IC2Items.jetpackQuantum || chest.getItem() == IC2Items.electricJetpackQuantum);
        boolean hasLegs = legs != null && legs.getItem() == IC2Items.quantumLeggings;
        boolean bl = hasBoots = boots != null && boots.getItem() == IC2Items.quantumBoots;
        if (hasHelmet && player.isInWaterOrRain() && player.airSupply < 300 && IC2PlayerTicker.hasCharge(helmet)) {
            player.airSupply = 300;
            IC2PlayerTicker.useCharge(helmet, 1);
        }
        if (hasChest && IC2PlayerTicker.hasCharge(chest)) {
            player.remainingFireTicks = 0;
        }
        if (hasLegs && IC2PlayerTicker.hasCharge(legs) && client && Keyboard.isKeyDown((int)29) && Keyboard.isKeyDown((int)17)) {
            player.moveRelative(0.0f, 1.0f, 0.25f);
            if (player.world.getWorldTime() % 10L == 0L) {
                IC2PlayerTicker.useCharge(legs, 1);
            }
        }
        if (hasBoots && player.fallDistance > 1.0f && IC2PlayerTicker.hasCharge(boots)) {
            player.fallDistance = 0.0f;
            IC2PlayerTicker.useCharge(boots, 1);
        }
    }

    private static boolean hasCharge(ItemStack armor) {
        return armor.getMetadata() <= armor.getMaxDamage();
    }

    private static void useCharge(ItemStack armor, int meta) {
        int m = armor.getMetadata() + meta;
        if (m > armor.getMaxDamage() + 1) {
            m = armor.getMaxDamage() + 1;
        }
        armor.setMetadata(m);
    }

    private static void tickRubberBoots(Player player) {
        ItemStack boots = player.inventory.armorItemInSlot((IArmorShape)HumanArmorShape.BOOTS);
        if (boots == null || boots.getItem() != IC2Items.rubberBoots) {
            rubberFallStorage = 0.0f;
            return;
        }
        float fallDistance = player.fallDistance;
        if (fallDistance < 1.0f && rubberFallStorage == 0.0f) {
            return;
        }
        if (rubberFallStorage > 0.0f && player.onGround) {
            rubberFallStorage = 0.0f;
        }
        if (fallDistance >= 1.0f) {
            player.fallDistance = fallDistance - 1.0f;
            rubberFallStorage += 1.0f;
        }
        if (player.onGround) {
            if (rubberFallStorage < 3.0f) {
                rubberFallStorage = 0.0f;
            } else {
                int damage = (int)Math.ceil(rubberFallStorage - 3.0f);
                damage = (damage + 1) / 2;
                boots.damageItem(damage, (Entity)player);
                if (boots.stackSize <= 0) {
                    player.setItemInArmorSlot(HumanArmorShape.BOOTS, null);
                }
                if (damage >= 4) {
                    player.hurt(null, damage / 4, DamageType.FALL);
                }
                rubberFallStorage = 0.0f;
            }
        }
    }
}

