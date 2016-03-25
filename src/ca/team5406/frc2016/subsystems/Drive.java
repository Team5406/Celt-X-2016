package ca.team5406.frc2016.subsystems;

import ca.team5406.frc2016.Constants;
import ca.team5406.util.PID;
import ca.team5406.util.Util;
import ca.team5406.util.sensors.RelativeEncoder;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.SPI;
import com.kauailabs.navx.frc.AHRS;

public class Drive extends Subsystem {

	private CANTalon leftSRX;
	private CANTalon leftFollowerSRX;
	
	private CANTalon rightSRX;
	private CANTalon rightFollowerSRX;
	
//	private RelativeEncoder leftEnc;
//	private RelativeEncoder rightEnc;

	private DoubleSolenoid shiftSolenoid;
	private PID turnToAnglePid;
	private PID driveToPid;
	
	private AHRS navX;
	
	public static enum ControlMode{
		POWER,
		SPEED,
		DISTANCE,
		ANGLE,
	}
	
	public Drive(){
    	super("Drivetrain");

    	navX = new AHRS(SPI.Port.kMXP);
    	navX.reset();
    	navX.resetDisplacement();
		
    	leftSRX = Util.createDriveTalon(Constants.leftDriveMotorA);
    	leftFollowerSRX = new CANTalon(Constants.leftDriveMotorB);
    	leftSRX.reverseSensor(true);
    	rightSRX = Util.createDriveTalon(Constants.rightDriveMotorA);
    	rightFollowerSRX = new CANTalon(Constants.rightDriveMotorB);
    	leftSRX.setInverted(true);
    	leftFollowerSRX.setInverted(true);
    	
    	turnToAnglePid = new PID();
    	turnToAnglePid.setConstants(Constants.lowGearTurnTo_kP, Constants.lowGearTurnTo_kI, Constants.lowGearTurnTo_kD);
    	driveToPid = new PID();
    	
    	leftFollowerSRX.changeControlMode(TalonControlMode.Follower);
    	leftFollowerSRX.set(leftSRX.getDeviceID());
    	rightFollowerSRX.changeControlMode(TalonControlMode.Follower);
    	rightFollowerSRX.set(rightSRX.getDeviceID());
    	
    	setControlMode(ControlMode.POWER);
   
    	shiftSolenoid = new DoubleSolenoid(Constants.shiftUpSolenoid, Constants.shiftDownSolenoid);
	}
	
	public void setControlMode(ControlMode mode){
		switch(mode){
		case ANGLE:
		case POWER:
			leftSRX.changeControlMode(TalonControlMode.PercentVbus);
			rightSRX.changeControlMode(TalonControlMode.PercentVbus);
			break;
		case SPEED:
			leftSRX.setPID(Constants.lowGearSpeed_kP, Constants.lowGearSpeed_kI, Constants.lowGearSpeed_kD, Constants.lowGearSpeed_kF, 0, 20, 0);
			rightSRX.setPID(Constants.lowGearSpeed_kP, Constants.lowGearSpeed_kI, Constants.lowGearSpeed_kD, Constants.lowGearSpeed_kF, 0, 20, 0);
			leftSRX.changeControlMode(TalonControlMode.Speed);
			rightSRX.changeControlMode(TalonControlMode.Speed);
		case DISTANCE:
			leftSRX.changeControlMode(TalonControlMode.Position);
			rightSRX.changeControlMode(TalonControlMode.Position);
			break;
		}
	}
	
	private boolean driveToFirstRun = true;
	private double initAngle = 0.0;
	public boolean driveToDistance(double distance, boolean straight, double speedLimit){
		if(driveToFirstRun){
			driveToPid.setDesiredPosition(distance);
			driveToPid.setConstants(Constants.lowGearDriveTo_kP, Constants.lowGearDriveTo_kI, Constants.lowGearDriveTo_kD);
			resetEncoders();
			resetGyro();
			initAngle = (navX.getAngle() + 180) % 360;
			driveToFirstRun = false;
		}
		
		double speed = driveToPid.calcSpeed(getLeftEncoder());
		speed = Util.limitValue(speed, speedLimit);
		
		double currentAngle = (navX.getAngle() + 180) % 360;
		double angleDiff = 0.0;
		angleDiff = (Math.abs(currentAngle) - Math.abs(initAngle)) * (currentAngle < initAngle ? -1.0 : 1.0);
		double leftSpeed = speed;
		double rightSpeed = speed;
		double angleCorrection = Util.limitValue(angleDiff * 0.05, 0.2);
		if(straight){
			leftSpeed += angleCorrection;
			rightSpeed -= angleCorrection;
			SmartDashboard.putNumber("angleDiff", angleCorrection);
		}

		leftSpeed = Util.limitValue(Util.applyMin(leftSpeed, 0.1), 1.0);
		rightSpeed = Util.limitValue(Util.applyMin(rightSpeed, 0.1), 1.0);
//		set(leftSpeed, rightSpeed);
		set(speed, speed);

		return driveToPid.isDone(getLeftEncoder(), 100);
	}
	public void resetDriveTo(){
		driveToFirstRun = true;
		driveToPid.resetAccumI();
		resetEncoder();
		resetGyro();
	}
	
	public int getLeftEncoder(){
		return -leftSRX.getEncPosition();
	}
	
	public int getRightEncoder(){
		return rightSRX.getEncPosition();
	}
	
	private void resetEncoders(){
		leftSRX.setEncPosition(0);
		rightSRX.setEncPosition(0);
	}
	
	public void resetEncoder(){
		resetEncoders();
	}

	public void resetGyro(){
		navX.zeroYaw();
		navX.reset();
	}
	
	private boolean turnToFirstRun = true;
	public boolean turnToAngle(double angle){
		if(turnToFirstRun){
			turnToAnglePid.setDesiredPosition((angle + 180) % 360);
			turnToFirstRun = false;
			setControlMode(ControlMode.ANGLE);
		}
		double currentAngle =  (navX.getAngle() + 180) % 360;
		double speed = turnToAnglePid.calcSpeed(currentAngle);
		speed = Util.applyMin(speed, Constants.minTurnSpeed);
		if(currentAngle > angle){
			set(speed, -speed);
		}
		else if(currentAngle < angle){
			set(speed, -speed);
			
		}
		else{
			set(0.0, 0.0);
		}
		
		return turnToAnglePid.isDone(currentAngle, Constants.lowGearTurnToDeadband);
	}
	public void resetTurnTo(){
		turnToFirstRun = true;
		navX.reset();
	}
	public void arcadeDrive(double yAxis, double xAxis){
		yAxis = Util.applyDeadband(yAxis, Constants.xboxControllerDeadband);
		xAxis = Util.applyDeadband(xAxis, Constants.xboxControllerDeadband);

		set(yAxis + xAxis, yAxis - xAxis);
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
	
	public void set(double left, double right){
		leftSRX.set(left);
		rightSRX.set(right);
	}
	
	public void stopMotors(){
		set(0.0, 0.0);
	}
	
	public void sendSmartdashInfo(){
		SmartDashboard.putNumber("Left Pos", getLeftEncoder());
		SmartDashboard.putNumber("Right Pos", getRightEncoder());
		SmartDashboard.putNumber("NavX Angle", navX.getAngle());
	}

	@Override
	public void runControlLoop() {
		return;		
	}
	
}
