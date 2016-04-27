package ca.team5406.frc2016.auto;

import ca.team5406.frc2016.Constants;
import ca.team5406.frc2016.subsystems.*;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TurnPidTuneTest extends AutonomousRoutine{
	
	private Drive drive;
	private Timer timer;
	
	private int autonStep;

	public TurnPidTuneTest(Drive drive) {
		super("PID Tune");
		this.drive = drive;
		timer = new Timer();
	}

	@Override
	public void init() {
		autonStep = 0;
		drive.shiftUp();
		drive.resetDriveTo();
		drive.resetEncoders();
		drive.shiftUp();
		timer.start();
		drive.updateTurnPidConstants();
	}
	
	@Override
	public void resetTimer(){
		timer.reset();
	}

	@Override
	public void execute() {	
		
		double target = (autonStep % 2 == 0 ? 25 : -45);
		
		drive.updateTurnPidConstants();
		
		boolean pidSuccess = drive.turnToAngle(target);// || timer.get() >= 5.0;
		if(pidSuccess){
			drive.resetTurnTo();
			timer.reset();
			autonStep++;
			try{
				Thread.sleep(100);
			}
			catch (InterruptedException e){}
		}
		SmartDashboard.putNumber("Auton Step", autonStep);
		SmartDashboard.putNumber("Drive Target", target);
		SmartDashboard.putNumber("Turn Success", (pidSuccess ? 100: 0));
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
