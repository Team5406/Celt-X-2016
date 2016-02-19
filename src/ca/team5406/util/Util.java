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
	
	public static double sineWave(Timer timer, double period, double min, double max){
		double amp = max - min;
		double value = amp * Math.sin(timer.get() * period / Math.PI) + (min - amp/2);
		return value;
	}
	
}
