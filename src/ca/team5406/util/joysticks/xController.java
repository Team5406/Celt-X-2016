package ca.team5406.util.joysticks;

import edu.wpi.first.wpilibj.Joystick;

public class xController extends Joystick {

	private int numButtons = 10;
	private boolean[] previousButtonState = new boolean[numButtons+1];
	
	public xController(int port) {
		super(port);
	}
	
	public void updateButtons(){
		for(int i = 1; i <= numButtons; i++){
			previousButtonState[i] = getRawButton(i);
		}
	}
	
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
