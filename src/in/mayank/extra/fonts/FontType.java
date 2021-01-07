package in.mayank.extra.fonts;

import java.io.File;

/** Represents a font. It holds the font's texture atlas as well as having the
 * ability to create the quad vertices for any text using this font.
 * 
 * @author Karl */
public class FontType {

	private int textureAtlas;
	private TextMeshCreator loader;

	/** Creates a new font and loads up the data about each character from the
	 * font file.
	 * 
	 * @param textureAtlas The ID of the font atlas texture.
	 * @param fontFile The font file containing information about each character in
	 * the texture atlas. */
	public FontType(int textureAtlas, File fontFile) {
		this.textureAtlas = textureAtlas;
		this.loader = new TextMeshCreator(fontFile);
	}

	/** @return The font texture atlas. */
	public int getTextureAtlas() {
		return textureAtlas;
	}

	/** Takes in an unloaded text and calculate all of the vertices for the quads
	 * on which this text will be rendered. The vertex positions and texture
	 * coords and calculated based on the information from the font file.
	 * 
	 * @param text The unloaded text.
	 * 
	 * @return Information about the vertices of all the quads. */
	public TextMeshData loadText(GUIText text) {
		return loader.createTextMesh(text);
	}
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((loader == null) ? 0 : loader.hashCode());
		result = prime * result + textureAtlas;
		return result;
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FontType other = (FontType) obj;
		if (loader == null) {
			if (other.loader != null)
				return false;
		} else if (!loader.equals(other.loader))
			return false;
		if (textureAtlas != other.textureAtlas)
			return false;
		return true;
	}
	
}
