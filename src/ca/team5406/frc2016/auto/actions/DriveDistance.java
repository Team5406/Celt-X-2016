package ca.team5406.frc2016.auto.actions;

import ca.team5406.frc2016.subsystems.Drive;
import ca.team5406.frc2016.subsystems.Drive.ControlMode;
import ca.team5406.frc2016.subsystems.Drive.Gear;
import ca.team5406.frc2016.subsystems.Subsystem;

public class DriveDistance extends Action{
	
	private double distance;
	private boolean straight;
	private double speedLimit;
	private Drive drive;
	private Gear gear;
	
	public DriveDistance(double distance){
		this(distance, true, 1.0, 3.0, Gear.HIGH);
	}
	
	public DriveDistance(double distance, boolean straight, double speedLimit, double timeout, Gear gear){
		super(timeout, false);
		this.distance = distance;
		this.drive = (Drive) Subsystem.getSubsystem(Drive.NAME);
		this.gear = gear;
		this.straight = straight;
		this.speedLimit = speedLimit;
	}

	@Override
	public void init() {
		drive.resetDriveTo();
		drive.resetTurnTo();
		drive.selectGear(gear);
	}

	@Override
	public void iter() {
		if(drive.driveToDistance(distance, straight, speedLimit)){
			finish();
		}
	}

	@Override
	public void end() {
		drive.resetDriveTo();
		drive.resetTurnTo();
		drive.stopMotors();
	}

}
