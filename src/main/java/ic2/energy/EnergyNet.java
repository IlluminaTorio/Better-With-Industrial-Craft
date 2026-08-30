

package ic2.energy;

import ic2.energy.Direction;
import ic2.energy.IEnergyAcceptor;
import ic2.energy.IEnergyConductor;
import ic2.energy.IEnergyEmitter;
import ic2.energy.IEnergySink;
import ic2.energy.IEnergySource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.world.World;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;

public class EnergyNet {
    private static final Map<World, EnergyNet> worldToEnergyNetMap = new HashMap<World, EnergyNet>();
    private static final Map<Mob, Integer> entityLivingToShockEnergyMap = new HashMap<Mob, Integer>();
    private final World world;
    private final Map<IEnergySource, List<EnergyPath>> energySourceToEnergyPathMap = new HashMap<IEnergySource, List<EnergyPath>>();

    
    public static EnergyNet getForWorld(World world) {
        if (world == null) {
            return null;
        }
        Map<World, EnergyNet> map = worldToEnergyNetMap;
        synchronized (map) {
            return worldToEnergyNetMap.computeIfAbsent(world, EnergyNet::new);
        }
    }

    public static void onTick() {
        for (Map.Entry<Mob, Integer> entry : entityLivingToShockEnergyMap.entrySet()) {
            Mob target = entry.getKey();
            int damage = (entry.getValue() + 63) / 64;
            if (!target.isAlive()) continue;
            target.hurt(null, damage, DamageType.COMBAT);
        }
        entityLivingToShockEnergyMap.clear();
    }

    public static void onTick(World world) {
        if (world == null || world.isClientSide) {
            return;
        }
        Iterator<Map.Entry<Mob, Integer>> it = entityLivingToShockEnergyMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Mob, Integer> entry = it.next();
            Mob target = entry.getKey();
            if (target.world != world) continue;
            it.remove();
            int damage = (entry.getValue() + 63) / 64;
            if (!target.isAlive()) continue;
            target.hurt(null, damage, DamageType.COMBAT);
        }
    }

    private EnergyNet(World world) {
        this.world = world;
    }

    public void addTileEntity(TileEntity addedTileEntity) {
        if (addedTileEntity instanceof IEnergyAcceptor) {
            List<EnergyPath> reverseEnergyPaths = this.discover(addedTileEntity, true, Integer.MAX_VALUE);
            for (EnergyPath reverseEnergyPath : reverseEnergyPaths) {
                IEnergySource energySource = (IEnergySource)reverseEnergyPath.target;
                if (!this.energySourceToEnergyPathMap.containsKey(energySource) || (double)energySource.getMaxEnergyOutput() <= reverseEnergyPath.loss) continue;
                this.energySourceToEnergyPathMap.remove(energySource);
            }
        }
    }

    public void removeTileEntity(TileEntity removedTileEntity) {
        Iterator<Map.Entry<IEnergySource, List<EnergyPath>>> sourceIt = this.energySourceToEnergyPathMap.entrySet().iterator();
        while (sourceIt.hasNext()) {
            Map.Entry<IEnergySource, List<EnergyPath>> entry = sourceIt.next();
            if (entry.getKey() == removedTileEntity) {
                sourceIt.remove();
                continue;
            }
            Iterator<EnergyPath> pathIt = entry.getValue().iterator();
            boolean empty = true;
            while (pathIt.hasNext()) {
                EnergyPath path = pathIt.next();
                if (path.target == removedTileEntity || path.conductors.contains(removedTileEntity)) {
                    pathIt.remove();
                    continue;
                }
                empty = false;
            }
            if (!empty) continue;
            sourceIt.remove();
        }
    }

    public int emitEnergyFrom(IEnergySource energySource, int amount) {
        IEnergySink energySink;
        if (!this.energySourceToEnergyPathMap.containsKey(energySource)) {
            this.energySourceToEnergyPathMap.put(energySource, this.discover((TileEntity)energySource, false, energySource.getMaxEnergyOutput()));
        }
        int energyConsumed = 0;
        Vector<EnergyPath> activeEnergyPaths = new Vector<EnergyPath>();
        double totalInvLoss = 0.0;
        for (EnergyPath energyPath : this.energySourceToEnergyPathMap.get(energySource)) {
            energySink = (IEnergySink)energyPath.target;
            if (!energySink.demandsEnergy()) continue;
            totalInvLoss += 1.0 / energyPath.loss;
            activeEnergyPaths.add(energyPath);
            if (activeEnergyPaths.size() < amount) continue;
        }
        for (EnergyPath energyPath : activeEnergyPaths) {
            int energyLoss;
            energySink = (IEnergySink)energyPath.target;
            int energyProvided = (int)Math.floor((double)amount / totalInvLoss / energyPath.loss);
            
            
            if (!ic2.IC2Config.cableOverloadBurn() && energyPath.minConductorBreakdownEnergy != Integer.MAX_VALUE) {
                int cableCapacity = energyPath.minConductorBreakdownEnergy - 1;
                if (energyProvided > cableCapacity) {
                    energyProvided = cableCapacity;
                }
            }
            if (energyProvided <= (energyLoss = (int)Math.floor(energyPath.loss))) continue;
            int energyReturned = energySink.injectEnergy(energyPath.targetDirection, energyProvided - energyLoss);
            energyConsumed += energyProvided - energyReturned;
            int energyInjected = energyProvided - energyLoss - energyReturned;
            energyPath.totalEnergyConducted += (long)energyInjected;
            
            
            if (ic2.IC2Config.voltageSystemOff() || !ic2.IC2Config.cableOverloadBurn()) {
                continue;
            }
            if (energyInjected > energyPath.minInsulationEnergyAbsorption) {
                List entitiesNearEnergyPath = this.world.getEntitiesWithinAABB(Mob.class, (AABBdc)new AABBd((double)(energyPath.minX - 1), (double)(energyPath.minY - 1), (double)(energyPath.minZ - 1), (double)(energyPath.maxX + 2), (double)(energyPath.maxY + 2), (double)(energyPath.maxZ + 2)));
                for (Object livingObj : entitiesNearEnergyPath) {
                    Mob living = (Mob)livingObj;
                    int maxShockEnergy = 0;
                    for (IEnergyConductor conductor : energyPath.conductors) {
                        TileEntity te = (TileEntity)conductor;
                        if (!living.bb.intersectsAABB(new AABBd((double)(te.tilePos.x() - 1), (double)(te.tilePos.y() - 1), (double)(te.tilePos.z() - 1), (double)(te.tilePos.x() + 2), (double)(te.tilePos.y() + 2), (double)(te.tilePos.z() + 2)))) continue;
                        int shockEnergy = energyInjected - conductor.getInsulationEnergyAbsorption();
                        if (shockEnergy > maxShockEnergy) {
                            maxShockEnergy = shockEnergy;
                        }
                        if (conductor.getInsulationEnergyAbsorption() != energyPath.minInsulationEnergyAbsorption) continue;
                    }
                    if (entityLivingToShockEnergyMap.containsKey(living)) {
                        entityLivingToShockEnergyMap.put(living, entityLivingToShockEnergyMap.get(living) + maxShockEnergy);
                        continue;
                    }
                    entityLivingToShockEnergyMap.put(living, maxShockEnergy);
                }
                if (energyInjected >= energyPath.minInsulationBreakdownEnergy) {
                    for (IEnergyConductor conductor : energyPath.conductors) {
                        if (energyInjected < conductor.getInsulationBreakdownEnergy()) continue;
                        conductor.removeInsulation();
                        if (conductor.getInsulationEnergyAbsorption() >= energyPath.minInsulationEnergyAbsorption) continue;
                        energyPath.minInsulationEnergyAbsorption = conductor.getInsulationEnergyAbsorption();
                    }
                }
            }
            if (energyInjected < energyPath.minConductorBreakdownEnergy) continue;
            for (IEnergyConductor conductor : energyPath.conductors) {
                if (energyInjected < conductor.getConductorBreakdownEnergy()) continue;
                conductor.removeConductor();
            }
        }
        return amount - energyConsumed;
    }

    public long getTotalEnergyConducted(TileEntity tileEntity) {
        long ret = 0L;
        if (tileEntity instanceof IEnergyConductor || tileEntity instanceof IEnergySink) {
            List<EnergyPath> reverseEnergyPaths = this.discover(tileEntity, true, Integer.MAX_VALUE);
            for (EnergyPath reverseEnergyPath : reverseEnergyPaths) {
                IEnergySource energySource = (IEnergySource)reverseEnergyPath.target;
                if (!this.energySourceToEnergyPathMap.containsKey(energySource) || (double)energySource.getMaxEnergyOutput() <= reverseEnergyPath.loss) continue;
                for (EnergyPath energyPath : this.energySourceToEnergyPathMap.get(energySource)) {
                    if (!(tileEntity instanceof IEnergySink && energyPath.target == tileEntity || tileEntity instanceof IEnergyConductor && energyPath.conductors.contains(tileEntity))) continue;
                    ret += energyPath.totalEnergyConducted;
                }
            }
        }
        if (tileEntity instanceof IEnergySource && this.energySourceToEnergyPathMap.containsKey(tileEntity)) {
            for (EnergyPath energyPath : this.energySourceToEnergyPathMap.get(tileEntity)) {
                ret += energyPath.totalEnergyConducted;
            }
        }
        return ret;
    }

    private List<EnergyPath> discover(TileEntity emitter, boolean reverse, int lossLimit) {
        LinkedList<EnergyPath> energyPaths = new LinkedList<EnergyPath>();
        energyPaths.addAll(this.discover(emitter, reverse, lossLimit, new EnergyPath()));
        HashMap<TileEntity, EnergyPath> bestResults = new HashMap<TileEntity, EnergyPath>();
        for (EnergyPath energyPath : energyPaths) {
            EnergyPath bestEnergyPath = (EnergyPath)bestResults.get(energyPath.target);
            if (bestEnergyPath != null && !(bestEnergyPath.loss > energyPath.loss)) continue;
            bestResults.put(energyPath.target, energyPath);
        }
        energyPaths.clear();
        energyPaths.addAll(bestResults.values());
        return energyPaths;
    }

    private List<EnergyPath> discover(TileEntity emitter, boolean reverse, int lossLimit, EnergyPath energyPath) {
        LinkedList<EnergyPath> energyPaths = new LinkedList<EnergyPath>();
        if (emitter instanceof IEnergyConductor) {
            IEnergyConductor energyConductor = (IEnergyConductor)emitter;
            if (emitter.tilePos.x() < energyPath.minX) {
                energyPath.minX = emitter.tilePos.x();
            }
            if (emitter.tilePos.y() < energyPath.minY) {
                energyPath.minY = emitter.tilePos.y();
            }
            if (emitter.tilePos.z() < energyPath.minZ) {
                energyPath.minZ = emitter.tilePos.z();
            }
            if (emitter.tilePos.x() > energyPath.maxX) {
                energyPath.maxX = emitter.tilePos.x();
            }
            if (emitter.tilePos.y() > energyPath.maxY) {
                energyPath.maxY = emitter.tilePos.y();
            }
            if (emitter.tilePos.z() > energyPath.maxZ) {
                energyPath.maxZ = emitter.tilePos.z();
            }
            energyPath.loss += energyConductor.getConductionLoss();
            if (energyPath.loss >= (double)lossLimit) {
                return energyPaths;
            }
            energyPath.conductors.add(energyConductor);
            if (energyConductor.getInsulationEnergyAbsorption() < energyPath.minInsulationEnergyAbsorption) {
                energyPath.minInsulationEnergyAbsorption = energyConductor.getInsulationEnergyAbsorption();
            }
            if (energyConductor.getInsulationBreakdownEnergy() < energyPath.minInsulationBreakdownEnergy) {
                energyPath.minInsulationBreakdownEnergy = energyConductor.getInsulationBreakdownEnergy();
            }
            if (energyConductor.getConductorBreakdownEnergy() < energyPath.minConductorBreakdownEnergy) {
                energyPath.minConductorBreakdownEnergy = energyConductor.getConductorBreakdownEnergy();
            }
        }
        List<EnergyTarget> validReceivers = this.getValidReceivers(emitter, reverse);
        Iterator<EnergyTarget> it = validReceivers.iterator();
        while (it.hasNext()) {
            if (!energyPath.conductors.contains(it.next().tileEntity)) continue;
            it.remove();
        }
        for (EnergyTarget validReceiver : validReceivers) {
            if (validReceiver.tileEntity instanceof IEnergyConductor) {
                energyPaths.addAll(this.discover(validReceiver.tileEntity, reverse, lossLimit, new EnergyPath(energyPath)));
            }
            if (!(!reverse && validReceiver.tileEntity instanceof IEnergySink || reverse && validReceiver.tileEntity instanceof IEnergySource)) continue;
            EnergyPath resultingEnergyPath = new EnergyPath(energyPath);
            if (resultingEnergyPath.loss < 0.1) {
                resultingEnergyPath.loss = 0.1;
            }
            resultingEnergyPath.target = validReceiver.tileEntity;
            resultingEnergyPath.targetDirection = validReceiver.direction;
            energyPaths.add(resultingEnergyPath);
        }
        return energyPaths;
    }

    private List<EnergyTarget> getValidReceivers(TileEntity emitter, boolean reverse) {
        LinkedList<EnergyTarget> validReceivers = new LinkedList<EnergyTarget>();
        for (Direction direction : Direction.values()) {
            TileEntity target = direction.applyToTileEntity(this.world, emitter);
            if (target == null) continue;
            Direction inverseDirection = direction.getInverse();
            if (!((!reverse && emitter instanceof IEnergyEmitter && ((IEnergyEmitter)emitter).emitsEnergyTo(target, direction) || reverse && emitter instanceof IEnergyAcceptor && ((IEnergyAcceptor)emitter).acceptsEnergyFrom(target, direction)) && (!reverse && target instanceof IEnergyAcceptor && ((IEnergyAcceptor)target).acceptsEnergyFrom(emitter, inverseDirection) || reverse && target instanceof IEnergyEmitter && ((IEnergyEmitter)target).emitsEnergyTo(emitter, inverseDirection)))) continue;
            validReceivers.add(new EnergyTarget(target, inverseDirection));
        }
        return validReceivers;
    }

    public static class EnergyPath {
        TileEntity target = null;
        Direction targetDirection;
        Set<IEnergyConductor> conductors = new HashSet<IEnergyConductor>();
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;
        double loss = 0.0;
        int minInsulationEnergyAbsorption = Integer.MAX_VALUE;
        int minInsulationBreakdownEnergy = Integer.MAX_VALUE;
        int minConductorBreakdownEnergy = Integer.MAX_VALUE;
        long totalEnergyConducted = 0L;

        EnergyPath() {
        }

        EnergyPath(EnergyPath ep) {
            this.target = ep.target;
            this.targetDirection = ep.targetDirection;
            this.conductors.addAll(ep.conductors);
            this.minX = ep.minX;
            this.minY = ep.minY;
            this.minZ = ep.minZ;
            this.maxX = ep.maxX;
            this.maxY = ep.maxY;
            this.maxZ = ep.maxZ;
            this.loss = ep.loss;
            this.minInsulationEnergyAbsorption = ep.minInsulationEnergyAbsorption;
            this.minInsulationBreakdownEnergy = ep.minInsulationBreakdownEnergy;
            this.minConductorBreakdownEnergy = ep.minConductorBreakdownEnergy;
            this.totalEnergyConducted = ep.totalEnergyConducted;
        }
    }

    static class EnergyTarget {
        TileEntity tileEntity;
        Direction direction;

        EnergyTarget(TileEntity tileEntity, Direction direction) {
            this.tileEntity = tileEntity;
            this.direction = direction;
        }
    }
}

