package ic2.client;

import ic2.entity.EntityMiningLaser;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.renderer.Shaders;
import net.minecraft.client.render.renderer.State;
import net.minecraft.core.entity.Entity;
import org.jetbrains.annotations.NotNull;


public class EntityRendererMiningLaser extends EntityRenderer<EntityMiningLaser> {

	public EntityRendererMiningLaser() {
		super(0.0f);
	}

	@Override
	public void render(@NotNull TessellatorGeneral tessellator, @NotNull EntityMiningLaser entity,
					double x, double y, double z, float yaw, float partialTick) {
		if (entity.yRot == 0.0f && entity.xRot == 0.0f) {
			return;
		}
		this.bindTexture("/assets/ic2/textures/entity/laser.png");
		GLRenderer.pushFrame();
		GLRenderer.modelM4f().translate((float) x, (float) y, (float) z);
		GLRenderer.modelM4f().rotate(entity.yRot, 0.0f, 1.0f, 0.0f);
		GLRenderer.modelM4f().rotate(entity.xRot, 1.0f, 0.0f, 0.0f);
		
		float f6 = 0.0f;
		float f7 = 0.15625f;
		float f8 = 5.0f / 32.0f;
		float f9 = 10.0f / 32.0f;
		float f10 = 0.05625f;
		GLRenderer.modelM4f().rotate(45.0f, 1.0f, 0.0f, 0.0f);
		GLRenderer.modelM4f().scale(f10, f10, f10);
		GLRenderer.modelM4f().translate(-4.0f, 0.0f, 0.0f);
		tessellator.startDrawing(net.minecraft.client.render.renderer.DrawMode.QUADS);
		
		tessellator.addVertexWithUV(-7.0, -2.0, -2.0, f6, f8);
		tessellator.addVertexWithUV(-7.0, -2.0, 2.0, f7, f8);
		tessellator.addVertexWithUV(-7.0, 2.0, 2.0, f7, f9);
		tessellator.addVertexWithUV(-7.0, 2.0, -2.0, f6, f9);
		tessellator.draw();
		tessellator.startDrawing(net.minecraft.client.render.renderer.DrawMode.QUADS);
		
		tessellator.addVertexWithUV(-7.0, 2.0, -2.0, f6, f8);
		tessellator.addVertexWithUV(-7.0, 2.0, 2.0, f7, f8);
		tessellator.addVertexWithUV(-7.0, -2.0, 2.0, f7, f9);
		tessellator.addVertexWithUV(-7.0, -2.0, -2.0, f6, f9);
		tessellator.draw();
		
		for (int j = 0; j < 4; ++j) {
			GLRenderer.pushFrame();
			GLRenderer.modelM4f().rotate(90.0f, 1.0f, 0.0f, 0.0f);
			tessellator.startDrawing(net.minecraft.client.render.renderer.DrawMode.QUADS);
			tessellator.addVertexWithUV(-7.0, -2.0, -2.0, f6, f8);
			tessellator.addVertexWithUV(-7.0, -2.0, 2.0, f7, f8);
			tessellator.addVertexWithUV(-7.0, 2.0, 2.0, f7, f9);
			tessellator.addVertexWithUV(-7.0, 2.0, -2.0, f6, f9);
			tessellator.draw();
			GLRenderer.popFrame();
		}
		GLRenderer.popFrame();
	}
}
