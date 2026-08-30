

package ic2.client;

import ic2.IC2Blocks;
import ic2.entity.EntityIC2Explosive;
import net.minecraft.client.render.block.model.BlockModel;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.renderer.BlendFactor;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.renderer.Shaders;
import net.minecraft.client.render.renderer.State;
import net.minecraft.client.render.shader.Shader;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.block.Block;
import org.jetbrains.annotations.NotNull;

public class EntityRendererIC2Explosive
extends EntityRenderer<EntityIC2Explosive> {
    public EntityRendererIC2Explosive() {
        super(0.5f);
    }

    public void render(@NotNull TessellatorGeneral tessellator, @NotNull EntityIC2Explosive entity, double x, double y, double z, float yaw, float partialTick) {
        Block<?> renderBlock;
        Block<?> block = renderBlock = entity.renderBlock != null ? entity.renderBlock : IC2Blocks.industrialTnt;
        if (renderBlock == null) {
            return;
        }
        GLRenderer.pushFrame();
        GLRenderer.modelM4f().translate((float)x, (float)y, (float)z);
        if ((float)entity.fuse - partialTick + 1.0f < 10.0f) {
            float f2 = 1.0f - ((float)entity.fuse - partialTick + 1.0f) / 10.0f;
            if (f2 < 0.0f) {
                f2 = 0.0f;
            }
            if (f2 > 1.0f) {
                f2 = 1.0f;
            }
            f2 *= f2;
            f2 *= f2;
            float f4 = 1.0f + f2 * 0.3f;
            GLRenderer.modelM4f().scale(f4, f4, f4);
        }
        float alpha = (1.0f - ((float)entity.fuse - partialTick + 1.0f) / 100.0f) * 0.8f;
        TextureRegistry.worldAtlas.bind();
        BlockModel model = (BlockModel)BlockModelDispatcher.getInstance().getDispatch(renderBlock);
        model.renderStandalone(tessellator, 0, entity.getLightIndex(partialTick));
        if (entity.fuse / 5 % 2 == 0) {
            GLRenderer.pushFrame();
            GLRenderer.setShader((Shader)Shaders.COLOR_WORLD);
            GLRenderer.globalSetLightEnabled((boolean)false);
            GLRenderer.enableState((State)State.BLEND);
            GLRenderer.setBlendFunc((BlendFactor)BlendFactor.SRC_ALPHA, (BlendFactor)BlendFactor.DST_ALPHA);
            GLRenderer.setColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)alpha);
            model.renderStandalone(tessellator, 0, entity.getLightIndex(partialTick));
            GLRenderer.setColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GLRenderer.disableState((State)State.BLEND);
            GLRenderer.globalSetLightEnabled((boolean)true);
            GLRenderer.popFrame();
        }
        GLRenderer.popFrame();
    }
}

