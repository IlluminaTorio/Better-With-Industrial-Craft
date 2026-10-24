package ic2.si;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.material.Materials;
import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.core.util.network.NetworkComponent;
import sunsetsatellite.catalyst.core.util.network.NetworkType;


public class BlockLogicCatalystEnergyNode
extends BlockLogic
implements NetworkComponent {

	public BlockLogicCatalystEnergyNode(@NotNull Block<?> block) {
		super(block, Materials.METAL);
	}

	@Override
	public NetworkType getType() {
		return NetworkType.CATALYST_ENERGY;
	}
}
