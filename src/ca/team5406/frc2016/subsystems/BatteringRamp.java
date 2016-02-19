package ca.team5406.frc2016.subsystems;

import java.util.TimerTask;

import ca.team5406.frc2016.Constants;
import ca.team5406.util.PID;
import ca.team5406.util.Util;
import ca.team5406.util.sensors.RelativeEncoder;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class BatteringRamp {
	
	private VictorSP motor;
	private RelativeEncoder encoder;
	
	private PID positionPid;
	private PID posHoldPid;
	
	private Positions desiredPosition;
	private Positions currentPosition;

	private java.util.Timer timer;
	
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
		
		positionPid = new PID();
		positionPid.setConstants(Constants.rampPos_kP, Constants.rampPos_kI, Constants.rampPos_kD);
		posHoldPid = new PID();
		posHoldPid.setConstants(Constants.rampHold_kP, Constants.rampHold_kI, Constants.rampHold_kD);

		desiredPosition = Positions.NONE;
		currentPosition = Positions.NONE;
		
		timer = new java.util.Timer("Ramp Scheduler");
	    timer.scheduleAtFixedRate(run, 10, 10);
	}
	
	public void setDesiredPos(Positions pos){
		if(pos == Positions.MANUAL){
			currentPosition = Positions.MANUAL;
			desiredPosition = Positions.MANUAL;
			return;
		}
		if(currentPosition != pos){
			currentPosition = Positions.MOVING;
			desiredPosition = pos;
			switch(desiredPosition){
				case DOWN:
					positionPid.setDesiredPosition(Constants.rampDownPosition);
					break;
				case MID:
					positionPid.setDesiredPosition(Constants.rampMidPosition);
					break;
				case UP:
					positionPid.setDesiredPosition(Constants.rampUpPosition);
					break;
				case SCALE:
					positionPid.setDesiredPosition(Constants.rampScalePosition);
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
			if((value > 0 && encoder.get() < Constants.rampUpPosition) || (value < 0 && encoder.get() > Constants.rampDownPosition)){
				set(value);
			}
		}
	}
	
	private TimerTask run = new TimerTask() {
        @Override
        public void run() {
			if(currentPosition == Positions.MOVING){
				switch(desiredPosition){
					case DOWN:
					case UP:
						if(Math.abs(positionPid.getError(getEncoder())) < Constants.rampPos_deadband){
							set(0);
							currentPosition = desiredPosition;
							desiredPosition = Positions.NONE;
							break;
						}
					case MID:
					case SCALE:
						double speed = positionPid.calcSpeed(getEncoder());
						SmartDashboard.putNumber("Calced Speed", speed);
						set(speed);
						break;
					case NONE:
					case MANUAL:
					case MOVING:
						break;
				}
				if(positionPid.isDone(getEncoder(), Constants.rampPos_deadband)){
					currentPosition = desiredPosition;
					posHoldPid.setDesiredPosition(getEncoder());
				}
			}
			else{
				if(!posHoldPid.isDone(getEncoder(), Constants.rampPos_deadband)){
					motor.set(posHoldPid.calcSpeed(getEncoder()));
				}
			}
		}
	};
	
	private void set(double value){
		motor.set(value);
	}
	
	public void sendSmartdashInfo(){
		SmartDashboard.putBoolean("Ramp On Target", positionPid.isDone(getEncoder(), Constants.rampPos_deadband));
		SmartDashboard.putString("Ramp Pos", currentPosition.name());
	}
	
}
