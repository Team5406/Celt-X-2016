package ca.team5406.frc2016.auto.actions;

import ca.team5406.frc2016.subsystems.Drive;
import ca.team5406.frc2016.subsystems.Drive.ControlMode;
import ca.team5406.frc2016.subsystems.Drive.Gear;
import ca.team5406.frc2016.subsystems.Subsystem;

public class TurnDeg extends Action{
	
	private double angle;
	private Drive drive;
	
	public TurnDeg(double distance){
		this(distance, 3.0);
	}
	
	public TurnDeg(double angle, double timeout){
		super(timeout, false);
		this.drive = (Drive) Subsystem.getSubsystem(Drive.NAME);
	}

	@Override
	public void init() {
		drive.resetDriveTo();
		drive.resetTurnTo();
		drive.selectGear(Gear.LOW);
	}

	@Override
	public void iter() {
		if(drive.turnToAngle(angle)){
			finish();
		}
	}

	@Override
	public void end() {
		drive.resetDriveTo();
		drive.resetTurnTo();
	}

}
