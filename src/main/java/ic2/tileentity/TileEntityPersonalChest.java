

package ic2.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import ic2.tileentity.TileEntityIC2Machine;
import net.minecraft.core.entity.player.Player;

public class TileEntityPersonalChest
extends TileEntityIC2Machine {
    public String owner = null;

    public TileEntityPersonalChest() {
        super(27);
    }

    @Override
    public void readAdditionalData(CompoundTag tag) {
        super.readAdditionalData(tag);
        this.owner = tag.containsKey("owner") ? tag.getString("owner") : null;
    }

    @Override
    public void writeAdditionalData(CompoundTag tag) {
        super.writeAdditionalData(tag);
        if (this.owner != null) {
            tag.putString("owner", this.owner);
        }
    }

    public void setOwner(Player player) {
        if (this.owner == null) {
            this.owner = player.username;
        }
    }

    public boolean canAccess(Player player) {
        return this.owner == null || this.owner.equals(player.username);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.canAccess(player) && super.stillValid(player);
    }

    @Override
    public String getMachineName() {
        return "Personal Safe";
    }
}

