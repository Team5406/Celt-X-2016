package ca.team5406.util;

public class Timer extends edu.wpi.first.wpilibj.Timer{

	private boolean isRunning;
	private double lastTime;
	
	public Timer(){
		super();
		isRunning = false;
		lastTime = 0.0;
	}
	
	@Override
	public void start(){
		isRunning = true;
		super.start();
	}
	
	@Override
	public void reset(){
		lastTime = 0.0;
		super.reset();
	}
	
	@Override
	public double get(){
		double time = super.get();
		lastTime = time;
		return time;
	}
	
	public double getTimeStep(){
		return super.get() - lastTime;
	}
	
	@Override
	public void stop(){
		isRunning = false;
		super.stop();
	}
	
	public boolean isRunning(){
		return isRunning;
	}
	
}
