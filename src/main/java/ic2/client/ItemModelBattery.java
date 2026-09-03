package ic2.client;

import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class ItemModelBattery
extends net.minecraft.client.render.item.model.ItemModelStandard {

        private final String[] textures;
        private final IconCoordinate[] icons;


        public ItemModelBattery(@NotNull Item item, String baseKey, String[] suffixes) {
                super(item, false);
                this.textures = suffixes;
                this.icons = new IconCoordinate[suffixes.length];
                for (int i = 0; i < suffixes.length; ++i) {
                        this.icons[i] = TextureRegistry.getTexture(baseKey + suffixes[i]);
                }
                this.icon = this.icons[0];
        }

        @Override
        @NotNull
        public IconCoordinate getIcon(@Nullable Entity entity, @NotNull ItemStack itemStack) {
                int maxDamage = itemStack.getMaxDamage();
                int meta = itemStack.getMetadata();
                double charge;
                if (maxDamage <= 0) {
                        charge = 1.0;
                } else {
                        charge = (double)Math.max(0, maxDamage + 1 - meta) / (double)(maxDamage + 1);
                }
                int level;
                if (charge > 0.75) {
                        level = 0;
                } else if (charge > 0.5) {
                        level = 1;
                } else if (charge > 0.25) {
                        level = 2;
                } else if (charge > 0.0) {
                        level = 3;
                } else {
                        level = 4;
                }
                if (level >= this.icons.length) {
                        level = this.icons.length - 1;
                }
                return this.icons[level];
        }
}
