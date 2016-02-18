package ca.team5406.frc2016.subsystems;

import ca.team5406.frc2016.Constants;
import ca.team5406.util.PID;
import ca.team5406.util.Util;
import ca.team5406.util.sensors.RelativeEncoder;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class BatteringRamp {
	
	private VictorSP motor;
	private RelativeEncoder encoder;
	
	private PID pid;
	
	private Positions desiredPosition;
	private Positions currentPosition;
	
	public static enum Positions{
		NONE,
		DOWN,
		MID,
		SCALE,
		UP,
		MOVING,
		MANUAL
	}
	
	public BatteringRamp(){
		motor = new VictorSP(Constants.batteringRampMotor);
		encoder = new RelativeEncoder(Constants.batteringRampEncA, Constants.batteringRampEncB);
		desiredPosition = Positions.NONE;
		currentPosition = Positions.NONE;
		pid = new PID();
		pid.setConstants(Constants.rampPos_kP, Constants.rampPos_kI, Constants.rampPos_kD);
	}
	
	public void setDesiredPos(Positions pos){
		if(currentPosition != desiredPosition){
			currentPosition = Positions.MOVING;
			desiredPosition = pos;
			switch(desiredPosition){
				case DOWN:
					pid.setDesiredPosition(Constants.rampDownPosition);
					break;
				case MID:
					pid.setDesiredPosition(Constants.rampMidPosition);
					break;
				case UP:
					pid.setDesiredPosition(Constants.rampUpPosition);
					break;
				case SCALE:
					pid.setDesiredPosition(Constants.rampScalePosition);
					break;
				default:
					break;
			}
		}
	}

	public Positions getDesiredPos(){
		return desiredPosition;
	}
	public Positions getCurrentPos(){
		return currentPosition;
	}
	
	public int getEncoder(){
		return encoder.get();
	}
	
	public void joystickControl(double value){
		if(desiredPosition == Positions.MANUAL){
			value = Util.applyDeadband(value, Constants.xboxControllerDeadband);
	//		if(encoder.get() > Constants.rampUpPosition && encoder.get() < Constants.rampDownPosition){
			set(value);
		}
	}
	
	public void runControlLoop(){
		switch(desiredPosition){
			case DOWN:
			case UP:
				if(Math.abs(pid.getError(getEncoder())) < Constants.rampPos_deadband){
					set(0);
					currentPosition = desiredPosition;
					desiredPosition = Positions.NONE;
					break;
				}
			case MID:
			case SCALE:
				double speed = pid.calcSpeed(getEncoder());
				set(speed);
				break;
			case NONE:
				set(0);
				break;
			case MANUAL:
			case MOVING:
				break;
		}
		if(pid.isDone(getEncoder(), Constants.rampPos_deadband)){
			currentPosition = desiredPosition;
			desiredPosition = Positions.NONE;
		}
	}
	
	private void set(double value){
		motor.set(value);
	}
	
	public void sendSmartdashInfo(){
		SmartDashboard.putNumber("Ramp Encoder", encoder.get());
		SmartDashboard.putNumber("Ramp Speed", encoder.getRate());
	}
	
}
