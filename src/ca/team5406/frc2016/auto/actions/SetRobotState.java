package ca.team5406.frc2016.auto.actions;

import ca.team5406.frc2016.RobotStateController;
import ca.team5406.frc2016.RobotStateController.RobotState;
import ca.team5406.frc2016.subsystems.Subsystem;

public class SetRobotState extends Action{
	
	private RobotStateController robotStateController;
	private RobotState desiredState;
	
	public SetRobotState(RobotState state){
		this(state, 2.0);
	}
	
	public SetRobotState(RobotState state, double timeout){
		super(timeout, true);
		this.desiredState = state;
		this.robotStateController = (RobotStateController) Subsystem.getSubsystem(RobotStateController.NAME);
	}

	@Override
	public void init() {
		robotStateController.setRobotState(desiredState);
	}

	@Override
	public void iter() {
		if(robotStateController.getRobotState() == desiredState){
			finish();
		}
	}

	@Override
	public void end() {

	}

}
