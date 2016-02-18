package ca.team5406.frc2016.subsystems;

import ca.team5406.frc2016.Constants;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.VictorSP;

public class Intake {

	private VictorSP intakeMotorA;
	private VictorSP intakeMotorB;
	private DigitalInput ballSensor;

	public Intake(){
		intakeMotorA = new VictorSP(Constants.intakeMotorA);
		intakeMotorB = new VictorSP(Constants.intakeMotorB);
		ballSensor = new DigitalInput(Constants.ballSensor);
	}
	
	public void setIntake(double speed){
		intakeMotorA.set(speed);
		intakeMotorB.set(-speed);
	}
	
	public boolean hasBall(){
		return !ballSensor.get();
	}
	
	public void intakeUntilBall(){
		if(!hasBall()){
			setIntake(1.0);
		}
		else{
			setIntake(0.0);
		}
	}
	
	public void reverseIntake(){
		setIntake(1.0);
	}
	
	public void stopIntake(){
		setIntake(0.0);
	}
	
	public void runControlLoop(boolean inButtonHeld, boolean outButtonHeld){
		if(inButtonHeld && !hasBall()){
			setIntake(1.0);
		}
		else if(outButtonHeld){
			setIntake(-1.0);
		}
		else{
			setIntake(0.0);
		}
	}
	
}
