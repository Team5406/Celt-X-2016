package ca.team5406.frc2016.subsystems;

import ca.team5406.frc2016.Constants;
import ca.team5406.util.Util;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Arm {
	
	private CANTalon leftMotor;
	private CANTalon rightMotor;

	private Positions desiredPosition;
	private Positions currentPosition;
	
	public static enum Positions{
		NONE,
		UP,
		DOWN,
		CARRY,
		INSIDE,
		MOVING,
		MANUAL
	}
	
	public Arm(){
		leftMotor = new CANTalon(Constants.armMotorA);
		rightMotor = new CANTalon(Constants.armMotorB);
		leftMotor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		leftMotor.setInverted(true);
		leftMotor.changeControlMode(TalonControlMode.PercentVbus);
		rightMotor.changeControlMode(TalonControlMode.PercentVbus);
//		rightMotor.set(leftMotor.getDeviceID());
		
		leftMotor.setPID(Constants.armPos_kP, Constants.armPos_kI, Constants.armPos_kD);
		leftMotor.setAllowableClosedLoopErr(Constants.armPos_deadband);

		desiredPosition = Positions.NONE;
		currentPosition = Positions.NONE;
	}
	
	public void setDesiredPos(Positions pos){
		if(currentPosition != desiredPosition){
			currentPosition = Positions.MOVING;
			desiredPosition = pos;
			switch(desiredPosition){
				case DOWN:
					leftMotor.set(Constants.armDownPos);
					break;
				case CARRY:
					leftMotor.set(Constants.armCarryPos);
					break;
				case INSIDE:
					leftMotor.set(Constants.armInsidePos);
					break;
				case UP:
					leftMotor.set(Constants.armUpPos);
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
	
	public void joystickControl(double value){
		if(desiredPosition == Positions.MANUAL){
			value = Util.applyDeadband(value, Constants.xboxControllerDeadband);
			set(value);
		}
	}
	
//	public void runControlLoop(){
//		switch(desiredPosition){
//			case DOWN:
//			case UP:
//				if(Math.abs(leftMotor.getClosedLoopError()) < Constants.rampPos_deadband){
//					set(0);
//					currentPosition = desiredPosition;
//					desiredPosition = Positions.NONE;
//					break;
//				}
//			case CARRY:
//			case INSIDE:
//				double speed = pid.calcSpeed(getEncoder());
//				set(speed);
//				break;
//			case NONE:
//				set(0);
//				break;
//			case MANUAL:
//			case MOVING:
//				break;
//		}
//		if(pid.isDone(getEncoder(), Constants.rampPos_deadband)){
//			currentPosition = desiredPosition;
//			desiredPosition = Positions.NONE;
//		}
//	}
	
	private void set(double speed){
		leftMotor.set(speed);
		rightMotor.set(speed);
	}
	
	public void sendSmartdashInfo(){
		SmartDashboard.putNumber("Arm Encoder", leftMotor.get());
		SmartDashboard.putNumber("Target", leftMotor.getSetpoint());
		SmartDashboard.putBoolean("On Target", (leftMotor.getError() < Constants.armPos_deadband));
	}

}
