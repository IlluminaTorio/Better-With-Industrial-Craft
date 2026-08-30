

package ic2.mixin;

import ic2.IC2Language;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.lang.Language;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={I18n.class})
public class I18nMixin {
    @Shadow
    @Nullable
    private Language currentLanguage;

    @Inject(method={"reload"}, at={@At(value="TAIL")})
    private void ic2$onLanguageReload(String languageCode, CallbackInfo ci) {
        IC2Language.inject(this.currentLanguage);
    }
}

