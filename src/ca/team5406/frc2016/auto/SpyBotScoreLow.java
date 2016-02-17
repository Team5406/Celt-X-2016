package ca.team5406.frc2016.auto;

import ca.team5406.frc2016.subsystems.Drivetrain;

public class SpyBotScoreLow extends AutonomousRoutine{

	private Drivetrain drive;
	private int autonStep = 0;
	
	public SpyBotScoreLow(Drivetrain drivetrain){
		drive = drivetrain;
	}
	
	public void init(){
		autonStep = 0;
		drive.initPositionControl();
	}
	
	public void run(){
		switch(autonStep){
		case 0:
			if(drive.driveToDistance(64)){
				autonStep++;
			}
			break;
		}
	}
}
