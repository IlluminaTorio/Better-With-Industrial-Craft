package ic2.client;

import ic2.entity.EntityDrone;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.renderer.DrawMode;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import org.jetbrains.annotations.NotNull;

public class EntityRendererDrone
extends EntityRenderer<EntityDrone> {
    public EntityRendererDrone() {
        super(0.3f);
    }

    @Override
    public void render(@NotNull TessellatorGeneral tessellator, @NotNull EntityDrone entity, double x, double y, double z, float yaw, float partialTick) {
        this.bindTexture("/assets/ic2/textures/entity/drone.png");
        GLRenderer.pushFrame();
        GLRenderer.modelM4f().translate((float)x, (float)y, (float)z);
        GLRenderer.modelM4f().rotate(entity.yRot, 0.0f, 1.0f, 0.0f);
        GLRenderer.modelM4f().rotate(45.0f, 0.0f, 1.0f, 0.0f);
        float body = 0.2f;
        float lift = 0.05f;
        tessellator.startDrawing(DrawMode.QUADS);
        tessellator.addVertexWithUV((double)(-body), 0.0, (double)(-body), 0.0, 0.0);
        tessellator.addVertexWithUV((double)(-body), 0.0, (double)body, 0.0, 1.0);
        tessellator.addVertexWithUV((double)body, 0.0, (double)body, 1.0, 1.0);
        tessellator.addVertexWithUV((double)body, 0.0, (double)(-body), 1.0, 0.0);
        tessellator.draw();
        tessellator.startDrawing(DrawMode.QUADS);
        tessellator.addVertexWithUV((double)(-body), 0.0, (double)(-body), 0.0, 0.0);
        tessellator.addVertexWithUV((double)body, 0.0, (double)(-body), 1.0, 0.0);
        tessellator.addVertexWithUV((double)body, 0.0, (double)body, 1.0, 1.0);
        tessellator.addVertexWithUV((double)(-body), 0.0, (double)body, 0.0, 1.0);
        tessellator.draw();
        tessellator.startDrawing(DrawMode.QUADS);
        tessellator.addVertexWithUV((double)(-body), 0.0, 0.0, 0.0, 0.5);
        tessellator.addVertexWithUV((double)body, 0.0, 0.0, 1.0, 0.5);
        tessellator.addVertexWithUV((double)body, (double)lift, 0.0, 1.0, 0.5);
        tessellator.addVertexWithUV((double)(-body), (double)lift, 0.0, 0.0, 0.5);
        tessellator.draw();
        GLRenderer.popFrame();
    }
}
