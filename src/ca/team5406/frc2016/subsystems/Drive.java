package ca.team5406.frc2016.subsystems;

import ca.team5406.frc2016.Constants;
import ca.team5406.util.PID;
import ca.team5406.util.Timer;
import ca.team5406.util.Util;
import ca.team5406.util.sensors.RelativeEncoder;
import ca.team5406.util.sensors.RelativeGyro;
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
	
	private RelativeEncoder leftEnc;
	private RelativeEncoder rightEnc;

	private DoubleSolenoid shiftSolenoid;
	private PID turnToAnglePid;
	private PID driveToPid;
	
	private AHRS navX;
	
	public static final String NAME = "Drivetrain";
	
	public static enum ControlMode{
		POWER,
		SPEED,
		DISTANCE,
		ANGLE,
	}
	
	public static enum Gear{
		HIGH,
		LOW
	}
	
	public Drive(){
    	super(NAME);

    	navX = new AHRS(SPI.Port.kMXP);
    	Timer timer = new Timer();
    	timer.start();
    	while(navX.isCalibrating() || timer.get() >= 10){
    		SmartDashboard.putBoolean("Cal Gyro", navX.isCalibrating());
    	}
		SmartDashboard.putBoolean("Cal Gyro", false);
		
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

    	leftEnc = new RelativeEncoder(Constants.leftDriveEncA, Constants.leftDriveEncB);
    	rightEnc = new RelativeEncoder(Constants.rightDriveEncA, Constants.rightDriveEncB);
    	
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
			leftSRX.changeControlMode(TalonControlMode.Speed);
			rightSRX.changeControlMode(TalonControlMode.Speed);
			updateSpeedConstants();
		case DISTANCE:
			leftSRX.changeControlMode(TalonControlMode.Position);
			rightSRX.changeControlMode(TalonControlMode.Position);
			break;
		}
	}
	
	private boolean driveToFirstRun = true;
	private double initAngle = 0.0;
	public boolean driveToDistance(double distance, boolean straight, double speedLimit){
		distance = distance * Constants.ticksPerInch;
		if(driveToFirstRun){
			driveToPid.setDesiredPosition(distance);
			driveToPid.setConstants(Constants.highGearDriveTo_kP, Constants.highGearDriveTo_kI, Constants.highGearDriveTo_kD);
			resetEncoders();
			int i = 0;
			do{
				if(i % 1000 == 0){
					resetGyro();
				}
				if(i % 200 == 0){
					initAngle = navX.getYaw();
				}
			} while(Math.abs(initAngle) >= 1.0);
			driveToFirstRun = false;
		}
		
		double speed = driveToPid.calcSpeed(getLeftEncoder());
		speed = Util.limitValue(speed, speedLimit);

		double currentAngle = navX.getYaw();
		double angleDiff = 0.0;
		angleDiff = Math.abs(currentAngle - initAngle) * (currentAngle < initAngle ? 1.0 : -1.0);
		double leftSpeed = speed;
		double rightSpeed = speed;
		double angleCorrection = Util.limitValue(angleDiff * 0.1, 0.2);
		if(straight){
			leftSpeed += angleCorrection;
			rightSpeed -= angleCorrection;
			SmartDashboard.putNumber("angleDiff", angleDiff);
			SmartDashboard.putNumber("currentAngle", currentAngle);
		}

		leftSpeed = Util.limitValue(Util.applyMin(leftSpeed, 0.1), 1.0);
		rightSpeed = Util.limitValue(Util.applyMin(rightSpeed, 0.1), 1.0);
		set(leftSpeed, rightSpeed);

		return driveToPid.isDone(getLeftEncoder(), 100);
	}
	public void resetDriveTo(){
		driveToFirstRun = true;
		driveToPid.resetAccumI();
		resetEncoders();
		resetGyro();
	}
	
	public int getLeftEncoder(){
		return -leftEnc.get();
	}
	
	public int getRightEncoder(){
		return rightEnc.get();
	}
	
	public void resetEncoders(){
		leftEnc.reset();
		rightEnc.reset();
	}

	public void resetGyro(){
		navX.zeroYaw();
		
		try{
			Thread.sleep(20);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private boolean turnToFirstRun = true;
	public boolean turnToAngle(double angle){
		if(turnToFirstRun){
			turnToAnglePid.setDesiredPosition(angle);
			turnToFirstRun = false;
			setControlMode(ControlMode.ANGLE);
		}
		double currentAngle =  navX.getYaw(); //(navX.getYaw() + 180) % 360;
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
		resetGyro();
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
		updateSpeedConstants();
	}
	public void shiftDown(){
		shiftSolenoid.set(DoubleSolenoid.Value.kReverse);
		updateSpeedConstants();
	}
	
	public void selectGear(Gear gear){
		if(gear == Gear.HIGH){
			shiftUp();
		}
		else if(gear == Gear.LOW){
			shiftDown();
		}
		updateSpeedConstants();
	}
	
	private void updateSpeedConstants(){
		if(getShiftState()){
			leftSRX.setPID(Constants.highGearSpeed_kP, Constants.highGearSpeed_kI, Constants.highGearSpeed_kD, Constants.highGearSpeed_kF, 0, 20, 0);
			rightSRX.setPID(Constants.highGearSpeed_kP, Constants.highGearSpeed_kI, Constants.highGearSpeed_kD, Constants.highGearSpeed_kF, 0, 20, 0);
		}
		else{
			leftSRX.setPID(Constants.lowGearSpeed_kP, Constants.lowGearSpeed_kI, Constants.lowGearSpeed_kD, Constants.lowGearSpeed_kF, 0, 20, 0);
			rightSRX.setPID(Constants.lowGearSpeed_kP, Constants.lowGearSpeed_kI, Constants.lowGearSpeed_kD, Constants.lowGearSpeed_kF, 0, 20, 0);
		}
	}
	
	public void setLeftRightSpeed(double leftSpeed, double rightSpeed){
		leftSpeed *= (getShiftState() ? Constants.highGearTPF : Constants.lowGearTPF) / (60 * 10);
		rightSpeed *= (getShiftState() ? Constants.highGearTPF : Constants.lowGearTPF) / (60 * 10);
		
		leftSRX.set(leftSpeed);
		rightSRX.set(rightSpeed);
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
		SmartDashboard.putNumber("NavX Angle", navX.getYaw());
//		SmartDashboard.putNumber("Left Drive Current", leftSRX.getOutputCurrent());
//		SmartDashboard.putNumber("Left Drive F Current", leftFollowerSRX.getOutputCurrent());
//		SmartDashboard.putNumber("Right Drive Current", rightSRX.getOutputCurrent());
//		SmartDashboard.putNumber("Right Drive F Current", rightFollowerSRX.getOutputCurrent());
	}

	@Override
	public void runControlLoop() {
		return;		
	}
	
}
