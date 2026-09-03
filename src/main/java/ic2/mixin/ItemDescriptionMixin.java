

package ic2.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import ic2.util.IC2EnergyTiers;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.lang.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;







@Mixin(value = {Item.class}, remap = false)
public abstract class ItemDescriptionMixin {

	@ModifyReturnValue(
		method = {"getTranslatedDescription(Lnet/minecraft/core/item/ItemStack;)Ljava/lang/String;"},
		at = {@At("RETURN")}
	)
	private String ic2$prependEnergyTier(String original, @Local(argsOnly = true) ItemStack stack) {
		if (stack == null || original == null || original.isEmpty()) {
			return original;
		}
		int tier = IC2EnergyTiers.tierForStack(stack);
		if (tier <= 0) {
			return original;
		}
		String line = IC2EnergyTiers.tooltipLine(tier, I18n.getInstance().translateKey("gui.ic2.energy_level"));
		if (line == null) {
			return original;
		}
		return line + "\n" + original;
	}
}
