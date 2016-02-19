package ca.team5406.frc2016.subsystems;

import java.util.TimerTask;

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

	private java.util.Timer timer;
	
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
		leftMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		leftMotor.changeControlMode(TalonControlMode.Position);
		leftMotor.setPosition(0);
		leftMotor.reverseOutput(true);
		leftMotor.setPID(Constants.armPos_kP, Constants.armPos_kI, Constants.armPos_kD);

		rightMotor = new CANTalon(Constants.armMotorB);
		rightMotor.reverseOutput(true);
		rightMotor.changeControlMode(TalonControlMode.Follower);
		rightMotor.set(leftMotor.getDeviceID());

		desiredPosition = Positions.NONE;
		currentPosition = Positions.NONE;
		
		timer = new java.util.Timer("Arm Scheduler");
	    timer.scheduleAtFixedRate(run, 10, 10);
	}
	
	public void setDesiredPos(Positions pos){
		if(pos == Positions.MANUAL){
			leftMotor.changeControlMode(TalonControlMode.PercentVbus);
			currentPosition = Positions.MANUAL;
			desiredPosition = Positions.MANUAL;
			return;
		}
		if(currentPosition != pos){
			leftMotor.changeControlMode(TalonControlMode.Position);
			currentPosition = Positions.MOVING;
			desiredPosition = pos;
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
			if((value > 0 && (leftMotor.getEncPosition() < (Constants.armUpPos - Constants.armPos_deadband))) || (value < 0 && (leftMotor.getEncPosition() > (Constants.armDownPos + Constants.armPos_deadband)))){
				set(value);
			}
		}
	}
	
	private TimerTask run = new TimerTask() {
        @Override
        public void run() {
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
	};
	
	private void set(double speed){
		leftMotor.set(-speed);
	}
	
	public void sendSmartdashInfo(){
		SmartDashboard.putString("Arm Position", desiredPosition.name());
		SmartDashboard.putBoolean("Arm On Target", (leftMotor.getError() < Constants.armPos_deadband));
	}

}
