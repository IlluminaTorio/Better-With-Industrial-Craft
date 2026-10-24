package ic2.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import ic2.IC2Config;
import ic2.IC2Items;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.world.pos.TilePosc;
import java.util.HashMap;
import java.util.Map;

public class TileEntityRareEarthExtractor
extends TileEntityElecMachine {
    public static final int maxInput = 32;
    public int progress = 0;
    public static final int operationLength = 35;
    public int maxRareEarth = 1000;
    public float rareEarth = 0.0f;
    public static final Map<Integer, Float> RARE_EARTH_AMOUNTS = new HashMap<Integer, Float>();

    public TileEntityRareEarthExtractor() {
        super(3, 1, 1120, maxInput);
    }

    public int gaugeProgressScaled(int i) {
        int r = this.progress * i / operationLength;
        if (r > i) {
            r = i;
        }
        return r;
    }

    public float getChargeLevel() {
        float f = (float)this.energy / (float)(this.maxEnergy - maxInput + 1);
        if (f > 1.0f) {
            f = 1.0f;
        }
        return f;
    }

    public int gaugeRareEarthScaled(int i) {
        return (int)(this.rareEarth * (float)i / (float)this.maxRareEarth);
    }

    public static boolean canConsume(ItemStack stack) {
        return TileEntityRareEarthExtractor.reAmount(stack) > 0.0f;
    }

    public static float reAmount(ItemStack stack) {
        if (stack == null) {
            return 0.0f;
        }
        Float amount = RARE_EARTH_AMOUNTS.get(stack.getItem().id);
        return amount == null ? 0.0f : amount.floatValue();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.worldObj.isClientSide) {
            return;
        }
        boolean wasActive = this.isActive();
        boolean needsInvUpdate = this.provideEnergy();
        if (this.energy >= 1 && this.canRun()) {
            ++this.progress;
            --this.energy;
            if (this.progress >= operationLength) {
                this.progress = 1;
                this.rareEarth += TileEntityRareEarthExtractor.reAmount(this.inventory[0]);
                --this.inventory[0].stackSize;
                if (this.inventory[0].stackSize <= 0) {
                    this.inventory[0] = null;
                }
                needsInvUpdate = true;
                if (this.rareEarth >= (float)this.maxRareEarth) {
                    this.rareEarth -= (float)this.maxRareEarth;
                    if (this.inventory[2] == null) {
                        this.inventory[2] = new ItemStack(IC2Items.rareEarthDust);
                    } else if (this.inventory[2].getItem() == IC2Items.rareEarthDust && this.inventory[2].stackSize < this.inventory[2].getMaxStackSize()) {
                        ++this.inventory[2].stackSize;
                    }
                }
            }
        } else {
            this.progress = 0;
        }
        if (this.active != this.isActive()) {
            this.active = this.isActive();
            this.worldObj.notifyBlockChange((TilePosc)this.tilePos, this.getBlock());
            needsInvUpdate = true;
        }
        if (needsInvUpdate) {
            this.setChanged();
        }
    }

    public boolean isActive() {
        return this.progress > 0;
    }

    public boolean canRun() {
        return this.inventory[0] != null && TileEntityRareEarthExtractor.canConsume(this.inventory[0]) && (this.inventory[2] == null || this.inventory[2].getItem() == IC2Items.rareEarthDust && this.inventory[2].stackSize < this.inventory[2].getMaxStackSize());
    }

    @Override
    public String getMachineName() {
        return "Rare Earth Extractor";
    }

    @Override
    public void readAdditionalData(CompoundTag tag) {
        super.readAdditionalData(tag);
        this.progress = tag.getShort("progress");
        this.rareEarth = tag.getFloat("rareEarth");
    }

    @Override
    public void writeAdditionalData(CompoundTag tag) {
        super.writeAdditionalData(tag);
        tag.putShort("progress", (short)this.progress);
        tag.putFloat("rareEarth", this.rareEarth);
    }
}
