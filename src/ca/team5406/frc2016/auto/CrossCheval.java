package ca.team5406.frc2016.auto;

import ca.team5406.frc2016.RobotStateController;
import ca.team5406.frc2016.RobotStateController.RobotState;
import ca.team5406.frc2016.subsystems.*;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class CrossCheval extends AutonomousRoutine{
	
	private Drive drive;
	private RobotStateController robotState;
	private Timer timer;
	
	private int autonStep;

	public CrossCheval(Drive drive, RobotStateController robotState) {
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
		robotState.setRobotState(RobotStateController.RobotState.INSIDE_SCALE);
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
				if(super.getStepTimer() >= 3.0){
					autonStep = -1;
					break;
				}
				if(drive.driveToDistance(42, true, 1.0)){
					robotState.setRobotState(RobotStateController.RobotState.DOWN_DOWN);
					drive.resetDriveTo();
					drive.stopMotors();
					nextStep();
				}
				break;
			case 1:
				if(super.getStepTimer() >= 3.0){
					autonStep = -1;
					robotState.setRobotState(RobotState.NONE_NONE);
					break;
				}
				if(robotState.getArmPos() == Arm.Positions.DOWN){
					nextStep();
				}
				break;
			case 2:
				if(super.getStepTimer() >= 4.0){
					autonStep = -1;
					break;
				}
				if(drive.driveToDistance(10*12, true, 0.8)){
					drive.resetDriveTo();
					drive.stopMotors();
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
