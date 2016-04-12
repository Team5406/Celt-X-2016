package ca.team5406.frc2016.auto;

import ca.team5406.frc2016.RobotStateController.RobotState;
import ca.team5406.frc2016.auto.actions.*;
import ca.team5406.frc2016.subsystems.Drive.Gear;
import ca.team5406.util.AutoBuilder;

public class NewCrossShort extends AutonomousRoutine{
	
	private AutoBuilder autoRoutine;
	
	public NewCrossShort(){
		super("Cross Short");
		autoRoutine = new AutoBuilder();

		autoRoutine.addStep(new DriveDistance(-300, true, 1.0, 1.0, Gear.HIGH));
		autoRoutine.addStep(new SetRobotState(RobotState.DOWN_DOWN));
		autoRoutine.addStep(new DriveDistance(-7000, true, 0.8, 2.5, Gear.HIGH));
		
	}
	
	public AutoBuilder getRoutine(){
		return autoRoutine;
	}


	@Override
	public void init() {
		autoRoutine.reset();
	}

	@Override
	public void execute() {
		autoRoutine.iterActions();
	}

	@Override
	public void end() {
		autoRoutine.stop();
	}

	@Override
	public void resetTimer() {}
	
}
