package ca.team5406.util.joysticks;

public class AttackStick extends xController {
	
	public static final int TRIGGER = 1;

	public AttackStick(int port) {
		super(port);
	}
	
	public double getMainX(){
		return super.getRawAxis(0);
	}
	
	public double getMainY(){
		return super.getRawAxis(1);
	}
	
	public double getTwist(){
		return super.getRawAxis(2);
	}
	
	public double getThrottle(){
		return -super.getRawAxis(3);
	}
	
	public int getMiniStick(){
		return super.getPOV();
	}

}
