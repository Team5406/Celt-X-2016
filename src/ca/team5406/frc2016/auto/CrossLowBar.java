package ca.team5406.frc2016.auto;

import ca.team5406.frc2016.Constants;
import ca.team5406.frc2016.RobotStateController;
import ca.team5406.frc2016.subsystems.*;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class CrossLowBar extends AutonomousRoutine{
	
	private Drive drive;
	private RobotStateController robotState;
	private Timer timer;
	
	private int autonStep;

	public CrossLowBar(Drive drive,RobotStateController robotState) {
		super("Cross LB");
		this.drive = drive;
		this.robotState = robotState;
		timer = new Timer();
	}

	@Override
	public void init() {
		robotState.setRobotState(RobotStateController.RobotState.INSIDE_INSIDE);
		autonStep = 0;
		drive.shiftUp();
		drive.resetDriveTo();
		drive.resetEncoders();
		timer.start();
	}
	
	@Override
	public void resetTimer(){
		timer.reset();
	}

	@Override
	public void execute() {
		drive.shiftUp();
		SmartDashboard.putNumber("Auton Step", autonStep);
		switch(autonStep){
		default:
			end();
			break;
		case 0:
			robotState.setRobotState(RobotStateController.RobotState.DOWN_DOWN);
			nextStep();
//			if(super.getStepTimer() >= 2.0){
//				autonStep = -1;
//				break;
//			}
//			drive.shiftUp();
//			if(drive.driveToDistance(-6, false, 1.0)){ //-500
//				drive.resetDriveTo();
//				drive.shiftUp();
//				robotState.setRobotState(RobotStateController.RobotState.DOWN_DOWN);
//				drive.stopMotors();
//				nextStep();
//			}
			break;
		case 1:
			boolean armGood = (robotState.getArmEnc() < Constants.armCarryPos) || robotState.getArmPos().equals(Arm.Positions.DOWN);
			boolean rampGood = true; //(robotState.getRampEnc() < Constants.rampMidPosition) || robotState.getRampPos().equals(BatteringRamp.Positions.DOWN);
			if(armGood && rampGood){
				nextStep();
			}
			break;
		case 2:
			if(super.getStepTimer() >= 40){
				autonStep = -1;
				break;
			}
			if(drive.driveToDistance(12*12, true, 0.8)){
				drive.resetDriveTo();
				drive.resetEncoders();
				nextStep();
				System.out.println("Auto Ended Successfully");
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
		drive.stopMotors();
		drive.resetDriveTo();
	}
	
	
	
}
