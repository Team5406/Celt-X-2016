package ca.team5406.frc2016.auto;

import ca.team5406.frc2016.RobotStateController.RobotState;
import ca.team5406.frc2016.auto.actions.*;
import ca.team5406.util.AutoBuilder;

public class PathCrossLowAndScore extends AutonomousRoutine{
	
	private AutoBuilder autoRoutine;
	
	public PathCrossLowAndScore(){
		super("Cross Short");
		autoRoutine = new AutoBuilder();
		
		double[][] waypoints = new double[][]{
			{2.5, 25},
			{20, 25},
//			{24, 22},
			{25, 19}
		};

		autoRoutine.addStep(new DriveDistance(300));
		autoRoutine.addStep(new SetRobotState(RobotState.DOWN_DOWN));
		autoRoutine.addStep(new FollowPath(waypoints));
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
