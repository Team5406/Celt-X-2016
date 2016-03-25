package ca.team5406.frc2016.auto;

import ca.team5406.frc2016.RobotStateController;
import ca.team5406.frc2016.subsystems.*;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TurnTest extends AutonomousRoutine{
	
	private Drive drive;
	private RobotStateController robotState;
	private Timer timer;
	
	private int autonStep;

	public TurnTest(Drive drive, RobotStateController robotState) {
		super("Turn Test");
		this.drive = drive;
		this.robotState = robotState;
		timer = new Timer();
	}

	@Override
	public void init() {
		autonStep = 0;
		drive.resetTurnTo();
		drive.resetEncoder();
		drive.shiftDown();
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
//		if(timer.get() >= 2.5){
//			drive.stopMotors();
//			return;
//		}
//		else{
			switch(autonStep){
				default:
					end();
					break;
				case 0:
					if(drive.turnToAngle(-45)){
						drive.resetTurnTo();
						drive.stopMotors();
						autonStep++;
					}
					break;
		//		case 1:
			}
		}
//	}

	@Override
	public void end() {
		timer.stop();
		drive.resetDriveTo();
		drive.stopMotors();
	}
	
	
	
}
