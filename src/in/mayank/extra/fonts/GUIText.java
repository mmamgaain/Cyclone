package in.mayank.extra.fonts;

import org.joml.Vector2f;
import org.joml.Vector3f;

import in.mayank.extra.utils.Loader;

/** Represents a piece of text in the game. */
public class GUIText {

	private StringBuilder textString;
	private float fontSize;

	private int textMeshVao;
	private int vertexCount;
	private Vector3f colour = new Vector3f();

	private Vector2f position;
	private float lineMaxSize;
	private int numberOfLines;

	private FontType font;
	
	private float charWidth = 0.5F, edgeTransition = 0.1F, borderWidth = 0.7F, borderEdge = 0.1F;
	private Vector3f outlineColor = new Vector3f();
	private Vector2f offset = new Vector2f(0.05F);
	
	private boolean centerText = false;

	/** Creates a new text, loads the text's quads into a VAO, and adds the text
	 * to the screen.
	 * 
	 * @param text The text.
	 * @param loader The instance of the {@link Loader} class.
	 * @param fontSize The font size of the text, where a font size of 1 is the
	 * default size.
	 * @param font The font that this text should use.
	 * @param position The position on the screen where the top left corner of the
	 * text should be rendered. The top left corner of the screen is (0, 0) and the
	 * bottom right is (1, 1).
	 * @param maxLineLength Basically the width of the virtual page in terms of screen
	 * width (1 is full screen width, 0.5 is half the width of the screen, etc.) Text
	 * cannot go off the edge of the page, so if the text is longer than this length it
	 * will go onto the next line. When text is centered it is centered into the middle
	 * of the line, based on this line length value.
	 * @param centered Whether the text should be centered or not. */
	public GUIText(String text, Loader loader, float fontSize, FontType font, Vector2f position, float maxLineLength, boolean centered) {
		textString = new StringBuilder(text);
		this.fontSize = fontSize;
		this.font = font;
		this.position = position;
		lineMaxSize = maxLineLength;
		centerText = centered;
		TextMaster.loadText(this, loader);
	}

	/** Remove the text from the screen. */
	public void remove() {
		TextMaster.removeText(this);
	}

	/** @return The font used by this text. */
	public FontType getFont() {
		return font;
	}
	
	public float getCharWidth() {
		return charWidth;
	}
	
	public GUIText setCharWidth(float charWidth) {
		this.charWidth = charWidth;
		return this;
	}
	
	public float getEdgeTransition() {
		return edgeTransition;
	}
	
	public GUIText setEdgeTransition(float edgeTransition) {
		this.edgeTransition = edgeTransition;
		return this;
	}
	
	public float getBorderWidth() {
		return borderWidth;
	}
	
	public GUIText setBorderWidth(float borderWidth) {
		this.borderWidth = borderWidth;
		return this;
	}
	
	public float getBorderEdge() {
		return borderEdge;
	}
	
	public GUIText setBorderEdge(float borderEdge) {
		this.borderEdge = borderEdge;
		return this;
	}
	
	public GUIText setOutlineColor(Vector3f color) {
		outlineColor = color;
		return this;
	}
	
	public Vector3f getOutlineColor() {
		return outlineColor;
	}
	
	public GUIText setBorderOffset(Vector2f offset) {
		this.offset = offset;
		return this;
	}
	
	public Vector2f getBorderOffset() {
		return offset;
	}

	/** Set the colour of the text.
	 * 
	 * @param r Red value, between 0 and 1.
	 * @param g Green value, between 0 and 1.
	 * @param b Blue value, between 0 and 1. */
	public GUIText setColour(float r, float g, float b) {
		colour.set(r, g, b);
		return this;
	}

	/** @return the colour of the text. */
	public Vector3f getColour() {
		return colour;
	}

	/** @return The number of lines of text. This is determined when the text is 
	 * loaded, based on the length of the text and the max line length
	 * that is set. */
	public int getNumberOfLines() {
		return numberOfLines;
	}

	/** @return The position of the top-left corner of the text in screen-space.
	 * (0, 0) is the top left corner of the screen, (1, 1) is the bottom
	 * right. */
	public Vector2f getPosition() {
		return position;
	}

	/** @return the ID of the text's VAO, which contains all the vertex data for
	 * the quads on which the text will be rendered. */
	public int getMesh() {
		return textMeshVao;
	}

	/** Set the VAO and vertex count for this text.
	 * 
	 * @param vao The VAO containing all the vertex data for the quads on
	 * which the text will be rendered.
	 * @param verticesCount The total number of vertices in all of the quads. */
	public void setMeshInfo(int vao, int verticesCount) {
		this.textMeshVao = vao;
		this.vertexCount = verticesCount;
	}
	 

	/** @return The total number of vertices of all the text's quads. */
	public int getVertexCount() {
		return this.vertexCount;
	}

	/** @return the font size of the text (a font size of 1 is normal). */
	protected float getFontSize() {
		return fontSize;
	}

	/** Sets the number of lines that this text covers (method used only in
	 * loading).
	 * 
	 * @param number */
	protected void setNumberOfLines(int number) {
		this.numberOfLines = number;
	}

	/** @return {@code true} if the text should be centered. */
	protected boolean isCentered() {
		return centerText;
	}

	/** @return The maximum length of a line of this text. */
	protected float getMaxLineSize() {
		return lineMaxSize;
	}
	
	/**  */
	public void setTextString(String text) {
		textString.replace(0, textString.length(), text);
	}

	/** @return The string of text. */
	protected String getTextString() {
		return textString.substring(0);
	}
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(borderEdge);
		result = prime * result + Float.floatToIntBits(borderWidth);
		result = prime * result + (centerText ? 1231 : 1237);
		result = prime * result + Float.floatToIntBits(charWidth);
		result = prime * result + ((colour == null) ? 0 : colour.hashCode());
		result = prime * result + Float.floatToIntBits(edgeTransition);
		result = prime * result + ((font == null) ? 0 : font.hashCode());
		result = prime * result + Float.floatToIntBits(fontSize);
		result = prime * result + Float.floatToIntBits(lineMaxSize);
		result = prime * result + numberOfLines;
		result = prime * result + ((offset == null) ? 0 : offset.hashCode());
		result = prime * result + ((outlineColor == null) ? 0 : outlineColor.hashCode());
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		result = prime * result + textMeshVao;
		result = prime * result + vertexCount;
		return result;
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GUIText other = (GUIText) obj;
		if (Float.floatToIntBits(borderEdge) != Float.floatToIntBits(other.borderEdge))
			return false;
		if (Float.floatToIntBits(borderWidth) != Float.floatToIntBits(other.borderWidth))
			return false;
		if (centerText != other.centerText)
			return false;
		if (Float.floatToIntBits(charWidth) != Float.floatToIntBits(other.charWidth))
			return false;
		if (colour == null) {
			if (other.colour != null)
				return false;
		} else if (!colour.equals(other.colour))
			return false;
		if (Float.floatToIntBits(edgeTransition) != Float.floatToIntBits(other.edgeTransition))
			return false;
		if (font == null) {
			if (other.font != null)
				return false;
		} else if (!font.equals(other.font))
			return false;
		if (Float.floatToIntBits(fontSize) != Float.floatToIntBits(other.fontSize))
			return false;
		if (Float.floatToIntBits(lineMaxSize) != Float.floatToIntBits(other.lineMaxSize))
			return false;
		if (numberOfLines != other.numberOfLines)
			return false;
		if (offset == null) {
			if (other.offset != null)
				return false;
		} else if (!offset.equals(other.offset))
			return false;
		if (outlineColor == null) {
			if (other.outlineColor != null)
				return false;
		} else if (!outlineColor.equals(other.outlineColor))
			return false;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		if (textMeshVao != other.textMeshVao)
			return false;
		if (vertexCount != other.vertexCount)
			return false;
		return true;
	}
	
}
