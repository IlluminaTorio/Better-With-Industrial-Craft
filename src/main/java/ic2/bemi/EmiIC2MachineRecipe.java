package ic2.bemi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.recipe.EmiShapedRecipe;
import ic2.client.IC2MachineLayouts;
import ic2.recipe.RecipeEntryIC2Machine;
import java.util.List;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.item.ItemStack;
import net.minecraft.util.Identifier;

public class EmiIC2MachineRecipe
implements EmiRecipe {
    private final Identifier id;
    private final EmiRecipeCategory category;
    private final IC2MachineLayouts.Layout layout;
    private final EmiIngredient input;
    private final EmiIngredient input2;
    private final EmiStack output;

    public EmiIC2MachineRecipe(Identifier id, EmiRecipeCategory category, RecipeEntryIC2Machine entry) {
        this.id = id;
        this.category = category;
        this.layout = IC2MachineLayouts.getLayoutFor(entry);
        this.input = EmiShapedRecipe.resolveSymbol(entry.getInput());
        this.input2 = entry.input2 != null ? EmiShapedRecipe.resolveSymbol(entry.input2) : EmiStack.EMPTY;
        this.output = EmiStack.of(entry.getOutput());
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return this.category;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return this.input2.isEmpty() ? List.of(this.input) : List.of(this.input, this.input2);
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(this.output);
    }

    @Override
    public int getDisplayWidth() {
        return this.layout != null ? this.layout.w : 82;
    }

    @Override
    public int getDisplayHeight() {
        return this.layout != null ? this.layout.h : 38;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        if (this.layout == null) {
            widgets.addFillingArrow(24, 5, 10000);
            widgets.addSlot(this.input, 0, 4);
            widgets.addSlot(this.output, 56, 0).large(true).recipeContext(this);
            return;
        }
        widgets.addFillingArrow(this.layout.arrowX, this.layout.arrowY, 10000);
        widgets.addSlot(this.input, this.layout.inX - 1, this.layout.inY - 1);
        if (this.layout.hasSecondarySlot()) {
            widgets.addSlot(this.input2, this.layout.in2X - 1, this.layout.in2Y - 1);
        }
        widgets.addSlot(this.output, this.layout.outX - 1, this.layout.outY - 1).recipeContext(this);
    }
}
