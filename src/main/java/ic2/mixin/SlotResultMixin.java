

package ic2.mixin;

import net.minecraft.core.achievement.Achievements;
import net.minecraft.core.achievement.stat.Stat;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCrafting;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemBucket;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.item.tool.ItemToolHoe;
import net.minecraft.core.item.tool.ItemToolPickaxe;
import net.minecraft.core.item.tool.ItemToolSword;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.container.ContainerCrafting;
import net.minecraft.core.player.inventory.slot.Slot;
import net.minecraft.core.player.inventory.slot.SlotResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={SlotResult.class}, remap=false)
public abstract class SlotResultMixin
extends Slot {
    @Shadow
    private Player thePlayer;
    @Shadow
    private Container craftSlots;

    public SlotResultMixin(Container container, int index, int x, int y) {
        super(container, index, x, y);
    }

    @Inject(method={"onTake"}, at={@At(value="HEAD")}, cancellable=true)
    private void ic2$safeOnTake(ItemStack itemStack, CallbackInfo ci) {
        Container container;
        ci.cancel();
        if (itemStack == null || this.thePlayer == null) {
            return;
        }
        itemStack.onCrafting(this.thePlayer.world, this.thePlayer);
        Item item = itemStack.getItem();
        if (item.id == Blocks.WORKBENCH.id()) {
            this.thePlayer.addStat((Stat)Achievements.BUILD_WORKBENCH, 1);
        }
        if (item.id == Blocks.FURNACE_STONE_IDLE.id()) {
            this.thePlayer.addStat((Stat)Achievements.BUILD_FURNACE, 1);
        }
        if (item.id == Blocks.FURNACE_BLAST_IDLE.id()) {
            this.thePlayer.addStat((Stat)Achievements.GET_STEEL_BLAST_FURNACE, 1);
        }
        if (item.id == Items.FOOD_CAKE.id) {
            this.thePlayer.addStat((Stat)Achievements.BAKE_CAKE, 1);
        }
        if (item instanceof ItemBucket && ItemBucket.getState((ItemStack)itemStack) == ItemBucket.STATE_ICECREAM) {
            this.thePlayer.addStat((Stat)Achievements.CRAFT_ICECREAM, 1);
        }
        if (item.id == Items.FOOD_PUMPKIN_PIE.id) {
            this.thePlayer.addStat((Stat)Achievements.CRAFT_PUMPKIN_PIE, 1);
        }
        if (item.id == Items.HANDCANNON_UNLOADED.id) {
            this.thePlayer.addStat((Stat)Achievements.CRAFT_HANDCANNON, 1);
        }
        if (item instanceof ItemToolHoe) {
            this.thePlayer.addStat((Stat)Achievements.BUILD_HOE, 1);
        }
        if (item instanceof ItemToolSword) {
            this.thePlayer.addStat((Stat)Achievements.BUILD_SWORD, 1);
        }
        if (item instanceof ItemToolPickaxe) {
            ItemToolPickaxe pickaxe = (ItemToolPickaxe)item;
            if (pickaxe.getMaterial().getMiningLevel() > 0) {
                this.thePlayer.addStat((Stat)Achievements.BUILD_BETTER_PICKAXE, 1);
            }
            this.thePlayer.addStat((Stat)Achievements.BUILD_PICKAXE, 1);
        }
        if (itemStack.itemID == Items.ARMOR_BOOTS_CHAINMAIL.id || itemStack.itemID == Items.ARMOR_HELMET_CHAINMAIL.id || itemStack.itemID == Items.ARMOR_CHESTPLATE_CHAINMAIL.id || itemStack.itemID == Items.ARMOR_LEGGINGS_CHAINMAIL.id) {
            this.thePlayer.addStat((Stat)Achievements.REPAIR_ARMOR, 1);
        }
        if (item.id == Items.INGOT_STEEL.id) {
            this.thePlayer.addStat((Stat)Achievements.OBTAIN_STEEL, 1);
        }
        if (item.id == Items.BUCKET_STEEL.id) {
            this.thePlayer.addStat((Stat)Achievements.CRAFT_STEEL_BUCKET, 1);
        }
        if (item.id == Blocks.CONDUIT.id()) {
            this.thePlayer.addStat((Stat)Achievements.CRAFT_CONDUIT, 1);
        }
        if ((container = this.craftSlots) instanceof ContainerCrafting) {
            RecipeEntryCrafting recipe;
            ContainerCrafting crafting = (ContainerCrafting)container;
            if (!this.thePlayer.world.isClientSide && (recipe = Registries.RECIPES.findMatchingCraftingRecipe(crafting)) != null && recipe.toString().startsWith("minecraft:workbench/acid_conversion_")) {
                this.thePlayer.addStat((Stat)Achievements.CRAFT_ACID_COBBLE_TO_STONE, 1);
            }
            Registries.RECIPES.onCraftResult(crafting);
        }
    }
}

