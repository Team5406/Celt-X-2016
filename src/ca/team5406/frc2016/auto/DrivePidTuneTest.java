package ca.team5406.frc2016.auto;

import ca.team5406.frc2016.Constants;
import ca.team5406.frc2016.subsystems.*;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DrivePidTuneTest extends AutonomousRoutine{
	
	private Drive drive;
	private Timer timer;
	
	private int autonStep;

	public DrivePidTuneTest(Drive drive) {
		super("PID Tune");
		this.drive = drive;
		timer = new Timer();
	}

	@Override
	public void init() {
//    	SmartDashboard.putNumber("100drive_kP", Constants.highGearDriveTo_kP*100);
//    	SmartDashboard.putNumber("100drive_kI", Constants.highGearDriveTo_kI*100);
//    	SmartDashboard.putNumber("100drive_kD", Constants.highGearDriveTo_kD*100);
		autonStep = 0;
		drive.shiftUp();
		drive.resetDriveTo();
		drive.resetEncoders();
		drive.shiftUp();
		timer.start();
		drive.updateDrivePidConstants();
	}
	
	@Override
	public void resetTimer(){
		timer.reset();
	}

	@Override
	public void execute() {
		double target = (autonStep % 2 == 0 ? 10*12 : 1*12);
		if(drive.driveToDistance(target, true, 1.0)){
			drive.resetDriveTo();
			timer.reset();
			autonStep++;
		}
		drive.updateDrivePidConstants();
		SmartDashboard.putNumber("Auton Step", autonStep);
		SmartDashboard.putNumber("Drive Target", target);
		SmartDashboard.putNumber("Drive Pos", drive.getAvgEncDistance()/Constants.ticksPerInch);
	}
	
	public void nextStep(){
		autonStep++;
		super.resetStepTimer();
	}
	
	@Override
	public void end() {
		timer.stop();
		drive.stopMotors();
		drive.resetDriveTo();
	}
	
	
	
}
