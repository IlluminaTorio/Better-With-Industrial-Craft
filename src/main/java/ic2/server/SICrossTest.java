package ic2.server;

import ic2.IC2;
import ic2.IC2Blocks;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;






public final class SICrossTest {
    private SICrossTest() {
    }

    public static void run(World world, int px, int py, int pz) {
        try {
            Class<?> sib = Class.forName("sunsetsatellite.signalindustries.SIBlocks");
            Block<?> conduit = (Block<?>)field(sib, "basicConduit").get(null);
            Block<?> dynamo = (Block<?>)field(sib, "basicSignalumDynamo").get(null);
            Block<?> cell = (Block<?>)field(sib, "basicEnergyCell").get(null);
            check("si blocks resolved", conduit != null && dynamo != null && cell != null, "blocks");
        } catch (Throwable t) {
            check("si classes present", false, String.valueOf(t));
            return;
        }

        int baseX = px + 40;
        int baseY = py;
        int baseZ = pz;

        try {
            Class<?> sib = Class.forName("sunsetsatellite.signalindustries.SIBlocks");
            Block<?> conduit = (Block<?>)field(sib, "basicConduit").get(null);
            Block<?> dynamo = (Block<?>)field(sib, "basicSignalumDynamo").get(null);
            Block<?> cell = (Block<?>)field(sib, "basicEnergyCell").get(null);
            Block<?> converter = IC2Blocks.converterCatalystToEu;


            place(world, baseX, baseY, baseZ, "si_cell", cell);
            place(world, baseX, baseY + 1, baseZ, "si_conduit", conduit);
            place(world, baseX, baseY + 2, baseZ, "si_dynamo", dynamo);
            if (converter != null) {
                place(world, baseX + 1, baseY + 1, baseZ, "ic2_catalyst_to_eu", converter);
            } else {
                log("  converter block missing, skipped");
            }

            TileEntity convTe = world.getTileEntity(new TilePos(baseX + 1, baseY + 1, baseZ));
            check("ic2 converter tile entity alive next to si conduit", convTe != null,
                "te=" + (convTe == null ? "null" : convTe.getClass().getSimpleName()));
            check("si conduit tile entity alive", world.getTileEntity(new TilePos(baseX, baseY + 1, baseZ)) != null, "te");
            check("si dynamo tile entity alive", world.getTileEntity(new TilePos(baseX, baseY + 2, baseZ)) != null, "te");
            check("si cell tile entity alive", world.getTileEntity(new TilePos(baseX, baseY, baseZ)) != null, "te");
        } catch (Throwable t) {
            check("si cross test", false, String.valueOf(t));
        }
    }

    private static Field field(Class<?> c, String name) throws NoSuchFieldException {
        Field f = c.getDeclaredField(name);
        f.setAccessible(true);
        return f;
    }

    private static void place(World world, int x, int y, int z, String key, Block<?> b) {
        if (b == null) {
            log("  block missing: " + key);
            return;
        }
        if (!world.isChunkLoaded(x >> 4, z >> 4)) {
            world.getChunkFromBlockCoords(x, z);
        }
        world.setBlockWithNotify(x, y, z, b.id());
        if (world.getBlockId(x, y, z) != b.id()) {
            log("  RETRY placing " + key);
            world.getChunkFromBlockCoords(x, z);
            world.setBlockWithNotify(x, y, z, b.id());
        }
        log("  placed " + key + " -> " + world.getBlockId(x, y, z) + " (expect " + b.id() + ")");
    }

    private static void check(String name, boolean ok, String detail) {
        try {
            Method m = ServerMachineTest.class.getDeclaredMethod("check", String.class, boolean.class, String.class);
            m.setAccessible(true);
            m.invoke(null, name, ok, detail);
        } catch (Throwable t) {
            IC2.LOGGER.info("[SRV-TEST] check fallback {}: {} ({})", name, ok, detail);
        }
    }

    private static void log(String s) {
        IC2.LOGGER.info("[SRV-TEST] {}", (Object)s);
    }
}
