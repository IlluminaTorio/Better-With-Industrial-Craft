package ic2.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import ic2.util.DroneAccessSavedData;
import net.minecraft.core.entity.player.Player;

public class TileEntityDispatcherTerminal
extends TileEntityIC2Machine {
    public String owner = null;

    public TileEntityDispatcherTerminal() {
        super(3);
    }

    public boolean canAccess(Player player) {
        if (this.owner == null) {
            return true;
        }
        if (player == null) {
            return false;
        }
        if (this.owner.equals(player.username)) {
            return true;
        }
        return DroneAccessSavedData.isGranted(this.worldObj, this.owner, player.username);
    }

    public void claimOwnership(Player player) {
        if (this.owner == null && player != null && player.username != null) {
            this.owner = player.username;
            this.setChanged();
        }
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

    @Override
    public String getMachineName() {
        return "Dispatcher Terminal";
    }
}
