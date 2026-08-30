package ic2.mixin.catalyst;

import ic2.si.WaterMillFluidAdapter;
import ic2.tileentity.TileEntityWaterGenerator;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.world.WorldSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.fluids.impl.tile.TileEntityFluidContainer;


@Mixin(value = TileEntityFluidContainer.class, remap = false)
public class FluidPipeWaterMillMixin {

	@Redirect(method = {"give(Lsunsetsatellite/catalyst/core/util/Direction;)V",
					"give(Lsunsetsatellite/catalyst/core/util/Direction;II)V"},
					at = @At(value = "INVOKE",
									target = "Lsunsetsatellite/catalyst/core/util/Direction;getTileEntity(Lnet/minecraft/core/world/WorldSource;Lnet/minecraft/core/block/entity/TileEntity;)Lnet/minecraft/core/block/entity/TileEntity;"))
	private TileEntity ic2$adaptWaterMill(Direction instance, WorldSource world, TileEntity from) {
		TileEntity neighbor = instance.getTileEntity(world, from);
		if (neighbor instanceof TileEntityWaterGenerator mill) {
			return new WaterMillFluidAdapter(mill);
		}
		return neighbor;
	}
}
