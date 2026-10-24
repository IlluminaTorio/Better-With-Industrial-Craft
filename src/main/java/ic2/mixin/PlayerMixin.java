

package ic2.mixin;

import ic2.util.IC2PlayerTicker;
import net.minecraft.core.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Player.class}, remap=false)
public class PlayerMixin {
    @Inject(method={"tick"}, at={@At(value="TAIL")})
    private void ic2$onPlayerTick(CallbackInfo ci) {
        IC2PlayerTicker.tick((Player)(Object)this);
    }
}

