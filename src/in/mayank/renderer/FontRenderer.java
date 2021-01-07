package in.mayank.renderer;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import in.mayank.extra.fonts.FontType;
import in.mayank.extra.fonts.GUIText;
import in.mayank.shader.ShaderProgram;

public class FontRenderer {

	private FontShader shader;
	
	public FontRenderer(String vertexFile, String fragmentFile) {
		shader = new FontShader(vertexFile, fragmentFile);
	}
	
	private void prepare(){
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		shader.start();
	}
	
	private void renderText(GUIText text){
		GL30.glBindVertexArray(text.getMesh());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		shader.loadText(text);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, text.getVertexCount());
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
	}
	
	public void render(Map<FontType, List<GUIText>> texts){
		prepare();
		for(FontType font : texts.keySet()){
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, font.getTextureAtlas());
			for(GUIText text : texts.get(font)) renderText(text);
		}
		endRendering();
	}
	
	public void clear(GUIText text) {
		
	}
	
	private void endRendering(){
		shader.stop();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
	
	public void dispose(){
		shader.dispose();
	}

}

class FontShader extends ShaderProgram {

	public FontShader(String vertexFile, String fragmentFile) {
		super(vertexFile, fragmentFile);
	}
	
	void loadText(GUIText text) {
		loadUniform("color", text.getColour());
		loadUniform("outlineColor", text.getOutlineColor());
		loadUniform("charWidth", text.getCharWidth());
		loadUniform("edgeTransition", text.getEdgeTransition());
		loadUniform("borderWidth", text.getBorderWidth());
		loadUniform("borderEdge", text.getBorderEdge());
		loadUniform("offset", text.getBorderOffset());
		loadUniform("translation", text.getPosition());
	}
	
}
