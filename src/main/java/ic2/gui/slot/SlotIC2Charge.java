package ic2.gui.slot;

import ic2.energy.IChargeableItem;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.slot.Slot;

public class SlotIC2Charge extends Slot {
	public SlotIC2Charge(Container container, int index, int x, int y) {
		super(container, index, x, y);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return super.mayPlace(stack) && stack != null && stack.getItem() instanceof IChargeableItem;
	}
}
