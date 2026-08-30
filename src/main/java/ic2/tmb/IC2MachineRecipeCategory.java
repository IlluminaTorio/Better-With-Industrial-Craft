

package ic2.tmb;

import ic2.tmb.IC2MachineRecipeTranslator;
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
import turing.tmb.api.drawable.IDrawableStatic;
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
    private final IDrawable background = new DrawableBlank(120, 40);
    private final IDrawable icon;
    private final IDrawable arrow;
    private final IDrawable arrowBack;
    private final String name;
    private final int x = 44;

    public IC2MachineRecipeCategory(String name, ItemStack icon) {
        this.name = name;
        this.icon = new DrawableIngredient((Object)icon, (IIngredientRenderer)ItemStackIngredientRenderer.INSTANCE);
        this.arrow = new DrawableAnimated((IDrawableStatic)new DrawableTexture("/assets/tmb/textures/gui/gui_vanilla.png", 82, 128, 24, 16, 0, 0, 0, 0, 24, 16), 10, IDrawableAnimated.StartDirection.LEFT, false);
        this.arrowBack = new DrawableTexture("/assets/tmb/textures/gui/gui_vanilla.png", 24, 133, 24, 16, 0, 0, 0, 0, 24, 16);
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
        this.getIngredients(recipe, layout, context, ingredients);
        this.arrowBack.draw(runtime.getGuiHelper(), this.x + 26, this.background.getHeight() / 2 - 5);
        this.arrow.draw(runtime.getGuiHelper(), this.x + 26, this.background.getHeight() / 2 - 5);
    }

    public void getIngredients(IC2MachineRecipeTranslator recipe, IRecipeLayout layout, ILookupContext context, List<IIngredientList> ingredients) {
        RecipeEntryBase<RecipeSymbol, ItemStack, Void> entry = (RecipeEntryBase<RecipeSymbol, ItemStack, Void>)recipe.getOriginal();
        ingredients.add(0, (IIngredientList)IngredientList.fromRecipeSymbol((RecipeSymbol)((RecipeSymbol)entry.getInput())));
        ingredients.add(1, (IIngredientList)new IngredientList(new ITypedIngredient[]{TypedIngredient.itemStackIngredient((ItemStack)((ItemStack)entry.getOutput()))}));
    }

    public IRecipeLayout getRecipeLayout() {
        return new RecipeLayoutBuilder().addInputSlot(0, (IIngredientType)VanillaTypes.ITEM_STACK).setPosition(this.x, this.background.getHeight() / 2 - 6).build().addOutputSlot(1, (IIngredientType)VanillaTypes.ITEM_STACK).setPosition(this.x + 56, this.background.getHeight() / 2 - 6).build().build();
    }
}

