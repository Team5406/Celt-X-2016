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
    	
    	leftFollowerSRX.changeControlMode(TalonControlMode.Follower);
    	leftFollowerSRX.set(leftSRX.getDeviceID());
    	rightFollowerSRX.changeControlMode(TalonControlMode.Follower);
    	rightFollowerSRX.set(rightSRX.getDeviceID());
   
    	shiftSolenoid = new DoubleSolenoid(Constants.shiftUpSolenoid, Constants.shiftDownSolenoid);
    	
    	rioAccel = new RioAccelerometer();
		
	}
	
	private CANTalon createDriveTalon(int id){
		CANTalon talon = new CANTalon(id);
		talon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
	    talon.reverseSensor(false);
	    talon.configNominalOutputVoltage(+0f, -0f);
	    talon.configPeakOutputVoltage(+12f, -12f);
	    talon.setAllowableClosedLoopErr(0);
	    return talon;
	}
	
	private void initPositionControlSRX(CANTalon talon){
		talon.changeControlMode(TalonControlMode.Position);
		talon.setP(Constants.lowGearDriveTo_kP);
		talon.setI(Constants.lowGearDriveTo_kI);
		talon.setD(Constants.lowGearDriveTo_kD);
		talon.setF(Constants.lowGearDriveTo_kF);
		talon.configEncoderCodesPerRev(Constants.lowGearCpr);
		talon.setEncPosition(0);
	}
	
	private void initDriverControlSRX(CANTalon talon){
		talon.changeControlMode(TalonControlMode.PercentVbus);
		talon.set(0);
	}
	
	public void initPositionControl(){
		initPositionControlSRX(leftSRX);
		initPositionControlSRX(rightSRX);
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
	
	public void initDriverControl(){
		initDriverControlSRX(leftSRX);
		initDriverControlSRX(rightSRX);
	}
	
	public void arcadeDrive(double yAxis, double xAxis){
		setLeftRightPower(yAxis + xAxis, yAxis - xAxis);
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
	
	private void setLeftRightPower(double leftPower, double rightPower){
		leftPower = Util.applyDeadband(leftPower, Constants.xboxControllerDeadband);
		rightPower = Util.applyDeadband(rightPower, Constants.xboxControllerDeadband);
		
		leftSRX.set(leftPower);
		rightSRX.set(rightPower);
	}
	
	public void sendSmartdashInfo(){
		SmartDashboard.putNumber("Left Speed", leftSRX.getEncPosition());
		SmartDashboard.putNumber("Left Pos", leftSRX.getEncVelocity());
		SmartDashboard.putNumber("Right Speed", rightSRX.getEncPosition());
		SmartDashboard.putNumber("Right Pos", rightSRX.getEncVelocity());
		SmartDashboard.putNumber("X Dist", rioAccel.getDistX());
		SmartDashboard.putNumber("Y Dist", rioAccel.getDistY());
		SmartDashboard.putNumber("Z Dist", rioAccel.getDistZ());
	}
	
}
