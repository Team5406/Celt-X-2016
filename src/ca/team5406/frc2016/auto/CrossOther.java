package ca.team5406.frc2016.auto;

import ca.team5406.frc2016.RobotStateController;
import ca.team5406.frc2016.subsystems.*;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class CrossOther extends AutonomousRoutine{
	
	private Drive drive;
	private RobotStateController robotState;
	private Timer timer;
	
	private int autonStep;

	public CrossOther(Drive drive, RobotStateController robotState) {
		super("Cross B or D");
		this.drive = drive;
		this.robotState = robotState;
		timer = new Timer();
	}

	@Override
	public void init() {
		autonStep = 0;
		drive.resetDriveTo();
		drive.resetEncoders();
		drive.shiftUp();
		timer.start();
		timer.reset();
	}
	
	@Override
	public void resetTimer(){
		timer.reset();
	}

	@Override
	public void execute() {
		SmartDashboard.putNumber("Auton Step", autonStep);
		switch(autonStep){
			default:
				end();
				break;
			case 0:
				robotState.setRobotState(RobotStateController.RobotState.CARRY_SCALE);
				if(super.getStepTimer() >= 4.0){
					autonStep = -1;
					break;
				}
				if(drive.driveToDistance(-16*12, true, 1.0)){
					drive.resetDriveTo();
					drive.stopMotors();
					System.out.println("Auto Ended Successfully");
					nextStep();
				}
				break;
		}
	}
	
	public void nextStep(){
		autonStep++;
		super.resetStepTimer();
	}

	@Override
	public void end() {
		timer.stop();
		drive.resetDriveTo();
		drive.stopMotors();
	}
	
	
	
}
