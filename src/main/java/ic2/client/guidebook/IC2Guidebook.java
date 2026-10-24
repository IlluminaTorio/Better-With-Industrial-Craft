package ic2.client.guidebook;

import ic2.IC2;
import net.minecraft.client.gui.guidebook.GuidebookSection;
import net.minecraft.client.gui.guidebook.GuidebookSections;
import net.minecraft.core.data.registry.Registries;

public class IC2Guidebook {
    public static void onSectionsInit() {
        try {
            for (GuidebookSection section : GuidebookSections.getSectionList()) {
                if (section instanceof GuidebookSectionIC2Machines) {
                    return;
                }
            }
            if (Registries.RECIPES.getItem(IC2.MOD_ID) == null) {
                return;
            }
            GuidebookSections.register(new GuidebookSectionIC2Machines());
            IC2.LOGGER.info("Guidebook: registered IC2 machines section");
        }
        catch (Throwable t) {
            IC2.LOGGER.warn("Guidebook: failed to register IC2 section: {}", (Object)t.toString());
        }
    }
}
