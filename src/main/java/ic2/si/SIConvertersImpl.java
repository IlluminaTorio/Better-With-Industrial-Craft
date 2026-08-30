

package ic2.si;

import ic2.IC2;
import ic2.IC2Blocks;
import net.minecraft.core.block.entity.TileEntityDispatcher;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.data.tag.Tag;
import net.minecraft.core.sound.BlockSounds;
import net.minecraft.core.util.collection.NamespaceID;
import net.fabricmc.loader.api.FabricLoader;
import turniplabs.halplibe.helper.BlockBuilder;

public final class SIConvertersImpl {
    private SIConvertersImpl() {
    }

    public static void register(int euToSignalumId, int signalumToEuId) {
        TileEntityDispatcher.addMapping(TileEntityEUToSignalum.class, (NamespaceID)IC2.id("converter_eu_to_catalyst"));
        TileEntityDispatcher.addMapping(TileEntitySignalumToEU.class, (NamespaceID)IC2.id("converter_catalyst_to_eu"));
        IC2Blocks.converterEuToCatalyst = new BlockBuilder(IC2.MOD_ID).setHardness(2.0f).setResistance(10.0f).setBlockSound(BlockSounds.METAL).addTags(new Tag[]{BlockTags.MINEABLE_BY_PICKAXE, Tag.of((String)"signalum_conduits_connect"), Tag.of((String)"fluid_conduits_connect")}).setTileEntity(TileEntityEUToSignalum::new).build("machine.converter_eu_to_catalyst", "converter_eu_to_catalyst", euToSignalumId, BlockLogicConverter::new);
        IC2Blocks.converterCatalystToEu = new BlockBuilder(IC2.MOD_ID).setHardness(2.0f).setResistance(10.0f).setBlockSound(BlockSounds.METAL).addTags(new Tag[]{BlockTags.MINEABLE_BY_PICKAXE, Tag.of((String)"signalum_conduits_connect"), Tag.of((String)"fluid_conduits_connect")}).setTileEntity(TileEntitySignalumToEU::new).build("machine.converter_catalyst_to_eu", "converter_catalyst_to_eu", signalumToEuId, BlockLogicConverter::new);
        IC2.LOGGER.info("Signal Industries energy converters registered (signalindustries found)");
    }

    
    public static void registerEnergy(int euToEnergyId, int energyToEuId) {
        TileEntityDispatcher.addMapping(TileEntityEUToCatalystEnergy.class, (NamespaceID)IC2.id("converter_eu_to_energy"));
        TileEntityDispatcher.addMapping(TileEntityCatalystEnergyToEU.class, (NamespaceID)IC2.id("converter_energy_to_eu"));
        IC2Blocks.converterEuToEnergy = new BlockBuilder(IC2.MOD_ID).setHardness(2.0f).setResistance(10.0f).setBlockSound(BlockSounds.METAL).addTags(new Tag[]{BlockTags.MINEABLE_BY_PICKAXE, Tag.of((String)"signalum_conduits_connect")}).setTileEntity(TileEntityEUToCatalystEnergy::new).build("machine.converter_eu_to_energy", "converter_eu_to_energy", euToEnergyId, BlockLogicCatalystEnergyNode::new);
        IC2Blocks.converterEnergyToEu = new BlockBuilder(IC2.MOD_ID).setHardness(2.0f).setResistance(10.0f).setBlockSound(BlockSounds.METAL).addTags(new Tag[]{BlockTags.MINEABLE_BY_PICKAXE, Tag.of((String)"signalum_conduits_connect")}).setTileEntity(TileEntityCatalystEnergyToEU::new).build("machine.converter_energy_to_eu", "converter_energy_to_eu", energyToEuId, BlockLogicCatalystEnergyNode::new);
        IC2.LOGGER.info("Catalyst universal energy converters registered (catalyst-energy found)");
    }

    public static boolean isCatalystEnergyInstalled() {
        return FabricLoader.getInstance().isModLoaded("catalyst-energy");
    }
}
