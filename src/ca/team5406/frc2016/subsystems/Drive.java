package ca.team5406.frc2016.subsystems;

import ca.team5406.frc2016.Constants;
import ca.team5406.util.AccelFilter;
import ca.team5406.util.PID;
import ca.team5406.util.Timer;
import ca.team5406.util.Util;
import ca.team5406.util.sensors.RelativeEncoder;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import usfirst.frc.team2168.FalconPathPlanner;
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
	private PID leftSideSpeedPid;
	private PID rightSideSpeedPid;

	private AccelFilter leftAccelFilter = new AccelFilter(0.05);
	private AccelFilter rightAccelFilter = new AccelFilter(0.05);
	private AccelFilter turnAccelFilter = new AccelFilter(0.05);
	
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
    	leftSRX.changeControlMode(TalonControlMode.PercentVbus);
    	leftSRX.reverseSensor(true);
    	leftSRX.setInverted(true);
    	leftFollowerSRX.setInverted(true);
    	
    	rightSRX = Util.createDriveTalon(Constants.rightDriveMotorA);
    	rightFollowerSRX = new CANTalon(Constants.rightDriveMotorB);
    	rightSRX.changeControlMode(TalonControlMode.PercentVbus);
    	
    	turnToAnglePid = new PID();
    	turnToAnglePid.setConstants(Constants.lowGearTurnTo_kP, Constants.lowGearTurnTo_kI, Constants.lowGearTurnTo_kD);
    	driveToPid = new PID();
    	leftSideSpeedPid = new PID();
    	rightSideSpeedPid = new PID();
    	
    	leftFollowerSRX.changeControlMode(TalonControlMode.Follower);
    	leftFollowerSRX.set(leftSRX.getDeviceID());
    	rightFollowerSRX.changeControlMode(TalonControlMode.Follower);
    	rightFollowerSRX.set(rightSRX.getDeviceID());

    	leftEnc = new RelativeEncoder(Constants.leftDriveEncA, Constants.leftDriveEncB);
    	rightEnc = new RelativeEncoder(Constants.rightDriveEncA, Constants.rightDriveEncB);

   
    	shiftSolenoid = new DoubleSolenoid(Constants.shiftUpSolenoid, Constants.shiftDownSolenoid);
	}	
	
	private boolean pathDriveToFirstRun = true;
	private double pathInitAngle = 0.0;
	private FalconPathPlanner pathDistancePlanner;
	private double[][] leftPath;
	private double[][] rightPath;
	private Timer timeStepMonitor = new Timer();
	public boolean drivePathDistance(double distance, boolean straight, double timeAllowed){
		if(pathDriveToFirstRun){
			timeStepMonitor.reset();
			timeStepMonitor.start();
			int i = 0;
			do{
				if(i % 1000 == 0){
					resetGyro();
				}
				if(i % 200 == 0){
					initAngle = navX.getYaw();
				}
			} while(Math.abs(initAngle) >= 1.0);
			
			double[][] waypoints = new double[][]{
				{0, 0},
				{0, distance/12.0},
			};
			pathDistancePlanner = new FalconPathPlanner(waypoints);
			pathDistancePlanner.calculate(timeAllowed, 0.1, Constants.trackWidth);
			leftPath = pathDistancePlanner.smoothLeftVelocity;
			rightPath = pathDistancePlanner.smoothRightVelocity;
			pathDriveToFirstRun = false;
		}
		int currentStep = (int) Math.floor(timeStepMonitor.get() / 0.1);
		
		if(currentStep > leftPath.length){
			stopMotors();
			return true;
		}
		
		double targetLeftSpeed = leftPath[currentStep][1];
		double targetRightSpeed = rightPath[currentStep][1];
		
		double currentAngle = navX.getYaw();
		double angleDiff = Math.abs(currentAngle - pathDistancePlanner.heading[currentStep][1]) * (currentAngle < pathDistancePlanner.heading[currentStep][1] ? 1.0 : -1.0);
		double angleCorrection = Util.limitValue(angleDiff * 0.1, 0.2);
		targetLeftSpeed += angleCorrection;
		targetRightSpeed -= angleCorrection;
		
		setLeftRightSpeed(targetLeftSpeed, targetRightSpeed);
		
		return false;
	}

	
	private boolean driveToFirstRun = true;
	private double initAngle = 0.0;
	private int j = 0;
	public boolean driveToDistance(double distance, boolean straight, double speedLimit){
		distance = distance * Constants.ticksPerInch;
		SmartDashboard.putBoolean("first", driveToFirstRun);
		if(driveToFirstRun){
			driveToPid.setConstants(Constants.highGearDriveTo_kP, Constants.highGearDriveTo_kI, Constants.highGearDriveTo_kD);
//			resetEncoders();
			driveToPid.setDesiredPosition(distance);
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
			leftAccelFilter.reset();
			rightAccelFilter.reset();
			j = 0;
		}
		
		double speed = driveToPid.calcSpeed(getAvgEncDistance());
		speed = Util.limitValue(speed, speedLimit);

		double currentAngle = navX.getYaw();
		double angleDiff = Math.abs(currentAngle - initAngle) * (currentAngle < initAngle ? 1.0 : -1.0);
		double leftSpeed = speed;
		double rightSpeed = speed;
		double angleCorrection = Util.limitValue(angleDiff * 0.2, 0.2);

		leftAccelFilter.set(Util.limitValue(leftSpeed, 1.0));
		rightAccelFilter.set(Util.limitValue(rightSpeed, 1.0));

		if(j++ < 1 / 0.03){
			leftSpeed = leftAccelFilter.get();
			rightSpeed = rightAccelFilter.get();
		}
		
		if(straight){
			leftSpeed += angleCorrection;
			rightSpeed -= angleCorrection;
		}

		SmartDashboard.putNumber("LeftSetSpeed", leftSpeed);
		SmartDashboard.putNumber("Setpoint", driveToPid.getDesiredPosition());
		set(Util.limitValue(Util.applyMin(leftSpeed, 0.1), 1.0), Util.limitValue(Util.applyMin(rightSpeed, 0.1), 1.0));

		return driveToPid.isDone(getAvgEncDistance(), Constants.highGearDriveToDeadband);
	}
	
	public void resetDriveTo(){
		driveToFirstRun = true;
		driveToPid.resetAccumI();
//		resetEncoders();
//		resetGyro();
	}
	
	public void updateDrivePidConstants(){
		driveToPid.setConstants(SmartDashboard.getNumber("100drive_kP", Constants.highGearDriveTo_kP)/100, SmartDashboard.getNumber("100drive_kI", Constants.highGearDriveTo_kI)/100, SmartDashboard.getNumber("100drive_kD", Constants.highGearDriveTo_kD)/100);
	}
	
	public void updateTurnPidConstants(){
		turnToAnglePid.setConstants(SmartDashboard.getNumber("100turn_kP", Constants.lowGearTurnTo_kP)/100, SmartDashboard.getNumber("100turn_kI", Constants.lowGearTurnTo_kI)/100, SmartDashboard.getNumber("100turn_kD", Constants.lowGearTurnTo_kD)/100);
	}
	
	public int getLeftEncoder(){
		return leftEnc.get();
	}
	
	public double getLeftVel(){
		return leftEnc.getRate() * 60;
	}
	
	public int getRightEncoder(){
		return rightEnc.get();
	}
	
	public double getRightVel(){
		return rightEnc.getRate() * 60;
	}
	
	public double getAvgEncDistance(){
		return (getLeftEncoder() + getRightEncoder()) / 2.0;
	}
	
	public double getAvgEncInches(){
		return getAvgEncDistance() / Constants.ticksPerInch;
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
	
	int k = 0;
	private boolean turnToFirstRun = true;
	public boolean turnToAngle(double angle){
		if(turnToFirstRun){
			turnToAnglePid.setDesiredPosition(angle);
			turnToFirstRun = false;
			turnAccelFilter.reset();
			k = 0;
		}
		double currentAngle =  navX.getYaw(); //(navX.getYaw() + 180) % 360;
		double speed = turnToAnglePid.calcSpeed(currentAngle);
		turnAccelFilter.set(Util.limitValue(speed, 1.0));
		if(k++ < 1 / 0.03){
			speed = turnAccelFilter.get();
		}
		speed = Util.limitValue(Util.applyMin(speed, 0.1), 1.0);
		
		set(speed, -speed);
		
		return turnToAnglePid.isDone(currentAngle, Constants.lowGearTurnToDeadband);
	}
	public void resetTurnTo(){
		turnToFirstRun = true;
//		resetGyro();
	}
	public void arcadeDrive(double yAxis, double xAxis){
		yAxis = Util.applyDeadband(yAxis, Constants.xboxControllerDeadband);
		xAxis = Util.applyDeadband(xAxis, Constants.xboxControllerDeadband);

		set(yAxis + xAxis, yAxis - xAxis);
	}
	
	private double prevTurn = 0.0;
	public void cheesyDrive(double throttle, double turn){
		double turnVel = turn - prevTurn;
		double kV = 0.0;
		turn += turnVel * kV;
		
		
		
		set(throttle + (throttle * turn), throttle - (throttle * turn));
	}
	
	public boolean getShiftedHigh(){
		return (shiftSolenoid.get() == DoubleSolenoid.Value.kForward);
	}
	
	public void shiftUp(){
		shiftSolenoid.set(DoubleSolenoid.Value.kForward);
	}
	public void shiftDown(){
		shiftSolenoid.set(DoubleSolenoid.Value.kReverse);
	}
	
	public void selectGear(Gear gear){
		if(gear == Gear.HIGH){
			shiftUp();
		}
		else if(gear == Gear.LOW){
			shiftDown();
		}
	}
	
	public void set(double left, double right){
		leftSRX.set(left);
		rightSRX.set(right);
	}
	
	public void setLeftRightSpeed(double desiredSpeedLeft, double desiredSpeedRight){
		leftSideSpeedPid.setConstants(Constants.highGearSpeed_kP, Constants.highGearSpeed_kI, Constants.highGearSpeed_kD);
		rightSideSpeedPid.setConstants(Constants.highGearSpeed_kP, Constants.highGearSpeed_kI, Constants.highGearSpeed_kD);
		leftSideSpeedPid.setDesiredPosition(desiredSpeedLeft);
		rightSideSpeedPid.setDesiredPosition(desiredSpeedRight);
		double maxRPM = Math.min(Constants.maxLeftRPM, Constants.maxRightRPM);
		double kV = 12.0 / DriverStation.getInstance().getBatteryVoltage();
		double leftPid = leftSideSpeedPid.calcSpeed(getLeftVel()/maxRPM);
		double rightPid = leftSideSpeedPid.calcSpeed(getRightVel()/maxRPM);

		double leftOutputPower = kV * (desiredSpeedLeft + leftPid);
		double rightOutputPower = kV * (desiredSpeedRight + rightPid);
		set(leftOutputPower, rightOutputPower);
	}
	
	public void stopMotors(){
		set(0.0, 0.0);
	}
	
	private double applyLeftFilter(double value){
		double max = 5000.0;
		double filtered = value * max;
		return filtered / max;
	}
	
	private double applyRightFilter(double value){
		double max = 5000.0;
		double filtered = value * max;
		return filtered / max;
	}
	
	public void sendSmartdashInfo(){
		SmartDashboard.putNumber("Left Pos", getLeftEncoder());
		SmartDashboard.putNumber("Right Pos", getRightEncoder());
		SmartDashboard.putNumber("Left Vel", getLeftVel());
		SmartDashboard.putNumber("Right Vel", getRightVel());
		SmartDashboard.putNumber("NavX Angle", navX.getYaw());
		SmartDashboard.putNumber("Left Set", leftSRX.get());
	}

	@Override
	public void runControlLoop() {
		return;		
	}
	
}
