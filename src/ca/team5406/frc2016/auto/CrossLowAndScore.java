package ca.team5406.frc2016.auto;

import ca.team5406.frc2016.Constants;
import ca.team5406.frc2016.RobotStateController;
import ca.team5406.frc2016.subsystems.*;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class CrossLowAndScore extends AutonomousRoutine{
	
	private Drive drive;
	private RobotStateController robotState;
	private Intake intake;
	private Timer timer;
	
	private int autonStep;

	public CrossLowAndScore(Drive drive, RobotStateController robotState, Intake intake) {
		super("Cross LB and Score");
		this.drive = drive;
		this.robotState = robotState;
		this.intake = intake;
		timer = new Timer();
	}

	@Override
	public void init() {
		timer.reset();
		timer.start();
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

	boolean armGood;
	boolean rampGood;
	@Override
	public void execute() {
		SmartDashboard.putNumber("Auton Step", autonStep);
		switch(autonStep){
		default:
			end();
			break;
		case 0:
			robotState.setRobotState(RobotStateController.RobotState.CARRY_SCALE);
			nextStep();
			//Disable Initial Scoot since we don't have the ramp.
//			if(super.getStepTimer() >= 5.0){
//				autonStep = -1;
//				break;
//			}
//			drive.set(0.5, 0.5);;
//			if(drive.getAvgEncInches() >= 6){
//				drive.stopMotors();
//				drive.resetDriveTo();
//				robotState.setRobotState(RobotStateController.RobotState.DOWN_DOWN);
//				nextStep();
//			}
			break;
		case 1:
			armGood = (robotState.getArmEnc() <= Constants.armCarryPos) || robotState.getArmPos().equals(Arm.Positions.CARRY);
			rampGood = true; //(robotState.getRampEnc() <= Constants.rampMidPosition) || SmartDashboard.getString("Ramp Pos").equals(BatteringRamp.Positions.MID.name());
			if(armGood && rampGood){
				armGood = false;
				rampGood = false;
				robotState.setRobotState(RobotStateController.RobotState.DOWN_DOWN);
				nextStep();
			}
			break;
		case 2:
			armGood = (robotState.getArmEnc() < Constants.armCarryPos) || robotState.getArmPos().equals(Arm.Positions.DOWN);
			rampGood = true; //(robotState.getRampEnc() < Constants.rampMidPosition) || SmartDashboard.getString("Ramp Pos").equals(BatteringRamp.Positions.DOWN.name());
			if(armGood && rampGood){
				nextStep();
			}
			break;
		case 3:
			if(super.getStepTimer() >= 6.0){
				autonStep = -1;
				break;
			}
			if(drive.driveToDistance(9.8*12, true, 0.7)){
				robotState.setRobotState(RobotStateController.RobotState.CARRY_UP);
				drive.resetDriveTo();
				drive.resetTurnTo();
				nextStep();
			}
			break;
		case 4:
			if(super.getStepTimer() >= 5.0){
				autonStep = -1;
				break;
			}
			if(drive.turnToAngle(20)){
				drive.resetDriveTo();
				drive.resetTurnTo();
				drive.resetEncoders();
				nextStep();
			}
			break;
		case 5:
			if(super.getStepTimer() >= 6.0){
				autonStep = -1;
				break;
			}
			if(drive.driveToDistance(7.5*12, true, 0.7)){
				robotState.setRobotState(RobotStateController.RobotState.DOWN_DOWN);
				drive.resetDriveTo();
				drive.resetTurnTo();
				nextStep();
			}
			break;
		case 6:
			if(super.getStepTimer() >= 4.0){
				autonStep = -1;
				break;
			}
			if(drive.turnToAngle(36)){
				drive.resetDriveTo();
				drive.resetTurnTo();
				drive.resetEncoders();
				nextStep();
			}
			break;
		case 7:
			if(super.getStepTimer() >= 2.0){
				nextStep();
				drive.shiftDown();
				break;
			}
			if(drive.driveToDistance(8*12, true, 0.8)){
				drive.resetDriveTo();
				drive.shiftDown();
				nextStep();
				System.out.println("Auto Ended Successfully");
				timer.reset();
			}
			break;
		case 8:
			if(timer.get() >= 2.0){
				timer.reset();
			}
			else if(timer.get() <= 1.0){
				intake.setIntakeButtons(false, true);
			}
			else{
				intake.setIntakeButtons(true, false);
			}
			break;
		}
	}
	
	public void nextStep(){
		autonStep++;
		super.resetStepTimer();
		drive.stopMotors();
	}
	
	@Override
	public void end() {
		timer.stop();
		drive.stopMotors();
		drive.resetDriveTo();
	}
	
	
	
}
