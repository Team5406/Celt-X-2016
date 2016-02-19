package ca.team5406.util.sensors;

import java.util.TimerTask;

import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RioAccelerometer{

	private BuiltInAccelerometer rioAcceler;

	private double xVelAccum;
	private double xDistAccum;
	private double yVelAccum;
	private double yDistAccum;
	private double zVelAccum;
	private double zDistAccum;
	
	private boolean useMeters;
	private boolean running;
	
	private java.util.Timer timer;
	private Timer wpiTimer;


	public RioAccelerometer(){
		this(false);
	}
	
	public RioAccelerometer(boolean useMeters){
		useMeters = this.useMeters;
		running = false;
		rioAcceler = new BuiltInAccelerometer();
		xVelAccum = 0;
		xDistAccum = 0;
		yVelAccum = 0;
		yDistAccum = 0;
		zVelAccum = 0;
		zDistAccum = 0;
		wpiTimer = new Timer();
		wpiTimer.start();
		
	    timer = new java.util.Timer("RioAccelTimer");//create a new Timer
	    start();
	    
	}

	private TimerTask update = new TimerTask() {
        @Override
        public void run() {
            xVelAccum += getAccelX() * wpiTimer.get();
            xDistAccum += xVelAccum * wpiTimer.get();
            yVelAccum += getAccelY() * wpiTimer.get();
            yDistAccum += yVelAccum * wpiTimer.get();
            zVelAccum += getAccelZ() * wpiTimer.get();
            zDistAccum += zVelAccum * wpiTimer.get();
            SmartDashboard.putNumber("Timer", wpiTimer.get());
            wpiTimer.reset();
        }
    };
    
    private void start(){
    	running = true;
	    timer.scheduleAtFixedRate(update, 10, 10);//this line starts the timer 
    }
    
    public void stop(){
    	running = false;
    	timer.cancel();
    }
    
    public boolean isRunning(){
    	return running;
    }
    
    public double getDistX(){
    	return xDistAccum;
    }
    public double getVelX(){
    	return xVelAccum;
    }
    public double getAccelX(){
    	return rioAcceler.getX() * (useMeters ? 9.81 : 32.17);
    }
    
    public double getDistY(){
    	return yDistAccum;
    }
    public double getVelY(){
    	return yVelAccum;
    }
    public double getAccelY(){
    	return rioAcceler.getY() * (useMeters ? 9.81 : 32.17);
    }
    
    public double getDistZ(){
    	return zDistAccum;
    }
    public double getVelZ(){
    	return zVelAccum;
    }
    public double getAccelZ(){
    	return rioAcceler.getZ() * (useMeters ? 9.81 : 32.17);
    }
	
}
