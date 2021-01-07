package in.mayank.extra.input;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

public class KeyboardHandler extends GLFWKeyCallback {
	
	private static final int LENGTH = 65536;
	private static Map<Integer, GameAction> keys = new HashMap<>();
	
	public void invoke(long window, int key, int scancode, int action, int mods) {
		if(key < LENGTH) {
			GameAction game = keys.get(key);
			if(game != null)
				if(action != GLFW.GLFW_RELEASE) game.press();
				else game.release();
		}
	}
	
	public static void mapToKey(GameAction action, int key) { if(key >= 0 && key < LENGTH) keys.put(key, action); }
	
}
