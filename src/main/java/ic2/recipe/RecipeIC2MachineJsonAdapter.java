package ic2.recipe;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.data.registry.recipe.adapter.RecipeJsonAdapter;
import net.minecraft.core.item.ItemStack;

public class RecipeIC2MachineJsonAdapter implements RecipeJsonAdapter<RecipeEntryIC2Machine> {
    @Override
    public RecipeEntryIC2Machine deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        RecipeSymbol input = (RecipeSymbol)context.deserialize(obj.get("input").getAsJsonObject(), (Type)((Object)RecipeSymbol.class));
        ItemStack output = (ItemStack)context.deserialize(obj.get("output").getAsJsonObject(), (Type)((Object)ItemStack.class));
        return new RecipeEntryIC2Machine(input, output);
    }

    @Override
    public JsonElement serialize(RecipeEntryIC2Machine src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", src.toString());
        obj.addProperty("type", Registries.RECIPE_TYPES.getKey(src.getClass()));
        obj.add("input", context.serialize(src.getInput(), (Type)((Object)RecipeSymbol.class)));
        obj.add("output", context.serialize(src.getOutput(), (Type)((Object)ItemStack.class)));
        return obj;
    }
}
