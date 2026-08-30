

package ic2.mixin;

import ic2.energy.EnergyNet;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={World.class}, remap=false)
public class WorldMixin {
    @Inject(method={"tick"}, at={@At(value="TAIL")})
    private void ic2$onWorldTick(CallbackInfo ci) {
        EnergyNet.onTick((World)(Object)this);
    }
}

