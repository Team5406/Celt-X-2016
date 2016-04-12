package ca.team5406.frc2016.subsystems;

import ca.team5406.frc2016.Constants;
import ca.team5406.util.Util;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Arm extends Subsystem{
	
	private CANTalon motorA;
	private CANTalon motorB;
	private AnalogInput pot;

	private Positions desiredPosition;
	private Positions currentPosition;
	
	public static final String NAME = "Arm";
	
	public static enum Positions{
		NONE,
		UP,
		DOWN,
		CARRY,
		INSIDE,
		MOVING,
		MANUAL;

	    private int id;
	    public int getValue() { return id; }
		public void set(int i) {
			this.id = i;
		}
	}
	
	public Arm(boolean isPracticeBot){
		super(NAME);
		motorA = Util.createArmTalon(Constants.armMotorA);
		motorB = Util.createArmTalon(Constants.armMotorB);
		pot = new AnalogInput(Constants.armPot);
		
		motorB.changeControlMode(TalonControlMode.Follower);
		motorB.set(motorB.getDeviceID());
		motorA.reverseOutput(false);
		motorB.reverseOutput(true);
		motorB.reverseSensor(true);
		Positions.NONE.set(Constants.nullPositionValue);
		Positions.MOVING.set(Constants.nullPositionValue);
		Positions.MANUAL.set(Constants.nullPositionValue);
		Positions.UP.set(Constants.armUpPos);
		Positions.DOWN.set(Constants.armDownPos);
		Positions.CARRY.set(Constants.armCarryPos);
		Positions.INSIDE.set(Constants.armInsidePos);

		desiredPosition = Positions.NONE;
		currentPosition = Positions.NONE;
	}
	
	public void setDesiredPos(Positions pos){
		if(pos == Positions.MANUAL){
			motorA.changeControlMode(TalonControlMode.PercentVbus);
			motorB.changeControlMode(TalonControlMode.PercentVbus);
			
			currentPosition = Positions.MANUAL;
			desiredPosition = Positions.MANUAL;
		}
		else if(currentPosition != pos){
			motorA.changeControlMode(TalonControlMode.Position);
			motorB.changeControlMode(TalonControlMode.Follower);
			motorB.set(motorA.getDeviceID());
			
			currentPosition = Positions.MOVING;
			desiredPosition = pos;
		}
	}
	
	public Positions getDesiredPos(){
		return desiredPosition;
	}
	
	public Positions getCurrentPos(){
		return currentPosition;
	}
	
	public void joystickControl(double value){
		if(desiredPosition == Positions.MANUAL){
			value = Util.applyDeadband(value, Constants.xboxControllerDeadband);
			setSpeed(value);
		}
	}
	
    @Override
    public void runControlLoop() {
//    	talonSafegaurd.test();
    	if(currentPosition != Positions.MANUAL){
	    	double pos = desiredPosition.getValue();
	    	if(pos == Constants.nullPositionValue) pos = getEncoder();
			motorA.set(pos);
			if(Math.abs(motorA.getEncPosition() - desiredPosition.getValue()) < Constants.armPos_deadband){
				currentPosition = desiredPosition;
			}
    	}
	}
	
	public void stopMotors(){
		setSpeed(0);
	}
	
	private void setSpeed(double speed){
		motorA.set(speed);
		if(currentPosition == Positions.MANUAL){
			motorB.set(-speed);
		}
	}
	
	public void resetEncoders(){
		motorA.setEncPosition(0);
		motorB.setEncPosition(0);
	}
	
	public int getEncoder(){
		return motorA.getEncPosition();
	}
	
	public double getPot(){
		return pot.getValue();
	}
	
	@Override
	public void sendSmartdashInfo(){
		SmartDashboard.putNumber("Arm EncA", motorA.getEncPosition());
		SmartDashboard.putNumber("Arm EncB", motorB.getEncPosition());
		SmartDashboard.putString("Arm Position", getCurrentPos().name());
		SmartDashboard.putBoolean("Arm On Target", (motorA.getError() < Constants.armPos_deadband));
	}

}
