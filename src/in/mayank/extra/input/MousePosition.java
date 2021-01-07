package in.mayank.extra.input;

import org.lwjgl.glfw.GLFWCursorPosCallback;

import in.mayank.extra.core.Core;

public class MousePosition extends GLFWCursorPosCallback {
	
	private static int centerX = Core.getWidth()/2, centerY = Core.getHeight()/2;
	public static float x = centerX, y = centerY;
	
	private static final int LENGTH = 4;
	private static GameAction[] action = new GameAction[LENGTH];
	private static boolean IS_MOVING = false;
	
	//movement
	public static final int MM_LEFT = 0, MM_RIGHT = 1, MM_UP = 2, MM_DOWN = 3;
	
	public void invoke(long window, double x, double y) {
		mouseHelper(MM_LEFT, MM_RIGHT, x - MousePosition.x);
		mouseHelper(MM_DOWN, MM_UP, y - MousePosition.y);
		
		IS_MOVING = (MousePosition.x != x || MousePosition.y != y);
		
		MousePosition.x = (float)x;
		MousePosition.y = (float)y;
	}
	
	public static boolean isMoving() {
		return IS_MOVING;
	}
	
	private void mouseHelper(int codeNeg, int codePos, double amount) {
		GameAction action = MousePosition.action[amount < 0 ? codeNeg : codePos];
		
		if(amount != 0 && action != null) {
			action.press(amount > 0 ? amount : -amount);
			action.release();
		}
	}
	
	public static void mapToMouse(GameAction action, int direction) {
		MousePosition.action[direction] = action;
	}
	
}
