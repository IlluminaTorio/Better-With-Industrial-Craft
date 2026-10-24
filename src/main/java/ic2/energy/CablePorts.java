package ic2.energy;

import com.mojang.nbt.tags.ByteArrayTag;
import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import com.mojang.nbt.tags.LongArrayTag;
import com.mojang.nbt.tags.LongTag;
import com.mojang.nbt.tags.Tag;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.world.World;
import net.minecraft.core.world.saveddata.SavedData;

public class CablePorts
extends SavedData {
    private static final String DATA_ID = "ic2_cable_ports";
    private final Map<Long, Map<Long, Integer>> chunks = new HashMap();

    public CablePorts(String id) {
        super(id);
    }

    public static CablePorts get(World world) {
        if (world == null || world.isClientSide) {
            return null;
        }
        CablePorts data = (CablePorts)world.getSavedData(CablePorts.class, DATA_ID);
        if (data == null) {
            data = new CablePorts(DATA_ID);
            world.setSavedData(DATA_ID, data);
        }
        return data;
    }

    public static long packPos(int x, int y, int z) {
        return ((long)(x & 0x3FFFFFF) << 38) + (((long)(z & 0x3FFFFFF) << 12) + (long)(y & 0xFFF));
    }

    public static long packChunk(int cx, int cz) {
        return ((long)cx << 32) + (long)(cz & 0xFFFFFFFFL);
    }

    public static int chunkKey(long packedPos) {
        return (int)(packedPos >> 38);
    }

    public void putPort(int x, int y, int z, short cableType, short color) {
        long pos = CablePorts.packPos(x, y, z);
        int meta = (cableType & 0xF) << 4 | (color & 0xF);
        Map<Long, Integer> ports = this.chunks.get(CablePorts.packChunk(x >> 4, z >> 4));
        if (ports == null) {
            ports = new HashMap();
            this.chunks.put(CablePorts.packChunk(x >> 4, z >> 4), ports);
        }
        if (ports.put(pos, meta) == null) {
            this.setDirty();
        }
    }

    public void removePort(int x, int y, int z) {
        Map<Long, Integer> ports = this.chunks.get(CablePorts.packChunk(x >> 4, z >> 4));
        if (ports == null) {
            return;
        }
        if (ports.remove(CablePorts.packPos(x, y, z)) != null) {
            this.setDirty();
            if (ports.isEmpty()) {
                this.chunks.remove(CablePorts.packChunk(x >> 4, z >> 4));
            }
        }
    }

    public int portMeta(World world, int x, int y, int z) {
        Map<Long, Integer> ports = this.chunks.get(CablePorts.packChunk(x >> 4, z >> 4));
        if (ports == null) {
            return -1;
        }
        Integer meta = ports.get(CablePorts.packPos(x, y, z));
        return meta == null ? -1 : meta;
    }

    public int chunkCount() {
        return this.chunks.size();
    }

    @Override
    public void load(CompoundTag tag) {
        this.chunks.clear();
        ListTag list = tag.getList("chunks");
        for (int i = 0; i < list.tagCount(); ++i) {
            CompoundTag ct = (CompoundTag)list.tagAt(i);
            long ck = ct.getLong("ck");
            long[] pos = ct.getLongArray("pos");
            byte[] meta = ct.getByteArray("meta");
            HashMap<Long, Integer> ports = new HashMap();
            for (int j = 0; j < pos.length && j < meta.length; ++j) {
                ports.put(pos[j], meta[j] & 0xFF);
            }
            this.chunks.put(ck, ports);
        }
    }

    @Override
    public void save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (Map.Entry<Long, Map<Long, Integer>> e : this.chunks.entrySet()) {
            CompoundTag ct = new CompoundTag();
            ct.putLong("ck", e.getKey());
            Map<Long, Integer> ports = e.getValue();
            long[] pos = new long[ports.size()];
            byte[] meta = new byte[ports.size()];
            int j = 0;
            for (Map.Entry<Long, Integer> pe : ports.entrySet()) {
                pos[j] = pe.getKey();
                meta[j] = (byte)(int)pe.getValue();
                ++j;
            }
            ct.putLongArray("pos", pos);
            ct.putByteArray("meta", meta);
            list.addTag(ct);
        }
        tag.put("chunks", list);
    }
}
