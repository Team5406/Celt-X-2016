package ca.team5406.frc2016.auto;

import java.util.TimerTask;

import edu.wpi.first.wpilibj.DriverStation;


public abstract class AutonomousRoutine {

	
	private java.util.Timer timer;
	private String name;
	
	public AutonomousRoutine(String name){
		name = this.name;
	}
	
	private TimerTask run = new TimerTask() {
        @Override
        public void run() {
        	if(!DriverStation.getInstance().isAutonomous() || DriverStation.getInstance().isDisabled()){
        		timer.cancel();
        	}
        	execute();
        }
    };
	
	public void start(){
		init();
		timer = new java.util.Timer(name + " Scheduler");
	    timer.scheduleAtFixedRate(run, 10, 10);
	}
	
	public void kill(){
		timer.cancel();
		end();
	}

	public abstract void init();
	public abstract void execute();
	public abstract void end();
	
}
