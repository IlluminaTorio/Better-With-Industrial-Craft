package ic2.si;

import sunsetsatellite.catalyst.core.util.Connection;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.fluids.api.IFluidInventory;
import sunsetsatellite.catalyst.fluids.util.Fluid;
import sunsetsatellite.catalyst.fluids.util.FluidStack;
import sunsetsatellite.catalyst.fluids.util.Fluids;
import sunsetsatellite.catalyst.core.util.io.IFluidIO;
import com.mojang.nbt.tags.CompoundTag;
import ic2.tileentity.TileEntityWaterGenerator;
import net.minecraft.core.block.entity.TileEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class WaterMillFluidAdapter extends TileEntity implements IFluidInventory, IFluidIO {

        private final TileEntityWaterGenerator mill;

        public WaterMillFluidAdapter(TileEntityWaterGenerator mill) {
                this.mill = mill;
        }

        @Override
        public void readAdditionalData(@NotNull CompoundTag tag) {
        }

        @Override
        public void writeAdditionalData(@NotNull CompoundTag tag) {
        }

	private static boolean isWater(FluidStack stack) {
		if (stack == null || stack.fluid == null) return false;
		Fluid water = Fluids.WATER;
		if (water != null && stack.fluid == water) return true;
		return "minecraft:water".equals(stack.fluid.id.toString());
	}


	@Override
	public boolean canInsertFluid(int slot, FluidStack stack) {
		return slot == 0 && isWater(stack) && this.mill.getFluidCapacityMillibuckets() > 0;
	}

	@Override
	public FluidStack insertFluid(int slot, FluidStack stack) {
		if (!this.canInsertFluid(slot, stack)) {
			return stack;
		}
		int accepted = this.mill.acceptWaterMillibuckets(stack.amount);
		if (accepted >= stack.amount) {
			return null;
		}
		return new FluidStack(stack.fluid, stack.amount - accepted);
	}

	@Override
	public FluidStack getFluidInSlot(int slot) {
		return null;
	}

	@Override
	public int getFluidCapacityForSlot(int slot) {
		return this.mill.getFluidCapacityMillibuckets();
	}

	@Override
	public ArrayList<Fluid> getAllowedFluidsForSlot(int slot) {
		ArrayList<Fluid> list = new ArrayList<>();
		if (Fluids.WATER != null) {
			list.add(Fluids.WATER);
		}
		return list;
	}

	@Override
	public void setFluidInSlot(int slot, FluidStack stack) {
		if (stack != null && isWater(stack)) {
			this.mill.acceptWaterMillibuckets(stack.amount);
		}
	}

	@Override
	public int getRemainingCapacity(int slot) {
		return this.mill.getFluidCapacityMillibuckets();
	}

	@Override
	public int getFluidInventorySize() {
		return 1;
	}

	@Override
	public void onFluidInventoryChanged() {
	}

	@Override
	public int getTransferSpeed() {
		return 1000;
	}


	@Override
	public int getActiveFluidSlotForSide(Direction direction) {
		return 0;
	}

	@Override
	public void setActiveFluidSlotForSide(Direction direction, int slot) {
	}

	@Override
	public Connection getFluidIOForSide(Direction direction) {
		return Connection.INPUT;
	}

	@Override
	public void setFluidIOForSide(Direction direction, Connection connection) {
	}

	@Override
	public void cycleFluidIOForSide(Direction direction) {
	}

	@Override
	public void cycleActiveFluidSlotForSide(Direction direction, boolean forward) {
	}
}
