package in.mayank.renderer;

import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import in.mayank.extra.model.Cards;
import in.mayank.extra.model.RawModel;
import in.mayank.extra.utils.Maths;
import in.mayank.shader.Shader;

public class CardsRenderer extends Renderer {
	
	private Shader shader;
	
	public CardsRenderer(String vertexFile, String fragmentFile) {
		shader = new Shader(vertexFile, fragmentFile);
	}
	
	public void render(Cards card) {
		shader.start();
		RawModel model = Cards.getModel();
		prepareRender(model);
		loadTexture2D(0, card.getTexture());
		GL11.glDepthMask(false);
		Vector2f pos = card.getPosition();
		shader.loadModelMatrix(Maths.createTransformationMatrix(new Vector3f(pos.x, pos.y, 0), card.getScale()));
		drawTriangleCall(model);
		GL11.glDepthMask(true);
		finishRender(model);
		shader.stop();
	}
	
	public void render(List<Cards> cards) {
		shader.start();
		RawModel model = Cards.getModel();
		prepareRender(model);
		GL11.glDepthMask(false);
		for(Cards card : cards) {
			loadTexture2D(0, card.getTexture());
			Vector2f pos = card.getPosition();
			shader.loadModelMatrix(Maths.createTransformationMatrix(new Vector3f(pos.x, pos.y, 0), card.getScale()));
			drawTriangleCall(model);
		}
		GL11.glDepthMask(true);
		finishRender(model);
		shader.stop();
	}
	
	public void dispose() {
		shader.dispose();
	}
	
}
