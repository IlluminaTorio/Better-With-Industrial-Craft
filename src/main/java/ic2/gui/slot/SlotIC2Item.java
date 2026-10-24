package ic2.gui.slot;

import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.slot.Slot;

public class SlotIC2Item extends Slot {
	private final Item[] allowed;

	public SlotIC2Item(Container container, int index, int x, int y, Item... allowed) {
		super(container, index, x, y);
		this.allowed = allowed;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		if (stack == null) {
			return false;
		}
		for (Item item : this.allowed) {
			if (stack.getItem() == item) {
				return super.mayPlace(stack);
			}
		}
		return false;
	}
}
