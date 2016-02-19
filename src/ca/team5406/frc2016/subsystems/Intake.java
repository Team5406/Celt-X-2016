package ca.team5406.frc2016.subsystems;

import java.util.TimerTask;

import ca.team5406.frc2016.Constants;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Intake {

	private VictorSP intakeMotorA;
	private VictorSP intakeMotorB;
	private DigitalInput ballSensor;
	
	private boolean forward;
	private boolean reverse;
	
	private java.util.Timer timer;

	public Intake(){
		intakeMotorA = new VictorSP(Constants.intakeMotorA);
		intakeMotorB = new VictorSP(Constants.intakeMotorB);
		ballSensor = new DigitalInput(Constants.ballSensor);
		forward = false;
		reverse = false;
		timer = new java.util.Timer("Intake Scheduler");
	    timer.scheduleAtFixedRate(run, 10, 10);
	}
	
	public void setIntakeSpeed(double speed){
		intakeMotorA.set(-speed);
		intakeMotorB.set(speed);
	}
	
	public boolean hasBall(){
		return !ballSensor.get();
	}
	
	public void setIntakeButtons(boolean forward, boolean reverse){
		forward = this.forward;
		reverse = this.reverse;
	}
	
	private TimerTask run = new TimerTask() {
        @Override
        public void run() {
		if(forward && !hasBall()){
				setIntakeSpeed(1.0);
			}
			else if(reverse){
				setIntakeSpeed(-1.0);
			}
			else{
				setIntakeSpeed(0.0);
			}
		}
	};
	
	public void sendSmartdashInfo(){
		SmartDashboard.putBoolean("Has Ball", hasBall());
	}
	
}
