package ca.team5406.frc2016.auto;

import ca.team5406.frc2016.subsystems.*;
import ca.team5406.util.DataLogger;
import ca.team5406.util.Util;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class GatherLinearizeData extends AutonomousRoutine{
	
	private Drive drive;
	private Timer timer;
	private DataLogger leftLogger;
	private DataLogger rightLogger;
	
	private int autonStep;

	public GatherLinearizeData(Drive drive) {
		super("Gather Data for Linearizing Function");
		this.drive = drive;
		timer = new Timer();
		leftLogger = new DataLogger("leftDrivePower");
		rightLogger = new DataLogger("rightDrivePower");
	}

	@Override
	public void init() {
		autonStep = 0;
		drive.shiftUp();
		drive.resetDriveTo();
		drive.resetEncoders();
		timer.start();
		leftLogger.addLine(new Double(DriverStation.getInstance().getBatteryVoltage()).toString());
		rightLogger.addLine(new Double(DriverStation.getInstance().getBatteryVoltage()).toString());
	}
	
	@Override
	public void resetTimer(){
		timer.reset();
	}

	private double prevPower = 0.0;
	@Override
	public void execute() {
		SmartDashboard.putNumber("Auton Step", autonStep);
		
		if(autonStep >= 6000){
			end();
			return;
		}
		
		double prevVelLeft = drive.getLeftVel();
		double prevVelRight = drive.getRightVel();
		double power = Util.limitValue(autonStep * 1.0/5000, 1.0);

		leftLogger.addLine(new Double(prevVelLeft).toString() + "," + new Double(prevPower).toString());
		rightLogger.addLine(new Double(prevVelRight).toString() + "," + new Double(prevPower).toString());
		
		prevPower = power;
		drive.set(power, power);
		SmartDashboard.putNumber("SetPoint", power);
		autonStep++;
	}
	
	public void nextStep(){
		autonStep++;
		super.resetStepTimer();
	}
	
	@Override
	public void end() {
		timer.stop();
		drive.stopMotors();
		drive.resetDriveTo();
		leftLogger.addLine(new Double(DriverStation.getInstance().getBatteryVoltage()).toString());
		rightLogger.addLine(new Double(DriverStation.getInstance().getBatteryVoltage()).toString());
		leftLogger.dump();
		rightLogger.dump();
	}
	
	
	
}
