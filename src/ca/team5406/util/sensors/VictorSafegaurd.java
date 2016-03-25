

package ca.team5406.util.sensors;

import edu.wpi.first.wpilibj.VictorSP;

public class VictorSafegaurd {
	
	private RelativeEncoder enc;
	private VictorSP motor;
	private double tolerance;
	
	private boolean cautionFlag;
	
	private int lastEncPosition;

	public VictorSafegaurd(RelativeEncoder enc, VictorSP motor, double tolerance){
		this.enc = enc;
		this.motor = motor;
		this.tolerance = tolerance;
		
		lastEncPosition = enc.get();
		cautionFlag = false;
	}
	
	public void test(){
		if((Math.abs(enc.get() - lastEncPosition) < tolerance) && Math.abs(motor.get()) > 0.1){
			cautionFlag = true;
		}
		else{
			cautionFlag = false;
		}
		lastEncPosition = enc.get();
	}
	
	public boolean getCaution(){
		return cautionFlag;
	}
	
	
	
}
