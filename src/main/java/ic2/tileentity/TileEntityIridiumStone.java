package ic2.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import ic2.tileentity.TileEntityIC2Block;

public class TileEntityIridiumStone
extends TileEntityIC2Block {
    public String owner = "";

    @Override
    public void readAdditionalData(CompoundTag tag) {
        super.readAdditionalData(tag);
        this.owner = tag.getString("owner");
    }

    @Override
    public void writeAdditionalData(CompoundTag tag) {
        super.writeAdditionalData(tag);
        tag.putString("owner", this.owner);
    }
}
