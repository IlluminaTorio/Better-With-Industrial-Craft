package ic2.tmb;

import ic2.client.IC2MachineLayouts;
import ic2.recipe.RecipeEntryIC2Machine;
import java.util.List;
import net.minecraft.core.data.registry.recipe.RecipeEntryBase;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import turing.tmb.RecipeLayoutBuilder;
import turing.tmb.TypedIngredient;
import turing.tmb.api.ItemStackIngredientRenderer;
import turing.tmb.api.VanillaTypes;
import turing.tmb.api.drawable.IDrawable;
import turing.tmb.api.drawable.IDrawableAnimated;
import turing.tmb.api.drawable.IIngredientList;
import turing.tmb.api.ingredient.IIngredientRenderer;
import turing.tmb.api.ingredient.IIngredientType;
import turing.tmb.api.ingredient.ITypedIngredient;
import turing.tmb.api.recipe.ILookupContext;
import turing.tmb.api.recipe.IRecipeCategory;
import turing.tmb.api.recipe.IRecipeLayout;
import turing.tmb.api.runtime.ITMBRuntime;
import turing.tmb.client.DrawableAnimated;
import turing.tmb.client.DrawableBlank;
import turing.tmb.client.DrawableIngredient;
import turing.tmb.client.DrawableTexture;
import turing.tmb.util.IngredientList;

public class IC2MachineRecipeCategory
implements IRecipeCategory<IC2MachineRecipeTranslator> {
    private final IC2MachineLayouts.Layout layout;
    private final IDrawable background;
    private final IDrawable arrow;
    private final IDrawable arrowBack;
    private final IDrawable icon;
    private final String name;

    public IC2MachineRecipeCategory(String name, ItemStack icon, IC2MachineLayouts.Layout layout) {
        this.name = name;
        this.layout = layout;
        this.background = new DrawableBlank(layout.w, layout.h);
        this.arrow = new DrawableAnimated(
            new DrawableTexture("/assets/tmb/textures/gui/gui_vanilla.png", 82, 128, 24, 16, 0, 0, 0, 0, 24, 16),
            10,
            IDrawableAnimated.StartDirection.LEFT,
            false
        );
        this.arrowBack = new DrawableTexture("/assets/tmb/textures/gui/gui_vanilla.png", 24, 133, 24, 16, 0, 0, 0, 0, 24, 16);
        this.icon = new DrawableIngredient((Object)icon, (IIngredientRenderer)ItemStackIngredientRenderer.INSTANCE);
    }

    public String getName() {
        return this.name;
    }

    public String getNamespace() {
        return "IC2";
    }

    public IDrawable getBackground() {
        return this.background;
    }

    @Nullable
    public IDrawable getIcon() {
        return this.icon;
    }

    public void drawRecipe(ITMBRuntime runtime, IC2MachineRecipeTranslator recipe, IRecipeLayout layout, List<IIngredientList> ingredients, ILookupContext context) {
        this.arrowBack.draw(runtime.getGuiHelper(), this.layout.arrowX, this.layout.arrowY);
        this.arrow.draw(runtime.getGuiHelper(), this.layout.arrowX, this.layout.arrowY);
        this.getIngredients(recipe, layout, context, ingredients);
    }

    public void getIngredients(IC2MachineRecipeTranslator recipe, IRecipeLayout layout, ILookupContext context, List<IIngredientList> ingredients) {
        RecipeEntryBase<RecipeSymbol, ItemStack, Void> entry = (RecipeEntryBase<RecipeSymbol, ItemStack, Void>)recipe.getOriginal();
        RecipeEntryIC2Machine machine = (RecipeEntryIC2Machine)entry;
        ItemStack output = (ItemStack)entry.getOutput();
        ITypedIngredient<ItemStack> outputIngredient = TypedIngredient.itemStackIngredient(output);
        if (this.layout.hasSecondarySlot()) {
            ingredients.add(0, (IIngredientList)IngredientList.fromRecipeSymbol((RecipeSymbol)entry.getInput()));
            ingredients.add(1, (IIngredientList)(machine.input2 != null ? IngredientList.fromRecipeSymbol(machine.input2) : new IngredientList()));
            ingredients.add(2, (IIngredientList)new IngredientList(new ITypedIngredient[]{outputIngredient}));
        } else {
            ingredients.add(0, (IIngredientList)IngredientList.fromRecipeSymbol((RecipeSymbol)entry.getInput()));
            ingredients.add(1, (IIngredientList)new IngredientList(new ITypedIngredient[]{outputIngredient}));
        }
    }

    public IRecipeLayout getRecipeLayout() {
        RecipeLayoutBuilder builder = new RecipeLayoutBuilder();
        builder.addInputSlot(0, (IIngredientType)VanillaTypes.ITEM_STACK).setPosition(this.layout.inX - 1, this.layout.inY - 1).build();
        int outputIndex = 1;
        if (this.layout.hasSecondarySlot()) {
            builder.addInputSlot(1, (IIngredientType)VanillaTypes.ITEM_STACK).setPosition(this.layout.in2X - 1, this.layout.in2Y - 1).build();
            outputIndex = 2;
        }
        builder.addOutputSlot(outputIndex, (IIngredientType)VanillaTypes.ITEM_STACK).setPosition(this.layout.outX - 1, this.layout.outY - 1).build();
        return builder.build();
    }
}
