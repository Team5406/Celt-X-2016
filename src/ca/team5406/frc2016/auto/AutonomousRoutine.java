package ca.team5406.frc2016.auto;

import ca.team5406.util.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public abstract class AutonomousRoutine {

	private String name = "Default";
	private boolean running;
	private double randomMonitor;
	private Timer stepTimer;
	
	public AutonomousRoutine(String name){
		this.name = name;
		running = false;
		stepTimer = new Timer();
	}
	
	public boolean isRunning(){
		return running;
	}
	
	public String getName(){
		return name;
	}
	
	public void start(){
		running = true;
		stepTimer.start();
		stepTimer.reset();
		init();
	}
	
	public void stop(){
		running = false;
		stepTimer.stop();
		end();
	}
	
	public void run(){
		randomMonitor = Math.random();
		execute();
	}
	
	public void sendSmartDashInfo(){
		SmartDashboard.putNumber("Auton Random", randomMonitor);
	}
	
	public void resetStepTimer(){
		stepTimer.reset();
	}
	
	public double getStepTimer(){
		return stepTimer.get();
	}

	public abstract void resetTimer();
	public abstract void init();
	public abstract void execute();
	public abstract void end();
	
}
