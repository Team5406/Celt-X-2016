package ca.team5406.frc2016.subsystems;

import ca.team5406.frc2016.Constants;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Intake extends Subsystem {

	private VictorSP intakeMotorA;
	private CANTalon intakeTalon;
	private DigitalInput ballSensor;
	
	private boolean isPracticeBot;
	
	private boolean forward;
	private boolean reverse;
	
	public static final String NAME = "Intake";

	public Intake(boolean isPracticeBot){
		super(NAME);
		
		this.isPracticeBot = isPracticeBot;
		if(isPracticeBot){
			intakeTalon = new CANTalon(Constants.intakeTalon);
			intakeTalon.changeControlMode(TalonControlMode.PercentVbus);
		    intakeTalon.configNominalOutputVoltage(+0.0f, -0.0f);
		    intakeTalon.configPeakOutputVoltage(+12.0f, -12.0f);
		}
		else{
			intakeMotorA = new VictorSP(Constants.intakeMotorA);
		}
		ballSensor = new DigitalInput(Constants.ballSensor);
		forward = false;
		reverse = false;

	}
	
	public void stopMotors(){
		forward = false;
		reverse = false;
		setIntakeSpeed(0.0);
	}
	
	public void setIntakeSpeed(double speed){
		if(isPracticeBot){
			intakeTalon.set(-speed);
		}
		else{
			intakeMotorA.set(-speed);
		}
	}
	
	public boolean hasBall(){
		return !ballSensor.get();
	}
	
	public void setIntakeButtons(boolean wantsForward, boolean wantsReverse){
		forward = wantsForward;
		reverse = wantsReverse;
	}
	
	private int getDesiredDirection(){
		return (forward ? 1 : 0) + (reverse ? -1: 0);
	}
	
	@Override
    public void runControlLoop() {
    	if(getDesiredDirection() == 1){
			setIntakeSpeed(1.0);
		}
		else if(getDesiredDirection() == -1){
			setIntakeSpeed(-1.0);
		}
		else{
			setIntakeSpeed(0.25);
		}
	}
	
	public void sendSmartdashInfo(){
		SmartDashboard.putBoolean("Has Ball", hasBall());
	}
	
}
