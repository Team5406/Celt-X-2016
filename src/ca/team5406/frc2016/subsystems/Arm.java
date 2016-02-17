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
	
	private enum ArmPositions{
		UP,
		DOWN,
		CARRY,
		INSIDE
	}
	
	public Arm(){
		leftMotor = new CANTalon(Constants.armMotorA);
		rightMotor = new CANTalon(Constants.armMotorB);
		leftMotor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);

		leftMotor.changeControlMode(TalonControlMode.PercentVbus);
		leftMotor.reverseOutput(true);
		leftMotor.reverseSensor(true);
		rightMotor.changeControlMode(TalonControlMode.Follower);
		rightMotor.set(leftMotor.getDeviceID());
	}
	
	public void joystickControl(double value){
		value = Util.applyDeadband(value, Constants.xboxControllerDeadband);
		set(value);
	}
	
	private void set(double speed){
		leftMotor.set(speed);
	}
	
	public void sendSmartdashInfo(){
		SmartDashboard.putNumber("Arm Encoder", leftMotor.getEncPosition());
		SmartDashboard.putNumber("Arm Speed", leftMotor.getEncVelocity());
	}

}
