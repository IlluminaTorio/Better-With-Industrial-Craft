package ic2.util;

import java.lang.reflect.Field;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemBucket;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.collection.NamespaceID;

public class BucketUtil {
    private static final String COMPAT_FILLED_CLASS = "teamport.aether.item.ItemBucketSkyroot";
    private static final String COMPAT_EMPTY_CLASS = "teamport.aether.item.ItemBucketSkyrootEmpty";
    private static volatile Item cachedCompatWaterBucket;

    public static boolean isBucketWithState(ItemStack stack, NamespaceID state) {
        if (stack == null) {
            return false;
        }
        Item item = stack.getItem();
        if (item instanceof ItemBucket) {
            return state.equals(ItemBucket.getState(stack));
        }
        return isCompatBucketWithState(stack, state);
    }

    public static boolean isEmptyBucket(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        Item item = stack.getItem();
        if (item instanceof ItemBucket) {
            return ItemBucket.STATE_EMPTY.equals(ItemBucket.getState(stack));
        }
        return isCompatEmptyBucket(stack);
    }

    public static ItemStack toEmpty(ItemStack filled) {
        if (filled == null) {
            return null;
        }
        Item item = filled.getItem();
        if (item instanceof ItemBucket) {
            ItemStack empty = filled.copy();
            empty.stackSize = 1;
            ItemBucket.setState(empty, ItemBucket.STATE_EMPTY);
            return empty;
        }
        Item container = item.getContainerItem();
        if (container != null) {
            return new ItemStack(container, 1);
        }
        ItemStack empty = filled.copy();
        empty.stackSize = 1;
        return empty;
    }

    public static ItemStack filledCopy(ItemStack bucket, NamespaceID state) {
        if (bucket == null) {
            return null;
        }
        Item item = bucket.getItem();
        if (item instanceof ItemBucket) {
            ItemStack filled = bucket.copy();
            filled.stackSize = 1;
            ItemBucket.setState(filled, state);
            return filled;
        }
        Item filledVariant = compatFilledVariant(state);
        if (filledVariant != null) {
            return new ItemStack(filledVariant, 1);
        }
        ItemStack filled = bucket.copy();
        filled.stackSize = 1;
        return filled;
    }

    public static ItemStack fillBucket(ItemStack empty, NamespaceID state) {
        return filledCopy(empty, state);
    }

    private static boolean isCompatBucketWithState(ItemStack stack, NamespaceID state) {
        if (ItemBucket.STATE_WATER.equals(state)) {
            return isCompatWaterBucket(stack);
        }
        return false;
    }

    private static boolean isCompatEmptyBucket(ItemStack stack) {
        return stack.getItem().getClass().getName().equals(COMPAT_EMPTY_CLASS);
    }

    private static boolean isCompatWaterBucket(ItemStack stack) {
        Item item = stack.getItem();
        if (!item.getClass().getName().equals(COMPAT_FILLED_CLASS)) {
            return false;
        }
        try {
            Field field = item.getClass().getField("blockToPlace");
            return field.get(item) == Blocks.FLUID_WATER_FLOWING;
        } catch (Throwable t) {
            return false;
        }
    }

    private static Item compatFilledVariant(NamespaceID state) {
        if (!ItemBucket.STATE_WATER.equals(state) && !ItemBucket.STATE_LAVA.equals(state)) {
            return null;
        }
        if (ItemBucket.STATE_LAVA.equals(state)) {
            return null;
        }
        Item cached = cachedCompatWaterBucket;
        if (cached != null) {
            return cached;
        }
        for (Item candidate : Item.itemsList) {
            if (candidate == null || !candidate.getClass().getName().equals(COMPAT_FILLED_CLASS)) {
                continue;
            }
            try {
                Field field = candidate.getClass().getField("blockToPlace");
                if (field.get(candidate) == Blocks.FLUID_WATER_FLOWING) {
                    cachedCompatWaterBucket = candidate;
                    return candidate;
                }
            } catch (Throwable t) {
            }
        }
        return null;
    }
}
