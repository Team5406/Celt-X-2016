package ca.team5406.frc2016.auto;

import ca.team5406.frc2016.RobotStateController.RobotState;
import ca.team5406.frc2016.auto.actions.*;
import ca.team5406.frc2016.subsystems.Drive.Gear;
import ca.team5406.util.AutoBuilder;

public class NewCrossTall extends AutonomousRoutine{
	
	private AutoBuilder autoRoutine;
	
	public NewCrossTall(){
		super("Cross Short");
		autoRoutine = new AutoBuilder();

		autoRoutine.addStep(new SetRobotState(RobotState.CARRY_UP));
		autoRoutine.addStep(new DriveDistance(-7500, true, 1.0, 2.5, Gear.HIGH));
		
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
