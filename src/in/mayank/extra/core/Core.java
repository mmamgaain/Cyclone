package in.mayank.extra.core;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowIcon;
import static org.lwjgl.glfw.GLFW.glfwSetWindowIconifyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_DEPTH_CLEAR_VALUE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_SCISSOR_TEST;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glClearDepth;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGetDouble;
import static org.lwjgl.opengl.GL11.glScissor;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.awt.Toolkit;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import in.mayank.extra.input.GameAction;
import in.mayank.extra.input.KeyboardHandler;
import in.mayank.extra.input.MouseManager;
import in.mayank.extra.input.MousePosition;
import in.mayank.extra.input.MouseWheel;

public abstract class Core {
	
	//The window handle
	private static long window;
	private static boolean fullscreen = true, isCellShaded = false, paused = false;
	private static GLFWImage.Buffer image = null;
	
	protected static int width = Toolkit.getDefaultToolkit().getScreenSize().width,
						 height = Toolkit.getDefaultToolkit().getScreenSize().height,
						 cellShadingLevels = 2, vsync = 1;
	private static float red = 0, green = 0, blue = 0, alpha = 1, near = 0.001F, far = 1000, fov = 70, aspectRatio;
	private static long numFrames = 0;
	
	//Time-keeping
	private static double delta = 0F, frameRate = 0F;
	
	private static String title = "Hello World!";
	
	public static final int MB_LEFT = 0, MB_RIGHT = 1, MB_MIDDLE = 2, MM_LEFT = 3,
							MM_RIGHT = 4, MM_UP = 5, MM_DOWN = 6, MW_DOWN = 7, MW_UP = 8;
	
	private List<Integer> queries = null;
	
	public static void init() {
		// Set an error callback. The default implementation will print
		// the error message in System.err
		GLFWErrorCallback.createPrint(System.err).set();
		
		// Initialize GLFW. Most GLFW functions will not work before
		// doing this
		if(!glfwInit()) throw new IllegalStateException("Unable to initialise GLFW");
		
		// Configure GLFW
		glfwDefaultWindowHints(); //Optional. The current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); //The window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, fullscreen ? GLFW_FALSE : GLFW_TRUE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		
		// Create the window
		window = glfwCreateWindow(width, height, title, fullscreen ? glfwGetPrimaryMonitor() : NULL, NULL);
		if(window == NULL) throw new RuntimeException("Failed to create the GLFW window.");
		
		// Setting aspect ratio
		aspectRatio = (float)width / height;
		
		// Setup key and mouse callbacks
		glfwSetKeyCallback(window, new KeyboardHandler());
		glfwSetMouseButtonCallback(window, new MouseManager());
		glfwSetCursorPosCallback(window, new MousePosition());
		glfwSetScrollCallback(window, new MouseWheel());
		glfwSetWindowIcon(window, image);
		glfwSetWindowIconifyCallback(window, (window, iconified) -> { if(iconified) { paused = true; } else { paused = false; } });
		
		// Get the thread stack and push a new frame
		try(MemoryStack stack = stackPush()){
			IntBuffer pWidth = stack.mallocInt(1),	// int*
					  pHeight = stack.mallocInt(1);	// int*
			
			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);
			
			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			
			// Center the window
			glfwSetWindowPos(window, ((vidmode.width() - pWidth.get(0)) / 2), ((vidmode.height() - pHeight.get(0)) / 2));
		}// The stack frame is popped automatically
		
		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(vsync);
		
		//Make the window visible
		glfwShowWindow(window);
		GL.createCapabilities();
		
		//
		glEnable(GL30.GL_CLIP_DISTANCE0);
		glEnable(GL13.GL_MULTISAMPLE);
		glViewport(0, 0, width, height);	
	}
	
	/***/
	public long getGLFWWindow() { return window; }
	
	/***/
	protected static void setVSync(int vsync) { Core.vsync = vsync; }
	
	/***/
	protected static int getVSync() { return vsync; }
	
	/** Enables the ability for a portion of the screen
	 * to be isolated. */
	protected static void enableScissor() { glEnable(GL_SCISSOR_TEST); }
	
	/** Disables the ability for a portion of the screen
	 * to be isolated. */
	protected static void disableScissor() { glDisable(GL_SCISSOR_TEST); }
	
	/** Isolates the specified portion of the screen. This size is in the
	 * pixel size of the screen. */
	protected static void scissor(int startX, int startY, int endX, int endY) { glScissor(startX, startY, endX, endY); }
	
	/** Sets the distance to which the depth buffer gets cleared to. This value
	 * should range between 0 and 1.
	 * 
	 * @param depth The clear distance of the depth buffer. */
	protected static void setClearDepth(double depth) { glClearDepth(depth); }
	
	protected static double getClearDepth() { return glGetDouble(GL_DEPTH_CLEAR_VALUE); }
	
	protected void startGame() {
		//-----------------Game initializing starts-----------------//
		
		double startTime, afterTime;
		
		//Set the clear color
		glClearColor(red, green, blue, alpha);
		
		//
		glEnable(GL_DEPTH_TEST);
		
		//glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		glfwSetCursorPos(window, width/2F, height/2F);
		
		startTime = glfwGetTime();
		
		//---------------------Game loop starts---------------------\\
		while(!glfwWindowShouldClose(window)) {
			glfwPollEvents();
			
			update();
			
			// Swaps the back buffer (the chunk of memory where OpenGL drew everything in
			// the update method) and the front buffer (the chunk of memory where OpenGL will
			// draw everything in the next update method).
			glfwSwapBuffers(window);
			
			// Calculating the time and time-step for the game loop...
			numFrames++;
			afterTime = glfwGetTime();
			delta = afterTime - startTime;
			startTime = afterTime;
			frameRate = 1.0 / delta;
		}
		//---------------------Game loop ends------------------------\\
		dispose();
		
		//Dispose of queries
		if(queries != null) for(int query : queries) GL15.glDeleteQueries(query);
		
		//Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		
		//Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
	
	protected void stopGame() { glfwSetWindowShouldClose(window, true); }
	
	/***/
	public static void mapToKey(GameAction action, int key) { KeyboardHandler.mapToKey(action, key); }
	
	/** Maps the appropriate mouse button with the appropriate action.
	 * 
	 * @param action The {@link GameAction} instance to which this button
	 * needs to be mapped.
	 * @param button The integer representation of the button being referenced.
	 * The valid options are : <ol><li>{@link #MB_LEFT}</li>
	 * <li>{@link #MB_RIGHT}</li>
	 * <li>{@link #MB_MIDDLE}.</li><ol> */
	private static void mapToMouseButton(GameAction action, int button) { MouseManager.mapToMouse(action, getMouseAction(button)); }
	
	/** Maps the relevant mouse direction with the appropriate action.
	 * 
	 * @param action The {@link GameAction} instance to which this button
	 * needs to be mapped.
	 * @param direction The integer representation of the button being referenced.
	 * The valid options are : <ol><li>{@link #MM_LEFT}</li>
	 * <li>{@link #MM_RIGHT}</li><li>{@link #MM_UP}</li>
	 * <li>{@link #MM_DOWN}.</li></ol> */
	private static void mapToMouseDirection(GameAction action, int direction) { MousePosition.mapToMouse(action, getMouseAction(direction)); }
	
	/** Maps the relevant mouse scrolling to the appropriate action.
	 * 
	 * @param action The {@link GameAction} instance to which this button
	 * needs to be mapped.
	 * @param rotation The integer representation of the direction of the
	 * wheel turn being referenced. The valid options are :
	 * <ol><li>{@link #MW_UP}</li><li>{@link #MW_DOWN}.</li></ol> */
	private static void mapToMouseWheel(GameAction action, int rotation) { MouseWheel.mapToMouse(action, getMouseAction(rotation)); }
	
	/***/
	public static void mapToMouse(GameAction action, int code) {
		if(code >= 0 && code <= 2) mapToMouseButton(action, code);
		else if(code >= 3 && code <= 6) mapToMouseDirection(action, code);
		else if(code == 7 || code == 8) mapToMouseWheel(action, code);
		else System.out.println("Invalid mouse parameter passed.");
	}
	
	private static int getMouseAction(int code) {
		switch(code) {
			case MM_LEFT : return MousePosition.MM_LEFT;
			case MM_RIGHT : return MousePosition.MM_RIGHT;
			case MM_UP : return MousePosition.MM_UP;
			case MM_DOWN : return MousePosition.MM_DOWN;
			case MW_UP : return MouseWheel.MW_UP;
			case MW_DOWN : return MouseWheel.MW_DOWN;
			case MB_LEFT : return MouseManager.MB_LEFT;
			case MB_MIDDLE : return MouseManager.MB_MIDDLE;
			case MB_RIGHT : return MouseManager.MB_RIGHT;
			default : return -1;
		}
	}
	
	public static double getMousePositionX() { return MousePosition.x; }
	
	public static double getMousePositionY() { return MousePosition.y; }
	
	public static Vector2f getMousePosition() { return new Vector2f(MousePosition.x, MousePosition.y); }
	
	public int generateQuery() {
		if(queries == null) queries = new ArrayList<>();
		int query = GL15.glGenQueries();
		queries.add(query);
		return query;
	}
	
	/** Sets the title of the application.
	 * It needs to be called before a call to the
	 * {@link #init()} method. */
	public static void setTitle(String title) { Core.title = title; }
	
	public static String getTitle() { return title; }
	
	public static void setFullscreen(boolean fullscreen) { Core.fullscreen = fullscreen; }
	
	public static boolean isFullscreen() { return fullscreen; }
	
	public static void setProjectionMatrixValues(float near, float far, float fov) {
		Core.near = near;
		Core.far = far;
		Core.fov = fov;
	}
	
	public static float getNearValue() { return near; }
	
	public static float getFarValue() { return far; }
	
	public static float getFieldOfViewValue() { return fov; }
	
	public static void setWidth(int width) { Core.width = width; aspectRatio = width / height; }
	
	public static void setHeight(int height) { Core.height = height; aspectRatio = width / height; }
	
	public static float getAspectRatio() { return aspectRatio; }
	
	public static int getWidth() {  return width; }
	
	public static int getHeight() { return height; }
	
	public static float getRed() { return red; }
	
	public static void setRed(float red) { Core.red = red; glClearColor(red, green, blue, alpha); }
	
	public static float getGreen() { return green; }
	
	public static void setGreen(float green) { Core.green = green; glClearColor(red, green, blue, alpha); }
	
	public static float getBlue() { return blue; }
	
	public static void setBlue(float blue) { Core.blue = blue; glClearColor(red, green, blue, alpha); }
	
	/** Sets the clear color. This call **HAS** to come after the
	 * {@link #init()} method call.
	 * 
	 * @param red The red component of the clear color.
	 * @param green The green component of the clear color.
	 * @param blue The blue component of the clear color.
	 * @param alpha The alpha component of the clear color. */
	public static void setColor(float red, float green, float blue, float alpha) {
		Core.red = red;
		Core.green = green;
		Core.blue = blue;
		Core.alpha = alpha;
		glClearColor(red, green, blue, alpha);
	}
	
	public static float getAlpha() { return alpha; }
	
	public static void setAlpha(float alpha) { Core.alpha = alpha; glClearColor(red, green, blue, alpha); }
	
	public static double getElapsedTimeInSeconds() { return glfwGetTime(); }
	
	public static double getElapsedTimeInMilliseconds() { return (glfwGetTime() * 1000); }
	
	public static double getDeltaTimeInMilliseconds() { return delta * 1000; }
	
	public static double getDeltaTimeInSeconds() { return delta; }
	
	public static double getInstantaneousFrameRate() { return frameRate; }
	
	public static double getAverageFrameRate() { return numFrames / GLFW.glfwGetTime(); }
	
	public static void setCellShading(boolean cell) { isCellShaded = cell; }
	
	public static boolean isCellShaded() { return isCellShaded; }
	
	public static void setCellShadingLevels(int levels) { cellShadingLevels = levels; }
	
	public static int getCellShadingLevels() { return cellShadingLevels; }
	
	public static void setCursorDisabled(boolean disabled) { glfwSetInputMode(window, GLFW_CURSOR, disabled ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL); }
	
	public static boolean isPaused() { return paused; }
	
	public static void setPaused(boolean paused) { Core.paused = paused; }
	
	protected abstract void update();
	
	protected abstract void dispose();
	
}