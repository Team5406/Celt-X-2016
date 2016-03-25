package ca.team5406.util;

import ca.team5406.frc2016.Constants;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;

public class Util {

	public static CANTalon createDriveTalon(int id){
		CANTalon talon = new CANTalon(id);
		talon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
	    talon.configNominalOutputVoltage(+0.0f, -0.0f);
	    talon.configPeakOutputVoltage(+12.0f, -12.0f);
	    talon.setAllowableClosedLoopErr(0);
	    return talon;
	}
	
	public static CANTalon createArmTalon(int id){
		CANTalon talon = new CANTalon(id);
		talon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
	    talon.configNominalOutputVoltage(+0.0f, -0.0f);
	    talon.configPeakOutputVoltage(+12.0f, -12.0f);
		talon.changeControlMode(TalonControlMode.Position);
		talon.setPosition(0);
		talon.setPID(Constants.armPos_kP, Constants.armPos_kI, Constants.armPos_kD);
		return talon;
	}
	
	public static double applyDeadband(double value, double deadband){
		return (Math.abs(value) < Math.abs(deadband) ? 0 : value);
	}
	
	public static double applyMin(double value, double min){
		if(value > -min && value < 0){
			return -min;
		}
		else if(value < min && value >0){
			return min;
		}
		else{
			return value;
		}
	}
	
	public static double limitValue(double value, double limit){
		if(value > limit){
			return limit;
		}
		else if(value < -limit){
			return -limit;
		}
		else{
			return value;
		}
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
