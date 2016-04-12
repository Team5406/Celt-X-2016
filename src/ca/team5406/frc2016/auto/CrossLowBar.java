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

	public CrossLowBar(RobotStateController robotState, Drive drive) {
		super("Cross LB or Port");
		this.drive = drive;
		this.robotState = robotState;
		timer = new Timer();
	}

	@Override
	public void init() {
		robotState.setRobotState(RobotStateController.RobotState.INSIDE_MID);
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
			if(super.getStepTimer() >= 2.0){
				autonStep = -1;
				break;
			}
			drive.shiftDown();
			if(drive.driveToDistance(-300, true, 1.0)){ //-500
				drive.resetDriveTo();
				drive.shiftUp();
				robotState.setRobotState(RobotStateController.RobotState.DOWN_DOWN);
				nextStep();
				drive.stopMotors();
			}
			break;
		case 1:
			boolean armGood = (robotState.getArmEnc() < Constants.armCarryPos) || robotState.getArmPos().equals(Arm.Positions.DOWN);
			boolean rampGood = (robotState.getRampEnc() < Constants.rampMidPosition) || SmartDashboard.getString("Ramp Pos").equals(BatteringRamp.Positions.DOWN.name());
			if(armGood && rampGood){
				nextStep();
			}
			break;
		case 2:
			if(super.getStepTimer() >= 2.5){
				autonStep = -1;
				break;
			}
			if(drive.driveToDistance(-7000, true, 0.8)){
				drive.resetDriveTo();
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
