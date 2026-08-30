

package ic2.client;

import ic2.IC2;
import ic2.IC2Language;
import ic2.client.ClientGuiOpener;
import ic2.client.IC2Models;
import ic2.client.SingleplayerTest;
import ic2.net.IC2Network;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.EntityRendererDispatcher;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import turniplabs.halplibe.event.defs.ClientEvents;
import turniplabs.halplibe.util.dependency.Key;

public class IC2Client
implements ClientModInitializer {
    public void onInitializeClient() {
        boolean spTest;
        ClientEvents.BLOCK_MODEL_RELOAD.listen(Key.of((String)IC2.MOD_ID), t -> new IC2Models().initBlockModels((BlockModelDispatcher)t));
        ClientEvents.ITEM_MODEL_RELOAD.listen(Key.of((String)IC2.MOD_ID), t -> new IC2Models().initItemModels((ItemModelDispatcher)t));
        ClientEvents.ENTITY_RENDERER_RELOAD.listen(Key.of((String)IC2.MOD_ID), t -> new IC2Models().initEntityRenderers((EntityRendererDispatcher)t));
        ClientEvents.BLOCK_COLOR_RELOAD.listen(Key.of((String)IC2.MOD_ID), t -> new IC2Models().initBlockColors((BlockColorDispatcher)t));
        IC2Network.setOpener(new ClientGuiOpener());
        IC2Language.installLanguagePack();
        boolean bl = spTest = System.getProperty("ic2.spTest") != null || "1".equals(System.getenv("IC2_SPTEST"));
        if (spTest) {
            IC2.LOGGER.info("[SP-TEST] harness armed, starting...");
            SingleplayerTest.start();
        }
    }
}

