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

	public CrossLowAndScore(RobotStateController robotState, Drive drive, Intake intake) {
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
		drive.resetEncoder();
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
		drive.shiftUp();
		SmartDashboard.putNumber("Auton Step", autonStep);
//		if(timer.get() >= 3.0){
//			drive.stopMotors();
//			return;
//		}
//		else{
			switch(autonStep){
			default:
				end();
				break;
			case 0:
				drive.shiftDown();
				if(drive.driveToDistance(800, false, 0.6)){ //-500
					drive.resetDriveTo();
					drive.shiftUp();
					robotState.setRobotState(RobotStateController.RobotState.CARRY_MID);
					autonStep++;
					drive.stopMotors();
				}
				break;
			case 1:
				armGood = (robotState.getArmEnc() <= Constants.armCarryPos) || robotState.getArmPos().equals(Arm.Positions.CARRY);
				rampGood = (robotState.getRampEnc() <= Constants.rampMidPosition) || SmartDashboard.getString("Ramp Pos").equals(BatteringRamp.Positions.MID.name());
				if(armGood && rampGood){
					armGood = false;
					rampGood = false;
					robotState.setRobotState(RobotStateController.RobotState.DOWN_DOWN);
					autonStep++;
				}
				break;
			case 2:
				armGood = (robotState.getArmEnc() < Constants.armCarryPos) || robotState.getArmPos().equals(Arm.Positions.DOWN);
				rampGood = (robotState.getRampEnc() < Constants.rampMidPosition) || SmartDashboard.getString("Ramp Pos").equals(BatteringRamp.Positions.DOWN.name());
				if(armGood && rampGood){
					autonStep++;
				}
				break;
			case 3:
				if(drive.driveToDistance(6800, true, 0.8)){
					robotState.setRobotState(RobotStateController.RobotState.CARRY_SCALE);
					drive.resetDriveTo();
					drive.resetTurnTo();
					drive.shiftDown();
					autonStep++;
				}
				break;
			case 4:
				if(drive.turnToAngle(20)){
					drive.resetDriveTo();
					drive.resetTurnTo();
					drive.shiftUp();
					autonStep++;
				}
				break;
			case 5:
				if(drive.driveToDistance(1000, true, 0.8)){
					robotState.setRobotState(RobotStateController.RobotState.DOWN_DOWN);
					drive.resetDriveTo();
					drive.resetTurnTo();
					drive.shiftDown();
					autonStep++;
				}
				break;
			case 6:
				if(drive.turnToAngle(30)){ //28
					drive.resetDriveTo();
					drive.resetTurnTo();
					drive.shiftUp();
					autonStep++;
				}
				break;
			case 7:
				if(drive.driveToDistance(4800, true, 0.8)){
					drive.resetDriveTo();
					autonStep++;
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
//	}
	
	@Override
	public void end() {
		timer.stop();
		drive.stopMotors();
		drive.resetDriveTo();
	}
	
	
	
}
