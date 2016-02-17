package ca.team5406.frc2016.subsystems;

import ca.team5406.frc2016.Constants;
import ca.team5406.util.TimedPrinter;
import ca.team5406.util.Util;
import ca.team5406.util.sensors.xEncoder;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class BatteringRamp {
	
	private VictorSP motor;
	private xEncoder encoder;
	private TimedPrinter printer;
	
	private enum Positions{
		DOWN,
		MID,
		UP,
		STOWED
	}
	
	public BatteringRamp(){
		motor = new VictorSP(Constants.batteringRampMotor);
		encoder = new xEncoder(Constants.batteringRampEncA, Constants.batteringRampEncB);
		printer = new TimedPrinter(0.5);
	}
	
	public int getEncoder(){
		return encoder.get();
	}
	
	public void joystickControl(double value){
		value = Util.applyDeadband(value, Constants.xboxControllerDeadband);
//		if(encoder.get() > Constants.rampStowedPosititon && encoder.get() < Constants.rampDownPosition){
		set(value);
//		}
	}
	
	private void set(double value){
		motor.set(value);
	}
	
	public void sendSmartdashInfo(){
		SmartDashboard.putNumber("Ramp Encoder", encoder.get());
		SmartDashboard.putNumber("Ramp Speed", encoder.getRate());
	}
	
	public void printData(){
		StringBuilder str = new StringBuilder();
		str.append("Ramp:\tEnc: ");
		str.append(getEncoder());
		printer.print(str.toString());
	}
	
}
