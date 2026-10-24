package ic2.gui.slot;

import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.slot.Slot;

public class SlotIC2Input extends Slot {
	public SlotIC2Input(Container container, int index, int x, int y) {
		super(container, index, x, y);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return super.mayPlace(stack) && !SlotIC2Battery.isEnergySource(stack);
	}
}
