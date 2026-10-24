

package ic2.mixin;

import java.util.Properties;
import net.minecraft.core.lang.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={Language.class})
public interface LanguageAccessor {
    @Accessor(value="entries")
    public Properties ic2$getEntries();
}

