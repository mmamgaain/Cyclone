package in.mayank.extra.fonts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.mayank.extra.utils.Loader;
import in.mayank.renderer.FontRenderer;

/**  */
public class TextMaster {
	
	private static Map<FontType, List<GUIText>> texts = new HashMap<FontType, List<GUIText>>();
	private static FontRenderer renderer;
	
	public static void init(String vertexFile, String fragmentFile){
		renderer = new FontRenderer(vertexFile, fragmentFile);
	}
	
	/** Renders all the texts which have been added to this class with
	 * the help of the {@link #loadText(GUIText)} method.<br><br>
	 * 
	 * <b><u>NOTE</u> :</b> <i>1. While rendering the text the depth test is disabled,
	 * so this command should be put at the end of the rendering process so that
	 * the texts are rendered on top of the scene.<br>
	 * 2. The default color of the text rendered is black, so if you want to change
	 * it, access the {@link GUIText #setColour(float, float, float)} method.</i> */
	public static void render(){
		renderer.render(texts);
	}
	
	/** Loads the supplied text into the list of other {@link GUIText}s which are
	 * to be rendered.
	 * 
	 * @param text The {@link GUIText} object that needs to be rendered.
	 * @param loader The instance of the {@link Loader} class. */
	public static void loadText(GUIText text, Loader loader){
		FontType font = text.getFont();
		TextMeshData data = font.loadText(text);
		int vao = loader.loadToVAO(data.getVertexPositions(), 2, data.getTextureCoords()).getVaoID();
		text.setMeshInfo(vao, data.getVertexCount());
		List<GUIText> textBatch = texts.get(font);
		if(textBatch == null){
			textBatch = new ArrayList<>();
			texts.put(font, textBatch);
		}
		textBatch.add(text);
	}
	
	/** Removes the supplied text from the list of texts that will be rendered.
	 * 
	 *  @param text The text that doesn't need to be rendered. */
	public static void removeText(GUIText text){
		List<GUIText> textBatch = texts.get(text.getFont());
		textBatch.remove(text);
		if(textBatch.isEmpty())
			texts.remove(text.getFont());
	}
	
	/** Incomplete */
	public static void clearText(GUIText text) {
		List<GUIText> textBatch = texts.get(text.getFont());
		//inform the renderer to clear text off the screen
		if(textBatch.isEmpty())
			texts.remove(text.getFont());
	}
	
	/** Closes the renderer. */
	public static void dispose(){
		renderer.dispose();
	}
	
}
