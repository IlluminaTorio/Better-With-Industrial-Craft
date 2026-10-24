package ic2.util;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import com.mojang.nbt.tags.StringTag;
import com.mojang.nbt.tags.Tag;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.world.World;
import net.minecraft.core.world.saveddata.SavedData;

public class DroneAccessSavedData
extends SavedData {
    private static final String DATA_ID = "ic2_drone_access";
    private final Map<String, Set<String>> grants = new HashMap();

    public DroneAccessSavedData(String id) {
        super(id);
    }

    public static DroneAccessSavedData get(World world) {
        if (world == null || world.isClientSide) {
            return null;
        }
        DroneAccessSavedData data = (DroneAccessSavedData)world.getSavedData(DroneAccessSavedData.class, DATA_ID);
        if (data == null) {
            data = new DroneAccessSavedData(DATA_ID);
            world.setSavedData(DATA_ID, data);
        }
        return data;
    }

    public static boolean isGranted(World world, String owner, String player) {
        if (world == null || owner == null || player == null) {
            return false;
        }
        DroneAccessSavedData data = DroneAccessSavedData.get(world);
        return data != null && data.hasGrant(owner, player);
    }

    public boolean hasGrant(String owner, String player) {
        Set<String> set = this.grants.get(owner);
        return set != null && set.contains(player);
    }

    public void grant(String owner, String player) {
        this.grants.computeIfAbsent(owner, k -> new HashSet()).add(player);
        this.setDirty();
    }

    public boolean revoke(String owner, String player) {
        Set<String> set = this.grants.get(owner);
        if (set == null || !set.remove(player)) {
            return false;
        }
        if (set.isEmpty()) {
            this.grants.remove(owner);
        }
        this.setDirty();
        return true;
    }

    public Set<String> listGrants(String owner) {
        Set<String> set = this.grants.get(owner);
        return set == null ? Collections.emptySet() : Collections.unmodifiableSet(set);
    }

    @Override
    public void load(CompoundTag tag) {
        this.grants.clear();
        for (Tag<?> element : tag.getValues()) {
            String owner = element.getTagName();
            if (!(element instanceof ListTag)) continue;
            ListTag list = (ListTag)element;
            HashSet<String> set = new HashSet<String>();
            for (int i = 0; i < list.tagCount(); ++i) {
                Tag<?> entry = list.tagAt(i);
                if (!(entry instanceof StringTag)) continue;
                set.add(((StringTag)entry).getValue());
            }
            this.grants.put(owner, set);
        }
    }

    @Override
    public void save(CompoundTag tag) {
        for (Map.Entry<String, Set<String>> entry : this.grants.entrySet()) {
            ListTag list = new ListTag();
            for (String player : entry.getValue()) {
                list.addTag(new StringTag(player));
            }
            tag.put(entry.getKey(), list);
        }
    }
}
