package ic2.mixin;

import ic2.client.guidebook.IC2Guidebook;
import net.minecraft.client.gui.guidebook.GuidebookSections;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuidebookSections.class)
public abstract class GuidebookSectionsMixin {
    @Inject(method = "init()V", at = @At("TAIL"))
    private static void ic2$registerSection(CallbackInfo ci) {
        IC2Guidebook.onSectionsInit();
    }
}
