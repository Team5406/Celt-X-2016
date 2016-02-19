package ca.team5406.frc2016.subsystems;

import ca.team5406.frc2016.Constants;
import ca.team5406.util.Util;
import ca.team5406.util.sensors.RioAccelerometer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Drivetrain {

	private CANTalon leftSRX;
	private CANTalon leftFollowerSRX;
	
	private CANTalon rightSRX;
	private CANTalon rightFollowerSRX;

	private DoubleSolenoid shiftSolenoid;
	
	private RioAccelerometer rioAccel;
	
	public Drivetrain(){
    	
    	leftSRX = createDriveTalon(Constants.leftDriveMotorA);
    	leftFollowerSRX = new CANTalon(Constants.leftDriveMotorB);
    	rightSRX = createDriveTalon(Constants.rightDriveMotorA);
    	rightFollowerSRX = new CANTalon(Constants.rightDriveMotorB);
    	leftSRX.setInverted(true);
    	leftFollowerSRX.setInverted(true);

    	leftSRX.changeControlMode(TalonControlMode.PercentVbus);
    	rightSRX.changeControlMode(TalonControlMode.PercentVbus);
//    	initSpeedControl();
    	
    	leftFollowerSRX.changeControlMode(TalonControlMode.Follower);
    	leftFollowerSRX.set(leftSRX.getDeviceID());
    	rightFollowerSRX.changeControlMode(TalonControlMode.Follower);
    	rightFollowerSRX.set(rightSRX.getDeviceID());
    	
//		leftSRX.setSafetyEnabled(true);
//		leftFollowerSRX.setSafetyEnabled(true);
//		rightSRX.setSafetyEnabled(true);
//		rightFollowerSRX.setSafetyEnabled(true);
   
    	shiftSolenoid = new DoubleSolenoid(Constants.shiftUpSolenoid, Constants.shiftDownSolenoid);
    	
    	rioAccel = new RioAccelerometer();
		
	}
	
	private CANTalon createDriveTalon(int id){
		CANTalon talon = new CANTalon(id);
		talon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
	    talon.reverseSensor(false);
	    talon.configNominalOutputVoltage(+0.0f, -0.0f);
	    talon.configPeakOutputVoltage(+12.0f, -12.0f);
	    talon.setAllowableClosedLoopErr(0);
	    return talon;
	}
	
	private void initSpeedControl(){
		leftSRX.changeControlMode(TalonControlMode.Speed);
		leftSRX.setPID(Constants.lowGearSpeed_kP, Constants.lowGearSpeed_kI, Constants.lowGearSpeed_kD, Constants.lowGearSpeed_kF, 0, 20, 0);
		rightSRX.changeControlMode(TalonControlMode.Speed);
		rightSRX.setPID(Constants.lowGearSpeed_kP, Constants.lowGearSpeed_kI, Constants.lowGearSpeed_kD, Constants.lowGearSpeed_kF, 0, 20, 0);
	}
	
	private boolean driveToFirstRun = true;
	private int successCounts = 0;
	public boolean driveToDistance(double distance){
		if(driveToFirstRun){
			distance = distance / Constants.distPerRev;
			leftSRX.set(distance);
			rightSRX.set(distance);
			driveToFirstRun = false;
		}

		if(Math.abs(leftSRX.get() - leftSRX.getSetpoint()) < Constants.lowGearDriveToDeadband){
			if(successCounts < 5){
				successCounts++;
				return false;
			}
			else{
				return true;
			}
		}
		else{
			successCounts = 0;
		}
		return false;
	}
	public void resetDriveTo(){
		driveToFirstRun = true;
	}
	
	public void arcadeDrive(double yAxis, double xAxis){
		yAxis = Util.applyDeadband(yAxis, Constants.xboxControllerDeadband);
		xAxis = Util.applyDeadband(xAxis, Constants.xboxControllerDeadband) * (getShiftState() ? Math.abs(yAxis) : 1);

		setLeftRightPower(yAxis + xAxis, yAxis - xAxis);
//		setLeftRightSpeed(yAxis + xAxis, yAxis - xAxis);
	}
	
	public boolean getShiftState(){
		return (shiftSolenoid.get() == DoubleSolenoid.Value.kForward);
	}
	
	public void shiftUp(){
		shiftSolenoid.set(DoubleSolenoid.Value.kForward);
	}
	public void shiftDown(){
		shiftSolenoid.set(DoubleSolenoid.Value.kReverse);
	}
	
	private void setLeftRightSpeed(double leftSpeed, double rightSpeed){
		leftSRX.set(leftSpeed * Constants.maxSpeed);
		rightSRX.set(rightSpeed * Constants.maxSpeed);
	}
	
	private void setLeftRightPower(double leftPower, double rightPower){
		leftSRX.set(leftPower);
		rightSRX.set(rightPower);
	}
	
	public void sendSmartdashInfo(){
		SmartDashboard.putNumber("Left Speed", leftSRX.getEncPosition());
		SmartDashboard.putNumber("Left Pos", leftSRX.getEncPosition());
		SmartDashboard.putNumber("Right Speed", rightSRX.getEncPosition());
		SmartDashboard.putNumber("Right Pos", rightSRX.getEncPosition());
		SmartDashboard.putNumber("Y Dist", rioAccel.getDistY());
	}
	
}
