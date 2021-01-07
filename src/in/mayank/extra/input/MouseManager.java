package in.mayank.extra.input;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMouseButtonCallback;


public class MouseManager extends GLFWMouseButtonCallback {
	
	private static final int LENGTH = 5;
	private static GameAction[] buttons = new GameAction[LENGTH];
	//buttons
	public static final int MB_LEFT = 0, MB_RIGHT = 1, MB_MIDDLE = 2;
	
	public void invoke(long window, int button, int action, int mods) {
		if(button < LENGTH) {
			GameAction game = buttons[button];
			if(game != null) {
				if(action == GLFW.GLFW_RELEASE) game.release();
				else game.press();
			}
		}
		/*System.out.println("Button : "+button);
		System.out.println("Action : "+action);
		System.out.println("Mods : "+mods);*/
	}
	
	/** Maps the appropriate mouse button with the appropriate action.
	 * 
	 * @param action The {@link GameAction} instance to which this button
	 * needs to be mapped.
	 * @param button The integer representation of the button being referenced.
	 * The valid options are : {@link #MB_LEFT}, {@link #MB_RIGHT} and {@link #MB_MIDDLE}. */
	public static void mapToMouse(GameAction action, int button) {
		if(button >= 0 && button < LENGTH)
			buttons[button] = action;
	}
	
}
