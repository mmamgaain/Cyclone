package in.mayank.extra.input;

/** The GameAction class is an abstract to a user-initiated
 * action, like jumping or moving. GameActions can be mapped
 * to keys or the mouse with the InputManager. */
public class GameAction {
	
    /** Normal behavior. The {@link #isPressed()} method returns true and the
     * {@link #getAmount()} method keeps incrementing the amount as long as the
     * key is held down. */
    public static final int BEHAVIOUR_NORMAL = 0;
    
    /** Initial press behavior. The {@link #isPressed()} method returns
     * true only after the key is first pressed, and not again
     * until the key is released and pressed again. */
    public static final int BEHAVIOUR_DETECT_INITIAL_PRESS_ONLY = 1;
    private static final int STATE_RELEASED = 0, STATE_PRESSED = 1, STATE_WAITING_FOR_RELEASE = 2;
    private String name;
    private static final String DEFAULT_NAME = "No name set";
    private int behaviour, amount, state;
    
    /***/
    public GameAction() { this(DEFAULT_NAME); }
    
    /***/
    public GameAction(final int behaviour) { this(DEFAULT_NAME, behaviour); }
    
    /** Create a new GameAction with the "normal" behavior. The default
     * behaviour of this {@link GameAction} is {@link #BEHAVIOUR_NORMAL}.
     * 
     * @param name The name designation of this {@link GameAction}. */
    public GameAction(final String name) { this(name, BEHAVIOUR_NORMAL); }
    
    /** Create a new GameAction with the specified behavior.
     * 
     * @param name The name designation of this {@link GameAction}.
     * @param behaviour The nature of this {@link GameAction}. It can
     * have {@link #BEHAVIOUR_NORMAL} and {@link #BEHAVIOUR_DETECT_INITIAL_PRESS_ONLY} static
     * final values as possible values. */
    public GameAction(final String name, final int behaviour) {
        this.name = name;
        this.behaviour = behaviour;
        reset();
    }
    
    /** Gets the name of this GameAction. */
    public String getName() { return name; }
    
    /** Resets this GameAction so that it appears like it hasn't
     * been pressed. */
    public void reset() { state = STATE_RELEASED; amount = 0; }
    
    /** Taps this GameAction. Same as calling {@link #press()} followed
     * by {@link #release()}. */
    public synchronized void tap() { press(); release(); }
    
    /** Signals that the key was pressed once. */
    public synchronized void press() { press(1); }
    
    /** Signals that the key was pressed a specified number of
     * times, or that the mouse move a specified distance.
     * 
     * @param amount The integer value representing the amount of
     * times this button will be pressed. */
    public synchronized void press(final double amount) {
        if(state != STATE_WAITING_FOR_RELEASE) {
            this.amount += amount;
            state = STATE_PRESSED;
        }
    }
    
    /** Signals that the key was released */
    public synchronized void release() {
        state = STATE_RELEASED;
    }
    
    /** Returns whether the key was pressed or not since last checked.
     * 
     * <b>NOTE : </b><i>The  amount is reset each time this method is called.
     * So, once this method is called and before the subsequent call no activity
     * happens on this GameAction, this method will return a false value.</i>
     * 
     * @return Whether this button has been pressed or not. It will be
     * counted from the last call of the {@link #isPressed()} or
     * {@link #getAmount()} method. */
    public synchronized boolean isPressed() { return getAmount() != 0; }
    
    /** For keys, this is the number of times the key was
     * pressed since it was last checked. For mouse movement,
     * this is the distance moved.<br><br>
     * 
     * <b><u>NOTE</u> : </b><i>The  amount is reset each time this method is called.
     * So, once this method is called and before the subsequent call no activity
     * happens on this GameAction, this method will return a zero value.</i>
     * 
     * @return The integer value of the amount for how many times
     * this button was pressed. It will be calculated from the last
     * call of the {@link #isPressed()} or {@link #getAmount()} method. */
    public synchronized float getAmount() {
        float retVal = amount;
        if(retVal != 0) {
        	if(state == STATE_RELEASED) amount = 0;
        	else if(behaviour == BEHAVIOUR_DETECT_INITIAL_PRESS_ONLY) { state = STATE_WAITING_FOR_RELEASE; amount = 0; }
        }
        return retVal;
    }
    
    /** Discards up any press registered up-till this point as if this key
     * was never pressed. */
    public void consume() { amount = 0; }
    
}
