package ic2.item.tool;

import ic2.IC2Items;
import ic2.item.ElectricItem;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;


public class ItemNanoSaber extends ElectricItem {
	public final boolean active;
	public int soundTicker = 0;
	public static int ticker = 0;

	public ItemNanoSaber(String name, String namespaceId, int id, boolean active) {
		super(name, namespaceId, id, 2, 8, 128);
		this.setMaxDamage(5002);
		this.setMaxStackSize(1);
		this.active = active;
	}

	@Override
	public int getDamageVsEntity(@NotNull ItemStack selfStack, @NotNull net.minecraft.core.entity.Entity entity) {
		return this.active ? 16 : 3;
	}

	@Override
	public float getStrVsBlock(@NotNull ItemStack selfStack, @NotNull net.minecraft.core.block.Block<?> block) {
		return this.active ? 4.0f : 1.0f;
	}

	@Override
	public boolean hitEntity(@NotNull ItemStack selfStack, @NotNull Mob target, @NotNull Mob attacker) {
		if (!this.active) {
			return true;
		}

		if (target instanceof Player enemy) {
			for (int i = 0; i < 4; ++i) {
				ItemStack armor = enemy.inventory.armorInventory[i];
				if (armor != null && armor.getItem() instanceof ic2.item.armor.ItemArmorNano) {
					armor.damageItem(10, enemy);
					drainSaber(selfStack, 2);
				}
			}
		}
		drainSaber(selfStack, 5);
		return true;
	}

	@Override
	public boolean onBlockDestroyed(@NotNull ItemStack selfStack, @NotNull World world, @NotNull Mob mob,
					@NotNull net.minecraft.core.block.Block<?> block, @NotNull net.minecraft.core.world.pos.TilePosc pos,
					@NotNull net.minecraft.core.util.helper.Side side) {
		if (!this.active) {
			return true;
		}
		drainSaber(selfStack, 10);
		return true;
	}


	@Override
	public net.minecraft.core.item.ItemStack onUse(@NotNull ItemStack itemstack, @NotNull World world, @NotNull Player entityplayer) {
		if (world.isClientSide) {
			return itemstack;
		}
		if (this.active) {
			itemstack.itemID = IC2Items.nanoSaberOff.id;
		} else if (itemstack.getMetadata() < itemstack.getMaxDamage() - 1) {
			itemstack.itemID = IC2Items.nanoSaber.id;
			world.playSoundEffect(entityplayer, net.minecraft.core.sound.SoundCategory.ENTITY_SOUNDS,
							entityplayer.x, entityplayer.y, entityplayer.z, "random.click", 0.8f, 1.6f);
		}
		return itemstack;
	}


	public static void timedLoss(Player player) {
		if (++ticker % 16 != 0) {
			return;
		}
		ItemStack[] inv = player.inventory.mainInventory;
		if (ticker % 64 == 0) {
			for (int i = 9; i < inv.length; ++i) {
				if (inv[i] != null && inv[i].getItem() == IC2Items.nanoSaber) {
					drainSaber(inv[i], 64);
				}
			}
		}
		for (int i = 0; i < 9; ++i) {
			if (inv[i] != null && inv[i].getItem() == IC2Items.nanoSaber) {
				drainSaber(inv[i], 16);
			}
		}
	}


	public static void drainSaber(ItemStack saber, int damage) {
		if (saber.getMetadata() + damage >= saber.getMaxDamage() - 1) {
			saber.itemID = IC2Items.nanoSaberOff.id;
			saber.setMetadata(saber.getMaxDamage() - 1);
		} else {
			saber.damageItem(damage, null);
		}
	}

	@Override
	public boolean isDamagable() {
		return false;
	}
}
