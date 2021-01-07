package in.mayank.extra.input;

import org.lwjgl.glfw.GLFWScrollCallback;

public class MouseWheel extends GLFWScrollCallback {
	
	public static final int MW_DOWN = 0, MW_UP = 1;
	private static GameAction[] scroller = new GameAction[2];
	
	public void invoke(long window, double xoffset, double yoffset) {
		mouseHelper(MW_DOWN, MW_UP, (int)yoffset);
	}
	
	public static void mapToMouse(GameAction action, int direction) {
		scroller[direction] = action;
	}
	
	private static void mouseHelper(int codeNeg, int codePos, int amount) {
		GameAction action = scroller[amount < 0 ? codeNeg : (amount > 0 ? codePos : null)];
		
		if(action != null) {
			action.press();
			action.release();
		}
	}
	
}
