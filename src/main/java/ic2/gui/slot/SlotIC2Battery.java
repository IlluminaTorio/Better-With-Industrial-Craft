package ic2.gui.slot;

import ic2.IC2Items;
import ic2.item.ItemBattery;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.slot.Slot;

public class SlotIC2Battery extends Slot {
	public SlotIC2Battery(Container container, int index, int x, int y) {
		super(container, index, x, y);
	}

	public static boolean isEnergySource(ItemStack stack) {
		return stack != null
			&& (stack.getItem() instanceof ItemBattery || stack.getItem() == IC2Items.singleUseBattery || stack.getItem() == Items.DUST_REDSTONE);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return super.mayPlace(stack) && isEnergySource(stack);
	}
}
