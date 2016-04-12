package ca.team5406.frc2016.auto.actions;

import ca.team5406.frc2016.RobotStateController;
import ca.team5406.frc2016.RobotStateController.RobotState;
import ca.team5406.frc2016.subsystems.Intake;
import ca.team5406.frc2016.subsystems.Subsystem;
import edu.wpi.first.wpilibj.Timer;

public class CycleIntake extends Action{
	
	private Intake intake;
	private double duration;
	private Timer cycleTimer;
	private boolean forward;
	
	public CycleIntake(){
		this(2.0);
	}
	
	public CycleIntake(double duration){
		super(duration + 0.5, true);
		this.duration = duration;
		this.intake = (Intake) Subsystem.getSubsystem(Intake.NAME);
		cycleTimer = new Timer();
		forward = false;
	}

	@Override
	public void init() {
		cycleTimer.start();
		cycleTimer.reset();
		forward = false;
	}

	@Override
	public void iter() {
		if(cycleTimer.get() % 0.5 == 0.0){
			forward = !forward;
		}
		intake.setIntakeButtons(forward, !forward);
		if(cycleTimer.get() >= duration){
			finish();
		}
	}

	@Override
	public void end() {
		forward = true;
	}

}
