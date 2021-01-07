package in.mayank.extra.fonts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import in.mayank.extra.core.Core;

/** Provides functionality for getting the values from a font file. */
public class MetaFile {

	private static final int PAD_TOP = 0, PAD_LEFT = 1, PAD_BOTTOM = 2, PAD_RIGHT = 3;

	private static final int DESIRED_PADDING = 8;

	private static final String SPLITTER = " ", NUMBER_SEPARATOR = ",";

	private double aspectRatio;

	private double verticalPerPixelSize, horizontalPerPixelSize, spaceWidth;
	private int padding[], paddingWidth, paddingHeight;

	private Map<Integer, Character> metaData = new HashMap<Integer, Character>();

	private BufferedReader reader;
	private Map<String, String> values = new HashMap<String, String>();

	/** Opens a font file in preparation for reading.
	 * 
	 * @param file The font file. */
	protected MetaFile(File file) {
		aspectRatio = Core.getWidth() / Core.getHeight();
		openFile(file);
		loadPaddingData();
		loadLineSizes();
		int imageWidth = getValueOfVariable("scaleW");
		loadCharacterData(imageWidth);
		close();
	}

	protected double getSpaceWidth() {
		return spaceWidth;
	}

	protected Character getCharacter(int ascii) {
		return metaData.get(ascii);
	}

	/** Read in the next line and store the variable values.
	 * 
	 * @return {@code true} if the end of the file hasn't been reached. */
	private boolean processNextLine() {
		values.clear();
		String line = null;
		try {
			line = reader.readLine();
		} catch (IOException e1) {System.out.println("Couldn't read the line of text.\n" + e1.getMessage()); }
		if (line == null) return false;
		for (String part : line.split(SPLITTER)) {
			String[] valuePairs = part.split("=");
			if (valuePairs.length == 2)
				values.put(valuePairs[0], valuePairs[1]);
		}
		return true;
	}

	/** Gets the {@code int} value of the variable with a certain name on the
	 * current line.
	 * 
	 * @param variable The name of the variable.
	 * @return The value of the variable. */
	private int getValueOfVariable(String variable) {
		return Integer.parseInt(values.get(variable));
	}

	/** Gets the array of ints associated with a variable on the current line.
	 * 
	 * @param variable The name of the variable.
	 * 
	 * @return The int array of values associated with the variable. */
	private int[] getValuesOfVariable(String variable) {
		String[] numbers = values.get(variable).split(NUMBER_SEPARATOR);
		int[] actualValues = new int[numbers.length];
		for (int i = 0; i < actualValues.length; i++) {
			actualValues[i] = Integer.parseInt(numbers[i]);
		}
		return actualValues;
	}

	/** Closes the font file after finishing reading. */
	private void close() {
		try {reader.close();} catch (IOException e) {e.printStackTrace();}
	}

	/** Opens the font file, ready for reading.
	 * 
	 * @param file The font file. */
	private void openFile(File file) {
		try {reader = new BufferedReader(new FileReader(file));} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Couldn't read font meta file!");
		}
	}

	/** Loads the data about how much padding is used around each character in
	 * the texture atlas. */
	private void loadPaddingData() {
		processNextLine();
		this.padding = getValuesOfVariable("padding");
		this.paddingWidth = padding[PAD_LEFT] + padding[PAD_RIGHT];
		this.paddingHeight = padding[PAD_TOP] + padding[PAD_BOTTOM];
	}

	/** Loads information about the line height for this font in pixels, and uses
	 * this as a way to find the conversion rate between pixels in the texture
	 * atlas and screen-space. */
	private void loadLineSizes() {
		processNextLine();
		int lineHeightPixels = getValueOfVariable("lineHeight") - paddingHeight;
		verticalPerPixelSize = TextMeshCreator.LINE_HEIGHT / (double) lineHeightPixels;
		horizontalPerPixelSize = verticalPerPixelSize / aspectRatio;
	}

	/** Loads in data about each character and stores the data in the
	 * {@link Character} class.
	 * 
	 * @param imageWidth The width of the texture atlas in pixels. */
	private void loadCharacterData(int imageWidth) {
		processNextLine();
		processNextLine();
		while (processNextLine()) {
			Character c = loadCharacter(imageWidth);
			if (c != null) {
				metaData.put(c.getId(), c);
			}
		}
	}

	/** Loads all the data about one character in the texture atlas and converts
	 * it all from 'pixels' to 'screen-space' before storing. The effects of
	 * padding are also removed from the data.
	 * 
	 * @param imageSize The size of the texture atlas in pixels.
	 * @return The data about the character. */
	private Character loadCharacter(int imageSize) {
		int id = getValueOfVariable("id");
		if (id == TextMeshCreator.SPACE_ASCII) {
			this.spaceWidth = (getValueOfVariable("xadvance") - paddingWidth) * horizontalPerPixelSize;
			return null;
		}
		double xTex = ((double) getValueOfVariable("x") + (padding[PAD_LEFT] - DESIRED_PADDING)) / imageSize;
		double yTex = ((double) getValueOfVariable("y") + (padding[PAD_TOP] - DESIRED_PADDING)) / imageSize;
		int width = getValueOfVariable("width") - (paddingWidth - (2 * DESIRED_PADDING));
		int height = getValueOfVariable("height") - (paddingHeight - (2 * DESIRED_PADDING));
		double quadWidth = width * horizontalPerPixelSize;
		double quadHeight = height * verticalPerPixelSize;
		double xTexSize = (double) width / imageSize;
		double yTexSize = (double) height / imageSize;
		double xOff = (getValueOfVariable("xoffset") + padding[PAD_LEFT] - DESIRED_PADDING) * horizontalPerPixelSize;
		double yOff = (getValueOfVariable("yoffset") + (padding[PAD_TOP] - DESIRED_PADDING)) * verticalPerPixelSize;
		double xAdvance = (getValueOfVariable("xadvance") - paddingWidth) * horizontalPerPixelSize;
		return new Character(id, xTex, yTex, xTexSize, yTexSize, xOff, yOff, quadWidth, quadHeight, xAdvance);
	}
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(aspectRatio);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(horizontalPerPixelSize);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((metaData == null) ? 0 : metaData.hashCode());
		result = prime * result + Arrays.hashCode(padding);
		result = prime * result + paddingHeight;
		result = prime * result + paddingWidth;
		temp = Double.doubleToLongBits(spaceWidth);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((values == null) ? 0 : values.hashCode());
		temp = Double.doubleToLongBits(verticalPerPixelSize);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MetaFile other = (MetaFile) obj;
		if (Double.doubleToLongBits(aspectRatio) != Double.doubleToLongBits(other.aspectRatio))
			return false;
		if (Double.doubleToLongBits(horizontalPerPixelSize) != Double.doubleToLongBits(other.horizontalPerPixelSize))
			return false;
		if (metaData == null) {
			if (other.metaData != null)
				return false;
		} else if (!metaData.equals(other.metaData))
			return false;
		if (!Arrays.equals(padding, other.padding))
			return false;
		if (paddingHeight != other.paddingHeight)
			return false;
		if (paddingWidth != other.paddingWidth)
			return false;
		if (Double.doubleToLongBits(spaceWidth) != Double.doubleToLongBits(other.spaceWidth))
			return false;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		if (Double.doubleToLongBits(verticalPerPixelSize) != Double.doubleToLongBits(other.verticalPerPixelSize))
			return false;
		return true;
	}
}
