package ca.team5406.util.joysticks;

import java.util.TimerTask;

import edu.wpi.first.wpilibj.Joystick;

public class ControllerBase extends Joystick {

	int numButtons;
	boolean[] previousButtonState;
	
	private java.util.Timer timer;
	
	public ControllerBase(int port) {
		super(port);
		numButtons = super.getButtonCount();
		previousButtonState = new boolean[numButtons + 1];
		
		timer = new java.util.Timer("Controller " + port);
	    timer.scheduleAtFixedRate(updateButtons, 10, 10);//this line starts the timer 
	}
	
	private TimerTask updateButtons = new TimerTask() {
        @Override
        public void run() {
			for(int i = 1; i <= numButtons; i++){
				previousButtonState[i] = getRawButton(i);
			}
		}
	};
	
	public boolean getButtonHeld(int button){
		return super.getRawButton(button);
	}
	
	public boolean getButtonOnce(int button){
		return (super.getRawButton(button) && !previousButtonState[button]);
	}
	
	public boolean getButtonRelease(int button){
		return (!super.getRawButton(button) && previousButtonState[button]);
	}
	
}
