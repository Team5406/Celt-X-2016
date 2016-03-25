package ca.team5406.util.sensors;

import edu.wpi.first.wpilibj.CANTalon;

public class TalonSafegaurd {
	
	private CANTalon talon;
	private double tolerance;
	
	private boolean cautionFlag;
	
	private double lastEncPosition;

	public TalonSafegaurd(CANTalon talon, double tolerance){
		this.talon = talon;
		this.tolerance = tolerance;
		
		cautionFlag = false;
	}
	
	public void test(){
		if((Math.abs(talon.getEncPosition() - lastEncPosition) < tolerance) && Math.abs(talon.getOutputVoltage()) > 4){
			cautionFlag = true;
		}
		else{
			cautionFlag = false;
		}
		lastEncPosition = talon.getEncPosition();
	}
	
	public boolean getCaution(){
		return cautionFlag;
	}
	
	
	
}