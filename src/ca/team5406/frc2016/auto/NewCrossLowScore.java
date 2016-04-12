package ca.team5406.frc2016.auto;

import ca.team5406.frc2016.RobotStateController.RobotState;
import ca.team5406.frc2016.auto.actions.*;
import ca.team5406.frc2016.subsystems.Drive.Gear;
import ca.team5406.util.AutoBuilder;

public class NewCrossLowScore extends AutonomousRoutine{
	
	private AutoBuilder autoRoutine;
	
	public NewCrossLowScore(){
		super("Cross Short");
		autoRoutine = new AutoBuilder();

		autoRoutine.addStep(new DriveDistance(800, true, 1.0, 1.0, Gear.HIGH));
		autoRoutine.addStep(new SetRobotState(RobotState.DOWN_DOWN));
		autoRoutine.addStep(new DriveDistance(6800, true, 0.8, 2.5, Gear.HIGH));
		autoRoutine.addStep(new TurnDeg(20));
		autoRoutine.addStep(new DriveDistance(1000, true, 0.8, 2.5, Gear.HIGH));
		autoRoutine.addStep(new TurnDeg(30));
		autoRoutine.addStep(new DriveDistance(4800, true, 0.8, 2.5, Gear.HIGH));
		autoRoutine.addStep(new CycleIntake());
		
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
