package ic2.client.guidebook;

import ic2.client.IC2MachineLayouts;
import ic2.recipe.RecipeEntryIC2Machine;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ItemElement;
import net.minecraft.client.gui.TooltipElement;
import net.minecraft.client.gui.guidebook.GuidebookPageManager;
import net.minecraft.client.gui.guidebook.GuidebookSection;
import net.minecraft.client.gui.guidebook.RecipePage;
import net.minecraft.client.gui.guidebook.ScreenGuidebook;
import net.minecraft.client.gui.guidebook.SlotGuidebook;
import net.minecraft.client.gui.guidebook.search.GuidebookPageSearch;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.option.enums.DescriptionPromptEnum;
import net.minecraft.client.render.TextureManager;
import net.minecraft.client.render.font.FontRenderer;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.data.registry.recipe.SearchQuery;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.util.collection.Pair;

public class RecipePageIC2
extends RecipePage<RecipeEntryIC2Machine> {
    private static final int BAND_GAP = 5;
    private static final int LABEL_GAP = 11;
    private static final int LABEL_COLOR = -12566464;
    private static final String GUIDEBOOK_TEXTURE = "/assets/minecraft/textures/gui/container/guidebook/guidebook.png";
    private static long ticks = 0L;
    public List<SlotGuidebook> slots;
    public Map<RecipeEntryIC2Machine, List<SlotGuidebook>> map;
    public Map<RecipeEntryIC2Machine, Pair<Integer, Integer>> origins;
    private final TooltipElement tooltipElement;
    private final ItemElement itemElement;
    private static final Minecraft mc = Minecraft.getMinecraft();

    public RecipePageIC2(GuidebookSection section, List<RecipeEntryIC2Machine> recipes) {
        this(section, recipes, null);
    }

    public RecipePageIC2(GuidebookSection section, List<RecipeEntryIC2Machine> recipes, SearchQuery searchQuery) {
        super(section);
        this.recipes = recipes;
        this.slots = new ArrayList<SlotGuidebook>();
        this.map = new HashMap<RecipeEntryIC2Machine, List<SlotGuidebook>>();
        this.origins = new HashMap<RecipeEntryIC2Machine, Pair<Integer, Integer>>();
        this.tooltipElement = new TooltipElement(mc);
        this.itemElement = new ItemElement(mc);
        int slotId = 0;
        int bandY = 6;
        for (RecipeEntryIC2Machine recipe : recipes) {
            IC2MachineLayouts.Layout layout = IC2MachineLayouts.getLayoutFor(recipe);
            if (layout == null) continue;
            List<SlotGuidebook> recipeSlots = new ArrayList<SlotGuidebook>();
            int x0 = (158 - layout.w) / 2;
            int bandOriginY = bandY + LABEL_GAP;
            this.origins.put(recipe, Pair.of(x0, bandOriginY));
            recipeSlots.add(new SlotGuidebook(slotId++, x0 + layout.inX, bandOriginY + layout.inY, recipe.getInput(), false, recipe));
            if (layout.hasSecondarySlot()) {
                recipeSlots.add(new SlotGuidebook(slotId++, x0 + layout.in2X, bandOriginY + layout.in2Y, recipe.input2, false, recipe));
            }
            recipeSlots.add(new SlotGuidebook(slotId++, x0 + layout.outX, bandOriginY + layout.outY, new RecipeSymbol(recipe.getOutput().copy()), false, recipe).setAsOutput());
            if (searchQuery != null) {
                for (SlotGuidebook slot : recipeSlots) {
                    slot.applySearchFilter(searchQuery);
                }
            }
            this.map.put(recipe, recipeSlots);
            this.slots.addAll(recipeSlots);
            bandY = bandOriginY + layout.h + BAND_GAP;
        }
    }

    @Override
    public void onTick() {
        ++ticks;
        for (SlotGuidebook slot : this.slots) {
            if (ticks <= 20L) continue;
            slot.showRandomItem();
            if (this.slots.get(this.slots.size() - 1) != slot) continue;
            ticks = 0L;
        }
    }

    @Override
    public void onBecomeVisible() {
        for (SlotGuidebook slot : this.slots) {
            slot.randomizeCycleStart();
        }
    }

    @Override
    protected void renderBackground(TextureManager re, int x, int y) {
        super.renderBackground(re, x, y);
        GLRenderer.setColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        re.bindTexture(re.loadTexture(GUIDEBOOK_TEXTURE));
        for (RecipeEntryIC2Machine recipe : this.recipes) {
            List<SlotGuidebook> recipeSlots = this.map.get(recipe);
            if (recipeSlots == null || recipeSlots.isEmpty()) continue;
            SlotGuidebook outputSlot = recipeSlots.get(recipeSlots.size() - 1);
            this.drawTexturedModalRect(x + outputSlot.x - 32, y + outputSlot.y, 234, 0, 22, 15);
        }
    }

    @Override
    protected void renderForeground(TextureManager re, FontRenderer sr, int x, int y, int mouseX, int mouseY, float partialTicks) {
        for (RecipeEntryIC2Machine recipe : this.recipes) {
            IC2MachineLayouts.Layout layout = IC2MachineLayouts.getLayoutFor(recipe);
            Pair<Integer, Integer> origin = this.origins.get(recipe);
            if (layout == null || origin == null) continue;
            String label = I18n.getInstance().translateKey(layout.nameKey);
            this.drawStringCenteredNoShadow(sr, label, x + origin.getLeft() + layout.w / 2, y + origin.getRight() - LABEL_GAP, LABEL_COLOR);
        }
        SlotGuidebook mouseOverSlot = null;
        for (SlotGuidebook slot : this.slots) {
            this.drawSlot(x + slot.x - 1, y + slot.y - 1, -1);
            if (this.getIsMouseOverSlot(slot, x, y, mouseX, mouseY)) {
                mouseOverSlot = slot;
            }
            this.itemElement.render(slot.getItemStack(), x + slot.x, y + slot.y, mouseOverSlot == slot, slot);
        }
    }

    @Override
    protected void renderOverlay(TextureManager re, FontRenderer sr, int x, int y, int mouseX, int mouseY, float partialTicks) {
        super.renderOverlay(re, sr, x, y, mouseX, mouseY, partialTicks);
        SlotGuidebook mouseOverSlot = null;
        for (SlotGuidebook slot : this.slots) {
            if (this.getIsMouseOverSlot(slot, x, y, mouseX, mouseY)) {
                mouseOverSlot = slot;
            }
        }
        if (mouseOverSlot != null && mouseOverSlot.hasItem()) {
            boolean showDescription = DescriptionPromptEnum.showDescription();
            String str = this.tooltipElement.getTooltipText(mouseOverSlot.getItemStack(), showDescription, mouseOverSlot);
            if (!str.isEmpty()) {
                this.tooltipElement.render(str, mouseX, mouseY, 8, -8);
            }
        }
    }

    public boolean getIsMouseOverSlot(SlotGuidebook slot, int x, int y, int mouseX, int mouseY) {
        return mouseX >= x + slot.x - 1 && mouseX < x + slot.x + 16 + 1 && mouseY >= y + slot.y - 1 && mouseY < y + slot.y + 16 + 1;
    }

    @Override
    public boolean keyTyped(char c, int key, int x, int y, int mouseX, int mouseY) {
        super.keyTyped(c, key, x, y, mouseX, mouseY);
        if (GameSettings.KEY_SHOW_RECIPE.isKeyboardKey(key)) {
            SlotGuidebook hoveringSlot = this.findHoveredSlot(x, y, mouseX, mouseY);
            if (hoveringSlot != null && hoveringSlot.hasItem()) {
                String query = "r:" + hoveringSlot.getItemStack().getDisplayName() + "!";
                GuidebookPageManager.searchQuery = SearchQuery.resolve(query);
                GuidebookPageSearch.searchField.setText(query);
                ScreenGuidebook.getPageManager().updatePages();
                ScreenGuidebook.getPageManager().setCurrentPage(ScreenGuidebook.getPageManager().getSectionIndex(this.getSection()), true);
                return true;
            }
        } else if (GameSettings.KEY_SHOW_USAGE.isKeyboardKey(key)) {
            SlotGuidebook hoveringSlot = this.findHoveredSlot(x, y, mouseX, mouseY);
            if (hoveringSlot != null && hoveringSlot.hasItem()) {
                String query = "u:" + hoveringSlot.getItemStack().getDisplayName() + "!";
                GuidebookPageManager.searchQuery = SearchQuery.resolve(query);
                GuidebookPageSearch.searchField.setText(query);
                ScreenGuidebook.getPageManager().updatePages();
                ScreenGuidebook.getPageManager().setCurrentPage(ScreenGuidebook.getPageManager().getSectionIndex(this.getSection()), true);
                return true;
            }
        }
        return false;
    }

    private SlotGuidebook findHoveredSlot(int x, int y, int mouseX, int mouseY) {
        SlotGuidebook hoveringSlot = null;
        for (SlotGuidebook slot : this.slots) {
            if (!this.getIsMouseOverSlot(slot, x, y, mouseX, mouseY)) continue;
            hoveringSlot = slot;
        }
        return hoveringSlot;
    }
}
