package ca.team5406.frc2016.auto.actions;

import ca.team5406.frc2016.Constants;
import ca.team5406.frc2016.subsystems.Drive;
import ca.team5406.frc2016.subsystems.Drive.Gear;
import ca.team5406.frc2016.subsystems.Subsystem;
import ca.team5406.util.Timer;
import usfirst.frc.team2168.FalconPathPlanner;

public class FollowPath extends Action{
	
	private Drive drive;
	private FalconPathPlanner path;
	private double[][] leftSpeedProfile;
	private double[][] rightSpeedProfile;
	private Timer timeStepMonitor;

	public FollowPath(double[][] path){
		super(2.0, false);
		this.path = new FalconPathPlanner(path);
		drive = (Drive) Subsystem.getSubsystem(Drive.NAME);
		timeStepMonitor = new Timer();
	}
	
	@Override
	public void init() {
		path.calculate(8.0, 0.2, Constants.trackWidth);
		drive.selectGear(Gear.HIGH);
		leftSpeedProfile = path.smoothLeftVelocity;
		rightSpeedProfile = path.smoothRightVelocity;
		timeStepMonitor.start();
	}

	@Override
	public void iter() {
		int currentStep = (int) Math.floor(timeStepMonitor.get() / 0.2);
		double leftSpeed = leftSpeedProfile[currentStep][1];
		double rightSpeed = rightSpeedProfile[currentStep][1];
		drive.setLeftRightSpeed(leftSpeed, rightSpeed);
		if(currentStep == leftSpeedProfile.length){
			finish();
		}
	}

	@Override
	public void end() {
		drive.stopMotors();
	}

}
