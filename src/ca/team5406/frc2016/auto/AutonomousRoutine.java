package ca.team5406.frc2016.auto;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public abstract class AutonomousRoutine {

	private String name = "Default";
	private boolean running;
	private double randomMonitor;
	
	public AutonomousRoutine(String name){
		this.name = name;
		running = false;
	}
	
	public boolean isRunning(){
		return running;
	}
	
	public String getName(){
		return name;
	}
	
	public void start(){
		running = true;
		init();
	}
	
	public void stop(){
		running = false;
		end();
	}
	
	public void run(){
		randomMonitor = Math.random();
		execute();
	}
	
	public void sendSmartDashInfo(){
		SmartDashboard.putNumber("Auton Random", randomMonitor);
	}

	public abstract void resetTimer();
	public abstract void init();
	public abstract void execute();
	public abstract void end();
	
}
