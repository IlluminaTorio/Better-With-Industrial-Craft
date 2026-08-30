

package ic2;

import ic2.IC2Blocks;
import ic2.IC2Items;
import ic2.IC2Recipes;
import ic2.entity.EntityDynamite;
import ic2.entity.EntityIC2Explosive;
import ic2.net.IC2Network;
import ic2.tileentity.TileEntityCable;
import ic2.tileentity.TileEntityCanner;
import ic2.tileentity.TileEntityCompressor;
import ic2.tileentity.TileEntityElecFurnace;
import ic2.tileentity.TileEntityElectricBatBox;
import ic2.tileentity.TileEntityElectricMFE;
import ic2.tileentity.TileEntityElectricMFSU;
import ic2.tileentity.TileEntityElectrolyzer;
import ic2.tileentity.TileEntityExtractor;
import ic2.tileentity.TileEntityGenerator;
import ic2.tileentity.TileEntityGeoGenerator;
import ic2.tileentity.TileEntityIC2Block;
import ic2.tileentity.TileEntityInduction;
import ic2.tileentity.TileEntityIronFurnace;
import ic2.tileentity.TileEntityLuminator;
import ic2.tileentity.TileEntityMacerator;
import ic2.tileentity.TileEntityMagnetizer;
import ic2.tileentity.TileEntityMatter;
import ic2.tileentity.TileEntityMiner;
import ic2.tileentity.TileEntityNuclearReactor;
import ic2.tileentity.TileEntityPersonalChest;
import ic2.tileentity.TileEntityPump;
import ic2.tileentity.TileEntityReactorChamber;
import ic2.tileentity.TileEntityRecycler;
import ic2.tileentity.TileEntitySolarGenerator;
import ic2.tileentity.TileEntityTeleporter;
import ic2.tileentity.TileEntityTerraformer;
import ic2.tileentity.TileEntityTesla;
import ic2.tileentity.TileEntityTradeOMat;
import ic2.tileentity.TileEntityTransformerHV;
import ic2.tileentity.TileEntityTransformerLV;
import ic2.tileentity.TileEntityTransformerMV;
import ic2.tileentity.TileEntityWaterGenerator;
import ic2.tileentity.TileEntityWindGenerator;
import ic2.util.IC2Creative;
import ic2.util.IC2Tags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.block.entity.TileEntityDispatcher;
import net.minecraft.core.entity.EntityDispatcher;
import net.minecraft.core.util.collection.NamespaceID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.HalpLibe;
import turniplabs.halplibe.event.defs.CommonEvents;
import turniplabs.halplibe.util.dependency.Key;

public class IC2
implements ModInitializer {
    public static final String MOD_ID = HalpLibe.registerMod((String)"ic2", (boolean)true);
    public static final Logger LOGGER = LoggerFactory.getLogger((String)MOD_ID);

    public void onInitialize() {
        LOGGER.info("IndustrialCraft 2 (BTA Edition) initializing...");
        CommonEvents.BEFORE_GAME_START.listen(Key.of((String)MOD_ID), this::beforeGameStart);
        CommonEvents.AFTER_GAME_START.listen(Key.of((String)MOD_ID), this::afterGameStart);
        CommonEvents.AFTER_BLOCK_INIT.listen(Key.of((String)MOD_ID), IC2Blocks::init);
        CommonEvents.AFTER_ITEM_INIT.listen(Key.of((String)MOD_ID), this::afterItemsInit);
        CommonEvents.RECIPES_NAMESPACE_INIT.listen(Key.of((String)MOD_ID), IC2Recipes::initNamespaces);
        CommonEvents.RECIPES_READY.listen(Key.of((String)MOD_ID), IC2Recipes::onRecipesReady);
        IC2Network.init();
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            try {
                IC2Network.GuiOpener opener = (IC2Network.GuiOpener)Class.forName("ic2.net.ServerGuiOpener").getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                IC2Network.setOpener(opener);
            }
            catch (ReflectiveOperationException e) {
                LOGGER.error("Failed to init server GUI opener", (Throwable)e);
            }
        }
        this.registerTileEntities();
        this.registerEntities();
        LOGGER.info("IndustrialCraft 2 core loaded.");
    }

    private void registerTileEntities() {
        TileEntityDispatcher.addMapping(TileEntityIC2Block.class, (NamespaceID)IC2.id("ic2_base"));
        TileEntityDispatcher.addMapping(TileEntityIronFurnace.class, (NamespaceID)IC2.id("iron_furnace"));
        TileEntityDispatcher.addMapping(TileEntityElecFurnace.class, (NamespaceID)IC2.id("elec_furnace"));
        TileEntityDispatcher.addMapping(TileEntityMacerator.class, (NamespaceID)IC2.id("macerator"));
        TileEntityDispatcher.addMapping(TileEntityExtractor.class, (NamespaceID)IC2.id("extractor"));
        TileEntityDispatcher.addMapping(TileEntityCompressor.class, (NamespaceID)IC2.id("compressor"));
        TileEntityDispatcher.addMapping(TileEntityCanner.class, (NamespaceID)IC2.id("canner"));
        TileEntityDispatcher.addMapping(TileEntityRecycler.class, (NamespaceID)IC2.id("recycler"));
        TileEntityDispatcher.addMapping(TileEntityElectrolyzer.class, (NamespaceID)IC2.id("electrolyzer"));
        TileEntityDispatcher.addMapping(TileEntityInduction.class, (NamespaceID)IC2.id("induction"));
        TileEntityDispatcher.addMapping(TileEntityMatter.class, (NamespaceID)IC2.id("mass_fabricator"));
        TileEntityDispatcher.addMapping(TileEntityTerraformer.class, (NamespaceID)IC2.id("terraformer"));
        TileEntityDispatcher.addMapping(TileEntityMiner.class, (NamespaceID)IC2.id("miner"));
        TileEntityDispatcher.addMapping(TileEntityPump.class, (NamespaceID)IC2.id("pump"));
        TileEntityDispatcher.addMapping(TileEntityMagnetizer.class, (NamespaceID)IC2.id("magnetizer"));
        TileEntityDispatcher.addMapping(TileEntityGenerator.class, (NamespaceID)IC2.id("generator"));
        TileEntityDispatcher.addMapping(TileEntityGeoGenerator.class, (NamespaceID)IC2.id("geothermal_generator"));
        TileEntityDispatcher.addMapping(TileEntityWaterGenerator.class, (NamespaceID)IC2.id("water_mill"));
        TileEntityDispatcher.addMapping(TileEntitySolarGenerator.class, (NamespaceID)IC2.id("solar_panel"));
        TileEntityDispatcher.addMapping(TileEntityWindGenerator.class, (NamespaceID)IC2.id("wind_mill"));
        TileEntityDispatcher.addMapping(TileEntityNuclearReactor.class, (NamespaceID)IC2.id("nuclear_reactor"));
        TileEntityDispatcher.addMapping(TileEntityReactorChamber.class, (NamespaceID)IC2.id("reactor_chamber"));
        TileEntityDispatcher.addMapping(TileEntityElectricBatBox.class, (NamespaceID)IC2.id("batbox"));
        TileEntityDispatcher.addMapping(TileEntityElectricMFE.class, (NamespaceID)IC2.id("mfe"));
        TileEntityDispatcher.addMapping(TileEntityElectricMFSU.class, (NamespaceID)IC2.id("mfsu"));
        TileEntityDispatcher.addMapping(TileEntityTransformerLV.class, (NamespaceID)IC2.id("transformer_lv"));
        TileEntityDispatcher.addMapping(TileEntityTransformerMV.class, (NamespaceID)IC2.id("transformer_mv"));
        TileEntityDispatcher.addMapping(TileEntityTransformerHV.class, (NamespaceID)IC2.id("transformer_hv"));
        TileEntityDispatcher.addMapping(TileEntityCable.class, (NamespaceID)IC2.id("cable"));
        TileEntityDispatcher.addMapping(TileEntityLuminator.class, (NamespaceID)IC2.id("luminator"));
        TileEntityDispatcher.addMapping(TileEntityPersonalChest.class, (NamespaceID)IC2.id("personal_safe"));
        TileEntityDispatcher.addMapping(TileEntityTradeOMat.class, (NamespaceID)IC2.id("trade_o_mat"));
        TileEntityDispatcher.addMapping(TileEntityTeleporter.class, (NamespaceID)IC2.id("teleporter"));
        TileEntityDispatcher.addMapping(TileEntityTesla.class, (NamespaceID)IC2.id("tesla_coil"));
    }

    public static NamespaceID id(String id) {
        return NamespaceID.fromPool((String)MOD_ID, (String)id);
    }

    private void registerEntities() {
        EntityDispatcher.getInstance().addMapping(EntityIC2Explosive.class, IC2.id("ic2_explosive"), EntityIC2Explosive::new);
        EntityDispatcher.getInstance().addMapping(EntityDynamite.class, IC2.id("dynamite_thrown"), EntityDynamite::new);
    }

    public static String key(String key) {
        return MOD_ID + ":" + key;
    }

    private void afterItemsInit() {
        IC2Items.init();
        IC2Creative.init();
        IC2Tags.init();
    }

    public void beforeGameStart() {
    }

    public void afterGameStart() {
        LOGGER.info("IndustrialCraft 2 loaded. Remember: safety first!");
    }
}

