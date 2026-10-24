

package ic2.mixin;

import net.minecraft.server.entity.player.PlayerServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={PlayerServer.class}, remap=false)
public interface PlayerServerAccessor {
    @Invoker(value="getNextWindowId")
    public void ic2$invokeGetNextWindowId();

    @Accessor(value="currentWindowId")
    public int ic2$getCurrentWindowId();

    @Accessor(value="currentWindowId")
    public void ic2$setCurrentWindowId(int var1);
}

