package ca.team5406.frc2016.auto;

import ca.team5406.frc2016.RobotStateController;
import ca.team5406.frc2016.subsystems.*;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DistanceTest extends AutonomousRoutine{
	
	private Drive drive;
	private RobotStateController robotState;
	private Timer timer;
	
	private int autonStep;

	public DistanceTest(Drive drive, RobotStateController robotState) {
		super("Distance Test");
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
		robotState.setRobotState(RobotStateController.RobotState.NONE_NONE);
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
				if(drive.driveToDistance(-100, true, 0.8)){ //-2600
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
