package ca.team5406.util;

public class Util {

	public static double applyDeadband(double value, double deadband){
		return (Math.abs(value) < Math.abs(deadband) ? 0 : value);
	}
	
}
