package ca.team5406.frc2016.auto.actions;

import ca.team5406.util.Timer;

public abstract class Action {
	
	private double randomMonitor;
	private Timer timeoutTimer;
	private ActionState currentState;
	private double maxTime;
	private boolean contAfterTimeout;
	
	public static enum ActionState{
		PRE,
		RUNNING,
		FINISHED,
		TIMED_OUT
	}
	
	public Action(double maxTime, boolean contAfterTimeout){
		this.maxTime = maxTime;
		this.contAfterTimeout = contAfterTimeout;
		currentState = ActionState.PRE;
		timeoutTimer = new Timer();
		
	}

	public void start(){
		init();
		currentState = ActionState.RUNNING;
		timeoutTimer.start();
	}
	
	public void run(){
		if(timeoutTimer.get() >= maxTime){
			timeout();
		}
		if(currentState == ActionState.RUNNING){
			randomMonitor = Math.random();
			iter();
		}
	}
	
	public void timeout(){
		currentState = ActionState.TIMED_OUT;
		stop();
	}
	
	private void stop(){
		end();
		timeoutTimer.stop();
	}
	
	public void finish(){
		currentState = ActionState.FINISHED;
		stop();
	}
	
	public boolean isRunning(){
		return currentState == ActionState.RUNNING;
	}
	
	public ActionState getState(){
		return currentState;
	}
	
	public boolean shouldContAfterTimeout(){
		return contAfterTimeout;
	}
	
	public double getRandomMonitor(){
		return randomMonitor;
	}
	
	public abstract void init();
	public abstract void iter();
	public abstract void end();
	
}
