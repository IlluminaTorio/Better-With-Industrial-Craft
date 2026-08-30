

package ic2.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import ic2.IC2Blocks;
import ic2.IC2Config;
import ic2.IC2Items;
import ic2.energy.Direction;
import ic2.energy.EnergyNet;
import ic2.energy.IEnergySource;
import ic2.tileentity.TileEntityIC2Machine;
import ic2.tileentity.TileEntityReactorChamber;
import ic2.util.ExplosionIC2;
import java.util.List;
import java.util.Random;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.item.ItemBucket;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;

public class TileEntityNuclearReactor
extends TileEntityIC2Machine
implements IEnergySource {
    public int output = 0;
    public int updateTicker = new Random().nextInt(this.tickRate());
    public int heat = 0;
    public boolean exploded = false;

    public TileEntityNuclearReactor() {
        super(54);
    }

    public static int pulsePower() {
        return 5;
    }

    public int tickRate() {
        return 20;
    }

    @Override
    public void readAdditionalData(CompoundTag tag) {
        super.readAdditionalData(tag);
        this.heat = tag.getInteger("heat");
        this.output = tag.getInteger("output");
    }

    @Override
    public void writeAdditionalData(CompoundTag tag) {
        super.writeAdditionalData(tag);
        tag.putInt("heat", this.heat);
        tag.putInt("output", this.output);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.worldObj.isClientSide) {
            return;
        }
        this.sendEnergy(this.output);
        if (++this.updateTicker < this.tickRate()) {
            return;
        }
        this.updateTicker = 0;
        this.dropAllUnfittingStuff();
        if (this.heat > 0) {
            this.heat -= this.coolReactorFromOutside();
            if (this.heat <= 0) {
                this.heat = 0;
            } else if (this.calculateHeatEffects()) {
                return;
            }
        }
        this.output = 0;
        this.processChambers();
        boolean wasActive = this.active;
        boolean bl = this.active = this.heat >= 1000 || this.output > 0;
        if (this.active != wasActive) {
            this.worldObj.notifyBlockChange((TilePosc)this.tilePos, this.getBlock());
        }
        this.setChanged();
    }

    public int sendEnergy(int send) {
        TileEntityReactorChamber c;
        TileEntity tileEntity;
        send = EnergyNet.getForWorld(this.worldObj).emitEnergyFrom(this, send);
        if (send > 0 && (tileEntity = this.worldObj.getTileEntity((TilePosc)new TilePos(this.tilePos.x(), this.tilePos.y() + 1, this.tilePos.z()))) instanceof TileEntityReactorChamber) {
            c = (TileEntityReactorChamber)tileEntity;
            send = c.sendEnergy(send);
        }
        if (send > 0 && (tileEntity = this.worldObj.getTileEntity((TilePosc)new TilePos(this.tilePos.x(), this.tilePos.y() - 1, this.tilePos.z()))) instanceof TileEntityReactorChamber) {
            c = (TileEntityReactorChamber)tileEntity;
            send = c.sendEnergy(send);
        }
        if (send > 0 && (tileEntity = this.worldObj.getTileEntity((TilePosc)new TilePos(this.tilePos.x() + 1, this.tilePos.y(), this.tilePos.z()))) instanceof TileEntityReactorChamber) {
            c = (TileEntityReactorChamber)tileEntity;
            send = c.sendEnergy(send);
        }
        if (send > 0 && (tileEntity = this.worldObj.getTileEntity((TilePosc)new TilePos(this.tilePos.x() - 1, this.tilePos.y(), this.tilePos.z()))) instanceof TileEntityReactorChamber) {
            c = (TileEntityReactorChamber)tileEntity;
            send = c.sendEnergy(send);
        }
        if (send > 0 && (tileEntity = this.worldObj.getTileEntity((TilePosc)new TilePos(this.tilePos.x(), this.tilePos.y(), this.tilePos.z() + 1))) instanceof TileEntityReactorChamber) {
            c = (TileEntityReactorChamber)tileEntity;
            send = c.sendEnergy(send);
        }
        if (send > 0 && (tileEntity = this.worldObj.getTileEntity((TilePosc)new TilePos(this.tilePos.x(), this.tilePos.y(), this.tilePos.z() - 1))) instanceof TileEntityReactorChamber) {
            c = (TileEntityReactorChamber)tileEntity;
            send = c.sendEnergy(send);
        }
        return send;
    }

    @Override
    public int getMaxEnergyOutput() {
        return 240 * TileEntityNuclearReactor.pulsePower();
    }

    @Override
    public boolean emitsEnergyTo(TileEntity receiver, Direction direction) {
        return true;
    }

    public ItemStack getMatrixCoord(int x, int y) {
        if (x < 0 || x >= 9 || y < 0 || y >= 6) {
            return null;
        }
        return this.inventory[x + y * 9];
    }

    public void setMatrixCoord(int x, int y, ItemStack stack) {
        if (x < 0 || x >= 9 || y < 0 || y >= 6) {
            return;
        }
        this.inventory[x + y * 9] = stack;
    }

    public int getReactorSize() {
        int rows = 3;
        for (int x = this.tilePos.x() - 1; x <= this.tilePos.x() + 1; ++x) {
            for (int y = this.tilePos.y() - 1; y <= this.tilePos.y() + 1; ++y) {
                for (int z = this.tilePos.z() - 1; z <= this.tilePos.z() + 1; ++z) {
                    if (this.worldObj.getBlock(x, y, z) != IC2Blocks.reactorChamber) continue;
                    ++rows;
                }
            }
        }
        return rows;
    }

    public boolean isUsefulItem(ItemStack item) {
        if (item == null) {
            return false;
        }
        if (item.getItem() == IC2Items.cellUran) {
            return true;
        }
        if (item.getItem() == IC2Items.cellCoolant) {
            return true;
        }
        if (item.getItem() == IC2Items.reactorPlating) {
            return true;
        }
        if (item.getItem() == IC2Items.heatDisperser) {
            return true;
        }
        if (item.getItem() == IC2Items.cellDepletedIsotope) {
            return true;
        }
        if (item.getItem() == IC2Items.cellReEnrichedUranium) {
            return true;
        }
        if (item.getItem() == IC2Items.cellNearDepletedUranium) {
            return true;
        }
        return item.getItem() == Items.BUCKET_IRON;
    }

    public void dropAllUnfittingStuff() {
        int size = this.getReactorSize();
        for (int x = 0; x < 9; ++x) {
            for (int y = 0; y < 6; ++y) {
                ItemStack stack = this.getMatrixCoord(x, y);
                if (stack == null) continue;
                if (stack.stackSize <= 0) {
                    this.setMatrixCoord(x, y, null);
                    continue;
                }
                if (x < size && this.isUsefulItem(stack)) continue;
                this.eject(stack);
                this.setMatrixCoord(x, y, null);
            }
        }
    }

    public void eject(ItemStack drop) {
        if (this.worldObj.isClientSide || drop == null) {
            return;
        }
        float f = 0.7f;
        double d = (double)(this.worldObj.rand.nextFloat() * f) + (double)(1.0f - f) * 0.5;
        double d1 = (double)(this.worldObj.rand.nextFloat() * f) + (double)(1.0f - f) * 0.5;
        double d2 = (double)(this.worldObj.rand.nextFloat() * f) + (double)(1.0f - f) * 0.5;
        EntityItem entityitem = new EntityItem(this.worldObj, (double)this.tilePos.x() + d, (double)this.tilePos.y() + d1, (double)this.tilePos.z() + d2, drop);
        entityitem.pickupDelay = 10;
        this.worldObj.entityJoinedWorld((Entity)entityitem);
    }

    public int coolReactorFromOutside() {
        int cool = 1;
        int air = 0;
        for (int x = this.tilePos.x() - 1; x <= this.tilePos.x() + 1; ++x) {
            for (int y = this.tilePos.y() - 1; y <= this.tilePos.y() + 1; ++y) {
                for (int z = this.tilePos.z() - 1; z <= this.tilePos.z() + 1; ++z) {
                    Material material;
                    Block block = this.worldObj.getBlock(x, y, z);
                    if (block == IC2Blocks.reactorChamber) {
                        cool += 2;
                    }
                    Material material2 = material = block != null ? block.getMaterial() : null;
                    if (material == Materials.WATER) {
                        ++cool;
                    }
                    if (block == null || block == Blocks.AIR) {
                        ++air;
                    }
                    if (block == Blocks.FIRE) {
                        air -= 2;
                    }
                    if (material != Materials.LAVA) continue;
                    cool -= 3;
                }
            }
        }
        if ((cool += air / 4) < 0) {
            return 0;
        }
        return cool;
    }

    public boolean calculateHeatEffects() {
        int[] coord;
        int[] coord2;
        Object mat;
        Block block;
        int[] coord3;
        if (this.heat < 4000) {
            return false;
        }
        int size = this.getReactorSize();
        int maxHeat = 10000;
        maxHeat += 1000 * (size - 3);
        for (int y = 0; y < 6; ++y) {
            for (int x = 0; x < size; ++x) {
                if (this.getMatrixCoord(x, y) == null || this.getMatrixCoord(x, y).getItem() != IC2Items.reactorPlating) continue;
                maxHeat += 100;
            }
        }
        float power = (float)this.heat / (float)maxHeat;
        if (power >= 1.0f) {
            if (!IC2Config.nuclearMeltdowns()) {
                IC2Blocks.explodeMachineAt(this.worldObj, this.tilePos.x(), this.tilePos.y(), this.tilePos.z(), 8.0f);
                return true;
            }
            int boomPower = 12;
            for (int y = 0; y < 6; ++y) {
                for (int x = 0; x < size; ++x) {
                    if (this.getMatrixCoord(x, y) != null && this.getMatrixCoord(x, y).getItem() == IC2Items.cellUran) {
                        boomPower += 5;
                    }
                    if (this.getMatrixCoord(x, y) == null || this.getMatrixCoord(x, y).getItem() != IC2Items.reactorPlating) continue;
                    --boomPower;
                }
            }
            if (boomPower < 40) {
                boomPower = 40;
            }
            if (boomPower > 160) {
                boomPower = 160;
            }
            this.exploded = true;
            this.worldObj.setBlockWithNotify(this.tilePos.x(), this.tilePos.y(), this.tilePos.z(), 0);
            ExplosionIC2 explosion = new ExplosionIC2(this.worldObj, null, this.tilePos.x(), this.tilePos.y(), this.tilePos.z(), boomPower, 0.01f, 1.5f);
            explosion.explode();
            explosion.addEffects(true);
            for (int y2 = this.tilePos.y() - 2; y2 <= this.tilePos.y() + 2; ++y2) {
                for (int x2 = this.tilePos.x() - 6; x2 <= this.tilePos.x() + 6; ++x2) {
                    for (int z2 = this.tilePos.z() - 6; z2 <= this.tilePos.z() + 6; ++z2) {
                        if (this.worldObj.rand.nextInt(4) != 0 || this.worldObj.getBlock(x2, y2, z2) != null && this.worldObj.getBlock(x2, y2, z2) != Blocks.AIR) continue;
                        this.worldObj.setBlockWithNotify(x2, y2, z2, Blocks.FIRE.id());
                    }
                }
            }
            return true;
        }
        if (power >= 0.85f && this.worldObj.rand.nextFloat() <= 4.0f * (power - 0.7f) && (coord3 = this.getRandCoord(2)) != null) {
            block = this.worldObj.getBlock(coord3[0], coord3[1], coord3[2]);
            if (block == null || block == Blocks.AIR) {
                this.worldObj.setBlockWithNotify(coord3[0], coord3[1], coord3[2], Blocks.FIRE.id());
            } else if (block != Blocks.BEDROCK) {
                mat = block.getMaterial();
                if (mat == Materials.STONE || mat == Materials.METAL || mat == Materials.IRON || mat == Materials.LAVA || mat == Materials.GLASS || mat == Materials.ICE) {
                    this.worldObj.setBlockWithNotify(coord3[0], coord3[1], coord3[2], Blocks.FLUID_LAVA_STILL.id());
                } else {
                    this.worldObj.setBlockWithNotify(coord3[0], coord3[1], coord3[2], Blocks.FIRE.id());
                }
            }
        }
        if (power >= 0.7f) {
            AABBd box = new AABBd((double)(this.tilePos.x() - 3), (double)(this.tilePos.y() - 3), (double)(this.tilePos.z() - 3), (double)(this.tilePos.x() + 4), (double)(this.tilePos.y() + 4), (double)(this.tilePos.z() + 4));
            List list = this.worldObj.getEntitiesWithinAABB(Mob.class, (AABBdc)box);
            for (Object entObj : list) {
                Mob ent = (Mob)entObj;
                ent.hurt(null, 1, DamageType.COMBAT);
            }
        }
        if (power >= 0.5f && (coord2 = this.getRandCoord(2)) != null && (block = this.worldObj.getBlock(coord2[0], coord2[1], coord2[2])) != null && block.getMaterial() == Materials.WATER) {
            this.worldObj.setBlockWithNotify(coord2[0], coord2[1], coord2[2], 0);
        }
        if (power >= 0.4f && this.worldObj.rand.nextFloat() <= 1.5f * (power - 0.4f) && (coord = this.getRandCoord(2)) != null && (block = this.worldObj.getBlock(coord[0], coord[1], coord[2])) != null && ((mat = block.getMaterial()) == Materials.WOOD || mat == Materials.LEAVES || mat == Materials.PLANT || mat == Materials.CLOTH)) {
            this.worldObj.setBlockWithNotify(coord[0], coord[1], coord[2], Blocks.FIRE.id());
        }
        return false;
    }

    public int[] getRandCoord(int radius) {
        if (radius <= 0) {
            return null;
        }
        int x = this.tilePos.x() + this.worldObj.rand.nextInt(2 * radius + 1) - radius;
        int y = this.tilePos.y() + this.worldObj.rand.nextInt(2 * radius + 1) - radius;
        int z = this.tilePos.z() + this.worldObj.rand.nextInt(2 * radius + 1) - radius;
        if (x == this.tilePos.x() && y == this.tilePos.y() && z == this.tilePos.z()) {
            return null;
        }
        return new int[]{x, y, z};
    }

    public void processChambers() {
        int size = this.getReactorSize();
        for (int y = 0; y < 6; ++y) {
            for (int x = 0; x < size; ++x) {
                this.processChamber(x, y);
            }
        }
    }

    public void processChamber(int x, int y) {
        if (this.getMatrixCoord(x, y) == null) {
            return;
        }
        ItemStack stack = this.getMatrixCoord(x, y);
        if (stack.getItem() == IC2Items.cellCoolant && stack.getMetadata() > 0) {
            stack.setMetadata(stack.getMetadata() - 1);
        }
        if (stack.getItem() == IC2Items.reactorPlating && stack.getMetadata() > 0 && this.worldObj.rand.nextInt(10) == 0) {
            stack.setMetadata(stack.getMetadata() - 1);
        }
        if (stack.getItem() == IC2Items.cellNearDepletedUranium || stack.getItem() == IC2Items.cellDepletedIsotope || stack.getItem() == IC2Items.cellReEnrichedUranium) {
            ++this.heat;
        }
        if (stack.getItem() == Items.BUCKET_IRON && ItemBucket.STATE_WATER.equals((Object)ItemBucket.getState((ItemStack)stack)) && this.heat > 4000) {
            this.heat -= 500;
            this.setMatrixCoord(x, y, new ItemStack(Items.BUCKET_IRON, 1));
        }
        if (stack.getItem() == Items.BUCKET_IRON && ItemBucket.STATE_LAVA.equals((Object)ItemBucket.getState((ItemStack)stack))) {
            this.heat += 2000;
            this.setMatrixCoord(x, y, new ItemStack(Items.BUCKET_IRON, 1));
        }
        if ((stack = this.getMatrixCoord(x, y)) != null && stack.getItem() == IC2Items.heatDisperser) {
            this.disperseHeat(x, y);
        }
        if ((stack = this.getMatrixCoord(x, y)) != null && stack.getItem() == IC2Items.cellUran && this.produceEnergy()) {
            this.generateEnergy(x, y);
        }
    }

    public void disperseHeat(int x, int y) {
        this.switchHeat(x, y, x - 1, y);
        this.switchHeat(x, y, x + 1, y);
        this.switchHeat(x, y, x, y - 1);
        this.switchHeat(x, y, x, y + 1);
        ItemStack disperser = this.getMatrixCoord(x, y);
        if (disperser == null) {
            return;
        }
        int rebalance = (disperser.getMetadata() - this.heat) / 2;
        if (rebalance > 0) {
            if (rebalance > 25) {
                rebalance = 25;
            }
            this.heat += rebalance;
            disperser.setMetadata(disperser.getMetadata() - rebalance);
        } else {
            if ((rebalance *= -1) > 25) {
                rebalance = 25;
            }
            this.heat -= rebalance;
            disperser.damageItem(rebalance, null);
        }
    }

    public void switchHeat(int x, int y, int x2, int y2) {
        int heat2;
        if (this.getMatrixCoord(x2, y2) == null) {
            return;
        }
        ItemStack source = this.getMatrixCoord(x, y);
        if (source == null) {
            return;
        }
        ItemStack target = this.getMatrixCoord(x2, y2);
        int id = target.getItem() == IC2Items.cellCoolant ? 0 : (target.getItem() == IC2Items.reactorPlating ? 1 : -1);
        if (id == -1) {
            return;
        }
        int heat = source.getMetadata();
        int rebalance = (heat - (heat2 = target.getMetadata())) / 2;
        if (rebalance > 0) {
            if (rebalance > 6) {
                rebalance = 6;
            }
            source.setMetadata(heat - rebalance);
            if (id == 0) {
                target.damageItem(rebalance, null);
            } else {
                this.spreadHeat(x2, y2, rebalance, false);
            }
        } else {
            if ((rebalance *= -1) > 6) {
                rebalance = 6;
            }
            source.damageItem(rebalance, null);
            target.setMetadata(heat2 - rebalance);
        }
    }

    public void generateEnergy(int x, int y) {
        int pulses = 1 + this.isUranium(x + 1, y) + this.isUranium(x - 1, y) + this.isUranium(x, y + 1) + this.isUranium(x, y - 1);
        this.output += pulses * TileEntityNuclearReactor.pulsePower();
        pulses += this.enrichDepleted(x + 1, y) + this.enrichDepleted(x - 1, y) + this.enrichDepleted(x, y + 1) + this.enrichDepleted(x, y - 1);
        while (pulses > 0) {
            int takers = this.canTakeHeat(x + 1, y, true, true) + this.canTakeHeat(x - 1, y, true, true) + this.canTakeHeat(x, y + 1, true, true) + this.canTakeHeat(x, y - 1, true, true);
            int genHeat = switch (takers) {
                case 2 -> 4;
                case 3 -> 2;
                case 4 -> 1;
                default -> 10;
            };
            if (takers == 0) {
                this.heat += genHeat;
            } else {
                this.giveHeatTo(x + 1, y, genHeat);
                this.giveHeatTo(x - 1, y, genHeat);
                this.giveHeatTo(x, y + 1, genHeat);
                this.giveHeatTo(x, y - 1, genHeat);
            }
            --pulses;
        }
        ItemStack cell = this.getMatrixCoord(x, y);
        if (cell == null) {
            return;
        }
        if (cell.getMetadata() == 9999 && this.worldObj.rand.nextInt(3) == 0) {
            this.setMatrixCoord(x, y, new ItemStack(IC2Items.cellNearDepletedUranium));
        } else {
            cell.damageItem(1, null);
            if (cell.stackSize <= 0) {
                this.setMatrixCoord(x, y, null);
            }
        }
    }

    public int isUranium(int x, int y) {
        ItemStack stack = this.getMatrixCoord(x, y);
        return stack != null && stack.getItem() == IC2Items.cellUran ? 1 : 0;
    }

    public int enrichDepleted(int x, int y) {
        ItemStack stack = this.getMatrixCoord(x, y);
        if (stack == null || stack.getItem() != IC2Items.cellDepletedIsotope) {
            return 0;
        }
        int oneInChance = 8;
        if (this.heat >= 3000) {
            oneInChance = 4;
        }
        if (this.heat >= 6000) {
            oneInChance = 2;
        }
        if (this.heat >= 9000) {
            oneInChance = 1;
        }
        if (this.worldObj.rand.nextInt(oneInChance) != 0) {
            return 1;
        }
        if (stack.getMetadata() <= 0) {
            this.setMatrixCoord(x, y, new ItemStack(IC2Items.cellReEnrichedUranium));
        } else {
            stack.setMetadata(stack.getMetadata() - 2);
        }
        return 1;
    }

    public int canTakeHeat(int x, int y, boolean countPlating, boolean countCooler) {
        ItemStack stack = this.getMatrixCoord(x, y);
        if (stack == null) {
            return 0;
        }
        if (stack.getItem() == IC2Items.cellCoolant || stack.getItem() == IC2Items.reactorPlating && countPlating || stack.getItem() == IC2Items.heatDisperser && countCooler) {
            return 1;
        }
        return 0;
    }

    public void giveHeatTo(int x, int y, int heat) {
        if (this.canTakeHeat(x, y, true, true) == 0) {
            return;
        }
        ItemStack stack = this.getMatrixCoord(x, y);
        if (stack.getItem() == IC2Items.reactorPlating) {
            this.spreadHeat(x, y, heat, true);
        } else {
            stack.damageItem(heat, null);
        }
    }

    public void spreadHeat(int x, int y, int heat, boolean primary) {
        int genHeat;
        ItemStack source = this.getMatrixCoord(x, y);
        if (source == null) {
            return;
        }
        int takers = this.canTakeHeat(x + 1, y, primary, false) + this.canTakeHeat(x - 1, y, primary, false) + this.canTakeHeat(x, y + 1, primary, false) + this.canTakeHeat(x, y - 1, primary, false);
        if (takers == 0) {
            source.damageItem(heat, null);
            return;
        }
        while (heat % takers != 0 && source.getMetadata() > 0) {
            ++heat;
            source.setMetadata(source.getMetadata() - 1);
        }
        if ((heat -= (genHeat = heat / takers) * takers) > 0) {
            source.damageItem(heat, null);
        }
        this.spreadHeatTo(x - 1, y, genHeat, primary);
        this.spreadHeatTo(x + 1, y, genHeat, primary);
        this.spreadHeatTo(x, y - 1, genHeat, primary);
        this.spreadHeatTo(x, y + 1, genHeat, primary);
    }

    public void spreadHeatTo(int x, int y, int heat, boolean toPlatings) {
        if (this.canTakeHeat(x, y, toPlatings, false) == 0) {
            return;
        }
        ItemStack stack = this.getMatrixCoord(x, y);
        if (stack.getItem() == IC2Items.reactorPlating && toPlatings) {
            this.spreadHeat(x, y, heat, false);
        } else {
            stack.damageItem(heat, null);
        }
    }

    public boolean produceEnergy() {
        return !this.worldObj.hasNeighborSignal((TilePosc)this.tilePos);
    }

    @Override
    public String getMachineName() {
        return "Nuclear Reactor";
    }
}

