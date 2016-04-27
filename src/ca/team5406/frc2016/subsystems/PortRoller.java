package ca.team5406.frc2016.subsystems;

import ca.team5406.frc2016.Constants;
import edu.wpi.first.wpilibj.VictorSP;

public class PortRoller extends Subsystem {

	private VictorSP portRollerMotor;

	
	private double setpoint;
	
	public static final String NAME = "PortRoller";

	public PortRoller(){
		super(NAME);
		
		portRollerMotor = new VictorSP(Constants.portRollerMotor);
		setpoint = 0.0;

	}
	
	public void stopMotors(){
		setIntakeSpeed(0.0);
	}

	public void setIntakeSpeed(double speed){
		setpoint = speed;
	}
	
	private void set(double speed){
		portRollerMotor.set(speed);
	}
	
	@Override
    public void runControlLoop() {
    	set(setpoint);
	}

	@Override
	public void sendSmartdashInfo() {}
	
}
