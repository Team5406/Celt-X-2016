package ca.team5406.util;

import edu.wpi.first.wpilibj.Timer;

public class Util {

	public static double applyDeadband(double value, double deadband){
		return (Math.abs(value) < Math.abs(deadband) ? 0 : value);
	}
	
	public static double squareWave(Timer timer, double period, double min, double max){
		if(timer.get() <= period/2){
			return min;
		}
		else if(timer.get() <= period){
			return max;
		}
		else{
			timer.reset();
			return min;
		}
	}
	
}
